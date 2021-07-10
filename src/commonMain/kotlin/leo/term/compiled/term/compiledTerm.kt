package leo.term.compiled.term

import leo.atom
import leo.base.iterate
import leo.base.runIf
import leo.fold
import leo.line
import leo.primitive
import leo.reverse
import leo.term.Term
import leo.term.compiled.Append
import leo.term.compiled.AppendExpression
import leo.term.compiled.Apply
import leo.term.compiled.ApplyExpression
import leo.term.compiled.Body
import leo.term.compiled.Compiled
import leo.term.compiled.Content
import leo.term.compiled.ContentExpression
import leo.term.compiled.DropSelectLine
import leo.term.compiled.EmptyExpression
import leo.term.compiled.Expression
import leo.term.compiled.Field
import leo.term.compiled.Function
import leo.term.compiled.FunctionExpression
import leo.term.compiled.Get
import leo.term.compiled.GetExpression
import leo.term.compiled.NativeExpression
import leo.term.compiled.PickSelectLine
import leo.term.compiled.Select
import leo.term.compiled.SelectExpression
import leo.term.compiled.Switch
import leo.term.compiled.SwitchExpression
import leo.term.compiled.VariableExpression
import leo.term.fix
import leo.term.fn
import leo.term.invoke
import leo.term.nativeTerm
import leo.term.term
import leo.term.typed.drop
import leo.term.typed.headOrNull
import leo.term.typed.lineTo
import leo.term.typed.pick
import leo.term.typed.plus
import leo.term.typed.tailOrNull
import leo.term.typed.typed
import leo.term.typed.typedTerm
import leo.type

val <V> Compiled<V>.term: Term<V> get() =
  expression.term

val <V> Expression<V>.term: Term<V> get() =
  when (this) {
    is EmptyExpression -> empty.term()
    is AppendExpression -> append.term
    is ApplyExpression -> apply.term
    is ContentExpression -> content.term
    is FunctionExpression -> function.term
    is GetExpression -> get.term
    is NativeExpression -> native.nativeTerm
    is SelectExpression -> select.term
    is SwitchExpression -> switch.term
    is VariableExpression -> variable.term()
  }

val <V> Append<V>.term: Term<V> get() =
  typed(lhs.term, lhs.type).plus(field.name lineTo typed(field.rhs.term, field.rhs.type)).v

val <V> Apply<V>.term: Term<V> get() =
  lhs.term.invoke(rhs.term)

val <V> Content<V>.term: Term<V> get() =
  lhs.term

val <V> Function<V>.term: Term<V> get() =
  body.term

val <V> Body<V>.term: Term<V> get() =
  fn(compiled.term).runIf(isRecursive) { fix<V>().invoke(fn(this)) }

val <V> Get<V>.term: Term<V> get() =
  typed(lhs.term, lhs.type).iterate(index) { tailOrNull!! }.headOrNull!!.v

val <V> Select<V>.term: Term<V> get() =
  typedTerm<V>().fold(lineStack.reverse) { line ->
    when (line) {
      is DropSelectLine -> drop(type(line.drop.typeField.primitive.atom.line))
      is PickSelectLine -> pick(typed(line.pick.field.term, line.pick.field.rhs.type))
    }
  }.v

val <V> Switch<V>.term: Term<V> get() =
  TODO()

val <V> Field<V>.term: Term<V> get() =
  rhs.term
