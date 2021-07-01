package leo.interactive

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.base.fold
import leo.base.reverse
import leo.lineSeq

typealias Tokenizer<S> = Processor<S, Token>

fun <S> Tokenizer<S>.process(script: Script): Tokenizer<S> =
	fold(script.lineSeq.reverse) { process(it) }

fun <S> Tokenizer<S>.process(scriptLine: ScriptLine): Tokenizer<S> =
	when (scriptLine) {
		is FieldScriptLine -> process(scriptLine.field)
		is LiteralScriptLine -> process(scriptLine.literal)
	}

fun <S> Tokenizer<S>.process(field: ScriptField): Tokenizer<S> =
	plusFn(token(begin(field.name))).process(field.rhs).plusFn(token(end))

fun <S> Tokenizer<S>.process(literal: Literal): Tokenizer<S> =
	plusFn(token(literal))