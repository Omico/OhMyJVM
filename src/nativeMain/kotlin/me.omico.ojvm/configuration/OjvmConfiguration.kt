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
package me.omico.ojvm.configuration

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import me.omico.ojvm.utility.exists
import me.omico.ojvm.utility.json
import me.omico.ojvm.utility.readUtf8
import me.omico.ojvm.utility.userHomeDirectory
import me.omico.ojvm.utility.writeUtf8
import okio.Path

@Serializable
data class OjvmConfiguration(
    val jdks: Set<JdkConfiguration> = emptySet(),
)

@Serializable
data class JdkConfiguration(
    val path: String,
    val version: String,
)

inline fun JdkConfiguration.prettyPrint() = buildString {
    appendLine("Path: $path ")
    appendLine("Version: $version ")
}.let(::println)

var ojvmConfiguration: OjvmConfiguration = OjvmConfiguration()

fun loadConfiguration() {
    if (configurationFile.exists()) ojvmConfiguration = json.decodeFromString(configurationFile.readUtf8())
}

fun saveConfiguration(block: (OjvmConfiguration.() -> OjvmConfiguration)? = null) {
    block?.let { ojvmConfiguration = block(ojvmConfiguration) }
    configurationFile.writeUtf8(json.encodeToString(ojvmConfiguration))
}

private val configurationFile: Path by lazy { userHomeDirectory / ".ojvm.json" }
