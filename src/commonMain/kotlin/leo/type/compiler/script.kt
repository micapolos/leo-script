package leo.type.compiler

import leo.Script
import leo.TypeStructure

val Script.typeStructure: TypeStructure get() =
	context().structure(this)