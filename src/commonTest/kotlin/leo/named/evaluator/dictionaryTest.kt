package leo.named.evaluator

import leo.base.assertEqualTo
import leo.get
import leo.named.expression.doing
import leo.named.expression.expression
import leo.named.value.function
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

	@Test
	fun plusRecursive() {
		val definition1 = definition(type("foo"), binding(function(dictionary(), doing(expression("bar")))))
		val dictionary1 = dictionary().plus(definition1)

		val definition2 = definition(type("zoo"), binding(function(dictionary(), doing(expression("zar")))))
		val dictionary2 = dictionary().plus(definition2)

		val definition3 = definition(type("goo"), binding(function(dictionary2, doing(expression("gar")))))
		val dictionary3 = dictionary2.plus(definition3)

		dictionary1
			.plusRecursive(dictionary3)
			.assertEqualTo(
				dictionary()
					.plus(definition1)
					.plus(definition(type("zoo"), binding(recursive(definition2.binding, dictionary1, dictionary3))))
					.plus(definition(type("goo"), binding(recursive(definition3.binding, dictionary1, dictionary3)))))
	}
}