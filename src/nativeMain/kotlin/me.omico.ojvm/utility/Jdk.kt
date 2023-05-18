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

import kotlinx.serialization.Serializable
import me.omico.ojvm.configuration.JdkConfiguration
import me.omico.ojvm.configuration.ojvmConfiguration
import me.omico.ojvm.configuration.saveConfiguration
import okio.Path
import platform.windows.CreateSymbolicLinkW
import platform.windows.GetLastError
import platform.windows.SYMBOLIC_LINK_FLAG_DIRECTORY

inline val Path.javaExecutable
    get() = this / "bin" / "java.exe"

@Serializable
data class JdkVersion(
    val major: Int,
    val minor: Int,
    val update: Int,
    val build: Int,
) : Comparable<JdkVersion> {
    override fun compareTo(other: JdkVersion): Int = when {
        major != other.major -> major.compareTo(other.major)
        minor != other.minor -> minor.compareTo(other.minor)
        update != other.update -> update.compareTo(other.update)
        else -> build.compareTo(other.build)
    }

    companion object {
        fun parse(version: String): JdkVersion = run {
            val versionStrings = version.split(".")
            require(versionStrings.size == 4) { "Cannot parse version: $version" }
            JdkVersion(
                major = versionStrings[0].toInt(),
                minor = versionStrings[1].toInt(),
                update = versionStrings[2].toInt(),
                build = versionStrings[3].toInt(),
            )
        }
    }
}

val jdkVendors = listOf(
    JdkVendor.Adoptium,
    JdkVendor.Zulu,
)

data class JdkVendor(
    val id: String,
    val installationLocations: List<String>,
    val versionRegex: Regex? = null,
    val useExecutableVersion: Boolean = false,
) {
    companion object {
        val Adoptium = JdkVendor(
            id = "temurin",
            installationLocations = listOf(
                "C:\\Program Files\\Eclipse Adoptium",
                "C:\\Program Files (x86)\\Eclipse Adoptium",
            ),
            versionRegex = "jdk-([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)-hotspot".toRegex(),
        )
        val Zulu = JdkVendor(
            id = "zulu",
            installationLocations = listOf(
                "C:\\Program Files\\Zulu",
                "C:\\Program Files (x86)\\Zulu",
            ),
            versionRegex = "zulu-([0-9]+)".toRegex(),
            useExecutableVersion = true,
        )
    }
}

fun discoverJdks() {
    val jdks = mutableSetOf<JdkConfiguration>()
    jdkVendors.forEach { vendor ->
        vendor.installationLocations
            .map(String::toPath)
            .filter(Path::exists)
            .forEach { path -> jdks.detectJdks(path, 1, vendor) }
    }
    val newJdkPaths = jdks.map(JdkConfiguration::path)
    ojvmConfiguration.jdks
        .filter { it.path.toPath().exists() }
        .forEach { jdk ->
            if (jdk.path in newJdkPaths) return@forEach
            jdks.add(jdk)
        }
    saveConfiguration {
        copy(jdks = jdks)
    }
}

fun MutableSet<JdkConfiguration>.detectJdks(path: Path, depthRemain: Int, vendor: JdkVendor? = null) {
    path.list()
        .filter(Path::isDirectory)
        .forEach { detectJdks(it, depthRemain - 1, vendor) }
    if (depthRemain != 0) return
    if (!path.javaExecutable.exists()) return
    path.javaExecutable.fileVersion()?.let { fileVersion ->
        val version = vendor?.versionRegex?.matchEntire(path.name)?.groupValues?.get(1) ?: fileVersion
        JdkConfiguration(
            path = path.toString(),
            version = when {
                vendor != null && vendor.useExecutableVersion -> fileVersion
                else -> version
            },
            alias = when {
                vendor != null -> "${vendor.id}-$version"
                else -> path.name
            },
        ).also(::add)
    }
}

fun relinkJdkAfterUpgrade() {
    val currentJdk = ojvmConfiguration.currentJdk ?: return
    val jdk = ojvmConfiguration.jdks.find { it.alias == currentJdk.alias }
    when {
        jdk == null -> saveConfiguration {
            ojvmCurrentJdkDirectory.delete()
            println("Current JDK is not found, please use `ojvm use` to select a JDK.")
            copy(currentJdk = null)
        }
        jdk.path != currentJdk.path -> jdk.linkAsCurrent()
    }
}

fun JdkConfiguration.linkAsCurrent() {
    println("Using JDK: $path")
    ojvmJdkDirectory.createDirectories() // Make sure the parent directory exists.
    ojvmCurrentJdkDirectory.delete()
    val fail = CreateSymbolicLinkW(ojvmCurrentJdkDirectory.toString(), path, SYMBOLIC_LINK_FLAG_DIRECTORY).toInt() == 0
    if (fail) println("Failed to create symbolic link: ${GetLastError()}")
    saveConfiguration {
        copy(currentJdk = if (fail) null else this@linkAsCurrent)
    }
}
