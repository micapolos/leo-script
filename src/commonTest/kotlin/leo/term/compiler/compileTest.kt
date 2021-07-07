package leo.term.compiler

import leo.applyName
import leo.base.assertEqualTo
import leo.functionName
import leo.getName
import leo.givingName
import leo.lineTo
import leo.literal
import leo.notName
import leo.numberTypeLine
import leo.quoteName
import leo.script
import leo.selectName
import leo.term.compiler.native.Native
import leo.term.compiler.native.native
import leo.term.compiler.native.nativeEnvironment
import leo.term.nativeTerm
import leo.term.typed.choicePlus
import leo.term.typed.invoke
import leo.term.typed.lineTo
import leo.term.typed.noSelection
import leo.term.typed.staticTypedTerm
import leo.term.typed.typed
import leo.term.typed.typedChoice
import leo.term.typed.typedFunctionLine
import leo.term.typed.typedTerm
import leo.term.typed.yesSelection
import leo.textTypeLine
import leo.textTypeScriptLine
import leo.theName
import leo.type
import kotlin.test.Test

class CompileTest {
	@Test
	fun empty() {
		nativeEnvironment
			.typedTerm(script())
			.assertEqualTo(typedTerm())
	}

	@Test
	fun name() {
		nativeEnvironment
			.typedTerm(script("foo"))
			.assertEqualTo(typedTerm("foo" lineTo typedTerm()))
	}

	@Test
	fun field() {
		nativeEnvironment
			.typedTerm(script("foo" lineTo script("bar")))
			.assertEqualTo(typedTerm("foo" lineTo typedTerm("bar" lineTo typedTerm())))
	}

	@Test
	fun number() {
		nativeEnvironment
			.typedTerm(script(literal(10)))
			.assertEqualTo(typedTerm(typed(10.0.native.nativeTerm, numberTypeLine)))
	}

	@Test
	fun text() {
		nativeEnvironment
			.typedTerm(script(literal("foo")))
			.assertEqualTo(typedTerm(typed("foo".native.nativeTerm, textTypeLine)))
	}

	@Test
	fun names() {
		nativeEnvironment
			.typedTerm(script("foo", "bar"))
			.assertEqualTo(typedTerm("bar" lineTo typedTerm("foo")))
	}

	@Test
	fun fields() {
		nativeEnvironment
			.typedTerm(script(
				"x" lineTo script("zero"),
				"y" lineTo script("one")))
			.assertEqualTo(
				typedTerm(
					"x" lineTo typedTerm("zero" lineTo typedTerm()),
					"y" lineTo typedTerm("one" lineTo typedTerm())))
	}

	@Test
	fun get() {
		nativeEnvironment
			.typedTerm(
				script(
					"point" lineTo script(
						"x" lineTo script("zero"),
						"y" lineTo script("one")),
					"x" lineTo script()))
			.assertEqualTo(
				typedTerm(
					"x" lineTo typedTerm("zero" lineTo typedTerm())))
	}

	@Test
	fun function() {
		nativeEnvironment
			.typedTerm(
				script(
					functionName lineTo script(
						"zero" lineTo script(),
						givingName lineTo script("one"))))
			.assertEqualTo(
				typedTerm(
					typedFunctionLine(type("zero"), typedTerm("one" lineTo typedTerm()))))
	}

	@Test
	fun apply() {
		nativeEnvironment
			.typedTerm(
				script(
					"ping" lineTo script(),
					applyName lineTo script(
						functionName lineTo script(
							"ping" lineTo script(),
							givingName lineTo script("pong")))))
			.assertEqualTo(
				typedTerm<Native>(typedFunctionLine(type("ping"), typedTerm("pong" lineTo typedTerm())))
					.invoke(typedTerm("ping" lineTo typedTerm())))
	}

	@Test
	fun quote() {
		nativeEnvironment
			.typedTerm(
				script(quoteName lineTo script(getName lineTo script("foo"))))
			.assertEqualTo(nativeEnvironment.staticTypedTerm(script(getName lineTo script("foo"))))
	}

	@Test
	fun select() {
		nativeEnvironment
			.typedTerm(
				script(
					selectName lineTo script(
						theName lineTo script(literal(10)),
						notName lineTo script(textTypeScriptLine))))
			.assertEqualTo(
				typedChoice<Native>()
					.choicePlus(yesSelection(typed(10.0.native.nativeTerm, numberTypeLine)))
					.choicePlus(noSelection(textTypeLine))
					.typedTerm)
	}
}