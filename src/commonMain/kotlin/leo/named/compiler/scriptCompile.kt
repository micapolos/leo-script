package leo.named.compiler

import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.base.notNullOrError
import leo.fieldOrNull
import leo.onlyLineOrNull

val Script.compileLine: ScriptLine
  get() =
    onlyLineOrNull.notNullOrError("$this.compileOnlyLine")

val ScriptLine.compileField: ScriptField
  get() =
    fieldOrNull.notNullOrError("$this.compileField")

val Script.compileField: ScriptField
  get() =
    compileLine.compileField