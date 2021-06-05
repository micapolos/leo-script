package leo

data class Typed(val expression: Expression, val type: Type)
data class TypedStructure(val expression: Expression, val typeStructure: TypeStructure)
data class TypedLine(val expressionLine: ExpressionLine, val typeLine: TypeLine)

data class Expression(val lineStack: Stack<ExpressionLine>)

sealed class ExpressionLine
data class AtomExpressionLine(val atom: ExpressionAtom): ExpressionLine()
data class GetExpressionLine(val get: ExpressionGet): ExpressionLine()

sealed class ExpressionAtom
data class FieldExpressionAtom(val field: ExpressionField): ExpressionAtom()
data class LiteralExpressionAtom(val literal: Literal): ExpressionAtom()
data class DoingExpressionAtom(val doing: ExpressionDoing): ExpressionAtom()

data class ExpressionField(val name: String, val rhs: Typed)
data class ExpressionDoing(val lhsType: TypeStructure, val rhsTyped: TypeLine)

data class ExpressionGet(val lhs: ExpressionLine, val name: String)

infix fun Expression.of(type: Type) = Typed(this, type)
infix fun Expression.of(type: TypeStructure) = TypedStructure(this, type)
fun expression(vararg lines: ExpressionLine): Expression = Expression(stack(*lines))

val emptyTyped: Typed get() = expression().of(type())
val emptyTypedStructure: TypedStructure get() = expression().of(typeStructure())

fun Expression.plus(line: ExpressionLine): Expression = Expression(lineStack.push(line))

fun ExpressionLine.get(name: String) = ExpressionGet(this, name)

fun line(atom: ExpressionAtom): ExpressionLine = AtomExpressionLine(atom)
fun line(get: ExpressionGet): ExpressionLine = GetExpressionLine(get)

fun expressionAtom(literal: Literal): ExpressionAtom = LiteralExpressionAtom(literal)
fun atom(field: ExpressionField): ExpressionAtom = FieldExpressionAtom(field)
fun atom(doing: ExpressionDoing): ExpressionAtom = DoingExpressionAtom(doing)

infix fun String.fieldTo(typed: Typed) = ExpressionField(this, typed)

val TypedStructure.typed: Typed get() = expression.of(type(typeStructure))