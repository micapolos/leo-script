package leo.typed.compiler.julia

import leo.lineTo
import leo.literal
import leo.script

data class Julia(val string: String)
fun julia(string: String) = Julia(string)

fun julia(boolean: Boolean) = julia(if (boolean) "true" else "false")
fun julia(int: Int) = julia(int.toString())
fun julia(double: Double) = julia(double.toString())
fun stringJulia(string: String) = julia("\"${string.replace("\\", "\\\\").replace("\n", "\\n").replace("\"", "\\\"")}\"")
fun tupleJulia(vararg julias: Julia) = julia("(" + julias.joinToString(",") { it.string } + ")")
fun Julia.get(julia: Julia): Julia = julia("$string[${julia.string}]")
fun Julia.invoke(vararg julias: Julia) = julia("$string${tupleJulia(*julias).string}")
fun Julia.ifThenElse(then: Julia, elze: Julia) = julia("($string?${then.string}:${elze.string})")
val Julia.scriptLine get() = "julia" lineTo script(literal(string))