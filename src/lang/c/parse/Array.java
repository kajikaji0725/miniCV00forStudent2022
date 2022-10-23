package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.exper.Expression;

public class Array extends CParseRule {
    // array ::= LBRA expression RBRA
    private CToken array;
    private CParseRule lrFactor;

    public Array(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LBRA;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        tk = ct.getCurrentToken(pcx);
        lrFactor = new Expression(pcx);
        lrFactor.parse(pcx);
        tk = ct.getCurrentToken(pcx);

        if (tk.getType() != CToken.TK_RBRA) {
            pcx.fatalError(tk.toExplainString() + "]で終わっていない");
        } else {
            ct.getNextToken(pcx);
            tk = ct.getCurrentToken(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        lrFactor.semanticCheck(pcx);
        CType type = lrFactor.getCType();
        if (!type.isCType(CType.T_int)) {
            pcx.fatalError("配列の要素はint型だけです");
        }
        this.setCType(CType.getCType(CType.T_aint));
        this.setConstant(true);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; number starts");
        if (array != null) {
            // o.println("\tMOV\t#" + num.getText() + ", (R6)+\t\t; Number: 数を積む<" +
            // num.toExplainString() + ">");
            o.println("Array hoge");
        }
        o.println(";;; number completes");
    }
}
