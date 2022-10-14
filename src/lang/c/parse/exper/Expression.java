package lang.c.parse.exper;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.term.Term;

public class Expression extends CParseRule {
	// expression ::= term { expressionAdd | expressionSub }
	private CParseRule expression;

	public Expression(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Term.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CParseRule term = null, list = null;
		term = new Term(pcx);
		term.parse(pcx);
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (true) {
			if (ExpressionAdd.isFirst(tk)) {
				list = new ExpressionAdd(pcx, term);
			} else if (ExpressionSub.isFirst(tk)) {
				list = new ExpressionSub(pcx, term);
			} else if (tk.getText().isEmpty()) {
				tk = ct.getNextToken(pcx);
				System.out.println(tk.getText());
				continue;
			} else {
				break;
			}
			list.parse(pcx);
			term = list;
			tk = ct.getCurrentToken(pcx);
		}
		expression = term;
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		if (expression != null) {
			expression.semanticCheck(pcx);
			this.setCType(expression.getCType()); // expression の型をそのままコピー
			this.setConstant(expression.isConstant());
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; expression starts");
		if (expression != null)
			expression.codeGen(pcx);
		o.println(";;; expression completes");
	}
}
