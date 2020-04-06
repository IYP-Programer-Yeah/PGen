# PGen

A **simple to use** graphical LL(1) parser generator


## Getting Started

This project uses Java, hence to run it, you machine should be Java enabled. To compile this project you are going to need maven, however, if you don't want to compile from source, there are releases you can use.

### Prerequisites

Following prerequisites are required, depending on your usage.

#### Java

To execute this project you need Java JDK. This program written with JavaFX. JavaFX removed after version 8 of java so
use only java 8 for execution of program. If you use linux, `openjdk-8-jdk` removed JavaFX and you must use oracle version of java.

#### Maven

You are going to need Maven only if you are compiling from source. You can install Maven using the following command if you are on a Linux machine.

```
sudo apt install maven
```

### Compilation

You can compile the code via the following command.

```
mvn package
```

The result of compilation is a `.jar` file, that you can find in the `target` director. The file is going to be named `PGen-*.*-jar-with-dependencies.jar` where the `*.*` is going to be the version of the binary.

### Execution

You can run the executable with the following command.

```
java -jar target/PGen-*.*-jar-with-dependencies.jar
```

### Usage

Hold <Shift> to make connections between nodes.
Right Click on edge's name for deleting or changing parameteres.

## Built With

* [GSon](https://github.com/google/gson) - The API for create JSON files
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors
* **Poya Payandeh** - [github](https://github.com/pouyapayandeh)
* **Hosein Ghahremanzadeh** - [github](https://github.com/IYP-Programer-Yeah)
* **Ali Shamakhi** - [github](https://github.com/ali-shamakhi)
* **Amin Borjian** - [github](https://github.com/Borjianamin98)

See also the list of [contributors](https://github.com/IYP-Programer-Yeah/PGen/graphs/contributors) who participated in this project.
