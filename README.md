# basicauth
Authentication is the process of determining whether someone or something is, in fact, who or what it is declared to be. Nowadays, the majority of applications require strong authentication procedures. The current implementation attempts to achieve a strong authentication background by using a hash algorithm. This algorithm is **PBKDF2WithHmacSHA1** (Password-Based Key Derivation Function 2), reinforced by a strong salt and a big number of iterations.

The concept of the project is the authentication process between a client - server application using Java remote method invocation or “Java RMI”. The server is actually a print server installed in a small company, which is able to support various operations like print(), start(), stop() etc. The full documentation can be found [here](https://drive.google.com/open?id=0B08EQYpU7wnCaVRzcnotVFlJZ0U). It is written in Java using the Eclipse environment,

Thanks to [Taylor Hornby](http://crackstation.net/hashing-security.htm) for the nice __implementation of Password Hashing With PBKDF2__.

Check out the implementation of access controls in the repository [basicac](https://github.com/dimitrisdan/basicac)
