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

public class Signal {

    private String type;
    private int x_i;
    private int y_i;

    public Signal(String type, int x_i, int y_i){
        this.type = type;
        this.x_i = x_i;
        this.y_i = y_i;
    }

    public String toString(){
        String Str = "";
        if (type.equals("P")) {
            Str = "P" + "[" + y_i + "]" + "[" + x_i + "]";
        }
        if (type.equals("S")) {
            Str = "S" + "[" + x_i + "]";
        }
        if (type.equals("Cout")) {
            Str = "Cout" + "[" + x_i + "]";
        }
        if (type.equals("Cin")) {
            Str = "Cin" + "[" + x_i + "]";
        }
        return Str;
    }

}