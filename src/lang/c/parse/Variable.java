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
        CParseRule list = null, ar = null;
        ident = new Ident(pcx);
        ident.parse(pcx);
        // ct.getNextToken(pcx);
        tk = ct.getCurrentToken(pcx);
        while (true) {
            if (Array.isFirst(tk)) {
                ct.getNextToken(pcx);
                list = new Array(pcx);
                // System.out.println(ct.getCurrentToken(pcx).getText());
            } else {
                break;
            }
            list.parse(pcx);
            tk = ct.getCurrentToken(pcx);
            ar = list;
        }
        array = ar;
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (ident != null) {
            ident.semanticCheck(pcx);
            CType type = ident.getCType();
            if (array != null) {
                array.semanticCheck(pcx);
                if (ident.isConstant()) {
                    pcx.fatalError("配列の定数は宣言できません");
                }
                if (type.isCType(CType.T_int) || type.isCType(CType.T_pint)) {
                    pcx.fatalError("配列を宣言する際は、ia_かipa_です");
                }
            } else {
                if (type.isCType(CType.T_aint) || type.isCType(CType.T_apint)) {
                    pcx.fatalError("配列の宣言には[]が必要です");
                }
            }

            this.setCType(ident.getCType());
            this.setConstant(ident.isConstant());
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Variable starts");
        if (ident != null) {
            ident.codeGen(pcx);
            if (array != null) {
                array.codeGen(pcx);
                o.println("\tMOV\t-(R6), R1\t; Arrayのexpの値をR1に格納する");
                o.println("\tMOV\t-(R6), R0\t; IdentのアドレスをR0に格納する");
                o.println("\tADD\tR1, R0   \t; 参照するアドレス値を計算");
                o.println("\tMOV\tR0, (R6)+\t; 計算結果をスタックに積む");
            }
        }
        o.println(";;; Variable completes");
    }
}
