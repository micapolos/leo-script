package leo

import leo.base.assertEqualTo
import leo.base.assertNull
import kotlin.test.Test

class ApplicationTest {
	@Test
	fun nonRecursive() {
		val dictionary = dictionary(
			definition(let(value("ping"), binding(value("pong")))),
			definition(let(value("pong"), binding(value("ping")))))

		dictionary
			.applicationOrNull(value("ping"))
			.assertEqualTo(application(dictionary(), binding(value("pong"))))

		dictionary
			.applicationOrNull(value("pong"))
			.assertEqualTo(
				application(
					dictionary(definition(let(value("ping"), binding(value("pong"))))),
					binding(value("ping"))))

		dictionary
			.applicationOrNull(value("zonk"))
			.assertNull
	}

	@Test
	fun recursive_infinite() {
		dictionary(
			definition(
				recursive(
					dictionary(),
					let(value("ping"), binding(body(block(syntax("ping"))))))))
			.applicationOrNull(value("ping"))
			.assertEqualTo(
				application(
					dictionary(
						definition(
							recursive(
								dictionary(),
								let(value("ping"), binding(body(block(syntax("ping")))))))),
					binding(body(block(syntax("ping"))))))
	}

	@Test
	fun recursive() {
		val dictionary = dictionary(
			definition(let(value("foo"), binding(value("bar")))),
			definition(
				recursive(
					dictionary(definition(let(value("ping"), binding(value("pong"))))),
					let(value("pong"), binding(value("ping"))))))

		dictionary
			.applicationOrNull(value("pong"))
			.assertEqualTo(
				application(
					dictionary(
						definition(let(value("foo"), binding(value("bar")))),
						definition(recursive(
							dictionary(definition(let(value("ping"), binding(value("pong"))))),
							let(value("pong"), binding(value("ping")))))),
					binding(value("ping"))))

		dictionary
			.applicationOrNull(value("ping"))
			.assertEqualTo(
				application(
					dictionary(
						definition(let(value("foo"), binding(value("bar")))),
						definition(recursive(
							dictionary(),
							let(value("pong"), binding(value("ping")))))),
					binding(value("pong"))))

		dictionary
			.applicationOrNull(value("foo"))
			.assertEqualTo(
				application(
					dictionary(),
					binding(value("bar"))))

		dictionary
			.applicationOrNull(value("zoo"))
			.assertNull
	}
}