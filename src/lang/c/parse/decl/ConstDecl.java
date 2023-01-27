package lang.c.parse.decl;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.factor.Factor;

public class ConstDecl extends CParseRule {
	// ConstDecl ::= CONST INT ConstItem { COMMA ConstItem } SEMI
	private CParseRule decl;

	public ConstDecl(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_CONST;
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
