`timescale 1ns / 1ps

module Mult_Dadda8 # (
	parameter N = 8
)(
	input [N-1:0] x,
	input [N-1:0] y,
	output [2*N-1:0] z
);

wire [N-1:0] P[N-1:0];


assign P[0][0] = x[0] & y[0];
assign P[0][1] = x[0] & y[1];
assign P[0][2] = x[0] & y[2];
assign P[0][3] = x[0] & y[3];
assign P[0][4] = x[0] & y[4];
assign P[0][5] = x[0] & y[5];
assign P[0][6] = x[0] & y[6];
assign P[0][7] = x[0] & y[7];
assign P[1][0] = x[1] & y[0];
assign P[1][1] = x[1] & y[1];
assign P[1][2] = x[1] & y[2];
assign P[1][3] = x[1] & y[3];
assign P[1][4] = x[1] & y[4];
assign P[1][5] = x[1] & y[5];
assign P[1][6] = x[1] & y[6];
assign P[1][7] = x[1] & y[7];
assign P[2][0] = x[2] & y[0];
assign P[2][1] = x[2] & y[1];
assign P[2][2] = x[2] & y[2];
assign P[2][3] = x[2] & y[3];
assign P[2][4] = x[2] & y[4];
assign P[2][5] = x[2] & y[5];
assign P[2][6] = x[2] & y[6];
assign P[2][7] = x[2] & y[7];
assign P[3][0] = x[3] & y[0];
assign P[3][1] = x[3] & y[1];
assign P[3][2] = x[3] & y[2];
assign P[3][3] = x[3] & y[3];
assign P[3][4] = x[3] & y[4];
assign P[3][5] = x[3] & y[5];
assign P[3][6] = x[3] & y[6];
assign P[3][7] = x[3] & y[7];
assign P[4][0] = x[4] & y[0];
assign P[4][1] = x[4] & y[1];
assign P[4][2] = x[4] & y[2];
assign P[4][3] = x[4] & y[3];
assign P[4][4] = x[4] & y[4];
assign P[4][5] = x[4] & y[5];
assign P[4][6] = x[4] & y[6];
assign P[4][7] = x[4] & y[7];
assign P[5][0] = x[5] & y[0];
assign P[5][1] = x[5] & y[1];
assign P[5][2] = x[5] & y[2];
assign P[5][3] = x[5] & y[3];
assign P[5][4] = x[5] & y[4];
assign P[5][5] = x[5] & y[5];
assign P[5][6] = x[5] & y[6];
assign P[5][7] = x[5] & y[7];
assign P[6][0] = x[6] & y[0];
assign P[6][1] = x[6] & y[1];
assign P[6][2] = x[6] & y[2];
assign P[6][3] = x[6] & y[3];
assign P[6][4] = x[6] & y[4];
assign P[6][5] = x[6] & y[5];
assign P[6][6] = x[6] & y[6];
assign P[6][7] = x[6] & y[7];
assign P[7][0] = x[7] & y[0];
assign P[7][1] = x[7] & y[1];
assign P[7][2] = x[7] & y[2];
assign P[7][3] = x[7] & y[3];
assign P[7][4] = x[7] & y[4];
assign P[7][5] = x[7] & y[5];
assign P[7][6] = x[7] & y[6];
assign P[7][7] = x[7] & y[7];

wire [56:0] S;
wire [56:0] Cout;
Half_Adder HA1 (P[0][6], P[1][5], S[1], Cout[1]);
Full_Adder FA1 (P[0][7], P[1][6], P[2][5], S[2], Cout[2]);
Half_Adder HA2 (P[3][4], P[4][3], S[3], Cout[3]);
Full_Adder FA2 (P[1][7], P[2][6], P[3][5], S[4], Cout[4]);
Half_Adder HA3 (P[4][4], P[5][3], S[5], Cout[5]);
Full_Adder FA3 (P[2][7], P[3][6], P[4][5], S[6], Cout[6]);
Half_Adder HA4 (P[0][4], P[1][3], S[7], Cout[7]);
Full_Adder FA4 (P[0][5], P[1][4], P[2][3], S[8], Cout[8]);
Half_Adder HA5 (P[3][2], P[4][1], S[9], Cout[9]);
Full_Adder FA5 (P[2][4], P[3][3], P[4][2], S[10], Cout[10]);
Full_Adder FA6 (P[5][1], P[6][0], S[1], S[11], Cout[11]);
Full_Adder FA7 (P[5][2], P[6][1], P[7][0], S[12], Cout[12]);
Full_Adder FA8 (Cout[1], S[2], S[3], S[13], Cout[13]);
Full_Adder FA9 (P[6][2], P[7][1], Cout[2], S[14], Cout[14]);
Full_Adder FA10 (Cout[3], S[4], S[5], S[15], Cout[15]);
Full_Adder FA11 (P[5][4], P[6][3], P[7][2], S[16], Cout[16]);
Full_Adder FA12 (Cout[4], Cout[5], S[6], S[17], Cout[17]);
Full_Adder FA13 (P[3][7], P[4][6], P[5][5], S[18], Cout[18]);
Full_Adder FA14 (P[6][4], P[7][3], Cout[6], S[19], Cout[19]);
Full_Adder FA15 (P[4][7], P[5][6], P[6][5], S[20], Cout[20]);
Half_Adder HA6 (P[0][3], P[1][2], S[21], Cout[21]);
Full_Adder FA16 (P[2][2], P[3][1], P[4][0], S[22], Cout[22]);
Full_Adder FA17 (P[5][0], Cout[7], S[8], S[23], Cout[23]);
Full_Adder FA18 (Cout[8], Cout[9], S[10], S[24], Cout[24]);
Full_Adder FA19 (Cout[10], Cout[11], S[12], S[25], Cout[25]);
Full_Adder FA20 (Cout[12], Cout[13], S[14], S[26], Cout[26]);
Full_Adder FA21 (Cout[14], Cout[15], S[16], S[27], Cout[27]);
Full_Adder FA22 (Cout[16], Cout[17], S[18], S[28], Cout[28]);
Full_Adder FA23 (P[7][4], Cout[18], Cout[19], S[29], Cout[29]);
Full_Adder FA24 (P[5][7], P[6][6], P[7][5], S[30], Cout[30]);
Half_Adder HA7 (P[0][2], P[1][1], S[31], Cout[31]);
Full_Adder FA25 (P[2][1], P[3][0], S[21], S[32], Cout[32]);
Full_Adder FA26 (S[7], Cout[21], S[22], S[33], Cout[33]);
Full_Adder FA27 (S[9], Cout[22], S[23], S[34], Cout[34]);
Full_Adder FA28 (S[11], Cout[23], S[24], S[35], Cout[35]);
Full_Adder FA29 (S[13], Cout[24], S[25], S[36], Cout[36]);
Full_Adder FA30 (S[15], Cout[25], S[26], S[37], Cout[37]);
Full_Adder FA31 (S[17], Cout[26], S[27], S[38], Cout[38]);
Full_Adder FA32 (S[19], Cout[27], S[28], S[39], Cout[39]);
Full_Adder FA33 (S[20], Cout[28], S[29], S[40], Cout[40]);
Full_Adder FA34 (Cout[20], Cout[29], S[30], S[41], Cout[41]);
Full_Adder FA35 (P[6][7], P[7][6], Cout[30], S[42], Cout[42]);
Half_Adder HA8 (P[0][1], P[1][0], S[0], Cout[0]);
Full_Adder FA36 (P[2][0], S[31], Cout[0], S[43], Cout[43]);
Full_Adder FA37 (Cout[31], S[32], Cout[43], S[44], Cout[44]);
Full_Adder FA38 (Cout[32], S[33], Cout[44], S[45], Cout[45]);
Full_Adder FA39 (Cout[33], S[34], Cout[45], S[46], Cout[46]);
Full_Adder FA40 (Cout[34], S[35], Cout[46], S[47], Cout[47]);
Full_Adder FA41 (Cout[35], S[36], Cout[47], S[48], Cout[48]);
Full_Adder FA42 (Cout[36], S[37], Cout[48], S[49], Cout[49]);
Full_Adder FA43 (Cout[37], S[38], Cout[49], S[50], Cout[50]);
Full_Adder FA44 (Cout[38], S[39], Cout[50], S[51], Cout[51]);
Full_Adder FA45 (Cout[39], S[40], Cout[51], S[52], Cout[52]);
Full_Adder FA46 (Cout[40], S[41], Cout[52], S[53], Cout[53]);
Full_Adder FA47 (Cout[41], S[42], Cout[53], S[54], Cout[54]);
Full_Adder FA48 (P[7][7], Cout[42], Cout[54], S[55], Cout[55]);

assign z[15] = Cout[55];
assign z[14] = S[55];
assign z[13] = S[54];
assign z[12] = S[53];
assign z[11] = S[52];
assign z[10] = S[51];
assign z[9] = S[50];
assign z[8] = S[49];
assign z[7] = S[48];
assign z[6] = S[47];
assign z[5] = S[46];
assign z[4] = S[45];
assign z[3] = S[44];
assign z[2] = S[43];
assign z[1] = S[0];
assign z[0] = P[0][0];

endmodule