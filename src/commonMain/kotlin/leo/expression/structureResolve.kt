package leo.expression

import leo.lineOrNull
import leo.lineTo
import leo.structureOrNull
import leo.type

fun Structure.resolveGetOrNull(name: String): Structure? =
	expressionOrNull?.let { expression ->
		expression.typeLine.structureOrNull?.lineOrNull(name)?.let { typeLine ->
			structure(expression.get(name).op of typeLine)
		}
	}

fun Structure.resolveMake(name: String): Structure =
	structure(make(name).op of (name lineTo type(typeStructure)))
