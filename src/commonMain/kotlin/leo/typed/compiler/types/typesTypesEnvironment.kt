package leo.typed.compiler.types

import leo.Types
import leo.typed.compiler.Environment

val typesTypesEnvironment: Environment<Types>
  get() =
    Environment(
      { literal -> error("") },
      { compiled -> null },
      { native -> error("") },
      { typesTypesEnvironment })
