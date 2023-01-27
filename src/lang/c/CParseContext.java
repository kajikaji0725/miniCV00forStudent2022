package lang.c;

import lang.*;

public class CParseContext extends ParseContext {
    public CParseContext(IOContext ioCtx, CTokenizer tknz) {
        super(ioCtx, tknz);
    }

    @Override
    public CTokenizer getTokenizer() {
        return (CTokenizer) super.getTokenizer();
    }

    private int seqNo = 0;
    private CSymbolTable table = new CSymbolTable();

    public int getSeqId() {
        return ++seqNo;
    }

    public CSymbolTable getSymbolTable() {
        return table;
    }
}
