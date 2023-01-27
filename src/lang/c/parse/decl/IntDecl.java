package lang.c.parse.decl;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.factor.Factor;

public class IntDecl extends CParseRule {
    // intDecl ::= INT declItem { COMMA declItem } SEMI
    private CParseRule declItem;
    private ArrayList<CParseRule> intDelcs = new ArrayList<CParseRule>();

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

        if (DeclItem.isFirst(tk)) {
            declItem = new DeclItem(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "intDecl error");
        }
        declItem.parse(pcx);
        intDelcs.add(declItem);

        tk = ct.getCurrentToken(pcx);
        while (tk.getType() == CToken.TK_COMMA) {
            tk = ct.getNextToken(pcx);
            declItem = new DeclItem(pcx);
            declItem.parse(pcx);
            intDelcs.add(declItem);

            tk = ct.getCurrentToken(pcx);
        }

        if (tk.getType() != CToken.TK_SEMI) {
            pcx.fatalError(tk.toExplainString() + "セミコロンが必要です");
        }
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        for (CParseRule intDelc : intDelcs) {
            intDelc.semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; IntDecl starts");
        for (CParseRule intDelc : intDelcs) {
            intDelc.codeGen(pcx);
        }
        o.println(";;; IntDecl completes");
    }
}
