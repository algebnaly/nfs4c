package com.algebnaly.nfs4c

import java.io.IOException
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.InvalidPathException
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.security.InvalidParameterException
import kotlin.io.path.absolute


// we do not support relative path for now, all path are assumed to be absolute path
class NFS4Path(
    private val nfs4FileSystem: NFS4FileSystem,
    private val path: String
) : Path {

    override fun getFileSystem(): FileSystem = nfs4FileSystem

    override fun compareTo(other: Path): Int {
        TODO("Not yet implemented")
    }

    override fun endsWith(other: Path): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAbsolute(): Boolean = path.startsWith(nfs4FileSystem.separator)

    override fun getRoot(): Path = NFS4Path(this.nfs4FileSystem, "/")

    override fun getFileName(): Path? {
        if (isRoot()) return null

        return path
            .trimEnd(nfs4FileSystem.separator.single())
            .split(nfs4FileSystem.separator)
            .lastOrNull { it.isNotEmpty() }
            ?.let { last ->
                NFS4Path(nfs4FileSystem, last)
            }
    }

    override fun getParent(): Path? {
        if (path.isEmpty()) {
            return null
        }
        if (isRoot()) {
            return null
        }

        val endingSepRemovedPath = path.trimEnd(nfs4FileSystem.separator.single())
        val lastSepIndex = endingSepRemovedPath.lastIndexOf(nfs4FileSystem.separator)
        if (lastSepIndex < 0) {
            return null
        }
        if (lastSepIndex == 0) {
            return NFS4Path(nfs4FileSystem, nfs4FileSystem.separator)//root
        }

        val newPath = path.substring(0, lastSepIndex)
        return NFS4Path(nfs4FileSystem, newPath)
    }

    override fun getNameCount(): Int {
        if (path.isEmpty()) return 0
        val withoutRoot = if (path.startsWith(nfs4FileSystem.separator)) {
            path.removePrefix(nfs4FileSystem.separator)
        } else {
            path
        }
        return withoutRoot.split(nfs4FileSystem.separator)
            .filter { it.isNotEmpty() }
            .size
    }

    override fun getName(index: Int): Path {
        val withoutRoot = if (path.startsWith(nfs4FileSystem.separator)) {
            path.removePrefix(nfs4FileSystem.separator)
        } else {
            path
        }
        val nameList = withoutRoot.split(nfs4FileSystem.separator)
            .filter { it.isNotEmpty() }
        return NFS4Path(nfs4FileSystem, nameList[index])
    }

    override fun subpath(beginIndex: Int, endIndex: Int): Path {
        TODO("Not yet implemented")
    }

    override fun startsWith(other: Path): Boolean {
        TODO("Not yet implemented")
    }

    override fun normalize(): Path {
        val components = mutableListOf<String>()
        val component_list = this.path.split(this.nfs4FileSystem.separator).filter {
            it != "." ||
                    it.isNotEmpty()
        }
        for (c in component_list) {
            if (c != "..") {
                components.add(c)
            } else {
                if (components.isEmpty()) {
                    throw InvalidParameterException("$this")
                }
                components.removeAt(components.size - 1)
            }
        }
        return NFS4Path(
            nfs4FileSystem,
            nfs4FileSystem.separator + components.joinToString(nfs4FileSystem.separator)
        )
    }

    override fun resolve(other: Path): Path {
        if (other.fileSystem != this.fileSystem) {
            throw InvalidParameterException("$other is not on the same file system")
        }

        if (other.isAbsolute) {
            return other
        }

        val thisComponents =
            this.toString().split(this.fileSystem.separator).filter { it.isNotEmpty() }
        val otherComponents =
            other.toString().split(other.fileSystem.separator).filter { it.isNotEmpty() }

        val resolvedComponents = thisComponents.toMutableList().apply {
            addAll(otherComponents)
        }

        val resolvedPathStr = resolvedComponents.joinToString(
            separator = this.fileSystem.separator,
            prefix = this.fileSystem.separator
        )
        return NFS4Path(nfs4FileSystem, resolvedPathStr)
    }

    override fun toUri(): URI {
        val scheme = nfs4FileSystem.nfs4Provider.scheme
        val host = nfs4FileSystem.serverAddress
        val port = nfs4FileSystem.port
        val appendedSep = if (path.startsWith(nfs4FileSystem.separator)) {
            ""
        } else {
            nfs4FileSystem.separator
        }
        val uri = URI.create("$scheme://$host:$port$appendedSep$path")
        return uri
    }

    override fun relativize(other: Path): Path {
        if (other.fileSystem != this.fileSystem) {
            throw InvalidParameterException("$other is not on the same file system")
        }

        if (!other.isAbsolute) {
            throw InvalidParameterException("$other is not absolute path")
        }

        val thisPath = this.normalize()
        val otherPath = other.normalize()

        val thisComponents =
            thisPath.toString().split(this.fileSystem.separator).filter { it.isNotEmpty() }
        val otherComponents =
            otherPath.toString().split(other.fileSystem.separator).filter { it.isNotEmpty() }

        var common = 0
        val minLength = minOf(thisComponents.size, otherComponents.size)
        while (common < minLength && thisComponents[common] == otherComponents[common]) {
            common++
        }

        val resultComponents = mutableListOf<String>()

        for (i in common until thisComponents.size) {
            resultComponents.add("..")
        }

        for (i in common until otherComponents.size) {
            resultComponents.add(otherComponents[i])
        }

        return NFS4Path(
            nfs4FileSystem,
            this.fileSystem.separator + resultComponents.joinToString(separator = this.fileSystem.separator)
        )
    }

    override fun toAbsolutePath(): Path = this

    override fun toRealPath(vararg options: LinkOption): Path = this

    override fun register(
        watcher: WatchService,
        events: Array<out WatchEvent.Kind<*>>,
        vararg modifiers: WatchEvent.Modifier?
    ): WatchKey {
        TODO("Not yet implemented")
    }

    fun isRoot(): Boolean = path == nfs4FileSystem.separator

    fun getNFS4Client(): Long {
        return nfs4FileSystem.getNFS4Client()
    }

    override fun toString(): String {
        return path
    }

    fun newPath(newPath: String): NFS4Path {
        return NFS4Path(nfs4FileSystem, newPath)
    }
}