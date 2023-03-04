/*
 * Oh My JVM - A JDK version manager written in Kotlin
 *
 * Copyright (C) 2023 Omico
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package me.omico.ojvm.utility

import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import okio.BufferedSink
import okio.BufferedSource
import okio.FileMetadata
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.getenv
import platform.windows.BYTEVar
import platform.windows.GetFileVersionInfoSizeW
import platform.windows.GetFileVersionInfoW
import platform.windows.LPVOIDVar
import platform.windows.UINTVar
import platform.windows.VS_FIXEDFILEINFO
import platform.windows.VerQueryValueW

val userHomeDirectory: Path by lazy {
    getenv("USERPROFILE")?.toKString()?.toPath()
        ?: error("Cannot get user's home directory.")
}

inline fun String.toPath() = toPath(true)

@Throws(IOException::class)
inline fun Path.exists(): Boolean = FileSystem.SYSTEM.exists(this)

inline val Path.metadata: FileMetadata
    get() = FileSystem.SYSTEM.metadata(this)

@Throws(IOException::class)
inline fun Path.isDirectory(): Boolean = metadata.isDirectory

@Throws(IOException::class)
inline fun Path.createDirectories(mustCreate: Boolean = false) = FileSystem.SYSTEM.createDirectories(this, mustCreate)

@Throws(IOException::class)
inline fun Path.delete(mustExist: Boolean = false) = FileSystem.SYSTEM.delete(this, mustExist)

@Throws(IOException::class)
inline fun Path.list(): List<Path> = FileSystem.SYSTEM.list(this)

@Throws(IOException::class)
inline fun <T> Path.read(readerAction: BufferedSource.() -> T): T = FileSystem.SYSTEM.read(this, readerAction)

@Throws(IOException::class)
inline fun Path.readUtf8(): String = read(BufferedSource::readUtf8)

@Throws(IOException::class)
inline fun <T> Path.write(
    mustCreate: Boolean = false,
    writerAction: BufferedSink.() -> T,
): T = FileSystem.SYSTEM.write(this, mustCreate, writerAction)

@Throws(IOException::class)
inline fun Path.writeUtf8(
    content: String,
    mustCreate: Boolean = false,
) {
    write(mustCreate) {
        writeUtf8(content)
    }
}

inline fun Path.fileVersion(): String? {
    require(exists()) { "$this does not exist." }
    val filePath = toString()
    val size = GetFileVersionInfoSizeW(filePath, null)
    if (size == 0u) return null
    memScoped {
        val data = allocArray<BYTEVar>(size.toInt())
        if (GetFileVersionInfoW(filePath, 0u, size, data) == 0) return null
        val len = alloc<UINTVar>()
        val fileInfo = alloc<LPVOIDVar>()
        if (VerQueryValueW(data, "\\", fileInfo.ptr.reinterpret(), len.ptr) == 0) return null
        val info = fileInfo.value!!.reinterpret<VS_FIXEDFILEINFO>().pointed
        val version = buildString {
            append(HIWORD(info.dwFileVersionMS))
            append(".")
            append(LOWORD(info.dwFileVersionMS))
            append(".")
            append(HIWORD(info.dwFileVersionLS))
            append(".")
            append(LOWORD(info.dwFileVersionLS))
        }
        return version
    }
}
