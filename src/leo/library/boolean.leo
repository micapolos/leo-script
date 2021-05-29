let
  boolean any
  negate
  do
    boolean
    switch
      false doing true.boolean
      true doing false.boolean

test
  false.boolean.negate
  is equal true.boolean

test
  true.boolean.negate
  is equal false.boolean

let
  boolean any
  and boolean any
  do
    boolean
    switch
      true doing and.boolean
      false doing false.boolean

test
  false.boolean
  and false.boolean
  is equal false.boolean

test
  false.boolean
  and true.boolean
  is equal false.boolean

test
  true.boolean
  and false.boolean
  is equal false.boolean

test
  true.boolean
  and true.boolean
  is equal true.boolean

let
  boolean any
  or boolean any
  do
    boolean
    switch
      false doing or.boolean
      true doing true.boolean

test
  false.boolean
  or false.boolean
  is equal false.boolean

test
  false.boolean
  or true.boolean
  is equal true.boolean

test
  true.boolean
  or false.boolean
  is equal true.boolean

test
  true.boolean
  and true.boolean
  is equal true.boolean
