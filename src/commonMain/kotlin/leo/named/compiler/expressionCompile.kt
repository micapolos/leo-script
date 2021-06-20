package leo.named.compiler

import leo.named.expression.Expression
import leo.named.expression.Line
import leo.onlyOrNull

val Expression.line: Line get() = lineStack.onlyOrNull!!
val Expression.choiceLine: Line get() = line
