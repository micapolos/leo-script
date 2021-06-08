package leo

val Type.normalizeRecursion: Type get() =
	updateLine { it.unshiftRecursion }
