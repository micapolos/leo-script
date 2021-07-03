package leo.interactive.parser

import leo.Script
import leo.fold
import leo.interactive.BeginToken
import leo.interactive.EndToken
import leo.interactive.LiteralToken
import leo.interactive.Processor
import leo.interactive.Token
import leo.interactive.plus
import leo.interactive.processor
import leo.line
import leo.lineTo
import leo.parser.Parser
import leo.parser.map
import leo.parser.parser
import leo.plus
import leo.reverse
import leo.script

val TokensPrefix.parser: Parser<TokensPrefix> get() =
	parser { char ->
		line.plusTokensPrefixOrNull(char)?.let { tokensPrefix ->
			prefix(tokens.plus(tokensPrefix.tokens), tokensPrefix.line).parser
		}
	}

val scriptParser: Parser<Script> get() =
	tokensPrefix().parser.map { it.endTokensOrNull?.script }

fun Script.plusTokenProcessor(ret: (Script) -> Processor<Script, Token>): Processor<Script, Token> =
	processor { token ->
		when (token) {
			is BeginToken ->
				script().plusTokenProcessor {
					plus(token.begin.name lineTo it).plusTokenProcessor(ret)
				}
			is LiteralToken ->
				plus(token.literal.line).plusTokenProcessor(ret)
			is EndToken ->
				ret(this)
		}
	}

val Tokens.script: Script get() =
	script()
		.plusTokenProcessor { error("unexpected end") }
		.fold(tokenStack.reverse) { this.plus(it) }
		.state