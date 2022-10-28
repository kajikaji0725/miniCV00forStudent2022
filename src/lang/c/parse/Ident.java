package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
    // ident ::= Ident
    private CToken ident;

    public Ident(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IDENT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        ident = tk;
        tk = ct.getNextToken(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        String text = ident.getText();
        int type = 0;
        boolean constantFlag = false;

        if (text.length() > 2 && text.substring(0, 2).equals("i_")) {
            type = CType.T_int;
        } else if (text.length() > 3 && text.substring(0, 3).equals("ip_")) {
            type = CType.T_pint;
        } else if (text.length() > 3 && text.substring(0, 3).equals("ia_")) {
            type = CType.T_aint;
        } else if (text.length() > 3 && text.substring(0, 4).equals("ipa_")) {
            type = CType.T_apint;
        } else if (text.length() > 2 && text.substring(0, 2).equals("c_")) {
            type = CType.T_int;
            constantFlag = true;
        } else {
            pcx.fatalError("変数は、i_ ip_ ia_ ipa_ c_ のどちらかで宣言してください");
        }

        this.setCType(CType.getCType(type));
        this.setConstant(constantFlag);
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Ident starts");
        if (ident != null) {
            // o.println("\tMOV\t#" + num.getText() + ", (R6)+\t\t; Number: 数を積む<" +
            // num.toExplainString() + ">");
            // o.println("ident hoge");

            if (ident != null) {
                o.println("\tMOV\t#" + ident.getText() + ", (R6)+\t; Ident: 変数アドレスを積む<"
                        + ident.toExplainString() + ">");
            }

        }
        o.println(";;; Ident completes");
    }
}
