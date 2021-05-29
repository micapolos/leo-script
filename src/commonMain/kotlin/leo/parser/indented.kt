package leo.parser

val <T> Parser<T>.indented: Parser<T>
	get() =
		indentedWith(tabUnitParser)

fun <T> Parser<T>.indentedWith(indentParser: Parser<Unit>): Parser<T> =
	doing { char ->
		indentParser.plus(char)?.let {
			indentedWith(it, it)
		}
	}

fun <T> Parser<T>.indentedWith(indentParser: Parser<Unit>, newIndentParser: Parser<Unit>): Parser<T> =
	partialParser { char ->
		indentParser.plus(char)?.let { indentParser ->
			indentParser.parsedOrNull.let { parsedOrNull ->
				if (parsedOrNull == null) indentedWith(indentParser, newIndentParser)
				else indentedBodyWith(newIndentParser)
			}
		}
	}

fun <T> Parser<T>.indentedBodyWith(indentParser: Parser<Unit>): Parser<T> =
	partialParser { char ->
		if (char == '\n') plus('\n')?.indented
		else plus(char)?.indentedBodyWith(indentParser)
	}
