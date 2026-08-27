# ⚙️ Machine Forge — Automata Designer & Simulator

> **Design. Simulate. Convert. Minimize. Understand Automata.**

**Machine Forge** is a Java-based **Automata Designer and Simulator** built to make the study and implementation of **Theory of Computation and Formal Languages** more interactive and practical.

The project provides tools for working with different types of finite-state machines, allowing users to simulate automata, convert between representations, minimize DFAs, and explore the behavior of **Mealy and Moore machines**.

---

## ✨ Features

### 🔵 NFA Simulator

Simulate a **Nondeterministic Finite Automaton (NFA)** by providing:

* States
* Input alphabet
* Transition functions
* Initial state
* Final states
* Input strings

The simulator determines whether a given string is **accepted or rejected** by the NFA.

---

### 🟢 NFA → DFA Conversion

Machine Forge implements the **Subset Construction Algorithm** to convert an NFA into an equivalent DFA.

```text
        NFA
         │
         │ Subset Construction
         ▼
        DFA
```

The generated DFA preserves the language recognized by the original NFA.

---

### 🟣 DFA Minimization

The project includes a **DFA Minimizer** that reduces a DFA to an equivalent DFA with the minimum possible number of states.

The minimization process helps demonstrate concepts such as:

* Equivalent states
* Distinguishable states
* State partitioning
* Reduced automata

---

### 🟠 Mealy Machine

Machine Forge supports **Mealy Machines**, where the output depends on:

```text
Current State + Input
          ↓
       Output
```

The project provides functionality for defining and working with Mealy machine transitions and outputs.

---

### 🔴 Moore Machine

Machine Forge also supports **Moore Machines**, where the output depends only on the current state.

```text
Current State
      ↓
    Output
```

This allows users to experiment with the differences between **Mealy and Moore models**.

---


## 🏗️ Project Structure

```text
MachineForge-AutomataDesignerAndSimulator/
│
├── MainMenu.java
│
├── NfaSimulator.java
│
├── NfaToDfaSimulator.java
│
├── DFAMinimizerUI.java
│
├── MealyMachineFunction.java
│
└── MooreMachineFunction.java
```

### Main Components

#### `MainMenu.java`

Acts as the entry point and provides access to the different Machine Forge modules.

#### `NfaSimulator.java`

Handles NFA simulation and string acceptance testing.

#### `NfaToDfaSimulator.java`

Implements NFA-to-DFA conversion using the **subset construction approach**.

#### `DFAMinimizerUI.java`

Provides the interface for DFA minimization.

#### `MealyMachineFunction.java`

Handles Mealy machine functionality and transition/output processing.

#### `MooreMachineFunction.java`

Handles Moore machine functionality and state-based output processing.

---

## 🔄 How Machine Forge Works

The overall workflow can be represented as:

```text
                    ┌─────────────────┐
                    │   Machine Forge │
                    │      Menu       │
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
          ▼                  ▼                  ▼
     NFA Simulator      DFA Minimizer      Mealy/Moore
          │                  │              Machines
          │                  │
          ▼                  │
      NFA → DFA              │
      Conversion             │
          │                  │
          └──────────┬───────┘
                     ▼
              Automata Analysis
```

---

## 🧪 Example

Consider an NFA:

```text
States: {q0, q1, q2}

Alphabet: {0, 1}

Initial State: q0

Final State: q2
```

The user can enter an input string such as:

```text
10101
```

Machine Forge processes the string through the automaton and produces the corresponding result:

```text
Input: 10101

Result: ACCEPTED
```

or

```text
Input: 11100

Result: REJECTED
```

---

## 🔁 NFA to DFA

For an NFA with:

```text
Q = {q0, q1, q2}
```

the subset construction algorithm creates DFA states representing **sets of NFA states**.

For example:

```text
NFA State Set

{q0}
{q0,q1}
{q1,q2}
{q2}
```

These sets become states in the equivalent DFA.

```text
NFA
 │
 │ Subset Construction
 ▼
Equivalent DFA
```

---

## ✂️ DFA Minimization

Machine Forge can minimize a DFA by identifying states that are equivalent and merging them.

Conceptually:

```text
Original DFA

q0 ──► q1
│      │
│      ▼
└────► q2


        ↓
   Minimization
        ↓

Reduced DFA

q0 ──► [q1,q2]
```

This demonstrates how a finite automaton can be simplified while preserving its language.

---

## 🛠️ Technologies

* **Java**
* Object-Oriented Programming
* Data Structures
* Graph-based state representation
* Finite Automata Algorithms
* Java UI components

---

## 💻 Requirements

To run Machine Forge, you need:

* **Java JDK 8 or later**
* A Java-compatible IDE or terminal

Recommended:

* IntelliJ IDEA
* Eclipse
* VS Code with Java extensions
* NetBeans

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Dipanshu0001-OP/MachineForge-AutomataDesignerAndSimulator.git
```

### 2. Enter the Project

```bash
cd MachineForge-AutomataDesignerAndSimulator
```

### 3. Compile

```bash
javac *.java
```

### 4. Run

```bash
java MainMenu
```

Alternatively, open the project in your preferred Java IDE and run:

```text
MainMenu.java
```

---

## 🎯 Project Objectives

Machine Forge was developed to bridge the gap between **theoretical concepts and practical implementation** in Theory of Computation.

The main objectives are:

* Make automata concepts easier to understand
* Provide an interactive environment for automata simulation
* Implement important automata algorithms
* Demonstrate NFA and DFA equivalence
* Visualize the idea of DFA minimization through implementation
* Provide practical implementations of Mealy and Moore machines
* Create a foundation for a more complete automata-design platform

---

## 🔮 Future Roadmap

Machine Forge is designed to grow into a more complete **Automata Design and Analysis Platform**.

### Planned Features

* [ ] Interactive state/transition designer
* [ ] Graphical automata visualization
* [ ] DFA simulator
* [ ] Regular Expression → NFA
* [ ] Regular Expression → DFA
* [ ] DFA/NFA → Regular Expression
* [ ] ε-NFA support
* [ ] PDA simulator
* [ ] Turing Machine simulator
* [ ] Grammar simulator
* [ ] CFG tools
* [ ] Step-by-step transition visualization
* [ ] Automata import/export
* [ ] Improved modern UI
* [ ] Automated test-string generation

---

## 📚 Educational Value

Machine Forge can be used to practically explore topics from **Theory of Computation**, including:

```text
Finite Automata
      │
      ├── DFA
      ├── NFA
      ├── ε-NFA
      │
      ├── NFA → DFA
      │
      ├── DFA Minimization
      │
      ├── Mealy Machine
      └── Moore Machine
```

Instead of only studying transition tables and algorithms theoretically, users can interact with their implementations and observe their behavior.

---

## 🤝 Contributing

Contributions are welcome!

If you want to improve Machine Forge:

```bash
# Fork the repository

# Clone your fork
git clone <your-fork-url>

# Create a feature branch
git checkout -b feature/new-feature

# Make your changes

# Commit
git add .
git commit -m "Add new automata feature"

# Push
git push origin feature/new-feature
```

Then open a Pull Request.

---

## 👨‍💻 Author

**Dipanshu Tandon**

GitHub:
https://github.com/Dipanshu0001-OP

---

## Machine Forge

> **From abstract state machines to executable automata.**

**Design → Simulate → Convert → Minimize → Understand**

⭐ If you find Machine Forge useful, consider starring the repository!
