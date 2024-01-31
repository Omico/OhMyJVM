/*
 * Oh My JVM - A JDK version manager written in Kotlin
 *
 * Copyright (C) 2023-2024 Omico
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
import kotlinx.serialization.encodeToString
import me.omico.ojvm.utility.createDirectories
import me.omico.ojvm.utility.delete
import me.omico.ojvm.utility.exists
import me.omico.ojvm.utility.json
import me.omico.ojvm.utility.ojvmConfigurationFile
import me.omico.ojvm.utility.ojvmDirectory
import me.omico.ojvm.utility.readUtf8
import me.omico.ojvm.utility.writeUtf8

@Serializable
data class OjvmConfiguration(
    val currentJdk: JdkConfiguration? = null,
    val jdks: Set<JdkConfiguration> = emptySet(),
) {
    companion object {
        val Empty = OjvmConfiguration()
    }
}

@Serializable
data class JdkConfiguration(
    val path: String,
    val version: String,
    val alias: String? = null,
)

inline fun JdkConfiguration.prettyPrint() = buildString {
    alias?.let { appendLine("Alias: $it ") }
    appendLine("Path: $path ")
    appendLine("Version: $version ")
}.let(::println)

var ojvmConfiguration: OjvmConfiguration = OjvmConfiguration.Empty
    private set

fun loadConfiguration() {
    if (!ojvmConfigurationFile.exists()) return
    runCatching { json.decodeFromString<OjvmConfiguration>(ojvmConfigurationFile.readUtf8()) }
        .onFailure { ojvmConfigurationFile.delete() }
        .getOrDefault(OjvmConfiguration.Empty)
}

fun saveConfiguration(block: (OjvmConfiguration.() -> OjvmConfiguration)? = null) {
    block?.let { ojvmConfiguration = block(ojvmConfiguration) }
    ojvmDirectory.createDirectories()
    ojvmConfigurationFile.writeUtf8(json.encodeToString(ojvmConfiguration))
}
