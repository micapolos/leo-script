package leo.typed.compiler

import leo.Rope
import leo.Script
import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.Types
import leo.atom
import leo.beName
import leo.choice
import leo.choiceOrNull
import leo.doName
import leo.doingName
import leo.fieldOrNull
import leo.fold
import leo.functionTo
import leo.givingName
import leo.haveName
import leo.lineCount
import leo.lineTo
import leo.make
import leo.matchInfix
import leo.matchPrefix
import leo.onlyLineOrNull
import leo.plus
import leo.push
import leo.repeatName
import leo.reverse
import leo.ropeOrNull
import leo.script
import leo.setName
import leo.stack
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiled.CompiledChoice
import leo.typed.compiled.CompiledField
import leo.typed.compiled.CompiledLine
import leo.typed.compiled.LinkExpression
import leo.typed.compiled.as_
import leo.typed.compiled.compiled
import leo.typed.compiled.compiledSelect
import leo.typed.compiled.compiledVariable
import leo.typed.compiled.fn
import leo.typed.compiled.have
import leo.typed.compiled.invoke
import leo.typed.compiled.not
import leo.typed.compiled.onlyCompiledFieldOrNull
import leo.typed.compiled.onlyCompiledLine
import leo.typed.compiled.recFn
import leo.typed.compiled.rhs
import leo.typed.compiled.the

data class Block<V>(
  val module: Module<V>,
  val compiledStack: Stack<Compiled<V>>) {
  override fun toString() = toScriptLine.toString()
}

val <V> Module<V>.block get() = Block(this, compiledStack = stack())

val <V> Block<V>.context get() = module.context

fun <V> Block<V>.plus(binding: Binding): Block<V> =
  copy(module = module.plus(binding))

fun <V> Block<V>.plusGiven(compiled: Compiled<V>): Block<V> =
  when (compiled.expression) {
    is LinkExpression ->
      this
        .plusGiven(compiled.expression.link.lhsCompiled)
        .plusGiven(compiled.expression.link.rhsCompiledLine)
    else ->
      when (compiled.type.lineCount) {
        0 -> this
        1 -> plusGiven(compiled.onlyCompiledLine)
        else -> compileError(script("given"))
      }

  }

fun <V> Block<V>.plusGiven(compiledLine: CompiledLine<V>): Block<V> =
  this
    .plus(binding(given(compiledLine.typeLine)))
    .plusParam(compiled(compiledLine))

fun <V> Block<V>.plusParam(compiled: Compiled<V>): Block<V> =
  copy(compiledStack = compiledStack.push(compiled))

fun <V> Block<V>.seal(compiled: Compiled<V>): Compiled<V> =
  compiled.fold(compiledStack) { paramCompiled ->
    fn(paramCompiled.type, this).invoke(paramCompiled)
  }

fun <V> Block<V>.plusLet(script: Script): Block<V> =
  script.matchInfix { lhs, name, rhs ->
    when (name) {
      beName -> plusLetBe(lhs, rhs)
      doName -> plusLetDo(lhs, rhs)
      haveName -> plusLetHave(lhs, rhs)
      repeatName -> plusLetRepeat(lhs, rhs)
      else -> null
    }
  }?:compileError(script("let" lineTo script))

fun <V> Block<V>.plusLetBe(lhs: Script, rhs: Script): Block<V> =
  module.type(lhs).let { lhsType ->
    module.compiled(rhs).let { rhsCompiled ->
      this
        .plus(binding(constant(lhsType, rhsCompiled.type)))
        .plusParam(rhsCompiled)
    }
  }

fun <V> Block<V>.plusLetDo(lhs: Script, rhs: Script): Block<V> =
  module.type(lhs).let { lhsType ->
    module
      .plus(given(lhsType))
      .compiled(rhs)
      .let { rhsCompiled ->
        this
          .plus(binding(lhsType functionTo rhsCompiled.type))
          .plusParam(fn(lhsType, rhsCompiled))
      }
  }

fun <V> Block<V>.plusLetHave(lhs: Script, rhs: Script): Block<V> =
  module.type(lhs).let { lhsType ->
    module.compiled(rhs).let { rhsCompiled ->
      lhsType.have(rhsCompiled).let { haveCompiled ->
        this
          .plus(binding(constant(lhsType, haveCompiled.type)))
          .plusParam(haveCompiled)
      }
    }
  }

fun <V> Block<V>.plusLetRepeat(repeatLhs: Script, repeatRhs: Script): Block<V> =
  repeatRhs.matchInfix(doingName) { doingLhs, doingRhs ->
    doingLhs.matchPrefix(givingName) { givingRhs ->
      module.type(repeatLhs).let { lhsType ->
        module.type(givingRhs).let { rhsType ->
          module
            .plus(binding(lhsType functionTo rhsType))
            .plus(given(lhsType))
            .compiled(doingRhs)
            .let { rhsCompiled ->
              this
                .plus(binding(lhsType functionTo rhsCompiled.as_(rhsType).type))
                .plusParam(recFn(lhsType, rhsCompiled))
          }
        }
      }
    }
  } ?: compileError(script("let" lineTo repeatLhs.plus("repeat" lineTo repeatRhs)))

fun <V> Block<V>.plusCast(type: Type): Block<V> =
  plusCast(stack(), type)

fun <V> Block<V>.plusCast(nameStack: Stack<String>, type: Type): Block<V> =
  type.choiceOrNull
    ?.let { choice -> plusCast(nameStack, choice) }
    ?: type.onlyLineOrNull?.atom?.fieldOrNull?.let { typeField ->
      plusCast(nameStack.push(typeField.name), typeField.rhsType)
    }
    ?:this

fun <V> Block<V>.plusCast(nameStack: Stack<String>, choice: TypeChoice): Block<V> =
  choice.lineStack.reverse.ropeOrNull
    ?.let { caseRope -> fold(caseRope) { plusCast(nameStack, it) } }
    ?:this

fun <V> Block<V>.plusCast(nameStack: Stack<String>, rope: Rope<TypeLine>): Block<V> =
  this
    .plus(
      binding(
        type(rope.current).fold(nameStack) { make(it) } functionTo
            rope.stack.reverse.choice.type.fold(nameStack) { make(it) }))
    .plusParam(
      fn(
        type(rope.current),
        compiledSelect<V>()
          .fold(rope.head) { not(it) }
          .the(compiledVariable<V>(0, type(rope.current)).onlyCompiledLine)
          .fold(rope.tail.reverse) { not(it) }
          .compiled))

fun <V> Block<V>.updateTypesBlock(fn: (Block<Types>) -> Block<Types>) =
  copy(module = module.updateTypesBlock(fn))

fun <V> Block<V>.compiledChoice(): CompiledChoice<V> =
  context.compiledChoice()

fun <V> Block<V>.resolveOrNull(compiled: Compiled<V>): Block<V>? =
  compiled.onlyCompiledFieldOrNull?.let { resolveOrNull(it) }

fun <V> Block<V>.resolveOrNull(compiledField: CompiledField<V>): Block<V>? =
  when (compiledField.typeField.name) {
    setName -> plusGiven(compiledField.rhs)
    else -> null
  }
