package leo

fun <T> Typed.term(fn: (T) -> Term<T>): Term<T> = termTask<T>().get(State(fn))

private data class State<T>(val fn: (T) -> Term<T>)
private typealias Task<S, T> = Stateful<State<S>, T>

private fun <T> Typed.termTask(): Task<T, Term<T>> =
	when (type) {
		is ChoiceType -> expression.termTask(type.choice)
		is StructureType -> expression.termTask(type.structure)
	}

private fun <T> TypedExpression.termTask(structure: TypeStructure): Task<T, Term<T>> =
	if (structure.lineStack.isEmpty) idTerm<T>().ret()
	else structure.lineStack.onlyOrNull
		?.let { line -> termTask(line) }
		?: TODO()

private fun <T> TypedExpression.termTask(choice: TypeChoice): Task<T, Term<T>> =
	TODO()

private fun <T> TypedExpression.termTask(line: TypeLine): Task<T, Term<T>> =
	TODO()

private fun<T> Typed.plusTask(line: TypedLine): Task<T, Term<T>> =
	TODO()