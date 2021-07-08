package leo.named.value

import leo.functionName
import leo.nativeName
import leo.numberName
import leo.textName

val ValueLine.name
  get() =
    when (this) {
      is AnyValueLine -> any.valueName
      is FieldValueLine -> field.name
      is FunctionValueLine -> function.name
    }

val Any?.valueName: String
  get() =
    when (this) {
      is String -> textName
      is Double -> numberName
      else -> nativeName
    }

@Suppress("unused")
val ValueFunction.name: String
  get() =
    functionName