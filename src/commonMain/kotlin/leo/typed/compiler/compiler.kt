package leo.typed.compiler

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
import leo.beName
import leo.commentName
import leo.compileName
import leo.debugName
import leo.doName
import leo.doingName
import leo.exampleName
import leo.fieldOrNull
import leo.fold
import leo.functionLineTo
import leo.functionName
import leo.functionTo
import leo.givingName
import leo.haveName
import leo.isEmpty
import leo.isSimple
import leo.letName
import leo.lineSeq
import leo.lineTo
import leo.makeName
import leo.matchInfix
import leo.matchPrefix
import leo.nameStackOrNull
import leo.onlyLineOrNull
import leo.plus
import leo.quoteName
import leo.repeatName
import leo.repeatingName
import leo.reverse
import leo.script
import leo.selectName
import leo.stack
import leo.switchName
import leo.theName
import leo.type
import leo.typed.compiled.Body
import leo.typed.compiled.Compiled
import leo.typed.compiled.CompiledLine
import leo.typed.compiled.apply
import leo.typed.compiled.as_
import leo.typed.compiled.body
import leo.typed.compiled.compiled
import leo.typed.compiled.compiledChoice
import leo.typed.compiled.compiledSelect
import leo.typed.compiled.do_
import leo.typed.compiled.function
import leo.typed.compiled.have
import leo.typed.compiled.indexed.indexedExpression
import leo.typed.compiled.line
import leo.typed.compiled.lineTo
import leo.typed.compiled.make
import leo.typed.compiled.onlyCompiledLineOrNull
import leo.typed.compiled.plus
import leo.typed.compiled.recursive
import leo.typed.compiler.python.pythonEnvironment
import leo.typed.compiler.python.scriptLine
import leo.typed.compiler.scheme.schemeEnvironment
import leo.typed.indexed.python.python
import leo.typed.indexed.scheme.scheme
import leo.typesName
import leo.wordName

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
    beName -> be(field.rhs)
    commentName -> comment(field.rhs)
    compileName -> compile(field.rhs)
    debugName -> debug(field.rhs)
    doName -> do_(field.rhs)
    exampleName -> example(field.rhs)
    functionName -> function(field.rhs)
    haveName -> have(field.rhs)
    letName -> let(field.rhs)
    makeName -> make(field.rhs)
    quoteName -> quote(field.rhs)
    repeatName -> repeat(field.rhs)
    selectName -> select(field.rhs)
    switchName -> switch(field.rhs)
    typesName -> types(field.rhs)
    theName -> the(field.rhs)
    wordName -> word(field.rhs)
    else -> null
  }

fun <V> Compiler<V>.as_(script: Script): Compiler<V> =
  as_(block.module.type(script))

fun <V> Compiler<V>.be(script: Script): Compiler<V> =
  set(block.module.compiled(script))

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
      "scheme" -> set(environment.staticCompiled(script(rhs.compiled(schemeEnvironment).indexedExpression.scheme.leoScriptLine)))
      "python" -> set(environment.staticCompiled(script(rhs.compiled(pythonEnvironment).indexedExpression.python.scriptLine)))
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
      doingName -> functionDoing(lhs, rhs)
      repeatingName -> functionRepeating(lhs, rhs)
      else -> null
    }
  } ?: compileError(script(functionName lineTo script))

fun <V> Compiler<V>.have(script: Script): Compiler<V> =
  have(block.module.compiled(script))

fun <V> Compiler<V>.have(rhs: Compiled<V>): Compiler<V> =
  set(compiled.have(rhs))

fun <V> Compiler<V>.functionDoing(lhs: Script, rhs: Script): Compiler<V> =
  block.module.type(lhs).let { lhsType ->
    block.module
      .plus(binding(given(lhsType)))
      .compiled(rhs)
      .let { bodyCompiled ->
        set(
          compiled(
            compiled(
              line(function(lhsType, body(bodyCompiled))),
              lhsType functionLineTo bodyCompiled.type)))
      }
  }

fun <V> Compiler<V>.functionRepeating(lhs: Script, rhs: Script): Compiler<V> =
  lhs.matchInfix(givingName) { givingLhs, givingRhs ->
    block.module.type(givingLhs).let { lhsType ->
      block.module.type(givingRhs).let { rhsType ->
        block.module
          .plus(binding(lhsType functionTo rhsType))
          .plus(binding(given(lhsType)))
          .compiled(rhs)
          .as_(rhsType)
          .let { bodyCompiled ->
            set(
              compiled(
                compiled(
                  line(function(compiled.type, recursive(body(bodyCompiled)))),
                  compiled.type functionLineTo rhsType)))
        }
      }
    }
  } ?: compileError(script(functionName lineTo script(repeatingName)))

