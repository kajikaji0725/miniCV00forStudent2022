package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class TermDiv extends CParseRule {
	// termDiv ::= DIV factor
	private CParseRule factor, left, right;
	private CToken op;

	public TermDiv(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_DIV;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		// factor = new Factor(pcx);
		// factor.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		op = ct.getCurrentToken(pcx);

		CToken tk = ct.getNextToken(pcx);
		if (Factor.isFirst(tk)) {
			right = new Factor(pcx);
			right.parse(pcx);
		} else {
			pcx.fatalError(tk.toExplainString() + "/の後ろはfactorです");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			this.setCType(factor.getCType()); // factor の型をそのままコピー
			this.setConstant(factor.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			o.println("");
            o.println("\tMOV\t-(R6), R1\t; TermDiv:");
            o.println("\tDIV\tR1, R0\t; TermDiv:");
            o.println("\tMOV\tR0, (R6)+\t; TermDiv:");
		}
		o.println(";;; term completes");
	}
}
