package leo.expression

import leo.isTypeLine

fun Expression.resolveEqual(expression: Expression): Expression =
	equal(expression).op.of(isTypeLine)
