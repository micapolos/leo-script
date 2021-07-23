debug compiler
  block
    module
      context
        environment native
        scope binding list
          binding constant
            height
            being height number native kotlin double
          binding constant
            width
            being width number native kotlin double
      types block option absent
    typed list
      binding
        type height
        compiled
          expression tuple field
            name height
            compiled
              expression tuple native double 20
              type number native kotlin double
          type height number native kotlin double
      binding
        type width
        compiled
          expression tuple field
            name width
            compiled
              expression tuple native double 10
              type number native kotlin double
          type width number native kotlin double
  compiled
    expression
      compiled
        expression tuple
          get
            compiled
              expression content compiled
                expression variable type width
                type width number native kotlin double
              type number native kotlin double
            name number
          field
            name times
            compiled
              expression tuple get
                compiled
                  expression content compiled
                    expression variable type height
                    type height number native kotlin double
                  type number native kotlin double
                name number
              type number native kotlin double
        type
          number native kotlin double
          times number native kotlin double
      apply compiled
        expression tuple native
          double
          times double
        type function
          number native kotlin double
          times number native kotlin double
          doing number native kotlin double
    type number native kotlin double
