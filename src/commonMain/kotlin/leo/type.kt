package leo

data class Type(val choiceStack: Stack<TypeChoice>)

data class TypeChoice(val lineStack: Stack<TypeLine>)

sealed class TypeLine
data class FieldTypeLine(val field: TypeField): TypeLine()
data class LiteralTypeLine(val text: TypeLiteral): TypeLine()
data class DoingTypeLine(val doing: TypeDoing): TypeLine()
data class ListTypeLine(val list: TypeList): TypeLine()

data class TypeField(val name: String, val typeRhs: TypeRhs)

sealed class TypeRhs
data class TypeTypeRhs(val type: Type): TypeRhs()
data class RecursiveTypeRhs(val recursive: TypeRecursive): TypeRhs()

sealed class TypeLiteral
data class TextTypeLiteral(val text: TypeText): TypeLiteral()
data class NumberTypeLiteral(val number: TypeNumber): TypeLiteral()

data class TypeDoing(val lhsType: Type, val rhsType: Type)
data class TypeList(val itemLine: TypeLine)

data class TypeRecursive(val line: TypeLine)

object TypeText
object TypeNumber

val Stack<TypeChoice>.type get() = Type(this)
val Stack<TypeLine>.choice get() = TypeChoice(this)

fun type(vararg choices: TypeChoice) = stack(*choices).type
fun choice(vararg lines: TypeLine) = stack(*lines).choice
fun type(line: TypeLine, vararg lines: TypeLine) = stack(stackLink(line, *lines).map { choice(this) }).type

fun rhs(type: Type): TypeRhs = TypeTypeRhs(type)
fun rhs(recursive: TypeRecursive): TypeRhs = RecursiveTypeRhs(recursive)

infix fun String.fieldTo(rhs: TypeRhs) = TypeField(this, rhs)
infix fun String.fieldTo(type: Type) = this fieldTo rhs(type)

val typeText get() = TypeText
val typeNumber get() = TypeNumber

fun literal(text: TypeText): TypeLiteral = TextTypeLiteral(text)
fun literal(number: TypeNumber): TypeLiteral = NumberTypeLiteral(number)

infix fun Type.doing(type: Type) = TypeDoing(this, type)

fun list(line: TypeLine) = TypeList(line)

fun line(field: TypeField): TypeLine = FieldTypeLine(field)
fun line(literal: TypeLiteral): TypeLine = LiteralTypeLine(literal)
fun line(doing: TypeDoing): TypeLine = DoingTypeLine(doing)
fun line(list: TypeList): TypeLine = ListTypeLine(list)

infix fun String.lineTo(rhs: TypeRhs) = line(this fieldTo rhs)
infix fun String.lineTo(type: Type) = this lineTo rhs(type)
