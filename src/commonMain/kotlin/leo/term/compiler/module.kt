package leo.term.compiler

import leo.Script
import leo.Stack
import leo.base.notNullOrError
import leo.fold
import leo.functionTo
import leo.giveName
import leo.matchInfix
import leo.push
import leo.reverse
import leo.stack
import leo.term.Term
import leo.term.fn
import leo.term.invoke

data class Module<V>(
	val context: Context<V>,
	val termStack: Stack<Term<V>>)

val <V> Context<V>.module get() = Module(this, stack())

fun <V> Module<V>.plus(binding: Binding): Module<V> =
	copy(context = context.plus(binding))

fun <V> Module<V>.plus(term: Term<V>): Module<V> =
	copy(termStack = termStack.push(term))

fun <V> Module<V>.seal(term: Term<V>): Term<V> =
	term.fold(termStack) { fn(this) }.fold(termStack.reverse) { invoke(it) }

fun <V> Module<V>.plusLet(script: Script): Module<V> =
	script.matchInfix(giveName) { lhs, rhs ->
		context.type(lhs).let { type ->
			context.plus(binding(given(type))).typedTerm(rhs).let { bodyTypedTerm ->
				this
					.plus(binding(definition(type functionTo bodyTypedTerm.t)))
					.plus(fn(bodyTypedTerm.v))
			}
		}
	}.notNullOrError("let $script")
