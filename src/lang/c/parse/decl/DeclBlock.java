package lang.c.parse.decl;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.factor.Factor;
import lang.c.parse.state.Statement;

public class DeclBlock extends CParseRule {
    // declBlock ::= LCUR { declaration } { statement } RCUR
    private CParseRule decl;
    private ArrayList<CParseRule> declarations;
    private ArrayList<CParseRule> states;
    private int address;

    public DeclBlock(CParseContext pcx) {
        declarations = new ArrayList<CParseRule>();
        states = new ArrayList<CParseRule>();
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        CSymbolTable table = pcx.getSymbolTable();

        tk = ct.getNextToken(pcx);
        table.setLocal();
        while (Declaration.isFirst(tk)) {
            decl = new Declaration(pcx);
            decl.parse(pcx);
            declarations.add(decl);
            tk = ct.getCurrentToken(pcx);
        }

        address = table.getAddress();

        while (Statement.isFirst(tk)) {
            decl = new Statement(pcx);
            decl.parse(pcx);
            states.add(decl);
            tk = ct.getCurrentToken(pcx);
        }

        if (tk.getType() != CToken.TK_RCUR) {
            pcx.fatalError(tk.toExplainString() + "{ }が閉じていません");
        }

        tk = ct.getNextToken(pcx);
        table.setGlobal();
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        for (CParseRule declaration : declarations) {
            declaration.semanticCheck(pcx);
        }
        for (CParseRule state : states) {
            state.semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; DeclBlock starts");
        // 局所変数がなければスタック領域の確保はいらない
        if (address != 0) {
            o.println("\tMOV\t R4,(R6)+\t; DeclBlock: 旧フレームポインタの退避");
            o.println("\tMOV\t R6,R4\t; DeclBlock: 新フレームポインタのセット");
            o.println("\tADD\t#" + address + ",R6\t; DeclBlock: 領域の確保");
        }
        for (CParseRule declaration : declarations) {
            declaration.codeGen(pcx);
        }
        for (CParseRule state : states) {
            state.codeGen(pcx);
        }
        if (address != 0) {
            o.println("\tSUB\t#" + address + ",R6\t; DeclBlock: 領域の解放");
            o.println("\tMOV\t -(R6),R4\t; DeclBlock: フレームポインタを復帰");
        }
        o.println(";;; DeclBlock completes");
    }
}
