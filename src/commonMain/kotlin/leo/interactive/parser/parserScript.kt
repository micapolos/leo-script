package leo.interactive.parser

import leo.ScriptLine
import leo.interactive.BeginToken
import leo.interactive.End
import leo.interactive.EndToken
import leo.interactive.LiteralToken
import leo.interactive.NameBegin
import leo.interactive.Token
import leo.line
import leo.lineTo
import leo.listScriptLine
import leo.literal
import leo.named.expression.scriptLine
import leo.optionScriptLine
import leo.script
import leo.stack

val Letter.scriptLine: ScriptLine get() =
	"letter" lineTo script(char.toString())

val Digit.scriptLine: ScriptLine get() =
	"digit" lineTo script(literal(int))

val Word.scriptLine: ScriptLine get() =
	"word" lineTo script(
		"letter" lineTo script(
			stack(letterStackLink).listScriptLine { scriptLine }))

val Number.scriptLine get() =
	"number" lineTo script(
		"digit" lineTo script(
			stack(digitStackLink).listScriptLine { scriptLine }))

val NumberNegative.scriptLine get() =
	"negative" lineTo script(
		"number" lineTo script(
			numberOrNull.optionScriptLine { scriptLine }))

val NumberPrefix.scriptLine get() =
	"partial" lineTo script(
		when (this) {
			is NegativeNumberPrefix -> negative.scriptLine
			is NumberNumberPrefix -> number.scriptLine
		}
	)

val Char.scriptLine: ScriptLine get() = "char" lineTo script(literal(toString()))

val Escape.scriptLine: ScriptLine get() = "escape" lineTo script()
val CharEscaped.scriptLine: ScriptLine get() = "escaped" lineTo script(char.scriptLine)

val TextItem.scriptLine: ScriptLine get() =
	"item" lineTo script(
		"text" lineTo script(
			when (this) {
				is CharTextItem -> char.scriptLine
				is EscapedTextItem -> escaped.scriptLine
			}
		))

val TextItemPrefix.scriptLine: ScriptLine get() =
	"prefix" lineTo script(
		"item" lineTo script(
			"text" lineTo script(escape.scriptLine)))

val TextOpening.scriptLine get() =
	"opening" lineTo script(
		"text" lineTo script(
			"item" lineTo script(
				itemStack.listScriptLine { scriptLine }),
			"prefix" lineTo script(
				"item" lineTo script(
					itemPrefixOrNull.optionScriptLine { scriptLine }))))

val LiteralPrefix.scriptLine get() = "partial" lineTo script(
	when (this) {
		is NumberLiteralPrefix -> numberPrefix.scriptLine
		is TextLiteralPrefix -> textPrefix.scriptLine
	}
)

val TextPrefix.scriptLine: ScriptLine get() =
	"prefix" lineTo script(
		"text" lineTo script(
			when (this) {
				is OpeningTextPrefix -> opening.scriptLine
				is StringTextPrefix -> string.literal.scriptLine
			}
		)
	)

val AtomPrefix.scriptLine get() =
	"partial" lineTo script(
		when (this) {
			is LiteralAtomPrefix -> literalPrefix.scriptLine
			is WordAtomPrefix -> word.scriptLine
		}
	)

val Tab.scriptLine: ScriptLine get() =
	"tab" lineTo script(
		"end" lineTo script(
			stack(endStackLink).listScriptLine { scriptLine }))

val TabPrefix.scriptLine: ScriptLine get() =
	"prefix" lineTo script(
		tab.scriptLine)

val End.scriptLine: ScriptLine get() =
	"end" lineTo script()

val Indent.scriptLine get() =
	"indent" lineTo script(
		tabStack.listScriptLine { scriptLine })

val IndentPrefix.scriptLine get() =
	"prefix" lineTo script(
		indent.scriptLine,
		"prefix" lineTo script(
			"tab" lineTo script(
				tabPrefixOrNull.optionScriptLine { scriptLine })))

val IndentSuffix.scriptLine get() =
	"suffix" lineTo script(indent.scriptLine)

val Header.scriptLine: ScriptLine get() =
	"header" lineTo script(
		prefix.scriptLine,
		"trailing" lineTo script(suffix.scriptLine))

val Spaced.scriptLine: ScriptLine get() =
	"spaced" lineTo script(
		"tab" lineTo script(tabOrNull.optionScriptLine { scriptLine }),
		"prefix" lineTo script("atom" lineTo script(atomPrefixOrNull.optionScriptLine { scriptLine })))

val Body.scriptLine: ScriptLine get() =
	"body" lineTo script(
		indent.scriptLine,
		spaced.scriptLine)

val Line.scriptLine: ScriptLine get() =
	"line" lineTo script(
		when (this) {
			is BodyLine -> body.scriptLine
			is HeaderLine -> header.scriptLine
		}
	)

val Token.scriptLine: ScriptLine get() =
	"token" lineTo script(
		when (this) {
			is BeginToken -> begin.scriptLine
			is EndToken -> end.scriptLine
			is LiteralToken -> literal.line
		}
	)

val NameBegin.scriptLine: ScriptLine get() =
	"begin" lineTo script(name)

val Tokens.scriptLine: ScriptLine get() =
	"token" lineTo script(
		tokenStack.listScriptLine { scriptLine })

val TokensPrefix.scriptLine: ScriptLine get() =
	"partial" lineTo script(
		tokens.scriptLine,
		line.scriptLine)