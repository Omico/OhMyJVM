# Oh My JVM

[![GitHub License](https://img.shields.io/github/license/Omico/OhMyJVM?style=for-the-badge)](https://www.gnu.org/licenses/gpl-3.0.html)

## Overview

TL;DR: The benefit of Oh My JVM is you can switch different JDKs without refreshing your environments.

Oh My JVM is a command-line tool for
Windows to manage multiple JDKs written in [Kotlin Native](https://kotlinlang.org/docs/native-overview.html) inspired by [nvm-windows](https://github.com/coreybutler/nvm-windows).

## Usage

Add or create `JAVA_HOME` with `%USERPROFILE%\.ojvm\jdk\current` to your system or user environment variables.

Add `%JAVA_HOME%\bin` to your system or user `PATH` environment variable.

Make sure you have cleaned your environment variables and let Oh My JVM to manage your JDKs.

### Out-of-box

Oh My JVM can automatically detect and add supported JDKs.

Officially Supported JDKs

- [Eclipse Temurin](https://adoptium.net)
- [Zulu](https://www.azul.com/downloads)

If you want to add other JDKs, please see [Manually add and use JDKs](#manually-add-and-use-jdks).

If you want to help us to support more JDKs, please create an issue or pull request.

Assume you have the following JDKs being installed. In `C:\Program Files\Eclipse Adoptium`:

```text
.
├── jdk-11.0.17.8-hotspot
├── jdk-17.0.6.10-hotspot
├── jdk-18.0.2.101-hotspot
└── jdk-8.0.362.9-hotspot
```

Oh My JVM has automatically added those JDKs. Run `ojvm list` to list them.

```text
Alias: temurin-11.0.17.8
Path: C:\Program Files\Eclipse Adoptium\jdk-11.0.17.8-hotspot
Version: 11.0.17.8

Alias: temurin-17.0.6.10
Path: C:\Program Files\Eclipse Adoptium\jdk-17.0.6.10-hotspot
Version: 17.0.6.10

Alias: temurin-18.0.2.101
Path: C:\Program Files\Eclipse Adoptium\jdk-18.0.2.101-hotspot
Version: 18.0.2.101

Alias: temurin-8.0.362.9
Path: C:\Program Files\Eclipse Adoptium\jdk-8.0.362.9-hotspot
Version: 8.0.362.9
```

#### Switch JDK

Now, you can switch different JDKs.

```text
ojvm use temurin-11.0.17.8 

java -version
openjdk version "11.0.17" 2022-10-18
OpenJDK Runtime Environment Temurin-11.0.17+8 (build 11.0.17+8)
OpenJDK 64-Bit Server VM Temurin-11.0.17+8 (build 11.0.17+8, mixed mode)

# Or you can use the full path
ojvm use "C:\Program Files\Eclipse Adoptium\jdk-17.0.6.10-hotspot"

java -version
openjdk version "17.0.6" 2023-01-17
OpenJDK Runtime Environment Temurin-17.0.6+10 (build 17.0.6+10)
OpenJDK 64-Bit Server VM Temurin-17.0.6+10 (build 17.0.6+10, mixed mode, sharing)
```

### Manually add and use JDKs

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

Go to [Switch JDK](#switch-jdk) to do the next step.
