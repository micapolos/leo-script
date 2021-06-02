let
  list any
  head
  do
    list
    switch
      empty doing fail
      link doing link.item.content

let
  list any
  tail
  do
    list
    switch
      empty doing fail
      link doing link.list.content

test
  list
    x 10
    y 20
    z 30
  head
  is equal
    z 30

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
  do list
    list.content
    with append.content

test
  list
    x 10
    y 20
  append
  is equal list
    x 10
    y 20

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
  do repeating
    folded content.list.tail
    do
      fold.list
      switch
        empty doing folded.content
        link doing
          fold.doing
          give soo
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
    doing
      "dupa"
      append "asiu"
  is equal "Hello, heaven, hell, world!"
