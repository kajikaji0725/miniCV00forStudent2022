package lang.c.parse.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Number;

class FactorAmp extends CParseRule {
    // number ::= AMP NUM
    private CToken num;
    private CParseRule factAmp;

    public FactorAmp(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AMP;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        num = tk;
        tk = ct.getNextToken(pcx);

        if (Number.isFirst(tk)) {
            CParseRule list = null;
            list = new Number(pcx);
            list.parse(pcx);

            factAmp = list;
        } else {
            pcx.fatalError("&のエラーです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        this.setCType(CType.getCType(CType.T_pint));
        this.setConstant(true);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; number starts");
        if (factAmp != null) {
            factAmp.codeGen(pcx);
        }
        o.println(";;; number completes");
    }
}