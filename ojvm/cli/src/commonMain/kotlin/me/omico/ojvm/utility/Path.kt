/*
 * Oh My JVM - A JDK version manager written in Kotlin
 *
 * Copyright (C) 2023-2025 Omico
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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.io.IOException
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileMetadata
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
val userHomeDirectory: Path by lazy {
    getenv("USERPROFILE")?.toKString()?.toPath()
        ?: error("Cannot get user's home directory.")
}

fun String.toPath(): Path = Path(this)

@Throws(IOException::class)
fun Path.exists(): Boolean = SystemFileSystem.exists(this)

inline val Path.metadata: FileMetadata
    get() = SystemFileSystem.metadataOrNull(this) ?: error("Cannot get metadata of path: $this")

@Throws(IOException::class)
fun Path.isDirectory(): Boolean = metadata.isDirectory

@Throws(IOException::class)
fun Path.createDirectories(mustCreate: Boolean = false) = SystemFileSystem.createDirectories(this, mustCreate)

@Throws(IOException::class)
fun Path.delete(mustExist: Boolean = false): Unit = SystemFileSystem.delete(this, mustExist)

@Throws(IOException::class)
fun Path.list(): Collection<Path> = SystemFileSystem.list(this)

@Throws(IOException::class)
fun Path.resolve(): Path = SystemFileSystem.resolve(this)

@Throws(IOException::class)
inline fun <T> Path.read(readerAction: Source.() -> T): T = SystemFileSystem.source(this).buffered().use(readerAction)

@Throws(IOException::class)
fun Path.readUtf8(): String = read { readString() }

@Throws(IOException::class)
inline fun <T> Path.write(writerAction: Sink.() -> T): T = SystemFileSystem.sink(this).buffered().use(writerAction)

@Throws(IOException::class)
fun Path.writeUtf8(content: String): Unit = write { writeString(content) }
