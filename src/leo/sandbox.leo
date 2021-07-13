((letrec 
  ((fib 
    (lambda (fib) 
      (lambda (n)
          (if (< n 2) 
            n 
            (+ (fib (- n 2)) (fib (- n 1))))))))
  (fib fib)) 10)