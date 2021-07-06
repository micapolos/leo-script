package leo.term.typed

import leo.Literal
import leo.Type
import leo.TypeLine
import leo.atomOrNull
import leo.base.fold
import leo.base.notNullIf
import leo.fieldOrNull
import leo.functionLineTo
import leo.isStatic
import leo.lineTo
import leo.linkOrNull
import leo.name
import leo.named.evaluator.any
import leo.onlyLineOrNull
import leo.plus
import leo.structure
import leo.structureOrNull
import leo.term.Term
import leo.term.Value
import leo.term.anyTerm
import leo.term.fn
import leo.term.head
import leo.term.id
import leo.term.invoke
import leo.term.plus
import leo.term.tail
import leo.type
import leo.typeLine

data class Typed<out V, out T>(val v: V, val t: T)
fun <V, T> typed(v: V, t: T) = Typed(v, t)

typealias TypedValue<V> = Typed<Value<V>, Type>
typealias TypedTerm<V> = Typed<Term<V>, Type>
typealias TypedLine<V> = Typed<Term<V>, TypeLine>

fun <V> typedTerm(): TypedTerm<V> = Typed(id(), type())

fun <V> TypedTerm<V>.plus(line: TypedLine<V>): TypedTerm<V> =
	typed(
		if (t.isStatic)
			if (line.t.isStatic) id()
			else line.v
		else
			if (line.t.isStatic) v
			else v.plus(line.v),
		t.plus(line.t))

infix fun <V> String.lineTo(typed: TypedTerm<V>): TypedLine<V> =
	typed(typed.v, this lineTo typed.t)

fun <V> typedTerm(vararg lines: TypedLine<V>): TypedTerm<V> =
	typedTerm<V>().fold(lines) { plus(it) }

fun typedLine(literal: Literal): TypedLine<Any?> =
	typed(literal.any.anyTerm, literal.typeLine)

val <V> TypedTerm<V>.pairOrNull: Pair<TypedTerm<V>, TypedLine<V>>? get() =
	t.structureOrNull?.lineStack?.linkOrNull?.let { link ->
		link.tail.structure.type.let { type ->
			link.head.let { line ->
				if (type.isStatic)
					if (line.isStatic) typed(id<V>(), type) to typed(id(), line)
					else typed(id<V>(), type) to typed(v, line)
				else
					if (line.isStatic) typed(v, type) to typed(id(), line)
					else typed(v.tail, type) to typed(v.head, line)
			}
		}
	}

val <V> TypedTerm<V>.headOrNull: TypedTerm<V>? get() =
	pairOrNull?.second?.let { typedTerm(it) }

val <V> TypedTerm<V>.tailOrNull: TypedTerm<V>? get() =
	pairOrNull?.first

val <V> TypedTerm<V>.contentOrNull: TypedTerm<V>? get() =
	t.onlyLineOrNull?.atomOrNull?.fieldOrNull?.rhsType?.let { type ->
		typed(v, type)
	}

fun <V> TypedTerm<V>.getOrNull(name: String): TypedTerm<V>? =
	contentOrNull?.lineOrNull(name)?.let { typedTerm(it) }

fun <V> TypedTerm<V>.lineOrNull(name: String): TypedLine<V>? =
	pairOrNull?.let { (typedTerm, typedLine) ->
		null
			?: typedLine.orNull(name)
			?: typedTerm.lineOrNull(name)
	}

fun <V> TypedLine<V>.orNull(name: String): TypedLine<V>? =
	notNullIf(t.name == name) { this }

fun <V> TypedTerm<V>.make(name: String): TypedTerm<V> =
	typedTerm(name lineTo this)

fun <V> TypedTerm<V>.do_(typedTerm: TypedTerm<V>): TypedTerm<V> =
	typed(fn(typedTerm.v).invoke(v), typedTerm.t)

fun <V> typedFunctionLine(type: Type, typedTerm: TypedTerm<V>): TypedLine<V> =
	typed(fn(typedTerm.v), type functionLineTo typedTerm.t)