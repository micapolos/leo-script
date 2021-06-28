package leo.interactive

import leo.Script
import leo.line
import leo.lineTo
import leo.plus
import leo.script

fun <S> S.scriptProcessor(script: Script, ret: (Script) -> Processor<S, Token>): Processor<S, Token> =
	processor { token ->
		when (token) {
			is BeginToken -> scriptProcessor(script()) {
				scriptProcessor(script.plus(token.begin.name lineTo it), ret)
			}
			is LiteralToken -> scriptProcessor(script.plus(line(token.literal)), ret)
			is EndToken -> ret(script)
		}
	}

val Script.rootProcessor: Processor<Script?, Token> get() =
	processor { token ->
		when (token) {
			is BeginToken -> null.scriptProcessor(script()) { plus(token.begin.name lineTo it).rootProcessor }
			is LiteralToken -> plus(line(token.literal)).rootProcessor
			is EndToken -> error("unexpected end")
		}
	}

val rootScriptProcessor: Processor<Script?, Token> get() =
	script().rootProcessor