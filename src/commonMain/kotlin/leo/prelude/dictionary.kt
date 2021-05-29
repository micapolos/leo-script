package leo.prelude

import leo.Dictionary
import leo.natives.nativeDictionary
import leo.plus

val preludeDictionary: Dictionary
	get() = nativeDictionary
		.plus(textAppendTextDefinition)
		.plus(numberPlusNumberDefinition)
		.plus(numberMinusNumberDefinition)
		.plus(numberTimesNumberDefinition)
		.plus(numberIsLessThanNumberDefinition)