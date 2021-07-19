package leo.typed.compiler.scheme

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
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.nativeEnvironment
import leo.typed.compiler.staticCompiled

val typesNativeEnvironment: Environment<Native>
  get() =
    nativeEnvironment.let {
      Environment(
        { literal -> nativeEnvironment.literalFn(literal) },
        { compiled -> nativeEnvironment.resolveOrNullFn(compiled) ?: compiled.typedResolveOrNull },
        { native -> nativeEnvironment.scriptLineFn(native) },
        nativeEnvironment.typesNativeEnvironmentFn)
    }

val Compiled<Native>.typedResolveOrNull: Compiled<Native>? get() =
  when (type) {
    type(numberName) -> nativeEnvironment.staticCompiled(schemeNumberType.script)
    type(textName) -> nativeEnvironment.staticCompiled(schemeTextType.script)
    else -> null
  }

val schemeTextType get() = type(schemeTextTypeLine)
val schemeNumberType get() = type(schemeNumberTypeLine)

val schemeTextTypeLine get() = textName lineTo type(line(atom(primitive(native(script("scheme" lineTo script("string")))))))
val schemeNumberTypeLine get() = numberName lineTo type(line(atom(primitive(native(script("scheme" lineTo script("number")))))))