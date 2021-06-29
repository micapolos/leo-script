package leo.interactive

import leo.Script
import leo.line
import leo.lineTo
import leo.plus
import leo.script

fun Script.tokenizer(ret: (Script) -> Tokenizer<Script?>): Tokenizer<Script?> =
	processor { token ->
		when (token) {
			is BeginToken -> script().tokenizer {
				plus(token.begin.name lineTo it).tokenizer(ret)
			}
			is LiteralToken -> plus(line(token.literal)).tokenizer(ret)
			is EndToken -> ret(this)
		}
	}

val Script.tokenizer: Tokenizer<Script?> get() =
	tokenizer { error("unexpected end") }

val scriptTokenizer: Tokenizer<Script?> get() =
	script().tokenizer