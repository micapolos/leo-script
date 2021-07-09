package leo.term.compiler

import leo.lineTo
import leo.notName
import leo.script
import leo.theName

val String.selectBoolean: Boolean
  get() =
    when (this) {
      theName -> true
      notName -> false
      else -> compileError(script("select" lineTo script(
        this lineTo script(),
        "is" lineTo script("not" lineTo script("matching" lineTo script("choice" lineTo script(
          "the" lineTo script(),
          "not" lineTo script())))))))
    }
