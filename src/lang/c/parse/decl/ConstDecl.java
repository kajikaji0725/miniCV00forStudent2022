package lang.c.parse.decl;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.factor.Factor;

public class ConstDecl extends CParseRule {
    // ConstDecl ::= CONST INT ConstItem { COMMA ConstItem } SEMI
    private CParseRule constItem;
    private ArrayList<CParseRule> constDelcs = new ArrayList<CParseRule>();

    public ConstDecl(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_CONST;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        tk = ct.getNextToken(pcx);

        if (tk.getType() != CToken.TK_INT) {
            pcx.fatalError(tk.toExplainString() + "const int 変数名という形にしてください");
        }
        tk = ct.getNextToken(pcx);

        if (ConstItem.isFirst(tk)) {
            constItem = new ConstItem(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "intDecl error");
        }
        constItem.parse(pcx);
        constDelcs.add(constItem);

        tk = ct.getCurrentToken(pcx);
        while (tk.getType() == CToken.TK_COMMA) {
            tk = ct.getNextToken(pcx);
            constItem = new ConstItem(pcx);
            constItem.parse(pcx);
            constDelcs.add(constItem);

            tk = ct.getCurrentToken(pcx);
        }

        if (tk.getType() != CToken.TK_SEMI) {
            pcx.fatalError(tk.toExplainString() + "セミコロンが必要です");
        }
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        for (CParseRule constDecl : constDelcs) {
            constDecl.semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; ConstDecl starts");
        for (CParseRule constDecl : constDelcs) {
            constDecl.codeGen(pcx);
        }
        o.println(";;; ConstDecl completes");
    }
}
