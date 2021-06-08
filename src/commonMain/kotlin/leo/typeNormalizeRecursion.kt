package leo

val Type.normalizeRecursion: Type get() =
	mapLine { it.unshiftRecursion }
