package leo.java.lang

fun execExpectingExitCode(expectedExitCode: Int, vararg command: String): String {
	val runtime = Runtime.getRuntime()
	val process = runtime.exec(command)
	val string = process.inputStream.reader().readText()
	val exitCode = process.waitFor()
	if (exitCode != expectedExitCode) error("exec(${command.contentToString()}) = $exitCode\n${process.errorStream.reader().readText()}")
	else return string
}

fun exec(vararg command: String): String =
	execExpectingExitCode(0, *command)
