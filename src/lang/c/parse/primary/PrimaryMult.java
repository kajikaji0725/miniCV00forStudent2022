package lang.c.parse.primary;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Variable;

public class PrimaryMult extends CParseRule {
    // primaryMult ::= MULT variable
    private CParseRule variable;

    public PrimaryMult(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MULT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);
        variable = new Variable(pcx);
        variable.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        this.setCType(CType.getCType(CType.T_int));
        this.setConstant(true);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; number starts");
        if (variable != null) {
            // o.println("\tMOV\t#" + num.getText() + ", (R6)+\t\t; Number: 数を積む<" +
            // num.toExplainString() + ">");
            variable.codeGen(pcx);
            o.println("primaryMult hoge");
        }
        o.println(";;; number completes");
    }
}
