package leo.interactive

import leo.Script
import leo.ScriptLine
import leo.line
import leo.lineTo
import leo.plus
import leo.script

fun <S> S.scriptLineProcessor(begin: NameBegin, script: Script, ret: (ScriptLine) -> Processor<S, Token>): Processor<S, Token> =
	processor { token ->
		when (token) {
			is BeginToken -> scriptLineProcessor(token.begin, script()) { scriptLineProcessor(begin, script.plus(it), ret) }
			is LiteralToken -> scriptLineProcessor(begin, script.plus(line(token.literal)), ret)
			is EndToken -> ret(begin.name lineTo script)
		}
	}

val Script.rootProcessor: Processor<Script?, Token> get() =
	processor { token ->
		when (token) {
			is BeginToken -> null.scriptLineProcessor(token.begin, script()) { plus(it).rootProcessor }
			is LiteralToken -> plus(line(token.literal)).rootProcessor
			is EndToken -> error("unexpected end")
		}
	}

val rootScriptProcessor: Processor<Script?, Token> get() =
	script().rootProcessor