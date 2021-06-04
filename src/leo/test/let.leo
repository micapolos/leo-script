test
  let
    ping
    be pong

  ping
  is equal pong

comment test
  let
    x any
    y any
    do it

  x 10
  y 20
  is equal it
    x 10
    y 20

comment recursive function
comment test
  let
    any.number.factorial
    do recursing bind
      factorial.number
      is equal 1
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
  is equal 120

