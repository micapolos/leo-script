package leo.prelude

import leo.Dictionary
import leo.natives.nativeDictionary
import leo.plus

val preludeDictionary: Dictionary
	get() = nativeDictionary
		.plus(textAppendTextDefinition)
		.plus(numberTextDefinition)
		.plus(numberPlusNumberDefinition)
		.plus(numberMinusNumberDefinition)
		.plus(numberTimesNumberDefinition)
		.plus(numberDividedByNumberDefinition)
		.plus(numberIsLessThanNumberDefinition)
		.plus(numberSinusDefinition)
		.plus(numberCosinusDefinition)
		.plus(numberRootDefinition)
		.plus(piNumberDefinition)