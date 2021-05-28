comment
  private
    let
      string.class
      be "java.lang.String".class.java.class

    let
      length string
      method
      be
        string.class
        method
          "length"
          args array.java

    let
      concat string
      method
      be
        string.class
        method
          "concat"
          args string.class.java.element.array.java

  let
    text any
    length
    do
      string.length.method
      invoke
        text.java
        args array.java
      integer.number.length

  test
    "Hello, world!".length
    is equal length 13

  let
    text any
    append text any
    do
      string.concat.method
      invoke
        text.java
        args append.text.java.element.array.java
      text

  test
    "Hello, "
    append "world!"
    is equal "Hello, world!"
