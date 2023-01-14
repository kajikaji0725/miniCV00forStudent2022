package lang.c;

// import java.util.Arrays;
// import java.util.HashSet;
// import java.util.Set;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;
import lang.c.CState.State;

public class CTokenizer extends Tokenizer<CToken, CParseContext> {
	@SuppressWarnings("unused")
	private CTokenRule rule;
	private int lineNo, colNo;
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
		CState.State state = CState.State.S_CLR;
		boolean accept = false;
		Boolean errFlag = false;
		while (!accept) {
			switch (state) {
				case S_CLR: // 初期状態
					ch = readChar();
					if (ch == '/') {
						state = CState.State.S_COMMENT;
					} else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == (char) -1) { // EOF
						startCol = colNo - 1;
						state = CState.State.S_EOF;
					} else if (ch >= '0' && ch <= '9') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_DEC;
						if (text.length() == 1) {
							if (ch == '0') {
								state = CState.State.S_OCT;
							}
							char xChar = readChar();
							if (xChar == 'x') {
								text.append(xChar);
								state = CState.State.S_HEX;
							} else {
								backChar(xChar);
							}
						}
					} else if (ch == '+') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_PLUS;
					} else if (ch == '-') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_MINUS;
					} else if (ch == '*') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_MULT;
					} else if (ch == '&') {
						startCol = colNo - 1;
						text.append(ch);
						char xChar = readChar();
						if (xChar == '&') {
							text.append(xChar);
							state = CState.State.S_AND;
						} else {
							backChar(xChar);
							state = CState.State.S_AMP;
						}
					} else if (ch == '|') {
						startCol = colNo - 1;
						text.append(ch);
						char xChar = readChar();
						if (xChar == '|') {
							text.append(xChar);
							state = CState.State.S_OR;
						} else {
							backChar(xChar);
							state = CState.State.S_ERR;
						}
					} else if (ch == '(') {
						startCol = colNo - 1;
						text.append(ch);

						state = CState.State.S_LPAR;
					} else if (ch == ')') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_RPAR;
					} else if (ch == '[') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_LBRA;
					} else if (ch == ']') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_RBRA;
					} else if (isIndent(ch) || ch == '_') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_IDENT;
					} else if (ch == '=') {
						startCol = colNo - 1;
						text.append(ch);
						char xChar = readChar();
						if (xChar == '=') {
							text.append(xChar);
							state = CState.State.S_EQ;
						} else {
							backChar(xChar);
							state = CState.State.S_ASSIGN;
						}
					} else if (ch == ';') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_SEMI;
					} else if (ch == '<') {
						startCol = colNo - 1;
						text.append(ch);
						char xChar = readChar();
						if (xChar == '=') {
							text.append(xChar);
							state = CState.State.S_LE;
						} else {
							state = CState.State.S_LT;
							backChar(xChar);
						}
					} else if (ch == '>') {
						startCol = colNo - 1;
						text.append(ch);
						char xChar = readChar();
						if (xChar == '=') {
							text.append(xChar);
							state = CState.State.S_GE;
						} else {
							state = CState.State.S_GT;
							backChar(xChar);
						}
					} else if (ch == '!') {
						startCol = colNo - 1;
						text.append(ch);
						char xChar = readChar();
						if (xChar == '=') {
							text.append(xChar);
							state = CState.State.S_NE;
						} else if (isIndent(xChar)) {
							backChar(xChar);
							state = CState.State.S_EXCLAM;
						} else {
							backChar(xChar);
						}
					} else if (ch == '{') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_LCUR;
					} else if (ch == '}') {
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_RCUR;
					} else { // ヘンな文字を読んだ
						startCol = colNo - 1;
						text.append(ch);
						state = CState.State.S_ERR;
					}
					break;
				case S_EOF: // EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case S_ERR: // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case S_DEC: // 数（10進数）の開始
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
					} else {
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする)
						if (text.charAt(0) != '&'
								&& Integer.valueOf(text.toString()) > (int) Math.pow(2, 15) - 1) {
							System.err.println("16ビット符号付整数内に収めてください");
							state = CState.State.S_ERR;
							break;
						}
						tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
						accept = true;
					}
					break;
				case S_PLUS: // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case S_MINUS: // -を読んだ
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case S_DIV: // /を読んだ
					tk = new CToken(CToken.TK_DIV, lineNo, startCol, "/");
					accept = true;
					break;
				case S_MULT: // *を読んだ
					tk = new CToken(CToken.TK_MULT, lineNo, startCol, "*");
					accept = true;
					break;
				case S_COMMENT: // コメントアウト /* */ //
					boolean commentFlag = false;
					ch = readChar();
					if (ch == '*') { // コメントアウト/* */
						while (!commentFlag) {
							ch = readChar();
							if (ch == '*') {
								ch = readChar();
								if (ch == '/') {
									commentFlag = true;
									state = CState.State.S_CLR;
								} else {
									state = CState.State.S_CLR;
									backChar(ch);
								}
							}
							if (ch == (char) -1) {
								System.err.println("コメントアウトを閉じずに終了");
								state = CState.State.S_EOF;
								commentFlag = true;
							}
						}
					} else if (ch == '/') { // コメントアウト//
						while (!commentFlag) {
							ch = readChar();
							if (ch == '\n') {
								commentFlag = true;
								state = CState.State.S_CLR;
								backChar(ch);
							}
							if (ch == (char) -1) {
								commentFlag = true;
								state = CState.State.S_EOF;
							}
						}
					} else {
						backChar(ch);
						state = CState.State.S_DIV;
					}
					break;
				case S_OCT: // 8進数計算
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
							state = CState.State.S_ERR;
							break;
						}
						if (text.length() > 7
								|| (text.length() == 7 && (text.charAt(1) >= '2' && text.charAt(1) <= '7'))) {
							System.err.println("16ビット内に収めてください");
							state = CState.State.S_ERR;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case S_HEX: // 16進数計算
					ch = readChar();
					if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f')) {
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする)
						if (text.length() > 6) {
							System.err.println("16ビット内に収めてください");
							state = CState.State.S_ERR;
						} else if (text.length() < 3) {
							System.err.println("16進数の書き方にエラーがあります");
							state = CState.State.S_ERR;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case S_LPAR: // (
					tk = new CToken(CToken.TK_LPAR, lineNo, startCol, "(");
					accept = true;
					break;
				case S_RPAR: // )
					tk = new CToken(CToken.TK_RPAR, lineNo, startCol, ")");
					accept = true;
					break;
				case S_AMP: // &
					tk = new CToken(CToken.TK_AMP, lineNo, startCol, "&");
					accept = true;
					break;
				case S_LBRA: // [
					tk = new CToken(CToken.TK_LBRA, lineNo, startCol, "[");
					accept = true;
					break;
				case S_RBRA: // ]
					tk = new CToken(CToken.TK_RBRA, lineNo, startCol, "]");
					accept = true;
					break;
				case S_IDENT: // ident
					ch = readChar();
					if (isIndent(ch) || Character.isDigit(ch) || ch == '_') {
						text.append(ch);
					} else {
						backChar(ch); // 変数名ではない文字は戻す（読まなかったことにする)
						String s = text.toString();
						Integer i = (Integer) rule.get(s);
						tk = new CToken(((i == null) ? CToken.TK_IDENT : i.intValue()), lineNo, startCol, s);
						accept = true;
					}
					break;
				case S_ASSIGN: // =
					tk = new CToken(CToken.TK_ASSIGN, lineNo, startCol, "=");
					accept = true;
					break;
				case S_SEMI: // ;
					tk = new CToken(CToken.TK_SEMI, lineNo, startCol, ";");
					accept = true;
					break;
				case S_LT: // <
					tk = new CToken(CToken.TK_LT, lineNo, startCol, "<");
					accept = true;
					break;
				case S_LE: // <=
					tk = new CToken(CToken.TK_LE, lineNo, startCol, "<=");
					accept = true;
					break;
				case S_GT: // >
					tk = new CToken(CToken.TK_GT, lineNo, startCol, ">");
					accept = true;
					break;
				case S_GE: // >=
					tk = new CToken(CToken.TK_GE, lineNo, startCol, ">=");
					accept = true;
					break;
				case S_EQ: // ==
					tk = new CToken(CToken.TK_EQ, lineNo, startCol, "==");
					accept = true;
					break;
				case S_NE: // !=
					tk = new CToken(CToken.TK_NE, lineNo, startCol, "!=");
					accept = true;
					break;
				case S_LCUR: // {
					tk = new CToken(CToken.TK_LCUR, lineNo, startCol, "{");
					accept = true;
					break;
				case S_RCUR: // }
					tk = new CToken(CToken.TK_RCUR, lineNo, startCol, "}");
					accept = true;
					break;
				case S_AND: // }
					tk = new CToken(CToken.TK_AND, lineNo, startCol, "&&");
					accept = true;
					break;
				case S_OR: // }
					tk = new CToken(CToken.TK_OR, lineNo, startCol, "||");
					accept = true;
					break;
				case S_EXCLAM: // }
					tk = new CToken(CToken.TK_EXCLAM, lineNo, startCol, "!");
					accept = true;
					break;
			}
		}
		return tk;
	}
}