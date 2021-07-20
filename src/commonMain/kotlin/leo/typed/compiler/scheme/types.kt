package leo.typed.compiler.scheme

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
import leo.typed.compiler.types.typesTypesEnvironment

val schemeTypesEnvironment: Environment<Types>
  get() =
    Environment(
      { literal -> error("") },
      { compiled -> compiled.resolveOrNull },
      { native -> error("") },
      { typesTypesEnvironment })

val Compiled<Types>.resolveOrNull: Compiled<Types>? get() =
  when (type) {
    type(numberName) -> schemeTypesEnvironment.staticCompiled(schemeNumberType.script)
    type(textName) -> schemeTypesEnvironment.staticCompiled(schemeTextType.script)
    else -> null
  }

val schemeTextType get() = type(schemeTextTypeLine)
val schemeNumberType get() = type(schemeNumberTypeLine)

val schemeTextTypeLine get() = textName lineTo type(line(atom(primitive(native(script("scheme" lineTo script("string")))))))
val schemeNumberTypeLine get() = numberName lineTo type(line(atom(primitive(native(script("scheme" lineTo script("number")))))))