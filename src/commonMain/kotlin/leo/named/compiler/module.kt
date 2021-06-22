package leo.named.compiler

data class Module(
	val privateContext: Context,
	val publicContext: Context)

val Context.module get() =  Module(privateContext = this, publicContext = context())
fun module() = context().module

fun Module.plus(definition: Definition) =
	Module(privateContext.plus(definition), publicContext.plus(definition))

fun Module.plus(dictionary: Dictionary) =
	Module(privateContext.plus(dictionary), publicContext.plus(dictionary))

fun Module.plusPrivate(definition: Definition) =
	Module(privateContext.plus(definition), publicContext)

fun Module.plusPrivate(context: Context) =
	Module(privateContext.plus(context), publicContext)
