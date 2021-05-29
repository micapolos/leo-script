let
  list any
  append item any
  do list link
    previous list
    last append.item

test
  list empty
  append item 0
  is equal list link
    previous list empty
    last item 0

test
  list empty
  append item 0
  append item 1
  is equal list link
    previous list link
      previous list empty
      last item 0
    last item 1

let
  item any
  fold
    list any
    doing any
  do repeating
    fold.list
    switch
      empty do item
      link do
        item
        with link.last
        take fold.doing
        fold
          link.previous.list
          with fold.doing
        repeat

test
  item empty.list
  fold
    list empty
    append item 1
    append item 2
    append item 3
    doing
      item.list
      append last.item
      item
  is equal item
    list empty
    append item 3
    append item 2
    append item 1

let
  list any
  reverse
  do
    item empty.list
    fold
      list
      doing
        item.list
        append last.item
        item
    list

test
  empty.list
  append item 1
  append item 2
  append item 3
  reverse
  is equal
    empty.list
    append item 3
    append item 2
    append item 1
