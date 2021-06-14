package leo.named.library

import leo.named.evaluator.get
import leo.named.expression.function
import leo.named.value.double
import leo.named.value.get
import leo.named.value.numberValue
import leo.named.value.textValue
import leo.natives.minusName
import leo.natives.timesName
import leo.numberName
import leo.plusName
import leo.textName

val numberTextFunction get() =
	function("numberText") {
		get(textName).get(numberName).double.toString().textValue
	}

val numberPlusNumberFunction get() =
	function("numberPlusNumber") {
		get(numberName).double
			.plus(get(plusName).get(numberName).double)
			.numberValue
	}

val numberMinusNumberFunction get() =
	function("numberMinusNumber") {
		get(numberName).double
			.minus(get(minusName).get(numberName).double)
			.numberValue
	}

val numberTimesNumberFunction get() =
	function("numberTimesNumber") {
		get(numberName).double
			.times(get(timesName).get(numberName).double)
			.numberValue
	}
