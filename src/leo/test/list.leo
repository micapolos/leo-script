test
  list
  switch
    empty doing empty
    link doing link
  is equal to empty

test
  list
    x 10
    y 20
    z 30
  switch
    empty doing empty
    link doing link
  is equal to
    link
      previous list
        x 10
        y 20
      last z 30
