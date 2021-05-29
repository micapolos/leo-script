package leo25.java.io

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.System.`in`

val inString get() = BufferedReader(InputStreamReader(`in`)).readText()