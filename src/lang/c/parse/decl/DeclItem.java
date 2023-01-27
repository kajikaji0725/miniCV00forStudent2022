package lang.c.parse.decl;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Ident;
import lang.c.parse.factor.Factor;

public class DeclItem extends CParseRule {
    // DeclItem ::= [ MULT ] IDENT [ LBRA NUM RBRA ]
    private CToken name;
    private int size;
    private CType type = CType.getCType(CType.T_int);

    public DeclItem(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IDENT || tk.getType() == CToken.TK_MULT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        if (tk.getType() == CToken.TK_MULT) {
            type = CType.getCType(CType.T_pint);
            tk = ct.getNextToken(pcx);
        }

        if (Ident.isFirst(tk)) {
            name = tk;
            tk = ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "IDENTが必要");
        }

        size = 1;
        if (tk.getType() == CToken.TK_LBRA) {
            tk = ct.getNextToken(pcx);
            if (tk.getType() == CToken.TK_NUM) {
                size = Integer.parseInt(tk.getText());
                tk = ct.getNextToken(pcx);
            } else {
                pcx.fatalError(tk.toExplainString() + "配列の要素はNumberのみです");
            }

            if (tk.getType() != CToken.TK_RBRA) {
                pcx.fatalError(tk.toExplainString() + "[]が閉じていません");
            }

            type = CType.getCType(CType.T_aint);
            tk = ct.getNextToken(pcx);
        }

        CSymbolTable table = pcx.getSymbolTable();
        CSymbolTableEntry ret = table.register(name.getText(), type, size, false);
        if (ret != null) {
            pcx.fatalError(name.toExplainString() + "変数名" + name.getText() + "は使用されています");
        }

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; DeclItem starts");
        o.print(name.getText() + ":");
        if (type.equals(CType.getCType(CType.T_aint))) {
            o.println("\t.BLKW\t" + size + "\t;");
        } else {
            o.println("\t.WORD\t0\t;");
        }
        o.println(";;; DeclItem completes");
    }
}
