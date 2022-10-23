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
        if (Array.isFirst(tk)) {
            ct.getNextToken(pcx);
            array = new Array(pcx);
            // System.out.println(ct.getCurrentToken(pcx).getText());
            array.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (ident != null && array != null) {
            ident.semanticCheck(pcx);

            this.setCType(ident.getCType());
            this.setConstant(ident.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; number starts");
        if (ident != null) {
            // o.println("\tMOV\t#" + num.getText() + ", (R6)+\t\t; Number: 数を積む<" +
            // num.toExplainString() + ">");
            // o.println("variable hoge");
            ident.semanticCheck(pcx);
            if (array != null) {
                if (ident.isConstant()) {
                    pcx.fatalError("配列の定数は宣言できません");
                }
                array.semanticCheck(pcx);
                ident.codeGen(pcx);
                array.codeGen(pcx);
            } else {
                ident.codeGen(pcx);
            }
        }
        o.println(";;; number completes");
    }
}
