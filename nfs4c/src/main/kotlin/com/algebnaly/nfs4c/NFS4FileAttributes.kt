package com.algebnaly.nfs4c

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime

class NFS4FileAttributes: BasicFileAttributes {
    override fun lastAccessTime(): FileTime {
        TODO("Not yet implemented")
    }

    override fun lastModifiedTime(): FileTime {
        TODO("Not yet implemented")
    }

    override fun creationTime(): FileTime {
        TODO("Not yet implemented")
    }

    override fun isRegularFile(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isDirectory(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSymbolicLink(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isOther(): Boolean {
        TODO("Not yet implemented")
    }

    override fun size(): Long {
        TODO("Not yet implemented")
    }

    override fun fileKey(): Any? {
        return null
    }
}