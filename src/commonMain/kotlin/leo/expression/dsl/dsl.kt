package leo.expression.dsl

import leo.base.notNullOrError
import leo.expression.Bind
import leo.expression.Expression
import leo.expression.Make
import leo.expression.Structure
import leo.expression.expression
import leo.expression.get
import leo.expression.kotlin
import leo.expression.of
import leo.expression.op
import leo.expression.structure
import leo.get
import leo.kotlin.Kotlin
import leo.literal
import leo.map
import leo.numberTypeLine
import leo.onlyOrNull
import leo.stack
import leo.textTypeLine

data class Field(val name: String, val structure: Structure)
infix fun String.fieldTo(structure: Structure) = Field(this, structure)

val String.text: Structure get() = structure(literal.op of textTypeLine)
val Double.number: Structure get() = structure(literal.op of numberTypeLine)
val Int.number: Structure get() = toDouble().number

fun Structure.get(name: String): Structure =
	expression.let { expression ->
		expression.get(name).op.of(expression.typeLine.get(name))
	}.structure

fun Structure.make(name: String): Structure = Make(this, name).expression.structure

fun Structure.bind(structure: Structure): Structure =
	structure.expression.let { expression ->
		Bind(this, expression).op.of(expression.typeLine).structure
	}

val Structure.expression: Expression get() =
	expressionStack.onlyOrNull.notNullOrError("$this not a structure")

val Structure.kotlin: Kotlin get() = expression.kotlin

val Field.expression: Expression get() =
	structure.make(name).expression

val String.variable: Structure get() = TODO()

fun structure(vararg fields: Field): Structure =
	Structure(stack(*fields).map { expression })