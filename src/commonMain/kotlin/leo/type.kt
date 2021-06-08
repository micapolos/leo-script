package leo

import leo.base.notNullOrError
import leo.base.orNullIf

sealed class Type {
	override fun toString() = script.toString()
}

data class StructureType(val structure: TypeStructure): Type() {
	override fun toString() = super.toString()
}

data class ChoiceType(val choice: TypeChoice): Type() {
	override fun toString() = super.toString()
}

data class TypeChoice(val lineStack: Stack<TypeLine>) {
	override fun toString() = script.toString()
}

data class TypeStructure(val lineStack: Stack<TypeLine>) {
	override fun toString() = script.toString()
}

sealed class TypeLine {
	override fun toString() = scriptLine.toString()
}

data class AtomTypeLine(val atom: TypeAtom): TypeLine()  {
	override fun toString() = super.toString()
}

data class RecursiveTypeLine(val recursive: TypeRecursive): TypeLine() {
	override fun toString() = super.toString()
}

data class RecurseTypeLine(val recurse: TypeRecurse): TypeLine() {
	override fun toString() = super.toString()
}

sealed class TypeRecursible
data class AtomTypeRecursible(val atom: TypeAtom): TypeRecursible()
data class RecurseTypeRecursible(val recurse: TypeRecurse): TypeRecursible()

sealed class TypeAtom {
	override fun toString() = scriptLine.toString()
}

data class FieldTypeAtom(val field: TypeField): TypeAtom() {
	override fun toString() = super.toString()
}

data class LiteralTypeAtom(val literal: TypeLiteral): TypeAtom() {
	override fun toString() = super.toString()
}

data class DoingTypeAtom(val doing: TypeDoing): TypeAtom() {
	override fun toString() = super.toString()
}

data class TypeField(val name: String, val rhsType: Type) {
	override fun toString() = scriptLine.toString()
}

sealed class TypeLiteral {
	override fun toString() = scriptLine.toString()
}

data class TextTypeLiteral(val text: TypeText): TypeLiteral() {
	override fun toString() = super.toString()
}

data class NumberTypeLiteral(val number: TypeNumber): TypeLiteral() {
	override fun toString() = super.toString()
}

data class TypeDoing(val lhsTypeStructure: TypeStructure, val rhsTypeLine: TypeLine) {
	override fun toString() = scriptLine.toString()
}

data class TypeRecursive(val line: TypeLine) {
	override fun toString() = scriptLine.toString()
}

object TypeText {
	override fun toString() = textName
}

object TypeNumber {
	override fun toString() = numberName
}

object TypeRecurse {
	override fun toString() = recurseName
}

val Stack<TypeLine>.structure get() = TypeStructure(this)
val Stack<TypeLine>.choice get() = TypeChoice(this)

fun type(structure: TypeStructure): Type = StructureType(structure)
fun type(choice: TypeChoice): Type = ChoiceType(choice)

fun typeStructure(vararg lines: TypeLine): TypeStructure = stack(*lines).structure
fun structure(line: TypeLine, vararg lines: TypeLine): TypeStructure = stack(line, *lines).structure
fun choice(vararg lines: TypeLine): TypeChoice = stack(*lines).choice
fun type(vararg lines: TypeLine): Type = type(typeStructure(*lines))
fun type(name: String): Type = type(name lineTo type())

val TypeStructure.type: Type get() = type(this)
val TypeChoice.type: Type get() = type(this)

infix fun String.fieldTo(type: Type) = TypeField(this, type)

val typeText get() = TypeText
val typeNumber get() = TypeNumber
val typeRecurse get() = TypeRecurse

fun literal(text: TypeText): TypeLiteral = TextTypeLiteral(text)
fun literal(number: TypeNumber): TypeLiteral = NumberTypeLiteral(number)

infix fun TypeStructure.doing(line: TypeLine) = TypeDoing(this, line)
infix fun TypeStructure.doingLineTo(line: TypeLine) = line(atom(this doing line))

fun atom(field: TypeField): TypeAtom = FieldTypeAtom(field)
fun atom(literal: TypeLiteral): TypeAtom = LiteralTypeAtom(literal)
fun atom(doing: TypeDoing): TypeAtom = DoingTypeAtom(doing)

val TypeField.atom: TypeAtom get() = atom(this)

fun line(atom: TypeAtom): TypeLine = AtomTypeLine(atom)
fun line(recursive: TypeRecursive): TypeLine = RecursiveTypeLine(recursive)
fun line(recurse: TypeRecurse): TypeLine = RecurseTypeLine(recurse)

val TypeAtom.line get() = line(this)

val TypeAtom.recursible: TypeRecursible get() = AtomTypeRecursible(this)
val TypeRecurse.recursible: TypeRecursible get() = RecurseTypeRecursible(this)

fun recursive(line: TypeLine) = TypeRecursive(line)

infix fun String.lineTo(type: Type): TypeLine = line(atom(this fieldTo type))

val textTypeLine: TypeLine get() = line(atom(literal(typeText)))
val numberTypeLine: TypeLine get() = line(atom(literal(typeNumber)))

fun TypeStructure.plus(line: TypeLine): TypeStructure = TypeStructure(lineStack.push(line))
fun TypeChoice.plus(line: TypeLine): TypeChoice = TypeChoice(lineStack.push(line))

val Literal.typeLine: TypeLine get() = line(typeAtom)

val Literal.typeAtom: TypeAtom get() =
	when (this) {
		is NumberLiteral -> atom(literal(typeNumber))
		is StringLiteral -> atom(literal(typeText))
	}
val TypeStructure.onlyLineOrNull: TypeLine? get() = lineStack.onlyOrNull
val Type.structureOrNull: TypeStructure? get() = (this as? StructureType)?.structure
val Type.onlyLineOrNull: TypeLine? get() = structureOrNull?.onlyLineOrNull
val TypeLine.atomOrNull: TypeAtom? get() = (this as? AtomTypeLine)?.atom
val TypeAtom.fieldOrNull: TypeField? get() = (this as? FieldTypeAtom)?.field
val TypeLine.structureOrNull: TypeStructure? get() = atomOrNull?.fieldOrNull?.rhsType?.structureOrNull
fun TypeStructure.lineOrNull(name: String): TypeLine? = lineStack.first { it.name == name }

fun TypeLine.get(name: String): TypeLine =
	structureOrNull
		.notNullOrError("$this is not structure")
		.lineOrNull(name)
		.notNullOrError("$this does not have field: $name")

val String.typeStructure: TypeStructure get() =
	typeStructure(this lineTo type())

val String.type: Type get() =
	typeStructure.type

val isTypeLine: TypeLine get() =
	line(atom(isTypeField))

val isTypeField: TypeField get() =
	isName fieldTo type(
		choice(
			noName lineTo type(),
			yesName lineTo type()))

val negateIsTypeLine: TypeLine get() =
	line(atom(negateIsTypeField))

val negateIsTypeField: TypeField get() =
	negateName fieldTo type(isTypeLine)

val TypeLine.structure: TypeStructure get() = typeStructure(this)

fun TypeStructure.getOrNull(name: String): TypeStructure? =
	onlyLineOrNull?.structureOrNull?.lineOrNull(name)?.structure

val TypeStructure.isEmpty: Boolean get() = lineStack.isEmpty
val Type.isEmpty: Boolean get() = structureOrNull?.isEmpty ?: false

val TypeStructure.nameOrNull: String? get() =
	onlyLineOrNull?.atomOrNull?.fieldOrNull?.orNullIf { !rhsType.isEmpty }?.name

val Type.nameOrNull: String? get() =
	structureOrNull?.nameOrNull