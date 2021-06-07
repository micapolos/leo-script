package leo.type.compiler

import leo.Script
import leo.ScriptLine
import leo.Type
import leo.TypeLiteral
import leo.anyName
import leo.lineTo
import leo.literal
import leo.numberName
import leo.script
import leo.textName
import leo.typeNumber
import leo.typeText

val Script.type: Type get() =
	context().type(this)

val textTypeScriptLine get() = textName lineTo script(anyName lineTo script())
val numberTypeScriptLine get() = numberName lineTo script(anyName lineTo script())

val ScriptLine.typeLiteralOrNull: TypeLiteral? get() =
	when (this) {
		numberTypeScriptLine -> literal(typeNumber)
		textTypeScriptLine -> literal(typeText)
		else -> null
	}

