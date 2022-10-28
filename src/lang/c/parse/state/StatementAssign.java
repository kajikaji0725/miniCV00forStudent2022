package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Variable;
import lang.c.parse.exper.Expression;
import lang.c.parse.primary.Primary;

public class StatementAssign extends CParseRule {
    // statementAssign ::= primary ASSIGN expression SEMI
    private CParseRule stateAssign;

    public StatementAssign(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        stateAssign = new Primary(pcx);
        stateAssign.parse(pcx);

        tk = ct.getCurrentToken(pcx);
        if (!tk.getText().equals("=")) {
            pcx.fatalError(tk.toExplainString() + "=が必要です");
        }
        ct.getNextToken(pcx);
        tk = ct.getCurrentToken(pcx);

        if (!Expression.isFirst(tk)) {
            pcx.fatalError(tk.toExplainString() + "=のあとはexperです");
        }
        stateAssign = new Expression(pcx);
        stateAssign.parse(pcx);

        tk = ct.getCurrentToken(pcx);
        if (!tk.getText().equals(";")) {
            pcx.fatalError(tk.toExplainString() + "experのあとは;です");
        }
        System.out.println(tk.getText());
        ct.getNextToken(pcx);

    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (stateAssign != null) {
            stateAssign.semanticCheck(pcx);
            setCType(stateAssign.getCType()); // number の型をそのままコピー
            setConstant(stateAssign.isConstant()); // number は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; AddressToValue starts");
        if (stateAssign != null) {
            stateAssign.codeGen(pcx);
        }
        o.println(";;; AddressToValue completes");
    }
}
