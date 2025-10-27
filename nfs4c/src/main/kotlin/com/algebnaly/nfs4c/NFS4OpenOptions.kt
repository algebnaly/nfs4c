package com.algebnaly.nfs4c

class NFS4OpenOptions {
    private var openOptions: Int = 0
    fun read(): NFS4OpenOptions {
        this.openOptions.or(1.shl(0))
        return this
    }

    fun write(): NFS4OpenOptions {
        this.openOptions.or(1.shl(1))
        return this
    }

    fun create(): NFS4OpenOptions {
        this.openOptions.or(1.shl(2))
        return this
    }

    fun truncate(): NFS4OpenOptions {
        this.openOptions.or(1.shl(3))
        return this
    }

    fun getOpenOptions(): Int {
        return this.openOptions
    }
}