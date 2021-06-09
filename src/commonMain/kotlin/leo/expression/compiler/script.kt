package leo.expression.compiler

import leo.Script
import leo.expression.Structure

val Script.structure: Structure get() = context().structure(this)
val Script.context: Context get() = context().context(this)