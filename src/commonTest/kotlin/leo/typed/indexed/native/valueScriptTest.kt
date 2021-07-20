package leo.typed.indexed.native

import leo.base.assertEqualTo
import leo.choice
import leo.doingName
import leo.empty
import leo.functionName
import leo.functionType
import leo.lineTo
import leo.literal
import leo.nativeName
import leo.numberType
import leo.script
import leo.scriptLine
import leo.textType
import leo.type
import leo.typed.compiler.native.DoublePlusDoubleNative
import leo.typed.compiler.native.Native
import leo.typed.compiler.native.StringPlusStringNative
import leo.typed.compiler.native.native
import leo.typed.compiler.native.nativeNumberType
import leo.typed.compiler.native.nativeNumberTypeLine
import leo.typed.compiler.native.nativeTextTypeLine
import leo.typed.indexed.nativeValue
import leo.typed.indexed.value
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
  fun nativeDouble() {
    nativeValue(128.0.native)
      .script(numberType)
      .assertEqualTo(script(literal(128)))
  }

  @Test
  fun nativeString() {
    nativeValue("Hello, world!".native)
      .script(textType)
      .assertEqualTo(script(literal("Hello, world!")))
  }

  @Test
  fun nativeFunction() {
    nativeValue(StringPlusStringNative)
      .script(textType)
      .assertEqualTo(
        script(
          nativeName lineTo script(
            "string" lineTo script(),
            "plus" lineTo script("string"))))
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
          type(nativeNumberTypeLine, "plus" lineTo nativeNumberType),
          nativeNumberType))
      .assertEqualTo(
        script(
          functionName lineTo script(
            nativeNumberTypeLine.scriptLine,
            "plus" lineTo nativeNumberType.script,
            doingName lineTo nativeNumberType.script)))
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

  @Test
  fun booleanComplexChoice() {
    value(value(true), nativeValue(10.0.native))
      .script(type(choice(nativeNumberTypeLine, nativeTextTypeLine)))
      .assertEqualTo(script(literal(10.0)))

    value(value(false), nativeValue("foo".native))
      .script(type(choice(nativeNumberTypeLine, nativeTextTypeLine)))
      .assertEqualTo(script(literal("foo")))
  }

  @Test
  fun indexComplexChoice() {
    value(value(0), nativeValue(10.0.native))
      .script(type(choice(nativeNumberTypeLine, nativeTextTypeLine, "foo" lineTo type())))
      .assertEqualTo(script(literal(10.0)))

    value(value(1), nativeValue("foo".native))
      .script(type(choice(nativeNumberTypeLine, nativeTextTypeLine, "foo" lineTo type())))
      .assertEqualTo(script(literal("foo")))

    value(value(2), value<Native>(empty))
      .script(type(choice(nativeNumberTypeLine, nativeTextTypeLine, "foo" lineTo type())))
      .assertEqualTo(script("foo"))
  }
}