package leo.term.compiled

import leo.Stack
import leo.Type
import leo.TypeLine
import leo.applyName
import leo.array
import leo.base.fold
import leo.base.ifOrNull
import leo.base.mapIndexed
import leo.base.notNullIf
import leo.base.reverse
import leo.base.stack
import leo.functionLineTo
import leo.functionOrNull
import leo.getFromBottom
import leo.isEmpty
import leo.lineSeq
import leo.lineTo
import leo.linkOrNull
import leo.mapIt
import leo.numberTypeLine
import leo.onlyLineOrNull
import leo.onlyOrNull
import leo.plus
import leo.push
import leo.script
import leo.size
import leo.structure
import leo.structureOrNull
import leo.term.compiler.compileError
import leo.textTypeLine
import leo.type
import leo.typeStructure
import leo.zip

fun <V> compiled(vararg lines: CompiledLine<V>): Compiled<V> =
  Compiled(expression(tuple<V>()), type()).fold(lines) { plus(it) }

fun <V> compiledTuple(vararg lines: CompiledLine<V>): CompiledTuple<V> =
  compiled(tuple<V>(), typeStructure()).fold(lines) { plus(it) }

fun <V> compiled(name: String): Compiled<V> =
  compiled(name lineTo compiled())

fun <V> nativeCompiled(native: V, type: Type): Compiled<V> =
  compiled(expression(tuple(nativeLine(native))), type)

val <V> Compiled<V>.compiledTuple: CompiledTuple<V> get() =
  compiledTupleOrNull?:compileError(type.script.plus("is" lineTo script("not" lineTo script("tuple"))))

fun <V> CompiledTuple<V>.plus(compiledLine: CompiledLine<V>): CompiledTuple<V> =
  compiled(Tuple(tuple.lineStack.push(compiledLine.line)), typeStructure.plus(compiledLine.typeLine))

val <V> CompiledTuple<V>.compiled: Compiled<V> get() =
  compiled(expression(tuple), type(typeStructure))

fun <V> Compiled<V>.plus(compiledLine: CompiledLine<V>): Compiled<V> =
  compiledTuple.plus(compiledLine).compiled

fun <V> Compiled<V>.apply(compiled: Compiled<V>): Compiled<V> =
  compiled.type.functionOrNull
    ?.let { function ->
      notNullIf(type == function.lhsType) {
        compiled(
          expression(apply(this, compiled)),
          function.rhsType)
      }
    }
    ?: compileError(compiled.type.script.plus(applyName lineTo compiled.type.script))

fun <V> Compiled<V>.invoke(compiled: Compiled<V>): Compiled<V> =
  compiled.apply(this)

fun <V> Compiled<V>.do_(body: Body<V>): Compiled<V> =
  apply(fn(type, body))

fun <V> Compiled<V>.as_(type: Type): Compiled<V> =
  also {
    if (this.type != type) compileError(
      this.type.script.plus(
        "is" lineTo script(
          "not" lineTo script(
            "equal" lineTo type.script))))
  }

fun <V> fn(type: Type, body: Body<V>): Compiled<V> =
  compiled(fnLine(type, body))

fun <V> fn(type: Type, body: Compiled<V>): Compiled<V> =
  compiled(fnLine(type, body))

fun <V> fnLine(type: Type, body: Body<V>): CompiledLine<V> =
  compiled(line(function(type, body)), type functionLineTo body.compiled.type)

fun <V> fnLine(type: Type, body: Compiled<V>): CompiledLine<V> =
  compiled(line(function(type, body(body))), type functionLineTo body.type)

fun <V> Compiled<V>.pick(compiledLine: CompiledLine<V>): Compiled<V> = TODO()
fun <V> Compiled<V>.drop(typeLine: TypeLine): Compiled<V> = TODO()

