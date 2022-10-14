package lang.c.parse.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

class FactorAmp extends CParseRule {
    // number ::= AMP NUM
    private CToken num;

    public FactorAmp(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AMP;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        num = tk;
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        this.setCType(CType.getCType(CType.T_pint));
        this.setConstant(true);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; number starts");
        if (num != null) {
            o.println("\tMOV\t#" + num.getText() + ", (R6)+\t; Number: 数を積む<" +
                    num.toExplainString() + ">");
        }
        o.println(";;; number completes");
    }
}