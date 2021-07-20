package leo.typed.compiled

import leo.Rope
import leo.Type
import leo.TypeChoice
import leo.TypeLine
import leo.base.ifNotNull
import leo.base.ifOrNull
import leo.base.notNullIf
import leo.choiceOrNull
import leo.fold
import leo.onlyFieldOrNull
import leo.onlyOrNull
import leo.push
import leo.reverse
import leo.ropeOrNull
import leo.stack

fun <V> Compiled<V>.castOrNull(type: Type): Compiled<V>? =
  null
    ?: choiceCastOrNull(type)
    ?: lineCastOrNull(type)

fun <V> Compiled<V>.choiceCastOrNull(type: Type): Compiled<V>? =
  type.choiceOrNull?.let { castOrNull(it) }

fun <V> Compiled<V>.lineCastOrNull(type: Type): Compiled<V>? =
  type.onlyFieldOrNull?.let { typeField ->
    onlyCompiledFieldOrNull?.let { compiledField ->
      ifOrNull(typeField.name == compiledField.field.name) {
        compiledField.rhs.castOrNull(typeField.rhsType)?.let { cast ->
          compiled(typeField.name lineTo cast)
        }
      }
    }
  }

fun <V> Compiled<V>.castOrNull(choice: TypeChoice): Compiled<V>? =
  choice.lineStack.ropeOrNull?.let { rope ->
    stack<Compiled<V>>()
      .fold(rope) { caseRope ->
        ifNotNull(castOrNull(caseRope)) { push(it) }
      }
      .onlyOrNull
  }

fun <V> Compiled<V>.castOrNull(caseRope: Rope<TypeLine>): Compiled<V>? =
  onlyCompiledLineOrNull?.let { compiledLine ->
    notNullIf(compiledLine.typeLine == caseRope.current) {
      compiledSelect<V>()
        .fold(caseRope.tail.reverse) { not(it) }
        .the(compiledLine)
        .fold(caseRope.head) { not(it) }
        .compiled
    }
  }

