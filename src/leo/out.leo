python "Z=lambda f:(lambda g:f(g(g)))(lambda g:f(lambda *y:g(g)(*y)));(lambda v0:v0(30))(Z(lambda v0:(lambda v1:(v1 if (v1<2) else (v0((v1-2))+v0((v1-1)))))))"
