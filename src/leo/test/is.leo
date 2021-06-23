comment 
  is
    does nothing by default

test
  foo
  is bar
  is equal to
    foo
    is bar

test
  foo
  is not bar
  is equal to
    foo
    is not bar

let
  text any
  is greeting
  do bind
    text
    is equal to "Hello, world!"

test
  "Hello, world!"
  is greeting
  is equal to is yes

test
  "Hello, hell!"
  is greeting
  is equal to is no

test
  "Hello, world!"
  is not greeting
  is equal to is no

test
  "Hello, hell!"
  is not greeting
  is equal to is yes
