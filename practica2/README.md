El repositorio de git está en:
https://github.com/tgs1003/Sistemas_distribuidos

*practica2* corresponde a la práctica con sockets EchoSocket con Multihilo y blacklist.

Ejecutar desde la linea de comandos en el raiz del proyecto.
Para el servidor con:

mvn exec:java@server

Y para el cliente con:

mvn exec:java@client

Si el puerto de origen del cliente es uno de los que está en la lista negra, devolverá una exceptión.

La lista negra es:
    37110,
    37220,
    37330,
    37440,
    37550,
    37660,
    37770,
    37880,
    37900