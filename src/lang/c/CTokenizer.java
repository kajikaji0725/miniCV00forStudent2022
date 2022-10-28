package lang.c;

// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.Set;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;
import lang.c.CState;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule rule;
	private int lineNo, colNo, lrCount, lrbcount; // lrCountは()の数が合うかどうか判定する。
	private char backCh;
	private boolean backChExist = false;

	public CTokenizer(CTokenRule rule) {
		this.rule = rule;
		lineNo = 1;
		colNo = 1;
	}

	private InputStream in;
	private PrintStream err;

	private char readChar() {
		char ch;
		if (backChExist) {
			ch = backCh;
			backChExist = false;
		} else {
			try {
				ch = (char) in.read();
			} catch (IOException e) {
				e.printStackTrace(err);
				ch = (char) -1;
			}
		}
		++colNo;
		if (ch == '\n') {
			colNo = 1;
			++lineNo;
		}
		// System.out.print("'"+ch+"'("+(int)ch+")");
		return ch;
	}

	private void backChar(char c) {
		backCh = c;
		backChExist = true;
		--colNo;
		if (c == '\n') {
			--lineNo;
		}
	}

	// 現在読み込まれているトークンを返す
	private CToken currentTk = null;

	public CToken getCurrentToken(CParseContext pctx) {
		return currentTk;
	}

	// 次のトークンを読んで返す
	public CToken getNextToken(CParseContext pctx) {
		in = pctx.getIOContext().getInStream();
		err = pctx.getIOContext().getErrStream();
		currentTk = readToken();
		// System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}

	public boolean isIndent(char ch) {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z');
	}

	private CToken readToken() {
		CToken tk = null;
		char ch;
		int startCol = colNo;

		StringBuffer text = new StringBuffer();

		// Character[] useOpe = { '+', '-', '/', '*', '(', ')' }; // 使用する演算子

		int state = 0;
		boolean accept = false;
		Boolean errFlag = false;
		// Set<Character> errChar = new HashSet<Character>();
		while (!accept) {
			switch (state) {
				case 0: // 初期状態
					ch = readChar();
					if (ch == '/') {
						state = CState.S_COMMENT;
					} else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == (char) -1) { // EOF
						startCol = colNo - 1;
						state = CState.S_EOF;
					} else if (ch >= '0' && ch <= '9') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_DEC;
						if (text.length() == 1) {
							if (ch == '0') {
								state = CState.S_OCT;
							}
							char xChar = readChar();
							if (xChar == 'x') {
								text.append(xChar);
								state = CState.S_HEX;
							} else {
								backChar(xChar);
							}
						}
					} else if (ch == '+') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_PLUS;
					} else if (ch == '-') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_MINUS;
					} else if (ch == '*') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_MULT;
					} else if (ch == '&') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_AMP;
					} else if (ch == '(') {
						startCol = colNo - 1;
						text.append(ch);
						lrCount++;
						state = CState.S_LPAR;
					} else if (ch == ')') {
						startCol = colNo - 1;
						text.append(ch);
						lrCount--;
						state = CState.S_RPAR;
					} else if (ch == '[') {
						startCol = colNo - 1;
						text.append(ch);
						lrbcount++;
						state = CState.S_LBRA;
					} else if (ch == ']') {
						startCol = colNo - 1;
						text.append(ch);
						lrbcount--;
						state = CState.S_RBRA;
					} else if (isIndent(ch) || ch == '_') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_IDENT;
					} else if (ch == '=') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_ASSIGN;
					} else if (ch == ';') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_SEMI;
					} else { // ヘンな文字を読んだ
						startCol = colNo - 1;
						text.append(ch);
						state = CState.S_ERR;
					}
					break;
				case CState.S_EOF: // EOFを読んだ
					if (lrCount != 0) {
						System.err.println("'(' と　')'の数が合いません");
						state = CState.S_ERR;
					}
					if (lrbcount != 0) {
						System.err.println("'[' と ']'の数が合いません");
						state = CState.S_ERR;
					}
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case CState.S_ERR: // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case CState.S_DEC: // 数（10進数）の開始
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
					} else {
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする)
						if (text.charAt(0) != '&'
								&& Integer.valueOf(text.toString()) > (int) Math.pow(2, 15) - 1) {
							System.err.println("16ビット符号付整数内に収めてください");
							state = CState.S_ERR;
							break;
						}
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case CState.S_PLUS: // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case CState.S_MINUS: // -を読んだ
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case CState.S_DIV: // /を読んだ
					tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
					accept = true;
					break;
				case CState.S_MULT: // *を読んだ
					tk = new CToken(CToken.TK_MULT, lineNo, startCol, "*");
					accept = true;
					break;
				case CState.S_COMMENT: // コメントアウト /* */ //
					boolean commentFlag = false;
					ch = readChar();
					if (ch == '*') { // コメントアウト/* */
						while (!commentFlag) {
							ch = readChar();
							if (ch == '*') {
								ch = readChar();
								if (ch == '/') {
									commentFlag = true;
									state = 0;
								} else {
									state = 0;
									backChar(ch);
								}
							}
							if (ch == (char) -1) {
								System.err.println("コメントアウトを閉じずに終了");
								state = CState.S_EOF;
								commentFlag = true;
							}
						}
					} else if (ch == '/') { // コメントアウト//
						while (!commentFlag) {
							ch = readChar();
							if (ch == '\n') {
								commentFlag = true;
								state = 0;
								backChar(ch);
							}
							if (ch == (char) -1) {
								commentFlag = true;
								state = CState.S_EOF;
							}
						}
					} else {
						backChar(ch);
						state = CState.S_DIV;
					}
					break;
				case CState.S_OCT: // 8進数計算
					ch = readChar();
					if (Character.isDigit(ch)) {
						if (ch > '7') {
							errFlag = true;
						}
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする)
						if (errFlag) {
							System.err.println("8進数の記法に誤りがありがあります");
							state = CState.S_ERR;
							break;
						}
						if (text.length() > 7
								|| (text.length() == 7 && (text.charAt(1) >= '2' && text.charAt(1) <= '7'))) {
							System.err.println("16ビット内に収めてください");
							state = CState.S_ERR;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case CState.S_HEX: // 16進数計算
					ch = readChar();
					if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f')) {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする)
						if (text.length() > 6) {
							System.err.println("16ビット内に収めてください");
							state = CState.S_ERR;
						} else if (text.length() < 3) {
							System.err.println("16進数の書き方にエラーがあります");
							state = CState.S_ERR;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case CState.S_LPAR: // (
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
					accept = true;
					break;
				case CState.S_RPAR: // )
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
					accept = true;
					break;
				case CState.S_AMP: // &
					tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
					accept = true;
					break;
				case CState.S_LBRA: // [
					tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
					accept = true;
					break;
				case CState.S_RBRA: // ]
					tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
					accept = true;
					break;
				case CState.S_IDENT: // ident
					ch = readChar();
					if (isIndent(ch) || Character.isDigit(ch) || ch == '_') {
						text.append(ch);
					} else {
						backChar(ch); // 変数名ではない文字は戻す（読まなかったことにする)
						tk = new CToken(CToken.TK_IDENT, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case CState.S_ASSIGN: // =
					tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, "=");
					accept = true;
					break;
				case CState.S_SEMI: // ;
					tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
					accept = true;
					break;
			}
		}
		return tk;
	}
}
