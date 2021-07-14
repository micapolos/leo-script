package leo.term.indexed.native

import leo.anyNumberScriptLine
import leo.base.assertEqualTo
import leo.choice
import leo.doingName
import leo.empty
import leo.functionName
import leo.functionType
import leo.lineTo
import leo.literal
import leo.numberType
import leo.numberTypeLine
import leo.script
import leo.term.compiler.native.DoublePlusDoubleNative
import leo.term.compiler.native.Native
import leo.term.compiler.native.native
import leo.term.indexed.nativeValue
import leo.term.indexed.value
import leo.textType
import leo.type
import kotlin.test.Test

class ValueScriptTest {
  @Test
  fun empty() {
    value<Native>(empty)
      .script(type())
      .assertEqualTo(script())
  }

  @Test
  fun static() {
    value<Native>(empty)
      .script(
        type(
          "point" lineTo type(
            "x" lineTo type("zero"),
            "y" lineTo type("one"))))
      .assertEqualTo(
        script(
          "point" lineTo script(
            "x" lineTo script("zero"),
            "y" lineTo script("one"))))
  }

  @Test
  fun number() {
    nativeValue(128.0.native)
      .script(numberType)
      .assertEqualTo(script(literal(128)))
  }

  @Test
  fun text() {
    nativeValue("Hello, world!".native)
      .script(textType)
      .assertEqualTo(script(literal("Hello, world!")))
  }

  @Test
  fun field() {
    nativeValue(128.0.native)
      .script(type("id" lineTo numberType))
      .assertEqualTo(script("id" lineTo script(literal(128))))
  }

  @Test
  fun fields() {
    value(nativeValue(128.0.native), nativeValue("foo".native))
      .script(type("id" lineTo numberType, "name" lineTo textType))
      .assertEqualTo(script(
        "id" lineTo script(literal(128)),
        "name" lineTo script(literal("foo"))))
  }

  @Test
  fun function() {
    nativeValue(DoublePlusDoubleNative)
      .script(
        functionType(
          type(numberTypeLine, "plus" lineTo numberType),
          numberType))
      .assertEqualTo(
        script(
          functionName lineTo script(
            anyNumberScriptLine,
            "plus" lineTo script(anyNumberScriptLine),
            doingName lineTo script(anyNumberScriptLine))))
  }

  @Test
  fun booleanChoice() {
    value<Native>(true)
      .script(type(choice("yes" lineTo type(), "no" lineTo type())))
      .assertEqualTo(script("yes"))

    value<Native>(false)
      .script(type(choice("yes" lineTo type(), "no" lineTo type())))
      .assertEqualTo(script("no"))
  }

  @Test
  fun indexChoice() {
    value<Native>(0)
      .script(type(choice("yes" lineTo type(), "no" lineTo type(), "maybe" lineTo type())))
      .assertEqualTo(script("yes"))

    value<Native>(1)
      .script(type(choice("yes" lineTo type(), "no" lineTo type(), "maybe" lineTo type())))
      .assertEqualTo(script("no"))

    value<Native>(2)
      .script(type(choice("yes" lineTo type(), "no" lineTo type(), "maybe" lineTo type())))
      .assertEqualTo(script("maybe"))
  }
}