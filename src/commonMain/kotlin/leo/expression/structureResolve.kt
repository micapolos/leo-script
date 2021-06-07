package leo.expression

import leo.lineOrNull
import leo.lineTo
import leo.structureOrNull
import leo.type

// TODO: Resolve normalized structures, because otherwise it won't work when used as prefix expression.
fun Structure.resolveGetOrNull(name: String): Structure? =
	expressionOrNull?.let { expression ->
		expression.typeLine.structureOrNull?.lineOrNull(name)?.let { typeLine ->
			structure(expression.get(name).op of typeLine)
		}
	}

fun Structure.resolveMake(name: String): Structure =
	structure(make(name).op of (name lineTo type(typeStructure)))

fun Structure.applyBind(expression: Expression): Structure =
	structure(bind(expression).op of expression.typeLine)

val Structure.resolveNegatedOrNull: Structure? get() =
	expressionOrNull?.resolveNegatedOrNull?.structure