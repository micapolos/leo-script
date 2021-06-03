package leo

import leo.base.Seq
import leo.base.SeqNode
import leo.base.fold
import leo.base.ifOrNull
import leo.base.mapFirstOrNull
import leo.base.negate
import leo.base.notNullIf
import leo.base.orNullIf
import leo.base.reverse
import leo.base.runIf
import leo.base.runIfNotNull
import leo.base.seq
import leo.base.seqNode

sealed class Value {
	override fun toString() = string
}

object EmptyValue : Value()

object AnyValue: Value()

data class LinkValue(val link: Link) : Value() {
	override fun toString() = super.toString()
}

data class Link(val value: Value, val field: Field)

data class Native(val any: Any?)

sealed class Rhs
data class ValueRhs(val value: Value) : Rhs()
data class FunctionRhs(val function: Function) : Rhs()
data class NativeRhs(val native: Native) : Rhs()
data class PatternRhs(val pattern: Type) : Rhs()

fun rhs(value: Value): Rhs = ValueRhs(value)
fun rhs(function: Function): Rhs = FunctionRhs(function)
fun rhs(native: Native): Rhs = NativeRhs(native)
fun valueRhs(pattern: Type): Rhs = PatternRhs(pattern)

val Rhs.valueOrNull: Value? get() = (this as? ValueRhs)?.value
val Rhs.valueOrThrow: Value
	get() = valueOrNull.notNullOrThrow {
		value(
			"rhs" fieldTo this,
			isName fieldTo value(notName fieldTo value(valueName))
		)
	}
val Rhs.functionOrNull: Function? get() = (this as? FunctionRhs)?.function
val Rhs.nativeOrNull: Native? get() = (this as? NativeRhs)?.native

data class Field(val name: String, val rhs: Rhs)
data class Structure(val name: String, val value: Value)

val Value.string: String get() = script.string

infix fun String.fieldTo(rhs: Rhs): Field = Field(this, rhs)
infix fun String.fieldTo(value: Value): Field = this fieldTo rhs(value)
fun field(literal: Literal): Field = literal.field
fun field(function: Function): Field = doingName fieldTo rhs(function)
infix fun String.structureTo(value: Value) = Structure(this, value)

infix fun Value.linkTo(field: Field) = Link(this, field)

val Value.linkOrNull: Link? get() = (this as? LinkValue)?.link

fun native(any: Any?) = Native(any)
val Native.stringOrNull: String? get() = any as? String
val Native.numberOrNull: Number? get() = any as? Number

operator fun Value.plus(field: Field): Value = value(this linkTo field)
operator fun Value.plus(value: Value): Value = fold(value.fieldSeq.reverse) { plus(it) }
val emptyValue: Value get() = EmptyValue
val anyValue: Value get() = AnyValue
fun value(vararg fields: Field) = emptyValue.fold(fields) { plus(it) }
fun value(name: String) = value(name fieldTo value())
fun value(link: Link): Value = LinkValue(link)

val Field.functionOrNull: Function? get() = rhs.functionOrNull
val Field.nativeOrNull: Native? get() = rhs.nativeOrNull

val Value.functionOrNull: Function? get() = fieldOrNull?.functionOrNull
val Value.fieldOrNull: Field? get() = linkOrNull?.run { notNullIf(value.isEmpty) { field } }
val Value.structureOrNull: Structure?
	get() = fieldOrNull?.let { field ->
		field.rhs.valueOrNull?.let { value ->
			field.name structureTo value
		}
	}
val Value.structureOrThrow: Structure
	get() =
		structureOrNull.notNullOrThrow { isNotValue("structure") }

val Value.switchFieldOrThrow: Field
	get() =
		switchFieldOrNull.notNullOrThrow { plus(switchName fieldTo value()) }

val Value.resolveEvaluation: Evaluation<Value>
	get() =
		resolveFunctionApplyOrNullEvaluation.or { resolve.evaluation }

val Value.resolve: Value
	get() = resolveNameOrNull ?: this

val Value.resolveFunctionApplyOrNullEvaluation: Evaluation<Value?>
	get() =
		resolveOrNull(doingName, giveName) { rhs ->
			functionOrNull?.let { function ->
				function.applyEvaluation(rhs)
			}
		} ?: evaluation(null)

val Value.fieldSeq: Seq<Field>
	get() =
		seq { linkOrNull?.fieldSeqNode }

