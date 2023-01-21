package lang.c.parse.opr.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.Condition;

public class OprFactor extends CParseRule {
    // opsFactor ::= oprExclam | condition
    private CParseRule factor;

    public OprFactor(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return OprExc.isFirst(tk) || Condition.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        if (OprExc.isFirst(tk)) {
            factor = new OprExc(pcx);
        } else {
            factor = new Condition(pcx);
        }
        factor.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (factor != null) {
            factor.semanticCheck(pcx);
            setCType(factor.getCType());
            setConstant(factor.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factor starts");
        if (factor != null) {
            factor.codeGen(pcx);
        }
        o.println(";;; factor completes");
    }
}
