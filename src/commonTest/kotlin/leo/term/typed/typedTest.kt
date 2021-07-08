package leo.term.typed

import leo.base.assertEqualTo
import leo.choice
import leo.lineTo
import leo.term.eitherFirst
import leo.term.eitherSecond
import leo.term.nativeTerm
import leo.type
import kotlin.test.Test
import kotlin.test.assertFails

class TypedTest {
	@Test
	fun noChoice() {
		assertFails {
			typedChoice<String>()
				.choicePlus(noSelection("one" lineTo type()))
				.choicePlus(noSelection("two" lineTo type()))
				.choicePlus(noSelection("three" lineTo type()))
				.typedTerm
		}
	}

	@Test
	fun multipleChoice() {
		assertFails {
			typedChoice<String>()
				.choicePlus(yesSelection(typed("one".nativeTerm, "one" lineTo type())))
				.choicePlus(yesSelection(typed("two".nativeTerm, "two" lineTo type())))
		}
	}

	@Test
	fun choice_oneOfThree() {
		typedChoice<String>()
			.choicePlus(yesSelection(typed("one".nativeTerm, "one" lineTo type())))
			.choicePlus(noSelection("two" lineTo type()))
			.choicePlus(noSelection("three" lineTo type()))
			.typedTerm
			.assertEqualTo(
				Typed(
					"one".nativeTerm.eitherFirst.eitherFirst,
					type(
						choice(
							"one" lineTo type(),
							"two" lineTo type(),
							"three" lineTo type()))))
	}

	@Test
	fun choice_twoOfThree() {
		typedChoice<String>()
			.choicePlus(noSelection("one" lineTo type()))
			.choicePlus(yesSelection(typed("two".nativeTerm, "two" lineTo type())))
			.choicePlus(noSelection("three" lineTo type()))
			.typedTerm
			.assertEqualTo(
				Typed(
					"two".nativeTerm.eitherSecond.eitherFirst,
					type(
						choice(
							"one" lineTo type(),
							"two" lineTo type(),
							"three" lineTo type()))))
	}

	@Test
	fun choice_threeOfThree() {
		typedChoice<String>()
			.choicePlus(noSelection("one" lineTo type()))
			.choicePlus(noSelection("two" lineTo type()))
			.choicePlus(yesSelection(typed("three".nativeTerm, "three" lineTo type())))
			.typedTerm
			.assertEqualTo(
				Typed(
					"three".nativeTerm.eitherSecond,
					type(
						choice(
							"one" lineTo type(),
							"two" lineTo type(),
							"three" lineTo type()))))
	}
}