package lang.c;

import java.util.HashMap;

import lang.SymbolTable;

public class CSymbolTable {
    private class OneSymbolTable extends SymbolTable<CSymbolTableEntry> {
        @Override
        public CSymbolTableEntry register(String name, CSymbolTableEntry e) {
            return put(name, e);
        }

        @Override
        public CSymbolTableEntry search(String name) {
            return get(name);
        }
    }

    private OneSymbolTable global = new OneSymbolTable(); // 大域変数用
    private OneSymbolTable local = new OneSymbolTable(); // 局所変数用

    public CSymbolTableEntry register(String name, CType type, int size, boolean isConst) {
        final CSymbolTableEntry entry = new CSymbolTableEntry(type, size, isConst, true, 0);
        CSymbolTableEntry ret = global.register(name, entry);
        return ret;
    }

    public CSymbolTableEntry search(String name) {
        CSymbolTableEntry ret = global.get(name);
        return ret;
    }
}
