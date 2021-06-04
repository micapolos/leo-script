package leo

data class Expression(val lineStack: Stack<ExpressionLine>)

sealed class ExpressionLine
data class GetExpressionLine(val get: ExpressionGet): ExpressionLine()

data class ExpressionGet(val name: String)

sealed class ExpressionAtom
data class FieldExpressionAtom(val name: String, val rhs: Expression): ExpressionAtom()
data class LiteralExpressionAtom(val literal: ExpressionLiteral): ExpressionAtom()
data class DoingExpressionAtom(val doing: ExpressionDoing): ExpressionAtom()

data class ExpressionField(val name: String, val rhs: Expression)
data class ExpressionLiteral(val literal: Literal)
data class ExpressionDoing(val lhsType: Type, val rhsExpression: Expression)