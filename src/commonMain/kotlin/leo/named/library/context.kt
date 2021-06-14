package leo.named.library

import leo.lineTo
import leo.named.compiler.Context
import leo.named.compiler.context
import leo.named.compiler.plus
import leo.natives.minusName
import leo.numberTypeLine
import leo.plusName
import leo.type

val preludeContext: Context get() =
	context()
		.plus(
			type(numberTypeLine, plusName lineTo type(numberTypeLine)),
			type(numberTypeLine),
			numberPlusNumberFunction)
		.plus(
			type(numberTypeLine, minusName lineTo type(numberTypeLine)),
			type(numberTypeLine),
			numberMinusNumberFunction)
