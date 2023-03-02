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
package me.omico.ojvm.command

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.DefaultRequiredType
import kotlinx.cli.SingleOption
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import me.omico.ojvm.configuration.JdkConfiguration
import me.omico.ojvm.configuration.ojvmConfiguration
import me.omico.ojvm.configuration.saveConfiguration
import me.omico.ojvm.utility.JdkVersion
import me.omico.ojvm.utility.description
import me.omico.ojvm.utility.exists
import me.omico.ojvm.utility.fileVersion
import me.omico.ojvm.utility.isDirectory
import me.omico.ojvm.utility.javaExecutable
import me.omico.ojvm.utility.list
import okio.Path
import okio.Path.Companion.toPath

object Add : Subcommand("add", "Add JDK(s)") {
    private val paths by paths(
        description = description {
            line()
            line("The JDK path(s) to add.")
            line("You can also use a path that contains multiple JDKs in its sub-path.")
        },
    )
    private val depth by depth()
    private val jdks = mutableSetOf<JdkConfiguration>()

    override fun execute() {
        paths
            .map { it.toPath() }
            .forEach {
                when (depth) {
                    -1 -> detectJdkPaths(it, Int.MAX_VALUE)
                    else -> detectJdkPaths(it, depth)
                }
            }
        val currentJdkPaths = ojvmConfiguration.jdks.map(JdkConfiguration::path)
        val newJdkPaths = jdks.filter { it.path !in currentJdkPaths }
        if (newJdkPaths.isEmpty()) {
            println("Skipping, no new JDK detected.")
            println("You can use `ojvm list` to check the current JDKs.")
            return
        }
        newJdkPaths.forEach { jdk -> println("Added JDK: ${jdk.path}") }
        saveConfiguration {
            val newJdks = (this.jdks + newJdkPaths)
                .sortedBy { JdkVersion.parse(it.version) }
                .toSet()
            copy(jdks = newJdks)
        }
    }

    private fun detectJdkPaths(path: Path, depthRemain: Int) {
        path.list()
            .filter(Path::isDirectory)
            .forEach { detectJdkPaths(it, depthRemain - 1) }
        if (depthRemain != 0) return
        if (!path.javaExecutable.exists()) return
        path.javaExecutable.fileVersion()?.let { version ->
            JdkConfiguration(
                path = path.toString(),
                version = version,
            ).also(jdks::add)
        }
    }
}

private inline fun ArgParser.depth(): SingleOption<Int, DefaultRequiredType.Default> = this
    .option(
        type = ArgType.Int,
        shortName = "d",
        description = description {
            line()
            line("The depth of the sub-path to search for JDKs.")
            line("Default: 0, which means only searching the current provide path.")
            line("If you want to search all the subdirectories, you can set the value to -1.")
        },
    )
    .default(0)
