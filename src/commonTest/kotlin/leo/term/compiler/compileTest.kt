package leo.term.compiler

import leo.actionName
import leo.base.assertEqualTo
import leo.doingName
import leo.lineTo
import leo.literal
import leo.script
import leo.term.compiler.runtime.runtimeEnvironment
import leo.term.typed.lineTo
import leo.term.typed.typedFunctionLine
import leo.term.typed.typedLine
import leo.term.typed.typedTerm
import leo.type
import kotlin.test.Test

class CompileTest {
	@Test
	fun empty() {
		runtimeEnvironment
			.typedTerm(script())
			.assertEqualTo(typedTerm())
	}

	@Test
	fun name() {
		runtimeEnvironment
			.typedTerm(script("foo"))
			.assertEqualTo(typedTerm("foo" lineTo typedTerm()))
	}

	@Test
	fun field() {
		runtimeEnvironment
			.typedTerm(script("foo" lineTo script("bar")))
			.assertEqualTo(typedTerm("foo" lineTo typedTerm("bar" lineTo typedTerm())))
	}

	@Test
	fun number() {
		runtimeEnvironment
			.typedTerm(script(literal(10)))
			.assertEqualTo(typedTerm(typedLine(literal(10))))
	}

	@Test
	fun text() {
		runtimeEnvironment
			.typedTerm(script(literal("foo")))
			.assertEqualTo(typedTerm(typedLine(literal("foo"))))
	}

	@Test
	fun names() {
		runtimeEnvironment
			.typedTerm(script("foo", "bar"))
			.assertEqualTo(
				typedTerm(
					"foo" lineTo typedTerm(),
					"bar" lineTo typedTerm()))
	}

	@Test
	fun fields() {
		runtimeEnvironment
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
		runtimeEnvironment
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