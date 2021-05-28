comment
  let
    number any
    plus number any
    do
      "leo25.natives.NumberKt".class.java.class
      method
        "plus"
        args
          element "leo14.Number".class.java
          element "leo14.Number".class.java
          array.java
      invoke
        null.java
        args
          element number.java
          element plus.number.java
          array.java
      number

  test
    2
    plus 3
    is equal 5

  let
    number any
    minus number any
    do
      "leo25.natives.NumberKt".class.java.class
      method
        "minus"
        args
          element "leo14.Number".class.java
          element "leo14.Number".class.java
          array.java
      invoke
        null.java
        args
          element number.java
          element minus.number.java
          array.java
      number

  test
    5
    minus 3
    is equal 2

  let
    number any
    times number any
    do
      "leo25.natives.NumberKt".class.java.class
      method
        "times"
        args
          element "leo14.Number".class.java
          element "leo14.Number".class.java
          array.java
      invoke
        null.java
        args
          element number.java
          element times.number.java
          array.java
      number

  test
    2
    times 3
    is equal 6

  let
    number any
    is less than number any
    do
      "leo25.natives.NumberKt".class.java.class
      method
        "lessThan"
        args
          element "leo14.Number".class.java
          element "leo14.Number".class.java
          array.java
      invoke
        null.java
        args
          element number.java
          element is.less.than.number.java
          array.java
      do
        java
        is equal true.java

  test
    5
    is less than 6
    is equal is yes

  test
    5
    is less than 5
    is equal is no

  test
    5
    is less than 4
    is equal is no

let
  number any
  increment
  do
    number
    plus 1

test
  2.increment
  is equal 3

let
  number any
  decrement
  do
    number
    minus 1

test
  2.decrement
  is equal 1
