package lang.c.parse.condition;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.exper.Expression;
import lang.c.parse.opr.exper.OprExper;

public class ConditionOpr extends CParseRule {
    // conditionOpr ::= condition { oprExper }
    private CParseRule condi, opr;

    public ConditionOpr(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Condition.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        tk = ct.getCurrentToken(pcx);
        if (Condition.isFirst(tk)) {
            opr = new Condition(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "conditionOpr condition error");
        }
        opr.parse(pcx);

        tk = ct.getCurrentToken(pcx);
        if (OprExper.isFirst(tk)) {
            System.out.println("hogehoge");
            condi = new OprExper(pcx);
            condi.parse(pcx);
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
