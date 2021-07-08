package leo.term.evaluator

import leo.Script
import leo.compileName
import leo.errorName
import leo.lineTo
import leo.named.compiler.CompileError
import leo.script
import leo.term.compiler.native.typedValue
import leo.term.decompiler.script

val Script.evaluate: Script
	get() =
		try {
			typedValue.script
		} catch (compileError: CompileError) {
			script(
				errorName lineTo script(
					compileName lineTo compileError.scriptFn()))
		}
