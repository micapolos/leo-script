package leo.typed.dsl

import leo.typed.Field
import leo.typed.fieldTo
import leo.typed.getOrNull
import leo.typed.structure

typealias X = Field

fun x(name: String, vararg xs: X): X = name fieldTo structure(*xs)
fun X.x(name: String): X = getOrNull(name) ?: x(name, this)

val X.type get() = x("type")

fun definition(vararg xs: X) = x("definition", *xs)