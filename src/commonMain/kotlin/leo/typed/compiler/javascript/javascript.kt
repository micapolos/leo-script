package leo.typed.compiler.javascript

import leo.lineTo
import leo.literal
import leo.script

data class Javascript(val string: String)
fun javascript(string: String) = Javascript(string)

fun javascript(boolean: Boolean) = javascript(if (boolean) "true" else "false")
fun javascript(int: Int) = javascript(int.toString())
fun javascript(double: Double) = javascript(double.toString())
fun stringJavascript(string: String) = javascript("'${string.replace("\\", "\\\\").replace("\n", "\\n").replace("'", "\\'")}'")
fun tupleJavascript(vararg javascripts: Javascript) = javascript("(" + javascripts.joinToString(",") { it.string } + ")")
fun arrayJavascript(vararg javascripts: Javascript) = javascript("[" + javascripts.joinToString(",") { it.string } + "]")
fun Javascript.get(javascript: Javascript): Javascript = javascript("$string[${javascript.string}]")
fun Javascript.invoke(vararg javascripts: Javascript) = javascript("$string${tupleJavascript(*javascripts).string}")
fun Javascript.ifThenElse(then: Javascript, elze: Javascript) = javascript("($string?${then.string}:${elze.string})")
val Javascript.scriptLine get() = "javascript" lineTo script(literal(string))