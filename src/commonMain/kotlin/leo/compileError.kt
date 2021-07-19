package leo

data class CompileError(val scriptFn: () -> Script) : Error() {
  override fun toString() = script(errorName lineTo scriptFn()).toString()
}

fun compileError(fn: () -> Script): Nothing = throw CompileError(fn)
