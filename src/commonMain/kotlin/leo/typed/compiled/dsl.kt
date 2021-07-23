package leo.typed.compiled

import leo.Stack
import leo.Type
import leo.TypeLine
import leo.applyName
import leo.array
import leo.atom
import leo.base.fold
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.choiceOrNull
import leo.fieldOrNull
import leo.functionLineTo
import leo.functionOrNull
import leo.getFromBottom
import leo.indexOrNull
import leo.isEmpty
import leo.line
import leo.lineTo
import leo.linkOrNull
import leo.map
import leo.mapIt
import leo.name
import leo.onlyFieldOrNull
import leo.onlyLineOrNull
import leo.onlyOrNull
import leo.plus
import leo.push
import leo.recursiveLine
import leo.script
import leo.stack
import leo.structure
import leo.structureOrNull
import leo.type
import leo.typeStructure
import leo.typed.compiler.compileError
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextTypeLine
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
            "equal" lineTo script(
              "to" lineTo type.script)))))
  }

fun <V> Compiled<V>.switch(caseStack: Stack<Compiled<V>>, type: Type): Compiled<V> =
  compiled(expression(switch(this, *caseStack.array)), type)

fun <V> Compiled<V>.switch(type: Type, vararg cases: Compiled<V>): Compiled<V> =
  switch(stack(*cases), type)

fun <V> fn(type: Type, body: Body<V>): Compiled<V> =
  compiled(fnLine(type, body))

fun <V> recFn(type: Type, body: Compiled<V>): Compiled<V> =
  compiled(recFnLine(type, body))

fun <V> recFnLine(type: Type, body: Compiled<V>): CompiledLine<V> =
  compiled(line(function(type, recursive(body(body)))), type functionLineTo body.type)

fun <V> fn(type: Type, body: Compiled<V>): Compiled<V> =
  compiled(fnLine(type, body))

fun <V> fnLine(type: Type, body: Body<V>): CompiledLine<V> =
  compiled(line(function(type, body)), type functionLineTo body.compiled.type)

fun <V> fnLine(type: Type, body: Compiled<V>): CompiledLine<V> =
  compiled(line(function(type, body(body))), type functionLineTo body.type)

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

fun <V> Compiled<V>.getOrNull(name: String): Compiled<V>? =
  null
    ?: lineOrNull(name)?.let { compiled(it) }
    ?: rhsOrNull?.getOrNull(name)

fun <V> Compiled<V>.get(name: String): Compiled<V> =
  getOrNull(name)?:compileError(script("get"))

fun <V> Compiled<V>.getLine(index: Int): CompiledLine<V> =
  getLineOrNull(index) ?: compileError(script("get" lineTo script("line")))

fun <V> Compiled<V>.getLineOrNull(index: Int): CompiledLine<V>? =
  rhsOrNull?.line(index)

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

fun <V> Compiled<V>.lineOrNull(index: Int): CompiledLine<V>? =
  null
    ?: tupleLineOrNull(index)
    ?: nonTupleLineOrNull(index)

fun <V> Compiled<V>.tupleLineOrNull(index: Int): CompiledLine<V>? =
  compiledTupleOrNull?.lineOrNull(index)

fun <V> CompiledTuple<V>.lineOrNull(index: Int): CompiledLine<V>? =
  compiled(
    tuple.lineStack.getFromBottom(index)!!,
    typeStructure.lineStack.getFromBottom(index)!!)

fun <V> Compiled<V>.nonTupleLineOrNull(index: Int): CompiledLine<V>? =
  type.structureOrNull?.lineStack?.getFromBottom(index)?.let { typeLine ->
    compiled(line(get(this, index)), typeLine)
  }

fun <V> Compiled<V>.lineCompiled(index: Int): Compiled<V> =
  compiled(line(index))

fun <V> Compiled<V>.line(index: Int): CompiledLine<V> =
  lineOrNull(index)?: compileError(script("line"))

fun <V> Compiled<V>.lineOrNull(name: String): CompiledLine<V>? =
  type.structureOrNull?.indexOrNull(name)?.let {
    line(it)
  }

fun <V> Compiled<V>.make(name: String): Compiled<V> =
  compiled(name lineTo this)

fun <V> nativeNumberCompiledLine(native: V): CompiledLine<V> = compiled(nativeLine(native), nativeNumberTypeLine)
fun <V> nativeTextCompiledLine(native: V): CompiledLine<V> = compiled(nativeLine(native), nativeTextTypeLine)

fun <V> nativeNumberCompiled(native: V): Compiled<V> = compiled(nativeNumberCompiledLine(native))
fun <V> nativeTextCompiled(native: V): Compiled<V> = compiled(nativeTextCompiledLine(native))

val <V> Compiled<V>.tupleCompiledTupleOrNull: CompiledTuple<V>? get() =
  expression.tupleOrNull?.let { compiled(it, type.structureOrNull!!) }

val <V> Compiled<V>.resolvedCompiledTupleOrNull: CompiledTuple<V>? get() =
  type.onlyLineOrNull?.let { typeLine ->
    compiled(tuple(line(get(this, 0))), typeStructure(typeLine))
  }

val <V> Compiled<V>.compiledTupleOrNull: CompiledTuple<V>? get() =
  null
    ?: tupleCompiledTupleOrNull
    ?: resolvedCompiledTupleOrNull

