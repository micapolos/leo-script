package leo.interactive

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.base.Seq
import leo.base.flat
import leo.base.map
import leo.base.onlySeq
import leo.base.reverse
import leo.base.then
import leo.lineSeq

val Script.tokenSeq: Seq<Token> get() =
	lineSeq.reverse.map { tokenSeq }.flat

val ScriptLine.tokenSeq: Seq<Token> get() =
	when (this) {
		is FieldScriptLine -> field.tokenSeq
		is LiteralScriptLine -> onlySeq { token(literal) }
	}

val ScriptField.tokenSeq: Seq<Token> get() =
	onlySeq { token(begin(name)) }.then(rhs.tokenSeq)
