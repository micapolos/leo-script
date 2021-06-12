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
import kotlin.test.Test

class CompileTest {
	@Test
	fun get_postfix() {
		script(
			"point" lineTo script(
				"x" lineTo script(literal(10)),
				"y" lineTo script(literal(20))),
			"x" lineTo script())
			.typedExpression
			.assertEqualTo(
				typed(
					expression(line(
						get(
							"point" lineTo expression(
								"x" lineTo expression(expressionLine<Unit>(literal(10))),
								"y" lineTo expression(expressionLine(literal(20)))
							),
							"x"))),
					type("x" lineTo type(numberTypeLine))))
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
			.typedExpression
			.assertEqualTo(
				typed(
					expression(line(
						function(
							type("foo"),
							expression("bar" lineTo expression<Unit>())
						)))
						.invoke(expression("foo" lineTo expression())),
					type("bar")))
	}

	@Test
	fun do_withBinding() {
		script(
			"x" lineTo script(literal(10)),
			doName lineTo script("x"))
			.typedExpression
			.assertEqualTo(
				typed(
					expression(line(
						function(
							type("x" lineTo type(numberTypeLine)),
							expression(line<Unit>(variable(type("x")))))))
						.invoke(expression("x" lineTo expression(expressionLine(literal(10))))),
					type("x" lineTo type(numberTypeLine))))
	}
}