package leo.term

import leo.Script
import leo.ScriptLine
import leo.base.stak.map
import leo.base.stak.stack
import leo.lineTo
import leo.literal
import leo.plus
import leo.script

val <T> Term<T>.scriptLine: ScriptLine
  get() =
    "term" lineTo script

val <T> Term<T>.script: Script
  get() =
    when (this) {
      is AbstractionTerm -> abstraction.script
      is ApplicationTerm -> application.script
      is NativeTerm -> native.nativeScript
      is VariableTerm -> variable.script
    }

val TermVariable.script: Script
  get() =
    script("variable" lineTo script(literal(index)))

val <T> T.nativeScript: Script
  get() =
    script("native" lineTo script(literal(toString())))

val <T> TermApplication<T>.script: Script
  get() =
    lhs.script.plus("apply" lineTo rhs.script)

val <T> TermAbstraction<T>.script: Script
  get() =
    script("lambda" lineTo term.script)

val <T> Value<T>.scriptLine: ScriptLine
  get() =
    "value" lineTo script

val <T> Value<T>.script: Script
  get() =
    when (this) {
      is FunctionValue -> script(function.scriptLine)
      is NativeValue -> native.nativeScript
    }

val <T> Function<T>.scriptLine
  get() =
    "function" lineTo script(scope.scriptLine, term.scriptLine)


val <T> Scope<T>.scriptLine
  get() =
    "scope" lineTo script("values" lineTo valueStak.map { scriptLine }.stack.script)