test
  let
    ping
    be pong

  ping
  is equal pong

test
  let
    x any
    y any
    do
      x
      and y

  x 10
  y 20
  is equal
    x 10
    and y 20

test
  let
    x any
    y any
    do content

  x 10
  y 20
  is equal quote
    x 10
    y 20

comment
  tail recursive function
  aka repeating function
test
  let
    number any
    times factorial number any
    do repeating
      times.factorial.number
      is equal 1
      switch
        yes doing number
        no doing
          number
          times times.factorial.number
          repeat times
            times.factorial.number
            minus 1
            factorial

  comment final function
  let
    number any
    factorial
    do
      1
      times factorial.number.factorial

  5.factorial
  is equal 120

comment recursive function
test
  let
    number any
    factorial
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

