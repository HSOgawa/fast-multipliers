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

package Wallace;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Main {

    // list of collected full-adders and half-adders
    private static ArrayList<Signal> Outputs = new ArrayList<>();
    private static ArrayList<Object> Adders = new ArrayList<>();

    // main
    public static void main(String args[]) {
        String N_str = args[0];
        wallaceReduction(N_str);
        genVerilog(Integer.parseInt(N_str));
    }

    /**
     * Generate partial product matrix
     *
     * @param N_bits operand size
     */
    private static LinkedHashMap<Integer, ArrayList<Signal>> setColumns(int N_bits) {
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
        for (int i = N_bits; i < 2 * N_bits - 1; i++) {
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
        columns.put(2 * N_bits - 1, column);
        return columns;
    }

    /**
     * Return the length of the longest column in partial product matrix
     *
     * @param PP_matrix partial product matrix
     */
    private static int getLengthofLongestColumn(LinkedHashMap<Integer, ArrayList<Signal>> PP_matrix) {
        int max_length = 0;
        // for each column in partial product matrix
        for (int c = 0; c < PP_matrix.size(); c++) {
            if (PP_matrix.get(c).size() > max_length) {
                max_length = PP_matrix.get(c).size();
            }
        }
        return max_length;
    }

    /**
     * Get the index of the longest column in partial product matrix
     *
     * @param PP_matrix partial product matrix
     */
    private static int getIndexofLongestCol(LinkedHashMap<Integer, ArrayList<Signal>> PP_matrix) {
        int col_length = 0;  // column length
        int index = 0;  // index of the longest column
        // for each column in partial product matrix
        for (int c = 0; c < PP_matrix.size(); c++) {
            if (PP_matrix.get(c).size() > col_length) {
                col_length = PP_matrix.get(c).size();
                index = c;
            }
        }
        return index;
    }

	/**
	 * Get the index of the last reduction column
	 *
	 * @param PP_matrix partial product matrix
	 */
    private static int getIndexofLastRedCol(LinkedHashMap<Integer, ArrayList<Signal>> PP_matrix) {
        for (int c = getIndexofLongestCol(PP_matrix); c < PP_matrix.size(); c++) {
            int col_length_in_window = PP_matrix.get(c).size() - (getLengthofLongestColumn(PP_matrix) % 3);
            if (col_length_in_window < 2) {
                return c - 1;
            }
        }
        return PP_matrix.size() - 1;
    }

	/**
	 * Print actual configuration of PP_matrix
	 *
	 * @param PP_matrix partial product matrix
	 */
    private static void printPPMatrix(LinkedHashMap<Integer, ArrayList<Signal>> PP_matrix) {
        for (int j = 1; j <=getLengthofLongestColumn(PP_matrix) ; j++) {
            //System.out.printf("           ");
            for (int c = PP_matrix.size() - 1; c >= 0; c--) {
                if (PP_matrix.get(c).size() >= j) {
                    System.out.printf(PP_matrix.get(c).get(j - 1).toString());
                    for (int n = PP_matrix.get(c).get(j - 1).toString().length(); n < 10; n++) {
                        System.out.printf(" ");
                    }
                    System.out.printf(" ");
                }
                else {
                    System.out.printf("           ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Apply Wallace reduction process
     *
     * @param N_str number of bits
     */
    private static void wallaceReduction(String N_str) {
        int NA = 0;  // NA = Not Applicable
        int N_bits = Integer.parseInt(N_str);  // number of bits
        LinkedHashMap<Integer, ArrayList<Signal>> PP_matrix = setColumns(N_bits);  // all partial product columns
        int weight_nxt = getLengthofLongestColumn(PP_matrix);   // actual weight of PP_matrix, maximum length of a column
        // reduction steps
        while (weight_nxt > 2) {
            int index_last_red_col = getIndexofLastRedCol(PP_matrix);
            // comment debug block when benchmarking
            /*System.out.printf("Weight          = %d\n", getLengthofLongestColumn(PP_matrix));
            System.out.printf("Reduction index = %d\n\n", getIndexofLastRedCol(PP_matrix));
            printPPMatrix(PP_matrix);
            for (int n = 0; n <= PP_matrix.size(); n++) {
                System.out.printf("-----------");
            }
            System.out.println();*/
            // collection of Cout signals to be included in next adjacent column
            ArrayList<Signal> CarryList_prev = new ArrayList<>();
            //
            int pp_outside_window = (getLengthofLongestColumn(PP_matrix) % 3);
            // for each column in partial product tree till reach end of row of three
            for (int c = 0; c <= index_last_red_col; c++) {
                // collection of sum signals
                ArrayList<Signal> SumList = new ArrayList<>();
                ArrayList<Signal> CarryList_act = new ArrayList<>();
                // apply FA reduction (col_length / 3) times
                int size_before = PP_matrix.get(c).size();
                if (c >= getIndexofLongestCol(PP_matrix)) {
                    size_before = PP_matrix.get(c).size() - pp_outside_window;
                }
                for (int i = 0; i < (size_before / 3); i++) {
                    Signal S = new Signal("S", Adders.size(), NA);
                    Signal Cout = new Signal("Cout", Adders.size(), NA);
                    FA Full_Adder = new FA(PP_matrix.get(c).get(0), PP_matrix.get(c).get(1), PP_matrix.get(c).get(2), S, Cout);
                    Adders.add(Full_Adder);  // insert created adder to global adder list
                    CarryList_act.add(Cout);  // insert created carry out signal for next column
                    // update partial product matrix: remove grouped partial products and add new sum
                    PP_matrix.get(c).remove(0);
                    PP_matrix.get(c).remove(0);
                    PP_matrix.get(c).remove(0);
                    SumList.add(S);
                }
                // apply HA reduction once, if applicable
                if ((size_before % 3 == 2)) {
                    Signal S = new Signal("S", Adders.size(), NA);
                    Signal Cout = new Signal("Cout", Adders.size(), NA);
                    HA Half_Adder = new HA(PP_matrix.get(c).get(0), PP_matrix.get(c).get(1), S, Cout);
                    Adders.add(Half_Adder);  // insert created adder to global adder list
                    CarryList_act.add(Cout);  // insert created carry out signal for next column
                    // update partial product matrix: remove grouped partial products and add new sum
                    PP_matrix.get(c).remove(0);
                    PP_matrix.get(c).remove(0);
                    SumList.add(S);
                }
                // update partial product matrix with carry signals from previous column
                if (CarryList_prev.size() > 0) {
                    for (Signal Cout : CarryList_prev) {
                        PP_matrix.get(c).add(Cout);
                    }
                }
                // add all collected sum signals to the present column
                if (SumList.size() > 0) {
                    for (Signal S : SumList) {
                        PP_matrix.get(c).add(S);
                    }
                }
                // update carry list with new signals
                CarryList_prev.clear();
                CarryList_prev = CarryList_act;
                // update weight value
                weight_nxt = getLengthofLongestColumn(PP_matrix);
            }
            // update partial product matrix with carry signals from previous column
            if ((CarryList_prev.size() > 0) && (index_last_red_col < PP_matrix.size() - 1)) {
                for (Signal Cout : CarryList_prev) {
                    PP_matrix.get(index_last_red_col + 1).add(Cout);
                }
            }
        }
        // comment debug block when benchmarking
        /*System.out.printf("Weight = %d\n\n", getLengthofLongestColumn(PP_matrix));
        printPPMatrix(PP_matrix);
        for (int n = 0; n <= PP_matrix.size(); n++) {
            System.out.printf("-----------");
        }
        System.out.println();*/
        // last stage: Carry-Propagate Adder
        for (int c = 0; c < PP_matrix.size(); c++) {
            // half-adder reduction
            if (PP_matrix.get(c).size() == 2) {
                Signal S = new Signal("S", Adders.size(), NA);
                Signal Cout = new Signal("Cout", Adders.size(), NA);
                HA Half_Adder = new HA(PP_matrix.get(c).get(0), PP_matrix.get(c).get(1), S, Cout);
                Adders.add(Half_Adder);  // insert created adder to global adder list
                // immediate carry propagation to the next column
                if (c < PP_matrix.size() - 1) {
                    PP_matrix.get(c + 1).add(Cout);
                }
                // update partial product matrix: remove grouped partial products and add new sum
                PP_matrix.get(c).remove(0);
                PP_matrix.get(c).remove(0);
                PP_matrix.get(c).add(S);
            }
            // full-adder reduction
            else if (PP_matrix.get(c).size() == 3) {
                Signal S = new Signal("S", Adders.size(), NA);
                Signal Cout = new Signal("Cout", Adders.size(), NA);
                FA Full_Adder = new FA(PP_matrix.get(c).get(0), PP_matrix.get(c).get(1), PP_matrix.get(c).get(2), S, Cout);
                Adders.add(Full_Adder);  // insert created adder to global adder list
                // immediate carry propagation to the next column
                if (c < PP_matrix.size() - 1) {
                    PP_matrix.get(c + 1).add(Cout);
                }
                // update partial product matrix: remove grouped partial products and add new sum
                PP_matrix.get(c).remove(0);
                PP_matrix.get(c).remove(0);
                PP_matrix.get(c).remove(0);
                PP_matrix.get(c).add(S);
            }
        }
        // list of output signals
        for (int c = 0; c < PP_matrix.size(); c++) {
            Outputs.add(c, PP_matrix.get(c).get(0));
        }
        // comment debug block when benchmarking
        /*System.out.println("Carry-propagating adder:\n");
        for (int i = Outputs.size() - 1; i >= 0; i--) {
            System.out.printf(Outputs.get(i).toString());
            for (int n = Outputs.get(i).toString().length(); n < 11; n++) {
                System.out.printf(" ");
            }
        }
        System.out.println();
        for (int n = 0; n <= PP_matrix.size(); n++) {
            System.out.printf("-----------");
        }
        System.out.println();*/
    }

    /**
     * Generate Verilog code
     *
     * @param N_bits operand size
     */
    private static void genVerilog(int N_bits) {
        String filename = "Mult_Wallace" + N_bits;
        try {
            File file = new File("verilog/" + filename + ".v");
            file.getParentFile().mkdirs();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("`timescale 1ns / 1ps");
            writer.write("\n\nmodule Mult_Wallace" + N_bits + " # (");
            writer.write("\n\tparameter N = " + N_bits);
            writer.write("\n)(");
            writer.write("\n\tinput [N-1:0] x,");
            writer.write("\n\tinput [N-1:0] y,");
            writer.write("\n\toutput [2*N-1:0] z");
            writer.write("\n);");
            writer.write("\n\nwire [N-1:0] P[N-1:0];\n");
            for (int i = 0; i < N_bits; i++) {
                for (int j = 0; j < N_bits; j++) {
                    writer.write("\nassign P[" + i + "][" + j + "] = x[" + i + "] & y[" + j + "];");
                }
            }
            writer.write("\n\nwire [" + (Adders.size() - 1) + ":0] S;");
            writer.write("\nwire [" + (Adders.size() - 1) + ":0] Cout;\n");
            int cnt = 1;
            for (Object adder : Adders) {
                if (adder.getClass().toString().equals("class Wallace.HA")) {
                    writer.write("\nHalf_Adder " + "HA" + cnt + " " + adder.toString() + ";");
                    cnt++;
                } else if (adder.getClass().toString().equals("class Wallace.FA")) {
                    writer.write("\nFull_Adder " + "FA" + cnt + " " + adder.toString() + ";");
                    cnt++;
                }
            }
            // output assignments
            writer.write("\n");
            for (int n = Outputs.size() - 1; n >= 0; n--) {
                writer.write("\nassign z[" + n + "] = " + Outputs.get(n).toString() + ";");
            }
            writer.write("\n\nendmodule");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
