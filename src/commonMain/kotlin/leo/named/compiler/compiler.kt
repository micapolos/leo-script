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
	val bodyTypedExpression: TypedExpression
)

fun compiler() = Compiler(module(), typed(expression(), type()))

val Module.compiler: Compiler get() =
	Compiler(this, typedExpression())

fun Compiler.set(module: Module): Compiler =
	copy(module = module)

fun Compiler.set(typedExpression: TypedExpression): Compiler =
	copy(bodyTypedExpression = typedExpression)

val Compiler.typedExpression: TypedExpression
	get() =
		module.privateContext.scope.in_(bodyTypedExpression)

fun Compiler.plus(typedLine: TypedLine): Compiler =
	set(bodyTypedExpression.plus(typedLine))

fun Compiler.bind(typedExpression: TypedExpression): Compiler =
	this
		.set(module.bind(typedExpression.type))
		.set(bodyTypedExpression.bind(typedExpression))

fun Compiler.give(typedExpression: TypedExpression): Compiler =
	set(bodyTypedExpression.give(typedExpression))

fun Compiler.take(typedExpression: TypedExpression): Compiler =
	set(bodyTypedExpression.take(typedExpression))

fun Compiler.with(typedExpression: TypedExpression): Compiler =
	set(bodyTypedExpression.with(typedExpression))

fun Compiler.be(typedExpression: TypedExpression): Compiler =
	set(bodyTypedExpression.be(typedExpression))

fun Compiler.letBe(type: Type, typedExpression: TypedExpression): Compiler =
	Compiler(
		module.plus(definition(type, constantBinding(typedExpression.type))),
		typed(
			bodyTypedExpression.expression
				.plus(line(let(type, rhs(be(typedExpression.expression))))),
			bodyTypedExpression.type))

fun Compiler.letDo(type: Type, typedExpression: TypedExpression): Compiler =
	Compiler(
		module.plus(definition(type, constantBinding(typedExpression.type))),
		typed(
			bodyTypedExpression.expression
				.plus(line(let(type, rhs(do_(body(typedExpression.expression)))))),
			bodyTypedExpression.type))

fun Compiler.plusPrivate(compiler: Compiler): Compiler =
	this
		.set(module.plusPrivate(compiler.module.publicContext.dictionary))
		.set(bodyTypedExpression.expression
			.plus(line(private(compiler.typedExpression.expression)))
			.of(bodyTypedExpression.type))

fun Compiler.of(type: Type): Compiler =
	set(bodyTypedExpression.of(type))

