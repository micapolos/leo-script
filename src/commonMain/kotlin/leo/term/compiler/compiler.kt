package leo.term.compiler

import leo.FieldScriptLine
import leo.Literal
import leo.LiteralScriptLine
import leo.Script
import leo.ScriptField
import leo.ScriptLine
import leo.base.fold
import leo.base.notNullOrError
import leo.base.reverse
import leo.compileName
import leo.doName
import leo.doingName
import leo.functionLineTo
import leo.functionName
import leo.functionTo
import leo.giveName
import leo.isEmpty
import leo.letName
import leo.lineSeq
import leo.lineTo
import leo.literal
import leo.matchInfix
import leo.matchPrefix
import leo.quoteName
import leo.repeatingName
import leo.reverse
import leo.script
import leo.selectName
import leo.switchName
import leo.term.compiler.haskell.haskell
import leo.term.compiler.haskell.haskellEnvironment
import leo.term.compiler.idris.idris
import leo.term.compiler.idris.idrisEnvironment
import leo.term.compiler.js.js
import leo.term.compiler.js.jsEnvironment
import leo.term.compiler.julia.julia
import leo.term.compiler.julia.juliaEnvironment
import leo.term.compiler.leo.scriptEnvironment
import leo.term.compiler.python.python
import leo.term.compiler.python.pythonEnvironment
import leo.term.compiler.scheme.scheme
import leo.term.compiler.scheme.schemeEnvironment
import leo.term.fn
import leo.term.script
import leo.term.typed.TypedLine
import leo.term.typed.TypedTerm
import leo.term.typed.choicePlus
import leo.term.typed.do_
import leo.term.typed.lineTo
import leo.term.typed.plus
import leo.term.typed.repeat
import leo.term.typed.typed
import leo.term.typed.typedChoice
import leo.term.typed.typedTerm

data class Compiler<V>(
  val module: Module<V>,
  val typedTerm: TypedTerm<V>
)

val <V> Module<V>.compiler: Compiler<V> get() = Compiler(this, typedTerm())

fun <V> Compiler<V>.set(typedTerm: TypedTerm<V>): Compiler<V> =
  copy(typedTerm = typedTerm)

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
  plus(environment.typedLine(literal))

fun <V> Compiler<V>.plus(field: ScriptField): Compiler<V> =
  null
    ?: plusSpecialOrNull(field)
    ?: plusNamed(field)

fun <V> Compiler<V>.plusNamed(field: ScriptField): Compiler<V> =
  if (field.rhs.isEmpty) set(typedTerm()).plus(field.name lineTo typedTerm)
  else plus(field.name lineTo context.typedTerm(field.rhs))

fun <V> Compiler<V>.plusSpecialOrNull(field: ScriptField): Compiler<V>? =
  when (field.name) {
    compileName -> plusCompile(field.rhs)
    doName -> plusDo(field.rhs)
    giveName -> plusGive(field.rhs)
    functionName -> plusFunction(field.rhs)
    letName -> plusLet(field.rhs)
    selectName -> plusSelect(field.rhs)
    switchName -> plusSwitch(field.rhs)
    quoteName -> plusQuote(field.rhs)
    else -> null
  }

fun <V> Compiler<V>.plusCompile(script: Script): Compiler<V> =
  if (!typedTerm.t.isEmpty) error("compile non empty")
  else script.matchPrefix { name, rhs ->
    when (name) {
      "js" -> set(context.typedTerm(script("js" lineTo script(literal(jsEnvironment.typedTerm(rhs).v.js.string)))))
      "julia" -> set(context.typedTerm(script("julia" lineTo script(literal(juliaEnvironment.typedTerm(rhs).v.julia.string)))))
      "haskell" -> set(context.typedTerm(script("haskell" lineTo script(literal(haskellEnvironment.typedTerm(rhs).v.haskell.string)))))
      "idris" -> set(context.typedTerm(script("idris" lineTo script(literal(idrisEnvironment.typedTerm(rhs).v.idris.string)))))
      "scheme" -> set(context.typedTerm(script("scheme" lineTo script(literal(schemeEnvironment.typedTerm(rhs).v.scheme.string)))))
      "python" -> set(context.typedTerm(script("python" lineTo script(literal(pythonEnvironment.typedTerm(rhs).v.python.string)))))
      "lambda" -> set(context.typedTerm(script("lambda" lineTo scriptEnvironment.typedTerm(rhs).v.script)))
      else -> null
    }
  }.notNullOrError("compile $script")

fun <V> Compiler<V>.plusFunction(script: Script): Compiler<V> =
  script.matchInfix { lhs, name, rhs ->
    when (name) {
      doingName -> context.type(lhs).let { type ->
        context.plus(binding(given(type))).typedTerm(rhs).let { typedTerm ->
          plus(typed(fn(typedTerm.v), type functionLineTo typedTerm.t))
        }
      }
      else -> null
    }
  }.notNullOrError("parse error action")

fun <V> Compiler<V>.plusDo(script: Script): Compiler<V> =
  set(typedTerm.do_(context.plus(binding(given(typedTerm.t))).typedTerm(script)))

fun <V> Compiler<V>.plusGive(script: Script): Compiler<V> =
  script.matchInfix { lhs, name, rhs ->
    context.type(lhs).let { type ->
      when (name) {
        doingName ->
          set(
            typedTerm
              .do_(context.plus(binding(given(typedTerm.t))).typedTerm(rhs))
              .check(type))
        repeatingName ->
          set(
            typedTerm
              .repeat(
                context
                  .plus(binding(definition(typedTerm.t.functionTo(type))))
                  .plus(binding(given(typedTerm.t))).typedTerm(rhs))
            .check(type))
        else -> null
      }
    }
  }?: compileError("give" lineTo script)

fun <V> Compiler<V>.plusLet(script: Script): Compiler<V> =
  if (typedTerm != typedTerm<V>()) error("let after term")
  else set(module.plusLet(script))

fun <V> Compiler<V>.plusSelect(script: Script): Compiler<V> =
  if (typedTerm != typedTerm<V>()) compileError("select" lineTo script())
  else set(
    typedChoice<V>()
      .fold(script.lineSeq.reverse) { choicePlus(context.typedSelection(it)) }
      .typedTerm)

fun <V> Compiler<V>.plusSwitch(script: Script): Compiler<V> =
  typedTerm.switchTypedChoice.let { typedChoice ->
    set(SwitchCompiler(context, typedChoice.t.lineStack.reverse, typedChoice.v, null).plus(script).typedTerm)
  }

fun <V> Compiler<V>.plusQuote(script: Script): Compiler<V> =
  if (!typedTerm.t.isEmpty) error("$typedTerm not empty")
  else set(environment.staticTypedTerm(script))

fun <V> Compiler<V>.plus(typedLine: TypedLine<V>): Compiler<V> =
  set(context.resolve(typedTerm.plus(typedLine)))

val <V> Compiler<V>.compiledTypedTerm: TypedTerm<V>
  get() =
    typed(module.seal(typedTerm.v), typedTerm.t)