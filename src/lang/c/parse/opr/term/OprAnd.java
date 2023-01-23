package lang.c.parse.opr.term;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.opr.factor.OprFactor;

public class OprAnd extends CParseRule {
    // OprAnd ::= AND OprFactor
    private CToken op;
    private CParseRule left, right;
    private int seq;

    public OprAnd(CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_AND;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        op = ct.getCurrentToken(pcx);
        CToken tk = ct.getNextToken(pcx);
        if (OprFactor.isFirst(tk)) {
            right = new OprFactor(pcx);
            right.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "/の後ろはfactorです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            int rt = right.getCType().getType(); // +の右辺の型
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
        o.println(";;; OprAnd starts");
        if (left != null && right != null) {
            left.codeGen(pcx);
            right.codeGen(pcx);
            o.println("\tMOV\t-(R6), R0\t; OprAnd: AND条件判定 両方真<" + op.getText() + ">");
            o.println("\tMOV\t-(R6), R1\t; OprAnd:");
            o.println("\tAND\tR1, R0\t;    OprAnd:");
            o.println("\tMOV\tR0, (R6)+\t; OprAnd:");
        }
        o.println(";;; OprAnd completes");
    }
}
