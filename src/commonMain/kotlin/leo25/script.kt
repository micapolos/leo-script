package leo25

import leo25.base.Seq
import leo25.base.SeqNode
import leo25.base.emptySeq
import leo25.base.fold
import leo25.base.ifOrNull
import leo25.base.notNullIf
import leo25.base.seq
import leo25.base.then
import leo13.Stack
import leo13.fold
import leo13.push
import leo13.reverse
import leo13.stack
import leo25.string

sealed class Script {
	override fun toString() = this.string
}

data class UnitScript(val unit: Unit) : Script() {
	override fun toString() = super.toString()
}

data class LinkScript(val link: ScriptLink) : Script() {
	override fun toString() = super.toString()
}

sealed class ScriptLine

data class LiteralScriptLine(val literal: Literal) : ScriptLine() {
	override fun toString() = super.toString()
}

data class FieldScriptLine(val field: ScriptField) : ScriptLine() {
	override fun toString() = super.toString()
}

data class ScriptLink(val lhs: Script, val line: ScriptLine)

data class ScriptField(val string: String, val rhs: Script)

fun script(unit: Unit): Script = UnitScript(unit)
fun script(literal: Literal): Script = script(line(literal))
fun script(link: ScriptLink): Script = LinkScript(link)
fun line(string: String): ScriptLine = line(field(string))
fun line(literal: Literal): ScriptLine = LiteralScriptLine(literal)
fun line(field: ScriptField): ScriptLine = FieldScriptLine(field)
fun Script.plus(vararg lines: ScriptLine) = fold(lines) { LinkScript(this linkTo it) }
fun Script.plus(field: ScriptField, vararg fields: ScriptField): Script = plus(line(field)).fold(fields) { plus(it) }
fun script(vararg lines: ScriptLine): Script = script(Unit).plus(*lines)
val List<ScriptLine>.script get() = script(*toTypedArray())
fun script(string: String, vararg strings: String) = script(field(string)).fold(strings) { plus(field(it)) }
fun script(field: ScriptField, vararg fields: ScriptField): Script =
	script(line(field)).fold(fields) { plus(line(it)) }
val ScriptLine.script get() = script(this)

val emptyScript: Script = UnitScript(Unit)
val String.scriptLine get() = line(this)

fun link(line: ScriptLine, vararg lines: ScriptLine) =
	ScriptLink(script(), line).fold(lines) { ScriptLink(script(this), it) }

val Literal.line get() = line(this)

fun Script.plus(script: Script): Script =
	when (script) {
		is UnitScript -> this
		is LinkScript -> plus(script.link.lhs).plus(script.link.line)
	}

infix fun String.fieldTo(rhs: Script) = ScriptField(this, rhs)
infix fun String.fieldTo(line: ScriptLine) = fieldTo(script(line))
infix fun String.fieldTo(literal: Literal) = fieldTo(script(literal))
fun field(string: String) = string fieldTo script()
infix fun Script.linkTo(line: ScriptLine) = ScriptLink(this, line)
infix fun String.lineTo(script: Script) = line(fieldTo(script))
val String.line: ScriptLine get() = this lineTo script()
val Stack<ScriptLine>.script get() = emptyScript.fold(reverse) { plus(it) }

val Script.isEmpty get() = (this is UnitScript)
val Script.linkOrNull get() = (this as? LinkScript)?.link

val Script.isWord
	get() =
		when (this) {
			is UnitScript -> false
			is LinkScript -> link.isWord
		}

val ScriptLink.isWord
	get() =
		lhs.isEmpty && line.isWord

val ScriptLine.isWord
	get() =
		when (this) {
			is LiteralScriptLine -> true
			is FieldScriptLine -> field.rhs.isEmpty
		}

val Script.isSimple: Boolean
	get() =
		when (this) {
			is UnitScript -> true
			is LinkScript -> link.isSimple
		}

val ScriptLink.isSimple
	get() =
		lhs.isSimple && line.isSimple

