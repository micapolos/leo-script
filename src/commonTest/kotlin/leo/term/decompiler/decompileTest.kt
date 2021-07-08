package leo.term.decompiler

import leo.base.assertEqualTo
import leo.choice
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
import leo.term.compiler.native.Native
import leo.term.compiler.native.native
import leo.term.eitherFirst
import leo.term.eitherSecond
import leo.term.idValue
import leo.term.nativeValue
import leo.term.typed.typed
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class DecompileTest {
	@Test
	fun either_single() {
		typed(
			"one".native.nativeValue,
			type(choice(textTypeLine)))
			.script
			.assertEqualTo(script(literal("one")))
	}

	@Test
	fun either_firstOfTwo() {
		typed(
			"one".native.nativeValue.eitherFirst,
			type(choice(textTypeLine, numberTypeLine)))
			.script
			.assertEqualTo(script(literal("one")))
	}

	@Test
	fun either_secondOfTwo() {
		typed(
			2.0.native.nativeValue.eitherSecond,
			type(choice(textTypeLine, numberTypeLine)))
			.script
			.assertEqualTo(script(literal(2)))
	}

	@Test
	fun either_firstOfThree() {
		typed(
			"one".native.nativeValue.eitherFirst.eitherFirst,
			type(choice(textTypeLine, numberTypeLine, "three" lineTo type())))
			.script
			.assertEqualTo(script(literal("one")))
	}

	@Test
	fun either_secondOfThree() {
		typed(
			2.0.native.nativeValue.eitherSecond.eitherFirst,
			type(choice(textTypeLine, numberTypeLine, "three" lineTo type())))
			.script
			.assertEqualTo(script(literal(2)))
	}

	@Test
	fun either_thirdOfThree() {
		typed(
			idValue<Native>().eitherSecond,
			type(choice(textTypeLine, numberTypeLine, "three" lineTo type())))
			.script
			.assertEqualTo(script("three" lineTo script()))
	}
}