package leo.term.compiler.native

import leo.term.Term
import leo.term.fn
import leo.term.invoke
import leo.term.nativeTerm

fun Term<Native>.objectEqualsObject(term: Term<Native>): Term<Native> =
	fn(fn(ObjectEqualsObjectNative.nativeTerm)).invoke(this).invoke(term)