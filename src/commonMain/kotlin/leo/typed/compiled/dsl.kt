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
import leo.empty
import leo.fieldOrNull
import leo.functionLineTo
import leo.functionOrNull
import leo.getFromBottom
import leo.indexOrNull
import leo.isEmpty
import leo.line
import leo.lineCount
import leo.lineTo
import leo.literal
import leo.name
import leo.onlyFieldOrNull
import leo.onlyLineOrNull
import leo.plus
import leo.plusOrNull
import leo.recursiveLine
import leo.script
import leo.stack
import leo.structureOrNull
import leo.type
import leo.typed.compiler.compileError
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextTypeLine
import leo.variable

fun <V> compiled(vararg lines: CompiledLine<V>): Compiled<V> =
  Compiled<V>(expression(empty), type()).fold(lines) { plus(it) }

fun <V> compiled(name: String): Compiled<V> =
  compiled(name lineTo compiled())

fun <V> Compiled<V>.plus(compiledLine: CompiledLine<V>): Compiled<V> =
  linkPlus(compiledLine)

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

val <V> CompiledLine<V>.fieldRhsOrNull: Compiled<V>? get() =
  line.fieldOrNull?.rhs

fun <V> Compiled<V>.getOrNull(name: String): Compiled<V>? =
  null
    ?: lineOrNull(name)?.let { compiled(it) }
    ?: rhsOrNull?.getOrNull(name)

fun <V> Compiled<V>.get(index: Int): Compiled<V> =
  getOrNull(index)?:compileError(script("get" lineTo script("index" lineTo script(literal(index)))))

fun <V> Compiled<V>.get(name: String): Compiled<V> =
  getOrNull(name)?:compileError(script("get" lineTo script("name" lineTo script(name))))

fun <V> Compiled<V>.getLineOrNull(index: Int): CompiledLine<V>? =
  rhsOrNull?.line(index)

fun <V> Compiled<V>.getOrNull(index: Int): Compiled<V>? =
  lineOrNull(index)?.let { compiled(it) }

val <V> Expression<V>.linkOrNull: Link<V>? get() =
  (this as? LinkExpression)?.link

val <V> Compiled<V>.onlyLineOrNull: CompiledLine<V>? get() =
  when (expression) {
    is LinkExpression ->
      ifOrNull(expression.link.lhs.type.isEmpty) {
        expression.link.rhsLine
      }
    else -> type.onlyLineOrNull?.let { line(0) }
  }

fun <V, R> Compiled<V>.infix(fn: (Compiled<V>, String, Compiled<V>) -> R?): R? =
  expression.linkOrNull?.let { (lhs, line) ->
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
  when (expression) {
    is LinkExpression ->
      if (index == type.lineCount - 1) expression.link.rhsLine
      else expression.link.lhs.lineOrNull(index)
    else ->
      type.structureOrNull?.lineStack?.getFromBottom(index)?.let { typeLine ->
        compiled(line(get(this, index)), typeLine)
      }
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

fun <V> Compiled<V>.indirect(fn: (Compiled<V>) -> Compiled<V>): Compiled<V> =
  fn(type, fn(compiledVariable(0, type))).invoke(this)

fun <V> compiledVariable(index: Int, rhsType: Type): Compiled<V> =
  compiled(expression(variable(index)), rhsType)

val <V> Compiled<V>.onlyCompiledFieldOrNull: CompiledField<V>? get() =
  onlyLineOrNull?.compiledFieldOrNull

val <V> Compiled<V>.onlyCompiledLine: CompiledLine<V> get() =
  onlyLineOrNull ?: compileError(script("line"))

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
      ?: onlyLineOrNull?.fieldRhsOrNull
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

//fun <V> Compiled<V>.with(binding: Binding<V>): Compiled<V> =
//  compiled(expression(bind(binding, this)), type)

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

fun <V> Compiled<V>.linkPlus(compiledLine: CompiledLine<V>): Compiled<V> =
  type.plusOrNull(compiledLine.typeLine)?.let { type ->
    compiled(expression(link(this, compiledLine)), type)
  }?: compileError(script("plus"))