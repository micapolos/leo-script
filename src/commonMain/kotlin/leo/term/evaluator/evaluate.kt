package leo.term.evaluator

import leo.Script
import leo.errorName
import leo.lineTo
import leo.named.compiler.CompileError
import leo.script
import leo.term.compiled.indexed.indexedExpression
import leo.term.compiler.compiled
import leo.term.compiler.native.nativeEnvironment
import leo.term.indexed.native.nativeEvaluator
import leo.term.indexed.native.script
import leo.term.indexed.value

val Script.evaluate: Script
  get() =
    try {
      compiled(nativeEnvironment).let { compiled ->
        compiled.indexedExpression.value(nativeEvaluator).script(compiled.type)
      }
    } catch (compileError: CompileError) {
      script(errorName lineTo compileError.scriptFn())
    }
