package leo.kotlin

import leo.AtomTypeLine
import leo.ChoiceType
import leo.DoingTypeAtom
import leo.FieldTypeAtom
import leo.ListTypeAtom
import leo.LiteralTypeAtom
import leo.NumberTypeLiteral
import leo.RecurseTypeLine
import leo.RecursiveTypeLine
import leo.Stateful
import leo.StructureType
import leo.TextTypeLiteral
import leo.Type
import leo.TypeAtom
import leo.TypeChoice
import leo.TypeDoing
import leo.TypeField
import leo.TypeLine
import leo.TypeList
import leo.TypeLiteral
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

val TypeLine.typeNameGeneration: Generation<String> get() =
	when (this) {
		is AtomTypeLine -> atom.typeNameGeneration
		is RecurseTypeLine -> TODO()
		is RecursiveTypeLine -> TODO()
	}

val TypeAtom.typeNameGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> doing.typeNameGeneration
		is FieldTypeAtom -> field.typeNameGeneration
		is ListTypeAtom -> list.typeNameGeneration
		is LiteralTypeAtom -> literal.typeNameGeneration
	}

val TypeDoing.typeNameGeneration: Generation<String> get() =
	lhsTypeStructure.lineStack
		.map { typeNameGeneration }
		.flat
		.bind { lhsTypeNameStack ->
			rhsTypeLine.typeNameGeneration.bind { rhsTypeName ->
				"(${lhsTypeNameStack.array.joinToString(", ")}) -> $rhsTypeName".generation
			}
		}

val TypeList.typeNameGeneration: Generation<String> get() =
	itemLine.typeNameGeneration.bind { itemTypeName ->
		"Stack<$itemTypeName>".generation
	}

val TypeLiteral.typeNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> "Double".generation
		is TextTypeLiteral -> "String".generation
	}

val TypeField.typeNameGeneration: Generation<String> get() =
	nameGeneration.map { it.kotlinClassName }

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
	sealedDeclarationGeneration(name).bind { sealedDeclaration ->
		casesDeclarationGeneration(name).bind { casesDeclaration ->
			lines(sealedDeclaration, casesDeclaration).generation
		}
	}

fun TypeChoice.sealedDeclarationGeneration(name: Name): Generation<String> =
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

val TypeLine.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is AtomTypeLine -> atom.fieldNameGeneration
		is RecurseTypeLine -> TODO()
		is RecursiveTypeLine -> TODO()
	}

val TypeAtom.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is DoingTypeAtom -> "doing".generation
		is FieldTypeAtom -> field.name.generation
		is ListTypeAtom -> "list".generation
		is LiteralTypeAtom -> literal.fieldNameGeneration
	}

val TypeLiteral.fieldNameGeneration: Generation<String> get() =
	when (this) {
		is NumberTypeLiteral -> "number".generation
		is TextTypeLiteral -> "text".generation
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