package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Factor extends CParseRule {
	// factor ::= number
	private CParseRule number;

	public Factor(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Number.isFirst(tk) | FactorAmp.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		if (FactorAmp.isFirst(pcx.getTokenizer().getCurrentToken(pcx))) {
			number = new FactorAmp(pcx);
		} else {
			number = new Number(pcx);
		}
		number.parse(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (number != null) {
			number.semanticCheck(pcx);
			setCType(number.getCType()); // number の型をそのままコピー
			setConstant(number.isConstant()); // number は常に定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; factor starts");
		if (number != null) {
			number.codeGen(pcx);
		}
		o.println(";;; factor completes");
	}
}

class FactorAmp extends CParseRule {
	// number ::= AMP NUM
	private CToken num;
	private CParseRule factAmp;

	public FactorAmp(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_AMP;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		num = tk;
		tk = ct.getNextToken(pcx);

		if (Number.isFirst(tk)) {
			CParseRule list = null;
			list = new Number(pcx);
			list.parse(pcx);

			factAmp = list;
		} else {
			pcx.fatalError("&のエラーです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		this.setCType(CType.getCType(CType.T_pint));
		this.setConstant(true);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; number starts");
		if (factAmp != null) {
			factAmp.codeGen(pcx);
		}
		o.println(";;; number completes");
	}
}