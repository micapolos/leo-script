package leo25.natives

import leo25.Dictionary
import leo25.dictionary
import leo25.plus

actual val nativeDictionary: Dictionary
	get() = dictionary()
		.plus(textAppendTextDefinition)
		.plus(numberPlusNumberDefinition)
		.plus(numberMinusNumberDefinition)
		.plus(numberTimesNumberDefinition)
		.plus(numberIsLessThanNumberDefinition)