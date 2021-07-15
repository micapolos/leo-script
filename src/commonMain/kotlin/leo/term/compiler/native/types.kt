package leo.term.compiler.native

import leo.Type
import leo.TypeLine
import leo.atom
import leo.line
import leo.lineTo
import leo.native
import leo.numberName
import leo.primitive
import leo.script
import leo.term.compiled.Compiled
import leo.term.compiler.Environment
import leo.term.compiler.staticCompiled
import leo.textName
import leo.type

val typesNativeEnvironment: Environment<Native> get() =
  nativeEnvironment.let {
    Environment(
      { literal -> nativeEnvironment.literalFn(literal) },
      { compiled -> nativeEnvironment.resolveOrNullFn(compiled) ?: compiled.typedResolveOrNull },
      { native -> nativeEnvironment.scriptLineFn(native) })
  }

val Compiled<Native>.typedResolveOrNull: Compiled<Native>? get() =
   when (type) {
     type(numberName) -> nativeEnvironment.staticCompiled(nativeNumberType.script)
     type(textName) -> nativeEnvironment.staticCompiled(nativeTextType.script)
     else -> null
   }

val nativeNumberType: Type get() = type(nativeNumberTypeLine)
val nativeTextType: Type get() = type(nativeTextTypeLine)

val nativeNumberTypeLine: TypeLine get() = numberName lineTo type(line(atom(primitive(native(script("double"))))))
val nativeTextTypeLine: TypeLine get() = textName lineTo type(line(atom(primitive(native(script("string"))))))
