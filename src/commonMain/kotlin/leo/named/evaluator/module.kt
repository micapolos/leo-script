package leo.named.evaluator

import leo.base.fold
import leo.named.value.Value

data class Module(
	val private: DictionaryPrivate,
	val public: DictionaryPublic) { override fun toString() = scriptLine.toString() }

data class DictionaryPrivate(val dictionary: Dictionary) { override fun toString() = scriptLine.toString() }
data class DictionaryPublic(val dictionary: Dictionary) { override fun toString() = scriptLine.toString() }

fun module(private: DictionaryPrivate, public: DictionaryPublic) =
	Module(private, public)

val Dictionary.private get() = DictionaryPrivate(this)
val Dictionary.public get() = DictionaryPublic(this)

fun private(dictionary: Dictionary) = dictionary.private
fun public(dictionary: Dictionary) = dictionary.public

val DictionaryPrivate.module get() = module(this, dictionary().public)

fun DictionaryPrivate.plus(definition: Definition) = dictionary.plus(definition).private
fun DictionaryPublic.plus(definition: Definition) = dictionary.plus(definition).public

fun Module.plusPrivate(dictionary: Dictionary): Module =
	copy(private = private.fold(dictionary.definitionSeq) { plus(it) })

fun Module.plus(definition: Definition): Module =
	module(private.plus(definition), public.plus(definition))

fun Module.plus(dictionary: Dictionary): Module =
	fold(dictionary.definitionSeq) { plus(it) }

fun Module.bind(value: Value): Module =
	plus(value.linesDictionary)

fun Module.plusRecursive(dictionary: Dictionary): Module =
	module(
		private.dictionary.plusRecursive(dictionary).private,
		public.dictionary.plusRecursive(dictionary).public)
