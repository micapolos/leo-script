package leo.named.library

import leo.named.evaluator.get
import leo.named.expression.function
import leo.named.value.double
import leo.named.value.get
import leo.named.value.numberValue
import leo.numberName
import leo.plusName

val numberPlusNumberFunction get() =
	function("numberPlusNumber") {
		get(numberName).double
			.plus(get(plusName).get(numberName).double)
			.numberValue
	}

val numberMinusNumberFunction get() =
	function("numberMinusNumber") {
		get(numberName).double
			.minus(get(plusName).get(numberName).double)
			.numberValue
	}
