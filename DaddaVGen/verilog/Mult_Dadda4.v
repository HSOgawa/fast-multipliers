`timescale 1ns / 1ps

module Mult_Dadda4 # (
	parameter N = 4
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
assign P[1][0] = x[1] & y[0];
assign P[1][1] = x[1] & y[1];
assign P[1][2] = x[1] & y[2];
assign P[1][3] = x[1] & y[3];
assign P[2][0] = x[2] & y[0];
assign P[2][1] = x[2] & y[1];
assign P[2][2] = x[2] & y[2];
assign P[2][3] = x[2] & y[3];
assign P[3][0] = x[3] & y[0];
assign P[3][1] = x[3] & y[1];
assign P[3][2] = x[3] & y[2];
assign P[3][3] = x[3] & y[3];

wire [12:0] S;
wire [12:0] Cout;
Half_Adder HA1 (P[0][3], P[1][2], S[1], Cout[1]);
Half_Adder HA2 (P[1][3], P[2][2], S[2], Cout[2]);
Half_Adder HA3 (P[0][2], P[1][1], S[3], Cout[3]);
Full_Adder FA1 (P[2][1], P[3][0], S[1], S[4], Cout[4]);
Full_Adder FA2 (P[3][1], Cout[1], S[2], S[5], Cout[5]);
Full_Adder FA3 (P[2][3], P[3][2], Cout[2], S[6], Cout[6]);
Half_Adder HA4 (P[0][1], P[1][0], S[0], Cout[0]);
Full_Adder FA4 (P[2][0], S[3], Cout[0], S[7], Cout[7]);
Full_Adder FA5 (Cout[3], S[4], Cout[7], S[8], Cout[8]);
Full_Adder FA6 (Cout[4], S[5], Cout[8], S[9], Cout[9]);
Full_Adder FA7 (Cout[5], S[6], Cout[9], S[10], Cout[10]);
Full_Adder FA8 (P[3][3], Cout[6], Cout[10], S[11], Cout[11]);

assign z[7] = Cout[11];
assign z[6] = S[11];
assign z[5] = S[10];
assign z[4] = S[9];
assign z[3] = S[8];
assign z[2] = S[7];
assign z[1] = S[0];
assign z[0] = P[0][0];

endmodule