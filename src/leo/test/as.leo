comment
  as
    throws an error
      if the current value
      does not match the given pattern
    returns the current value
      if it matches the given pattern

test
  "Hello, world!"
  as text
  is equal to "Hello, world!"

test
  try 
    "Hello, world!"
    as number
  is equal to quote try error
    "Hello, world!"
    as number

test
  point
    x 10
    y 20
  as point anything
  is equal to point
    x 10
    y 20
