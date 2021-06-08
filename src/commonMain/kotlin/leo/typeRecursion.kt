package leo

import leo.base.Effect
import leo.base.effect
import leo.base.notNullOrError

typealias TypeRecursion<T> = Stateful<TypeRecursive?, T>
val <T> T.typeRecursion: TypeRecursion<T> get() = stateful()
val <T> TypeRecursion<T>.apply: T get() = get(null)

@JvmName("typeRecursiveLineAtomEffect")
fun Effect<TypeRecursive?, TypeLine>.typeRecursiveAtomEffect(): Effect<TypeRecursive?, TypeAtom> =
	when (value) {
		is RecursiveTypeLine -> value.recursive.effect(value.recursive.line.shiftRecursion).typeRecursiveAtomEffect()
		is RecursibleTypeLine -> state.effect(value.recursible).typeRecursiveAtomEffect()
	}

@JvmName("typeRecursiveRecursibleAtomEffect")
fun Effect<TypeRecursive?, TypeRecursible>.typeRecursiveAtomEffect(): Effect<TypeRecursive?, TypeAtom> =
	when (value) {
		is AtomTypeRecursible -> state.effect(value.atom)
		is RecurseTypeRecursible -> state.effect(line(state.notNullOrError("recursion"))).typeRecursiveAtomEffect()
	}

val TypeLine.atomRecursion: TypeRecursion<TypeAtom> get() =
	TypeRecursion { it.effect(this).typeRecursiveAtomEffect() }
