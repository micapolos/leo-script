package leo.term

import leo.Script
import leo.ScriptLine
import leo.base.stak.map
import leo.base.stak.stack
import leo.lineTo
import leo.literal
import leo.plus
import leo.script

fun <T> Term<T>.scriptLine(fn: (T) -> ScriptLine): ScriptLine =
  "term" lineTo script(fn)

fun <T> Term<T>.script(fn: (T) -> ScriptLine): Script =
  when (this) {
    is AbstractionTerm -> abstraction.script(fn)
    is ApplicationTerm -> application.script(fn)
    is NativeTerm -> native.nativeScript(fn)
    is VariableTerm -> variable.script
  }

val IndexVariable.script: Script
  get() =
    script("variable" lineTo script(literal(index)))

fun <T> T.nativeScript(fn: (T) -> ScriptLine): Script =
    script("native" lineTo script(fn(this)))

fun <T> TermApplication<T>.script(fn: (T) -> ScriptLine): Script =
    lhs.script(fn).plus("apply" lineTo rhs.script(fn))

fun <T> TermAbstraction<T>.script(fn: (T) -> ScriptLine): Script =
    script("lambda" lineTo term.script(fn))

fun <T> Value<T>.scriptLine(fn: (T) -> ScriptLine): ScriptLine =
    "value" lineTo script(fn)

fun <T> Value<T>.script(fn: (T) -> ScriptLine): Script =
  when (this) {
    is FunctionValue -> script(function.scriptLine(fn))
    is NativeValue -> native.nativeScript(fn)
  }

fun <T> Function<T>.scriptLine(fn: (T) -> ScriptLine): ScriptLine =
    "function" lineTo script(scope.scriptLine(fn), term.scriptLine(fn))


fun <T> Scope<T>.scriptLine(fn: (T) -> ScriptLine): ScriptLine =
    "scope" lineTo script("values" lineTo valueStak.map { scriptLine(fn) }.stack.script)