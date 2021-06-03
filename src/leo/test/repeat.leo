use library number

comment
  tail recursive fibonacci

test
  16
  first 0
  second 1
  do repeating bind
    number
    is equal 0
    switch
      yes be first.number
      no be
        number
        is equal 1
        switch
          yes be second.number
          no be
            number.decrement
            first second.number
            second
              first.number
              plus second.number
            repeat
  is equal 987
