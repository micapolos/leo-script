package leo.natives

import leo.Number
import leo.anyValue
import leo.field
import leo.fieldTo
import leo.literal
import leo.native
import leo.numberAnyField
import leo.numberName
import leo.rhs
import leo.textAnyField
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
		nativeDefinition(value(nullName fieldTo value(), javaName fieldTo value())) {
			null.javaValue
		}

val trueJavaDefinition
	get() =
		nativeDefinition(value(trueName fieldTo value(), javaName fieldTo value())) {
			true.javaValue
		}

val falseJavaDefinition
	get() =
		nativeDefinition(value(falseName fieldTo value(), javaName fieldTo value())) {
			false.javaValue
		}

val javaObjectClassDefinition
	get() =
		nativeDefinition(
			value(
				objectName fieldTo value(javaName fieldTo anyValue),
				className fieldTo value()
			)
		) {
			value(
				className fieldTo
					this
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
			value(
				textAnyField,
				javaName fieldTo value()
			)
		) {
			nativeValue(textName).nativeText.javaValue
		}

val javaTextDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo anyValue,
				textName fieldTo value()
			)
		) {
			value(field(literal(nativeValue(javaName).javaObject as String)))
		}

val numberJavaDefinition
	get() =
		nativeDefinition(
			value(
				numberName fieldTo anyValue,
				javaName fieldTo value()
			)
		) {
			nativeValue(numberName).nativeNumber.javaValue
		}

val javaNumberDefinition
	get() =
		nativeDefinition(
			value(
				javaName fieldTo anyValue,
				numberName fieldTo value()
			)
		) {
			value(field(literal(nativeValue(javaName).javaObject as Number)))
		}

val numberIntegerObjectJavaDefinition
	get() =
		nativeDefinition(
			value(
				integerName fieldTo value(numberAnyField),
				javaName fieldTo value()
			)
		) {
			this
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
				integerName fieldTo value(javaName fieldTo anyValue),
				numberName fieldTo value()
			)
		) {
			value(field(literal(nativeValue(integerName).nativeValue(javaName).javaObject as Int)))
		}

val arrayJavaDefinition
	get() =
		nativeDefinition(
			value(
				arrayName fieldTo anyValue,
				javaName fieldTo value()
			)
		) {
			nativeValue(arrayName).nativeArray.javaValue
		}

val textClassJavaDefinition
	get() =
		nativeDefinition(
			value(
				className fieldTo value(textAnyField),
				javaName fieldTo value()
			)
		) {
			this
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
				fieldName fieldTo value(textAnyField)
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
