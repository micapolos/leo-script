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
					this
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
			this
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
			value(field(literal(nativeValue(textName).nativeValue(javaName).javaObject as String)))
		}

val numberJavaDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo numberAnyValue
			)
		) {
			nativeValue(javaName).nativeValue(numberName).nativeNumber.javaValue
		}

val javaNumberDefinition
	get() =
		nativeDefinition(
			value(
				numberName fieldTo value(javaName fieldTo anyValue)
			)
		) {
			value(field(literal(nativeValue(numberName).nativeValue(javaName).javaObject as Number)))
		}

val numberIntegerObjectJavaDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo value(integerName fieldTo value(numberAnyField))
			)
		) {
			this
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
			value(field(literal(nativeValue(numberName).nativeValue(integerName).nativeValue(javaName).javaObject as Int)))
		}

val arrayJavaDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo value(arrayName fieldTo anyValue),
			)
		) {
			nativeValue(javaName).nativeValue(arrayName).nativeArray.javaValue
		}

val textClassJavaDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo value(className fieldTo value(textAnyField))
			)
		) {
			this
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
				fieldName fieldTo this
					.nativeValue(className)
					.nativeValue(javaName)
					.javaObject
					.run { this as Class<*> }
					.getField(
						this
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
			this
				.nativeValue(fieldName)
				.nativeValue(javaName)
				.javaObject
				.run { this as Field }
				.get(
					this
						.nativeValue(objectName)
						.nativeValue(javaName)
						.javaObject
				)
				.javaValue
		}

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
				methodName fieldTo this
					.nativeValue(className)
					.nativeValue(javaName)
					.javaObject
					.run { this as Class<*> }
					.getMethod(
						this
							.nativeValue(methodName)
							.nativeValue(textName)
							.nativeText,
						*this
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
						this
							.nativeValue(methodName)
							.nativeValue(javaName)
							.javaObject
							.run { this as Method }
							.invoke(
								this
									.nativeValue(invokeName)
									.nativeValue(javaName)
									.javaObject,
								*this
									.nativeValue(invokeName)
									.nativeValue(argsName)
									.nativeValue(javaName)
									.javaObject as Array<*>
							)
					)
				)
			)
		}
