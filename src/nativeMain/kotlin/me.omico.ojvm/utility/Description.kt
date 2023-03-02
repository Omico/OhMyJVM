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

fun description(block: DescriptionScope.() -> Unit): String =
    DescriptionBuilder().apply(block).toString()

interface DescriptionScope {
    fun line(content: String = "")
}

private class DescriptionBuilder : DescriptionScope {
    private val builder = StringBuilder()
    private val indent: String = "        "
    private var isFirstLine = true

    override fun line(content: String) {
        when {
            isFirstLine -> builder.appendLine(content).also { isFirstLine = false }
            else -> builder.append(indent).appendLine(content)
        }
    }

    override fun toString() = builder.append(indent).toString()
}
