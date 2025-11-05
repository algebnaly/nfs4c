package com.algebnaly.nfs4c

import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class NFS4SeekableBytesChannel(
    private val session: Long,
    private val openedFile: Long
) : SeekableByteChannel {
    var position: Long = 0
    var isOpenFlag: Boolean = true
    var isEof: Boolean = false

    companion object {
        fun create(session: Long, path: String, opts: NFS4OpenOptions): NFS4SeekableBytesChannel {
            println(opts.getOpenOptions())
            val openedFile = NFS4CNativeBridge.openFile(
                session,
                path,
                opts.getOpenOptions()
            )
            return NFS4SeekableBytesChannel(session, openedFile)
        }
    }

    override fun read(dst: ByteBuffer?): Int {
        if(dst == null){
            throw NullPointerException()
        }
        if(isEof){
            return -1;// eof
        }
        val r =  NFS4CNativeBridge.fileRead(session, openedFile, dst)
        isEof = r.eof
        if(r.readBytes == 0 && isEof){
            return -1
        }
        position += r.readBytes
        return r.readBytes
    }

    override fun write(src: ByteBuffer?): Int {
        if(src == null){
            throw NullPointerException()
        }
        val w =  NFS4CNativeBridge.fileWrite(session, openedFile, src)
        position += w.WrittenBytes
        return w.WrittenBytes
    }

    override fun position(): Long = position

    override fun size(): Long = NFS4CNativeBridge.fileSize(session, openedFile)

    override fun position(newPosition: Long): SeekableByteChannel? {
        TODO("Not yet implemented")
    }

    override fun truncate(size: Long): SeekableByteChannel? {
        TODO("Not yet implemented")
    }

    override fun isOpen(): Boolean = isOpenFlag

    override fun close() {
        if(!isOpenFlag){
            return
        }
        isOpenFlag = false
        NFS4CNativeBridge.fileClose(session, openedFile)
    }
}

class NFS4FileReadResult(
    val eof: Boolean,
    val readBytes: Int
)

class NFS4FileWriteResult(
    val WrittenBytes: Int
)