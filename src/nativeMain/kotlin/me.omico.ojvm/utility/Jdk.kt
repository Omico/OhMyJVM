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
import okio.Path

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
