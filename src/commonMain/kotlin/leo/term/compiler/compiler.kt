package leo.term.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.Type
import leo.asName
import leo.base.fold
import leo.base.reverse
import leo.commentName
import leo.compileName
import leo.debugName
import leo.doName
import leo.doingName
import leo.exampleName
import leo.functionName
import leo.giveName
import leo.isEmpty
import leo.isSimple
import leo.letName
import leo.lineSeq
import leo.lineTo
import leo.matchInfix
import leo.matchPrefix
import leo.plus
import leo.quoteName
import leo.repeatingName
import leo.reverse
import leo.script
import leo.stack
import leo.switchChoice
import leo.switchName
import leo.term.compiled.Body
import leo.term.compiled.Compiled
import leo.term.compiled.CompiledLine
import leo.term.compiled.as_
import leo.term.compiled.body
import leo.term.compiled.compiled
import leo.term.compiled.do_
import leo.term.compiled.fnLine
import leo.term.compiled.indexed.indexedExpression
import leo.term.compiled.lineTo
import leo.term.compiled.plus
import leo.term.compiler.scheme.schemeEnvironment
import leo.term.indexed.scheme.scheme
import leo.typesName

data class Compiler<V>(
  val block: Block<V>,
  val compiled: Compiled<V>
)

fun <V> Block<V>.compiler(compiled: Compiled<V>): Compiler<V> = Compiler(this, compiled)
val <V> Block<V>.compiler: Compiler<V> get() = compiler(compiled())

fun <V> Compiler<V>.set(compiled: Compiled<V>): Compiler<V> =
  copy(compiled = compiled)

fun <V> Compiler<V>.set(block: Block<V>): Compiler<V> =
  copy(block = block)

val <V> Compiler<V>.context get() = block.context
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
  else plus(field.name lineTo block.module.compiled(field.rhs))

fun <V> Compiler<V>.plusSpecialOrNull(field: ScriptField): Compiler<V>? =
  when (field.name) {
    asName -> as_(field.rhs)
    commentName -> comment(field.rhs)
    compileName -> compile(field.rhs)
    debugName -> debug(field.rhs)
    doName -> do_(field.rhs)
    exampleName -> example(field.rhs)
    giveName -> give(field.rhs)
    functionName -> function(field.rhs)
    letName -> let(field.rhs)
    quoteName -> quote(field.rhs)
    switchName -> switch(field.rhs)
    typesName -> types(field.rhs)
    else -> null
  }

fun <V> Compiler<V>.as_(script: Script): Compiler<V> =
  as_(block.module.type(script))

fun <V> Compiler<V>.as_(type: Type): Compiler<V> =
  set(compiled.as_(type))

fun <V> Compiler<V>.comment(@Suppress("UNUSED_PARAMETER") script: Script): Compiler<V> =
  this

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
      "scheme" -> set(environment.staticCompiled(script(rhs.compiled(schemeEnvironment).indexedExpression.scheme.toScriptLine)))
      else -> compileError(script("compile" lineTo script(name)))
    }
  }?: compileError(script("compile" lineTo script))

fun <V> Compiler<V>.types(script: Script): Compiler<V> =
  set(block.updateTypesBlock { it.compiler.plus(script).block })

fun <V> Compiler<V>.debug(@Suppress("UNUSED_PARAMETER") script: Script): Compiler<V> =
  if (!compiled.type.isEmpty) compileError(script("debug" lineTo script()))
  else set(environment.staticCompiled(script("debug" lineTo script(toScriptLine))))

fun <V> Compiler<V>.function(script: Script): Compiler<V> =
  script.matchInfix { lhs, name, rhs ->
    when (name) {
      doingName -> block.module.type(lhs).let { type ->
        block.module.bind(type).compiled(rhs).let { compiled ->
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
  do_(body(block.module.bind(compiled.type).compiled(script)))

fun <V> Compiler<V>.do_(body: Body<V>): Compiler<V> =
  set(compiled.do_(body))

fun <V> Compiler<V>.example(script: Script): Compiler<V> =
  block.module.compiled(script).let { this }

fun <V> Compiler<V>.give(script: Script): Compiler<V> =
  script.matchInfix { lhs, name, rhs ->
    block.module.type(lhs).let { type ->
      when (name) {
        doingName ->
          set(
            compiled
              .do_(body(block.module.bind(compiled.type).compiled(rhs)))
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
  else set(block.plusLet(script))

fun <V> Compiler<V>.switch(script: Script): Compiler<V> =
  compiled.type.switchChoice.let { choice ->
    set(
      SwitchCompiler(
        block.module,
        choice.lineStack.reverse,
        choice.isSimple,
        stack(),
        null)
        .plus(script)
        .compiled(compiled))
  }

fun <V> Compiler<V>.quote(script: Script): Compiler<V> =
  if (!compiled.type.isEmpty) compileError(script("quote" lineTo script("after" lineTo compiled.type.script)))
  else set(environment.staticCompiled(script))

fun <V> Compiler<V>.plus(compiledLine: CompiledLine<V>): Compiler<V> =
  set(context.resolve(compiled.plus(compiledLine)))

val <V> Compiler<V>.completeCompiled: Compiled<V>
  get() =
    block.seal(compiled)
