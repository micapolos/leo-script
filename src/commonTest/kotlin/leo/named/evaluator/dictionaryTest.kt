package leo.named.evaluator

import leo.base.assertEqualTo
import leo.get
import leo.named.value.lineTo
import leo.named.value.numberValue
import leo.named.value.value
import leo.type
import kotlin.test.Test

class DictionaryTest {
	@Test
	fun valueDictionary() {
		value(
			"x" lineTo 10.numberValue,
			"y" lineTo 20.numberValue)
			.linesDictionary
			.assertEqualTo(
				dictionary(
					definition(type("x"), binding(value("x" lineTo 10.numberValue))),
					definition(type("y"), binding(value("y" lineTo 20.numberValue)))))
	}

	@Test
	fun dictionaryConstantValue() {
		dictionary(
			definition(type("x"), binding(value("x" lineTo 10.numberValue))),
			definition(type("y"), binding(value("y" lineTo 20.numberValue))))
			.giveEvaluation(type("x"), value())
			.get(Unit)
			.assertEqualTo(value("x" lineTo 10.numberValue))

		dictionary(
			definition(type("x"), binding(value("x" lineTo 10.numberValue))),
			definition(type("y"), binding(value("y" lineTo 20.numberValue))))
			.giveEvaluation(type("y"), value())
			.get(Unit)
			.assertEqualTo(value("y" lineTo 20.numberValue))
	}
}