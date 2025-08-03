package com.algebnaly.nfs4c

class NFS4CNativeBridge {
    companion object{
        init {
            System.loadLibrary("nfscrs_jni")
        }
    }
    @JvmName("getClientSession")
    external fun getClientSession(uid: Int, gid: Int, remoteAddress: String): Long
    @JvmName("listDir")
    external fun listDir(session: Long, path: String): ArrayList<String>
}