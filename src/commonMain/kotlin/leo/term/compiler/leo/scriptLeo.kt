package leo.term.compiler.leo

import leo.Script
import leo.term.compiler.typedTerm

val Script.termScript: Script get() =
	scriptEnvironment.typedTerm(this).v.script