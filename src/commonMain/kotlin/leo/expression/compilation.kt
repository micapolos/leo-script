package leo.expression

import leo.Literal
import leo.Stateful
import leo.TypeField
import leo.array
import leo.base.effect
import leo.base.fold
import leo.base.lines
import leo.bind
import leo.fieldTo
import leo.flat
import leo.getStateful
import leo.kotlin.Kotlin
import leo.kotlin.constructorNameGeneration
import leo.kotlin.kotlin
import leo.kotlin.plus
import leo.map
import leo.name
import leo.seq
import leo.stateful
import leo.type

typealias Compilation<T> = Stateful<Compiler, T>
val <T> T.compilation: Compilation<T> get() = stateful()

val compilerCompilation: Compilation<Compiler> get() = getStateful()

val Expression.mainKotlinCompilation: Compilation<Kotlin> get() =
	kotlinCompilation.bind { kotlin ->
		compilerCompilation.map { compiler ->
			compiler.types.kotlin.string.let { typesString ->
				"fun main() = println(${kotlin.string})".let { mainString ->
					if (typesString.isEmpty()) mainString.kotlin
					else lines(typesString, mainString).kotlin
				}
			}
		}
	}

val Expression.kotlinCompilation: Compilation<Kotlin> get() =
	null
		?: staticKotlinCompilationOrNull
		?: dynamicKotlinCompilation

val Expression.staticKotlinCompilationOrNull: Compilation<Kotlin>? get() =
	null
		?: booleanKotlinCompilationOrNull

val Expression.dynamicKotlinCompilation: Compilation<Kotlin> get() =
	null
		?: booleanKotlinCompilationOrNull
		?: when (op) {
			is GetOp -> op.get.kotlinCompilation
			is LiteralOp -> op.literal.kotlinCompilation
			is MakeOp -> op.make.kotlinCompilation
			is BindOp -> op.bind.kotlinCompilation
			is EqualOp -> op.equal.kotlinCompilation
			is VariableOp -> op.variable.kotlinCompilation
			is InvokeOp -> op.invoke.kotlinCompilation
		}

val Expression.booleanKotlinCompilationOrNull: Compilation<Kotlin>? get() =
	booleanOrNull?.kotlin?.compilation

val Literal.kotlinCompilation: Compilation<Kotlin> get() =
	toString().kotlin.compilation

val Get.kotlinCompilation: Compilation<Kotlin> get() =
	lhsExpression.kotlinCompilation.map { lhsKotlin ->
		lhsKotlin + ".".kotlin + name.kotlin
	}

val Make.kotlinCompilation: Compilation<Kotlin> get() =
	lhsStructure
		.expressionStack
		.map { kotlinCompilation }
		.flat
		.bind { kotlinStack ->
			name
				.fieldTo(type(lhsStructure.typeStructure))
				.constructorNameCompilation
				.map { constructorName ->
					constructorName.kotlin
						.plus("(".kotlin)
						.plus(kotlinStack.map { string }.array.joinToString(", ").kotlin)
						.plus(")".kotlin)
				}
		}

val Bind.kotlinCompilation: Compilation<Kotlin> get() =
	rhsExpression.kotlinCompilation.fold(lhsStructure.expressionStack.seq) { field ->
		bind { kotlin ->
			kotlin.letCompilation(field)
		}
	}

val Equal.kotlinCompilation: Compilation<Kotlin> get() =
	lhsExpression.kotlinCompilation.bind { lhsKotlin ->
		rhsExpression.kotlinCompilation.map { rhsKotlin ->
			"${lhsKotlin.string}.equals(${rhsKotlin.string})".kotlin
		}
	}

fun Kotlin.letCompilation(expression: Expression): Compilation<Kotlin> =
	expression.kotlinCompilation.map { rhsKotlin ->
		kotlin(rhsKotlin.string + ".let { " + expression.typeLine.name + " -> " + string + " }")
	}

val Variable.kotlinCompilation: Compilation<Kotlin> get() =
	name.kotlin.compilation

val Invoke.kotlinCompilation: Compilation<Kotlin> get() =
	structure.paramsCompilation.map { params ->
		"_invoke($params)".kotlin
	}

val Structure.paramsCompilation: Compilation<String> get() =
	expressionStack
		.map { kotlinCompilation }
		.flat
		.map { kotlinStack -> kotlinStack.map { string }.array.joinToString(", ") }

val TypeField.constructorNameCompilation: Compilation<String> get() =
	Compilation { compiler ->
		constructorNameGeneration.run(compiler.types).let { effect ->
			compiler.copy(types = effect.state) effect effect.value
		}
	}
