package leo.kotlin

import leo.ChoiceType
import leo.Stateful
import leo.StructureType
import leo.Type
import leo.TypeChoice
import leo.TypeField
import leo.TypeLine
import leo.TypeStructure
import leo.array
import leo.base.effect
import leo.base.lines
import leo.base.orIfNull
import leo.bind
import leo.flat
import leo.getStateful
import leo.isEmpty
import leo.map
import leo.ret
import leo.setStateful
import leo.updateAndGetEffect

typealias Generation<T> = Stateful<Types, T>

val <T> T.generation: Generation<T> get() = ret()
val typesGeneration: Generation<Types> get() = getStateful()
val Types.setGeneration: Generation<Unit> get() = setStateful(this)

val String.nameGeneration: Generation<Name> get() =
	Generation { types ->
		types
			.nameCounts
			.updateAndGetEffect(this) { countOrNull -> countOrNull.orIfNull { 0 }.inc() }
			.let { types.copy(nameCounts = it.state) effect Name(this, it.value) }
	}

val TypeLine.valGeneration: Generation<String> get() =
	fieldNameGeneration.bind { fieldName ->
		typeNameGeneration.bind { typeName ->
			"val $fieldName: $typeName".generation
		}
	}

val TypeField.nameGeneration: Generation<Name> get() =
	nameOrNullGeneration.bind { nameOrNull ->
		if (nameOrNull != null) nameOrNull.generation
		else name.nameGeneration.bind { name ->
			type.declarationGeneration(name).bind { declaration ->
				typesGeneration.bind { types ->
					types
						.plus(this, GeneratedType(name, kotlin(declaration)))
						.setGeneration
						.map { name }
				}
			}
		}
	}

val TypeField.nameOrNullGeneration: Generation<Name?> get() =
	Generation { it effect it.generatedTypes.get(this)?.name }

fun Type.declarationGeneration(name: Name): Generation<String> =
	when (this) {
		is ChoiceType -> choice.declarationGeneration(name)
		is StructureType -> structure.declarationGeneration(name)
	}

fun TypeStructure.declarationGeneration(name: Name): Generation<String> =
	lineStack
		.map { valGeneration }
		.flat
		.map { valStack ->
			if (valStack.isEmpty) "object ${name.kotlinClassName}"
			else "data class ${name.kotlinClassName}(${valStack.array.joinToString(", ")})"
		}

fun TypeChoice.declarationGeneration(name: Name): Generation<String> =
	sealedClassDeclarationGeneration(name).bind { sealedDeclaration ->
		casesDeclarationGeneration(name).bind { casesDeclaration ->
			lines(sealedDeclaration, casesDeclaration).generation
		}
	}

fun sealedClassDeclarationGeneration(name: Name): Generation<String> =
	"sealed class ${name.kotlinClassName}".generation

fun TypeChoice.casesDeclarationGeneration(name: Name): Generation<String> =
	lineStack
		.map { caseDeclarationGeneration(name) }
		.flat
		.map { it.array.joinToString("\n") }

fun TypeLine.caseDeclarationGeneration(sealedClassName: Name): Generation<String> =
	typeNameGeneration.bind { caseClassName ->
		fieldNameGeneration.bind { fieldNameGeneration ->
			"data class $caseClassName${sealedClassName.kotlinClassName}(val $fieldNameGeneration: $caseClassName): ${sealedClassName.kotlinClassName}()".generation
		}
	}

val TypeLine.kotlinGeneration: Generation<Kotlin> get() =
	typeNameGeneration.bind { unusedName ->
		typesGeneration.map { it.kotlin }
	}

val Type.kotlinGeneration: Generation<Kotlin> get() =
	when (this) {
		is ChoiceType -> TODO()
		is StructureType -> structure.kotlinGeneration
	}

val TypeStructure.kotlinGeneration: Generation<Kotlin> get() =
	lineStack.map { kotlinGeneration }.flat.bind {
		typesGeneration.bind { it.kotlin.generation }
	}