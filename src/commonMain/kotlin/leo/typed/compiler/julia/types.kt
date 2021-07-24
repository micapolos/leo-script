package leo.typed.compiler.julia

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
import leo.typed.compiler.staticCompiled
import leo.typed.compiler.types.typesEnvironment

val juliaTypesEnvironment: Environment<Types>
  get() = typesEnvironment { compiled -> compiled.resolveOrNull }

val Compiled<Types>.resolveOrNull: Compiled<Types>? get() =
  when (type) {
    type(numberName) -> juliaTypesEnvironment.staticCompiled(juliaNumberType.script)
    type(textName) -> juliaTypesEnvironment.staticCompiled(juliaTextType.script)
    else -> null
  }

val juliaTextType get() = type(juliaTextTypeLine)
val juliaNumberType get() = type(juliaNumberTypeLine)

val juliaTextTypeLine get() = textName lineTo type(line(atom(primitive(native(script("julia" lineTo script("string")))))))
val juliaNumberTypeLine get() = numberName lineTo type(line(atom(primitive(native(script("julia" lineTo script("number")))))))

val TypeLine.scriptLineOrNull: ScriptLine? get() =
  when (this) {
    juliaNumberTypeLine -> numberName lineTo script()
    juliaTextTypeLine -> textName lineTo script()
    else -> null
  }