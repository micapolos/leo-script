package leo.typed.evaluator

import leo.base.assertEqualTo
import leo.typed.dsl._dsl
import leo.typed.dsl.get
import leo.typed.dsl.number
import leo.typed.dsl.point
import leo.typed.dsl.x
import leo.typed.dsl.y
import kotlin.test.Test
import kotlin.test.assertFails

class EvaluateTest {
	@Test
	fun empty() {
		_dsl()
			.evaluate
			.assertEqualTo(_dsl())
	}

	@Test
	fun literal() {
		_dsl(number(10))
			.evaluate
			.assertEqualTo(_dsl(number(10)))
	}

	@Test
	fun get() {
		_dsl(
			point(x(number(10)), y(number(20))),
			get(x()))
			.evaluate
			.assertEqualTo(_dsl(x(number(10))))

		_dsl(
			point(x(number(10)), y(number(20))),
			get(y()))
			.evaluate
			.assertEqualTo(_dsl(y(number(20))))

		assertFails {
			_dsl(
				point(x(number(10)), y(number(20))),
				get(point()))
				.evaluate
		}
	}
}