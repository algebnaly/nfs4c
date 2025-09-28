package com.algebnaly.nfs4c

import java.net.URI
import java.nio.channels.SeekableByteChannel
import java.nio.file.AccessMode
import java.nio.file.CopyOption
import java.nio.file.DirectoryStream
import java.nio.file.FileStore
import java.nio.file.FileSystem
import java.nio.file.FileSystemAlreadyExistsException
import java.nio.file.FileSystemNotFoundException
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.name

class NFS4FileSystemProvider : FileSystemProvider() {
    companion object {
        const val SCHEME = "nfs4"

        const val DEFAULT_PORT: Short = 2049
        const val DEFAULT_UID: Int = 1000
        const val DEFAULT_GID: Int = 1000
        const val DEFAULT_TIMEOUT = 30000L
        const val MAX_PATH_LENGTH = 4096
    }

    private val fileSystems = ConcurrentHashMap<ConnectionKey, NFS4FileSystem>()

    override fun getScheme(): String {
        return SCHEME
    }

    override fun newFileSystem(uri: URI, env: Map<String, *>): NFS4FileSystem {
        val host =
            uri.host ?: (env["host"] as? String ?: throw IllegalArgumentException("host required"))

        val port: Short =
            if (uri.port >= 1) uri.port.toShort() else (env["port"] as? Short ?: DEFAULT_PORT)
        val uid = (env["uid"] as? Int) ?: DEFAULT_UID
        val gid = (env["gid"] as? Int) ?: DEFAULT_GID

        val key = ConnectionKey(host)

        val fs = NFS4FileSystem.create(
            this, host, port, uid, gid
        )

        val existing = fileSystems.putIfAbsent(key, fs)
        if (existing != null) {
            fs.close()
            throw FileSystemAlreadyExistsException("FileSystem already exists for $uri")
        }
        return fs
    }

    override fun getFileSystem(uri: URI): FileSystem {
        val host =
            uri.host ?: throw IllegalArgumentException("host required")

        val key = ConnectionKey(
            host
        )
        return fileSystems.get(key) ?: throw FileSystemNotFoundException("No FileSystem for $uri")
    }

    override fun getPath(uri: URI): Path {
        val host =
            uri.host ?: throw IllegalArgumentException("host required")
        val fs = getFileSystem(uri)
        return fs.getPath(uri.path)
    }

    override fun newByteChannel(
        path: Path,
        options: Set<OpenOption>?,
        vararg attrs: FileAttribute<*>
    ): SeekableByteChannel {
        TODO("Not yet implemented")
    }

    override fun newDirectoryStream(
        dir: Path,
        filter: DirectoryStream.Filter<in Path>
    ): DirectoryStream<Path> {
        if (dir !is NFS4Path) {
            throw IllegalArgumentException("$dir is not NFS4 Path")
        }
        return NFS4DirectoryStream(dir, filter)
    }

    override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>) {
        TODO("Not yet implemented")
    }

    override fun delete(path: Path) {
        TODO("Not yet implemented")
    }

    override fun copy(source: Path, target: Path, vararg options: CopyOption) {
        TODO("Not yet implemented")
    }

    override fun move(source: Path, target: Path, vararg options: CopyOption) {
        TODO("Not yet implemented")
    }

    //TODO: need further work
    override fun isSameFile(path: Path, path2: Path): Boolean = path == path2

    override fun isHidden(path: Path): Boolean = path.name.startsWith(".")

    override fun getFileStore(path: Path): FileStore {
        TODO("Not yet implemented")
    }

    override fun checkAccess(path: Path, vararg modes: AccessMode) {
        val nfsPath = path as NFS4Path

        val attrs = try {
            readAttributes(nfsPath, NFS4FileAttributes::class.java)
        } catch (e: Exception) {
            throw java.nio.file.NoSuchFileException(path.toString())
        }

        for (mode in modes) {
            when (mode) {
                AccessMode.READ -> {
                    if (!attrs.isReadable()) {
                        throw java.nio.file.AccessDeniedException(path.toString())
                    }
                }

                AccessMode.WRITE -> {
                    if (!attrs.isWritable()) {
                        throw java.nio.file.AccessDeniedException(path.toString())
                    }
                }

                AccessMode.EXECUTE -> {
                    if (!attrs.isExecutable()) {
                        throw java.nio.file.AccessDeniedException(path.toString())
                    }
                }
            }
        }
    }

    override fun <V : FileAttributeView> getFileAttributeView(
        path: Path,
        type: Class<V>,
        vararg options: LinkOption
    ): V {
        TODO("Not yet implemented")
    }

    override fun readAttributes(
        path: Path,
        attributes: String,
        vararg options: LinkOption
    ): Map<String, Any?> {
        if (path !is NFS4Path) {
            throw IllegalArgumentException("$path is not NFS4 Path")
        }
        val result = mutableMapOf<String, Any?>()

        val attrList = if (attributes == "*" || attributes == "basic:*") {
            listOf(
                "lastModifiedTime",
                "lastAccessTime",
                "creationTime",
                "size",
                "isRegularFile",
                "isDirectory",
                "isSymbolicLink",
                "isOther",
                "fileKey"
            )
        } else if (attributes.startsWith("basic:")) {
            attributes.removePrefix("basic:").split(",")
        } else {
            attributes.split(",")
        }

        val fs = path.fileSystem
        if (fs !is NFS4FileSystem) {
            throw IllegalArgumentException("$fs is not NFS4FileSystem")
        }

        val nfs4attr = NFS4CNativeBridge.readAttr(session = fs.nfsClient, path = path.toString())

        for (attr in attrList) {
            when (attr) {
                "lastModifiedTime" -> result["lastModifiedTime"] = nfs4attr.lastModifiedTime()
                "lastAccessTime" -> result["lastAccessTime"] = nfs4attr.lastAccessTime()
                "creationTime" -> result["creationTime"] = nfs4attr.creationTime()
                "size" -> result["size"] = nfs4attr.size()
                "isRegularFile" -> result["isRegularFile"] = nfs4attr.isRegularFile()
                "isDirectory" -> result["isDirectory"] = nfs4attr.isDirectory()
                "isSymbolicLink" -> result["isSymbolicLink"] = nfs4attr.isSymbolicLink()
                "isOther" -> result["isOther"] = nfs4attr.isOther()
                "fileKey" -> result["fileKey"] = nfs4attr.fileKey()
                "isExecutable" -> result["isExecutable"] = nfs4attr.isExecutable()
                "isWritable" -> result["isWritable"] = nfs4attr.isWritable()
                "isReadable" -> result["isReadable"] = nfs4attr.isReadable()
            }
        }

        return result
    }

    override fun <A : BasicFileAttributes> readAttributes(
        path: Path,
        type: Class<A>,
        vararg options: LinkOption
    ): A {
        if (type != BasicFileAttributes::class.java && type != NFS4FileAttributes::class.java) {
            throw UnsupportedOperationException("Attributes of type $type not supported")
        }

        val nfsPath = path as? NFS4Path
            ?: throw IllegalArgumentException("Path must be NFS4Path")

        val fs = nfsPath.fileSystem as NFS4FileSystem
        val client = fs.getNFS4Client()

        val attrs = NFS4CNativeBridge.readAttr(client, nfsPath.toString())

        @Suppress("UNCHECKED_CAST")
        return attrs as A
    }

    override fun setAttribute(
        path: Path,
        attribute: String,
        value: Any,
        vararg options: LinkOption
    ) {
        TODO("Not yet implemented")
    }
}