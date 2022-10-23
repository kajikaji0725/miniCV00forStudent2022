package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Variable;
import lang.c.parse.primary.Primary;

public class AddressToValue extends CParseRule {
    // primary ::= primaryMult | variable
    private CParseRule address;

    public AddressToValue(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return Primary.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CToken ck = pcx.getTokenizer().getCurrentToken(pcx);
        address = new Primary(pcx);
        address.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (address != null) {
            address.semanticCheck(pcx);
            setCType(address.getCType()); // number の型をそのままコピー
            setConstant(address.isConstant()); // number は常に定数
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; AddressToValue starts");
        if (address != null) {
            address.codeGen(pcx);
        }
        o.println(";;; AddressToValue completes");
    }
}
