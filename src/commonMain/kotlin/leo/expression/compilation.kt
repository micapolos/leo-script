package leo.expression

import leo.Literal
import leo.Stateful
import leo.TypeField
import leo.TypeLine
import leo.array
import leo.base.effect
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
	when (op) {
		is GetOp -> op.get.kotlinCompilation
		is LiteralOp -> op.literal.kotlinCompilation
		is MakeOp -> op.make.kotlinCompilation(typeLine)
	}

val Literal.kotlinCompilation: Compilation<Kotlin> get() =
	toString().kotlin.compilation

val Get.kotlinCompilation: Compilation<Kotlin> get() =
	lhsExpression.kotlinCompilation.map { lhsKotlin ->
		lhsKotlin + ".".kotlin + name.kotlin
	}

fun Make.kotlinCompilation(typeLine: TypeLine): Compilation<Kotlin> =
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

val TypeField.constructorNameCompilation: Compilation<String> get() =
	Compilation { compiler ->
		constructorNameGeneration.run(compiler.types).let { effect ->
			compiler.copy(types = effect.state) effect effect.value
		}
	}
