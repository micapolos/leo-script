package leo

private data class State(val depth: Int)
private typealias Task<T> = Stateful<State, T>

private val State.push get() = State(depth.inc())

val Term.schemeString: String get() = schemeStringTask.run(State(0)).value

private val Term.schemeStringTask: Task<String> get() =
	when (this) {
		is AbstractionTerm -> abstraction.schemeStringTask
		is ApplicationTerm -> application.schemeStringTask
		is LiteralTerm -> literal.schemeStringTask
		is VariableTerm -> variable.schemeStringTask
	}

private val TermAbstraction.schemeStringTask: Task<String> get() =
	variableSchemeStringTask.bind { variableString ->
		updateStateful<State> { it.push }.bind {
			term.schemeStringTask.bind { termString ->
				"(lambda $variableString $termString)".ret()
			}
		}
	}

private val TermApplication.schemeStringTask: Task<String> get() =
	lhs.schemeStringTask.bind { lhsString ->
		rhs.schemeStringTask.bind { rhsString ->
			"($lhsString $rhsString)".ret()
		}
	}

private val TermVariable.schemeStringTask: Task<String> get() =
	getStateful<State>().map { state ->
		state.depth.minus(this.index).dec().variableSchemeString
	}

private val Literal.schemeStringTask: Task<String> get() =
	toString().ret()

private val variableSchemeStringTask: Task<String> get() =
	getStateful<State>().map { state ->
		state.depth.variableSchemeString
	}

private val Int.variableSchemeString: String get() = "v${this}"
