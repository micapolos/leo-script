package leo

val TypeLine.normalizedAtomRecursion: TypeRecursion<TypeAtom> get() =
	when (this) {
		is AtomTypeLine -> TODO()
		is RecurseTypeLine -> TODO()
		is RecursiveTypeLine -> TODO()
	}