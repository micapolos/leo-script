package leo.term.compiler

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Stack
import leo.Type
import leo.TypeLine
import leo.base.orIfNull
import leo.base.runIf
import leo.doingName
import leo.fold
import leo.lineStack
import leo.lineTo
import leo.linkOrNull
import leo.name
import leo.plus
import leo.push
import leo.reverse
import leo.rhsOrNull
import leo.script
import leo.term.compiled.Compiled
import leo.term.compiled.switch
import leo.type

data class SwitchCompiler<V>(
  val context: Context<V>,
  val remainingCaseStack: Stack<TypeLine>,
  val isSimple: Boolean,
  val caseStack: Stack<Compiled<V>>,
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
            .runIf(!isSimple) { bind(type(remainingCaseStackLink.head)) }
            .compiled(rhs)
            .let { caseCompiled ->
              caseCompiled.type.also {
                if (typeOrNull != null && typeOrNull != it) {
                  compileError(
                    script(
                      "switch" lineTo script(
                        "case" lineTo caseCompiled.type.script.plus(
                          "is" lineTo script(
                            "not" lineTo typeOrNull.script)))))
                }
              }
                .let { newType ->
                  SwitchCompiler(
                    context,
                    remainingCaseStackLink.tail,
                    isSimple,
                    caseStack.push(caseCompiled),
                    newType)
                }
            }
        }
    }

fun <V> SwitchCompiler<V>.compiled(inputTerm: Compiled<V>): Compiled<V> =
    if (typeOrNull == null)
      compileError(script("empty" lineTo script("switch")))
    else remainingCaseStack.linkOrNull.let { link ->
      if (link != null)
        compileError(
          script(
            "switch" lineTo script(
              "case" lineTo script(
                "expected" lineTo script(
                  link.head.name)))))
      else
        inputTerm.switch(caseStack, typeOrNull)
    }
