define
  empty.text
  is ""

define
  any.text.text
  does
    "\""
    append text.text
    append "\""

define
  any.literal.text
  does text.literal.content.text

recursive
  define
    any.field.text
    does
      text.field.word.text
      append "("
      append text.field.script.text
      append ")"

  define
    any.line.text
    does text.line.content.text

  define
    any.link.text
    does
      text.link.lhs.script.text
      append "."
      append text.link.rhs.line.text

  define
    any.script.text
    does text.script.content.text

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
