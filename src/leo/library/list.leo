let
  any.list.head
  do
    list
    switch
      empty fail
      link item.content

test
  list
    x 10
    y 20
    z 30
  head
  is equal
    z 30

let
  any.list.tail
  do
    list
    switch
      empty fail
      link list.content

test
  list
    x 10
    y 20
    z 30
  tail
  is equal
    x 10
    y 20

let
  list any
  append any
  do bind list
    list.content
    with append.content

test
  list
    x 10
    y 20
  append
    z 30
  is equal list
    x 10
    y 20
    z 30

let
  any
  fold
    list any
    doing any
  do repeating bind
    folded content.list.tail
    bind
      fold.list
      switch
        empty be folded.content
        link bind
          fold.doing
          give
            folded.content
            with link.item.content.other
          repeat fold
            link.list
            with fold.doing

test
  "Hello, "
  fold
    list
      "world!"
      "hell, "
      "heaven, "
    doing bind
      text
      append other.text
  is equal "Hello, heaven, hell, world!"
