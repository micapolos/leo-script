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
import leo.term.Term
import leo.term.fn
import leo.term.get
import leo.term.invoke
import leo.term.typed.drop
import leo.term.typed.pick
import leo.term.typed.typed
import leo.term.typed.typedTerm
import leo.type

data class Module<V>(
  val context: Context<V>,
  val termStack: Stack<Term<V>>) { override fun toString() = toScriptLine.toString() }

val <V> Context<V>.module get() = Module(this, stack())

fun <V> Module<V>.plus(binding: Binding): Module<V> =
  copy(context = context.plus(binding))

fun <V> Module<V>.plus(term: Term<V>): Module<V> =
  copy(termStack = termStack.push(term))

fun <V> Module<V>.seal(term: Term<V>): Term<V> =
  term.fold(termStack) { fn(this) }.fold(termStack.reverse) { invoke(it) }

fun <V> Module<V>.plusLet(script: Script): Module<V> =
  script.matchInfix(doName) { lhs, rhs ->
    context.type(lhs).let { type ->
      context.plus(binding(given(type))).typedTerm(rhs).let { bodyTypedTerm ->
        this
          .plus(binding(definition(type functionTo bodyTypedTerm.t)))
          .plus(fn(bodyTypedTerm.v))
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
        definition(
          type(rope.current).fold(nameStack) { make(it) } functionTo
              rope.stack.reverse.choice.type.fold(nameStack) { make(it) })))
    .plus(
      fn(
        typedTerm<V>()
          .fold(rope.head) { drop(type(it)) }
          .pick(typed(get(0), type(rope.current)))
          .fold(rope.tail.reverse) { drop(type(it)) }
          .v))
