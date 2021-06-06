package leo

import leo.base.fold

sealed class Text
data class StringText(val string: String): Text()
data class StackText(val stack: Stack<Text>): Text()

val String.text: Text get() = StringText(this)
val Stack<Text>.text: Text get() = StackText(this)

val Text.string: String get() =
	stack<String>().stringPush(this).reverse.array.joinToString("")

operator fun Text.plus(text: Text): Text =
	when (this) {
		is StackText -> StackText(stack.push(text))
		is StringText -> StackText(stack(this, text))
	}

fun text(vararg texts: Text): Text = stack(*texts).text
fun text(string: String, vararg strings: String): Text = string.text.fold(strings) { plus(it.text) }

fun Stack<String>.stringPush(text: Text): Stack<String> =
	when (text) {
		is StackText -> fold(text.stack) { stringPush(it) }
		is StringText -> push(text.string)
	}