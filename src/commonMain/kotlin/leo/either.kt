package leo

sealed class Either<out First, out Second>

data class FirstEither<First, Second>(val first: First): Either<First, Second>()
data class SecondEither<First, Second>(val second: Second): Either<First, Second>()

fun <First, Second> First.firstEither(): Either<First, Second> = FirstEither(this)
fun <First, Second> Second.secondEither(): Either<First, Second> = SecondEither(this)
