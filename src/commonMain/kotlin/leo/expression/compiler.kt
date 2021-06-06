package leo.expression

import leo.kotlin.Types
import leo.kotlin.types

data class Compiler(val types: Types)

fun compiler() = Compiler(types())