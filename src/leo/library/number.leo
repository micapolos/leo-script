let
  number any
  with increment
  do
    number
    plus 1

test
  2.increment
  is equal 3

let
  number any
  with decrement
  do
    number
    minus 1

test
  2.decrement
  is equal 1

let
  number any
  with squared
  do
    number
    times number
 
let
  any
  iterate
    times number any
    doing any
  do repeating
    use library list
    item content.list.tail
    do
      iterate.times.number
      is less than 1
      switch
        yes doing item.content
        no doing
          item.content
          take iterate.doing
          repeat iterate
            iterate.times.number
            minus 1
            times
            with iterate.doing

test
  ""
  iterate
    5.times
    doing
      text
      append "!"
  is equal "!!!!!"
