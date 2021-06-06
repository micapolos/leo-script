package leo.expression

import leo.get
import leo.kotlin.Kotlin

val Expression.kotlin: Kotlin get() =
	kotlinCompilation.get(compiler())

val Expression.fullKotlin: Kotlin get() =
	fullKotlinCompilation.get(compiler())
