package leo.kotlin

import leo.TypeLine
import leo.bind

val TypeLine.valGeneration: Generation<String> get() =
	fieldNameGeneration.bind { fieldName ->
		typeNameGeneration.bind { typeName ->
			"val $fieldName: $typeName".generation
		}
	}

