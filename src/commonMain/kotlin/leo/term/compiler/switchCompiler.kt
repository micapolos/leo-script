package leo.term.compiler

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stack
import leo.Type
import leo.TypeLine
import leo.base.ifNotNull
import leo.base.orIfNull
import leo.doingName
import leo.fold
import leo.lineStack
import leo.lineTo
import leo.linkOrNull
import leo.name
import leo.plus
import leo.reverse
import leo.rhsOrNull
import leo.script
import leo.term.Term
import leo.term.fn
import leo.term.get
import leo.term.invoke
import leo.term.typed.Typed
import leo.term.typed.TypedTerm
import leo.type

data class SwitchCompiler<V>(
  val context: Context<V>,
  val remainingCaseStack: Stack<TypeLine>,
  val firstTermOrNull: Term<V>?,
  val secondTermOrNull: Term<V>?,
  val typeOrNull: Type?,
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
      if (remainingCaseStackLink == null)
        compileError(
          script(
            "switch" lineTo script(
              "case" lineTo script(
                "not" lineTo script(
                  "expected" lineTo script(field))))))
      else if (remainingCaseStackLink.head.name != field.name)
        compileError(
          script(
            "switch" lineTo script(
              "case" lineTo script(
                field.name lineTo script(),
                "is" lineTo script(
                  "not" lineTo script(
                    remainingCaseStackLink.head.name))))))
      else field.rhs
        .rhsOrNull(doingName)
        .orIfNull { compileError(script("doing")) }
        .let { rhs ->
          context
            .plus(binding(given(type(remainingCaseStackLink.head))))
            .typedTerm(rhs)
            .let { caseTypedTerm ->
              caseTypedTerm.t.also {
                if (typeOrNull != null && typeOrNull != it) {
                  compileError(
                    script(
                      "switch" lineTo script(
                        "case" lineTo caseTypedTerm.t.script.plus(
                          "is" lineTo script(
                            "not" lineTo typeOrNull.script)))))
                }
              }
                .let { newType ->
                  if (firstTermOrNull == null)
                    SwitchCompiler(
                      context,
                      remainingCaseStackLink.tail,
                      fn(caseTypedTerm.v),
                      null,
                      newType)
                  else if (secondTermOrNull == null)
                    SwitchCompiler(
                      context,
                      remainingCaseStackLink.tail,
                      firstTermOrNull,
                      fn(caseTypedTerm.v),
                      newType)
                  else
                    SwitchCompiler(
                      context,
                      remainingCaseStackLink.tail,
                      fn(get<V>(0).invoke(firstTermOrNull).invoke(secondTermOrNull)),
                      fn(caseTypedTerm.v),
                      newType)
                }
            }
        }
    }

fun <V> SwitchCompiler<V>.typedTerm(inputTerm: Term<V>): TypedTerm<V> =
    if (typeOrNull == null || firstTermOrNull == null)
      compileError(script("empty" lineTo script("switch")))
    else remainingCaseStack.linkOrNull.let { link ->
      if (link != null)
        compileError(
          script(
            "switch" lineTo script(
              "case" lineTo script(
                "expected" lineTo script(
                  link.head.name)))))
      else Typed(
        inputTerm
          .invoke(firstTermOrNull)
          .ifNotNull(secondTermOrNull) { invoke(it) },
        typeOrNull)
    }
