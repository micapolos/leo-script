package leo.term.compiled.term

import leo.Empty
import leo.fold
import leo.get
import leo.reverse
import leo.term.Term
import leo.term.compiled.Compiled
import leo.term.compiled.EmptyExpression
import leo.term.compiled.Expression
import leo.term.compiled.Function
import leo.term.compiled.FunctionExpression
import leo.term.compiled.Invoke
import leo.term.compiled.InvokeExpression
import leo.term.compiled.NativeExpression
import leo.term.compiled.Tuple
import leo.term.compiled.TupleAt
import leo.term.compiled.TupleAtExpression
import leo.term.compiled.VariableExpression
import leo.term.fn
import leo.term.id
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.term
import leo.term.typed.TypedLine
import leo.term.typed.plus
import leo.term.typed.typed
import leo.term.typed.typedTerm

val <T> Compiled<T>.typedLine: TypedLine<T> get() =
  typed(expression.term, typeLine)

val <T> Compiled<T>.term: Term<T> get() =
  expression.term

val <T> Expression<T>.term: Term<T> get() =
  when (this) {
    is EmptyExpression -> empty.term()
    is FunctionExpression -> function.term
    is InvokeExpression -> invoke.term
    is NativeExpression -> native.nativeTerm
    is TupleAtExpression -> tupleAt.term
    is VariableExpression -> term(variable)
  }

fun <T> Empty.term(): Term<T> = id()

val <T> Function<T>.term: Term<T> get() =
  bodyTuple.term.fold(paramTypeLineStack.reverse) { fn(this) }

val <T> Invoke<T>.term: Term<T> get() =
  functionCompiled.term.fold(paramTuple.compiledStack.reverse) { invoke(it.term) }

val <T> TupleAt<T>.term: Term<T> get() =
  tuple.compiledStack.get(index)!!.term

val <T> Tuple<T>.term: Term<T> get() =
  typedTerm<T>().fold(compiledStack.reverse) { plus(typed(it.term,  it.typeLine)) }.v
