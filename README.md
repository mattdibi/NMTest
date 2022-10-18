# Test app for NetworkManager dbus-java integration

## Requirements

### Prerequisites

Before installing Kura, you need to have the following programs installed in your system
* JDK 1.8
* Maven 3.5.x

#### Installing Prerequisites in Mac OS 

To install Java 8, download the JDK tar archive from the [Adoptium Project Repository](https://adoptium.net/releases.html?variant=openjdk8&jvmVariant=hotspot).

Once downloaded, copy the tar archive in `/Library/Java/JavaVirtualMachines/` and cd into it. Unpack the archive with the following command:

```bash
sudo tar -xzf <archive-name>.tar.gz
```
The tar archive can be deleted afterwards.

Depending on which terminal you are using, edit the profiles (.zshrc, .profile, .bash_profile) to contain:

```bash
# Adoptium JDK 8
export JAVA_8_HOME=/Library/Java/JavaVirtualMachines/<archive-name>/Contents/Home
alias java8='export JAVA_HOME=$JAVA_8_HOME'
java8 
```

Reload the terminal and run `java -version` to make sure it is installed correctly.

Using [Brew](https://brew.sh/) you can easily install Maven from the command line:

```bash
brew install maven@3.5
```

Run `mvn -version` to ensure that Maven has been added to the PATH. If Maven cannot be found, try running `brew link maven@3.5 --force` or manually add it to your path with:

```bash
export PATH="/usr/local/opt/maven@3.5/bin:$PATH"
```

#### Installing Prerequisites in Linux

For Java

```bash
sudo apt install openjdk-8-jdk
```

For Maven you can follow the tutorial from the official [Maven](http://maven.apache.org/install.html) site. Remember that you need to install the 3.5.x version.

## Building

```bash
mvn clean package
```

## Running

> **Note**: Administration privileges might be required to apply changes on the network configuration. If so, run the following code from the `root` user.

```bash
mvn exec:java -Dexec.mainClass="org.eclipse.kura.NMTest.App"
```

For testing I’m currently running this code on a Raspberry Pi 4 with Raspberry Pi OS 32 bit using NetworkManager.

_Note_: The [latest release of Raspberry Pi OS](https://www.raspberrypi.com/news/the-latest-update-to-raspberry-pi-os/) now offers Network Manager as an option.

To switch to NetworkManager, just open a terminal window and type:

```bash
sudo raspi-config
```

This launches the configuration tool. Go into option 6, Advanced Options, and then into option AA, Network Config – choose option 2, NetworkManager, and then reboot when prompted.
