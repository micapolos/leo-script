package leo.interactive

data class Processor<State, Value>(val state: State, val plusFn: (Value) -> Processor<State, Value>)
fun <State, Input> State.processor(fn: (Input) -> Processor<State, Input>) = Processor(this, fn)

fun <State, Input> Processor<State, Input>.plus(input: Input): Processor<State, Input> = plusFn(input)

fun <FirstState, SecondState, Input> Processor<FirstState, Input>.mapState(fn: (FirstState) -> SecondState): Processor<SecondState, Input> =
	fn(state).processor { plusFn(it).mapState(fn) }
