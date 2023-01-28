package lang.c.parse.state;

import java.io.PrintStream;
import java.util.ArrayList;

import lang.*;
import lang.c.*;

public class StatementBlock extends CParseRule {
    // StatementBlock ::= Statement | LCUR statement RCUR
    // if(true) ia_hoge=1のパターンがある
    // if文の中は複数の文で構成されている場合がある

    private CParseRule singleStateBlock;
    private ArrayList<CParseRule> stateBlocks;

    public StatementBlock(CParseContext pcx) {
        stateBlocks = new ArrayList<CParseRule>();
    }

    public static boolean isFirst(CToken tk) {
        return tk.getType() == CToken.TK_LCUR || Statement.isFirst(tk);
    }

    public void parse(CParseContext pcx) throws FatalErrorException {
        // ここにやってくるときは、必ずisFirst()が満たされている
        CTokenizer ct = pcx.getTokenizer();
        CToken tk = ct.getCurrentToken(pcx);
        CParseRule list = null;

        if (Statement.isFirst(tk)) {
            singleStateBlock = new Statement(pcx);
            singleStateBlock.parse(pcx);
        } else {
            tk = ct.getNextToken(pcx);
            while (Statement.isFirst(tk)) {
                try {
                    list = new Statement(pcx);
                    list.parse(pcx);
                    stateBlocks.add(list);
                } catch (RecoverableErrorException e) {
                    ct.skipTo(pcx);
                    tk = ct.getCurrentToken(pcx);
                }
                tk = ct.getCurrentToken(pcx);
            }
            tk = ct.getCurrentToken(pcx);
            if (tk.getType() == CToken.TK_RCUR) {
                tk = ct.getNextToken(pcx);
            } else {
                pcx.warning("}が閉じていませんので補いました");
            }
        }
    }

    public void semanticCheck(CParseContext pcx) throws FatalErrorException {
        if (singleStateBlock != null) {
            singleStateBlock.semanticCheck(pcx);
        }
        if (stateBlocks != null) {
            for (CParseRule stateBlock : stateBlocks) {
                stateBlock.semanticCheck(pcx);
            }
        }
    }

    public void codeGen(CParseContext pcx) throws FatalErrorException {
        PrintStream o = pcx.getIOContext().getOutStream();
        o.println(";;; statementBlock starts");
        if (singleStateBlock != null) {
            singleStateBlock.codeGen(pcx);
        }
        if (stateBlocks != null) {
            for (CParseRule stateBlock : stateBlocks) {
                stateBlock.codeGen(pcx);
            }
        }
        o.println(";;; statementBlock completes");
    }
}
