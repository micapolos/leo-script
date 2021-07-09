let
  any circle
  do circle radius any number

let
  any rectangle
  do rectangle
    width any number
    height any number

let
  any triangle
  do triangle
    base any number
    height any number

let
  any shape
  do shape
    either any circle
    either any rectangle
    either any triangle

shape
  drop any rectangle
  drop any triangle
  pick circle radius 10
type
