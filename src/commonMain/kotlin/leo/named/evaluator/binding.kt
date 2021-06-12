package leo.named.evaluator

import leo.named.Expression

sealed class Binding<out T>
data class ValueBinding<T>(val value: Value<T>): Binding<T>()
data class ExpressionBinding<T>(val expression: Expression<T>): Binding<T>()

fun <T> binding(value: Value<T>): Binding<T> = ValueBinding(value)
fun <T> binding(expression: Expression<T>): Binding<T> = ExpressionBinding(expression)