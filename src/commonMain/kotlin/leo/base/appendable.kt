package leo.base

fun Appendable.tryAppend(tryFn: Appendable.() -> Appendable?): Appendable? =
  StringBuilder().let { stringBuilder ->
    stringBuilder.tryFn().let { triedAppendable ->
      if (triedAppendable == null) null
      else append(stringBuilder.toString())
    }
  }


fun string(fn: Appendable.() -> Unit) {
  StringBuilder().apply { fn() }.toString()
}

fun Appendable.appendParenthesized(fn: Appendable.() -> Appendable) =
  append('(').fn().append(')')

fun Appendable.appendSquareParenthesized(fn: Appendable.() -> Appendable) =
  append('[').fn().append(']')
