package leo.named.compiler

import leo.fold
import leo.reverse

data class Module(
	val privateDictionary: Dictionary,
	val publicDictionary: Dictionary)

val Dictionary.module get() =  Module(privateDictionary = this, publicDictionary = dictionary())
fun module() = dictionary().module

fun Module.plus(definition: Definition) =
	Module(privateDictionary.plus(definition), publicDictionary.plus(definition))

fun Module.plus(dictionary: Dictionary) =
	Module(privateDictionary.plus(dictionary), publicDictionary.plus(dictionary))

fun Module.plusPrivate(definition: Definition) =
	Module(privateDictionary.plus(definition), publicDictionary)

fun Module.plusPrivate(dictionary: Dictionary) =
	fold(dictionary.definitionStack.reverse) { plusPrivate(it) }
