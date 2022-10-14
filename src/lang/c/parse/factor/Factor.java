package lang.c.parse.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// factor ::= plusFactor | minusFactor | unsignedFactor
	private CParseRule factor;

	public Factor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return PlusFactor.isFirst(tk) || MinusFactor.isFirst(tk) || UnsignedFactor.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CToken ck = pcx.getTokenizer().getCurrentToken(pcx);
		if (PlusFactor.isFirst(ck)) {
			factor = new PlusFactor(pcx);
		} else if (MinusFactor.isFirst(ck)) {
			factor = new MinusFactor(pcx);
		} else {
			factor = new UnsignedFactor(pcx);
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
