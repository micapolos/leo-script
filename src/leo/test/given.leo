comment
  given
    is a special binding inside
      do
      and doing
    which gives the input value

test
  x 10
  y 20
  do given
  is equal to given
    x 10
    y 20

test
  let
    any number
    and any number
    do
      given.number
      plus given.and.number
  10
  and 20
  is equal to 30
