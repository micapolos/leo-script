package leo.term.compiler

import leo.Type
import leo.TypeLine
import leo.choice
import leo.equalsName
import leo.isStatic
import leo.lineTo
import leo.noName
import leo.term.compiler.native.Native
import leo.term.decompiler.script
import leo.term.idValue
import leo.term.typed.TypedTerm
import leo.term.typed.typed
import leo.type
import leo.yesName

val <V> TypedTerm<V>.type: Type get() =
	if (!t.isStatic) error("type not static")
	else typed(idValue<Native>(), t).script.type

val equalsTypeLine: TypeLine get() =
	equalsName lineTo type(
		choice(
			yesName lineTo type(),
			noName lineTo type()))
