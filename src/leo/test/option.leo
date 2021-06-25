comment
  option contains one of
    absent
    present value
  option
    is a monad
    and will be lifted automatically
  todo
    implement a way
    to define custom monads

test
  10
  plus 20
  is equal to 30

test
  10
  plus 20.present.option
  is equal to 30.present.option

test
  10
  plus absent.option
  is equal to absent.option

test
  10.present.option
  plus 20
  is equal to 30.present.option

test
  10.present.option
  plus 20.present.option
  is equal to 30.present.option

test
  10.present.option
  plus absent.option
  is equal to absent.option

test
  absent.option
  plus 20
  is equal to absent.option

test
  absent.option
  plus 20.present.option
  is equal to absent.option

test
  absent.option
  plus absent.option
  is equal to absent.option
