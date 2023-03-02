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
@file:Suppress(
    "FunctionName",
    "SpellCheckingInspection",
)

package me.omico.ojvm.utility

import platform.windows.DWORD
import platform.windows.WORD

fun HIWORD(value: DWORD): WORD = ((value and 0xFFFF0000u) shr 16).toUShort()

fun LOWORD(value: DWORD): WORD = (value and 0xFFFFu).toUShort()
