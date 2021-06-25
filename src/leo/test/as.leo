comment
  as
    throws an error
      if the current value
      does not match the given pattern
    returns the current value
      if it matches the given pattern

test
  "Hello, world!"
  as any.text
  is equal to "Hello, world!"

test
  try 
    "Hello, world!"
    as any.number
  is equal to quote try error
    "Hello, world!"
    as number any

test
  try
    "Hello, world!"
    as text
  is equal to quote try error
    "Hello, world!"
    as text

test
  point
    x 10
    y 20
  as any.point
  is equal to point
    x 10
    y 20
