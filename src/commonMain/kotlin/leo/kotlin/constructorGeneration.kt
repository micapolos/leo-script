package leo.kotlin

import leo.ChoiceType
import leo.StructureType
import leo.TypeField
import leo.TypeStructure
import leo.bind
import leo.get
import leo.map

val TypeField.constructorString: String get() =
	constructorGeneration.get(types())

val TypeField.constructorGeneration: Generation<String> get() =
	when (type) {
		is ChoiceType -> TODO()
		is StructureType -> nameGeneration.bind { typeName ->
			type.structure.constructorGeneration(name, typeName)
		}
	}

fun TypeStructure.constructorGeneration(methodName: String, typeName: Name): Generation<String> =
	valsGeneration.bind { vals ->
		paramsGeneration.map { params ->
			"fun $methodName($vals) = ${typeName.kotlinClassName}($params)"
		}
	}

fun TypeField.postfixConstructorGeneration(methodName: String, typeName: Name): Generation<String> =
	nameGeneration.bind { name ->
		"val ${name.kotlinClassName}.$methodName get() = $methodName(this)".generation
	}
