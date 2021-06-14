package leo.named.compiler

import leo.named.expression.Expression
import leo.named.expression.Line
import leo.named.expression.linkOrNull

val Expression.line: Line get() = linkOrNull!!.line
val Expression.choiceLine: Line get() = line
