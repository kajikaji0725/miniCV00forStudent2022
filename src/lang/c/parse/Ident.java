package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
    // ident ::= Ident
    private CToken ident;
    private CSymbolTableEntry entry;

    public Ident(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IDENT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        ident = tk;

        entry = pcx.getSymbolTable().search(ident.getText());
        if (entry == null) {
            pcx.fatalError(ident.toExplainString() + "変数名" + ident.getText() + "は宣言されていません");
        }
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        this.setCType(entry.getType());
        this.setConstant(entry.isConst());
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Ident starts");
        if (ident != null) {
            o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 大域変数アドレスを積む<"
                    + ident.toExplainString() + ">");
        }
        o.println(";;; Ident completes");
    }
}
