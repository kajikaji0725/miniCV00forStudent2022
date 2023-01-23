package lang.c.parse.opr.exper;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.opr.term.OprTerm;

public class OprExper extends CParseRule {
    // oprExper ::= oprTerm { oprOr }
    private CParseRule expression;

    public OprExper(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return OprTerm.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CParseRule term = null, list = null;
        term = new OprTerm(pcx);
        term.parse(pcx);
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        while (true) {
            if (OprOr.isFirst(tk)) {
                list = new OprOr(pcx, term);
            } else {
                break;
            }
            list.parse(pcx);
            term = list;
            tk = ct.getCurrentToken(pcx);
        }
        expression = term;
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (expression != null) {
            expression.semanticCheck(pcx);
            this.setCType(expression.getCType()); // expression の型をそのままコピー
            this.setConstant(expression.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; expression starts");
        if (expression != null)
            expression.codeGen(pcx);
        o.println(";;; expression completes");
    }
}