fun <V> Compiled<V>.pick(compiled: Compiled<V>): Compiled<V> = TODO()
fun <V> Compiled<V>.drop(type: Type): Compiled<V> = TODO()
val <V> Compiled<V>.content: Compiled<V> get() = TODO()

val <V> Compiled<V>.tupleContentOrNull: Compiled<V>? get() =
  tupleOnlyLineOrNull?.fieldRhsOrNull

val <V> CompiledLine<V>.fieldRhsOrNull: Compiled<V>? get() =
  line.fieldOrNull?.rhs

fun <V> Compiled<V>.tupleLineOrNull(name: String): CompiledLine<V>? =
  expression.tupleOrNull?.let { tuple ->
    type.indexedLineOrNull(name)?.let { indexedLine ->
      compiled(tuple.lineStack.getFromBottom(indexedLine.index)!!, indexedLine.value)
    }
  }

fun <V> Compiled<V>.tupleGetLineOrNull(name: String): CompiledLine<V>? =
  tupleContentOrNull?.tupleLineOrNull(name)

fun <V> Compiled<V>.resolvedGetLineOrNull(name: String): CompiledLine<V>? =
  type.contentOrNull?.indexedLineOrNull(name)?.let { indexedLine ->
    compiled(line(get(this, indexedLine.index)), indexedLine.value)
  }

fun <V> Compiled<V>.getLineOrNull(name: String): CompiledLine<V>? =
  null
    ?: tupleGetLineOrNull(name)
    ?: resolvedGetLineOrNull(name)

fun <V> Compiled<V>.getOrNull(name: String): Compiled<V>? =
  getLineOrNull(name)?.let { compiled(it) }

fun <V> Compiled<V>.get(name: String): Compiled<V> =
  getOrNull(name)?:compileError(script("get"))

fun <V> Compiled<V>.getLineOrNull(index: Int): CompiledLine<V>? =
  type.getLineOrNull(index)?.let { compiled(line(get(this, index)), it) }

fun <V> Compiled<V>.getOrNull(index: Int): Compiled<V>? =
  getLineOrNull(index)?.let { compiled(it) }

val <V> Compiled<V>.tupleOnlyLineOrNull: CompiledLine<V>? get() =
  tuplePairOrNull?.let { pair ->
    notNullIf(pair.first.type.isEmpty) {
      pair.second
    }
  }

val <V> Compiled<V>.tuplePairOrNull: Pair<Compiled<V>, CompiledLine<V>>? get() =
  expression.tupleOrNull?.lineStack?.linkOrNull?.let { lineLink ->
    type.structureOrNull!!.lineStack.linkOrNull!!.let { typeLink ->
      compiled(expression(lineLink.tail.let(::Tuple)), typeLink.tail.structure.type) to compiled(lineLink.head, typeLink.head)
    }
  }

fun <V, R> Compiled<V>.infix(fn: (Compiled<V>, String, Compiled<V>) -> R?): R? =
  tuplePairOrNull?.let { (lhs, line) ->
    line.line.fieldOrNull?.let { field ->
       fn(lhs, field.name, field.rhs)
    }
  }

fun <V, R> Compiled<V>.infix(name: String, fn: (Compiled<V>, Compiled<V>) -> R?): R? =
  infix { lhs, infixName, rhs ->
    ifOrNull(infixName == name) {
      fn(lhs, rhs)
    }
  }

fun <V, R> Compiled<V>.prefix(fn: (String, Compiled<V>) -> R?): R? =
  infix { lhs, name, rhs ->
    ifOrNull(lhs.type.isEmpty) {
      fn(name, rhs)
    }
  }

fun <V, R> Compiled<V>.prefix(name: String, fn: (Compiled<V>) -> R?): R? =
  infix(name) { lhs, rhs ->
    ifOrNull(lhs.type.isEmpty) {
      fn(rhs)
    }
  }

