package leo.typed.compiler.python

import leo.lineTo
import leo.literal
import leo.script

data class Python(val string: String)
fun python(string: String) = Python(string)

fun python(boolean: Boolean) = python(if (boolean) "True" else "False")
fun python(int: Int) = python(int.toString())
fun python(double: Double) = python(double.toString())
fun stringPython(string: String) = python("'${string.replace("\\", "\\\\").replace("\n", "\\n").replace("'", "\\'")}'")
fun tuplePython(vararg pythons: Python) = python("(" + pythons.joinToString(",") { it.string } + ")")
fun Python.get(python: Python): Python = python("$string[${python.string}]")
fun Python.invoke(vararg pythons: Python) = python("$string${tuplePython(*pythons).string}")
fun Python.ifThenElse(then: Python, elze: Python) = python("(${then.string} if $string else ${elze.string})")
val Python.scriptLine get() = "python" lineTo script(literal(string))