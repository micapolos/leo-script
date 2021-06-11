package leo.indexed.compiler

import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.TypeStructure
import leo.base.firstOrNull
import leo.base.mapIndexed
import leo.base.notNullOrError
import leo.base.reverse
import leo.choiceOrNull
import leo.onlyLineOrNull
import leo.seq
import leo.structureOrNull

val Type.compileStructure: TypeStructure get() =
	structureOrNull.notNullOrError("$this not structure")

val Type.compileChoice: TypeChoice get() =
	choiceOrNull.notNullOrError("$this not choice")

val TypeStructure.compileOnlyExpression: TypeLine get() =
	onlyLineOrNull.notNullOrError("$this not a line")

fun TypeChoice.compileIndexOf(line: TypeLine): Int =
	lineStack.seq.reverse.mapIndexed.reverse.firstOrNull { value == line }.notNullOrError("$this select error").index

fun TypeLine.compileIndexOf(typeLine: TypeLine): Int =
	choiceOrNull?.let { choice ->
		typeLine.structureOrNull?.onlyLineOrNull?.let { line ->
			choice.compileIndexOf(line)
		}
	}?:error("$this.compileIndexOf($typeLine)")
