package leo.typed.evaluator

import leo.Stateful
import leo.foldStateful
import leo.get
import leo.getName
import leo.map
import leo.onlyOrNull
import leo.reverse
import leo.seq
import leo.stateful
import leo.typed.Field
import leo.typed.NativeValue
import leo.typed.Structure
import leo.typed.StructureValue
import leo.typed.Value
import leo.typed.dsl.X
import leo.typed.dsl.context
import leo.typed.dsl.get
import leo.typed.dsl.name
import leo.typed.fieldTo
import leo.typed.plus
import leo.typed.structure
import leo.typed.structureOrNull
import leo.typed.value

typealias Evaluation<T> = Stateful<X, T>
val <T> T.evaluation: Evaluation<T> get() = stateful()

val Structure.evaluate: Structure get() =
	structureEvaluation.get(context())

val Value.valueEvaluation: Evaluation<Value> get() =
	structureOrNull!!.structureEvaluation.map { it.value }

val Field.fieldEvaluation: Evaluation<Field> get() =
	when (value) {
		is NativeValue -> evaluation
		is StructureValue -> value.structure.structureEvaluation.map { name fieldTo it  }
	}

val Structure.structureEvaluation: Evaluation<Structure> get() =
	structure().evaluation.foldStateful(fieldStack.reverse.seq) { plusEvaluation(it) }

val Structure.fieldEvaluation: Evaluation<Field> get() =
	structureEvaluation.map { it.fieldStack.onlyOrNull!! }

fun Structure.plusEvaluation(field: Field): Evaluation<Structure> =
	when (field.name) {
		getName -> plusGetEvaluation(field.value)
		else -> plusFieldEvaluation(field)
	}

fun Structure.plusGetEvaluation(value: Value): Evaluation<Structure> =
	structure(get(value.name)).evaluation

fun Structure.plusFieldEvaluation(field: Field): Evaluation<Structure> =
	field.fieldEvaluation.map { plus(it) }
