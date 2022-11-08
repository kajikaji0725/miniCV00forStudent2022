package lang.c.parse.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

class MinusFactor extends CParseRule {
    // minusFactor ::= MINUS unsignedFactor
    private CParseRule miFactor = null, list = null;

    public MinusFactor(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MINUS;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {

        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);
        if (tk.getType() == 8) {
            pcx.fatalError("-の後ろに&はおけません");
        }
        if (UnsignedFactor.isFirst(tk)) {
            list = new UnsignedFactor(pcx);
        } else {
            // pcx.fatalError("MINUSのあとはnumber、Amp、LPARです");
        }
        list.parse(pcx);
        miFactor = list;
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (miFactor != null && list != null) {
            list.semanticCheck(pcx);

            if (list.getCType() == CType.getCType(CType.T_pint) || list.getCType() == CType.getCType(CType.T_apint)) {
                pcx.fatalError("ポインタに符号はつけれません");
            }

            this.setCType(CType.getCType(CType.T_int));
            this.setConstant(true);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; number starts");
        if (miFactor != null && list != null) {
            list.codeGen(pcx);
            // XORとADDを使わなくても、0-R0をすれば、符号を得られるため、処理を簡略化できる。
            o.println("\tMOV\t-(R6), R0\t; スタックに積んだ'-'符号の数値をR0に移す");
            o.println("\tMOV\t#0, R1\t\t; 引き算をするために、R1に0を入れる");
            o.println("\tSUB\tR0, R1\t\t; R1-R0をすることで、0-R0を実現している");
            o.println("\tMOV\tR1, (R6)+\t; 結果をスタックに積む");
        }
        o.println(";;; number completes");
    }
}