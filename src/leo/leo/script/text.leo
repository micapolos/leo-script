use leo field
use leo literal
use leo script
use library list

let
  any.literal.text
  do
    literal
    switch
      text bind
        "\""
        append text
        append "\""
      number text

test
  123.literal.text
  is equal "123"

test
  "foo".literal.text
  is equal "\"foo\""

let
  any.field.text
  do
    field
    map doing text
    bind
      field.text
      append "("
      append
        text.doing
        give field.to.content
      append ")"

test
  field
    "point"
    to 123
  text
  is equal "point(123)"

let
  any.line.text
  do
    line
    map doing text

test
  line field
    "x" 
    to 10
  text
  is equal "x(10)"

test
  line literal 10
  text doing text
  is equal "10"

let
  any.script.text
  do recursing bind
    text.script.content.list
    map doing text doing text.recurse
    separate with "."
    text

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
  is equal "point(x(10).y(20))"
