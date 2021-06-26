use library list

let
  any.list.text
  do
    ""
    fold
      text.list.reverse
      doing
        text
        append other.text

test
  list
    ".foo"
    ".bar"
    ".zoo"
  text
  is equal to ".foo.bar.zoo"
