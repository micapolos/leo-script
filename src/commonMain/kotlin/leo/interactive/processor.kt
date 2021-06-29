package leo.interactive

data class Processor<S, I>(val state: S, val process: (I) -> Processor<S, I>)
fun <S, I> S.processor(fn: (I) -> Processor<S, I>) = Processor(this, fn)

fun <S1, S2, I> Processor<S1, I>.mapState(fn: (S1) -> S2): Processor<S2, I> =
	fn(state).processor { process(it).mapState(fn) }
