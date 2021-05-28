package leo25

import leo14.Script

// TODO: Create custom implementation parsed from Script
data class Pattern(val script: Script)

fun pattern(script: Script) = Pattern(script)
