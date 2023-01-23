package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.opr.exper.OprExper;
import lang.c.parse.condition.*;

public class StateCondition extends CParseRule {
    // StateCondition ::=  oprExper 
    private CParseRule condi;

    public StateCondition(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Condition.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        tk = ct.getCurrentToken(pcx);
        if (OprExper.isFirst(tk)) {
            condi = new OprExper(pcx);
            condi.parse(pcx);
            tk = ct.getCurrentToken(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condi != null) {
            condi.semanticCheck(pcx);
            setCType(condi.getCType()); // condi の型をそのままコピー
            setConstant(condi.isConstant()); // condi は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition starts");
        if (condi != null) {
            condi.codeGen(pcx);
        }
        o.println(";;; condition completes");
    }
}
