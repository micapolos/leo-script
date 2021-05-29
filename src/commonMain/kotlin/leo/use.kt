package leo

import leo.natives.fileText
import leo.parser.scriptOrThrow

data class Use(val nameStackLink: StackLink<String>)

fun use(name: String, vararg names: String) = Use(stackLink(name, *names))

val Script.useOrNull: Use?
	get() =
		nameStackOrNull?.reverse?.linkOrNull?.let { nameStackLink ->
			Use(nameStackLink)
		}

val Use.fileString: String
	get() =
		stack(nameStackLink).array.joinToString("/")+".leo"

val Use.dictionary: Dictionary
	get() =
		try {
			fileString.fileText.scriptOrThrow
		} catch (valueError: ValueError) {
			value(
				"path" fieldTo value(field(literal(fileString.fileText))),
				"location" fieldTo valueError.value
			).throwError<Script>()
		} catch (ioException: Exception) {
			value(field(literal(ioException.message ?: ioException.toString()))).throwError<Script>()
		}.dictionary
