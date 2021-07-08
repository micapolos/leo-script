package leo.base

fun appendableString(fn: (Appendable) -> Unit): String {
  val stringBuilder = StringBuilder()
  fn(stringBuilder)
  return stringBuilder.toString()
}

fun appendableIndentedString(fn: (AppendableIndented) -> Unit): String =
  appendableString { fn(it.indented) }

fun <V> V.string(fn: Appendable.(V) -> Unit): String {
  val stringBuilder = StringBuilder()
  stringBuilder.fn(this)
  return stringBuilder.toString()
}

fun Appendable.appendString(value: Any?): Appendable =
  append(value.string)

fun <R> R.fold(charSequence: CharSequence, fn: R.(Char) -> R): R =
  charSequence.fold(this, fn)

fun String.mapChars(fn: (Char) -> Char) =
  StringBuilder(length).fold(this) { append(fn(it)) }.toString()

fun String.charSeqAt(index: Int): Seq<Char> =
  Seq {
    notNullIf(index < length) {
      this[index] then charSeqAt(index.inc())
    }
  }

val String.charSeq
  get() =
    charSeqAt(0)

val Seq<Char>.charString
  get() =
    appendableString { fold(it, Appendable::append) }

val String.parenthesized get() = "($this)"

fun String.indentNewlines(indent: Int) =
  replace("\n", "\n" + indent.indentString)

val Int.indentString: String
  get() =
    "  ".repeat(this)

val bomString = "\uFEFF"

val String.titleCase: String
  get() =
    if (isEmpty()) this
    else substring(0..0).uppercase() + substring(1 until length)

fun lines(vararg strings: String): String = strings.joinToString("\n")