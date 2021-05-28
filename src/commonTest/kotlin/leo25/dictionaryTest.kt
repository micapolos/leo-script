package leo25

import leo.base.assertEqualTo
import leo14.lineTo
import leo14.literal
import leo14.script
import kotlin.test.Test

class DictionaryTest {
	@Test
	fun plusAny() {
		dictionary()
			.plus(
				definition(
					pattern(script(anyName)),
					binding(value("ok"))
				)
			)
			.assertEqualTo(
				dictionary(token(anyEnd) to resolution(binding(value("ok"))))
			)
	}

	@Test
	fun applyString() {
		dictionary()
			.plus(
				definition(
					pattern(script("ping")),
					binding(value("pong"))
				)
			)
			.applyOrNullLeo(value("ping"))
			.get
			.assertEqualTo(value("pong"))
	}

	@Test
	fun applyStruct() {
		dictionary()
			.plus(
				definition(
					pattern(script("name" lineTo script(anyName))),
					binding(value("ok"))
				)
			)
			.run {
				applyOrNullLeo(value("name" fieldTo value())).get.assertEqualTo(value("ok"))
				applyOrNullLeo(value("name" fieldTo value("michal"))).get.assertEqualTo(value("ok"))
				applyOrNullLeo(value("name" fieldTo value(field(literal("Michał"))))).get.assertEqualTo(value("ok"))
			}
	}

	@Test
	fun applyAny() {
		dictionary()
			.plus(
				definition(
					pattern(script(anyName)),
					binding(value("pong"))
				)
			)
			.run {
				applyOrNullLeo(value("ping")).get.assertEqualTo(value("pong"))
				applyOrNullLeo(value("ping")).get.assertEqualTo(value("pong"))
			}
	}

	@Test
	fun anyValueApply() {
		dictionary()
			.plus(
				definition(
					pattern(script(anyName lineTo script(), "plus" lineTo script(anyName))),
					binding(value("ok"))
				)
			)
			.run {
				applyOrNullLeo(value("a" fieldTo value(), "plus" fieldTo value("b" fieldTo value())))
					.get
					.assertEqualTo(value("ok"))
			}
	}

	@Test
	fun literalApply() {
		dictionary()
			.plus(
				definition(
					pattern(script(textName lineTo script(anyName))),
					binding(value("ok"))
				)
			)
			.applyOrNullLeo(value(field(literal("foo"))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					pattern(script(literal("foo"))),
					binding(value("ok"))
				)
			)
			.applyOrNullLeo(value(field(literal("foo"))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					pattern(script(literal("foo"))),
					binding(value("ok"))
				)
			)
			.applyOrNullLeo(value(field(literal("bar"))))
			.get
			.assertEqualTo(null)

		dictionary()
			.plus(
				definition(
					pattern(script(literal(123))),
					binding(value("ok"))
				)
			)
			.applyOrNullLeo(value(field(literal(123))))
			.get
			.assertEqualTo(value("ok"))

		dictionary()
			.plus(
				definition(
					pattern(script(literal(123))),
					binding(value("ok"))
				)
			)
			.applyOrNullLeo(value(field(literal(124))))
			.get
			.assertEqualTo(null)
	}

	@Test
	fun plusDifferentTokens() {
		dictionary(
			token(begin("x")) to resolution(
				dictionary(
					token(emptyEnd) to resolution(
						dictionary(
							token(emptyEnd) to resolution(binding(value("x")))
						)
					)
				)
			)
		)
			.plus(
				dictionary(
					token(begin("y")) to resolution(
						dictionary(
							token(emptyEnd) to resolution(
								dictionary(
									token(emptyEnd) to resolution(binding(value("y")))
								)
							)
						)
					)
				)
			)
			.assertEqualTo(
				dictionary(
					token(begin("x")) to resolution(
						dictionary(
							token(emptyEnd) to resolution(
								dictionary(
									token(emptyEnd) to resolution(binding(value("x")))
								)
							)
						)
					),
					token(begin("y")) to resolution(
						dictionary(
							token(emptyEnd) to resolution(
								dictionary(
									token(emptyEnd) to resolution(binding(value("y")))
								)
							)
						)
					)
				)
			)
	}

	@Test
	fun plusSharedTokens() {
		dictionary(
			token(begin("point")) to resolution(
				dictionary(
					token(begin("x")) to resolution(
						dictionary(
							token(emptyEnd) to resolution(
								dictionary(
									token(emptyEnd) to resolution(
										dictionary(
											token(emptyEnd) to resolution(binding(value("x")))
										)
									)
								)
							)
						)
					)
				)
			)
		)
			.plus(
				dictionary(
					token(begin("point")) to resolution(
						dictionary(
							token(begin("y")) to resolution(
								dictionary(
									token(emptyEnd) to resolution(
										dictionary(
											token(emptyEnd) to resolution(
												dictionary(
													token(emptyEnd) to resolution(binding(value("y")))
												)
											)
										)
									)
								)
							)
						)
					)
				)
			)
			.assertEqualTo(
				dictionary(
					token(begin("point")) to resolution(
						dictionary(
							token(begin("x")) to resolution(
								dictionary(
									token(emptyEnd) to resolution(
										dictionary(
											token(emptyEnd) to resolution(
												dictionary(
													token(emptyEnd) to resolution(binding(value("x")))
												)
											)
										)
									)
								)
							),
							token(begin("y")) to resolution(
								dictionary(
									token(emptyEnd) to resolution(
										dictionary(
											token(emptyEnd) to resolution(
												dictionary(
													token(emptyEnd) to resolution(binding(value("y")))
												)
											)
										)
									)
								)
							)
						)
					)
				)
			)
	}

	@Test
	fun plusAnyOverride() {
		dictionary(
			token(begin("x")) to resolution(
				dictionary(
					token(emptyEnd) to resolution(
						dictionary(
							token(emptyEnd) to resolution(binding(value("x")))
						)
					)
				)
			),
			token(emptyEnd) to resolution(binding(value("end")))
		)
			.plus(
				dictionary(
					token(anyEnd) to resolution(
						dictionary(
							token(emptyEnd) to resolution(binding(value("y")))
						)
					)
				)
			)
			.assertEqualTo(
				dictionary(
					token(anyEnd) to resolution(
						dictionary(
							token(emptyEnd) to resolution(binding(value("y")))
						)
					)
				)
			)
	}

	@Test
	fun switchOrNull() {
		dictionary()
			.switchLeo(
				value("shape" fieldTo value("circle" fieldTo value("radius" fieldTo value("zero")))),
				script(
					"circle" lineTo script("radius"),
					"rectangle" lineTo script("side")
				)
			)
			.get
			.assertEqualTo(value("radius" fieldTo value("zero")))
	}
}