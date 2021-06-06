package leo.kotlin

import leo.ChoiceType
import leo.StructureType
import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.TypeStructure
import leo.array
import leo.base.lines
import leo.bind
import leo.flat
import leo.isEmpty
import leo.isSingleton
import leo.map

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
			else "$classDeclarationPrefixString class ${name.kotlinClassName}(${valStack.array.joinToString(", ")})"
		}

val TypeStructure.classDeclarationPrefixString: String get() =
	if (lineStack.isSingleton) "@JvmInline value"
	else "data"

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

