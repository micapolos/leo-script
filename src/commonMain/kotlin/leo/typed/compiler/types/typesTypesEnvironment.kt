@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package leo.typed.compiler.types

import leo.Types
import leo.script
import leo.typed.compiled.Compiled
import leo.typed.compiler.Environment
import leo.typed.compiler.compileError

fun typesEnvironment(resolveOrNullFn: (Compiled<Types>) -> Compiled<Types>?): Environment<Types> =
  Environment(
    { literal -> compileError(script("literal")) },
    resolveOrNullFn,
    { native -> compileError(script("native")) },
    { typesTypesEnvironment },
    { typeLine -> null })

val typesTypesEnvironment: Environment<Types>
  get() =
    typesEnvironment { null }
