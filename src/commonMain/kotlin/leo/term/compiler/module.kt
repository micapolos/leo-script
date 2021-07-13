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
import leo.term.compiled.drop
import leo.term.compiled.expression
import leo.term.compiled.fn
import leo.term.compiled.invoke
import leo.term.compiled.line
import leo.term.compiled.pick
import leo.term.variable
import leo.type

data class Module<V>(
  val context: Context<V>,
  val compiledStack: Stack<Compiled<V>>) { override fun toString() = toScriptLine.toString() }

val <V> Context<V>.module get() = Module(this, stack())

fun <V> Module<V>.plus(binding: Binding): Module<V> =
  copy(context = context.plus(binding))

fun <V> Module<V>.plus(compiled: Compiled<V>): Module<V> =
  copy(compiledStack = compiledStack.push(compiled))

fun <V> Module<V>.seal(compiled: Compiled<V>): Compiled<V> =
  compiled
    .fold(compiledStack) { fn(it.type, this) } // Is it correct?
    .fold(compiledStack.reverse) { invoke(it) }

fun <V> Module<V>.plusLet(script: Script): Module<V> =
  script.matchInfix(doName) { lhs, rhs ->
    context.type(lhs).let { type ->
      context.bind(type).compiled(rhs).let { bodyCompiled ->
        this
          .plus(binding(type functionTo bodyCompiled.type))
          .plus(fn(type, bodyCompiled))
          .run {
            lhs.matchPrefix(anyName) {
              context.type(lhs).let { type ->
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

fun <V> Module<V>.let(compiledFunction: CompiledFunction<V>): Module<V> =
  this
    .plus(binding(compiledFunction.typeFunction))
    .plus(compiled(compiled(line(compiledFunction.function), line(atom(compiledFunction.typeFunction)))))

fun <V> Module<V>.bind(compiled: Compiled<V>): Module<V> =
  Module(
    context.bind(compiled.type),
    compiledStack.fold(compiled.compiledLineStack.reverse) { push(compiled(it)) })

fun <V> Module<V>.plusCast(type: Type): Module<V> =
  plusCast(stack(), type)

fun <V> Module<V>.plusCast(nameStack: Stack<String>, type: Type): Module<V> =
  type.choiceOrNull
    ?.let { choice -> plusCast(nameStack, choice) }
    ?: type.onlyLineOrNull?.atom?.fieldOrNull?.let { typeField ->
      plusCast(nameStack.push(typeField.name), typeField.rhsType)
    }
    ?:this

fun <V> Module<V>.plusCast(nameStack: Stack<String>, choice: TypeChoice): Module<V> =
  choice.lineStack.reverse.ropeOrNull
    ?.let { caseRope -> fold(caseRope) { plusCast(nameStack, it) } }
    ?:this

fun <V> Module<V>.plusCast(nameStack: Stack<String>, rope: Rope<TypeLine>): Module<V> =
  this
    .plus(
      binding(
        type(rope.current).fold(nameStack) { make(it) } functionTo
            rope.stack.reverse.choice.type.fold(nameStack) { make(it) }))
    .plus(
      fn(
        type(rope.current),
        compiled<V>()
          .fold(rope.head) { drop(type(it)) }
          .pick(compiled(expression(variable(0)), type(rope.current)))
          .fold(rope.tail.reverse) { drop(type(it)) }))
