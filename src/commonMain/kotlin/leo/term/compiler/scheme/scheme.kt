package leo.term.compiler.scheme

import scheme.Scheme
import scheme.scheme

val idScheme: Scheme get() = "(lambda (x) x)".scheme
val trueScheme: Scheme get() = "(lambda (f0) (lambda (f1) (f0 ${idScheme.string})))".scheme
val falseScheme: Scheme get() = "(lambda (f0) (lambda (f1) (f1 ${idScheme.string})))".scheme
val Scheme.boolean: Scheme get() = "(if $string ${trueScheme.string} ${falseScheme.string})".scheme