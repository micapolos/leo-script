package leo.term.typed

import leo.Type
import leo.TypeLine
import leo.isStatic
import leo.linkOrNull
import leo.plus
import leo.structure
import leo.structureOrNull
import leo.term.Term
import leo.term.Value
import leo.term.head
import leo.term.id
import leo.term.plus
import leo.term.tail
import leo.type

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

val <V> TypedTerm<V>.headOrNull: TypedTerm<V>? get() =
	t.structureOrNull?.lineStack?.linkOrNull?.let { link ->
		link.tail.structure.type.let { type ->
			link.head.let { line ->
				typed(
					if (type.isStatic)
						if (line.isStatic) id()
						else v
					else
						if (line.isStatic) v
						else v.head,
				  type(line))
			}
		}
	}

val <V> TypedTerm<V>.tailOrNull: TypedTerm<V>? get() =
	t.structureOrNull?.lineStack?.linkOrNull?.let { link ->
		link.tail.structure.type.let { type ->
			link.head.let { line ->
				typed(
					if (type.isStatic)
						if (line.isStatic) id()
						else v
					else
						if (line.isStatic) v
						else v.tail,
					type)
			}
		}
	}
