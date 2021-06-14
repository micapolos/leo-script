package leo.named.compiler

import leo.Type
import leo.TypeChoice
import leo.TypeDoing
import leo.TypeLine
import leo.TypeStructure
import leo.atom
import leo.choiceOrNull
import leo.doingOrNull
import leo.equalName
import leo.fieldOrNull
import leo.first
import leo.getLineOrNull
import leo.getName
import leo.getOrNull
import leo.isName
import leo.lineOrNull
import leo.lineTo
import leo.ofName
import leo.onlyLineOrNull
import leo.plus
import leo.script
import leo.structureOrNull
import leo.throwScriptIfNull

val Type.compileStructure: TypeStructure get() =
	structureOrNull.throwScriptIfNull { script("structure" lineTo script) }

val Type.compileChoice: TypeChoice
	get() =
		choiceOrNull.throwScriptIfNull { script("choice" lineTo script) }

val Type.compileLine: TypeLine get() =
	compileStructure.compileLine

val TypeStructure.compileLine: TypeLine get() =
	onlyLineOrNull.throwScriptIfNull { script("line" lineTo script) }

fun TypeLine.lineOrNull(name: String): TypeLine? =
	structureOrNull?.lineOrNull(name)

val TypeLine.resolveGetOrNull: TypeLine? get() =
	atom.fieldOrNull?.let { field ->
		field.rhsType.structureOrNull?.getLineOrNull(field.name)
	}

val Type.compileDoing: TypeDoing get() =
	doingOrNull.throwScriptIfNull { script("doing" lineTo script) }

fun <R> Type.check(type: Type, fn: () -> R): R =
	if (this != type) (null as R?).throwScriptIfNull { script.plus(isName lineTo script(equalName lineTo type.script)) }
	else fn()

fun Type.get(name: String): Type =
	getOrNull(name).throwScriptIfNull { script.plus(getName lineTo script(name)) }

fun Type.checkOf(type: Type): Type =
	compileLine.let { line ->
		type.compileChoice.lineStack.first { it == line }
			.throwScriptIfNull { script.plus(ofName lineTo type.script) }
			.let { type }
	}
