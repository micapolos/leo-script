let
  jabłko.opis
  be "piękne, czerwone jabłko"

let
  gruszka.opis
  be "dorodna, słodziutka gruszka"

let
  owoc any
  opis
  do
    "posiadasz jeden owoc: "
    append
      owoc
      switch
        jabłko doing jabłko.opis
        gruszka doing gruszka.opis

jabłko.owoc.opis
