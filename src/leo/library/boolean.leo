let
  any boolean
  be boolean
    or true
    or false

test
  boolean true
  is matching any boolean

test
  boolean false
  is matching any boolean

let
  any boolean
  negate
  do
    boolean
    select
      true doing boolean false
      false doing boolean true

test
  boolean false
  negate
  is equal to boolean true

test
  boolean true
  negate
  is equal to boolean false

let
  any boolean
  and any boolean
  do
    boolean
    select
      true doing and.boolean
      false doing boolean false

test
  boolean false
  and boolean false
  is equal to boolean false

test
  boolean false
  and boolean true
  is equal to boolean false

test
  boolean true
  and boolean false
  is equal to boolean false

test
  boolean true
  and boolean true
  is equal to boolean true
