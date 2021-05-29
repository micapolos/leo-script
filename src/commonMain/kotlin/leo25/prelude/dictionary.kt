package leo25.prelude

import leo25.Dictionary
import leo25.natives.nativeDictionary
import leo25.plus

val preludeDictionary: Dictionary
	get() = nativeDictionary
		.plus(textAppendTextDefinition)
		.plus(numberPlusNumberDefinition)
		.plus(numberMinusNumberDefinition)
		.plus(numberTimesNumberDefinition)
		.plus(numberIsLessThanNumberDefinition)