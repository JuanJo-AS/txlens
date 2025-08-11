package io.txlens.interceptor;

import io.txlens.annotations.TxRead;
import io.txlens.annotations.TxWrite;

public class Service {
    @TxRead
    public String readMethod() {
        return "read";
    }

    @TxWrite
    public String writeMethod() {
        return "write";
    }

    public String noTxMethod() {
        return "no tx";
    }
}
