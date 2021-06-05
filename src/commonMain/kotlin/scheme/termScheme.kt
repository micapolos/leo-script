package scheme

import leo.AbstractionTerm
import leo.ApplicationTerm
import leo.NativeTerm
import leo.Stateful
import leo.Term
import leo.TermAbstraction
import leo.TermApplication
import leo.TermVariable
import leo.VariableTerm
import leo.array
import leo.base.iterate
import leo.bind
import leo.flat
import leo.getStateful
import leo.map
import leo.push
import leo.ret
import leo.stack
import leo.updateStateful

private data class State(val depth: Int)
private typealias Task<T> = Stateful<State, T>

private val State.push get() = copy(depth = depth.inc())

val Term<Scheme>.scheme: Scheme get() = schemeTask.run(State(0)).value

private val Term<Scheme>.schemeTask: Task<Scheme> get() =
	when (this) {
		is NativeTerm -> value.string.scheme.ret()
		is AbstractionTerm -> abstraction.schemeTask
		is ApplicationTerm -> application.schemeTask
		is VariableTerm -> variable.schemeTask
	}

private val TermAbstraction<Scheme>.schemeTask: Task<Scheme> get() =
	pushVariablesSchemeTask(variableCount).bind { variablesScheme ->
		term.schemeTask.bind { termScheme ->
			"(lambda ${variablesScheme.string} ${termScheme.string})".scheme.ret()
		}
	}

private val TermApplication<Scheme>.schemeTask: Task<Scheme> get() =
	lhs.schemeTask.bind { lhsScheme ->
		rhsStack
			.map { schemeTask }
			.flat
			.bind { rhsSchemes ->
			"(${lhsScheme.string} ${rhsSchemes.flatScheme.string})".scheme.ret()
		}
	}

private val TermVariable.schemeTask: Task<Scheme> get() =
	getStateful<State>().map { state ->
		state.depth.minus(this.index).dec().variableScheme
	}

private val variableSchemeTask: Task<Scheme> get() =
	getStateful<State>().map { state ->
		state.depth.variableScheme
	}

private fun pushVariablesSchemeTask(count: Int): Task<Scheme> =
	stack<Unit>()
		.iterate(count) { push(Unit) }
		.map { variableSchemeTask.bind { variableScheme -> pushTask.map { variableScheme } } }
		.flat
		.map { it.map { string } }
		.map { it.array.joinToString(" ") }
		.map { "($it)" }
		.map { it.scheme }

private val pushTask: Task<Unit> get() =
	updateStateful { it.push }

private val Int.variableScheme: Scheme get() = "v${this}".scheme
