30
fibonacci
do repeating
  number
  equals 0
  switch
    yes 0
    no
      number
      equals 1
      switch
        yes 1
        no
          number
          minus 2
          fibonacci
          plus
            number
            minus 1
            fibonacci
