package leo.kotlin

import leo.ChoiceType
import leo.StructureType
import leo.TypeChoice
import leo.TypeField
import leo.TypeLine
import leo.TypeStructure
import leo.array
import leo.bind
import leo.flat
import leo.get
import leo.map

val TypeField.constructorString: String get() =
	constructorGeneration.get(types())

val TypeField.constructorGeneration: Generation<String> get() =
	when (rhsType) {
		is ChoiceType -> nameGeneration.bind { typeName ->
			rhsType.choice.prefixConstructorGeneration(name, typeName)
		}
		is StructureType -> nameGeneration.bind { typeName ->
			rhsType.structure.prefixConstructorGeneration(name, typeName)
		}
	}

fun TypeStructure.prefixConstructorGeneration(methodName: String, typeName: Name): Generation<String> =
	paramsDeclarationGeneration.bind { paramsDeclaration ->
		paramsGeneration.map { params ->
			if (paramsDeclaration.isEmpty()) "fun $methodName() = ${typeName.kotlinClassName}"
			else "inline fun $methodName($paramsDeclaration) = ${typeName.kotlinClassName}($params)"
		}
	}

fun TypeChoice.prefixConstructorGeneration(methodName: String, typeName: Name): Generation<String> =
	lineStack
		.map { caseConstructorGeneration(methodName, typeName) }
		.flat
		.map { it.array.joinToString("\n") }

fun TypeLine.caseConstructorGeneration(methodName: String, sealedClassName: Name): Generation<String> =
	classNameGeneration.bind { caseClassName ->
		typeNameGeneration.bind { typeName ->
			fieldNameGeneration.map { fieldName ->
				"inline fun $methodName($fieldName: $typeName): ${sealedClassName.kotlinClassName} = $caseClassName${sealedClassName.kotlinClassName}($fieldName)"
			}
		}
	}
