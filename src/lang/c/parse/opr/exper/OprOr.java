package lang.c.parse.opr.exper;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.opr.factor.OprFactor;
import lang.c.parse.opr.term.OprTerm;
import lang.c.parse.term.Term;

public class OprOr extends CParseRule {
    // oprOr ::= OR oprTarm
    private CToken op;
    private CParseRule left, right;
    private int seq;

    public OprOr(CParseContext pcx, CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_OR;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        op = ct.getCurrentToken(pcx);
        // +の次の字句を読む
        CToken tk = ct.getNextToken(pcx);
        if (OprTerm.isFirst(tk)) {
            right = new OprTerm(pcx);
            right.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "+の後ろはtermです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        left.semanticCheck(pcx);
        right.semanticCheck(pcx);
        if (!right.getCType().equals(left.getCType())) {
            pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "] と右辺の型["
                    + right.getCType().toString() + "] が一致しないので比較できません");
        } else {
            this.setCType(CType.getCType(CType.T_bool));
            this.setConstant(true);
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (left != null && right != null) {
            left.codeGen(pcx); // 左部分木のコード生成を頼む
            right.codeGen(pcx); // 右部分木のコード生成を頼む
            o.println("\tMOV\t-(R6), R0\t; OprOr: OR条件判定 どちらかが真<" + op.getText() + ">");
            o.println("\tMOV\t-(R6), R1\t; OprOr:");
            o.println("\tOR\tR1, R0\t;    OprOr:");
            o.println("\tMOV\tR0, (R6)+\t; OprOr:");
        }
    }
}