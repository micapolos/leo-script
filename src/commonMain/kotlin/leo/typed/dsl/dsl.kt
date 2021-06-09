package leo.typed.dsl

import leo.typed.Field
import leo.typed.fieldTo
import leo.typed.getOrNull
import leo.typed.numberField
import leo.typed.structure
import leo.typed.textField

typealias X = Field

fun x(name: String, vararg xs: X): X = name fieldTo structure(*xs)
fun X.x(name: String): X = getOrNull(name) ?: x(name, this)
fun _dsl(vararg xs: X) = structure(*xs)

fun number(double: Double): X = double.numberField
fun number(int: Int): X = int.numberField
fun text(string: String): X = string.textField

val X.type get() = x("type")

fun context(vararg xs: X) = x("context", *xs)
fun definition(vararg xs: X) = x("definition", *xs)
fun get(vararg xs: X) = x("get", *xs)
fun point(vararg xs: X) = x("point", *xs)
fun x(vararg xs: X) = x("x", *xs)
fun y(vararg xs: X) = x("y", *xs)
