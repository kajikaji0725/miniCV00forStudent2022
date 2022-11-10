package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.Condition;

public class StatementWhile extends CParseRule {
    // statementIF ::= while LPAR condition RPAR StatementBlock
    private CParseRule condi, stateWhile;

    public StatementWhile(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_WHILE;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);

        if (tk.getType() == CToken.TK_LPAR) {
            tk = ct.getNextToken(pcx);
            condi = new Condition(pcx);
            condi.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "LPAR error");
        }
        tk = ct.getCurrentToken(pcx);

        if (tk.getType() == CToken.TK_RPAR) {
            tk = ct.getNextToken(pcx);
            if (StatementBlock.isFirst(tk)) {
                stateWhile = new StatementBlock(pcx);
            } else {
                pcx.fatalError(tk.toExplainString() + "LCUR { が必要");
            }
            stateWhile.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "RPAR error");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condi != null && stateWhile != null) {
            condi.semanticCheck(pcx);
            stateWhile.semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; statementWhile starts");
        if (stateWhile != null && condi != null) {
            int seq = pcx.getSeqId();

            o.println("\tWHILE_BEGIN" + seq + ":");
            condi.codeGen(pcx);

            o.println("\tMOV\t-(R6), R0\t; StatementWhile: conditionの結果をR0に格納");
            o.println("\tBRZ\tWHILE_END" + seq + "\t; StatementWhile: R0の値が0なら偽");

            stateWhile.codeGen(pcx);
            o.println("\tJMP\tWHILE_BEGIN" + seq + "\t; StatementWhile: 強制的にジャンプ");

            o.println("\tWHILE_END" + seq + ":");
        }
        o.println(";;; statementWhile completes");
    }
}
