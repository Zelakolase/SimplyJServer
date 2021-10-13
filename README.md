# SimplyJServer
HTTP Server Model made in java

## Features
* Fast : SimplyJServer is 40%-60% faster than Apache, due to it's simplicity.
* Simple to implement : You can use pure java to make your own web application, just override the engine as written in `main.java` and start making rockets!
* No (D)DoS : You can set a limit for the HTTP request size to avoid memory combustion by one thread, and also the Maximum concurrent requests to process.
* GZIP : Wait, it's not a feature. But yea it exists.

## Settings.conf
* mode : values are **static** or **dynamic**. For Static mode, SimplyJServer gets the requested path and returns the content. For Dynamic mode, the request passes through an abstract function, your role is to return a response.
* port : the http port
* mcr : (M)aximum (C)oncurrent (R)equests is a limit for the number of running threads, in other words, the number of http requests that can be processed at the same time.
* gzip : values are **0** for no gzip compression and **1** for gzip compression.
* mrs : (M)aximum (R)equest (S)ize is a limit for the http requests, how many kilobytes should be read. Adjust this value depending on the nature of your web application, the physical limit is 1.2TB.

## How to make a web app?
See `main.java`, then execute `$ javac main.java` and then `$ sudo java main`

## TO-DO
* Allow POST bodies to be stored.
