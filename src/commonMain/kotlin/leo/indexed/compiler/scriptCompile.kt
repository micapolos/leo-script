package leo.indexed.compiler

import leo.Script
import leo.ScriptLine
import leo.base.notNullOrError
import leo.onlyLineOrNull

val Script.compileOnlyLine: ScriptLine get() =
	onlyLineOrNull.notNullOrError("$this.compileOnlyLineó")