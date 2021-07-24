python "import math;Z=lambda f:(lambda g:f(g(g)))(lambda g:f(lambda *y:g(g)(*y)));Z(lambda v0:(lambda v1:(v1 if (v1<2) else (v0((v1-2))+v0((v1-1))))))(30)"
