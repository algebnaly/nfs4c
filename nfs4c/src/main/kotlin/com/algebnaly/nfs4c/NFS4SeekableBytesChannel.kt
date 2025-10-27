package com.algebnaly.nfs4c

import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class NFS4SeekableBytesChannel(
    private val session: Long,
    private val openedFile: Long
) : SeekableByteChannel {
    var position: Long = 0

    companion object {
        fun create(session: Long, path: String): NFS4SeekableBytesChannel {
            val openedFile = NFS4CNativeBridge.openFile(
                session,
                path,
                NFS4OpenOptions().create().read().write().truncate().getOpenOptions()
            )
            return NFS4SeekableBytesChannel(session, openedFile)
        }
    }

    override fun read(dst: ByteBuffer?): Int {
        TODO("Not yet implemented")
    }

    override fun write(src: ByteBuffer?): Int {
        TODO("Not yet implemented")
    }

    override fun position(): Long {
        TODO("Not yet implemented")
    }

    override fun size(): Long {
        TODO("Not yet implemented")
    }

    override fun position(newPosition: Long): SeekableByteChannel? {
        TODO("Not yet implemented")
    }

    override fun truncate(size: Long): SeekableByteChannel? {
        TODO("Not yet implemented")
    }

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}