package lang.c.parse.decl;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.factor.Factor;

public class Declaration extends CParseRule {
    // decl ::= intDecl | constDecl
    private CParseRule decl;

    public Declaration(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return IntDecl.isFirst(tk) || ConstDecl.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        if (IntDecl.isFirst(tk)) {
            decl = new IntDecl(pcx);
        } else {
            decl = new ConstDecl(pcx);
        }

        decl.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        decl.semanticCheck(pcx);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Declaration starts");
        decl.codeGen(pcx);
        o.println(";;; Declaration completes");
    }
}
