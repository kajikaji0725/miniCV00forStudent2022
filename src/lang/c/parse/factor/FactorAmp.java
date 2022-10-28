package lang.c.parse.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Number;
import lang.c.parse.primary.Primary;
import lang.c.parse.primary.PrimaryMult;

class FactorAmp extends CParseRule {
    // factorAmp ::= AMP (NUM | primary)
    private CParseRule factAmp;

    public FactorAmp(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AMP;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);
        CParseRule list = null;

        if (Number.isFirst(tk)) {
            list = new Number(pcx);
            list.parse(pcx);
            factAmp = list;
        } else if (Primary.isFirst(tk)) {
            if (PrimaryMult.isFirst(tk)) {
                pcx.fatalError("&*~の記法はできません");
            }
            list = new Primary(pcx);
            list.parse(pcx);
            factAmp = list;
        } else {
            pcx.fatalError("&のエラーです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        factAmp.semanticCheck(pcx);

        if (factAmp.getCType() == CType.getCType(CType.T_pint) || factAmp.getCType() == CType.getCType(CType.T_apint)) {
            pcx.fatalError("今回はポインタのポインタは使えません");
        }

        this.setCType(CType.getCType(CType.T_pint));
        this.setConstant(true);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; factorAmp starts");
        if (factAmp != null) {
            factAmp.codeGen(pcx);
        }
        o.println(";;; factorAmp completes");
    }
}