package leo.named.compiler

import leo.Type
import leo.TypeField
import leo.TypeFunction

data class Module(
  val privateContext: Context,
  val publicContext: Context
)

val Context.module get() = Module(privateContext = this, publicContext = context())
fun module() = context().module
fun module(privateContext: Context, publicContext: Context) = Module(privateContext, publicContext)

fun Module.plus(definition: Definition) =
  Module(privateContext.plus(definition), publicContext.plus(definition))

fun Module.plus(dictionary: Dictionary) =
  Module(privateContext.plus(dictionary), publicContext.plus(dictionary))

fun Module.plus(type: Type) =
  Module(privateContext.plus(type), publicContext.plus(type))

fun Module.plusPrivate(definition: Definition) =
  Module(privateContext.plus(definition), publicContext)

fun Module.plusPrivate(context: Context) =
  Module(privateContext.plus(context), publicContext)

fun Module.define(typeField: TypeField): Module =
  plus(typeField.definition)

fun Module.define(function: TypeFunction): Module =
  plus(function.definition)
