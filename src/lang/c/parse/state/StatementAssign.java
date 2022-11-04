package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.exper.Expression;
import lang.c.parse.primary.Primary;
import lang.c.parse.primary.PrimaryMult;

public class StatementAssign extends CParseRule {
    // statementAssign ::= primary ASSIGN expression SEMI
    private CParseRule primary,exper;
    private CToken token;

    public StatementAssign(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        primary = new Primary(pcx);
        primary.parse(pcx);
        token = tk;
        tk = ct.getCurrentToken(pcx);
        if (!tk.getText().equals("=")) {
            pcx.fatalError(tk.toExplainString() + "=が必要です");
        }
        ct.getNextToken(pcx);
        tk = ct.getCurrentToken(pcx);

        if (!Expression.isFirst(tk)) {
            pcx.fatalError(tk.toExplainString() + "=のあとはexperです");
        }
        exper = new Expression(pcx);
        exper.parse(pcx);

        tk = ct.getCurrentToken(pcx);
        if (!tk.getText().equals(";")) {
            pcx.fatalError(tk.toExplainString() + "experのあとは;です");
        }

        ct.getNextToken(pcx);

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null && exper != null) {
            primary.semanticCheck(pcx);
            exper.semanticCheck(pcx);

            final int s[][] = {
                    // T_err T_int
                    { CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err }, // T_err
                    { CType.T_err, CType.T_int, CType.T_err, CType.T_err, CType.T_err }, // T_int,T_pint
                    { CType.T_err, CType.T_err, CType.T_pint, CType.T_err, CType.T_err }, // T_pint
                    { CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err },
                    { CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err },
            };

            int primType = primary.getCType().getType();
            if (primary instanceof PrimaryMult) {
                System.out.println("hoge");
                if (primary.getCType().isCType(CType.T_pint)) {
                    primType = CType.T_int;
                }
                if (primary.getCType().isCType(CType.T_apint)) {
                    primType = CType.T_pint;
                }
            }
            int experType = exper.getCType().getType();

            int nt = s[primType][experType];

            if (primary.isConstant()) {
                pcx.fatalError(token.toExplainString() + "定数に代入できません");
            }
            if (nt == CType.T_err) {
                pcx.fatalError("左辺の型[" + primary.getCType().toString() + "]に右辺の型["
                        + exper.getCType().toString() + "]は代入できません");
            }

            setCType(primary.getCType()); // number の型をそのままコピー
            setConstant(primary.isConstant()); // number は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; StatementAssign starts");
        if (primary != null && exper != null) {
            primary.codeGen(pcx);
            exper.codeGen(pcx);
        }
        o.println(";;; StatementAssign completes");
    }
}
