package leo.term.compiled.term

import leo.base.iterate
import leo.base.runIf
import leo.choiceOrNull
import leo.empty
import leo.fold
import leo.isStatic
import leo.reverse
import leo.size
import leo.term.Term
import leo.term.compiled.Append
import leo.term.compiled.AppendExpression
import leo.term.compiled.Apply
import leo.term.compiled.ApplyExpression
import leo.term.compiled.Body
import leo.term.compiled.Compiled
import leo.term.compiled.Content
import leo.term.compiled.ContentExpression
import leo.term.compiled.EmptyExpression
import leo.term.compiled.Field
import leo.term.compiled.Function
import leo.term.compiled.FunctionExpression
import leo.term.compiled.Get
import leo.term.compiled.GetExpression
import leo.term.compiled.NativeExpression
import leo.term.compiled.Select
import leo.term.compiled.SelectExpression
import leo.term.compiled.Switch
import leo.term.compiled.SwitchExpression
import leo.term.compiled.VariableExpression
import leo.term.fix
import leo.term.fn
import leo.term.get
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.plus
import leo.term.term

val <V> Compiled<V>.term: Term<V> get() =
  when (expression) {
    is EmptyExpression -> expression.empty.term()
    is AppendExpression -> expression.append.term
    is ApplyExpression -> expression.apply.term
    is ContentExpression -> expression.content.term
    is FunctionExpression -> expression.function.term
    is GetExpression -> expression.get.term
    is NativeExpression -> expression.native.nativeTerm
    is SelectExpression -> expression.select.term(type.choiceOrNull!!.lineStack.size)
    is SwitchExpression -> expression.switch.term
    is VariableExpression -> expression.variable.term()
  }

val <V> Append<V>.term: Term<V> get() =
  if (lhs.type.isStatic)
    if (field.rhs.type.isStatic) empty.term()
    else field.term
  else
    if (field.rhs.type.isStatic) lhs.term
    else lhs.term.plus(field.rhs.term)

val <V> Apply<V>.term: Term<V> get() =
  lhs.term.invoke(rhs.term)

val <V> Content<V>.term: Term<V> get() =
  lhs.term

val <V> Function<V>.term: Term<V> get() =
  body.term

val <V> Body<V>.term: Term<V> get() =
  fn(compiled.term).runIf(isRecursive) { fix<V>().invoke(fn(this)) }

val <V> Get<V>.term: Term<V> get() =
  TODO()

fun <V> Select<V>.term(size: Int): Term<V> =
  fn(get<V>(size).invoke(get(index))).iterate(size) { fn(this) }

val <V> Switch<V>.term: Term<V> get() =
  lhs.term.fold(fieldStack.reverse) { invoke(fn(it.term)) }

val <V> Field<V>.term: Term<V> get() =
  rhs.term
