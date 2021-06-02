test
  list
  switch
    empty doing empty
    link doing link
  is equal empty

test
  list
    x 10
    y 20
    z 30
  switch
    empty doing empty
    link doing link
  is equal
    link
      list
        x 10
        y 20
      item z 30
