use library fold

let
  script any
  text
  do recursing
    let
      literal any
      text
      do
        literal
        switch
          text doing
            "\""
            append text
            append "\""
          number doing number.text

    let
      field any
      text
      do
        field.text
        append "("
        append
          field.to.script
          recurse text
        append ")"

    let
      line any
      text
      do line.content.text

    ""
    fold
      script.content.reverse
      doing
        content.tail
        append content.head.text

test
  script.text
  is equal ""

test
  script line field
    "point"
    to script
      line field
        "x"
        to script line literal 10
      line field
        "y"
        to script line literal 20
  text
  is equal "point(x(10)y(20))"
