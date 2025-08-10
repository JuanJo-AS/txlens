package io.txlens.annotations;

public class TxAnotations {

    private TxRead txRead;
    private TxWrite txWrite;

    public TxAnotations(TxRead txread, TxWrite txWrite) {
        this.txRead = txread;
        this.txWrite = txWrite;
    }

    public TxRead getTxRead() {
        return txRead;
    }

    public TxWrite getTxWrite() {
        return txWrite;
    }

    public boolean hasNoTxAnnotation() {
        return txRead == null && txWrite == null;
    }
}
