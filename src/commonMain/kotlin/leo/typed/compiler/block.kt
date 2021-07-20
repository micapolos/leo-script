package leo.typed.compiler

import leo.Rope
import leo.Script
import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeLine
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
import leo.lineTo
import leo.make
import leo.matchInfix
import leo.matchPrefix
import leo.nameOrNull
import leo.onlyLineOrNull
import leo.plus
import leo.push
import leo.repeatName
import leo.reverse
import leo.ropeOrNull
import leo.script
import leo.stack
import leo.type
import leo.typed.compiled.Compiled
import leo.typed.compiled.as_
import leo.typed.compiled.bind
import leo.typed.compiled.binding
import leo.typed.compiled.compiled
import leo.typed.compiled.compiledLineStack
import leo.typed.compiled.compiledSelect
import leo.typed.compiled.compiledVariable
import leo.typed.compiled.expression
import leo.typed.compiled.fn
import leo.typed.compiled.not
import leo.typed.compiled.onlyCompiledLine
import leo.typed.compiled.recFn
import leo.typed.compiled.the
import leo.typed.compiler.native.Native

data class Block<V>(
  val module: Module<V>,
  val bindingStack: Stack<leo.typed.compiled.Binding<V>>) {
  override fun toString() = toScriptLine.toString()
}

val <V> Module<V>.block get() = Block(this, bindingStack = stack())

val <V> Block<V>.context get() = module.context

fun <V> Block<V>.plus(binding: Binding): Block<V> =
  copy(module = module.plus(binding))

fun <V> Block<V>.plus(binding: leo.typed.compiled.Binding<V>): Block<V> =
  copy(bindingStack = bindingStack.push(binding))

fun <V> Block<V>.seal(compiled: Compiled<V>): Compiled<V> =
  // TODO: Correct order?
  compiled.fold(bindingStack.reverse) {
    compiled(expression(bind(it, this)), type)
  }

fun <V> Block<V>.plusLet(script: Script): Block<V> =
  script.matchInfix { lhs, name, rhs ->
    when (name) {
      beName -> plusLetBe(lhs, rhs)
      doName -> plusLetDo(lhs, rhs)
      repeatName -> plusLetRepeat(lhs, rhs)
      else -> null
    }
  }?:compileError(script("let" lineTo script))

fun <V> Block<V>.plusLetBe(lhs: Script, rhs: Script): Block<V> =
  module.type(lhs).let { lhsType ->
    module.compiled(rhs).let { rhsCompiled ->
      this
        .plus(binding(constant(lhsType, rhsCompiled.type)))
        .plus(binding(lhsType, rhsCompiled))
    }
  }

fun <V> Block<V>.plusLetDo(lhs: Script, rhs: Script): Block<V> =
  module.type(lhs).let { lhsType ->
    module.plus(binding(given(lhsType))).compiled(rhs).let { rhsCompiled ->
      this
        .plus(binding(lhsType functionTo rhsCompiled.type))
        .plus(binding(lhsType, fn(lhsType, rhsCompiled)))
    }
  }

fun <V> Block<V>.plusLetRepeat(repeatLhs: Script, repeatRhs: Script): Block<V> =
  repeatRhs.matchInfix(doingName) { doingLhs, doingRhs ->
    doingLhs.matchPrefix(givingName) { givingRhs ->
      module.type(repeatLhs).let { lhsType ->
        module.type(givingRhs).let { rhsType ->
          module
            .plus(binding(lhsType functionTo rhsType))
            .plus(binding(given(lhsType)))
            .compiled(doingRhs)
            .let { rhsCompiled ->
              this
                .plus(binding(lhsType functionTo rhsCompiled.as_(rhsType).type))
                .plus(binding(lhsType, recFn(lhsType, rhsCompiled)))
          }
        }
      }
    }
  } ?: compileError(script("let" lineTo repeatLhs.plus("repeat" lineTo repeatRhs)))

fun <V> Block<V>.bind(compiled: Compiled<V>): Block<V> =
  Block(
    module.plus(binding(given(compiled.type))),
    bindingStack.fold(compiled.compiledLineStack.reverse) {
      push(binding(type(it.typeLine.nameOrNull!!), compiled(it)))
    })

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
    .plus(
      binding(
        type(rope.current),
        fn(
          type(rope.current),
          compiledSelect<V>()
            .fold(rope.head) { not(it) }
            .the(compiledVariable<V>(type(rope.current.nameOrNull!!), type(rope.current)).onlyCompiledLine)
            .fold(rope.tail.reverse) { not(it) }
            .compiled)))

fun <V> Block<V>.updateTypesBlock(fn: (Block<Native>) -> Block<Native>) =
  copy(module = module.updateTypesBlock(fn))
