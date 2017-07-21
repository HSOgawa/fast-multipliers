// Copyright (C) 2017 Henrique Ogawa, Lucas Gaia
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package Dadda;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Main {

    private static ArrayList<Object> Adders = new ArrayList<>();
    private static final double ReductionRatio = 1.5;
    // Dadda heights: 2, 3, 4, 6, 9, 13, 19, 28, 42, 63
    private static int n_adder = 1; //total number of adders
    private static final int NA = 0;
    private static LinkedHashMap<Integer, ArrayList<Signal>> PP_tree;


    public static void main(String[] args){
        String N_str = args[0];
        setDaddaAdders(N_str);
        genVerilog(Integer.parseInt(N_str));
    }

    // @brief - set all partial product columns (tree configuration)
    private static LinkedHashMap<Integer, ArrayList<Signal>> setColumns(int N_bits){
        LinkedHashMap<Integer, ArrayList<Signal>> columns = new LinkedHashMap<>();
        // i represents the counter for each column
        for (int i = 0; i < N_bits; i++) {
            ArrayList<Signal> column = new ArrayList<>();
            // j represents the counter in each column
            for (int j = 0; j <= i; j++) {
                Signal P = new Signal("P", i - j, j);
                column.add(j, P);
            }
            columns.put(i, column);
        }
        for (int i = N_bits; i < 2*N_bits-1; i++) {
            ArrayList<Signal> column = new ArrayList<>();
            // j represents the counter in each column
            int cnt = 0;
            for (int j = i - N_bits + 1; j < N_bits; j++) {
                Signal P = new Signal("P", i - j, j);
                column.add(cnt, P);
                cnt++;
            }
            columns.put(i, column);
        }
        ArrayList<Signal> column = new ArrayList<>();
        columns.put(2*N_bits-1, column);
        return columns;
    }

    private static int getTreeSize(){
    	int MaxTreeSize = PP_tree.get(0).size();
    	for(int i = 1; i < PP_tree.size();  i ++){
    		if (MaxTreeSize < PP_tree.get(i).size()) MaxTreeSize = PP_tree.get(i).size();
    	}
    	return MaxTreeSize;

    }

    private static void addHalfAdder(int col){
    	Signal S = new Signal("S", n_adder, NA);
        Signal Cout = new Signal("Cout", n_adder, NA);
        HA half_adder = new HA(PP_tree.get(col).get(0), PP_tree.get(col).get(1), S, Cout);
		n_adder++;
		PP_tree.get(col).remove(0);
		PP_tree.get(col).remove(0);
		PP_tree.get(col).add(S);
		PP_tree.get(col + 1).add(Cout);
		// @lgaia - debug
		//System.out.println("HA " + half_adder.toString() + ";");
        Adders.add(half_adder);
    }

    private static void addFullAdder(int col){
    	Signal S = new Signal("S", n_adder, NA);
        Signal Cout = new Signal("Cout", n_adder, NA);
        FA full_adder = new FA(PP_tree.get(col).get(0), PP_tree.get(col).get(1), PP_tree.get(col).get(2), S, Cout);
		n_adder++;
		PP_tree.get(col).remove(0);
		PP_tree.get(col).remove(0);
		PP_tree.get(col).remove(0);
		PP_tree.get(col).add(S);
		PP_tree.get(col + 1).add(Cout);
		// @lgaia - debug
		//System.out.println("FA " + full_adder.toString() + ";");
        Adders.add(full_adder);
    }

    private static void daddaReduction(int minReduction){
    	//System.out.println("Altura desejada: " + minReduction);
    	ArrayList<Signal> column = new ArrayList<>();  // pick a specific partial product column
        int col;  // index a specific column in PP_tree

		for (col = 2; col < PP_tree.size(); col ++){
			column = PP_tree.get(col);
			while(column.size() > minReduction){
				if(column.size() > minReduction + 1) addFullAdder(col);
				else addHalfAdder(col);
			}
		}
    }

    private static void setDaddaAdders(String N_str){
        int N_bits = Integer.parseInt(N_str);  // number of bits
        PP_tree = setColumns(N_bits);  // all partial product columns
        int MaxSize = getTreeSize(); // Maximum column size
        int Height = 2;
        while(ReductionRatio * Height < MaxSize) Height = (int) Math.floor(Height * ReductionRatio);
        while(MaxSize > 2) {
        	daddaReduction(Height);
        	// update MaxSize and Dadda Height
        	MaxSize = getTreeSize();
        	Height = (int) Math.ceil(Height / ReductionRatio);
        }
        // column 0 only has one partial product
        // column 1 will be left with S[0], the output of a half adder
        Signal S = new Signal("S", NA, NA);
        Signal Cout = new Signal("Cout", NA, NA);
    		HA half_adder = new HA(PP_tree.get(1).get(0), PP_tree.get(1).get(1), S, Cout);
    		PP_tree.get(1).remove(0);
    		PP_tree.get(1).remove(0);
    		PP_tree.get(1).add(S);
    		PP_tree.get(2).add(Cout);
    		// @lgaia - debug
    		//System.out.println("HA " + half_adder.toString() + ";");
        Adders.add(half_adder);

        for (int col = 2; col < PP_tree.size(); col ++){
        	if(PP_tree.get(col).size() == 2){
        		addHalfAdder(col);
        	}else if(PP_tree.get(col).size() == 3){
        		addFullAdder(col);
        	}
        }
    }

    // write Verilog code line by line
    private static void genVerilog(int N_bits){
        String filename = "Mult_Dadda" + N_bits;
        try {
            File file = new File("verilog/" + filename + ".v");
            file.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write("`timescale 1ns / 1ps");

            writer.write("\n\nmodule Mult_Dadda" + N_bits + " # (");
            writer.write("\n\tparameter N = " + N_bits);
            writer.write("\n)(");
            writer.write("\n\tinput [N-1:0] x,");
            writer.write("\n\tinput [N-1:0] y,");
            writer.write("\n\toutput [2*N-1:0] z");
            writer.write("\n);");

            writer.write("\n\nwire [N-1:0] P[N-1:0];\n\n");
            for (int i = 0; i < N_bits; i++) {
                for (int j = 0; j < N_bits; j++) {
                    writer.write("\nassign P[" + i + "][" + j + "] = x[" + i + "] & y[" + j + "];");
                }
            }

            writer.write("\n\nwire [" + n_adder + ":0] S;");
            writer.write("\nwire [" +  n_adder + ":0] Cout;");

            int HAcnt = 1;
            int FAcnt = 1;
            for (Object adder: Adders) {
                if (adder.getClass().toString().equals("class Dadda.HA")){
                    writer.write("\nHalf_Adder " + "HA" + HAcnt + " " + adder.toString() + ";");
                    HAcnt++;
                }
                else if (adder.getClass().toString().equals("class Dadda.FA")) {
                    writer.write("\nFull_Adder " + "FA" + FAcnt + " " + adder.toString() + ";");
                    FAcnt++;
                }
            }

            // output assignments
            writer.write("\n\nassign z[" + (2*N_bits-1) + "] = Cout[" + (n_adder-1) + "];");
            int t = 1;
            for (int i = (2*N_bits-2); i > 1; i--) {
                writer.write("\nassign z[" + i + "] = S[" + (n_adder-t) + "];");
                t++;
            }
            writer.write("\nassign z[1] = S[0];");
            writer.write("\nassign z[0] = P[0][0];");

            writer.write("\n\nendmodule");

            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
