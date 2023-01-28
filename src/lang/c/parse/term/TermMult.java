package lang.c.parse.term;

import java.io.PrintStream;

import lang.*;
import lang.c.*;
import lang.c.parse.factor.Factor;

public class TermMult extends CParseRule {
	// termDiv ::= MULT factor
	private CToken op;
	private CParseRule left, right;

	public TermMult(CParseRule left) {
		this.left = left;
	}

	public static boolean isFirst(CToken tk) {
		return tk.getType() == CToken.TK_MULT;
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
		final int s[][] = {
				// T_err T_int T_pint T_aint T_apint
				{ CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err }, // T_err
				{ CType.T_err, CType.T_int, CType.T_err, CType.T_err, CType.T_err, CType.T_err }, // T_int(T_pintはerror)
				{ CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err }, // T_pint Divではerror
				{ CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err }, // T_aint Divではerror
				{ CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err, CType.T_err }, // T_apint Divではerror
		};
		if (left != null && right != null) {
			left.semanticCheck(pcx);
			right.semanticCheck(pcx);
			int lt = left.getCType().getType(); // +の左辺の型
			int rt = right.getCType().getType(); // +の右辺の型
			int nt = s[lt][rt]; // 規則による型計算
			if (nt == CType.T_err) {
				pcx.warning(op.toExplainString() + "左辺の型[" + left.getCType().toString() + "]と右辺の型["
						+ right.getCType().toString() + "]は乗算できません :演算結果の保障はできません");
			}
			this.setCType(CType.getCType(nt));
			this.setConstant(left.isConstant() && right.isConstant()); // +の左右両方が定数のときだけ定数
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; term starts");
		if (left != null && right != null) {
			left.codeGen(pcx);
			right.codeGen(pcx);
			o.println("\tJSR\tMULT\t\t ; サブルーチンMULTを呼ぶ");
			o.println("\tSUB\t#2, R6\t\t ; スタックに積んである引数をおろす");
			o.println("\tMOV\tR0, (R6)+\t ; R0に格納されているMULTの結果をスタックに積む");
		}
		o.println(";;; term completes");
	}
}
