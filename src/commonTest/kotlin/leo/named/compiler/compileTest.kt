package leo.named.compiler

import leo.base.assertEqualTo
import leo.doName
import leo.lineTo
import leo.literal
import leo.named.expression.expression
import leo.named.expression.expressionLine
import leo.named.expression.function
import leo.named.expression.get
import leo.named.expression.invoke
import leo.named.expression.line
import leo.named.expression.lineTo
import leo.named.expression.variable
import leo.named.typed.typed
import leo.numberTypeLine
import leo.script
import leo.type
import leo.typeStructure
import kotlin.test.Test

class CompileTest {
	@Test
	fun get_postfix() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))),
			"x" lineTo script())
			.typedLine
			.assertEqualTo(
				typed(
					line(
						get(
							"point" lineTo expression(
								"x" lineTo expression(expressionLine<Unit>(literal(10))),
								"y" lineTo expression(expressionLine(literal(20)))
							),
							"x")),
					"x" lineTo type(numberTypeLine)))
	}

//	@Test
//	fun get_prefix() {
//		script(
//			"x" lineTo script(
//				"point" lineTo script(
//					"x" lineTo script(literal(10)),
//					"y" lineTo script(literal(20)))))
//			.typedExpression
//			.assertEqualTo(
//				typed(
//					expression(
//						get(
//							"point" expressionTo structure(
//								"x" expressionTo structure(expression<Unit>(literal(10))),
//								"y" expressionTo structure(expression(literal(20)))),
//							"x")),
//					"x" lineTo type(numberTypeLine)))
//	}

	@Test
	fun do_withoutBindings() {
		script(
			"foo" lineTo script(),
			doName lineTo script("bar"))
			.typedLine
			.assertEqualTo(
				typed(
					line(
						function(
							typeStructure("foo"),
							"bar" lineTo expression<Unit>()
						))
						.invoke(expression("foo" lineTo expression())),
					"bar" lineTo type()))
	}

	@Test
	fun do_withBinding() {
		script(
			"x" lineTo script(literal(10)),
			doName lineTo script("x"))
			.typedLine
			.assertEqualTo(
				typed(
					line(
						function(
							typeStructure("x" lineTo type(numberTypeLine)),
							line<Unit>(variable(typeStructure("x")))))
						.invoke(expression("x" lineTo expression(expressionLine(literal(10))))),
					"x" lineTo type(numberTypeLine)))
	}
}