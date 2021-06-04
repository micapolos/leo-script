use library list

let
  any.list.text
  do bind
    ""
    fold
      text.list.reverse
      doing bind
        text
        append other.text

test
  list
    ".foo"
    ".bar"
    ".zoo"
  text
  is equal ".foo.bar.zoo"
