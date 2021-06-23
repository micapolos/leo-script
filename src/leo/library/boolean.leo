define
  any.boolean.negate
  does
    boolean
    switch
      false be true.boolean
      true be false.boolean

test
  false.boolean.negate
  is equal to true.boolean

test
  true.boolean.negate
  is equal to false.boolean

define
  any.boolean
  and any.boolean
  does bind
    boolean
    switch
      true be and.boolean
      false be false.boolean

test
  false.boolean
  and false.boolean
  is equal to false.boolean

test
  false.boolean
  and true.boolean
  is equal to false.boolean

test
  true.boolean
  and false.boolean
  is equal to false.boolean

test
  true.boolean
  and true.boolean
  is equal to true.boolean

define
  any.boolean
  or any.boolean
  does bind
    boolean
    switch
      false be or.boolean
      true be true.boolean

test
  false.boolean
  or false.boolean
  is equal to false.boolean

test
  false.boolean
  or true.boolean
  is equal to true.boolean

test
  true.boolean
  or false.boolean
  is equal to true.boolean

test
  true.boolean
  and true.boolean
  is equal to true.boolean