fun <V> Compiled<V>.indirect(fn: (Compiled<V>) -> Compiled<V>): Compiled<V> =
  fn(type, fn(compiledVariable(type, type))).invoke(this)

fun <V> compiledVariable(lhsType: Type, rhsType: Type): Compiled<V> =
  compiled(expression(variable(lhsType)), rhsType)

fun <V> Tuple<V>.plus(line: Line<V>): Tuple<V> =
  lineStack.push(line).let(::Tuple)

fun <V> Fragment<V>.plus(line: Line<V>): Fragment<V> =
  copy(tuple = tuple.plus(line))

val <V> Compiled<V>.onlyCompiledLineOrNull: CompiledLine<V>? get() =
  type.onlyLineOrNull?.let { typeLine ->
    null
      ?: expression.tupleOrNull?.lineStack?.onlyOrNull?.let { line -> compiled(line, typeLine) }
      ?: compiled(line(get(this, 0)), typeLine)
  }

val <V> Compiled<V>.onlyCompiledFieldOrNull: CompiledField<V>? get() =
  onlyCompiledLineOrNull?.compiledFieldOrNull

val <V> Compiled<V>.onlyCompiledLine: CompiledLine<V> get() =
  onlyCompiledLineOrNull ?: compileError(script("line"))

val <V> Compiled<V>.compiledLineStack: Stack<CompiledLine<V>> get() =
  zip(compiledTuple.tuple.lineStack, compiledTuple.typeStructure.lineStack)
    .mapIt { compiled(it.first!!, it.second!!) }

fun <V> CompiledSelect<V>.the(compiledLine: CompiledLine<V>): CompiledSelect<V> =
  ifOrNull(caseOrNull == null) {
    CompiledSelect(case(compiledLine.typeLine.name, compiledLine.line), choice.plus(compiledLine.typeLine))
  }?: compileError(script("pick"))

fun <V> CompiledSelect<V>.not(typeLine: TypeLine): CompiledSelect<V> =
  CompiledSelect(caseOrNull, choice.plus(typeLine))

fun <V> CompiledSelect<V>.plus(line: CompiledSelectLine<V>): CompiledSelect<V> =
  when (line) {
    is NotCompiledSelectLine -> not(line.not.typeLine)
    is TheCompiledSelectLine -> the(line.the.compiledLine)
  }

val <V> CompiledSelect<V>.compiled: Compiled<V> get() =
  if (caseOrNull == null) compileError(script("not" lineTo script("selected")))
  else compiled(expression(select(choice, caseOrNull)), type(choice))

val <V> CompiledFunction<V>.compiled: Compiled<V> get() =
  compiled(compiledLine)

val <V> CompiledFunction<V>.compiledLine: CompiledLine<V> get() =
  compiled(line(function), line(atom(typeFunction)))

val <V> Compiled<V>.rhsOrNull: Compiled<V>? get() =
  type.rhsOrNull?.let { rhsType ->
    null
      ?: expression.tupleOrNull?.lineStack?.onlyOrNull?.fieldOrNull?.rhs
      ?: compiled(expression(content(this)), rhsType)
  }

val <V> Compiled<V>.rhs: Compiled<V> get() =
  rhsOrNull ?: compileError(script("rhs"))

val <V> Compiled<V>.compiledChoice: CompiledChoice<V> get() =
  compiledChoiceOrNull?: compileError(script("choice"))

val <V> Compiled<V>.compiledChoiceOrNull: CompiledChoice<V>? get() =
  type.choiceOrNull
    ?.let { compiled(expression, it) }
    ?: rhsOrNull?.compiledChoiceOrNull

fun <V> Compiled<V>.with(binding: Binding<V>): Compiled<V> =
  compiled(expression(bind(binding, this)), type)

val <V> CompiledLine<V>.compiledFieldOrNull: CompiledField<V>? get() =
  typeLine.atom.fieldOrNull?.let { typeField ->
    line.fieldOrNull?.let { field ->
      compiled(field, typeField)
    }
  }

val <V> CompiledField<V>.rhs: Compiled<V> get() = field.rhs

fun <V> Compiled<V>.have(compiled: Compiled<V>): Compiled<V> =
  type.have(compiled)

fun <V> Compiled<V>.haveOrNull(compiled: Compiled<V>): Compiled<V>? =
  type.haveOrNull(compiled)

fun <V> Type.have(compiled: Compiled<V>): Compiled<V> =
  haveOrNull(compiled) ?: compileError(script("have"))

fun <V> Type.haveOrNull(compiled: Compiled<V>): Compiled<V>? =
  if (isEmpty) compiled
  else onlyFieldOrNull?.let { typeField ->
    typeField.rhsType.haveOrNull(compiled)?.let { have ->
      compiled(typeField.name lineTo have)
    }
  }

fun <V> selectCompiled(vararg lines: CompiledSelectLine<V>): Compiled<V> =
  compiledSelect<V>().fold(lines) { plus(it) }.compiled

fun <V> recursive(compiledLine: CompiledLine<V>): CompiledLine<V> =
  compiled(compiledLine.line, recursiveLine(compiledLine.typeLine))

val <V> CompiledTuple<V>.compiledLineStack: Stack<CompiledLine<V>> get() =
  zip(tuple.lineStack, typeStructure.lineStack).map { compiled(first!!, second!!) }