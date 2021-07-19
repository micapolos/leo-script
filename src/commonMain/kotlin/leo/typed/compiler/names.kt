package leo.typed.compiler

import leo.doName
import leo.doingName
import leo.eitherName
import leo.lineTo
import leo.notName
import leo.repeatName
import leo.repeatingName
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

val String.nameBlockIsRepeat: Boolean get() =
  when (this) {
    doName -> false
    repeatName -> true
    else -> compileError(
      script(
        "expected" lineTo script(
          eitherName lineTo script(doName),
          eitherName lineTo script(repeatName))))
  }

val String.nameFunctionIsRepeat: Boolean get() =
  when (this) {
    doingName -> false
    repeatingName -> true
    else -> compileError(
      script(
        "expected" lineTo script(
          eitherName lineTo script(doingName),
          eitherName lineTo script(repeatingName))))
  }
