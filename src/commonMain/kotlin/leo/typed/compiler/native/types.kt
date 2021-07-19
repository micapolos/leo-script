package leo.typed.compiler.native

import leo.Type
import leo.TypeLine
import leo.atom
import leo.line
import leo.lineTo
import leo.literal
import leo.native
import leo.numberName
import leo.primitive
import leo.script
import leo.textName
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiler.Environment
import leo.typed.compiler.staticCompiled

val typesNativeEnvironment: Environment<Native> get() =
  nativeEnvironment.let {
    Environment(
      { literal -> nativeEnvironment.literalFn(literal) },
      { compiled -> nativeEnvironment.resolveOrNullFn(compiled) ?: compiled.typedResolveOrNull },
      { native -> nativeEnvironment.scriptLineFn(native) },
      nativeEnvironment.typesNativeEnvironmentFn)
  }

val Compiled<Native>.typedResolveOrNull: Compiled<Native>? get() =
   when (type) {
     type(numberName) -> nativeEnvironment.staticCompiled(nativeNumberType.script)
     type(textName) -> nativeEnvironment.staticCompiled(nativeTextType.script)
     else -> null
   }

val nativeNumberType: Type get() = type(nativeNumberTypeLine)
val nativeTextType: Type get() = type(nativeTextTypeLine)

val nativeNumberTypeLine: TypeLine get() =
  numberName lineTo type(line(atom(primitive(native(script(literal("kotlin.Double")))))))
val nativeTextTypeLine: TypeLine get() =
  textName lineTo type(line(atom(primitive(native(script(literal("kotlin.String")))))))