val ScriptLine.isSimple
	get() =
		when (this) {
			is LiteralScriptLine -> true
			is FieldScriptLine -> field.isSimple
		}

val Script.isSingleLine: Boolean
	get() =
		when (this) {
			is UnitScript -> true
			is LinkScript -> link.isSingleLine
		}

val ScriptLink.isSingleLine
	get() =
		lhs.isEmpty

val Script.hasWordsOnly: Boolean
	get() =
		when (this) {
			is UnitScript -> true
			is LinkScript -> link.hasWordsOnly
		}

val ScriptLink.hasWordsOnly
	get() =
		lhs.hasWordsOnly && line.hasWordsOnly

val ScriptLine.hasWordsOnly
	get() =
		when (this) {
			is LiteralScriptLine -> true
			is FieldScriptLine -> field.rhs.isEmpty
		}

val ScriptField.isSimple get() = rhs.isEmpty

val String.code get() = "\"$this\"" // TODO: Escape

val Script.code: String
	get() =
	when (this) {
		is UnitScript -> ""
		is LinkScript -> link.code
	}

val ScriptLine.code
	get() =
	when (this) {
		is LiteralScriptLine -> literal.code
		is FieldScriptLine -> field.code
	}

val Literal.code
	get() =
		when (this) {
			is StringLiteral -> string.code
			is NumberLiteral -> number.code
		}

val ScriptLink.code
	get() =
		if (lhs is UnitScript) line.code
		else "${lhs.code}.${line.code}"

val ScriptField.code
	get() =
		"$string(${rhs.code})"

fun <V> Stack<V>.script(fn: V.() -> ScriptLine): Script =
	script().fold(reverse) { plus(it.fn()) }

// == Core string

val Script.coreString: String
	get() =
		when (this) {
			is UnitScript -> ""
			is LinkScript -> link.coreString
		}

val ScriptLink.coreString: String
	get() =
		if (lhs.isEmpty) line.coreString
		else "${lhs.coreString} ${line.coreString}"

val ScriptLine.coreString: String
	get() =
		when (this) {
			is LiteralScriptLine -> literal.toString()
			is FieldScriptLine -> field.coreString
		}

val ScriptField.coreString: String
	get() =
		"$string(${rhs.coreString})"

// === Line count

val Script.lineSeq: Seq<ScriptLine>
	get() =
		when (this) {
			is UnitScript -> emptySeq()
			is LinkScript -> seq { link.lineSeqNode }
		}

val ScriptLink.lineSeqNode: SeqNode<ScriptLine>
	get() =
		line then lhs.lineSeq

val Script.lineCount
	get() =
		0.fold(lineSeq) { inc() }

val Script.forget
	get() =
		script()

fun Script.replaceWith(script: Script) =
	script

val Script.headOrNull
	get() =
		when (this) {
			is UnitScript -> null
			is LinkScript -> script(link.line)
		}

val Script.tailOrNull
	get() =
		when (this) {
			is UnitScript -> null
			is LinkScript -> link.lhs
		}

val Script.bodyOrNull
	get() =
		when (this) {
			is UnitScript -> null
			is LinkScript -> link.line.bodyOrNull
		}

val ScriptLine.bodyOrNull
	get() =
		when (this) {
			is LiteralScriptLine -> null
			is FieldScriptLine -> field.rhs
		}

val Script.head
	get() =
		headOrNull ?: plus("head" lineTo script())

val Script.tail
	get() =
		tailOrNull ?: plus("tail" lineTo script())

val Script.body
	get() =
		bodyOrNull ?: plus("body" lineTo script())

fun Script.make(string: String) =
	script(string lineTo this)

operator fun Script.get(string: String) =
	when (this) {
		is UnitScript -> null
		is LinkScript -> link[string]
	}

operator fun ScriptLink.get(string: String) =
	when (lhs) {
		is UnitScript -> line[string]
		is LinkScript -> null
	}

