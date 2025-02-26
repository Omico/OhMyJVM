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
package me.omico.ojvm.utility

import kotlinx.io.files.Path

val ojvmDirectory: Path by lazy { Path(userHomeDirectory, ".ojvm") }
val ojvmJdkDirectory: Path by lazy { Path(ojvmDirectory, "jdks") }
val ojvmCurrentJdkDirectory: Path by lazy { Path(ojvmJdkDirectory, "current") }

val ojvmConfigurationFile: Path by lazy { Path(ojvmDirectory, "config.json") }
