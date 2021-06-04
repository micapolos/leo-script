use library number

test
  10000
  loop
    check equal 0
    switch
      yes number.break
      no number.decrement
  is equal 0
