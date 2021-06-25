comment
  let
    defines local binding
    matching the given pattern
  let be
    defines a constant binding
  let do
    defines a function binding

test
  let
    ping
    be pong

  ping
  is equal to pong

test
  let
    10.increment
    do
      number
      plus 1

  10.increment
  is equal to 11

test
  let
    any.number.factorial
    do recursing bind
      factorial.number
      is equal to 1
      switch
        yes be factorial.number
        no be
          factorial.number
          times
            factorial.number
            minus 1
            factorial
            recurse

  5.factorial
  is equal to 120

