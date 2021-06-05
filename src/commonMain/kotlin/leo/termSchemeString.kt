package leo

private data class State(val depth: Int)
private typealias Task<T> = Stateful<State, T>

private val State.push get() = copy(depth = depth.inc())

val Term<Scheme>.schemeString: String get() = schemeStringTask.run(State(0)).value

private val Term<Scheme>.schemeStringTask: Task<String> get() =
	when (this) {
		is NativeTerm -> value.string.ret()
		is AbstractionTerm -> abstraction.schemeStringTask
		is ApplicationTerm -> application.schemeStringTask
		is VariableTerm -> variable.schemeStringTask
	}

private val TermAbstraction<Scheme>.schemeStringTask: Task<String> get() =
	variableSchemeStringTask.bind { variableString ->
		pushTask.bind {
			term.schemeStringTask.bind { termString ->
				"(lambda $variableString $termString)".ret()
			}
		}
	}

private val TermApplication<Scheme>.schemeStringTask: Task<String> get() =
	lhs.schemeStringTask.bind { lhsString ->
		rhs.schemeStringTask.bind { rhsString ->
			"($lhsString $rhsString)".ret()
		}
	}

private val TermVariable.schemeStringTask: Task<String> get() =
	getStateful<State>().map { state ->
		state.depth.minus(this.index).dec().variableSchemeString
	}

private val variableSchemeStringTask: Task<String> get() =
	getStateful<State>().map { state ->
		state.depth.variableSchemeString
	}

private val pushTask: Task<Unit> get() =
	updateStateful { it.push }

private val Int.variableSchemeString: String get() = "v${this}"
