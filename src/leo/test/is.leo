comment 
  is
    does nothing by default

test
  foo
  is bar
  is equal
    foo
    is bar

test
  foo
  is not bar
  is equal
    foo
    is not bar

let
  text any
  is greeting
  do
    text
    is equal "Hello, world!"

test
  "Hello, world!"
  is greeting
  is equal is yes

test
  "Hello, hell!"
  is greeting
  is equal is no

test
  "Hello, world!"
  is not greeting
  is equal is no

test
  "Hello, hell!"
  is not greeting
  is equal is yes
