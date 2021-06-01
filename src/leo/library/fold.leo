let
  any
  fold
    any
    doing any
  do repeating
    folded content.tail
    do
      fold.content.tail.structure
      switch
        empty doing folded.content
        link doing
          fold.doing
          give
            folded.content
            with link.content.head
          repeat fold
            link.content.tail
            with fold.doing

test
  100
  fold
    10
    20
    30
    doing
      content.tail
      plus content.head
  is equal 160

let
  any.reverse
  do
    fold
      content.tail
      doing
        content.tail
        with content.head

test
  10
  20
  30
  reverse
  is equal
    30
    20
    10

let
  any
  map doing any
  do
    fold
      content.tail
      doing
        content.tail
        with
          map.doing
          give content.head
    reverse

test
  use library number
  10
  20
  30
  map doing number.increment
  is equal
    11
    21
    31
