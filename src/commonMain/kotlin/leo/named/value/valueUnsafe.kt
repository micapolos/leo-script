package leo.named.value

import leo.base.notNullOrError
import leo.lineTo
import leo.named.evaluator.Evaluation
import leo.named.evaluator.invokeEvaluation
import leo.onlyOrNull
import leo.script
import leo.throwScriptIfNull

fun numberValueLine(double: Double): ValueLine = anyValueLine(double)
fun numberValueLine(int: Int): ValueLine = numberValueLine(int.toDouble())
fun textValueLine(string: String): ValueLine = anyValueLine(string)

fun numberValue(double: Double): Value = value(numberValueLine(double))
fun numberValue(int: Int): Value = value(numberValueLine(int))
fun textValue(string: String): Value = value(textValueLine(string))

val Double.numberValue get() = value(anyValueLine(this))
val Int.numberValue get() = toDouble().numberValue
val String.textValue get() = value(anyValueLine(this))

val Any?.anyValueLine: ValueLine get() = anyValueLine(this)
val Any?.anyValue: Value get() = value(anyValueLine)

val Value.unsafeLine: ValueLine
  get() =
    lineStack.onlyOrNull.notNullOrError("$this not a single line")

val Value.unsafeSwitchLine: ValueLine
  get() =
    unsafeLine.field.value.unsafeLine

val Value.unsafeFunction: ValueFunction
  get() =
    (unsafeLine as? FunctionValueLine)
      ?.function
      .throwScriptIfNull { script("function" lineTo script) }

val Value.unsafeAny: Any?
  get() =
    unsafeLine.unsafeAny

val Value.double: Double
  get() =
    (unsafeAny as? Double).throwScriptIfNull { script("double" lineTo script) }

val Value.unsafeString: String
  get() =
    (unsafeAny as? String).throwScriptIfNull { script("string" lineTo script) }

val ValueLine.unsafeAny: Any?
  get() =
    (this as AnyValueLine).any

val Value.unsafeInt: Int
  get() =
    (unsafeLine.unsafeAny as Int)

fun Value.intPlusInt(value: Value): Value =
  unsafeInt.plus(value.unsafeInt).anyValue

fun Value.numberPlusNumber(value: Value): Value =
  double.plus(value.double).numberValue

fun Value.giveEvaluation(value: Value): Evaluation<Value> =
  unsafeFunction.invokeEvaluation(value)

fun Value.takeEvaluation(value: Value): Evaluation<Value> =
  value.unsafeFunction.invokeEvaluation(this)
