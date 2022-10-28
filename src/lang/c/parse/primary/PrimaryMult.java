package lang.c.parse.primary;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Variable;

public class PrimaryMult extends CParseRule {
    // primaryMult ::= MULT variable
    private CToken op;
    private CParseRule variable;

    public PrimaryMult(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MULT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        op = tk;
        tk = ct.getNextToken(pcx);
        variable = new Variable(pcx);
        variable.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (variable != null) {
            variable.semanticCheck(pcx);
            CType type = variable.getCType();

            if(type.isCType(CType.T_int)){
                pcx.fatalError("*の変数名はint型ではだめです");
            }

            this.setCType(variable.getCType());
            this.setConstant(false);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; PrimaryMult starts");
        if (variable != null) {
            // o.println("\tMOV\t#" + num.getText() + ", (R6)+\t\t; Number: 数を積む<" +
            // num.toExplainString() + ">");
            variable.codeGen(pcx);
            o.println("\tMOV\t-(R6), R0\t; PrimaryMult: アドレスを取り出して、内容を参照して、積む<"
                    + op.toExplainString() + ">");
            o.println("\tMOV\t(R0), (R6)+\t; PrimaryMult:");
        }
        o.println(";;; PrimaryMult completes");
    }
}
