package leo.named.compiler

import leo.Type
import leo.named.typed.TypedExpression

data class Context(
	val dictionary: Dictionary,
	val types: Types)

fun context() = Context(dictionary(), types())

fun Context.plus(definition: Definition) =
	copy(dictionary = dictionary.plus(definition))

fun Context.plus(dictionary: Dictionary) =
	copy(dictionary = this.dictionary.plus(dictionary))

fun Context.plus(context: Context) =
	Context(dictionary.plus(context.dictionary), types.plus(context.types))

fun Context.plus(type: Type) =
	copy(types = types.plus(type))

fun Context.resolve(typedExpression: TypedExpression): TypedExpression =
	dictionary.resolve(types.cast(typedExpression))

