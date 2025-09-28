package com.algebnaly.nfs4c

import java.nio.file.ClosedDirectoryStreamException
import java.nio.file.DirectoryStream
import java.nio.file.Path

class NFS4DirectoryStream(
    private val directory: NFS4Path,
    private val filter: DirectoryStream.Filter<in NFS4Path>
) : DirectoryStream<Path> {
    private var isClosed = false
    private val lazyIterator: MutableIterator<Path> by lazy {
        // 检查流是否已关闭
        if (isClosed) {
            throw ClosedDirectoryStreamException()
        }
        val methods = java.nio.file.spi.FileSystemProvider::class.java.methods
        methods.forEach { m ->
            println("Provider method: ${m.name}(${m.parameterTypes.joinToString()})")
        }

        val client = directory.getNFS4Client()
        //TODO: check result
        val resultList = NFS4CNativeBridge.listDir(client, directory.toString())


        resultList.map {
            NFS4Path(
                directory.fileSystem as NFS4FileSystem,
                directory.resolve(directory.newPath(it)).toString()
            ) as Path
        }
            .toMutableList().iterator()
    }

    override fun iterator(): MutableIterator<Path> {
        if (isClosed) {
            throw ClosedDirectoryStreamException()
        }
        return lazyIterator
    }

    override fun close() {
        isClosed = true
        return
    }
}