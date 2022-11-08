package lang.c.parse.primary;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Variable;

public class Primary extends CParseRule {
    // primary ::= primaryMult | variable
    private CParseRule primary;

    public Primary(CParseContext pcx) {
    }

    public boolean check() {
        return primary == null ? false : primary instanceof PrimaryMult;
    }

    public static boolean isFirst(CToken tk) {
        return PrimaryMult.isFirst(tk) || Variable.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CToken ck = pcx.getTokenizer().getCurrentToken(pcx);
        if (PrimaryMult.isFirst(ck)) {
            primary = new PrimaryMult(pcx);
        } else {
            primary = new Variable(pcx);
        }
        primary.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null) {
            primary.semanticCheck(pcx);
            setCType(primary.getCType());
            setConstant(primary.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Primary starts");
        if (primary != null) {
            primary.codeGen(pcx);
        }
        o.println(";;; Primary completes");
    }
}
