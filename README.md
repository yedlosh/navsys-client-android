# navsys - Android Client

**navsys** is a thesis project, which aims to provide an indoor navigation solution with audiovisual feedback, 
using a combination of smartphone based WiFi positioning and physical navigation units. 
For a documentation of the whole project see [navsys-docs](https://github.com/yedlosh/navsys-docs) repository.

## Android Client

The user facing component of this system, which provides interface for selecting desired destination, 
and collecting WiFi fingerprints for localization needs.

### Dependencies

This repository contains an Android Studio project, which is needed for building and running it. 
Android Studio will download other dependencies itself via gradle build system.

### Building and Running
1. Clone / Download this repository
2. Import the repository into Android Studio as an existing project and select : Run > Run app

### Configuration

Users are not supposed to be able to change the configuration, which is why it is not present in the UI. 
You can change the configuration in the Constants.java file. 
The only one constant needed to change is the `NAVSYS_API_ADDR`, which states where is your instance of 
navsys-backend running.
