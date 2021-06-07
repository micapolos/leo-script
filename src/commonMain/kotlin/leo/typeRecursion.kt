package leo

import leo.base.Effect
import leo.base.effect
import leo.base.notNullOrError

typealias TypeRecursion<T> = Stateful<TypeRecursive?, T>
val <T> T.typeRecursion: TypeRecursion<T> get() = stateful()

val Effect<TypeRecursive?, TypeLine>.typeRecursiveAtomEffect: Effect<TypeRecursive?, TypeAtom> get() =
	when (value) {
		is AtomTypeLine -> state.effect(value.atom)
		is RecursiveTypeLine -> value.recursive.effect(value.recursive.atom)
		is RecurseTypeLine -> state.effect(line(state.notNullOrError("recursion"))).typeRecursiveAtomEffect
	}

val TypeLine.atomRecursion: TypeRecursion<TypeAtom> get() =
	TypeRecursion { it.effect(this).typeRecursiveAtomEffect }

