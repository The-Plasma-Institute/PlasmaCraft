# Project setup

## Java JDK
Firstly you'll need to install Java version `17` of the Java Development Kit (JDK). This will allow you to compile and run the code, and it's important you download the correct version.

Once downloaded make sure that the Java HOME path gets set correctly within the installer.

### Windows and macOS
Download the x64 file for your operating system from https://adoptium.net/en-GB/temurin/releases/?os=any&version=17

> [!TIP]
> If installing on windows you'll need to restart your computer for changes to take effect

### Linux
Open the terminal and type the following:

`sudo apt install openjdk-17-jdk`

Then type the following and check that it says `17`:

`java -version`

Then navigate to the `Software` application and install intelliJ

## IDE
Download the Jetbrains [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) IDE. 

> [!TIP]
> Since we're students we have access to the IntelliJ IDEA Ultimate edition, you'll just need to create an account and specify you're a student. Alternatively you can use the IntelliJ IDEA Community Edition

## Testing your progress
Now that you've installed both of these create a new project specifying the java version to be `temurin-17...`. Once the project is created attempt to run the code in the top right with the green play button.

> [!NOTE]
> If you're struggling to install and get setup then follow this video https://youtu.be/G1ifRRtJm7w?si=SeZ4O0d0OmhYbLmT


## Installing the code
Firstly install the code into a folder of your choosing:

`git clone https://github.com/JoelLucaAdams/PlasmaCraft.git`

Once installed open the folder from IntelliJ and check that SDK version is set to 17. To do this press the `ALT+;` keys or `cmd+;` on macOS.

>[!NOTE]
> To build this minecraft mod we're going to be using minecraft Forge which is a minecraft mod manager that allows us to make mods that can talk to other mods. 

## Running the code
On the right hand side of IntelliJ you'll see an elephant icon called Gradle which will be used to run the code. There are few different commands that can be used to run the code:

> [!IMPORTANT]
> All the commands will run in a terminal at the bottom, please wait for them to finish with the words `BUILD SUCCESSFUL` before starting another one. Some of these commands take a good minute to run so please be patient

1. Open the `Tasks -> forgegradle runs` folder
2. Run `genIntellijRuns` - This only needs to be run whenever we add a new mod to our list otherwise just run it once
3. Run `runData` - This generates the minecraft textures for all the custom items we have added and needs to be run every time we change one of the textures
4. Run `runClient` - This should actually boot up minecraft and should allow you to explore and play the game.
