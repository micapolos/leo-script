let
  empty.text
  be ""

let
  any.text.text
  do bind
    "\""
    append text.text
    append "\""

let
  any.literal.text
  do literal.content.text

recursive
  let
    any.field.text
    do bind
      text.field.word.text
      append "("
      append text.field.script.text
      append ")"

  let
    any.line.text
    do line.content.text

  let
    any.link.text
    do bind
      text.link.lhs.script.text
      append "."
      append text.link.rhs.line.text

  let
    any.script.text
    do script.content.text

test
  "foo".text
  is equal to "\"foo\""

test
  literal "foo"
  text
  is equal to "\"foo\""

test
  literal 123
  text
  is equal to "123"

test
  field
    word "foo"
    script empty
  text
  is equal to "foo()"

test
  line field
    word "foo"
    script empty
  text
  is equal to "foo()"

test
  line literal 123
  text
  is equal to "123"

test
  empty.script.text
  is equal to ""

test
  script link
    lhs script empty
    rhs line field
      word "foo"
      script empty
  text
  is equal to ".foo()"
