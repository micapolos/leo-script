package leo25.natives

import leo25.*
import leo25.Number
import leo25.*
import java.lang.reflect.Field
import java.lang.reflect.Method

object ClassLoaderSource

val classLoader = ClassLoaderSource::class.java.classLoader

val String.loadClass: Class<*>
	get() =
		classLoader.loadClass(this)

val nullJavaDefinition
	get() =
		nativeDefinition(script(line(nullName), line(javaName))) {
			null.javaValue
		}

val trueJavaDefinition
	get() =
		nativeDefinition(script(line(trueName), line(javaName))) {
			true.javaValue
		}

val falseJavaDefinition
	get() =
		nativeDefinition(script(line(falseName), line(javaName))) {
			false.javaValue
		}

val javaObjectClassDefinition
	get() =
		nativeDefinition(
			script(
				objectName lineTo script(javaName lineTo script(anyName)),
				className lineTo script()
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
			script(
				textName lineTo script(anyName),
				javaName lineTo script()
			)
		) {
			nativeValue(textName).nativeText.javaValue
		}

val javaTextDefinition
	get() =
		nativeDefinition(
			script(
				javaName lineTo script(anyName),
				textName lineTo script()
			)
		) {
			value(field(literal(nativeValue(javaName).javaObject as String)))
		}

val numberJavaDefinition
	get() =
		nativeDefinition(
			script(
				numberName lineTo script(anyName),
				javaName lineTo script()
			)
		) {
			nativeValue(numberName).nativeNumber.javaValue
		}

val javaNumberDefinition
	get() =
		nativeDefinition(
			script(
				javaName lineTo script(anyName),
				numberName lineTo script()
			)
		) {
			value(field(literal(nativeValue(javaName).javaObject as Number)))
		}

val numberIntegerObjectJavaDefinition
	get() =
		nativeDefinition(
			script(
				integerName lineTo script(numberName lineTo script(anyName)),
				javaName lineTo script()
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
			script(
				integerName lineTo script(javaName lineTo script(anyName)),
				numberName lineTo script()
			)
		) {
			value(field(literal(nativeValue(integerName).nativeValue(javaName).javaObject as Int)))
		}

val arrayJavaDefinition
	get() =
		nativeDefinition(
			script(
				arrayName lineTo script(anyName),
				javaName lineTo script()
			)
		) {
			nativeValue(arrayName).nativeArray.javaValue
		}

val textClassJavaDefinition
	get() =
		nativeDefinition(
			script(
				className lineTo script(
					textName lineTo script(anyName)
				),
				javaName lineTo script()
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
			script(
				className lineTo script(
					javaName lineTo script(anyName)
				),
				fieldName lineTo script(
					textName lineTo script(anyName)
				)
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

val javaFieldGetDefinition
	get() =
		nativeDefinition(
			script(
				fieldName lineTo script(javaName lineTo script(anyName)),
				getName lineTo script(javaName lineTo script(anyName))
			)
		) {
			this
				.nativeValue(fieldName)
				.nativeValue(javaName)
				.javaObject
				.run { this as Field }
				.get(
					this
						.nativeValue(getName)
						.nativeValue(javaName)
						.javaObject
				)
				.javaValue
		}

val javaClassMethodNameTextDefinition
	get() =
		nativeDefinition(
			script(
				className lineTo script(
					javaName lineTo script(anyName)
				),
				methodName lineTo script(
					textName lineTo script(anyName),
					argsName lineTo script(
						javaName lineTo script(anyName)
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
			script(
				methodName lineTo script(javaName lineTo script(anyName)),
				invokeName lineTo script(
					javaName lineTo script(anyName),
					argsName lineTo script(javaName lineTo script(anyName))
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
