package leo.type.compiler

import leo.Dict
import leo.Type

@kotlin.jvm.JvmInline
value class TypeDictionary(val dict: Dict<String, Type>)
