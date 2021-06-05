package leo.kotlin

import leo.Stateful
import leo.getStateful
import leo.ret
import leo.setStateful

typealias Generation<T> = Stateful<Types, T>

val <T> T.generation: Generation<T> get() = ret()
val typesGeneration: Generation<Types> get() = getStateful()
val Types.setGeneration: Generation<Unit> get() = setStateful(this)
