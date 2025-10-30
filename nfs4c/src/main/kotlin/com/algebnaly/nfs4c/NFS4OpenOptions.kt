package com.algebnaly.nfs4c

import java.nio.file.OpenOption
import java.nio.file.StandardOpenOption

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

    fun getOpenOptions(): Int = this.openOptions
}

fun getOpenOptionsFromSet(openOptionsSet: Set<OpenOption>): NFS4OpenOptions {
    val opt = NFS4OpenOptions()
    if(StandardOpenOption.READ in openOptionsSet){
        opt.read()
    }
    if(StandardOpenOption.WRITE in openOptionsSet){
        opt.write()
    }
    if(StandardOpenOption.CREATE in openOptionsSet){
        opt.create()
    }
    if(StandardOpenOption.TRUNCATE_EXISTING in openOptionsSet){
        opt.truncate()
    }
    return opt
}