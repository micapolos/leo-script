let
  any id
  do id
    either any number
    either any text
    either secret

id secret
switch
  number doing
    number
    plus 1
  text doing
    text
    length
    number
  secret doing 0
