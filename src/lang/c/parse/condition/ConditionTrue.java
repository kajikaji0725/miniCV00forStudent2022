package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class ConditionTrue extends CParseRule {
    // conditionTrue ::= TRUE

    public ConditionTrue(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_TRUE;
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
        o.println(";;; TRUE starts");
        o.println("\tMOV\t#0x0001, (R6)+\t; conditionTRUE: set true");
        o.println(";;; TRUE completes");
    }
}
