package leo.term.compiler

import leo.Type
import leo.isStatic
import leo.term.compiler.native.Native
import leo.term.decompiler.script
import leo.term.idValue
import leo.term.typed.TypedTerm
import leo.term.typed.typed
import leo.type

val <V> TypedTerm<V>.type: Type get() =
	if (!t.isStatic) error("type not static")
	else typed(idValue<Native>(), t).script.type
