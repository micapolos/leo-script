package leo.interactive

data class Processor<S, I>(val state: S, val fn: (I) -> Processor<S, I>)
fun <S, I> S.processor(fn: (I) -> Processor<S, I>) = Processor(this, fn)
fun <S, I> Processor<S, I>.process(input: I) = fn(input)
