package lang.c.parse.condition;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.exper.Expression;

public class ConditionEQ extends CParseRule {
    // conditionEQ ::= EQ expression
    private CParseRule right, left;
    private CToken op;
    private int seq;

    public ConditionEQ(CParseContext pcx, CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_EQ;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);
        tk = ct.getCurrentToken(pcx);
        op = tk;
        right = new Expression(pcx);
        right.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (right != null) {
            right.semanticCheck(pcx);
            left.semanticCheck(pcx);

            if (!right.getCType().equals(left.getCType())) {
                pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "] と右辺の型["
                        + right.getCType().toString() + "] が一致しないので比較できません");
            } else {
                this.setCType(CType.getCType(CType.T_bool));
                this.setConstant(true);
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition == (compare) starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            seq = pcx.getSeqId();
            o.println("\tMOV\t-(R6), R0\t; conditionEQ: ２数を取り出して、比べる");
            o.println("\tMOV\t-(R6), R1\t; conditionEQ:");
            o.println("\tMOV\t#0x0001, R2\t; conditionEQ: set true");
            o.println("\tCMP\tR0, R1\t; conditionEQ: R1==R0 -> R1-R0==0");
            o.println("\tBRZ\tEQ" + seq + "\t; conditionEQ");
            o.println("\tCLR\tR2\t\t; conditionEQ: set false");
            o.println("EQ" + seq + ":\tMOV\tR2, (R6)+\t; conditionEQ:");
        }
        o.println(";;; condition == (compare) completes");
    }
}
