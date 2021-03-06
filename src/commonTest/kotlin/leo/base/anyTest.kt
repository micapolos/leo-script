package leo.base

import kotlin.math.abs
import kotlin.math.sin
import kotlin.test.Test

class AnyTest {
  val times = 10000

  @Test
  fun whileNotNull() {
    1.0
      .whileNotNull { notNullIf(this < times) { plus(abs(sin(this)).inc()) } }
      .assert { this >= times }
  }

  @Test
  fun repeatUntilNull() {
    1.0
      .repeatUntilNull { notNullIf(this < times) { plus(abs(sin(this))).inc() } }
      .assert { this >= times }
  }
}