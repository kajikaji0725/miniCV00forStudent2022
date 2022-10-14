package lang.c.parse.exper;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.term.Term;

class ExpressionSub extends CParseRule {
    // expressionSub ::= '-' term
    private CToken op;
    private CParseRule left, right;

    public ExpressionSub(CParseContext pcx, CParseRule left) {
        this.left = left;
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_MINUS;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        op = ct.getCurrentToken(pcx);
        // -の次の字句を読む
        CToken tk = ct.getNextToken(pcx);
        if (Term.isFirst(tk)) {
            right = new Term(pcx);
            right.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "-の後ろはtermです");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        // 引き算の型計算規則
        final int s[][] = {
                // T_err T_int
                { CType.T_err, CType.T_err, CType.T_err }, // T_err
                { CType.T_err, CType.T_int, CType.T_err }, // T_int
                { CType.T_err, CType.T_pint, CType.T_Long }, // c++では、pointer-pointerはlongになっていた
                // c++のtypeidを用いたところ、pointer-pointerの結果のtypeidが"l(エル)"と出力された
        };
        if (left != null && right != null) {
            left.semanticCheck(pcx);
            right.semanticCheck(pcx);
            int lt = left.getCType().getType(); // -の左辺の型
            int rt = right.getCType().getType(); // -の右辺の型
            int nt = s[lt][rt]; // 規則による型計算
            if (nt == CType.T_err) {
                pcx.fatalError(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
                        + right.getCType().toString() + "]は減算できません");
            }
            this.setCType(CType.getCType(nt));
            this.setConstant(left.isConstant() && right.isConstant()); // -の左右両方が定数のときだけ定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (left != null && right != null) {
            left.codeGen(pcx); // 左部分木のコード生成を頼む
            right.codeGen(pcx); // 右部分木のコード生成を頼む
            o.println("\tMOV\t-(R6), R0\t; ExpressionSub: ２数を取り出して、減算し、積む<" + op.toString() + ">");
            o.println("\tMOV\t-(R6), R1\t; ExpressionSub:");
            o.println("\tSUB\tR0, R1\t\t; ExpressionSub:"); // 前回計算した数、または左部分の数はR1に入っているため、R1からR0を引き算した数をR1に格納する。
            o.println("\tMOV\tR0, (R6)+\t; ExpressionSub:");
        }
    }
}