package leo

import kotlin.reflect.KClass

sealed class Type

object EmptyType: Type()
data class FieldType(val field: TypeField): Type()

data class TypeField(val name: String, val rhs: TypeRhs)

sealed class TypeRhs
data class ClassTypeRhs(val class_ : KClass<*>): TypeRhs()
data class ArrowTypeRhs(val arrow: Arrow): TypeRhs()
data class TypeTypeRhs(val type: Type): TypeRhs()
data class ValueTypeRhs(val value: Value): TypeRhs()

data class Arrow(val lhsType: Type, val rhsType: Type)
