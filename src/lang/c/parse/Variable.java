package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Variable extends CParseRule {
    // variable ::= ident [ array ]
    private CParseRule ident, array;

    public Variable(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Ident.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        ident = new Ident(pcx);
        ident.parse(pcx);
        // ct.getNextToken(pcx);
        tk = ct.getCurrentToken(pcx);
        if (!Array.isFirst(tk)) {
            pcx.fatalError(tk.toExplainString() + "配列は[1]のように宣言してください");
        } else {
            ct.getNextToken(pcx);
            array = new Array(pcx);
            // System.out.println(ct.getCurrentToken(pcx).getText());
            array.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (ident != null && array != null) {
            ident.semanticCheck(pcx);
            
            this.setCType(CType.getCType(CType.T_int));
            this.setConstant(true);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; number starts");
        if (ident != null) {
            // o.println("\tMOV\t#" + num.getText() + ", (R6)+\t\t; Number: 数を積む<" +
            // num.toExplainString() + ">");
            // o.println("variable hoge");
            array.semanticCheck(pcx);
            ident.codeGen(pcx);
            array.codeGen(pcx);
        }
        o.println(";;; number completes");
    }
}
