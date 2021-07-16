package leo.term.compiler

import leo.FieldScriptLine
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Type
import leo.base.fold
import leo.base.ifNotNull
import leo.base.reverse
import leo.base.runIf
import leo.functionTo
import leo.givingName
import leo.line
import leo.lineSeq
import leo.lineTo
import leo.script
import leo.term.compiled.CompiledFunction
import leo.term.compiled.as_
import leo.term.compiled.body
import leo.term.compiled.compiled
import leo.term.compiled.function
import leo.term.compiled.recursive

data class FunctionCompiler<V>(
  val lhsType: Type,
  val isRepeat: Boolean,
  val rhsTypeOrNull: Type?,
  val isEmpty: Boolean,
  val bodyCompiler: Compiler<V>)

fun <V> Module<V>.functionCompiler(type: Type, isRepeat: Boolean): FunctionCompiler<V> =
  FunctionCompiler(type, isRepeat, null, true, block.compiler)

val <V> FunctionCompiler<V>.compiledFunction: CompiledFunction<V> get() =
  touch.run {
    bodyCompiler.completeCompiled.let { rhsCompiled ->
      compiled(
        function(
          lhsType,
          body(rhsCompiled.ifNotNull(rhsTypeOrNull) { as_(it) })
            .runIf(isRepeat) { recursive(this) }),
        lhsType functionTo rhsCompiled.type)
    }
  }

fun <V> FunctionCompiler<V>.plus(script: Script): FunctionCompiler<V> =
  fold(script.lineSeq.reverse) { plus(it) }

fun <V> FunctionCompiler<V>.plus(scriptLine: ScriptLine): FunctionCompiler<V> =
  when (scriptLine) {
    is FieldScriptLine -> plus(scriptLine.field)
    is LiteralScriptLine -> plusBody(scriptLine)
  }

fun <V> FunctionCompiler<V>.plus(scriptField: ScriptField): FunctionCompiler<V> =
  when (scriptField.name) {
    givingName -> giving(scriptField.rhs)
    else -> plusBody(line(scriptField))
  }

fun <V> FunctionCompiler<V>.giving(script: Script): FunctionCompiler<V> =
  if (rhsTypeOrNull != null) compileError(script("giving"))
  else bodyCompiler.block.module.type(script).let { rhsType ->
    copy(rhsTypeOrNull = rhsType)
  }

fun <V> FunctionCompiler<V>.plusBody(scriptLine: ScriptLine): FunctionCompiler<V> =
  touch.run { copy(bodyCompiler = bodyCompiler.plus(scriptLine)) }

val <V> FunctionCompiler<V>.touch: FunctionCompiler<V> get() =
  runIf(isEmpty) {
    this
      .markNonEmpty
      .runIf(isRepeat) {
        if (rhsTypeOrNull == null) compileError(script("no" lineTo script("giving" lineTo script("type"))))
        else plus(binding(lhsType functionTo rhsTypeOrNull))
      }
      .bind(lhsType)
  }

val <V> FunctionCompiler<V>.markNonEmpty get() = copy(isEmpty = false)
fun <V> FunctionCompiler<V>.bind(type: Type) = copy(bodyCompiler = bodyCompiler.bind(type))
fun <V> FunctionCompiler<V>.plus(binding: Binding) = copy(bodyCompiler = bodyCompiler.plus(binding))