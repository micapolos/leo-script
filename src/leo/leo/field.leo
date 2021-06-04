let
  field any
  map doing any
  do bind field
    field.text
    to
      field.to.content
      take map.doing

test
  field
    "x"
    to 10
  map doing text
  is equal field
    "x"
    to "10"
