package leo.named.library

import leo.named.evaluator.get
import leo.named.expression.body
import leo.named.value.double
import leo.named.value.get
import leo.named.value.numberValue
import leo.named.value.textValue
import leo.natives.minusName
import leo.natives.timesName
import leo.numberName
import leo.plusName
import leo.textName

val numberTextBody get() =
	body("numberText") {
		get(textName).get(numberName).double.toString().textValue
	}

val numberPlusNumberBody get() =
	body("numberPlusNumber") {
		get(numberName).double
			.plus(get(plusName).get(numberName).double)
			.numberValue
	}

val numberMinusNumberBody get() =
	body("numberMinusNumber") {
		get(numberName).double
			.minus(get(minusName).get(numberName).double)
			.numberValue
	}

val numberTimesNumberBody get() =
	body("numberTimesNumber") {
		get(numberName).double
			.times(get(timesName).get(numberName).double)
			.numberValue
	}
