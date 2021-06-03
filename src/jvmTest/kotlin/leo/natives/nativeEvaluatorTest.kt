package leo.natives

import leo.base.assertEqualTo
import leo.base.assertNotNull
import leo.evaluate
import leo.get
import leo.line
import leo.lineTo
import leo.literal
import leo.native
import leo.nativeName
import leo.numberName
import leo.script
import leo.textName
import kotlin.test.Test

class NativeEvaluatorTest {
	@Test
	fun nullObjectJava() {
		script(line(nullName), line(javaName))
			.evaluate
			.assertEqualTo(script(javaName lineTo native(null)))
	}

	@Test
	fun trueObjectJava() {
		script(line(trueName), line(javaName))
			.evaluate
			.assertEqualTo(script(javaName lineTo native(true)))
	}

	@Test
	fun falseObjectJava() {
		script(line(falseName), line(javaName))
			.evaluate
			.assertEqualTo(script(javaName lineTo native(false)))
	}

	@Test
	fun javaObjectClassJava() {
		script(
			line(literal("foo")),
			javaName lineTo script(),
			objectName lineTo script(),
			className lineTo script()
		)
			.evaluate
			.assertEqualTo(script(className lineTo script(javaName lineTo native("foo".javaClass))))
	}

	@Test
	fun javaText() {
		script(
			line(literal("Hello, world!")),
			javaName lineTo script(),
			textName lineTo script()
		)
			.evaluate
			.assertEqualTo(script(literal("Hello, world!")))
	}

	@Test
	fun numberJava() {
		script(
			line(literal(123)),
			javaName lineTo script()
		)
			.evaluate
			.assertEqualTo(script(javaName lineTo native(123)))
	}

	@Test
	fun textJava() {
		script(
			line(literal("Hello, world!")),
			javaName lineTo script()
		)
			.evaluate
			.assertEqualTo(script(javaName lineTo native("Hello, world!")))
	}

	@Test
	fun numberIntegerJava() {
		script(
			line(literal(123)),
			integerName lineTo script(),
			javaName lineTo script()
		)
			.evaluate
			.assertEqualTo(script(javaName lineTo native(123)))
	}

	@Test
	fun javaObjectIntegerNumber() {
		script(
			line(literal(123)),
			integerName lineTo script(),
			javaName lineTo script(),
			integerName lineTo script(),
			numberName lineTo script()
		)
			.evaluate
			.assertEqualTo(script(literal(123)))
	}

	@Test
	fun javaNumber() {
		script(
			line(literal(123)),
			javaName lineTo script(),
			numberName lineTo script()
		)
			.evaluate
			.assertEqualTo(script(literal(123)))
	}

	@Test
	fun arrayObjectJava() {
		script(
			elementName lineTo script(line(literal("foo")), line(javaName)),
			elementName lineTo script(line(literal("bar")), line(javaName)),
			arrayName lineTo script(),
			javaName lineTo script()
		)
			.evaluate
			.get(nativeName)
			.assertNotNull
	}

	@Test
	fun textNameClassJava() {
		script(
			line(literal("java.lang.String")),
			className lineTo script(),
			javaName lineTo script()
		)
			.evaluate
			.assertEqualTo(script(javaName lineTo native(String::class.java)))
	}

	@Test
	fun javaClassField() {
		script(
			line(literal("java.lang.Integer")),
			className lineTo script(),
			javaName lineTo script(),
			className lineTo script(),
			fieldName lineTo script(literal("TYPE"))
		)
			.evaluate
			.assertEqualTo(
				script(
					fieldName lineTo script(
						javaName lineTo native(
							Integer::class.java.getField("TYPE")
						)
					)
				)
			)
	}

	@Test
	fun javaFieldGet() {
		script(
			line(literal("java.lang.Integer")),
			className lineTo script(),
			javaName lineTo script(),
			className lineTo script(),
			fieldName lineTo script(literal("TYPE")),
			objectName lineTo script(line(nullName), line(javaName))
		)
			.evaluate
			.assertEqualTo(
				script(
					javaName lineTo native(
						Integer::class.java.getField("TYPE").get(null)
					)
				)
			)
	}

	@Test
	fun classJavaObjectMethod() {
		script(
			line(literal("java.lang.String")),
			className lineTo script(),
			javaName lineTo script(),
			className lineTo script(),
			methodName lineTo script(
				line(literal("length")),
				argsName lineTo script(
					line(arrayName), line(javaName)
				)
			)
		)
			.evaluate
			.assertEqualTo(
				script(
					methodName lineTo script(
						javaName lineTo native(String::class.java.getMethod("length"))
					)
				)
			)
	}

	@Test
	fun javaObjectInvokeMethod() {
		script(
			line(literal("java.lang.String")),
			className lineTo script(),
			javaName lineTo script(),
			className lineTo script(),
			methodName lineTo script(
				line(literal("length")),
				argsName lineTo script(line(arrayName), line(javaName))
			),
			invokeName lineTo script(
				line(literal("Hello, world!")),
				javaName lineTo script(),
				argsName lineTo script(line(arrayName), line(javaName))
			)
		)
			.evaluate
			.assertEqualTo(
				script(
					javaName lineTo native(
						String::class.java.getMethod("length").invoke("Hello, world!")
					)
				)
			)
	}
}