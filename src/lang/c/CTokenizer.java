package lang.c;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import lang.*;

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
		// System.out.println("Token='" + currentTk.toString());
		return currentTk;
	}

	private CToken readToken() {
		CToken tk = null;
		char ch;
		int startCol = colNo;

		StringBuffer text = new StringBuffer();

		Character[] useOpe = { '+', '-' }; // 使用する演算子

		int state = 0;
		boolean accept = false;
		Boolean errFlag = false;
		Set<Character> errChar = new HashSet<Character>();
		while (!accept) {
			switch (state) {
				case 0: // 初期状態
					ch = readChar();
					if (ch == '/') {
						state = 6;
					} else if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
					} else if (ch == (char) -1) { // EOF
						startCol = colNo - 1;
						state = 1;
					} else if (ch >= '0' && ch <= '9') {
						startCol = colNo - 1;
						text.append(ch);
						state = 3;
						if (text.length() == 1) {
							if (ch == '0') {
								state = 7;
							}
							char xChar = readChar();
							if (xChar == 'x') {
								text.append(xChar);
								state = 8;
							} else {
								backChar(xChar);
							}
						}
					} else if (ch == '+') {
						startCol = colNo - 1;
						text.append(ch);
						state = 4;
					} else if (ch == '-') {
						startCol = colNo - 1;
						text.append(ch);
						state = 5;
					} else if (ch == '&') {
						startCol = colNo - 1;
						text.append(ch);
						state = 3; // &のあとが10進数の場合のみ対応
					} else { // ヘンな文字を読んだ
						startCol = colNo - 1;
						text.append(ch);
						state = 2;
					}
					break;
				case 1: // EOFを読んだ
					tk = new CToken(CToken.TK_EOF, lineNo, startCol, "end_of_file");
					accept = true;
					break;
				case 2: // ヘンな文字を読んだ
					tk = new CToken(CToken.TK_ILL, lineNo, startCol, text.toString());
					accept = true;
					break;
				case 3: // 数（10進数）の開始
					ch = readChar();
					if (Character.isDigit(ch)) {
						text.append(ch);
					} else {
						if (!Arrays.asList(useOpe).contains(ch) && ch != (char) -1) { // 四則演算が来た場合は数の終わりとする。
							text.append(ch);
							errChar.add(ch);
							errFlag = true;
						} else {
							// 数の終わり
							backChar(ch); // 数を表さない文字は戻す（読まなかったことにする)
							if (errFlag) {
								System.err.println(errChar + " は10進数では使えません");
								state = 2;
							}
							if (text.charAt(0) == '&' && text.charAt(0) != '&'
									&& Integer.valueOf(text.toString()) > (int) Math.pow(2, 15) - 1) {
								System.err.println("16ビット符号付整数内に収めてください");
								state = 2;
							}
							if (text.charAt(0) == '&') {
								tk = new CToken(CToken.TK_AMP, lineNo, startCol, text.toString());
								accept = true;
							} else {
								tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
								accept = true;
							}

						}
					}
					break;
				case 4: // +を読んだ
					tk = new CToken(CToken.TK_PLUS, lineNo, startCol, "+");
					accept = true;
					break;
				case 5:
					tk = new CToken(CToken.TK_MINUS, lineNo, startCol, "-");
					accept = true;
					break;
				case 6: // コメントアウト /* */ //
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
								state = 1;
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
								state = 1;
							}
						}
					} else {
						backChar(ch);
						text.append('/');
						state = 2;
					}
					break;
				case 7: // 8進数計算
					ch = readChar();
					if (Character.isDigit(ch)) {
						if (ch >= '8') {
							errChar.add(ch);
							errFlag = true;
						}
						text.append(ch);
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする)
						if (text.length() > 7
								|| (text.length() == 7 && (text.charAt(1) >= '2' && text.charAt(1) <= '7'))) {
							System.err.println("16ビット内に収めてください");
							state = 2;
						} else if (errFlag) {
							System.err.println(errChar + " は8進数では使えません");
							state = 2;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
				case 8: // 16進数計算
					ch = readChar();
					if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f')) {

						text.append(ch);
					} else if (ch > 'f' && ch != (char) -1) {
						text.append(ch);
						errChar.add(ch);
						errFlag = true;
					} else {
						// 数の終わり
						backChar(ch); // 数を表さない文字は戻す（読まなかったことにする)
						if (text.length() > 6) {
							System.err.println("16ビット内に収めてください");
							state = 2;
						} else if (errFlag) {
							System.err.println(errChar + " は16進数では使えません");
							state = 2;
						} else if (text.length() == 2) {
							System.err.println("不正な16進数の表記です");
							state = 2;
						} else {
							tk = new CToken(CToken.TK_NUM, lineNo, startCol, text.toString());
							accept = true;
						}
					}
					break;
			}
		}
		return tk;
	}
}
