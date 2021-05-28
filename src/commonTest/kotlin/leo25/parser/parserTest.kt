package leo25.parser

import leo.base.assertEqualTo
import leo13.charString
import leo13.stack
import leo13.stackLink
import leo14.literal
import leo14.number
import kotlin.test.Test

class ParserTest {
	@Test
	fun charStackParser() {
		charStackParser.parsed("").assertEqualTo(stack())
		charStackParser.parsed("a").assertEqualTo(stack('a'))
		charStackParser.parsed("a\n").assertEqualTo(stack('a', '\n'))
	}

	@Test
	fun stringParser() {
		stringParser.parsed("").assertEqualTo("")
		stringParser.parsed("a").assertEqualTo("a")
		stringParser.parsed("a\n").assertEqualTo("a\n")
	}

	@Test
	fun textParser() {
		parser("abc").parsed("abc").assertEqualTo("abc")
		parser("abc").parsed("ab").assertEqualTo(null)
		parser("abc").parsed("abcd").assertEqualTo(null)
	}

	@Test
	fun stackParser() {
		val itemParser = unitParser('.').unitThen(nameParser)
		itemParser.stackParser.parsed("").assertEqualTo(stack())
		itemParser.stackParser.parsed(".a").assertEqualTo(stack("a"))
		itemParser.stackParser.parsed(".ab").assertEqualTo(stack("ab"))
		itemParser.stackParser.parsed(".ab.").assertEqualTo(null)
		itemParser.stackParser.parsed(".ab.a").assertEqualTo(stack("ab", "a"))
		itemParser.stackParser.parsed(".ab1").assertEqualTo(null)
	}

	@Test
	fun escapeCharParser() {
		escapeCharParser.parsed("\\").assertEqualTo('\\')
		escapeCharParser.parsed("n").assertEqualTo('\n')
		escapeCharParser.parsed("t").assertEqualTo('\t')
		escapeCharParser.parsed("\"").assertEqualTo('\"')

		escapeCharParser.parsed("a").assertEqualTo(null)
		escapeCharParser.parsed("\na").assertEqualTo(null)
	}

	@Test
	fun escapeSequenceCharParser() {
		escapeSequenceCharParser.parsed("\\\\").assertEqualTo('\\')
		escapeSequenceCharParser.parsed("\\n").assertEqualTo('\n')
		escapeSequenceCharParser.parsed("\\na").assertEqualTo(null)
	}

	@Test
	fun stringBody() {
		textBodyParser.parsed("").assertEqualTo("")
		textBodyParser.parsed("abc").assertEqualTo("abc")
		textBodyParser.parsed("a\\nc").assertEqualTo("a\nc")
		textBodyParser.parsed("a\\\"c").assertEqualTo("a\"c")
		textBodyParser.parsed("a\"").assertEqualTo(null)
	}

	@Test
	fun name() {
		nameParser.parsed("a").assertEqualTo("a")
		nameParser.parsed("foo").assertEqualTo("foo")

		nameParser.parsed("").assertEqualTo(null)
		nameParser.parsed("1").assertEqualTo(null)
		nameParser.parsed("foo1").assertEqualTo(null)
		nameParser.parsed("foo bar").assertEqualTo(null)
	}

	@Test
	fun number() {
		numberParser.parsed("0").assertEqualTo(number(0))
		//numberParser.parsed("-0").assertEqualTo(number(0))
		numberParser.parsed("1").assertEqualTo(number(1))
		numberParser.parsed("12").assertEqualTo(number(12))
		numberParser.parsed("-1").assertEqualTo(number(-1))
		numberParser.parsed("-12").assertEqualTo(number(-12))

		numberParser.parsed("").assertEqualTo(null)
		numberParser.parsed("-").assertEqualTo(null)
		numberParser.parsed("+").assertEqualTo(null)
		numberParser.parsed("+1").assertEqualTo(null)
		numberParser.parsed("1.").assertEqualTo(null)
		numberParser.parsed("1.0").assertEqualTo(null)
		numberParser.parsed("1a").assertEqualTo(null)
	}

	@Test
	fun literal() {
		literalParser.parsed("\"foo\"").assertEqualTo(literal("foo"))
		literalParser.parsed("123").assertEqualTo(literal(123))

		literalParser.parsed("").assertEqualTo(null)
		literalParser.parsed("abc").assertEqualTo(null)
	}

	@Test
	fun map() {
		val parser = stackParser(letterCharParser).bind { letterCharStack ->
			stackParser(digitCharParser).map { digitCharStack ->
				letterCharStack.charString + digitCharStack.charString
			}
		}

		parser.parsed("").assertEqualTo("")
		parser.parsed("a").assertEqualTo("a")
		parser.parsed("ab").assertEqualTo("ab")
		parser.parsed("1").assertEqualTo("1")
		parser.parsed("12").assertEqualTo("12")
		parser.parsed("a1").assertEqualTo("a1")
		parser.parsed("ab12").assertEqualTo("ab12")

		parser.parsed("+").assertEqualTo(null)
		parser.parsed("a+").assertEqualTo(null)
		parser.parsed("1+").assertEqualTo(null)
		parser.parsed("a1+").assertEqualTo(null)
	}

