package lang.c.parse.state;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.primary.Primary;

public class StatementInput extends CParseRule {
    // statementInput ::= INPUT primary SEMI
    private CParseRule primary;

    public StatementInput(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_INPUT;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);

        if (Primary.isFirst(tk)) {
            primary = new Primary(pcx);
            primary.parse(pcx);
        } else {
            pcx.fatalError(tk.toExplainString() + "StatementInput error");
        }

        tk = ct.getCurrentToken(pcx);
        if (tk.getType() == CToken.TK_SEMI) {
            tk = ct.getNextToken(pcx);
        } else {
            pcx.warning(tk.toExplainString() + "セミコロンが必要 \n 「;」を補いました");
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (primary != null) {
            primary.semanticCheck(pcx);
            if (primary.isConstant()) {
                pcx.fatalError("定数に入力はできません");
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; statementInput starts");
        if (primary != null) {
            primary.codeGen(pcx);

            o.println("\tMOV\t#0xFFE0, R0\t; statementInput: R0に入力用アドレスを格納");
            o.println("\tMOV\t-(R6), R1\t; statementInput: R1に変数アドレスを格納");
            o.println("\tMOV\t(R0), (R1)\t; statementInput: input値をR1のアドレス先に格納");
        }
        o.println(";;; statementInput completes");
    }
}
