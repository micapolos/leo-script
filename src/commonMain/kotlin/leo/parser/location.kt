package leo.parser

import leo.Script
import leo.lineTo
import leo.literal
import leo.script

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