	@Test
	fun stackLinkSeparatedBy() {
		val parser = nameParser.stackLinkSeparatedBy(unitParser('.'))
		parser.parsed("foo").assertEqualTo(stackLink("foo"))
		parser.parsed("foo.bar").assertEqualTo(stackLink("foo", "bar"))
		parser.parsed("foo.bar.zoo").assertEqualTo(stackLink("foo", "bar", "zoo"))

		parser.parsed("").assertEqualTo(null)
		parser.parsed(".").assertEqualTo(null)
		parser.parsed("foo.").assertEqualTo(null)
		parser.parsed("foo..zoo").assertEqualTo(null)
		parser.parsed("foo.123").assertEqualTo(null)
	}

	@Test
	fun firstCharOr() {
		val parser = parser("12").firstCharOr(parser("ab"))
		parser.parsed("12").assertEqualTo("12")
		parser.parsed("ab").assertEqualTo("ab")
		parser.parsed("1").assertEqualTo(null)
		parser.parsed("a").assertEqualTo(null)

		val parser2 = parser("12").firstCharOr(parser("13"))
		parser2.parsed("12").assertEqualTo("12")
		parser2.parsed("13").assertEqualTo(null)
	}

	@Test
	fun or() {
		val parser = parser("12").or(parser("13"))
		parser.parsed("12").assertEqualTo("12")
		parser.parsed("13").assertEqualTo("13")
	}

	@Test
	fun string() {
		textParser.parsed("\"\"").assertEqualTo("")
		textParser.parsed("\"foo\"").assertEqualTo("foo")
		textParser.parsed("\"foo 123\"").assertEqualTo("foo 123")
		textParser.parsed("\"\t\"").assertEqualTo("\t")
		textParser.parsed("\"\n\"").assertEqualTo("\n")
		textParser.parsed("\"\\\\\"").assertEqualTo("\\")
		textParser.parsed("\"\\\"\"").assertEqualTo("\"")

		textParser.parsed("").assertEqualTo(null)
		textParser.parsed("\"").assertEqualTo(null)
		textParser.parsed("\"foo").assertEqualTo(null)
	}

	@Test
	fun tabParser() {
		tabParser.parsed("  ").assertEqualTo(Tab)
		tabParser.parsed("").assertEqualTo(null)
		tabParser.parsed(" ").assertEqualTo(null)
		tabParser.parsed("   ").assertEqualTo(null)
	}

	@Test
	fun maxIndentParser() {
		0.maxIndentUnitParser.parsed("").assertEqualTo(Unit)
		0.maxIndentUnitParser.parsed(" ").assertEqualTo(null)
		0.maxIndentUnitParser.parsed("  ").assertEqualTo(null)

		1.maxIndentUnitParser.parsed("").assertEqualTo(Unit)
		1.maxIndentUnitParser.parsed(" ").assertEqualTo(null)
		1.maxIndentUnitParser.parsed("  ").assertEqualTo(Unit)
		1.maxIndentUnitParser.parsed("   ").assertEqualTo(null)
		1.maxIndentUnitParser.parsed("    ").assertEqualTo(null)

		2.maxIndentUnitParser.parsed("").assertEqualTo(Unit)
		2.maxIndentUnitParser.parsed(" ").assertEqualTo(null)
		2.maxIndentUnitParser.parsed("  ").assertEqualTo(Unit)
		2.maxIndentUnitParser.parsed("   ").assertEqualTo(null)
		2.maxIndentUnitParser.parsed("    ").assertEqualTo(Unit)
		2.maxIndentUnitParser.parsed("     ").assertEqualTo(null)
		2.maxIndentUnitParser.parsed("      ").assertEqualTo(null)
	}

	@Test
	fun withoutEmptyLines() {
		stringParser.withoutEmptyLines.run {
			parsed("foo").assertEqualTo("foo")
			parsed("foo\nbar").assertEqualTo("foo\nbar")
			parsed("foo\n\nbar").assertEqualTo("foo\nbar")
		}
	}

	@Test
	fun withoutTrailingSpaces() {
		stringParser.withoutTrailingSpaces.run {
			parsed("  foo bar  ").assertEqualTo("  foo bar")
			parsed("foo bar\n  \nzoo zar  ").assertEqualTo("foo bar\n\nzoo zar")
		}
	}

	@Test
	fun addingMissingNewline() {
		stringParser.addingMissingNewline.run {
			parsed("").assertEqualTo("")
			parsed("foo").assertEqualTo("foo\n")
			parsed("foo\n").assertEqualTo("foo\n")
			parsed("foo\nbar").assertEqualTo("foo\nbar\n")
			parsed("foo\nbar\n").assertEqualTo("foo\nbar\n")
		}
	}
}