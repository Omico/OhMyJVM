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
package me.omico.ojvm.command

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.DefaultRequiredType
import kotlinx.cli.SingleArgument
import kotlinx.cli.Subcommand
import me.omico.ojvm.configuration.ojvmConfiguration
import me.omico.ojvm.utility.linkAsCurrent

object UseCommand : Subcommand("use", "Use a specific JDK") {
    private val pathOrAlias by pathOrAlias()

    override fun execute() {
        when (val jdk = ojvmConfiguration.jdks.find { it.path == pathOrAlias || it.alias == pathOrAlias }) {
            null -> println("JDK not found.")
            else -> jdk.linkAsCurrent()
        }
    }
}

private fun ArgParser.pathOrAlias(): SingleArgument<String, DefaultRequiredType.Required> = this
    .argument(
        type = ArgType.String,
        description = "The JDK path or alias.",
    )
