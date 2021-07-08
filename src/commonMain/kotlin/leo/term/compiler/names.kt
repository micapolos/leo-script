package leo.term.compiler

import leo.notName
import leo.theName

val String.selectBoolean: Boolean
  get() =
    when (this) {
      theName -> true
      notName -> false
      else -> error("$this.yesNoBoolean")
    }