val Link.fieldSeqNode: SeqNode<Field>
	get() =
		field.seqNode(value.fieldSeq)

fun Value.selectOrNull(name: String): Value? =
	fieldSeq.mapFirstOrNull { selectOrNull(name) }

fun Value.selectFieldOrNull(name: String): Field? =
	fieldSeq.mapFirstOrNull { orNull(name) }

val Literal.selectName: String
	get() =
		when (this) {
			is NumberLiteral -> numberName
			is StringLiteral -> textName
		}

fun Field.selectOrNull(name: String): Value? =
	notNullIf(this.name == name) { value(this) }

val Value.bodyOrNull: Value?
	get() =
		fieldOrNull?.valueOrNull

fun Value.getOrNull(value: Value): Value? =
	runIfNotNull(value.fieldOrNull) {
		getOrNull(it)
	}

fun Value.getOrNull(field: Field): Value? =
	runIfNotNull(field.rhs.valueOrNull) {
		getOrNull(field.name, it)
	}

fun Value.getOrNull(name: String, value: Value): Value? =
	getOrSetOrNull(name, value) ?: make(name, value)

fun Value.getOrSetOrNull(name: String, value: Value): Value? =
	bodyOrNull?.selectFieldOrNull(name)?.let { field ->
		if (value.isEmpty) value(field)
		else value(name fieldTo value)
	}

fun Value.make(name: String, value: Value): Value =
	value(name fieldTo plus(value))

fun Value.getOrNull(name: String): Value? =
	bodyOrNull?.selectOrNull(name)

fun Value.resolveOrNull(name: String): Value? =
	getOrNull(name) ?: makeOrNull(name)

fun Field.rhsOrNull(name: String): Rhs? =
	notNullIf(this.name == name) { rhs }

fun Field.valueOrNull(name: String): Value? =
	rhsOrNull(name)?.valueOrNull

fun Value.make(name: String): Value =
	value(name fieldTo this)

fun Value.makeOrNull(name: String): Value? =
	notNullIf(!isEmpty) {
		value(name fieldTo this)
	}

fun Value.plus(name: String): Value =
	plus(name fieldTo value())

fun Value.orNull(name: String): Value? =
	fieldOrNull?.orNull(name)?.let { this }

val Field.textOrNull: String?
	get() =
		rhsOrNull(textName)?.nativeOrNull?.any as? String

val Field.numberOrNull: Number?
	get() =
		rhsOrNull(numberName)?.nativeOrNull?.any as? Number

val Value.textOrNull: String?
	get() =
		fieldOrNull?.textOrNull

val Value.textOrThrow: String
	get() =
		textOrNull.notNullOrThrow { isNotValue(textName) }

fun Value.isNotValue(name: String) =
	plus(isName fieldTo value(notName fieldTo value(name)))

val Value.functionOrThrow: Function
	get() =
		functionOrNull.notNullOrThrow { isNotValue("function") }

val Value.numberOrNull: Number?
	get() =
		fieldOrNull?.numberOrNull

val Value.numberOrThrow: Number
	get() =
		numberOrNull.notNullOrThrow { isNotValue(numberName) }

val Value.isEmpty: Boolean
	get() =
		this is EmptyValue

val Rhs.isEmpty: Boolean
	get() =
		valueOrNull?.isEmpty ?: false

val Field.onlyNameOrNull: String?
	get() =
		notNullIf(rhs.isEmpty) { name }

val Literal.native: Native
	get() =
		when (this) {
			is NumberLiteral -> native(number)
			is StringLiteral -> native(string)
		}

val Field.valueOrNull: Value?
	get() =
		rhs.valueOrNull

val Value.nameOrNull: String?
	get() =
		fieldOrNull?.onlyNameOrNull

fun <T: Any> Value.resolveInfixOrNull(name: String, fn: Value.(Value) -> T?): T? =
	linkOrNull?.run {
		value.let { lhs ->
			field.valueOrNull(name)?.let { rhs ->
				lhs.fn(rhs)
			}
		}
	}

fun <T: Any> Value.resolvePrefixOrNull(name: String, fn: (Value) -> T?): T? =
	resolveInfixOrNull(name) { rhs ->
		resolveEmptyOrNull {
			fn(rhs)
		}
	}

fun <T: Any> Value.resolvePostfixOrNull(name: String, fn: Value.() -> T?): T? =
	resolveInfixOrNull(name) { rhs ->
		rhs.resolveEmptyOrNull {
			fn()
		}
	}

