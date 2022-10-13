package lang.c;

import lang.SimpleToken;

public class CToken extends SimpleToken {
	public static final int TK_PLUS = 2; // +
	public static final int TK_MINUS = 3; // -
	public static final int TK_DIV = 4; // /
	public static final int TK_MULT = 5; // *
	public static final int TK_L = 6; // (
	public static final int TK_R = 7; // )
	// public static final int TK_LR = 8; // ()

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}
