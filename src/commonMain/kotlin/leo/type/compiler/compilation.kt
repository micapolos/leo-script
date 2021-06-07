package leo.type.compiler

import leo.Stateful
import leo.stateful

typealias TypeCompilation<T> = Stateful<TypeContext, T>
val <T> T.typeCompilaton: TypeCompilation<T> get() = stateful()

