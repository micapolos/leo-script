package leo.named.compiler

import leo.Type
import leo.named.expression.be
import leo.named.expression.body
import leo.named.expression.do_
import leo.named.expression.expression
import leo.named.expression.let
import leo.named.expression.line
import leo.named.expression.plus
import leo.named.expression.private
import leo.named.expression.rhs
import leo.named.typed.TypedExpression
import leo.named.typed.TypedLine
import leo.named.typed.give
import leo.named.typed.of
import leo.named.typed.plus
import leo.named.typed.take
import leo.named.typed.typed
import leo.named.typed.typedExpression
import leo.named.typed.with
import leo.type

data class Compiler(
	val module: Module,
	val typedExpression: TypedExpression
)

fun compiler() = Compiler(module(), typed(expression(), type()))

val Module.compiler: Compiler get() =
	Compiler(this, typedExpression())

fun Compiler.set(module: Module): Compiler =
	copy(module = module)

fun Compiler.set(typedExpression: TypedExpression): Compiler =
	copy(typedExpression = typedExpression)

fun Compiler.plus(typedLine: TypedLine): Compiler =
	set(typedExpression.plus(typedLine))

fun Compiler.bind(typedExpression: TypedExpression): Compiler =
	this
		.set(module.bind(typedExpression.type))
		.set(this.typedExpression.bind(typedExpression))

fun Compiler.give(typedExpression: TypedExpression): Compiler =
	set(this.typedExpression.give(typedExpression))

fun Compiler.take(typedExpression: TypedExpression): Compiler =
	set(this.typedExpression.take(typedExpression))

fun Compiler.with(typedExpression: TypedExpression): Compiler =
	set(this.typedExpression.with(typedExpression))

fun Compiler.be(typedExpression: TypedExpression): Compiler =
	set(this.typedExpression.be(typedExpression))

fun Compiler.letBe(type: Type, typedExpression: TypedExpression): Compiler =
	Compiler(
		module.plus(definition(type, constantBinding(typedExpression.type))),
		typed(
			this.typedExpression.expression
				.plus(line(let(type, rhs(be(typedExpression.expression))))),
			this.typedExpression.type))

fun Compiler.letDo(type: Type, typedExpression: TypedExpression): Compiler =
	Compiler(
		module.plus(definition(type, constantBinding(typedExpression.type))),
		typed(
			this.typedExpression.expression
				.plus(line(let(type, rhs(do_(body(typedExpression.expression)))))),
			this.typedExpression.type))

fun Compiler.plusPrivate(compiler: Compiler): Compiler =
	this
		.set(module.plusPrivate(compiler.module.publicDictionary))
		.set(
			typedExpression.expression
			.plus(line(private(compiler.typedExpression.expression)))
			.of(typedExpression.type))

fun Compiler.of(type: Type): Compiler =
	set(typedExpression.of(type))

