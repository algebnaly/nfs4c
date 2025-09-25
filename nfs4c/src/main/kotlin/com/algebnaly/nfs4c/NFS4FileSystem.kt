package com.algebnaly.nfs4c

import java.nio.file.*
import java.nio.file.attribute.UserPrincipalLookupService
import java.nio.file.spi.FileSystemProvider


class NFS4FileSystem internal constructor(
    val nfs4Provider: NFS4FileSystemProvider,
    val serverAddress: String,
    val port: Short = NFS4FileSystemProvider.DEFAULT_PORT,
    var nfsClient: Long
) : FileSystem() {

    companion object {
        fun create(
            nfs4Provider: NFS4FileSystemProvider,
            serverAddress: String,
            port: Short = NFS4FileSystemProvider.DEFAULT_PORT,
            uid: Int = 1000,
            gid: Int = 1000,
        ): NFS4FileSystem {
            val nfs4Client = NFS4CNativeBridge.getClientSession(uid, gid, "$serverAddress:$port")
            val nfs4FileSystem = NFS4FileSystem(nfs4Provider, serverAddress, port, nfs4Client)
            return nfs4FileSystem
        }
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun isOpen(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isReadOnly(): Boolean {
        TODO("Not yet implemented")
    }

    override fun provider(): FileSystemProvider? = nfs4Provider

    override fun getSeparator(): String = "/"

    override fun getRootDirectories(): Iterable<Path> {
        TODO("Not yet implemented")
    }

    override fun getFileStores(): Iterable<FileStore?>? {
        TODO("Not yet implemented")
    }

    override fun supportedFileAttributeViews(): Set<String> {
        TODO("Not yet implemented")
    }

    override fun getPath(first: String, vararg more: String?): Path {
        val allSegments = (listOf(first) + more).filterNotNull().filter { it.isNotEmpty() }

        if (allSegments.isEmpty()) {
            throw InvalidPathException("", "Cannot create path: all segments are empty")
        }

        val pathBuilder = StringBuilder(allSegments[0])

        for (segment in allSegments.drop(1)) {
            if (!pathBuilder.endsWith("/") && !segment.startsWith("/")) {
                pathBuilder.append("/")
            }
            pathBuilder.append(segment)
        }

        return NFS4Path(this, pathBuilder.toString())
    }

    override fun getPathMatcher(syntaxAndPattern: String?): PathMatcher? {
        TODO("Not yet implemented")
    }

    override fun getUserPrincipalLookupService(): UserPrincipalLookupService? {
        TODO("Not yet implemented")
    }

    override fun newWatchService(): WatchService? {
        TODO("Not yet implemented")
    }

    fun getNFS4Client(): Long {
        return nfsClient
    }
}