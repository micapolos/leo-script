package leo25.parser

import leo25.Script
import leo25.lineTo
import leo25.literal
import leo25.script

data class Location(
	val lineNumber: Int,
	val columnNumber: Int
)

val startLocation get() = Location(1, 1)
val Location.nextColumn get() = Location(lineNumber, columnNumber.inc())
val Location.newLine get() = Location(lineNumber.inc(), 1)

val Location.script: Script
	get() =
		script(
			"location" lineTo script(
				"line" lineTo script(literal(lineNumber)),
				"column" lineTo script(literal(columnNumber))
			)
		)