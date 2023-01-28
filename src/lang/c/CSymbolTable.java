package lang.c;

import java.util.HashMap;

import lang.SymbolTable;
import lang.c.parse.AddressToValue;

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
    private int localAddress = 0; // ローカル変数の番地
    private boolean isGrobal = true;

    public CSymbolTableEntry register(String name, CType type, int size, boolean isConst) {
        final int address = isGrobal ? 0 : localAddress;
        final CSymbolTableEntry entry = new CSymbolTableEntry(type, size, isConst, isGrobal, address);
        CSymbolTableEntry ret;
        if (isGrobal) {
            ret = global.register(name, entry);
        } else {
            ret = local.register(name, entry);
            localAddress += size;
        }
        return ret;
    }

    public CSymbolTableEntry search(String name) {
        CSymbolTableEntry ret = local.get(name);
        if (ret == null) {
            ret = global.get(name);
        }
        return ret;
    }

    public void show() {
        global.show();
        local.show();
    }

    public void setLocal() {
        isGrobal = false;
    }

    public void setGlobal() {
        isGrobal = true;
        local.clear();
        localAddress = 0x0000;
    }

    public int getAddress() {
        return localAddress;
    }

    public boolean isGlobal() {
        return isGrobal;
    }
}
