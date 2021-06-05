package leo.kotlin

import leo.base.titleCase

data class Name(val string: String, val version: Int)
val Name.kotlinClassName get() = "${string.titleCase}${versionString}"
val Name.kotlinFieldName get() = "$string"
val Name.versionString: String get() = if (version == 1) "" else "$version"
