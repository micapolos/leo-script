package leo.prelude

import leo.compareTo
import leo.cosinus
import leo.field
import leo.fieldTo
import leo.isName
import leo.isValue
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
import leo.numberAnyField
import leo.numberAnyValue
import leo.numberName
import leo.numberOrThrow
import leo.plus
import leo.sinus
import leo.string
import leo.textAnyField
import leo.textAnyValue
import leo.textName
import leo.textOrThrow
import leo.times
import leo.value
import kotlin.math.PI
import kotlin.math.sqrt

val textAppendTextDefinition get() =
	nativeDefinition(
		value(
			textAnyField,
			appendName fieldTo textAnyValue)
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

val numberTextDefinition get() =
	nativeDefinition(
		value(
			textName fieldTo value(numberAnyField))) {
		value(field(literal(
			this
				.nativeValue(textName)
				.nativeValue(numberName)
				.numberOrThrow
				.string)))
	}

val numberPlusNumberDefinition get() =
	nativeDefinition(
		value(
			numberAnyField,
			plusName fieldTo value(numberAnyField))) {
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
		value(
			numberAnyField,
			minusName fieldTo value(numberAnyField))) {
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
		value(
			numberAnyField,
			timesName fieldTo value(numberAnyField))) {
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
		value(
			numberAnyField,
			isName fieldTo value(lessName fieldTo value(thanName fieldTo value(numberAnyField))))) {
			(nativeValue(numberName)
				.numberOrThrow <
					nativeValue(isName)
						.nativeValue(lessName)
						.nativeValue(thanName)
						.nativeValue(numberName)
						.numberOrThrow).isValue
	}

val numberSinusDefinition get() =
	nativeDefinition(
		value(sinusName fieldTo numberAnyValue)) {
		value(field(literal(
			this
				.nativeValue(sinusName)
				.nativeValue(numberName)
				.numberOrThrow
				.sinus)))
	}

val numberCosinusDefinition get() =
	nativeDefinition(
		value(cosinusName fieldTo numberAnyValue)) {
		value(field(literal(
			this
				.nativeValue(cosinusName)
				.nativeValue(numberName)
				.numberOrThrow
				.cosinus)))
	}

val numberRootDefinition get() =
	nativeDefinition(
		value(rootName fieldTo numberAnyValue)) {
		value(field(literal(
			this
				.nativeValue(rootName)
				.nativeValue(numberName)
				.numberOrThrow
				.double
				.let(::sqrt))))
	}

val piNumberDefinition get() =
	nativeDefinition(
		value(numberName fieldTo value(piName))) {
		value(field(literal(PI)))
	}
