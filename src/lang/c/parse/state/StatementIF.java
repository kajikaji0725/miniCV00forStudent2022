package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.Condition;

public class StatementIF extends CParseRule {
    // statementIF ::= IF LPAR StateCondition RPAR StatementBlock [ else
    // StatementBlock ]
    private CParseRule condi, stateIF, stateElse;

    public StatementIF(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_IF;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);

        if (tk.getType() == CToken.TK_LPAR) {
            tk = ct.getNextToken(pcx);
            condi = new StateCondition(pcx);
            condi.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "LPAR error");
        }
        tk = ct.getCurrentToken(pcx);

        if (tk.getType() == CToken.TK_RPAR) {
            tk = ct.getNextToken(pcx);
            if (StatementBlock.isFirst(tk)) {
                stateIF = new StatementBlock(pcx);
            } else {
                pcx.fatalError(tk.toExplainString() + "LCUR { が必要");
            }
            stateIF.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "RPAR error");
        }

        tk = ct.getCurrentToken(pcx);
        if (tk.getType() == CToken.TK_ELSE) {
            tk = ct.getNextToken(pcx);
            if (StatementBlock.isFirst(tk)) {
                stateElse = new StatementBlock(pcx);
            } else {
                pcx.fatalError(tk.toExplainString() + "LCUR { が必要");
            }
            stateElse.parse(pcx);
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condi != null && stateIF != null) {
            condi.semanticCheck(pcx);
            stateIF.semanticCheck(pcx);
        }
        if (stateElse != null) {
            stateElse.semanticCheck(pcx);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; statementIF starts");
        if (stateIF != null && condi != null) {
            int seq = pcx.getSeqId();
            condi.codeGen(pcx);

            o.println("\tMOV\t-(R6), R0\t; StatementIF: conditionの結果をR0に格納");
            o.println("\tBRZ\tELSE" + seq + "\t; StatementIF: R0の値が0なら偽");

            stateIF.codeGen(pcx);
            o.println("\tJMP\tIF_END" + seq + "\t; StatementIF: 強制的にジャンプ");

            if (stateElse != null) {
                o.println(";;; statementElse starts");
                o.println("\tELSE" + seq + ":");
                stateElse.codeGen(pcx);
                o.println(";;; statementElse completes");
            }
            o.println("\tIF_END" + seq + ":");
        }
        o.println(";;; statementIF completes");
    }
}
