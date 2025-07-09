
# 🤖 Robot World Project — Team JHB29 🌍

## 🚀 A Java-Based Multi-Client Robot Simulation

---

## 📜 Table of Contents:

* [📖 Project Description](#-project-description)
* [🛠️ Client/Server Architecture](#-clientserver-architecture)
* [🎨 Server GUI Features](#-server-gui-features)
* [📋 Completed Features](#-completed-features)
* [📋 Outstanding Tasks](#-outstanding-tasks)
* [🔮 Future Improvements](#-future-improvements)
* [👥 Team Members](#-team-members)
* [💻 How to Run](#-how-to-run)

---

## 📖 Project Description

**Robot World** is a Java-based multiplayer robot simulation game where users control robots in a shared 2D world through a CLI client. The server handles the logic, world state, and now includes a Swing-based **graphical interface** for live visual feedback on robot activity and combat.

---

## 🛠️ Client/Server Architecture

### 🖥️ Server:

* Multi-threaded TCP server managing concurrent robot clients
* Generates a 2D map with random obstacles
* Handles game logic: movement, turns, shooting, damage, and repairs
* Hosts a real-time **Swing GUI** to visually represent the robot world

### 🤝 Client:

* Text-based CLI that sends JSON-formatted commands to the server
* Handles full input-output lifecycle from the user to the server
* Displays parsed responses (state, status, damage, errors, etc.)

### 🔄 Communication Protocol:

* All communication is done via **JSON messages** over TCP sockets
* Structure example:

```json
{
  "robot": "hal",
  "command": "forward",
  "arguments": { "steps": 5 }
}
````

---

## 🎨 Server GUI Features

* **Real-time grid** showing robots and obstacles
* **Dynamic updates** with every client action
* Built using **Java Swing**

> Note: Only the server GUI is graphical. Each client runs in its own terminal using a CLI interface.

---

## 📋 Completed Features

- ✅ Fully functional server-side game engine
- ✅ CLI-based client for each user
- ✅ JSON message protocol
- ✅ Multi-client support with concurrency
- ✅ Server GUI for visualizing robot world
- ✅ Combat system with `fire`, `repair`, `reload`
- ✅ Edge handling (map bounds, obstacles, collisions)
- ✅ World persistence during sessions

---

## 📋 Outstanding Tasks

* GUI-based client was not implemented due to time constraints
* Limited fault tolerance testing under high concurrency

---

## 🔮 Future Improvements

* 🧪 Improve exception handling and test coverage
* 🎨 Implement GUI-based clients (JavaFX or Swing)
* 💬 WebSocket support for modern frontends

---

## 💻 How to Run

### 🛠️ Requirements:

* ☕ [Java 21+](https://www.oracle.com/java/technologies/javase-downloads.html)
* 🐘 [Apache Maven 3.8+](https://maven.apache.org/download.cgi)

---

### 📥 Clone the Repository:

```bash
git clone git@gitlab.wethinkco.de:haazizjhb024/oop-ex-toy-robot-group-29-2025.git
cd oop-ex-toy-robot-group-29-2025
```

---

### 🔨 Build the Project:

```bash
mvn clean install
```

---

### 🚀 Run the Server:

```bash
mvn exec:java -Dexec.mainClass="za.co.wethinkcode.robots.server.MultiServers"
```

---

### Run a Client (in a new terminal):

```bash
mvn exec:java -Dexec.mainClass="za.co.wethinkcode.robots.client.Client"
```

---

### 💬 Example Client Commands:

```
launch robot hal
forward 10
back 10
turn right
look
state
fire
repair
reload
```

---

### 🛠️ Example Server Console Commands:

```
robots
dump
quit
```

---

## 📌 Notes

* The server must be started **before any clients connect**
* Server GUI opens automatically — no extra input required
* Each client should be run in its own terminal session

---
