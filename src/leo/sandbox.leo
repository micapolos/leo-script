let
  any.point
  squared distance to any.point
  do bind
    point.x.number
    minus squared.distance.to.point.x.number
    bind
      number
      times number
    plus
      point.y.number
      minus squared.distance.to.point.y.number
      bind
        number
        times number

let
  my.point.list
  be list
    point
      x 10
      y 20
    point
      x 20
      y 30
    point
      x 40
      y 30

let
  reference.point
  be point
    x 30
    y 60

my.point.list
take doing squared distance to reference.point
