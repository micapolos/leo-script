package leo.named.typed

import leo.FieldScriptLine
import leo.LinkScript
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.ScriptLink
import leo.UnitScript

val Script.reflectTypedExpression: TypedExpression
  get() =
    when (this) {
      is UnitScript -> typedExpression()
      is LinkScript -> link.reflectTypedExpression
    }

val ScriptLink.reflectTypedExpression: TypedExpression
  get() =
    lhs.reflectTypedExpression.plus(line.reflectTypedLine)

val ScriptLine.reflectTypedLine: TypedLine
  get() =
    when (this) {
      is FieldScriptLine -> field.reflectTypedLine
      is LiteralScriptLine -> literal.reflectTypedLine
    }

val ScriptField.reflectTypedLine: TypedLine
  get() =
    name lineTo rhs.reflectTypedExpression

val Literal.reflectTypedLine: TypedLine
  get() =
    typedLine(this)