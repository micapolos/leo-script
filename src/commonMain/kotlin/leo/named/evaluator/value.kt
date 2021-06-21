package leo.named.evaluator

import leo.isName
import leo.named.value.Value
import leo.named.value.lineTo
import leo.named.value.value
import leo.noName
import leo.yesName

val Boolean.isValue: Value get() =
	value(isName lineTo value(if (this) yesName else noName))

fun Value.isEqualTo(value: Value): Value =
	equals(value).isValue