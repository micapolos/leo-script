package leo.term.compiler.python

import leo.Literal
import leo.NumberLiteral
import leo.StringLiteral
import leo.lineTo
import leo.literal
import leo.script

data class Python(val string: String)
val String.python: Python get() = Python(this)

val Literal.python: Python get() =
  when (this) {
    is NumberLiteral -> toString().python
    is StringLiteral -> ("'" + string.replace("\n", "\\n").replace("'", "\\'") + "'").python
  }

val idPython = "(lambda x: x)".python
val truePython = "(lambda f0: lambda f1: f0${idPython.string})".python
val falsePython = "(lambda f0: lambda f1: f1${idPython.string})".python
val Python.boolean: Python get() = "(${truePython.string} if $string else ${falsePython.string})".python

val Python.scriptLine get() = "python" lineTo script(literal(string))