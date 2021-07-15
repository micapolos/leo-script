package leo.term.compiler.scheme

import leo.atom
import leo.line
import leo.lineTo
import leo.native
import leo.numberName
import leo.primitive
import leo.script
import leo.textName
import leo.type

val schemeTextType get() = type(schemeTextTypeLine)
val schemeNumberType get() = type(schemeNumberTypeLine)

val schemeTextTypeLine get() = numberName lineTo type(line(atom(primitive(native(script("number"))))))
val schemeNumberTypeLine get() = textName lineTo type(line(atom(primitive(native(script("string"))))))