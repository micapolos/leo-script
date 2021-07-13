package leo.term.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Type
import leo.TypeLine
import leo.asName
import leo.base.fold
import leo.base.reverse
import leo.compileName
import leo.debugName
import leo.doName
import leo.doingName
import leo.dropName
import leo.functionName
import leo.giveName
import leo.isEmpty
import leo.letName
import leo.lineSeq
import leo.lineTo
import leo.matchInfix
import leo.matchPrefix
import leo.pickName
import leo.plus
import leo.quoteName
import leo.repeatingName
import leo.reverse
import leo.script
import leo.switchName
import leo.term.compiled.Body
import leo.term.compiled.Compiled
import leo.term.compiled.CompiledLine
import leo.term.compiled.as_
import leo.term.compiled.body
import leo.term.compiled.compiled
import leo.term.compiled.do_
import leo.term.compiled.drop
import leo.term.compiled.fnLine
import leo.term.compiled.lineTo
import leo.term.compiled.onlyCompiledLine
import leo.term.compiled.pick
import leo.term.compiled.plus

data class Compiler<V>(
  val module: Module<V>,
  val compiled: Compiled<V>
)

fun <V> Module<V>.compiler(compiled: Compiled<V>): Compiler<V> = Compiler(this, compiled)
val <V> Module<V>.compiler: Compiler<V> get() = compiler(compiled())

fun <V> Compiler<V>.set(compiled: Compiled<V>): Compiler<V> =
  copy(compiled = compiled)

fun <V> Compiler<V>.set(module: Module<V>): Compiler<V> =
  copy(module = module)

val <V> Compiler<V>.context get() = module.context
val <V> Compiler<V>.environment get() = context.environment

fun <V> Compiler<V>.plus(script: Script): Compiler<V> =
  fold(script.lineSeq.reverse) { plus(it) }

fun <V> Compiler<V>.plus(scriptLine: ScriptLine): Compiler<V> =
  when (scriptLine) {
    is FieldScriptLine -> plus(scriptLine.field)
    is LiteralScriptLine -> plus(scriptLine.literal)
  }

fun <V> Compiler<V>.plus(literal: Literal): Compiler<V> =
  plus(environment.compiledLine(literal))

fun <V> Compiler<V>.plus(field: ScriptField): Compiler<V> =
  null
    ?: plusSpecialOrNull(field)
    ?: plusNamed(field)

fun <V> Compiler<V>.plusNamed(field: ScriptField): Compiler<V> =
  if (field.rhs.isEmpty) set(compiled()).plus(field.name lineTo compiled)
  else plus(field.name lineTo context.compiled(field.rhs))

fun <V> Compiler<V>.plusSpecialOrNull(field: ScriptField): Compiler<V>? =
  when (field.name) {
    asName -> as_(field.rhs)
    compileName -> compile(field.rhs)
    debugName -> debug(field.rhs)
    doName -> do_(field.rhs)
    dropName -> drop(field.rhs)
    giveName -> give(field.rhs)
    functionName -> function(field.rhs)
    letName -> let(field.rhs)
    pickName -> pick(field.rhs)
    switchName -> switch(field.rhs)
    quoteName -> quote(field.rhs)
    else -> null
  }

fun <V> Compiler<V>.as_(script: Script): Compiler<V> =
  as_(context.type(script))

fun <V> Compiler<V>.as_(type: Type): Compiler<V> =
  set(compiled.as_(type))

fun <V> Compiler<V>.compile(script: Script): Compiler<V> =
  if (!compiled.type.isEmpty) compileError(
    compiled.type.script
      .plus("compile" lineTo script)
      .plus("is" lineTo script(
        "not" lineTo script(
          "matching" lineTo script(
            "compile" lineTo script(
              "any" lineTo script(
                "language" lineTo script(
                  "any" lineTo script("script")))))))))
  else script.matchPrefix { name, rhs ->
    when (name) {
      else -> compileError(script("compile" lineTo script(name)))
    }
  }?: compileError(script("compile" lineTo script))

fun <V> Compiler<V>.debug(script: Script): Compiler<V> =
  if (!compiled.type.isEmpty) compileError(script("debug" lineTo script()))
  else set(environment.staticCompiled(script("debug" lineTo script(toScriptLine))))

fun <V> Compiler<V>.function(script: Script): Compiler<V> =
  script.matchInfix { lhs, name, rhs ->
    when (name) {
      doingName -> context.type(lhs).let { type ->
        context.plus(binding(given(type))).compiled(rhs).let { compiled ->
          plus(fnLine(type, compiled))
        }
      }
      else -> null
    }
  }?: compileError(
    script(
      "function" lineTo script,
      "is" lineTo script("not" lineTo script("matching" lineTo script(
        "function" lineTo script(
          "any" lineTo script("type"),
          "doing" lineTo script("any" lineTo script("compiled"))))))))

fun <V> Compiler<V>.do_(script: Script): Compiler<V> =
  do_(body(context.plus(binding(given(compiled.type))).compiled(script)))

fun <V> Compiler<V>.do_(body: Body<V>): Compiler<V> =
  set(compiled.do_(body))

fun <V> Compiler<V>.give(script: Script): Compiler<V> =
  script.matchInfix { lhs, name, rhs ->
    context.type(lhs).let { type ->
      when (name) {
        doingName ->
          set(
            compiled
              .do_(body(context.plus(binding(given(compiled.type))).compiled(rhs)))
              .as_(type))
        repeatingName -> TODO()
//          set(
//            compiled
//              .repeat(
//                context
//                  .plus(binding(definition(compiled.type.functionTo(type))))
//                  .plus(binding(given(compiled.type))).compiled(rhs))
//            .as_(type))
        else -> null
      }
    }
  }?: compileError(script("give" lineTo script))

fun <V> Compiler<V>.let(script: Script): Compiler<V> =
  if (!compiled.type.isEmpty) compileError(script("let" lineTo script("after" lineTo compiled.type.script)))
  else set(module.plusLet(script))

fun <V> Compiler<V>.pick(script: Script): Compiler<V> =
  pick(context.compiled(script).onlyCompiledLine)

fun <V> Compiler<V>.pick(line: CompiledLine<V>): Compiler<V> =
  set(compiled.pick(line))

fun <V> Compiler<V>.drop(script: Script): Compiler<V> =
  drop(context.type(script).onlyLine)

fun <V> Compiler<V>.drop(typeLine: TypeLine): Compiler<V> =
  set(compiled.drop(typeLine))

fun <V> Compiler<V>.switch(script: Script): Compiler<V> =
  compiled.switchTypedChoice.let { typedChoice ->
    set(SwitchCompiler(context, typedChoice.t.lineStack.reverse, null, null, null).plus(script).compiled(compiled))
  }

fun <V> Compiler<V>.quote(script: Script): Compiler<V> =
  if (!compiled.type.isEmpty) compileError(script("quote" lineTo script("after" lineTo compiled.type.script)))
  else set(environment.staticCompiled(script))

fun <V> Compiler<V>.plus(compiledLine: CompiledLine<V>): Compiler<V> =
  set(context.resolve(compiled.plus(compiledLine)))

val <V> Compiler<V>.completeCompiled: Compiled<V>
  get() =
    module.seal(compiled)