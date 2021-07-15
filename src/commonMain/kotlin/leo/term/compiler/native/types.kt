package leo.term.compiler.native

import leo.anyName
import leo.numberName
import leo.term.compiled.Compiled
import leo.term.compiled.compiled
import leo.term.compiled.lineTo
import leo.term.compiler.Environment
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
     type(numberName) -> compiled(anyName lineTo compiled(numberName))
     type(textName) -> compiled(anyName lineTo compiled(textName))
     else -> null
   }
