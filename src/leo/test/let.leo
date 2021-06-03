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

comment
  tail recursive function
  aka repeating function
test
  let
    any.number
    times any.number.factorial
    do repeating bind
      times.factorial.number
      is equal 1
      switch
        yes doing be number
        no doing be
          number
          times times.factorial.number
          repeat times
            times.factorial.number
            minus 1
            factorial

  comment final function
  let
    any.number.factorial
    do
      1
      times factorial.number.factorial

  5.factorial
  is equal 120

comment recursive function
test
  let
    any.number.factorial
    do recursing
      factorial.number
      is equal 1
      switch
        yes doing factorial.number
        no doing
          factorial.number
          times
            factorial.number
            minus 1
            factorial
            recurse

  5.factorial
  is equal 120

