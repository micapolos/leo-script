package leo.term.compiler

import leo.noName
import leo.yesName

val String.yesNoBoolean: Boolean get() =
	when (this) {
		yesName -> true
		noName -> false
		else -> error("$this.yesNoBoolean")
	}
