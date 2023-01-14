package lang.c.parse.condition;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.exper.Expression;

public class Condition extends CParseRule {
    // condition ::= TRUE | FALSE | expression ( conditionLT | conditionLE |
    // conditionGT | conditionGE | conditionEQ | conditionNE )
    private CParseRule condi;
    private ArrayList<CParseRule> oprArr;

    public Condition(CParseContext pcx) {
        oprArr = new ArrayList<CParseRule>();
    }

    public static boolean isFirst(CToken tk) {
        return ConditionTrue.isFirst(tk) || ConditionFalse.isFirst(tk) || Expression.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        condi = new ConditionOpr(pcx);
        // if (ConditionTrue.isFirst(tk)) {
        //     condi = new ConditionTrue(pcx);
        // } else if (ConditionFalse.isFirst(tk)) {
        //     condi = new ConditionFalse(pcx);
        // } else if (Expression.isFirst(tk)) {
        //     CParseRule exper = new Expression(pcx);
        //     exper.parse(pcx);

        //     tk = ct.getCurrentToken(pcx);
        //     if (ConditionLT.isFirst(tk)) {
        //         condi = new ConditionLT(pcx, exper);
        //     } else if (ConditionLE.isFirst(tk)) {
        //         condi = new ConditionLE(pcx, exper);
        //     } else if (ConditionGT.isFirst(tk)) {
        //         condi = new ConditionGT(pcx, exper);
        //     } else if (ConditionGE.isFirst(tk)) {
        //         condi = new ConditionGE(pcx, exper);
        //     } else if (ConditionEQ.isFirst(tk)) {
        //         condi = new ConditionEQ(pcx, exper);
        //     } else if (ConditionNE.isFirst(tk)) {
        //         condi = new ConditionNE(pcx, exper);
        //     } else {
        //         pcx.fatalError(tk.toExplainString() + "条件演算子error");
        //     }
        // } else {
        //     pcx.fatalError(tk.toExplainString() + "ConditionTrueかConditionFalseかExpressionが必要です");
        // }
        condi.parse(pcx);

        tk = ct.getCurrentToken(pcx);
        // if (ConditionOpr.isFirst(tk)) {
        //     while (true) {
        //         CParseRule opr = new ConditionOpr(pcx);
        //         opr.parse(pcx);
        //         oprArr.add(opr);
        //         tk = ct.getCurrentToken(pcx);
        //     }
        // }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condi != null) {
            condi.semanticCheck(pcx);
            setCType(condi.getCType()); // condi の型をそのままコピー
            setConstant(condi.isConstant()); // condi は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; condition starts");
        if (condi != null) {
            condi.codeGen(pcx);
        }
        o.println(";;; condition completes");
    }
}
