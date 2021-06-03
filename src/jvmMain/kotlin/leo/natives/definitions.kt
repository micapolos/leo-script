package leo.natives

import leo.Number
import leo.anyValue
import leo.field
import leo.fieldTo
import leo.literal
import leo.native
import leo.numberAnyField
import leo.numberAnyValue
import leo.numberName
import leo.rhs
import leo.textAnyField
import leo.textAnyValue
import leo.textName
import leo.value
import java.lang.reflect.Field
import java.lang.reflect.Method

object ClassLoaderSource

val classLoader = ClassLoaderSource::class.java.classLoader

val String.loadClass: Class<*>
	get() =
		classLoader.loadClass(this)

val nullJavaDefinition
	get() =
		nativeDefinition(value(javaName fieldTo value(nullName))) {
			null.javaValue
		}

val trueJavaDefinition
	get() =
		nativeDefinition(value(javaName fieldTo value(trueName))) {
			true.javaValue
		}

val falseJavaDefinition
	get() =
		nativeDefinition(value(javaName fieldTo value(falseName))) {
			false.javaValue
		}

val javaObjectClassDefinition
	get() =
		nativeDefinition(
			value(className fieldTo value(objectName fieldTo value(javaName fieldTo anyValue)))) {
			value(
				className fieldTo
					it
						.nativeValue(className)
						.nativeValue(objectName)
						.nativeValue(javaName)
						.javaObject!!
						.javaClass
						.javaValue
			)
		}

val textJavaDefinition
	get() =
		nativeDefinition(
			value(javaName fieldTo value(textAnyField))
		) {
			it
				.nativeValue(javaName)
				.nativeValue(textName)
				.nativeText
				.javaValue
		}

val javaTextDefinition
	get() =
		nativeDefinition(
			value(textName fieldTo value(javaName fieldTo anyValue))
		) {
			value(field(literal(it.nativeValue(textName).nativeValue(javaName).javaObject as String)))
		}

val numberJavaDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo numberAnyValue
			)
		) {
			it.nativeValue(javaName).nativeValue(numberName).nativeNumber.javaValue
		}

val javaNumberDefinition
	get() =
		nativeDefinition(
			value(
				numberName fieldTo value(javaName fieldTo anyValue)
			)
		) {
			value(field(literal(it.nativeValue(numberName).nativeValue(javaName).javaObject as Number)))
		}

val numberIntegerObjectJavaDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo value(integerName fieldTo value(numberAnyField))
			)
		) {
			it
				.nativeValue(javaName)
				.nativeValue(integerName)
				.nativeValue(numberName)
				.nativeNumber
				.double
				.toInt()
				.javaValue
		}

val javaObjectIntegerNumberDefinition
	get() =
		nativeDefinition(
			value(
				numberName fieldTo value(integerName fieldTo value(javaName fieldTo anyValue)),
			)
		) {
			value(field(literal(it.nativeValue(numberName).nativeValue(integerName).nativeValue(javaName).javaObject as Int)))
		}

val arrayJavaDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo value(arrayName fieldTo anyValue),
			)
		) {
			it.nativeValue(javaName).nativeValue(arrayName).nativeArray.javaValue
		}

val textClassJavaDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo value(className fieldTo value(textAnyField))
			)
		) {
			it
				.nativeValue(javaName)
				.nativeValue(className)
				.nativeValue(textName)
				.nativeText
				.loadClass
				.javaValue
		}

val javaClassFieldDefinition
	get() =
		nativeDefinition(
			value(
				className fieldTo value(
					javaName fieldTo anyValue
				),
				fieldName fieldTo textAnyValue
			)
		) {
			value(
				fieldName fieldTo it
					.nativeValue(className)
					.nativeValue(javaName)
					.javaObject
					.run { this as Class<*> }
					.getField(
						it
							.nativeValue(fieldName)
							.nativeValue(textName)
							.nativeText
					)
					.javaValue)
		}

val javaFieldObjectDefinition
	get() =
		nativeDefinition(
			value(
				fieldName fieldTo value(javaName fieldTo anyValue),
				objectName fieldTo value(javaName fieldTo anyValue)
			)
		) {
			it
				.nativeValue(fieldName)
				.nativeValue(javaName)
				.javaObject
				.run { this as Field }
				.get(
					it
						.nativeValue(objectName)
						.nativeValue(javaName)
						.javaObject
				)
				.javaValue
		}

@Suppress("UNCHECKED_CAST")
val javaClassMethodNameTextDefinition
	get() =
		nativeDefinition(
			value(
				className fieldTo value(
					javaName fieldTo anyValue
				),
				methodName fieldTo value(
					textAnyField,
					argsName fieldTo value(
						javaName fieldTo anyValue
					)
				)
			)
		) {
			value(
				methodName fieldTo it
					.nativeValue(className)
					.nativeValue(javaName)
					.javaObject
					.run { this as Class<*> }
					.getMethod(
						it
							.nativeValue(methodName)
							.nativeValue(textName)
							.nativeText,
						*it
							.nativeValue(methodName)
							.nativeValue(argsName)
							.nativeValue(javaName)
							.javaObject
							.run { this as Array<*> }
							.toList()
							.run { this as List<Class<*>> }
							.toTypedArray())
					.javaValue)
		}

val javaMethodInvokeDefinition
	get() =
		nativeDefinition(
			value(
				methodName fieldTo value(javaName fieldTo anyValue),
				invokeName fieldTo value(
					javaName fieldTo anyValue,
					argsName fieldTo value(javaName fieldTo anyValue)
				)
			)
		) {
			value(
				javaName fieldTo rhs(
					native(
						it
							.nativeValue(methodName)
							.nativeValue(javaName)
							.javaObject
							.run { this as Method }
							.invoke(
								it
									.nativeValue(invokeName)
									.nativeValue(javaName)
									.javaObject,
								*it
									.nativeValue(invokeName)
									.nativeValue(argsName)
									.nativeValue(javaName)
									.javaObject as Array<*>
							)
					)
				)
			)
		}
