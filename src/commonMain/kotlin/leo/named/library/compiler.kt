package leo.named.library

import leo.Type
import leo.lineTo
import leo.named.compiler.Compiler
import leo.named.compiler.compiler
import leo.named.compiler.definition
import leo.named.compiler.functionBinding
import leo.named.compiler.plusPrivate
import leo.named.expression.Doing
import leo.named.expression.do_
import leo.named.expression.let
import leo.named.expression.line
import leo.named.expression.plus
import leo.named.expression.rhs
import leo.named.typed.typed
import leo.natives.minusName
import leo.natives.timesName
import leo.numberTypeLine
import leo.plusName
import leo.textName
import leo.textTypeLine
import leo.type

fun Compiler.plus(lhsType: Type, rhsType: Type, doing: Doing): Compiler =
	Compiler(
		module.plusPrivate(definition(lhsType, functionBinding(rhsType))),
		typed(
			typedExpression.expression.plus(line(let(lhsType, rhs(do_(doing))))),
			typedExpression.type))

val preludeCompiler: Compiler get() =
	compiler()
		.plus(
			type(textName lineTo type(numberTypeLine)),
			type(textTypeLine),
			numberTextBody)
		.plus(
			type(numberTypeLine, plusName lineTo type(numberTypeLine)),
			type(numberTypeLine),
			numberPlusNumberBody)
		.plus(
			type(numberTypeLine, minusName lineTo type(numberTypeLine)),
			type(numberTypeLine),
			numberMinusNumberBody)
		.plus(
			type(numberTypeLine, timesName lineTo type(numberTypeLine)),
			type(numberTypeLine),
			numberTimesNumberBody)
