# HW3 EPL 231 Dictionary Project

## Description
The EPL 231 Dictionary Project is a Java-based application that implements an efficient dictionary system. It uses a Trie data structure and Robin Hood Hashing for optimized storage, fast search, and memory analysis. The project also includes a utility to generate random dictionary files for testing.

---

## Authors
- **Constantinos Theophilou** 
- **Marinos Antoniou** 

---

## Bugs
- **None reported so far, but memory optimization is ongoing.**

---

## Features
- Reads dictionary data from files and stores them in a Trie structure.
- Uses Robin Hood Hashing to optimize child node management in the Trie.
- Supports dynamic word generation:
  - Fixed-length words.
  - Log-normal distributed word lengths for realistic data simulation.
- Calculates memory usage of the Trie and hashing implementation.
- Outputs loaded dictionary statistics and memory usage.

---

## Files in the Project
- **`Trie.java`**: Implements the basic Trie for word storage and retrieval.
- **`TrieHashing.java`**: Extends the Trie with Robin Hood Hashing for child nodes.
- **`randomWordGenerator.java`**: Generates random dictionary files for testing.
- **`dictionary1.txt` - `dictionary6.txt`**: Example dictionary files generated for testing.

---

## Closing Thoughts About the Project
The project was challenging but rewarding. Implementing Robin Hood Hashing in a Trie structure and optimizing memory usage were particularly engaging tasks. Generating realistic test data with log-normal word lengths added a practical touch. Debugging memory calculation methods was tricky but manageable.
