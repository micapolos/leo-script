package leo.term.compiler

import leo.actionName
import leo.base.assertEqualTo
import leo.doingName
import leo.lineTo
import leo.literal
import leo.numberTypeLine
import leo.script
import leo.term.compiler.runtime.thing
import leo.term.compiler.runtime.thingEnvironment
import leo.term.nativeTerm
import leo.term.typed.lineTo
import leo.term.typed.typed
import leo.term.typed.typedFunctionLine
import leo.term.typed.typedTerm
import leo.textTypeLine
import leo.type
import kotlin.test.Test

class CompileTest {
	@Test
	fun empty() {
		thingEnvironment
			.typedTerm(script())
			.assertEqualTo(typedTerm())
	}

	@Test
	fun name() {
		thingEnvironment
			.typedTerm(script("foo"))
			.assertEqualTo(typedTerm("foo" lineTo typedTerm()))
	}

	@Test
	fun field() {
		thingEnvironment
			.typedTerm(script("foo" lineTo script("bar")))
			.assertEqualTo(typedTerm("foo" lineTo typedTerm("bar" lineTo typedTerm())))
	}

	@Test
	fun number() {
		thingEnvironment
			.typedTerm(script(literal(10)))
			.assertEqualTo(typedTerm(typed(10.0.thing.nativeTerm, numberTypeLine)))
	}

	@Test
	fun text() {
		thingEnvironment
			.typedTerm(script(literal("foo")))
			.assertEqualTo(typedTerm(typed("foo".thing.nativeTerm, textTypeLine)))
	}

	@Test
	fun names() {
		thingEnvironment
			.typedTerm(script("foo", "bar"))
			.assertEqualTo(
				typedTerm(
					"foo" lineTo typedTerm(),
					"bar" lineTo typedTerm()))
	}

	@Test
	fun fields() {
		thingEnvironment
			.typedTerm(script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")))
			.assertEqualTo(
				typedTerm(
					"x" lineTo typedTerm("zero" lineTo typedTerm()),
					"y" lineTo typedTerm("one" lineTo typedTerm())))
	}

	@Test
	fun action() {
		thingEnvironment
			.typedTerm(
				script(
					actionName lineTo script(
						"zero" lineTo script(),
						doingName lineTo script("one"))))
			.assertEqualTo(
				typedTerm(
					typedFunctionLine(type("zero"), typedTerm("one" lineTo typedTerm()))))
	}
}