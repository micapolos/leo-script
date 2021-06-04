test
  0
  loop bind
    number
    is equal 10000
    switch
      yes be number.break
      no be
        number
        plus 1
  is equal 10000
