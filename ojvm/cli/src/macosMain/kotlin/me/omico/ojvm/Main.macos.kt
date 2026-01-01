/*
 * Oh My JVM - A JDK version manager written in Kotlin
 *
 * Copyright (C) 2026 Omico
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
package me.omico.ojvm

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.Foundation.NSArray
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSError
import platform.Foundation.NSPipe
import platform.Foundation.NSPropertyListFormatVar
import platform.Foundation.NSPropertyListMutableContainersAndLeaves
import platform.Foundation.NSPropertyListSerialization
import platform.Foundation.NSString
import platform.Foundation.NSTask
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.Foundation.launch
import platform.Foundation.readDataToEndOfFile
import platform.Foundation.setLaunchPath
import platform.Foundation.waitUntilExit
import platform.posix.memcpy

actual fun main(arguments: Array<String>) {
    val jdks = listJdksViaJavaHome()
    jdks.forEach {
        println(
            buildString {
                append(it.homePath)
                it.version?.let { v -> append(" | version=$v") }
                it.vendor?.let { v -> append(" | vendor=$v") }
                it.name?.let { v -> append(" | name=$v") }
                it.arch?.let { v -> append(" | arch=$v") }
            },
        )
    }
}

data class JdkInfo(
    val homePath: String,
    val version: String?,
    val vendor: String?,
    val name: String?,
    val arch: String?,
)

@OptIn(
    BetaInteropApi::class,
    ExperimentalForeignApi::class,
)
private fun runProcess(
    launchPath: String,
    args: List<String>,
): ByteArray {
    val task =
        NSTask().apply {
            setLaunchPath(launchPath)
            setArguments(args)
        }

    val outPipe = NSPipe()
    val errPipe = NSPipe()
    task.setStandardOutput(outPipe)
    task.setStandardError(errPipe)

    task.launch()
    task.waitUntilExit()

    val status = task.terminationStatus

    val outData = outPipe.fileHandleForReading.readDataToEndOfFile()
    val errData = errPipe.fileHandleForReading.readDataToEndOfFile()

    if (status != 0) {
        val errStr = NSString.create(errData, NSUTF8StringEncoding)?.toString() ?: "<no stderr>"
        error("Process failed (exit=$status): $errStr")
    }

    // NSData -> ByteArray
    val len = outData.length.toInt()
    val bytes = ByteArray(len)
    bytes.usePinned { pinned ->
        memcpy(pinned.addressOf(0), outData.bytes, outData.length)
    }
    return bytes
}

@OptIn(
    BetaInteropApi::class,
    ExperimentalForeignApi::class,
)
private fun parseJavaHomePlist(plistBytes: ByteArray): List<JdkInfo> {
    val data = plistBytes.toNSData()

    memScoped {
        val errorPtr = alloc<ObjCObjectVar<NSError?>>()
        val formatPtr = alloc<NSPropertyListFormatVar>()

        val plistObj =
            NSPropertyListSerialization.propertyListWithData(
                data = data,
                options = NSPropertyListMutableContainersAndLeaves,
                format = formatPtr.ptr,
                error = errorPtr.ptr,
            )

        val err = errorPtr.value
        if (err != null) error("Failed to parse plist: ${err.localizedDescription}")

        // java_home -X returns an Array of Dict (each dict is a JDK entry)
        val arr = plistObj as? NSArray ?: return emptyList()

        return (0 until arr.count.toInt()).mapNotNull { i ->
            val dict = arr.objectAtIndex(i.toULong()) as? NSDictionary ?: return@mapNotNull null

            fun NSDictionary.string(key: String): String? =
                (objectForKey(key) as? NSString)?.toString()

            // 常见字段（不同 JDK/系统版本可能略有差异）
            JdkInfo(
                homePath = dict.string("JVMHomePath") ?: return@mapNotNull null,
                version = dict.string("JVMVersion"),
                vendor = dict.string("JVMVendor"),
                name = dict.string("JVMName"),
                arch = dict.string("JVMArch"),
            )
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData =
    usePinned { pinned -> NSData.dataWithBytes(pinned.addressOf(0), size.toULong()) }

fun listJdksViaJavaHome(): List<JdkInfo> {
    val bytes = runProcess("/usr/libexec/java_home", listOf("-X"))
    return parseJavaHomePlist(bytes)
}
