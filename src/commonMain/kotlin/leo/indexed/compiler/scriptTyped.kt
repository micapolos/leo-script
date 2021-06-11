package leo.indexed.compiler

import leo.Script
import leo.get
import leo.indexed.typed.Typed
import leo.indexed.typed.TypedTuple
import leo.map

val Script.bodyTyped: Typed<Unit> get() =
	unitEnvironment.context.typedCompilation(this).get(unitEnvironment.context)

val Script.bodyTypedTuple: TypedTuple<Unit> get() =
	unitEnvironment.context.tupleCompilation(this).get(unitEnvironment.context)

val Script.paramsTuple: TypedTuple<Unit> get() =
	unitEnvironment.context.compiler.plusCompilation(this).map { it.context.paramsTuple }.get(unitEnvironment.context)

val Script.typed: Typed<Unit> get() =
	unitEnvironment.context.compiler.plusCompilation(this).map { it.typed }.get(unitEnvironment.context)
