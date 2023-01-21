package lang.c.parse.opr.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

import lang.c.parse.factor.*;
import lang.c.parse.condition.*;

public class OprExc extends CParseRule {
    // oprExclam ::= EXCLAM Condition
    private CParseRule condi;
    private CToken op;

    public OprExc(CParseContext pcx) {
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_EXCLAM;
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        // num = tk;
        op = ct.getCurrentToken(pcx);
        tk = ct.getNextToken(pcx);

        if (Condition.isFirst(tk)) {
            condi = new Condition(pcx);
        } else {
            // pcx.fatalError("MINUSのあとはnumber、Amp、LPARです");
        }
        condi.parse(pcx);
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (condi != null) {
            condi.semanticCheck(pcx);
            if (condi.getCType().getType() == CType.getCType(CType.T_bool).getType()) {
                this.setCType(condi.getCType());
                this.setConstant(condi.isConstant());
            } else {
                pcx.fatalError("条件演算子[" + op.getText() + "]の後ろはboolです");
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        if (condi != null) {
            condi.codeGen(pcx);
            o.println("\tMOV\t-(R6), R0\t; OprExc:  反転する<" + op.getText() + ">");
            o.println("\tXOR\t#0x0001, R0\t; OprExc:");
            o.println("\tMOV\tR0, (R6)+\t; OprExc:");
        }
    }
}