fun <V> Compiled<V>.line(index: Int): CompiledLine<V> =
  expression.tupleOrNull
    ?.let { tuple -> compiled(tuple.lineStack.getFromBottom(index)!!, type.structureOrNull!!.lineStack.getFromBottom(index)!!) }
    ?: compiled(line(get(this, index)), type.structureOrNull!!.lineStack.getFromBottom(index)!!)

fun <V> Compiled<V>.make(name: String): Compiled<V> =
  compiled(name lineTo this)

fun <V> nativeNumberCompiledLine(native: V): CompiledLine<V> = compiled(nativeLine(native), numberTypeLine)
fun <V> nativeTextCompiledLine(native: V): CompiledLine<V> = compiled(nativeLine(native), textTypeLine)

fun <V> nativeNumberCompiled(native: V): Compiled<V> = compiled(nativeNumberCompiledLine(native))
fun <V> nativeTextCompiled(native: V): Compiled<V> = compiled(nativeTextCompiledLine(native))

val <V> Compiled<V>.tupleCompiledTupleOrNull: CompiledTuple<V>? get() =
  expression.tupleOrNull?.let { compiled(it, type.structureOrNull!!) }

val <V> Compiled<V>.resolvedCompiledTupleOrNull: CompiledTuple<V>? get() =
  type.structureOrNull?.let { typeStructure ->
    compiled("tuple" lineTo this).indirectIf(typeStructure.lineStack.size >= 2) { target ->
      compiled(
        *typeStructure.lineSeq.mapIndexed.reverse.stack.mapIt { indexedTypeLine ->
          compiled(line(get(target, indexedTypeLine.index)), indexedTypeLine.value)
        }.array)
    }.tupleCompiledTupleOrNull?:compileError(script("tuple"))
  }

val <V> Compiled<V>.compiledTupleOrNull: CompiledTuple<V>? get() =
  null
    ?: tupleCompiledTupleOrNull
    ?: resolvedCompiledTupleOrNull

fun <V> Compiled<V>.indirect(fn: (Compiled<V>) -> Compiled<V>): Compiled<V> =
  fn(type, fn(compiledVariable(0, type))).invoke(this)

fun <V> Compiled<V>.indirectIf(condition: Boolean, fn: (Compiled<V>) -> Compiled<V>): Compiled<V> =
  if (condition) indirect(fn)
  else this

fun <V> compiledVariable(index: Int, type: Type): Compiled<V> =
  compiled(expression(leo.term.variable(index)), type)

fun <V> Tuple<V>.plus(line: Line<V>): Tuple<V> =
  lineStack.push(line).let(::Tuple)

fun <V> Fragment<V>.plus(line: Line<V>): Fragment<V> =
  copy(tuple = tuple.plus(line))

val <V> Compiled<V>.compiledFragment: CompiledFragment<V> get() =
  compiled(fragment(expression, tuple()), type)

fun <V> CompiledFragment<V>.plus(compiledLine: CompiledLine<V>): CompiledFragment<V> =
  compiled(fragment.plus(compiledLine.line), type.plus(compiledLine.typeLine))

fun <V> CompiledFragment<V>.resolveCompiledLines(fn: (Stack<CompiledLine<V>>) -> Compiled<V>): Compiled<V> =
  TODO()

val <V> Compiled<V>.onlyCompiledLineOrNull: CompiledLine<V>? get() =
  type.onlyLineOrNull?.let { typeLine ->
    null
      ?: expression.tupleOrNull?.lineStack?.onlyOrNull?.let { line -> compiled(line, typeLine) }
      ?: compiled(line(get(compiled("tuple" lineTo this), 0)), typeLine)
  }

val <V> Compiled<V>.onlyCompiledLine: CompiledLine<V> get() =
  onlyCompiledLineOrNull ?: compileError(script("line"))

val <V> Compiled<V>.compiledLineStack: Stack<CompiledLine<V>> get() =
  zip(compiledTuple.tuple.lineStack, compiledTuple.typeStructure.lineStack)
    .mapIt { compiled(it.first!!, it.second!!) }