package lang.c.parse.opr.term;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.opr.factor.OprFactor;

public class OprTerm extends CParseRule {
    // oprTerm ::= oprFactor { oprAnd }
    private CParseRule term;

    public OprTerm(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return OprFactor.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CParseRule list = null, factor = null;
        factor = new OprFactor(pcx);
        factor.parse(pcx);

        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        while(true){
            if(OprAnd.isFirst(tk)){
                list = new OprAnd(factor);
            }else{
                break;
            }
            list.parse(pcx);
            factor = list;
            tk=ct.getCurrentToken(pcx);
        }
        term = factor;
        // ここにやってくるときは、必ずisFirst()が満たされている
        // CParseRule list = null, factor = null;
        // factor = new Factor(pcx);
        // factor.parse(pcx);

        // CTokenizer ct = pcx.getTokenizer();
        // CToken tk = ct.getCurrentToken(pcx);
        // while (true) {
        // if (TermDiv.isFirst(tk)) {
        // list = new TermDiv(factor);
        // } else if (TermMult.isFirst(tk)) {
        // list = new TermMult(factor);
        // } else {
        // break;
        // }
        // list.parse(pcx);
        // factor = list;
        // tk = ct.getCurrentToken(pcx);
        // }
        // term = factor;
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (term != null) {
            term.semanticCheck(pcx);
            this.setCType(term.getCType()); // factor の型をそのままコピー
            this.setConstant(term.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; term starts");
        if (term != null) {
            term.codeGen(pcx);
        }
        o.println(";;; term completes");
    }
}
