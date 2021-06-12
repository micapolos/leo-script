package leo.named.compiler

import leo.Type
import leo.TypeLine
import leo.TypeStructure
import leo.atom
import leo.base.indexed
import leo.base.notNullOrError
import leo.base.runIf
import leo.fieldOrNull
import leo.getOrNull
import leo.indexed.compiler.compileIndexOf
import leo.indexed.compiler.compileOnlyExpression
import leo.indexed.expression
import leo.indexed.invoke
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.indexed.typed.expressionTuple
import leo.indexed.typed.onlyTypedOrNull
import leo.indexed.typed.typed
import leo.indexed.variable
import leo.linkOrNull
import leo.named.expression.get
import leo.named.typed.TypedExpression
import leo.named.typed.TypedStructure
import leo.named.typed.typed
import leo.onlyLineOrNull
import leo.onlyOrNull
import leo.structure
import leo.structureOrNull
import leo.type

val <T> TypedStructure<T>.resolve: TypedStructure<T>
	get() = this

fun <T> IndexedValue<Binding>.apply(tuple: TypedTuple<T>): Typed<T> =
	typed(
		expression<T>(variable(index)).runIf(!value.isConstant) {
			expression(invoke(this, tuple.expressionTuple))
		},
		value.typeLine)

val <T> TypedTuple<T>.compileTyped: Typed<T>
	get() =
	onlyTypedOrNull.notNullOrError("$this not expression")

fun <T> Typed<T>.compileCast(typeLine: TypeLine): Typed<T> =
	typed(
		expression(typeLine.compileIndexOf(this.typeLine).indexed(expression)),
		typeLine)

val <T> TypedStructure<T>.compileOnlyExpression: TypedExpression<T> get() =
	typeStructure.compileOnlyExpression.let { typeLine ->
		typed(structure.expressionStack.onlyOrNull!!, typeLine)
	}

fun <R> Type.resolveInfix(fn: (Type, String, Type) -> R?): R? =
	structureOrNull?.resolveInfix(fn)

fun <R> TypeStructure.resolveInfix(fn: (Type, String, Type) -> R?): R? =
	lineStack.linkOrNull?.let { link ->
		link.head.atom.fieldOrNull?.let { field ->
			fn(link.tail.structure.type, field.name, field.rhsType)
		}
	}

val <T> TypedStructure<T>.onlyTypedExpressionOrNull: TypedExpression<T>? get() =
	typeStructure.onlyLineOrNull?.let {
		typed(structure.expressionStack.onlyOrNull!!, it)
	}

fun <T> TypedStructure<T>.getOrNull(name: String): TypedStructure<T>? =
	typeStructure.getOrNull(name)?.let {
		typed(structure.get(name), it)
	}