package leo.natives

import leo.Dictionary
import leo.dictionary
import leo.plus

actual val nativeDictionary: Dictionary
	get() =
		dictionary()
			.plus(nullJavaDefinition)
			.plus(trueJavaDefinition)
			.plus(falseJavaDefinition)

			.plus(javaObjectClassDefinition)
			.plus(arrayJavaDefinition)

			.plus(textJavaDefinition)
			.plus(javaTextDefinition)

			.plus(numberJavaDefinition)
			.plus(javaNumberDefinition)

			.plus(numberIntegerObjectJavaDefinition)
			.plus(javaObjectIntegerNumberDefinition)

			.plus(textClassJavaDefinition)

			.plus(javaClassFieldDefinition)
			.plus(javaFieldGetDefinition)

			.plus(javaClassMethodNameTextDefinition)
			.plus(javaMethodInvokeDefinition)

