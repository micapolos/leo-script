package leo.typed.compiler.python

import leo.ScriptLine
import leo.TypeLine
import leo.Types
import leo.atom
import leo.line
import leo.lineTo
import leo.native
import leo.numberName
import leo.primitive
import leo.script
import leo.textName
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiler.Environment
import leo.typed.compiler.native.resolveOrNull
import leo.typed.compiler.staticCompiled
import leo.typed.compiler.types.typesEnvironment

val pythonTypesEnvironment: Environment<Types>
  get() = typesEnvironment { compiled -> compiled.resolveOrNull }

val Compiled<Types>.resolveOrNull: Compiled<Types>? get() =
  when (type) {
    type(numberName) -> pythonTypesEnvironment.staticCompiled(pythonNumberType.script)
    type(textName) -> pythonTypesEnvironment.staticCompiled(pythonTextType.script)
    else -> null
  }

val pythonTextType get() = type(pythonTextTypeLine)
val pythonNumberType get() = type(pythonNumberTypeLine)

val pythonTextTypeLine get() = textName lineTo type(line(atom(primitive(native(script("python" lineTo script("string")))))))
val pythonNumberTypeLine get() = numberName lineTo type(line(atom(primitive(native(script("python" lineTo script("number")))))))


val TypeLine.scriptLineOrNull: ScriptLine? get() =
  when (this) {
    pythonNumberTypeLine -> numberName lineTo script()
    pythonTextTypeLine -> textName lineTo script()
    else -> null
  }