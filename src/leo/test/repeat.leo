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
      yes do first.number
      no do
        number
        is equal 1
        switch
          yes do second.number
          no do
            number.decrement
            first second.number
            second
              first.number
              plus second.number
            repeat
  is equal 987
