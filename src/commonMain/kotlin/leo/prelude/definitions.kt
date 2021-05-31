package leo.prelude

import leo.anyName
import leo.compareTo
import leo.field
import leo.isName
import leo.isValue
import leo.lineTo
import leo.literal
import leo.minus
import leo.natives.appendName
import leo.natives.lessName
import leo.natives.minusName
import leo.natives.nativeDefinition
import leo.natives.nativeValue
import leo.natives.plusName
import leo.natives.thanName
import leo.natives.timesName
import leo.numberName
import leo.numberOrThrow
import leo.plus
import leo.script
import leo.textName
import leo.textOrThrow
import leo.times
import leo.value
import kotlin.math.PI

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

val piNumberDefinition get() =
	nativeDefinition(
		script(
			piName lineTo script(),
			numberName lineTo script())) {
		value(field(literal(PI)))
	}
