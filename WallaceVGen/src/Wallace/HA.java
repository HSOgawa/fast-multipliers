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

public class HA {

    private Signal a;
    private Signal b;
    private Signal S;
    private Signal Cout;

    public HA (Signal a, Signal b, Signal S, Signal Cout){
        this.a = a;
        this.b = b;
        this.S = S;
        this.Cout = Cout;
    }

    public String toString(){
        return "(" + a.toString() + ", " + b.toString() + ", " + S.toString() + ", " + Cout.toString() + ")";
    }

}