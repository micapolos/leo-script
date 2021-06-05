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
import leo.bind
import leo.getStateful
import leo.map
import leo.ret
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
	variableSchemeTask.bind { variableScheme ->
		pushTask.bind {
			term.schemeTask.bind { termScheme ->
				"(lambda ${variableScheme.string} ${termScheme.string})".scheme.ret()
			}
		}
	}

private val TermApplication<Scheme>.schemeTask: Task<Scheme> get() =
	lhs.schemeTask.bind { lhsScheme ->
		rhs.schemeTask.bind { rhsScheme ->
			"(${lhsScheme.string} ${rhsScheme.string})".scheme.ret()
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

private val pushTask: Task<Unit> get() =
	updateStateful { it.push }

private val Int.variableScheme: Scheme get() = "v${this}".scheme
