let
  empty.list
  be list

test
  empty.list
  is equal list

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
  do repeat bind
    folded content.list.tail
    bind
      fold.list
      switch
        empty be folded.content.break
        link bind
          fold.doing
          give
            folded.content
            with link.item.content.other
          fold
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

let
  any.list.reverse
  do bind
    empty.list
    fold
      reverse.list
      doing bind
        list
        append other.content

test
  list
    10
    20
    30
  reverse
  is equal list
    30
    20
    10

let
  any.list
  map any.doing
  do bind
    empty.list
    fold
      list.reverse
      doing bind
        list
        append
          map.doing
          give other.content

test
  list
    10
    20
    30
  map doing plus 1
  is equal list
    11
    21
    31

let
  list any
  separate with any
  do bind
    empty.list
    fold
      list
      doing bind
        list
        switch
          empty be list
          link be
            list
            append separate.content
        append other.content
    reverse

test
  list
    10
    20
    30
  separate with 0
  is equal list
    10
    0
    20
    0
    30




