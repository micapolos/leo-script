package leo

data class Named<out V>(val lineStack: Stack<NamedLine<V>>) { override fun toString() = reflectScriptLine.toString() }

sealed class NamedLine<out V> { override fun toString() = reflectScriptLine.toString() }
data class AnyNamedLine<V>(val any: V): NamedLine<V>() { override fun toString() = super.toString() }
data class FieldNamedLine<V>(val field: NamedField<V>): NamedLine<V>() { override fun toString() = super.toString() }

data class NamedField<out V>(val name: String, val rhs: Named<V>)

fun <V> named(vararg lines: NamedLine<V>) = Named(stack(*lines))

fun <V> line(field: NamedField<V>): NamedLine<V> = FieldNamedLine(field)
fun <V> anyLine(any: V): NamedLine<V> = AnyNamedLine(any)

infix fun <V> String.fieldTo(rhs: Named<V>) = NamedField(this, rhs)
infix fun <V> String.lineTo(rhs: Named<V>) = NamedField(this, rhs)
