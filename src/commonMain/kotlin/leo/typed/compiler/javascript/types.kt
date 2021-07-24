package leo.typed.compiler.javascript

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

val javascriptTypesEnvironment: Environment<Types>
  get() = typesEnvironment { compiled -> compiled.resolveOrNull }

val Compiled<Types>.resolveOrNull: Compiled<Types>? get() =
  when (type) {
    type(numberName) -> javascriptTypesEnvironment.staticCompiled(javascriptNumberType.script)
    type(textName) -> javascriptTypesEnvironment.staticCompiled(javascriptTextType.script)
    else -> null
  }

val javascriptTextType get() = type(javascriptTextTypeLine)
val javascriptNumberType get() = type(javascriptNumberTypeLine)

val javascriptTextTypeLine get() = textName lineTo type(line(atom(primitive(native(script("javascript" lineTo script("string")))))))
val javascriptNumberTypeLine get() = numberName lineTo type(line(atom(primitive(native(script("javascript" lineTo script("number")))))))

val TypeLine.scriptLineOrNull: ScriptLine? get() =
  when (this) {
    javascriptNumberTypeLine -> numberName lineTo script()
    javascriptTextTypeLine -> textName lineTo script()
    else -> null
  }