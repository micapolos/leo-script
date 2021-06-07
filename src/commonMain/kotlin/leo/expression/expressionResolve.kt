package leo.expression

import leo.base.ifOrNull
import leo.isTypeLine
import leo.negateIsTypeLine

fun Expression.resolveEqual(expression: Expression): Expression =
	equal(expression).op.of(isTypeLine)

val Expression.resolveNegatedOrNull: Expression? get() =
	ifOrNull(typeLine == negateIsTypeLine) {
		(op as? MakeOp)?.make?.lhsStructure?.expressionOrNull
	}
