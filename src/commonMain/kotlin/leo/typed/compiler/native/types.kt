package leo.typed.compiler.native

import leo.Type
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

val nativeTypesEnvironment: Environment<Types> get() =
  typesEnvironment { compiled -> compiled.resolveOrNull }

val Compiled<Types>.resolveOrNull: Compiled<Types>? get() =
   when (type) {
     type(numberName) -> nativeTypesEnvironment.staticCompiled(nativeNumberType.script)
     type(textName) -> nativeTypesEnvironment.staticCompiled(nativeTextType.script)
     else -> null
   }

val nativeNumberType: Type get() = type(nativeNumberTypeLine)
val nativeTextType: Type get() = type(nativeTextTypeLine)

val nativeNumberTypeLine: TypeLine get() =
  numberName lineTo type(line(atom(primitive(native(script("kotlin" lineTo script("double")))))))
val nativeTextTypeLine: TypeLine get() =
  textName lineTo type(line(atom(primitive(native(script("kotlin" lineTo script("string")))))))
