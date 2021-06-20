package leo.named.evaluator

import leo.lineTo
import leo.named.library.numberMinusNumberBody
import leo.named.library.numberPlusNumberBody
import leo.named.library.numberTextBody
import leo.named.library.numberTimesNumberBody
import leo.named.value.function
import leo.natives.minusName
import leo.natives.timesName
import leo.numberTypeLine
import leo.plusName
import leo.textName
import leo.type

val preludeDictionary get() =
	dictionary(
		definition(
			type(numberTypeLine, plusName lineTo type(numberTypeLine)),
			binding(function(dictionary(), numberPlusNumberBody))),
		definition(
			type(numberTypeLine, minusName lineTo type(numberTypeLine)),
			binding(function(dictionary(), numberMinusNumberBody))),
		definition(
			type(numberTypeLine, timesName lineTo type(numberTypeLine)),
			binding(function(dictionary(), numberTimesNumberBody))),
		definition(
			type(textName lineTo type(numberTypeLine)),
			binding(function(dictionary(), numberTextBody))))