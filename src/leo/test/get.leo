comment
  get is not a keyword
  details
    it gives
      the field of the current value
      with the given name
    the value
      must be a structure
      so it needs to have a single line

test
  point
    x 10
    y 20
  x
  is equal to x 10

test
  point
    x 10
    y 20
  y
  is equal to y 20

test
  circle
    radius 10
    center point
      x 20
      y 30
  center.point.x.number
  is equal to 20

test
  this 10
  number
  plus 1
  is equal to 11

test
  this "Hello, "
  text
  append "world!"
  is equal to "Hello, world!"

test
  this doing
    text
    append "world!"
  doing
  give "Hello, "
  is equal to "Hello, world!"

test
  this applying append "world!"
  applying
  give "Hello, "
  is equal to "Hello, world!"
