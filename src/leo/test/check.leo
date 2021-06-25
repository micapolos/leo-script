comment
  check
    gives
      check yes value
        if the current value satisfies the given condition
      check no value
        if the current value does not satisfy the given condition

test
  123
  check equal to 123
  is equal to quote check yes 123

test
  123
  check equal to 124
  is equal to quote check no 123

test
  123
  check less than 124
  is equal to quote check yes 123

test
  123
  check less than 123
  is equal to quote check no 123

test
  123
  check not less than 124
  is equal to quote check no 123

test
  123
  check not less than 123
  is equal to quote check yes 123
