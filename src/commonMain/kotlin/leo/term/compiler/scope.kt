package leo.term.compiler

import leo.Stack
import leo.base.mapFirstOrNull
import leo.base.mapIndexed
import leo.push
import leo.seq
import leo.stack
import leo.term.typed.TypedTerm
import leo.term.variable

data class Scope(val bindingStack: Stack<Binding>)

fun scope() = Scope(stack())

fun Scope.plus(binding: Binding): Scope =
	bindingStack.push(binding).let(::Scope)

fun <V> Scope.resolveOrNull(typedTerm: TypedTerm<V>): TypedTerm<V>? =
	bindingStack.seq.mapIndexed.mapFirstOrNull {
		value.resolveOrNull(variable(index), typedTerm)
	}

fun <V> Scope.getOrNull(name: String): TypedTerm<V>? =
	bindingStack.seq.mapIndexed.mapFirstOrNull {
		value.getOrNull(variable(index), name)
	}

fun <V> Scope.resolve(typedTerm: TypedTerm<V>): TypedTerm<V> =
	resolveOrNull(typedTerm) ?: typedTerm

