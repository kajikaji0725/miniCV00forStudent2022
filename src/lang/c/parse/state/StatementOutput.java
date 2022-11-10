package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.Condition;
import lang.c.parse.exper.Expression;
import lang.c.parse.primary.Primary;

public class StatementOutput extends CParseRule {
    // statemenOutput ::= Output Expression SEMI
    // 出力は計算したものをそのまま出力する可能性がある
    // また、アドレスも出力する可能性がある
    private CParseRule primary;

    public StatementOutput(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_OUTPUT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);

        if (Expression.isFirst(tk)) {
            primary = new Expression(pcx);
            primary.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "StatementOutput error");
        }

        tk = ct.getCurrentToken(pcx);
        if (tk.getType() == CToken.TK_SEMI) {
            tk = ct.getNextToken(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "セミコロンが必要");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null) {
            primary.semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; statementOutput starts");
        if (primary != null) {
            primary.codeGen(pcx);

            o.println("\tMOV\t#0xFFE0, R0\t; statementInput: R0に出力用アドレスを格納");
            o.println("\tMOV\t-(R6), R1\t; statementInput: R1に変数アドレスを格納");
            o.println("\tMOV\tR1, (R0)\t; statementInput: R1の値を出力");
        }
        o.println(";;; statementOutput completes");
    }
}
