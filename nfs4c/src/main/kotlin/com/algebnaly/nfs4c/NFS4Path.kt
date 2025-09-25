package com.algebnaly.nfs4c

import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService



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
        TODO("Not yet implemented")
    }

    override fun resolve(other: Path): Path {
        TODO("Not yet implemented")
    }

    override fun toUri(): URI {
        val scheme = nfs4FileSystem.nfs4Provider.scheme
        val host = nfs4FileSystem.serverAddress
        val port = nfs4FileSystem.port
        val appendedSep =  if(path.startsWith(nfs4FileSystem.separator)){
            ""
        }else{
            "/"
        }
        val uri = URI.create("$scheme://$host:$port$appendedSep$path")
        return uri
    }

    override fun relativize(other: Path): Path {
        TODO("Not yet implemented")
    }

    override fun toAbsolutePath(): Path = this // we assume all path are absolute

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
}