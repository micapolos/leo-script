package leo.named.compiler

import leo.Stack
import leo.push
import leo.stack

data class Scope(val bindingStack: Stack<Binding>)
fun scope(vararg bindings: Binding) = Scope(stack(*bindings))
fun Scope.plus(binding: Binding) = bindingStack.push(binding).let(::Scope)