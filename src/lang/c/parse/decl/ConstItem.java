package lang.c.parse.decl;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Ident;
import lang.c.parse.factor.Factor;

public class ConstItem extends CParseRule {
    // ConstItem ::= [ MULT ] IDENT ASSIGN [ AMP ] NUM
    private CSymbolTableEntry entry;
    private CToken name;
    private CType mulType = CType.getCType(CType.T_int);
    private CType ampType = CType.getCType(CType.T_int);
    private int value;

    public ConstItem(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IDENT || tk.getType() == CToken.TK_MULT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        if (tk.getType() == CToken.TK_MULT) {
            mulType = CType.getCType(CType.T_pint);
            tk = ct.getNextToken(pcx);
        }

        if (Ident.isFirst(tk)) {
            name = tk;
            tk = ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "IDENTが必要");
        }

        if (tk.getType() != CToken.TK_ASSIGN) {
            pcx.fatalError(tk.toExplainString() + "=が必要");
        }
        tk = ct.getNextToken(pcx);

        if (tk.getType() == CToken.TK_AMP) {
            ampType = CType.getCType(CType.T_pint);
            tk = ct.getNextToken(pcx);
        }

        int size = 1;
        if (tk.getType() == CToken.TK_NUM) {
            value = Integer.parseInt(tk.getText());
            tk = ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "int型の値はNumberのみです");
        }

        CSymbolTable table = pcx.getSymbolTable();
        CSymbolTableEntry ret = table.register(name.getText(), mulType, size, true);
        if (ret != null) {
            pcx.fatalError(name.toExplainString() + "変数名" + name.getText() + "は使用されています");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (!mulType.equals(ampType)) {
            pcx.fatalError(name.getText() + "で宣言された型と、代入した値" + value + "の型が合いません");
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; ConstItem starts");
        o.print(name.getText() + ":");
        o.println("\t.WORD\t" + value + "\t;");
        o.println(";;; ConstItem completes");
    }
}
