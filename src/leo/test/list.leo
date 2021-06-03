test
  list
  switch
    empty
    link
  is equal empty

test
  list
    x 10
    y 20
    z 30
  switch
    empty
    link
  is equal
    link
      list
        x 10
        y 20
      item z 30
