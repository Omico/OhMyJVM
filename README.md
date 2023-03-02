# Oh My JVM

[![GitHub License](https://img.shields.io/github/license/Omico/OhMyJVM?style=for-the-badge)](https://www.gnu.org/licenses/gpl-3.0.html)

## Overview

Oh My JVM is inspired by [nvm-windows](https://github.com/coreybutler/nvm-windows). It is a command-line tool for
Windows to manage multiple JDKs written in [Kotlin Native](https://kotlinlang.org/docs/native-overview.html).

## Usage

Add `%USERPROFILE%\.ojvm\jdk\current` to your `PATH` environment variable.

Assume you have the following JDKs being installed. In `C:\Program Files\Eclipse Adoptium`:

```text
.
├── jdk-11.0.17.8-hotspot
├── jdk-17.0.6.10-hotspot
├── jdk-18.0.2.101-hotspot
└── jdk-8.0.362.9-hotspot
```

```shell
ojvm add "C:\Program Files\Eclipse Adoptium" --depth 1
```

```text
Added JDK: C:\Program Files\Eclipse Adoptium\jdk-11.0.17.8-hotspot
Added JDK: C:\Program Files\Eclipse Adoptium\jdk-17.0.6.10-hotspot
Added JDK: C:\Program Files\Eclipse Adoptium\jdk-18.0.2.101-hotspot
Added JDK: C:\Program Files\Eclipse Adoptium\jdk-8.0.362.9-hotspot
```

Now, you can switch different JDKs without refresh your environments.

```text
ojvm use "C:\Program Files\Eclipse Adoptium\jdk-11.0.17.8-hotspot"

java -version
openjdk version "11.0.17" 2022-10-18
OpenJDK Runtime Environment Temurin-11.0.17+8 (build 11.0.17+8)
OpenJDK 64-Bit Server VM Temurin-11.0.17+8 (build 11.0.17+8, mixed mode)

ojvm use "C:\Program Files\Eclipse Adoptium\jdk-17.0.6.10-hotspot"

java -version
openjdk version "17.0.6" 2023-01-17
OpenJDK Runtime Environment Temurin-17.0.6+10 (build 17.0.6+10)
OpenJDK 64-Bit Server VM Temurin-17.0.6+10 (build 17.0.6+10, mixed mode, sharing)
```
