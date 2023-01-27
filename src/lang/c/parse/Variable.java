package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Variable extends CParseRule {
    // variable ::= ident [ array ]
    private CParseRule ident, array;
    private CToken name;

    public Variable(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Ident.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        name = tk;
        ident = new Ident(pcx);
        ident.parse(pcx);
        // ct.getNextToken(pcx);
        tk = ct.getCurrentToken(pcx);

        if (Array.isFirst(tk)) {
            array = new Array(pcx);
            array.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (ident != null) {
            ident.semanticCheck(pcx);
            CSymbolTableEntry type = pcx.getSymbolTable().search(name.getText());
            if (array != null) {
                array.semanticCheck(pcx);
                if (ident.isConstant()) {
                    pcx.fatalError("配列の定数は宣言できません");
                }
                if (type.getType().isCType(CType.T_int) || type.getType().isCType(CType.T_pint)) {
                    pcx.fatalError(name.toExplainString() + "変数" + name.getText() + "は配列ではありません");
                }
                this.setCType(CType.changeType(type.getType()));
                this.setConstant(ident.isConstant());
            } else {
                if (type.getType().isCType(CType.T_aint) || type.getType().isCType(CType.T_apint)) {
                    pcx.fatalError(name.toExplainString() + "変数" + name.getText() + "は配列です");
                }
                this.setCType(type.getType());
                this.setConstant(type.isConst());
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; Variable starts");
        if (ident != null) {
            ident.codeGen(pcx);
            if (array != null) {
                array.codeGen(pcx);
                o.println("\tMOV\t-(R6), R1\t; Arrayのexpressionの値をR1に格納する");
                o.println("\tMOV\t-(R6), R0\t; 変数アドレスをR0に格納する");
                o.println("\tADD\tR1, R0   \t; 参照するアドレス値を計算");
                o.println("\tMOV\tR0, (R6)+\t; 計算結果をスタックに積む");
            }
        }
        o.println(";;; Variable completes");
    }
}
