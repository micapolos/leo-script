package leo.typed.compiler

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
import leo.nameOrNull
import leo.plus
import leo.push
import leo.reverse
import leo.rhsOrNull
import leo.script
import leo.scriptLine
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiled.switch

data class SwitchCompiler<V>(
  val module: Module<V>,
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
      else if (remainingCaseStackLink.head.nameOrNull != field.name)
        compileError(
          script(
            "switch" lineTo script(
              "case" lineTo script(
                field.name lineTo script(),
                "is" lineTo script(
                  "not" lineTo script(
                    remainingCaseStackLink.head.nameOrNull!!))))))
      else field.rhs
        .rhsOrNull(doingName)
        .orIfNull { compileError(script("doing")) }
        .let { rhs ->
          module
            .runIf(!isSimple) { plus(binding(given(type(remainingCaseStackLink.head)))) }
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
                    module,
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
                  link.head.scriptLine)))))
      else
        inputTerm.switch(caseStack, typeOrNull)
    }
