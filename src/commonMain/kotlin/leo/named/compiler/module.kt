package leo.named.compiler

data class Module(
	val privateDictionary: Dictionary,
	val publicDictionary: Dictionary)

fun module(dictionary: Dictionary) = Module(privateDictionary = dictionary, publicDictionary = dictionary())
fun module() = module(dictionary())

fun Module.plus(definition: Definition) =
	Module(privateDictionary.plus(definition), publicDictionary.plus(definition))

fun Module.plusPrivate(definition: Definition) =
	Module(privateDictionary.plus(definition), publicDictionary)
