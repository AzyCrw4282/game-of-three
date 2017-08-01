[![Build Status](https://travis-ci.org/hishamaborob/game-of-three-hisham.svg?branch=master)](https://travis-ci.org/hishamaborob/game-of-three-hisham)

https://game-of-three-hisham.herokuapp.com/

# Overview

This a game played through WebSockets. Game of Three, when a player starts, he is added to a queue to wait for a rival, once a rival player is available, either of them can incept a random (whole) number and sends it to the second a player as an approach of starting the game.
The receiving player can now always choose between adding one of  {­1, 0, 1} to get to a number that is divisible by 3. Divide it by three. The resulting whole number is then sent back to the original sender.

# Design

- This app built Spring Boot, Java 8, WebSockets.
- It demonstrates Java concurrency, thread safe, sessions and locking.
- As a backend developer, HTML, JS, and CSS is very basic and reused from another project with some minor modifications.
- This app is stateful, it uses the browser session id after page load.
- Sessions are added to a queue so they can wait for other sessions.
- Game states are also stored to maintained locking, waiting, and game state.
- When the game ends, sessions are simply cleared.
- What is missing or good to have feature, is a scheduler to check expired sessions in the queue and check also for disconnection events.

### Testing

This app have a CI pipeline using Github hooks, Travis-CI, and Heroku host for deployment:

https://travis-ci.org/hishamaborob/game-of-three-hisham
https://game-of-three-hisham.herokuapp.com/

Application can be built with: 
```sh
$ mvn clean install
```
Run the JAR with:
```sh
java -jar target/game-of-three-0.1.0.jar
```
Test on two browsers: http://localhost:8080 or https://game-of-three-hisham.herokuapp.com/
