package leo.typed.indexed.types

import leo.Script
import leo.Type
import leo.Types
import leo.typed.indexed.Value
import leo.typed.indexed.ValueScriptContext
import leo.typed.indexed.script

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
val typesValueScriptContext: ValueScriptContext<Types>
  get() = ValueScriptContext(
    { native -> error("") },
    { value, typeLine -> null })

fun Value<Types>.script(type: Type): Script =
  script(type, typesValueScriptContext)

