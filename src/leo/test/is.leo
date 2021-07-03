comment 
  is
    does nothing by itself
  it can be used in combination with
    equal to
    matching
  it is expected to evaluate to one of
    is yes
    is no
  when used in combination with not
    it negates the given value

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
  any text
  is greeting
  do
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
