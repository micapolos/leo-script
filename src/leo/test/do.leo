comment
  do
    binds
      all fields of the current value
      by their name
      locally
    and
      does the given script
    given
      is a special local binding
      which contains the input value

test
  x 10
  y 20
  do x
  is equal to x 10

test
  x 10
  y 20
  do y
  is equal to y 20

test
  x 10
  y 20
  do
    x.number
    plus y.number
  is equal to 30

test
  x 10
  y 20
  do given
  is equal to given
    x 10
    y 20

test
  "Hello, world!"
  do text
  is equal to "Hello, world!"

test
  123
  do number
  is equal to 123

test
  doing number plus 1
  do doing
  is equal to doing number plus 1

test
  applying number plus 1
  do applying
  is equal to applying number plus 1
