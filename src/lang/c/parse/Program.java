package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.decl.Declaration;
import lang.c.parse.state.Statement;

public class Program extends CParseRule {
    // program ::= {declaration} { statement } EOF
    private CParseRule program;
    private ArrayList<CParseRule> declarations;
    private ArrayList<CParseRule> states;

    public Program(CParseContext pcx) {
        states = new ArrayList<CParseRule>();
        declarations = new ArrayList<CParseRule>();
    }

    public static boolean isFirst(CToken tk) {
        return Declaration.isFirst(tk) || Statement.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        while (Declaration.isFirst(tk)) {
            program = new Declaration(pcx);
            program.parse(pcx);
            declarations.add(program);
            tk = ct.getCurrentToken(pcx);
        }
        while (Statement.isFirst(tk)) {
            program = new Statement(pcx);
            program.parse(pcx);
            states.add(program);
            tk = ct.getCurrentToken(pcx);
        }
        if (tk.getType() != CToken.TK_EOF) {
            pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        for (CParseRule state : states) {
            state.semanticCheck(pcx);
        }
        for (CParseRule declaration : declarations) {
            declaration.semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; program starts");
        for (CParseRule declaration : declarations) {
            declaration.codeGen(pcx);
        }
        o.println("\t. = 0x100");
        o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
        // ここには将来、宣言に対するコード生成が必要
        if (program != null) {
            o.println("__START:");
            o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
            for (CParseRule parseState : states) {
                parseState.codeGen(pcx);
            }
        }
        o.println("\tHLT\t\t\t; ProgramNode:");
        o.println("\t.END\t\t\t; ProgramNode:");
        o.println(";;; program completes");
    }
}
