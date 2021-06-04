package leo

data class Type(val choiceStack: Stack<TypeChoice>)

data class TypeChoice(val lineStack: Stack<TypeLine>)

sealed class TypeLine
data class FieldTypeLine(val field: TypeField): TypeLine()
data class LiteralTypeLine(val text: TypeLiteral): TypeLine()
data class DoingTypeLine(val doing: TypeDoing): TypeLine()
data class ListTypeLine(val list: TypeList): TypeLine()

data class TypeField(val name: String, val type: Type)

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
