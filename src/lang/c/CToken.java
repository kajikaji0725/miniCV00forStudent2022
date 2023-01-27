package lang.c;

import lang.SimpleToken;

public class CToken extends SimpleToken {

	public static final int TK_PLUS = 2; // +
	public static final int TK_MINUS = 3; // -
	public static final int TK_DIV = 4; // /
	public static final int TK_MULT = 5; // *
	public static final int TK_LPAR = 6; // (
	public static final int TK_RPAR = 7; // )
	public static final int TK_LBRA = 8; // [
	public static final int TK_RBRA = 9; // ]
	public static final int TK_IDENT = 10; // indent
	public static final int TK_ASSIGN = 11; // =
	public static final int TK_SEMI = 12; // ;
	public static final int TK_TRUE = 13; // true
	public static final int TK_FALSE = 14; // false
	public static final int TK_LT = 15; // <
	public static final int TK_LE = 16; // <=
	public static final int TK_GT = 17; // >
	public static final int TK_GE = 18; // >=
	public static final int TK_EQ = 19; // ==
	public static final int TK_NE = 20; // !=
	public static final int TK_IF = 21; // if
	public static final int TK_WHILE = 22; // while
	public static final int TK_INPUT = 23; // input
	public static final int TK_OUTPUT = 24; // output
	public static final int TK_LCUR = 25; // {
	public static final int TK_RCUR = 26; // }
	public static final int TK_ELSE = 27; // else
	public static final int TK_ELSEIf = 28; // else if
	public static final int TK_EXCLAM = 29; // !
	public static final int TK_AND = 30; // &&
	public static final int TK_OR = 31; // ||
	public static final int TK_INT = 32; // int
	public static final int TK_CONST = 33; //const
    public static final int TK_COMMA = 34; // ,

	public CToken(int type, int lineNo, int colNo, String s) {
		super(type, lineNo, colNo, s);
	}
}
