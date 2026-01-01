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

import kotlinx.cli.ArgParser
import me.omico.ojvm.command.AddCommand
import me.omico.ojvm.command.ListCommand
import me.omico.ojvm.command.RemoveCommand
import me.omico.ojvm.command.UseCommand
import me.omico.ojvm.configuration.loadConfiguration
import me.omico.ojvm.utility.discoverJdks
import me.omico.ojvm.utility.relinkJdkAfterUpgrade

actual fun main(arguments: Array<String>) {
    loadConfiguration()
    discoverJdks()
    relinkJdkAfterUpgrade()
    val parser = ArgParser("ojvm")
    parser.subcommands(
        AddCommand,
        ListCommand,
        RemoveCommand,
        UseCommand,
    )
    when {
        arguments.isEmpty() -> arrayOf("--help")
        else -> arguments
    }.let(parser::parse)
}
