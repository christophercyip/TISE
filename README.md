TISE
====

This program finds a specified energy level (n) for a handful of potential wells, or will otherwise say that such an energy level does not exist.  The wells included are a "paddleball" potential (one-sided harmonic oscillator with an infinite wall for x > 0), a harmonic oscillator potential, a finite square well, a sloped finite square well ("bounded ramp"), and an uneven square well which has two distinct lower energies within its walls.

What the program will display is the particle energy (for an electron, precise to machine precision), the wavefunction itself, and the probability distribution of the particle.  It will also show dotted lines on all graphs to indicate where the particle energy equals the potential.

The way the program finds the specified state is essentially via a binary search.  It finds a suitable range of energies by starting with the interval [0, 1 eV] and successively doubling the upper bound until the desired state is within the interval.

The math behind all the calculations is as follows:
- Analytically, the Time-Independent Schrodinger Equation was expanded, by use of the definition of the derivative, to write f(x+dx) in terms of f(x) and f(x-dx).
- This recurrence relation was then used to calculate an array representing the function, where the initial conditions were f[0] = 0 and f[1] = 1e-15. (Arbitrary small number; normalization is done later.)
- The binary search is performed by calculating how many "bumps" (f' = 0 and sign(-f) = sign(f'')) the middle point has.  If is has too many, the middle point is too large.  Similarly, too few means the middle is too small.  If the "bump count" is correct, the parity needs to be checked.  If n is even and the middle diverges to +inf or n is odd and it diverges to -inf, then the energy is again too large.  Otherwise, it is too small.
- After finding the energy, the program will trim the diverging right end of the function and then rescale the array so its maximum is 1.  (This is where the program will find "impossible" states.  If n is too large, the entire array will be trimmed and the submethod will run off the end of the array, and then pass the exception upwards.)

This program was created as a final project for my Intro. to Modern Physics class (PHYS272 @ University of San Diego).
