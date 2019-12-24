# 2006Rebotted 2.0 Client - an open source, bottable remake server. Pull requests welcome

## Discord Link: https://discord.gg/4zrA2Wy

If you want to play the server, check out [2006rebotted.tk](https://2006rebotted.tk/)

# How to Develop 2006Rebotted 2.0 Client

Step 1: Register a GitHub account

Step 2 (Windows users): Install Git Bash: https://git-scm.com/downloads

Step 3: Install IntelliJ Community Edition: https://www.jetbrains.com/idea/download/

Step 4: Install Java 8 from https://adoptopenjdk.net

Step 4: [Fork this repository](https://i.imgur.com/PoMTxZj.png)

Step 5: Open Git Bash and type `git clone <YOUR_FORK_URL>` ([Example](https://i.imgur.com/Hs1upNf.png)) 

Step 6: In Git Bash, type `cd 2006RebottedClient`

Step 7: In Git Bash, type `git remote add upstream https://github.com/dginovker/2006RebottedClient`

Step 8: In Git Bash, type `git checkout -b my-development`

Step 9: Open IntelliJ and click "Open" on the Right-Hand panel

Step 10: Find where you "Cloned" the code to in Git Bash. If you can't find it, type `pwd` in Git Bash to help ([Image](https://i.imgur.com/YvVFtmW.png))

Step 11: Click this button if your code structure is not visible: https://i.imgur.com/bxXvoKv.png

Step 12: Click File -> Project Structure

Step 13: Set Project SDK to 1.8, Project Language Level to 8, & Project Compiler Output to any valid folder [Image](https://i.imgur.com/9PJDk0Q.png)

Step 14: Hit OK in the project structure screen 

Step 15: Right click `pom.xml`, select `Maven` and click `Reimport` ([Image](https://i.imgur.com/FhT025V.png))

Step 16: Click "Run Core" [Image](https://i.imgur.com/c8hxMx0.png)
