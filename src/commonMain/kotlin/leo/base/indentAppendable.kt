package leo.base

data class AppendableIndented(val appendable: Appendable, val indent: Int)

fun Appendable.indented(indent: Int) = AppendableIndented(this, indent)
val Appendable.indented get() = indented(0)
fun AppendableIndented.append(string: String) =
  appendable.append(string.indentNewlines(indent)).indented(indent)

fun AppendableIndented.append(char: Char) = append(char.toString())

val AppendableIndented.indented: AppendableIndented
  get() =
    appendable.indented(indent.inc())

fun AppendableIndented.indented(fn: AppendableIndented.() -> AppendableIndented): AppendableIndented =
  appendable.indented(indent.inc()).fn().appendable.indented(indent)
