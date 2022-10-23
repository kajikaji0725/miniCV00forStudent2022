package lang.c.parse.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

class PlusFactor extends CParseRule {
    // number ::= PLUS unsignedFactor
    private CParseRule plusFactor = null, list = null;

    public PlusFactor(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_PLUS;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        // num = tk;
        tk = ct.getNextToken(pcx);

        if (UnsignedFactor.isFirst(tk)) {
            list = new UnsignedFactor(pcx);
        } else {
            // pcx.fatalError("MINUSのあとはnumber、Amp、LPARです");
        }
        list.parse(pcx);
        plusFactor = list;
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (list != null) {
            list.semanticCheck(pcx);
            this.setCType(CType.getCType(CType.T_int));
            this.setConstant(true);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (plusFactor != null && list != null) {
            list.codeGen(pcx);
        }
    }
}