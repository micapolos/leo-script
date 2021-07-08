package leo.named.value

import leo.base.orNullIf
import leo.mapFirst
import leo.onlyOrNull

fun Value.line(name: String): ValueLine =
  lineStack.mapFirst { orNullIf(this.name != name) }!!

val Value.line: ValueLine
  get() =
    lineStack.onlyOrNull!!

val ValueLine.field: ValueField
  get() =
    (this as FieldValueLine).field

fun ValueLine.get(name: String): ValueLine =
  field.value.line(name)

fun Value.get(name: String): Value =
  value(line.get(name))
