package leo

fun Value.have(rhs: Value): Value =
  haveOrNull(rhs) ?: plus(haveName fieldTo rhs)

fun Value.haveOrNull(rhs: Value): Value? =
  when (this) {
    EmptyValue -> rhs
    is LinkValue -> link.haveOrNull(rhs)
  }

fun Link.haveOrNull(rhs: Value): Value? =
  field.haveOrNull(rhs)?.let { value.plus(it) }

fun Field.haveOrNull(rhs: Value): Field? =
  this.rhs.haveOrNull(rhs)?.let { name fieldTo it }

fun Rhs.haveOrNull(rhs: Value): Rhs? =
  when (this) {
    is FunctionRhs -> null
    is NativeRhs -> null
    is ValueRhs -> value.haveOrNull(rhs)?.let(::rhs)
  }