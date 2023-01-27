package lang.c.parse.decl;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.factor.Factor;

public class DeclItem extends CParseRule {
	// DeclItem ::= [ MULT ] IDENT [ LBRA NUM RBRA ]
	private CParseRule decl;

	public DeclItem(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_IDENT || tk.getType() == CToken.TK_MULT;
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		// if (term != null) {
		// 	term.codeGen(pcx);
		// }
		o.println(";;; term completes");
	}
}
