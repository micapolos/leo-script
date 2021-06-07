package leo.type.compiler

import leo.Script
import leo.Type

val Script.type: Type get() =
	context().type(this)