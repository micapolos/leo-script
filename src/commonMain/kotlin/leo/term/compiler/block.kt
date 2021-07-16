package leo.term.compiler

import leo.Rope
import leo.Script
import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.atom
import leo.choice
import leo.choiceOrNull
import leo.doName
import leo.doingName
import leo.fieldOrNull
import leo.fold
import leo.functionTo
import leo.givingName
import leo.line
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
import leo.stack
import leo.term.compiled.Compiled
import leo.term.compiled.CompiledFunction
import leo.term.compiled.as_
import leo.term.compiled.compiled
import leo.term.compiled.compiledLineStack
import leo.term.compiled.compiledSelect
import leo.term.compiled.drop
import leo.term.compiled.expression
import leo.term.compiled.fn
import leo.term.compiled.invoke
import leo.term.compiled.line
import leo.term.compiled.onlyCompiledLine
import leo.term.compiled.pick
import leo.term.compiler.native.Native
import leo.term.variable
import leo.type

data class Block<V>(
  val module: Module<V>,
  val paramStack: Stack<Compiled<V>>) {
  override fun toString() = toScriptLine.toString()
}

val <V> Module<V>.block get() = Block(this, paramStack = stack())

val <V> Block<V>.context get() = module.context

fun <V> Block<V>.plus(binding: Binding): Block<V> =
  copy(module = module.plus(binding))

fun <V> Block<V>.plus(compiled: Compiled<V>): Block<V> =
  copy(paramStack = paramStack.push(compiled))

fun <V> Block<V>.seal(compiled: Compiled<V>): Compiled<V> =
  compiled
    .fold(paramStack) { fn(it.type, this) } // Is it correct?
    .fold(paramStack.reverse) { invoke(it) }

fun <V> Block<V>.plusLet(script: Script): Block<V> =
  script.matchInfix { lhs, name, rhs ->
    when (name) {
      doName -> plusLetDo(lhs, rhs)
      repeatName -> plusLetRepeat(lhs, rhs)
      else -> null
    }
  }?:compileError(script("let" lineTo script))

fun <V> Block<V>.plusLetDo(lhs: Script, rhs: Script): Block<V> =
  module.type(lhs).let { lhsType ->
    module.compiled(rhs).let { rhsCompiled ->
      this
        .plus(binding(lhsType functionTo rhsCompiled.type))
        .plus(fn(lhsType, rhsCompiled))
    }
  }

fun <V> Block<V>.plusLetRepeat(lhs: Script, rhs: Script): Block<V> =
  rhs.matchInfix(doingName) { lhs, doingScript ->
    lhs.matchPrefix(givingName) { givingScript ->
      module.type(lhs).let { lhsType ->
        module.type(givingScript).let { rhsType ->
          module.compiled(doingScript).let { rhsCompiled ->
            this
              .plus(binding(lhsType functionTo rhsCompiled.as_(rhsType).type))
              .plus(fn(lhsType, rhsCompiled))
          }
        }
      }
    }
  } ?: compileError(script("let" lineTo lhs.plus("repeat" lineTo rhs)))

fun <V> Block<V>.let(compiledFunction: CompiledFunction<V>): Block<V> =
  this
    .plus(binding(compiledFunction.typeFunction))
    .plus(compiled(compiled(line(compiledFunction.function), line(atom(compiledFunction.typeFunction)))))

fun <V> Block<V>.bind(compiled: Compiled<V>): Block<V> =
  Block(
    module.bind(compiled.type),
    paramStack.fold(compiled.compiledLineStack.reverse) { push(compiled(it)) })

fun <V> Block<V>.bind(type: Type): Block<V> =
  copy(module = module.bind(type))

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
      fn(
        type(rope.current),
        compiledSelect<V>()
          .fold(rope.head) { drop(it) }
          .pick(compiled(expression<V>(variable(0)), type(rope.current)).onlyCompiledLine)
          .fold(rope.tail.reverse) { drop(it) }
          .compiled))

fun <V> Block<V>.updateTypesBlock(fn: (Block<Native>) -> Block<Native>) =
  copy(module = module.updateTypesBlock(fn))
