package leo.base.stak

import leo.Script
import leo.ScriptLine
import leo.base.fold
import leo.base.reverse
import leo.emptyScript
import leo.lineTo
import leo.plus
import leo.script

fun <T : Any> Stak<T>.scriptLine(fn: T.() -> Script): ScriptLine =
	"stak" lineTo script(
		nodeOrNull?.scriptLine(fn) ?: "node" lineTo script("null")
	)

fun <T : Any> Node<T>.scriptLine(fn: T.() -> Script): ScriptLine =
	"node" lineTo script(
		"value" lineTo value.fn(),
		linkOrNull?.scriptLine(fn) ?: "link" lineTo script("null"))

fun <T : Any> Link<T>.scriptLine(fn: T.() -> Script): ScriptLine =
	"link" lineTo script(
		node.scriptLine(fn),
		linkOrNull?.scriptLine(fn) ?: "link" lineTo script("null"))

fun <T : Any> Stak<T>.contentScript(fn: T.() -> ScriptLine): Script =
	emptyScript.fold(seq.reverse) { plus(it.fn()) }
