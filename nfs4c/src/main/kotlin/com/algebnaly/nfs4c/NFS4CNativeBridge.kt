package com.algebnaly.nfs4c

import java.nio.ByteBuffer

class NFS4CNativeBridge {
    companion object{
        init {
            System.loadLibrary("nfscrs_jni")
        }
        @JvmStatic
        @JvmName("getClientSession")
        external fun getClientSession(uid: Int, gid: Int, remoteAddress: String): Long
        @JvmStatic
        @JvmName("listDir")
        external fun listDir(session: Long, path: String): ArrayList<String>

        @JvmStatic
        @JvmName("readAttr")
        external fun readAttr(session: Long, path: String): NFS4FileAttributes

        @JvmStatic
        @JvmName("openFile")
        external fun openFile(session: Long, path: String, openOptions: Int): Long

        @JvmStatic
        @JvmName("fileRead")
        external fun fileRead(session: Long, openedFile: Long, byteBuffer: ByteBuffer): NFS4FileReadResult

        @JvmStatic
        @JvmName("fileWrite")
        external fun fileWrite(session: Long, openedFile: Long, byteBuffer: ByteBuffer): NFS4FileWriteResult

        @JvmStatic
        @JvmName("fileSize")
        external fun fileSize(session: Long, openedFile: Long): Long

        @JvmStatic
        @JvmName("fileClose")
        external fun fileClose(session: Long, openedFile: Long)

    }
}