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

data class RecursiveTypeLine(val recursive: TypeRecursive): TypeLine() {
	override fun toString() = super.toString()
}

data class RecursibleTypeLine(val recursible: TypeRecursible): TypeLine()  {
	override fun toString() = super.toString()
}

sealed class TypeRecursible {
	override fun toString() = scriptLine.toString()
}

data class AtomTypeRecursible(val atom: TypeAtom): TypeRecursible() {
	override fun toString() = super.toString()
}

data class RecurseTypeRecursible(val recurse: TypeRecurse): TypeRecursible() {
	override fun toString() = super.toString()
}

sealed class TypeAtom {
	override fun toString() = scriptLine.toString()
}

data class PrimitiveTypeAtom(val primitive: TypePrimitive): TypeAtom() {
	override fun toString() = super.toString()
}

data class DoingTypeAtom(val doing: TypeDoing): TypeAtom() {
	override fun toString() = super.toString()
}

data class TypeField(val name: String, val rhsType: Type) {
	override fun toString() = scriptLine.toString()
}

sealed class TypePrimitive {
	override fun toString() = scriptLine.toString()
}

data class LiteralTypePrimitive(val literal: TypeLiteral): TypePrimitive() {
	override fun toString() = super.toString()
}

data class FieldTypePrimitive(val field: TypeField): TypePrimitive() {
	override fun toString() = super.toString()
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

data class TypeDoing(val lhsType: Type, val rhsType: Type) {
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

fun typeStructure(name: String): TypeStructure = typeStructure(name lineTo type())
fun typeStructure(vararg lines: TypeLine): TypeStructure = stack(*lines).structure
fun structure(line: TypeLine, vararg lines: TypeLine): TypeStructure = stack(line, *lines).structure
fun choice(vararg lines: TypeLine): TypeChoice = stack(*lines).choice
fun type(vararg lines: TypeLine): Type = type(typeStructure(*lines))
fun type(name: String): Type = type(name lineTo type())

val TypeLiteral.primitive: TypePrimitive get() = LiteralTypePrimitive(this)
val TypeField.primitive: TypePrimitive get() = FieldTypePrimitive(this)

val TypePrimitive.literalOrNull: TypeLiteral? get() = (this as? LiteralTypePrimitive)?.literal
val TypePrimitive.fieldOrNull: TypeField? get() = (this as? FieldTypePrimitive)?.field

val TypeStructure.type: Type get() = type(this)
val TypeChoice.type: Type get() = type(this)

infix fun String.fieldTo(type: Type) = TypeField(this, type)

val typeText get() = TypeText
val typeNumber get() = TypeNumber
val typeRecurse get() = TypeRecurse
val recurseTypeLine get() = line(recursible(typeRecurse))
fun recursiveLine(line: TypeLine) = line(recursive(line))

fun literal(text: TypeText): TypeLiteral = TextTypeLiteral(text)
fun literal(number: TypeNumber): TypeLiteral = NumberTypeLiteral(number)

infix fun Type.doing(type: Type) = TypeDoing(this, type)
infix fun Type.doingLineTo(type: Type) = line(atom(this doing type))

fun atom(doing: TypeDoing): TypeAtom = DoingTypeAtom(doing)
fun atom(primitive: TypePrimitive): TypeAtom = PrimitiveTypeAtom(primitive)
fun atom(field: TypeField): TypeAtom = field.primitive.atom
fun atom(literal: TypeLiteral): TypeAtom = literal.primitive.atom

val TypeField.atom: TypeAtom get() = atom(this)
val TypePrimitive.atom: TypeAtom get() = atom(this)
val TypeDoing.atom: TypeAtom get() = atom(this)

fun line(recursible: TypeRecursible): TypeLine = RecursibleTypeLine(recursible)
fun line(recursive: TypeRecursive): TypeLine = RecursiveTypeLine(recursive)
fun line(atom: TypeAtom): TypeLine = atom.line

val TypeAtom.line get() = recursible.line

fun recursible(atom: TypeAtom) = atom.recursible
fun recursible(recurse: TypeRecurse) = recurse.recursible

val TypeAtom.recursible: TypeRecursible get() = AtomTypeRecursible(this)
val TypeRecurse.recursible: TypeRecursible get() = RecurseTypeRecursible(this)
val TypeLine.recursive: TypeRecursive get() = recursive(this)

fun recursive(line: TypeLine) = TypeRecursive(line)

val TypeRecursive.toLine: TypeLine get() = RecursiveTypeLine(this)
val TypeRecursible.line: TypeLine get() = RecursibleTypeLine(this)

infix fun String.lineTo(type: Type): TypeLine = line(atom(this fieldTo type).recursible)

val textTypeLine: TypeLine get() = line(atom(literal(typeText)))
val numberTypeLine: TypeLine get() = line(atom(literal(typeNumber)))

fun Type.plusOrNull(line: TypeLine): Type? = structureOrNull?.plus(line)?.type
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
val Type.choiceOrNull: TypeChoice? get() = (this as? ChoiceType)?.choice
val Type.doingOrNull: TypeDoing? get() = structureOrNull?.onlyLineOrNull?.atom?.doingOrNull
val Type.onlyLineOrNull: TypeLine? get() = structureOrNull?.onlyLineOrNull
val TypeLine.recursibleOrNull: TypeRecursible? get() = (this as? RecursibleTypeLine)?.recursible
val TypeLine.recursiveOrNull: TypeRecursive? get() = (this as? RecursiveTypeLine)?.recursive
val TypeLine.atomOrNull: TypeAtom? get() = recursibleOrNull?.atomOrNull
val TypeAtom.primitiveOrNull: TypePrimitive? get() = (this as? PrimitiveTypeAtom)?.primitive
val TypeAtom.doingOrNull: TypeDoing? get() = (this as? DoingTypeAtom)?.doing
val TypeAtom.fieldOrNull: TypeField? get() = primitiveOrNull?.fieldOrNull
val TypeLine.structureOrNull: TypeStructure? get() = atomOrNull?.fieldOrNull?.rhsType?.structureOrNull
val TypeLine.choiceOrNull: TypeChoice? get() = atomOrNull?.fieldOrNull?.rhsType?.choiceOrNull
fun TypeStructure.lineOrNull(name: String): TypeLine? = lineStack.first { it.name == name }
val TypeRecursible.atomOrNull: TypeAtom? get() = (this as? AtomTypeRecursible)?.atom

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

fun Type.getOrNull(name: String): Type? =
	structureOrNull?.getOrNull(name)?.type

fun TypeStructure.getOrNull(name: String): TypeStructure? =
	getLineOrNull(name)?.structure

fun TypeStructure.getLineOrNull(name: String): TypeLine? =
	onlyLineOrNull?.structureOrNull?.lineOrNull(name)

val TypeStructure.isEmpty: Boolean get() = lineStack.isEmpty
val Type.isEmpty: Boolean get() = structureOrNull?.isEmpty ?: false

val TypeStructure.nameOrNull: String? get() =
	onlyLineOrNull?.atomOrNull?.fieldOrNull?.orNullIf { !rhsType.isEmpty }?.name

val Type.nameOrNull: String? get() =
	structureOrNull?.nameOrNull
