package lang.c;

public class CState {
    public static final int S_EOF = 1;
    public static final int S_ERR = 2;
    public static final int S_PLUS = 3;
    public static final int S_MINUS = 4;
    public static final int S_SUB = 5;
    public static final int S_MULT = 6;
    public static final int S_DIV = 7;
    public static final int S_DEC = 8;
    public static final int S_OCT = 9;
    public static final int S_HEX = 10;
    public static final int S_COMMENT = 11; // //
    public static final int S_LPAR = 12; // (
    public static final int S_RPAR = 13; // )
    public static final int S_AMP = 14; // &
    public static final int S_LBRA = 15; // [
    public static final int S_RBRA = 16; // ]
    public static final int S_IDENT = 17; // indent
}
