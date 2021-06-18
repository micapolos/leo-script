package leo.base

inline fun <T> resultToTimeMillis(fn: () -> T): Pair<T, Long> {
	val startTime = System.currentTimeMillis()
	val result = fn()
	val endTime = System.currentTimeMillis()
	val time = endTime - startTime
	return result to time
}

inline fun timeMillis(fn: () -> Unit): Long =
	resultToTimeMillis { fn() }.second

inline fun <T, R> T.runPrintingTime(prefix: String, fn: T.() -> R): R {
	val resultToTime = resultToTimeMillis { fn() }
	println("${prefix}${resultToTime.second}ms")
	return resultToTime.first
}

inline fun <T, R> T.runPrintingTime(fn: T.() -> R): R =
	runPrintingTime("", fn)

inline fun printTime(label: String, fn: () -> Unit) {
	Unit.runPrintingTime(label) { fn() }
}

inline fun printTime(fn: () -> Unit) =
	printTime("", fn)