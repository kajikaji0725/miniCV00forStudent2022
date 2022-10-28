package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Variable;
import lang.c.parse.primary.Primary;

public class Statement extends CParseRule {
    // statement ::= statementAssign
    private CParseRule state;

    public Statement(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return StatementAssign.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CToken ck = pcx.getTokenizer().getCurrentToken(pcx);
        state = new StatementAssign(pcx);
        state.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        // if (address != null) {
        //     address.semanticCheck(pcx);
        //     setCType(address.getCType()); // number の型をそのままコピー
        //     setConstant(address.isConstant()); // number は常に定数
        // }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; state starts");
        if (state != null) {
            //state.codeGen(pcx);

        }
        o.println(";;; state completes");
    }
}