fun <V> Compiler<V>.do_(script: Script): Compiler<V> =
  block.module
    .plus(binding(given(compiled.type)))
    .compiled(script)
    .let { bodyCompiled ->
      apply(
        compiled(
          compiled(
            line(function(compiled.type, body(bodyCompiled))),
            compiled.type functionLineTo bodyCompiled.type)))
    }

fun <V> Compiler<V>.repeat(script: Script): Compiler<V> =
  script.matchInfix(doingName) { doingLhs, doingRhs ->
    doingLhs.matchPrefix(givingName) { givingRhs ->
      block.module.type(givingRhs).let { rhsType ->
        block.module
          .plus(binding(compiled.type functionTo rhsType))
          .plus(binding(given(compiled.type)))
          .compiled(doingRhs)
          .as_(rhsType)
          .let { bodyCompiled ->
            apply(
              compiled(
                compiled(
                  line(function(compiled.type, recursive(body(bodyCompiled)))),
                  compiled.type functionLineTo rhsType)))
          }
      }
    }
  } ?: compileError(script(repeatName lineTo script))

fun <V> Compiler<V>.apply(rhs: Compiled<V>): Compiler<V> =
  set(compiled.apply(rhs))

fun <V> Compiler<V>.do_(body: Body<V>): Compiler<V> =
  set(compiled.do_(body))

fun <V> Compiler<V>.example(script: Script): Compiler<V> =
  block.module.compiled(script).let { this }

fun <V> Compiler<V>.let(script: Script): Compiler<V> =
  if (!compiled.type.isEmpty) compileError(script("let" lineTo script("after" lineTo compiled.type.script)))
  else set(block.plusLet(script))

fun <V> Compiler<V>.make(script: Script): Compiler<V> =
  script.nameStackOrNull?.let { nameStack ->
    fold(nameStack.reverse) { make(it) }
  } ?: compileError(script("make" lineTo script))

fun <V> Compiler<V>.make(name: String): Compiler<V> =
  set(compiled.make(name))

fun <V> Compiler<V>.select(script: Script): Compiler<V> =
  set(
    SelectCompiler(block.module, compiledSelect())
      .plus(script)
      .compiledSelect
      .compiled)

fun <V> Compiler<V>.switch(script: Script): Compiler<V> =
  compiled.compiledChoice.let { compiledChoice ->
    set(
      SwitchCompiler(
        block.module,
        compiledChoice.choice.lineStack.reverse,
        compiledChoice.choice.isSimple,
        stack(),
        null)
        .plus(script)
        .compiled(compiled(compiledChoice.expression, type(compiledChoice.choice))))
  }

fun <V> Compiler<V>.quote(script: Script): Compiler<V> =
  if (!compiled.type.isEmpty) compileError(script("quote" lineTo script("after" lineTo compiled.type.script)))
  else set(environment.staticCompiled(script))

fun <V> Compiler<V>.the(script: Script): Compiler<V> =
  block.module.compiled(script).onlyCompiledLineOrNull?.let { plus(it) }
    ?: compileError(script(theName lineTo script))

fun <V> Compiler<V>.word(script: Script): Compiler<V> =
  script.onlyLineOrNull?.fieldOrNull?.let { scriptField ->
    block.module.compiled(scriptField.rhs)?.let { rhsCompiled ->
      word(scriptField.name lineTo rhsCompiled)
    }
  }?: compileError(script(wordName lineTo script))

fun <V> Compiler<V>.word(compiledLine: CompiledLine<V>): Compiler<V> =
  set(compiled.plus(compiledLine))

fun <V> Compiler<V>.plus(compiledLine: CompiledLine<V>): Compiler<V> =
  set(block.module.resolve(compiled.plus(compiledLine)))

val <V> Compiler<V>.completeCompiled: Compiled<V>
  get() =
    block.seal(compiled)

fun <V> Compiler<V>.plus(binding: Binding): Compiler<V> =
  set(block.plus(binding))

val <V> Compiler<V>.begin: Compiler<V> get() =
  block.module.block.compiler
