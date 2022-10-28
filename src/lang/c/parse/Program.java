package lang.c.parse;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;
import lang.c.parse.exper.Expression;
import lang.c.parse.state.Statement;

public class Program extends CParseRule {
	// program ::= expression EOF
	private CParseRule program;
	private ArrayList<String> state = new ArrayList<String>();

	public Program(CParseContext pcx) {
	}

	public static boolean isFirst(CToken tk) {
		return Statement.isFirst(tk);
	}

	public void parse(CParseContext pcx) throws FatalErrorException {
		// ここにやってくるときは、必ずisFirst()が満たされている
		CTokenizer ct = pcx.getTokenizer();
		CToken tk = ct.getCurrentToken(pcx);
		while (true) {
			if (Statement.isFirst(tk)) {
				program = new Statement(pcx);
			} else {
				break;
			}
			program.parse(pcx);
			System.out.println(tk.getText());
			tk = ct.getCurrentToken(pcx);
			state.add(tk.getText());

			// ct.getNextToken(pcx);
		}
		// state.add(tk.getText());
		if (tk.getType() != CToken.TK_EOF) {
			pcx.fatalError(tk.toExplainString() + "プログラムの最後にゴミがあります");
		}
	}

	public void semanticCheck(CParseContext pcx) throws FatalErrorException {
		// System.out.println(state);
		if (program != null) {
			program.semanticCheck(pcx);
		}
	}

	public void codeGen(CParseContext pcx) throws FatalErrorException {
		PrintStream o = pcx.getIOContext().getOutStream();
		o.println(";;; program starts");
		o.println("\t. = 0x100");
		o.println("\tJMP\t__START\t; ProgramNode: 最初の実行文へ");
		// ここには将来、宣言に対するコード生成が必要
		if (program != null) {
			o.println("__START:");
			o.println("\tMOV\t#0x1000, R6\t; ProgramNode: 計算用スタック初期化");
			program.codeGen(pcx);
			o.println("\tMOV\t-(R6), R0\t; ProgramNode: 計算結果確認用");
		}
		o.println("\tHLT\t\t\t; ProgramNode:");
		o.println("\t.END\t\t\t; ProgramNode:");
		o.println(";;; program completes");
	}
}
