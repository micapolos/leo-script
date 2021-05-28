package leo25.natives

import leo14.compareTo
import leo14.lineTo
import leo14.literal
import leo14.minus
import leo14.plus
import leo14.script
import leo14.times
import leo14.compareTo
import leo25.anyName
import leo25.field
import leo25.isName
import leo25.isValue
import leo25.numberName
import leo25.numberOrThrow
import leo25.textName
import leo25.textOrThrow
import leo25.value

val textAppendTextDefinition get() =
	nativeDefinition(
		script(
			textName lineTo script(anyName),
			appendName lineTo script(textName lineTo script(anyName)))) {
		value(field(literal(
			nativeValue(textName)
				.textOrThrow
				.plus(
					nativeValue(appendName)
						.nativeValue(textName)
						.textOrThrow))))
	}

val numberPlusNumberDefinition get() =
	nativeDefinition(
		script(
			numberName lineTo script(anyName),
			plusName lineTo script(numberName lineTo script(anyName)))) {
		value(field(literal(
			nativeValue(numberName)
				.numberOrThrow
				.plus(
					nativeValue(plusName)
						.nativeValue(numberName)
						.numberOrThrow))))
	}

val numberMinusNumberDefinition get() =
	nativeDefinition(
		script(
			numberName lineTo script(anyName),
			minusName lineTo script(numberName lineTo script(anyName)))) {
		value(field(literal(
			nativeValue(numberName)
				.numberOrThrow
				.minus(
					nativeValue(minusName)
						.nativeValue(numberName)
						.numberOrThrow))))
	}

val numberTimesNumberDefinition get() =
	nativeDefinition(
		script(
			numberName lineTo script(anyName),
			timesName lineTo script(numberName lineTo script(anyName)))) {
		value(field(literal(
			nativeValue(numberName)
				.numberOrThrow
				.times(
					nativeValue(timesName)
						.nativeValue(numberName)
						.numberOrThrow))))
	}

val numberIsLessThanNumberDefinition get() =
	nativeDefinition(
		script(
			numberName lineTo script(anyName),
			isName lineTo script(lessName lineTo script(thanName lineTo script(numberName lineTo script(anyName)))))) {
			(nativeValue(numberName)
				.numberOrThrow <
					nativeValue(isName)
						.nativeValue(lessName)
						.nativeValue(thanName)
						.nativeValue(numberName)
						.numberOrThrow).isValue
	}
