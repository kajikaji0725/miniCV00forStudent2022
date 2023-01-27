package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Statement extends CParseRule {
    // statement ::= statementAssign | statementIF | statementWHILE | statementINPUT
    // | statementOUTPUT
    private CParseRule state;

    public Statement(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return StatementAssign.isFirst(tk) || StatementIF.isFirst(tk) || StatementWhile.isFirst(tk)
                || StatementInput.isFirst(tk) || StatementOutput.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);

        if (StatementAssign.isFirst(tk)) {
            state = new StatementAssign(pcx);
        } else if (StatementIF.isFirst(tk)) {
            state = new StatementIF(pcx);
        } else if (StatementWhile.isFirst(tk)) {
            state = new StatementWhile(pcx);
        } else if (StatementInput.isFirst(tk)) {
            state = new StatementInput(pcx);
        } else if (StatementOutput.isFirst(tk)) {
            state = new StatementOutput(pcx);
        } else {
            pcx.fatalError("statement error");
        }

        state.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (state != null) {
            state.semanticCheck(pcx);
            // setCType(state.getCType()); // number の型をそのままコピー
            // setConstant(state.isConstant()); // number は常に定数
        }

    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; statement starts");
        if (state != null) {
            state.codeGen(pcx);
        }
        o.println(";;; statement completes");
    }
}
