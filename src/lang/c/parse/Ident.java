package lang.c.parse;

import java.io.PrintStream;

import lang.*;
import lang.c.*;

public class Ident extends CParseRule {
	// ident ::= Ident
	private CToken ident;

	public Ident(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		ident = tk;
		tk = ct.getNextToken(pcx);
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		this.setCType(CType.getCType(SimpleToken.TK_IDENT));
		this.setConstant(true);
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; number starts");
		if (ident != null) {
			// o.println("\tMOV\t#" + num.getText() + ", (R6)+\t\t; Number: 数を積む<" +
			// num.toExplainString() + ">");
			o.println("ident hoge");
		}
		o.println(";;; number completes");
	}
}
