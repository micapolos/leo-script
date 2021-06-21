package leo.named.compiler

import leo.Script
import leo.errorName
import leo.lineTo
import leo.script

data class CompileError(val scriptFn: () -> Script): Error() {
	override fun toString() = script(errorName lineTo scriptFn()).toString()
}

fun compileError(fn: () -> Script): Nothing = throw CompileError(fn)
