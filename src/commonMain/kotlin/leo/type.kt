package leo

sealed class Type
data class StructureType(val structure: TypeStructure): Type()
data class ChoiceType(val choice: TypeChoice): Type()

data class TypeChoice(val lineStack: Stack<TypeLine>)
data class TypeStructure(val lineStack: Stack<TypeLine>)

sealed class TypeLine
data class AtomTypeLine(val atom: TypeAtom): TypeLine()
data class RecursiveTypeLine(val recursive: TypeRecursive): TypeLine()
data class RecurseTypeLine(val recurse: TypeRecurse): TypeLine()

sealed class TypeAtom
data class FieldTypeAtom(val field: TypeField): TypeAtom()
data class LiteralTypeAtom(val literal: TypeLiteral): TypeAtom()
data class DoingTypeAtom(val doing: TypeDoing): TypeAtom()
data class ListTypeAtom(val list: TypeList): TypeAtom()

data class TypeField(val name: String, val type: Type)

sealed class TypeLiteral
data class TextTypeLiteral(val text: TypeText): TypeLiteral()
data class NumberTypeLiteral(val number: TypeNumber): TypeLiteral()

data class TypeDoing(val lhsType: Type, val rhsType: Type)
data class TypeList(val itemAtom: TypeAtom)

data class TypeRecursive(val line: TypeLine)

object TypeText
object TypeNumber
object TypeRecurse

val Stack<TypeLine>.structure get() = TypeStructure(this)
val Stack<TypeLine>.choice get() = TypeChoice(this)

fun type(structure: TypeStructure): Type = StructureType(structure)
fun type(choice: TypeChoice): Type = ChoiceType(choice)

fun structure(vararg lines: TypeLine): TypeStructure = stack(*lines).structure
fun choice(vararg lines: TypeLine): TypeChoice = stack(*lines).choice
fun type(vararg lines: TypeLine): Type = type(structure(*lines))

infix fun String.fieldTo(type: Type) = TypeField(this, type)

val typeText get() = TypeText
val typeNumber get() = TypeNumber
val typeRecurse get() = TypeRecurse

fun literal(text: TypeText): TypeLiteral = TextTypeLiteral(text)
fun literal(number: TypeNumber): TypeLiteral = NumberTypeLiteral(number)

infix fun Type.doing(type: Type) = TypeDoing(this, type)

fun list(atom: TypeAtom) = TypeList(atom)

fun atom(field: TypeField): TypeAtom = FieldTypeAtom(field)
fun atom(literal: TypeLiteral): TypeAtom = LiteralTypeAtom(literal)
fun atom(doing: TypeDoing): TypeAtom = DoingTypeAtom(doing)
fun atom(list: TypeList): TypeAtom = ListTypeAtom(list)

fun line(atom: TypeAtom): TypeLine = AtomTypeLine(atom)
fun line(recursive: TypeRecursive): TypeLine = RecursiveTypeLine(recursive)
fun line(recurse: TypeRecurse): TypeLine = RecurseTypeLine(recurse)

fun recursive(line: TypeLine) = TypeRecursive(line)

infix fun String.lineTo(type: Type): TypeLine = line(atom(this fieldTo type))

val textTypeLine: TypeLine get() = line(atom(literal(typeText)))
val numberTypeLine: TypeLine get() = line(atom(literal(typeNumber)))
