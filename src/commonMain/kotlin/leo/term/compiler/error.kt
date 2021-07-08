package leo.term.compiler

import leo.ScriptLine
import leo.named.compiler.CompileError
import leo.script

fun compileError(scriptLine: ScriptLine): Nothing =
	throw CompileError { script(scriptLine) }