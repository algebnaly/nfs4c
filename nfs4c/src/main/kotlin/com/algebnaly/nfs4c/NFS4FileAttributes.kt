package com.algebnaly.nfs4c

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime

class NFS4FileAttributes(
    private val lastAccessTime: FileTime,
    private val lastModifiedTime: FileTime,
    private val creationTime: FileTime,
    private val isRegularFile: Boolean,
    private val isDirectory: Boolean,
    private val isSymbolicLink: Boolean,
    private val isOther: Boolean,
    private val size: Long,
    private val mode: Int,
    private val fileKey: Any? = null
) : BasicFileAttributes {

    override fun lastAccessTime(): FileTime = lastAccessTime

    override fun lastModifiedTime(): FileTime = lastModifiedTime

    override fun creationTime(): FileTime = creationTime

    override fun isRegularFile(): Boolean = isRegularFile

    override fun isDirectory(): Boolean = isDirectory

    override fun isSymbolicLink(): Boolean = isSymbolicLink

    override fun isOther(): Boolean = isOther

    override fun size(): Long = size

    override fun fileKey(): Any? = fileKey

    fun isExecutable(): Boolean = mode.and(1.shl(6)) != 0

    fun isWritable(): Boolean = mode.and(1.shl(7)) != 0

    fun isReadable(): Boolean = mode.and(1.shl(8)) != 0


    fun mode(): Int = mode
}