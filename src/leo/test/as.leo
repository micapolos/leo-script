test
  "Hello, world!"
  as any.text
  is equal "Hello, world!"

test
  try 
    "Hello, world!"
    as any.number
  is equal quote try error
    "Hello, world!"
    as number any

test
  try
    "Hello, world!"
    as text
  is equal quote try error
    "Hello, world!"
    as text

test
  point
    x 10
    y 20
  as any.point
  is equal point
    x 10
    y 20
