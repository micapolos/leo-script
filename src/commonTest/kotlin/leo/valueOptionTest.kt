package leo

import leo.base.assertEqualTo
import kotlin.test.Test

class ValueOptionTest {
	@Test
	fun optionBind() {
		value("red")
			.optionBind { it.make("color") }
			.assertEqualTo(value("red").make("color"))

		value("red")
			.presentOption
			.optionBind { it.make("color") }
			.assertEqualTo(value("red").make("color").presentOption)

		absentOptionValue
			.optionBind { it.make("color") }
			.assertEqualTo(absentOptionValue)

		value("red")
			.optionBind { it.make("color").presentOption }
			.assertEqualTo(value("red").make("color").presentOption )

		value("red")
			.presentOption
			.optionBind { it.make("color").presentOption }
			.assertEqualTo(value("red").make("color").presentOption)

		absentOptionValue
			.optionBind { it.make("color").presentOption }
			.assertEqualTo(absentOptionValue)

		value("red")
			.optionBind { absentOptionValue }
			.assertEqualTo(absentOptionValue)

		value("red")
			.presentOption
			.optionBind { absentOptionValue }
			.assertEqualTo(absentOptionValue)

		absentOptionValue
			.optionBind { absentOptionValue }
			.assertEqualTo(absentOptionValue)
	}
}