let
  script any
  text
  do recursing
    let
      empty.text
      be ""

    let
      string any
      text
      do
        "\""
        append string.text
        append "\""

    let
      literal any
      text
      do literal.content.text

    let
      field any
      text
      do
        field.name.text
        append "("
        append
          field.script
          recurse text
        append ")"

    let
      line any
      text
      do line.content.text

    let
      link any
      text
      do
        link.script
        recurse text
        append "."
        append link.line.text

    script.content.text

test
  empty.script.text
  is equal ""

test
  script link
    script empty
    line field
      name "foo"
      script link

  text
  is equal ".foo()"
