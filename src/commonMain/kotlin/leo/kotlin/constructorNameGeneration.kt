package leo.kotlin

import leo.TypeField
import leo.bind
import leo.text

val TypeField.constructorNameGeneration: Generation<String> get() =
	constructorGeneration.bind { constructor ->
		typesGeneration.bind { types ->
			types.plusMethods(constructor.text).setGeneration.bind {
				name.generation
			}
		}
	}