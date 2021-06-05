package leo

data class Typed(val expression: Expression, val type: Type)

data class Expression(val lineStack: Stack<ExpressionLine>)

sealed class ExpressionLine
data class AtomExpressionLine(val atom: ExpressionAtom): ExpressionLine()
data class GetExpressionLine(val get: ExpressionGet): ExpressionLine()

sealed class ExpressionAtom
data class FieldExpressionAtom(val field: ExpressionField): ExpressionAtom()
data class LiteralExpressionAtom(val literal: Literal): ExpressionAtom()
data class DoingExpressionAtom(val doing: ExpressionDoing): ExpressionAtom()

data class ExpressionField(val name: String, val rhs: Typed)
data class ExpressionDoing(val lhsType: Type, val rhsTyped: Typed)

data class ExpressionGet(val name: String)

fun expression(vararg lines: ExpressionLine): Expression = Expression(stack(*lines))
