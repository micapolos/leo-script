package leo.named.compiler

import leo.Type
import leo.TypeStructure
import leo.atom
import leo.fieldOrNull
import leo.getOrNull
import leo.linkOrNull
import leo.named.expression.get
import leo.named.expression.linkOrNull
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.typed
import leo.structure
import leo.structureOrNull
import leo.type

val TypedExpression.resolve: TypedExpression
	get() = this

val TypedExpression.compileOnlyLine: TypedLine get() =
	type.compileLine.let { typeLine ->
		typed(expression.linkOrNull!!.line, typeLine)
	}

fun <R> Type.resolveInfix(fn: (Type, String, Type) -> R?): R? =
	structureOrNull?.resolveInfix(fn)

fun <R> TypeStructure.resolveInfix(fn: (Type, String, Type) -> R?): R? =
	lineStack.linkOrNull?.let { link ->
		link.head.atom.fieldOrNull?.let { field ->
			fn(link.tail.structure.type, field.name, field.rhsType)
		}
	}

fun TypedExpression.getOrNull(name: String): TypedExpression? =
	type.getOrNull(name)?.let {
		typed(expression.get(name), it)
	}
