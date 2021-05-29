package leo25.prelude

import leo25.anyName
import leo25.compareTo
import leo25.field
import leo25.isName
import leo25.isValue
import leo25.lineTo
import leo25.literal
import leo25.minus
import leo25.natives.appendName
import leo25.natives.lessName
import leo25.natives.minusName
import leo25.natives.nativeDefinition
import leo25.natives.nativeValue
import leo25.natives.plusName
import leo25.natives.thanName
import leo25.natives.timesName
import leo25.numberName
import leo25.numberOrThrow
import leo25.plus
import leo25.script
import leo25.textName
import leo25.textOrThrow
import leo25.times
import leo25.value

val textAppendTextDefinition get() =
	nativeDefinition(
		script(
			textName lineTo script(anyName),
			appendName lineTo script(textName lineTo script(anyName))
		)
	) {
		value(field(
			literal(
			nativeValue(textName)
				.textOrThrow
				.plus(
					nativeValue(appendName)
						.nativeValue(textName)
						.textOrThrow))
		))
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
