package leo.term.compiler

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stack
import leo.Type
import leo.TypeLine
import leo.fold
import leo.lineStack
import leo.lineTo
import leo.linkOrNull
import leo.name
import leo.plus
import leo.reverse
import leo.script
import leo.term.Term
import leo.term.fn
import leo.term.invoke
import leo.term.typed.Typed
import leo.term.typed.TypedTerm
import leo.type

data class SwitchCompiler<V>(
  val context: Context<V>,
  val remainingCaseStack: Stack<TypeLine>,
  val term: Term<V>,
  val typeOrNull: Type?
)

fun <V> SwitchCompiler<V>.plus(script: Script): SwitchCompiler<V> =
  fold(script.lineStack.reverse) { plus(it) }

fun <V> SwitchCompiler<V>.plus(line: ScriptLine): SwitchCompiler<V> =
  when (line) {
    is FieldScriptLine -> plus(line.field)
    is LiteralScriptLine -> compileError(script("switch" lineTo script("case" lineTo script(line))))
  }

fun <V> SwitchCompiler<V>.plus(field: ScriptField): SwitchCompiler<V> =
  remainingCaseStack.linkOrNull
    .let { remainingCaseStackLink ->
      if (remainingCaseStackLink == null) compileError(script("switch" lineTo script("case" lineTo script("not" lineTo script("expected" lineTo script(field))))))
      else if (remainingCaseStackLink.head.name != field.name) compileError(script("switch" lineTo script("case" lineTo script(
        field.name lineTo script(),
        "is" lineTo script("not" lineTo script(remainingCaseStackLink.head.name))))))
      else context.plus(binding(given(type(remainingCaseStackLink.head)))).typedTerm(field.rhs).let { caseTypedTerm ->
        caseTypedTerm.t.also {
          if (typeOrNull != null && typeOrNull != it) {
            compileError(
              script(
                "switch" lineTo script("case" lineTo caseTypedTerm.t.script.plus(
                    "is" lineTo script("not" lineTo typeOrNull.script)))))
          }
        }
          .let { newType ->
            term.invoke(fn(caseTypedTerm.v)).let { newTerm ->
              SwitchCompiler(context, remainingCaseStackLink.tail, newTerm, newType)
            }
          }
      }
    }

val <V> SwitchCompiler<V>.typedTerm: TypedTerm<V>
  get() =
    if (typeOrNull == null) compileError(script("empty" lineTo script("switch")))
    else remainingCaseStack.linkOrNull.let { link ->
      if (link != null)
        compileError(
          script(
            "switch" lineTo script("case" lineTo script(
              "expected" lineTo script(link.head.name)))))
      else Typed(term, typeOrNull)
    }
