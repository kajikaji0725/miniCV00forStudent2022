package lang.c;

public class CState {
    public static enum State {
        S_CLR,
        S_EOF,
        S_ERR,
        S_PLUS,
        S_MINUS,
        S_MULT,
        S_DIV,
        S_DEC,
        S_OCT,
        S_HEX,
        S_COMMENT,
        S_LPAR,
        S_RPAR,
        S_AMP,
        S_LBRA,
        S_RBRA,
        S_IDENT,
        S_ASSIGN,
        S_SEMI,
        S_LT,
        S_LE,
        S_GT,
        S_GE,
        S_EQ,
        S_NE,
        S_LCUR,
        S_RCUR,
        S_AND,
        S_OR,
        S_EXCLAM
    }

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
    public static final int S_ASSIGN = 18; // =
    public static final int S_SEMI = 19; // ;
    public static final int S_TRUE = 20; // true
    public static final int S_FALSE = 21; // false
    public static final int S_LT = 22; // <
    public static final int S_LE = 23; // <=
    public static final int S_GT = 24; // >
    public static final int S_GE = 25; // >=
    public static final int S_EQ = 26; // ==
    public static final int S_NE = 27; // !=
    public static final int S_LCUR = 28; // {
    public static final int S_RCUR = 29; // }
}
