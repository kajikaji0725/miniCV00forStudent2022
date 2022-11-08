package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionFalse extends CParseRule {
    // conditionTrue ::= FALSE

    public ConditionFalse(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_FALSE;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; FALSE starts");
        o.println("\tMOV\t#0x0000, R2\t; conditionFALSE: set false");
        o.println(";;; FALSE completes");
    }
}
