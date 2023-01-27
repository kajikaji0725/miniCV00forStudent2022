package lang.c.parse.decl;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.factor.Factor;

public class IntDecl extends CParseRule {
    // intDecl ::= INT declItem { COMMA declItem } SEMI
    private CParseRule intDecl;

    public IntDecl(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_INT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        tk = ct.getNextToken(pcx);

        if (IntDecl.isFirst(tk)) {
            intDecl = new IntDecl(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "intDecl error");
        }
        intDecl.parse(pcx);

        tk = ct.getCurrentToken(pcx);
        while(true){
            
        }

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; term starts");
        // if (term != null) {
        // term.codeGen(pcx);
        // }
        o.println(";;; term completes");
    }
}
