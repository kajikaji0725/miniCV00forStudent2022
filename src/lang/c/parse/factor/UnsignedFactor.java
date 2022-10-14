package lang.c.parse.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.Number;
import lang.c.parse.exper.Expression;

public class UnsignedFactor extends CParseRule {
	// factor ::= factorAmp | number | LPAR expression RPAR
	private CParseRule lrFactor;

	public UnsignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Number.isFirst(tk) || FactorAmp.isFirst(tk) || CToken.TK_LPAR == tk.getType();
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ck = pcx.getTokenizer();
		CToken tk = ck.getCurrentToken(pcx);

		if (tk.getType() == CToken.TK_LPAR) {
			ck.getNextToken(pcx);
			lrFactor = new Expression(pcx);
			lrFactor.parse(pcx);
			ck.getNextToken(pcx);
		} else {
			if (FactorAmp.isFirst(tk)) {
				lrFactor = new FactorAmp(pcx);
			} else {
				lrFactor = new Number(pcx);
			}
			lrFactor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (lrFactor != null) {
			lrFactor.semanticCheck(pcx);
			setCType(lrFactor.getCType()); // number の型をそのままコピー
			setConstant(lrFactor.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (lrFactor != null) {
			lrFactor.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}