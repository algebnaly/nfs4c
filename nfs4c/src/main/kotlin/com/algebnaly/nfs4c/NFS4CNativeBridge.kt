package com.algebnaly.nfs4c

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
    }
}