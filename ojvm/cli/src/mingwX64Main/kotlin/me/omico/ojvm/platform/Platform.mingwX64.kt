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
package me.omico.ojvm.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import kotlinx.io.files.Path
import me.omico.ojvm.configuration.JdkConfiguration
import me.omico.ojvm.platform.windows.HIWORD
import me.omico.ojvm.platform.windows.LOWORD
import me.omico.ojvm.utility.exists
import me.omico.ojvm.utility.ojvmCurrentJdkDirectory
import platform.windows.BYTEVar
import platform.windows.CreateSymbolicLinkW
import platform.windows.GetFileVersionInfoSizeW
import platform.windows.GetFileVersionInfoW
import platform.windows.GetLastError
import platform.windows.LPVOIDVar
import platform.windows.SYMBOLIC_LINK_FLAG_DIRECTORY
import platform.windows.UINTVar
import platform.windows.VS_FIXEDFILEINFO
import platform.windows.VerQueryValueW

@OptIn(ExperimentalForeignApi::class)
actual fun Path.fileVersion(): String? {
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

actual fun JdkConfiguration.createJdkDirectorySymbolicLink(): Boolean {
    val fail = CreateSymbolicLinkW(
        lpSymlinkFileName = ojvmCurrentJdkDirectory.toString(),
        lpTargetFileName = path,
        dwFlags = SYMBOLIC_LINK_FLAG_DIRECTORY.toUInt(),
    ).toInt() == 0
    if (fail) println("Failed to create symbolic link: ${GetLastError()}")
    return fail.not()
}
