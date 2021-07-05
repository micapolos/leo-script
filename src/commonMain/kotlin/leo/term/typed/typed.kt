package leo.term.typed

import leo.Type
import leo.TypeLine
import leo.isStatic
import leo.plus
import leo.term.Term
import leo.term.id
import leo.term.plus
import leo.type

data class Typed<out V, out T>(val v: V, val t: T)
fun <V, T> typed(v: V, t: T) = Typed(v, t)

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
