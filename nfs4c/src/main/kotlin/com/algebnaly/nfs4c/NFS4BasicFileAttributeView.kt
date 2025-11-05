package com.algebnaly.nfs4c

import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import kotlin.io.path.name

class NFS4BasicFileAttributeView(private val path: NFS4Path) : BasicFileAttributeView{

    companion object{
        fun create(path: NFS4Path): NFS4BasicFileAttributeView = NFS4BasicFileAttributeView(path)
    }

    override fun name(): String = path.name

    override fun readAttributes(): BasicFileAttributes {
        return NFS4CNativeBridge.readAttr(session = (path.fileSystem as NFS4FileSystem).nfsClient ,path = path.toString())
    }

    override fun setTimes(
        lastModifiedTime: FileTime?,
        lastAccessTime: FileTime?,
        createTime: FileTime?
    ) {
        val fs = path.fileSystem as NFS4FileSystem
        val session = fs.nfsClient

        var flags = 0
        val mtime = lastModifiedTime?.toMillis() ?: 0L
        val atime = lastAccessTime?.toMillis() ?: 0L
        val ctime = createTime?.toMillis() ?: 0L

        if (lastModifiedTime != null) flags = flags or (1 shl 0)
        if (lastAccessTime != null) flags = flags or (1 shl 1)
        if (createTime != null) flags = flags or (1 shl 2)
    }
}