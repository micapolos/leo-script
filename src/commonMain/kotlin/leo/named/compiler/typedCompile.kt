package leo.named.compiler

import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.TypeStructure
import leo.atom
import leo.base.notNullOrError
import leo.base.runIf
import leo.choiceOrNull
import leo.fieldOrNull
import leo.getOrNull
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
import leo.named.typed.TypedLine
import leo.named.typed.typed
import leo.onlyLineOrNull
import leo.onlyOrNull
import leo.structure
import leo.structureOrNull
import leo.type

val <T> TypedExpression<T>.resolve: TypedExpression<T>
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

val <T> TypedExpression<T>.compileOnlyLine: TypedLine<T> get() =
	typeStructure.compileOnlyExpression.let { typeLine ->
		typed(expression.lineStack.onlyOrNull!!, typeLine)
	}

fun <R> Type.resolveInfix(fn: (Type, String, Type) -> R?): R? =
	structureOrNull?.resolveInfix(fn)

fun <R> TypeStructure.resolveInfix(fn: (Type, String, Type) -> R?): R? =
	lineStack.linkOrNull?.let { link ->
		link.head.atom.fieldOrNull?.let { field ->
			fn(link.tail.structure.type, field.name, field.rhsType)
		}
	}

val <T> TypedExpression<T>.onlyLineOrNull: TypedLine<T>? get() =
	typeStructure.onlyLineOrNull?.let {
		typed(expression.lineStack.onlyOrNull!!, it)
	}

fun <T> TypedExpression<T>.getOrNull(name: String): TypedExpression<T>? =
	typeStructure.getOrNull(name)?.let {
		typed(expression.get(name), it)
	}

val Type.compileStructure: TypeStructure get() =
	structureOrNull.notNullOrError("$this not structure")

val Type.compileChoice: TypeChoice
	get() =
	choiceOrNull.notNullOrError("$this not choice")

val TypeStructure.compileOnlyExpression: TypeLine get() =
	onlyLineOrNull.notNullOrError("$this not a line")

