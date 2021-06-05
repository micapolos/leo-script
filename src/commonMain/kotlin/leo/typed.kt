package leo

data class Typed(val expression: TypedExpression, val type: Type)

data class TypedExpression(val lineStack: Stack<TypedLine>)

sealed class TypedLine
data class GetTypedLine(val get: TypedGet): TypedLine()

data class TypedGet(val name: String)

sealed class TypedAtom
data class FieldTypedAtom(val field: TypedField): TypedAtom()
data class LiteralTypedAtom(val literal: TypedLiteral): TypedAtom()
data class DoingTypedAtom(val doing: TypedDoing): TypedAtom()

data class TypedField(val name: String, val rhs: Typed)
data class TypedLiteral(val literal: Literal)
data class TypedDoing(val lhsType: Type, val rhsTyped: Typed)

fun TypedExpression.of(type: Type): Typed = Typed(this, type)
