package leo.term.compiler

import leo.Rope
import leo.Script
import leo.Stack
import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.anyName
import leo.atom
import leo.choice
import leo.choiceOrNull
import leo.doName
import leo.fieldOrNull
import leo.fold
import leo.functionTo
import leo.line
import leo.lineTo
import leo.make
import leo.matchInfix
import leo.matchPrefix
import leo.onlyLineOrNull
import leo.push
import leo.reverse
import leo.ropeOrNull
import leo.script
import leo.stack
import leo.term.compiled.Compiled
import leo.term.compiled.CompiledFunction
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

data class Local<V>(
  val module: Module<V>,
  val compiledStack: Stack<Compiled<V>>) { override fun toString() = toScriptLine.toString() }

val <V> Module<V>.local get() = Local(this, stack())

val <V> Context<V>.local get() = module.local
val <V> Local<V>.context get() = module.context

fun <V> Local<V>.plus(binding: Binding): Local<V> =
  copy(module = module.plus(binding))

fun <V> Local<V>.plus(compiled: Compiled<V>): Local<V> =
  copy(compiledStack = compiledStack.push(compiled))

fun <V> Local<V>.seal(compiled: Compiled<V>): Compiled<V> =
  compiled
    .fold(compiledStack) { fn(it.type, this) } // Is it correct?
    .fold(compiledStack.reverse) { invoke(it) }

fun <V> Local<V>.plusLet(script: Script): Local<V> =
  script.matchInfix(doName) { lhs, rhs ->
    module.type(lhs).let { type ->
      module.bind(type).compiled(rhs).let { bodyCompiled ->
        this
          .plus(binding(type functionTo bodyCompiled.type))
          .plus(fn(type, bodyCompiled))
          .run {
            lhs.matchPrefix(anyName) {
              module.type(lhs).let { type ->
                plusCast(type)
              }
            }?: this
          }
      }
    }
  }?:compileError(
    script(
      "let" lineTo script,
      "is" lineTo script(
        "not" lineTo script(
          "matching" lineTo script(
            "let" lineTo script(
              "any" lineTo script("type"),
              "do" lineTo script("any" lineTo script("compiled"))))))))

fun <V> Local<V>.let(compiledFunction: CompiledFunction<V>): Local<V> =
  this
    .plus(binding(compiledFunction.typeFunction))
    .plus(compiled(compiled(line(compiledFunction.function), line(atom(compiledFunction.typeFunction)))))

fun <V> Local<V>.bind(compiled: Compiled<V>): Local<V> =
  Local(
    module.bind(compiled.type),
    compiledStack.fold(compiled.compiledLineStack.reverse) { push(compiled(it)) })

fun <V> Local<V>.plusCast(type: Type): Local<V> =
  plusCast(stack(), type)

fun <V> Local<V>.plusCast(nameStack: Stack<String>, type: Type): Local<V> =
  type.choiceOrNull
    ?.let { choice -> plusCast(nameStack, choice) }
    ?: type.onlyLineOrNull?.atom?.fieldOrNull?.let { typeField ->
      plusCast(nameStack.push(typeField.name), typeField.rhsType)
    }
    ?:this

fun <V> Local<V>.plusCast(nameStack: Stack<String>, choice: TypeChoice): Local<V> =
  choice.lineStack.reverse.ropeOrNull
    ?.let { caseRope -> fold(caseRope) { plusCast(nameStack, it) } }
    ?:this

fun <V> Local<V>.plusCast(nameStack: Stack<String>, rope: Rope<TypeLine>): Local<V> =
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

fun <V> Local<V>.updateTypeLocal(fn: (Local<Native>) -> Local<Native>) =
  copy(module = module.updateTypeLocal(fn))