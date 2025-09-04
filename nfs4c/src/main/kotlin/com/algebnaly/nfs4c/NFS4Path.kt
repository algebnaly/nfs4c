package com.algebnaly.nfs4c

import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService


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
        TODO("Not yet implemented")
    }

    override fun getNameCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getName(index: Int): Path {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }

    override fun relativize(other: Path): Path {
        TODO("Not yet implemented")
    }

    override fun toAbsolutePath(): Path {
        TODO("Not yet implemented")
    }

    override fun toRealPath(vararg options: LinkOption): Path {
        TODO("Not yet implemented")
    }

    override fun register(
        watcher: WatchService,
        events: Array<out WatchEvent.Kind<*>>,
        vararg modifiers: WatchEvent.Modifier?
    ): WatchKey {
        TODO("Not yet implemented")
    }

    fun isRoot(): Boolean = path == nfs4FileSystem.separator

    fun getNFS4Client(): Long{
        return nfs4FileSystem.getNFS4Client()
    }

    override fun toString(): String {
        return path
    }
}