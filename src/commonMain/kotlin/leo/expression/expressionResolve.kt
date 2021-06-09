package leo.expression

import leo.base.ifOrNull
import leo.get
import leo.isTypeLine
import leo.negateIsTypeLine

fun Expression.resolveEqual(expression: Expression): Expression =
	equal(expression).op.of(isTypeLine)

fun Expression.resolveGet(name: String): Expression =
	get(name).op of typeLine.get(name)

val Expression.resolveNegatedOrNull: Expression? get() =
	ifOrNull(typeLine == negateIsTypeLine) {
		(op as? MakeOp)?.make?.lhsStructure?.expressionOrNull
	}
