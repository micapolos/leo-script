package leo.expression

import leo.Literal
import leo.kotlin.Kotlin
import leo.kotlin.kotlin
import leo.literal

typealias Dsl = Compilation<Kotlin>

val Literal.dsl: Dsl get() = toString().kotlin.compilation
val String.dsl: Dsl get() = literal.dsl
val Int.dsl: Dsl get() = literal.dsl
val Double.dsl: Dsl get() = literal.dsl
