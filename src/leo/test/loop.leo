use library number

test
  0
  loop
    check equal 10000
    switch
      yes number.break
      no number.increment
  is equal 10000
