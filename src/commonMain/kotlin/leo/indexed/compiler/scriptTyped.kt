package leo.indexed.compiler

import leo.Script
import leo.get
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple

val Script.typedTuple: TypedTuple<Unit> get() =
	unitEnvironment.context.vectorCompilation(this).get(unitEnvironment.context)

val Script.typed: Typed<Unit> get() =
	unitEnvironment.context.typedCompilation(this).get(unitEnvironment.context)
