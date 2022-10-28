package lang.c.parse.factor;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.AddressToValue;
import lang.c.parse.Number;
import lang.c.parse.exper.Expression;
import lang.c.parse.primary.Primary;

public class UnsignedFactor extends CParseRule {
	// factor ::= factorAmp | number | LPAR expression RPAR
	private CParseRule unFactor;

	public UnsignedFactor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Number.isFirst(tk) || FactorAmp.isFirst(tk) || CToken.TK_LPAR == tk.getType() || AddressToValue.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ck = pcx.getTokenizer();
		CToken tk = ck.getCurrentToken(pcx);

		

		if (tk.getType() == CToken.TK_LPAR) {
			ck.getNextToken(pcx);
			unFactor = new Expression(pcx);
			unFactor.parse(pcx);
			tk = ck.getCurrentToken(pcx);
			if (tk.getType() != CToken.TK_RPAR) {
				pcx.fatalError(tk.toExplainString() + ")で終わっていない");
			} else {
				ck.getNextToken(pcx);
			}
			// System.out.println(ck.getCurrentToken(pcx).getText());

		} else {
			if (FactorAmp.isFirst(tk)) {
				unFactor = new FactorAmp(pcx);
			} else if(Number.isFirst(tk)){
				unFactor = new Number(pcx);
			} else {
				unFactor = new AddressToValue(pcx);
			}
			unFactor.parse(pcx);
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (unFactor != null) {
			unFactor.semanticCheck(pcx);
			setCType(unFactor.getCType()); // number の型をそのままコピー
			setConstant(unFactor.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; UnsignedFactor starts");
		if (unFactor != null) {
			unFactor.codeGen(pcx);
		}
		o.println(";;; UnsignedFactor completes");
	}
}