fun <T> Value.resolveOrNull(name: String, fn: () -> T?): T? =
	resolveInfixOrNull(name) { rhs ->
		resolveEmptyOrNull {
			rhs.resolveEmptyOrNull {
				fn()
			}
		}
	}

fun <T: Any> Value.resolveEmptyOrNull(fn: () -> T?): T? =
	ifOrNull(isEmpty) {
		fn()
	}

val Boolean.isValue
	get() =
		value(isName fieldTo value(if (this) yesName else noName))

val Value.isBooleanOrNull: Boolean?
	get() =
		resolvePrefixOrNull(isName) { rhs ->
			rhs.nameOrNull?.let { name ->
				when (name) {
					yesName -> true
					noName -> false
					else -> null
				}
			}
		}

val Value.isNegate: Value
	get() =
		isNegateOrNull.notNullOrThrow { value("negate") }

val Value.isNegateOrNull: Value?
	get() =
		null
			?: isYesNoNegateOrNull
			?: isAnyNegateOrNull

val Value.isYesNoNegateOrNull: Value?
	get() =
		isBooleanOrNull?.negate?.isValue

val Value.isAnyNegateOrNull: Value?
	get() =
		resolveInfixOrNull(isName) { rhs ->
			plus(isName fieldTo value(notName fieldTo rhs))
		}

val Value.isBoolean: Boolean
	get() =
		isBooleanOrNull.notNullOrThrow { plus(notName fieldTo value("boolean")) }

fun Boolean.isValue(negated: Boolean) =
	runIf(negated) { negate }.isValue

val Value.hashValue
	get() =
		value(hashName fieldTo value(field(literal(hashCode()))))

fun Value.fieldOrNull(name: String): Field? =
	fieldOrNull?.orNull(name)

fun Field.orNull(name: String): Field? =
	orNullIf { this.name != name }

fun <R> Value.resolveOrNull(lhsName: String, rhsName: String, fn: Value.(Value) -> R?): R? =
	linkOrNull?.run {
		field.valueOrNull(rhsName)?.let { rhs ->
			value.orNull(lhsName)?.let { lhs ->
				lhs.fn(rhs)
			}
		}
	}

fun Value.as_(type: Type): Value =
	dictionary()
		.plus(definition(type, binding(this)))
		.bindingOrNull(this)
		?.let { this }
		.notNullOrThrow { plus(value(asName fieldTo type.script.value)) }

val Value.resolveNameOrNull: Value?
	get() =
		linkOrNull?.run {
			field.onlyNameOrNull?.let { name ->
				value.resolveOrNull(name)
			}
		}

fun Value.setOrThrow(value: Value): Value =
	structureOrThrow.let { structure ->
		structure.value
			.fold(value.fieldSeq.reverse) { replaceOrThrow(it) }
			.let { value(structure.name fieldTo it) }
	}

fun Value.replaceOrThrow(field: Field): Value =
	when (this) {
		EmptyValue -> value("no" fieldTo value("field" fieldTo value(field.name))).throwError()
		AnyValue -> value("no" fieldTo value("field" fieldTo value(field.name))).throwError()
		is LinkValue -> link.replaceOrThrow(field).let { value(it) }
	}

fun Link.replaceOrThrow(field: Field): Link =
	if (this.field.name == field.name) value linkTo field
	else value.replaceOrThrow(field).linkTo(this.field)

fun Value.get(name: String): Value =
	bodyOrNull?.selectOrNull(name).notNullOrThrow { plus(getName fieldTo value(name)) }

fun Value.apply(get: Get): Value =
	let { target ->
		value().fold(get.nameSeq) { name ->
			plus(target.get(name))
		}
	}

val Value.structureValue: Value get() =
	value(
		"structure" fieldTo
			when (this) {
				EmptyValue -> value("empty")
				AnyValue -> value("any")
				is LinkValue -> value("link" fieldTo value(link))
			})

val Value.switchFieldOrNull: Field? get() =
	fieldOrNull?.switchFieldOrNull

val Field.switchFieldOrNull: Field? get() =
	when (name) {
		listName -> valueOrNull?.let { value ->
			when (value) {
				EmptyValue -> emptyName fieldTo value()
				AnyValue -> anyName fieldTo value()
				is LinkValue -> linkName fieldTo value(
					listName fieldTo value.link.value,
					itemName fieldTo value(value.link.field))
			}
		}
		else -> valueOrNull?.fieldOrNull
	}
