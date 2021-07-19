package leo.typed.compiler

import leo.Script
import leo.StackLink
import leo.base.notNullOrError
import leo.linkOrNull
import leo.nameStackOrNull
import leo.stackLink

data class Get(val nameStackLink: StackLink<String>)

fun get(name: String, vararg names: String) = Get(stackLink(name, *names))

val Script.getOrNull: Get?
  get() =
    nameStackOrNull?.linkOrNull?.let(::Get)

val Script.get: Get
  get() =
    getOrNull.notNullOrError("$this.get")
