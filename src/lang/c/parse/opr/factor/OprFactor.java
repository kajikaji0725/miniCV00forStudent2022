package lang.c.parse.opr.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.condition.Condition;
import lang.c.parse.factor.UnsignedFactor;

public class OprFactor extends CParseRule {
	// opsFactor ::= oprExclam condition | condition
	private CParseRule factor, opr;

	public OprFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		// return PlusFactor.isFirst(tk) || MinusFactor.isFirst(tk) ||
		// UnsignedFactor.isFirst(tk);
		return OprExc.isFirst(tk) || Condition.isFirst(tk) || tk.getType() == CToken.TK_AND
				|| tk.getType() == CToken.TK_OR
				|| tk.getType() == CToken.TK_EXCLAM;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		// CToken ck = pcx.getTokenizer().getCurrentToken(pcx);
		// if (PlusFactor.isFirst(ck)) {
		// factor = new PlusFactor(pcx);
		// } else if (MinusFactor.isFirst(ck)) {
		// factor = new MinusFactor(pcx);
		// } else {
		// factor = new UnsignedFactor(pcx);
		// }
		// factor.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		if (OprExc.isFirst(tk)) {
			opr = new OprExc(pcx);
			opr.parse(pcx);
			tk = ct.getCurrentToken(pcx);
			factor = new Condition(pcx);
		} else {
			factor = new Condition(pcx);
		}
		factor.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (factor != null) {
			factor.semanticCheck(pcx);
			setCType(factor.getCType()); // number の型をそのままコピー
			setConstant(factor.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (factor != null) {
			factor.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}