operator fun ScriptLine.get(string: String) =
	when (this) {
		is LiteralScriptLine -> null
		is FieldScriptLine -> field.rhs.lineOrNull(string)?.let { line -> script(line) }
	}

fun Script.rhsOrNull(string: String): Script? =
	when (this) {
		is UnitScript -> null
		is LinkScript -> link.rhsOrNull(string)
	}

fun ScriptLink.rhsOrNull(string: String) =
	line.rhsOrNull(string) ?: lhs.rhsOrNull(string)

fun ScriptLine.rhsOrNull(string: String) =
	when (this) {
		is LiteralScriptLine -> null
		is FieldScriptLine -> field.rhsOrNull(string)
	}

fun ScriptField.rhsOrNull(string: String) =
	if (this.string == string) rhs
	else null

fun Script.lineOrNull(string: String): ScriptLine? =
	when (this) {
		is UnitScript -> null
		is LinkScript -> link.lineOrNull(string)
	}

val Script.onlyLineOrNull: ScriptLine?
	get() =
		when (this) {
			is UnitScript -> null
			is LinkScript ->
				when (link.lhs) {
					is UnitScript -> link.line
					is LinkScript -> null
				}
		}

fun ScriptLink.lineOrNull(string: String) =
	line.ifNamed(string) ?: lhs.lineOrNull(string)

fun ScriptLine.ifNamed(string: String) =
	if (isNamed(string)) this
	else null

fun ScriptLine.isNamed(string: String) =
	when (this) {
		is LiteralScriptLine -> literal.name == string
		is FieldScriptLine -> field.string == string
	}

val Literal.name
	get() =
		when (this) {
			is NumberLiteral -> "number"
			is StringLiteral -> "text"
		}

val Script.reflectScriptLine
	get() =
		"script" lineTo this

val ScriptLine.fieldOrNull: ScriptField?
	get() =
		(this as? FieldScriptLine)?.field

val ScriptLine.literalOrNull: Literal?
	get() =
		(this as? LiteralScriptLine)?.literal

val ScriptLink.onlyLineOrNull: ScriptLine?
	get() =
		notNullIf(lhs.isEmpty) { line }

val ScriptField.onlyStringOrNull: String?
	get() =
		notNullIf(rhs.isEmpty) { string }

val Script.onlyStringOrNull: String?
	get() =
		linkOrNull?.onlyLineOrNull?.fieldOrNull?.onlyStringOrNull

tailrec fun Stack<String>.plusNamesOrNull(script: Script): Stack<String>? =
	when (script) {
		is UnitScript -> this
		is LinkScript -> {
			val field = script.link.line.fieldOrNull
			if (!script.link.lhs.isEmpty || field == null) null
			else push(field.string).plusNamesOrNull(field.rhs)
		}
	}

val Script.nameStackOrNull: Stack<String>?
	get() =
		stack<String>().plusNamesOrNull(this)?.reverse

// === matching

fun <R : Any> Script.matchInfix(name: String, fn: (Script, Script) -> R?): R? =
	linkOrNull?.let { link ->
		link.lhs.let { lhs ->
			link.rhsOrNull(name)?.let { rhs ->
				fn(lhs, rhs)
			}
		}
	}

fun <R : Any> Script.matchPrefix(name: String, fn: (Script) -> R?): R? =
	matchInfix(name) { lhs, rhs ->
		lhs.matchEmpty {
			fn(rhs)
		}
	}

fun <R : Any> Script.match(name: String, fn: () -> R?): R? =
	matchInfix(name) { lhs, rhs ->
		lhs.matchEmpty {
			rhs.matchEmpty {
				fn()
			}
		}
	}

fun <R : Any> Script.matchEmpty(fn: () -> R?): R? =
	ifOrNull(isEmpty) {
		fn()
	}

fun <R : Any> ScriptLine.match(name: String, fn: (Script) -> R?): R? =
	fieldOrNull?.match(name, fn)

fun <R : Any> ScriptField.match(name: String, fn: (Script) -> R?): R? =
	ifOrNull(string == name) {
		fn(rhs)
	}
