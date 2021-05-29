use library number

comment
  tail recursive fibonacci

test
  16
  first 0
  second 1
  do repeating
    number
    is equal 0
    switch
      yes doing first.number
      no doing
        number
        is equal 1
        switch
          yes doing
            second.number
          no doing
            number.decrement
            first second.number
            second
              first.number
              plus second.number
            repeat
  is equal 987
