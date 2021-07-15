package leo.named.evaluator

import leo.anyNumberScriptLine
import leo.anyTextScriptLine
import leo.base.assertEqualTo
import leo.base.assertSameAfter
import leo.beName
import leo.bindName
import leo.doName
import leo.doingName
import leo.functionName
import leo.giveName
import leo.line
import leo.lineTo
import leo.literal
import leo.privateName
import leo.quoteName
import leo.script
import leo.takeName
import leo.takingName
import leo.typeName
import leo.withName
import kotlin.test.Test

class ScriptEvaluateTest {
  @Test
  fun empty() {
    script().assertSameAfter { evaluate }
  }

  @Test
  fun literals() {
    script(literal(10)).assertSameAfter { evaluate }
    script(literal("foo")).assertSameAfter { evaluate }
  }

  @Test
  fun field() {
    script("x" lineTo script(literal(10))).assertSameAfter { evaluate }
  }

  @Test
  fun fields() {
    script(
      "x" lineTo script(literal(10)),
      "y" lineTo script(literal(20))
    )
      .assertSameAfter { evaluate }
  }

  @Test
  fun fieldGet() {
    script(
      "point" lineTo script(
        "x" lineTo script(literal(10)),
        "y" lineTo script(literal(20))
      ),
      "x" lineTo script()
    )
      .evaluate
      .assertEqualTo(script("x" lineTo script(literal(10))))
  }

  @Test
  fun make() {
    script(
      "x" lineTo script(literal(10)),
      "y" lineTo script(literal(20)),
      "point" lineTo script()
    )
      .evaluate
      .assertEqualTo(
        script(
          "point" lineTo script(
            "x" lineTo script(literal(10)),
            "y" lineTo script(literal(20))
          )
        )
      )
  }

  @Test
  fun be() {
    script(
      "ugly" lineTo script(),
      beName lineTo script("pretty")
    )
      .evaluate
      .assertEqualTo(script("pretty"))
  }

  @Test
  fun do_() {
    script(
      "x" lineTo script(literal(10)),
      "y" lineTo script(literal(20)),
      doName lineTo script("x" lineTo script())
    )
      .evaluate
      .assertEqualTo(script("x" lineTo script(literal(10))))
  }

  @Test
  fun functionGive() {
    script(
      functionName lineTo script(
        takingName lineTo script("ping"),
        doingName lineTo script("pong" lineTo script())
      ),
      giveName lineTo script("ping")
    )
      .evaluate
      .assertEqualTo(script("pong"))
  }

  @Test
  fun takeFunction() {
    script(
      "ping" lineTo script(),
      takeName lineTo script(
        functionName lineTo script(
          takingName lineTo script("ping"),
          doingName lineTo script("pong" lineTo script())
        )
      )
    )
      .evaluate
      .assertEqualTo(script("pong"))
  }

  @Test
  fun type() {
    script(
      "name" lineTo script(literal("foo")),
      "age" lineTo script(literal(15)),
      typeName lineTo script()
    )
      .evaluate
      .assertEqualTo(
        script(
          "name" lineTo script(anyTextScriptLine),
          "age" lineTo script(anyNumberScriptLine)
        )
      )
  }

  @Test
  fun bind() {
    script(
      bindName lineTo script(
        "x" lineTo script(literal(10)),
        "y" lineTo script(literal(20)),
        "x" lineTo script(literal(30))
      ),
      "x" lineTo script()
    )
      .evaluate
      .assertEqualTo(script("x" lineTo script(literal(30))))

    script(
      bindName lineTo script(
        "x" lineTo script(literal(10)),
        "y" lineTo script(literal(20)),
        "x" lineTo script(literal(30))
      ),
      "y" lineTo script()
    )
      .evaluate
      .assertEqualTo(script("y" lineTo script(literal(20))))

    script(
      bindName lineTo script(
        "x" lineTo script(literal(10)),
        "y" lineTo script(literal(20)),
        "x" lineTo script(literal(30))
      ),
      "z" lineTo script()
    )
      .evaluate
      .assertEqualTo(script("z"))
  }

  @Test
  fun with() {
    script(
      line(literal(10)),
      "x" lineTo script(),
      withName lineTo script(
        line(literal(20)),
        "y" lineTo script()
      )
    )
      .evaluate
      .assertEqualTo(
        script(
          "x" lineTo script(literal(10)),
          "y" lineTo script(literal(20))
        )
      )
  }

  @Test
  fun quote() {
    script(
      line(literal(10)),
      "x" lineTo script(),
      quoteName lineTo script(
        line(literal(20)),
        "y" lineTo script()
      )
    )
      .evaluate
      .assertEqualTo(
        script(
          "x" lineTo script(literal(10)),
          line(literal(20)),
          "y" lineTo script()
        )
      )
  }

  @Test
  fun private() {
    script(
      privateName lineTo script(
        bindName lineTo script("x" lineTo script(literal(10))),
        bindName lineTo script("y" lineTo script("x"))
      ),
      withName lineTo script("x" lineTo script()),
      withName lineTo script("y" lineTo script())
    )
      .evaluate
      .assertEqualTo(
        script(
          "x" lineTo script(literal(10)),
          "y" lineTo script("x" lineTo script(literal(10)))
        )
      )
  }

  @Test
  fun privatePrivate() {
    script(
      privateName lineTo script(
        privateName lineTo script(
          bindName lineTo script("x" lineTo script(literal(10)))
        ),
        bindName lineTo script("y" lineTo script("x"))
      ),
      withName lineTo script("x" lineTo script()),
      withName lineTo script("y" lineTo script())
    )
      .evaluate
      .assertEqualTo(
        script(
          "x" lineTo script(),
          "y" lineTo script("x" lineTo script(literal(10)))
        )
      )
  }
}