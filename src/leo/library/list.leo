let
  empty.list
  be list

test
  empty.list
  is equal to list

let
  any.list.last
  apply
    list
    switch
      empty list.last
      link last

test
  list
    x 10
    y 20
    z 30
  last
  is equal to
    last z 30

let
  any.list.previous
  do
    list
    switch
      empty list.previous
      link previous

test
  list
    x 10
    y 20
    z 30
  previous
  is equal to previous list
    x 10
    y 20

let
  list any
  append any
  do list
    list.content
    with append.content

test
  list
    x 10
    y 20
  append
    z 30
  is equal to list
    x 10
    y 20
    z 30

let
  list any
  fold to any
  doing any
  do repeat
    list
    switch
      empty be fold.to.content.end
      link do
        link.previous.list
        fold to
          fold.to.content
          other link.last.content
          take doing
        with doing

test
  list
  fold to "Hello"
  doing
    text
    append other.text
  is equal to "Hello"

test
  list "world!"
  fold to "Hello, "
  doing
    text
    append other.text
  is equal to "Hello, world!"

test
  list
    "world!"
    "hell, "
    "heaven, "
  fold to "Hello "
  doing
    text
    append other.text
  is equal to "Hello heaven, hell, world!"

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
  is equal to list
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
  is equal to list
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
  is equal to list
    10
    0
    20
    0
    30




