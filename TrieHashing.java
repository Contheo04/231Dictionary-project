//package test3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrieHashing {

    private TrieNode root; // The root of the TrieHashing structure
    private static int numOfWords = 0;

    public TrieHashing() {
        this.root = new TrieNode();
    }

    // Inner class representing a node in the Trie
    public class TrieNode {
        private RobinHoodHashing children; // RobinHoodHashing structure for storing child nodes
        private int wordLength; // Indicates if this node represents a complete word
        private int importance;

        public TrieNode() {
            this.children = new RobinHoodHashing(); // Initializing RobinHoodHashing for child nodes
            this.wordLength = 0; // Default to 0, indicating no complete word
            this.importance = 0;
        }
    }

    // Inner class implementing Robin Hood Hashing
    public class RobinHoodHashing {
        private class Element {
            char key; // The character key for this element
            int probeLength; // Distance from the original hash index
            TrieNode trieNode; // Reference to the associated TrieNode

            Element(char key, TrieNode trieNode) {
                this.key = key;
                this.trieNode = trieNode;
                this.probeLength = 0;
            }
        }

        private Element[] table; // The hash table
        private int capacity; // Current capacity of the table
        private int size; // Number of elements in the table
        private int maxProbeLength; // Maximum probe length observed

        public RobinHoodHashing() {
            this.capacity = 5; // Initial size of the table
            this.size = 0; // Initially empty
            this.maxProbeLength = 0;
            this.table = new Element[capacity];
        }

        private int hash(char key) {
            return key % capacity; // Hash function based on the ASCII value of the character modulo the capacity
        }

        private void rehash() {
            Element[] oldTable = table;

            // Update the capacity based on the current value
            if (capacity == 5)
                capacity = 11;
            else if (capacity == 11)
                capacity = 19;
            else if (capacity == 19)
                capacity = 29;

            // Create a new hash table with the updated capacity
            table = new Element[capacity];
            size = 0; // Reset size
            maxProbeLength = 0; // Reset max probe length

            // Reinsert all elements into the new table
            for (Element e : oldTable) {
                if (e != null) {
                    insert(e.key, e.trieNode); // Rehash and reinsert elements
                }
            }
        }

        public void insert(char key, TrieNode trieNode) {
            if (size > 0.9 * capacity) { // If the size surpasses the 90% of the capacity rehash
                rehash();
            }

            Element newElement = new Element(key, trieNode);
            int index = hash(key);
            int probe = 0;

            while (table[index] != null) {
                Element current = table[index];
                // Robin Hood technique to check the current probe with all the other probes
                if (current.probeLength < probe) {
                    table[index] = newElement; // Switch elements if needed
                    newElement = current;
                }
                index = (index + 1) % capacity;
                probe++;
            }

            table[index] = newElement;
            newElement.probeLength = probe;
            size++;
            maxProbeLength = Math.max(maxProbeLength, probe); // Update the max probe length
        }

        public TrieNode search(char key) {
            int index = hash(key);
            int probe = 0;

            while (table[index] != null) {
                if (table[index].key == key) {
                    return table[index].trieNode;
                }
                index = (index + 1) % capacity;
                probe++;
                if (probe > maxProbeLength)
                    break; // Break early if probe exceeds max probe length
            }

            return null;
        }
    }

    public void insertRecursively(String word, int index) {
        insertRecursively(word.toLowerCase(), index, root);
    }

    private void insertRecursively(String word, int index, TrieNode node) {
        if (index == word.length()) {
            node.wordLength = word.length(); // Mark the node as representing a complete word
            return;
        }

        char c = word.charAt(index);
        TrieNode child = node.children.search(c);
        if (child == null) {
            child = new TrieNode();
            node.children.insert(c, child);
        }

        insertRecursively(word, index + 1, child);
    }

    public void printWords() {
        printWords(root, "");
    }

    private void printWords(TrieNode node, String prefix) {
        if (node.wordLength > 0) {
            System.out.println(prefix);
        }

        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                printWords(child, prefix + c);
            }
        }
    }

    public void loadFile(String filePath) {
        File file = new File(filePath);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toLowerCase();
                if (!word.isEmpty()) {
                    numOfWords++;
                    insertRecursively(word, 0); // Insert each word into the Trie
                }
            }
            System.out.println("Loaded words from dictionary: " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath);
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }
    /* Just a suggestion
    public int calcMem() {
        int rootMemory = 4; // wordLength (4 bytes for root node)
        rootMemory += calcRobinHoodMem(root.children); // Add memory for RobinHoodHashing at the root level
        return rootMemory + calcMem(root); // Include recursive memory from all child nodes
    }*/


    public int calcMem() {
        return calcMem(root);
    }

    private int calcMem(TrieNode node) {
        if (node == null) {
            return 0;
        }

        int memory = 4; // wordLength (4 bytes)
        memory += calcRobinHoodMem(node.children); // Memory used by the Robin Hood Hashing structure

        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                memory += calcMem(child);
            }
        }

        return memory;
    }

    private int calcRobinHoodMem(RobinHoodHashing robinHoodHashing) {
        if (robinHoodHashing == null || robinHoodHashing.table == null) {
            return 0;
        }

        int memory = 0;

        // Memory for the hash table and metadata
        memory += robinHoodHashing.capacity * 12; // Each entry: 1 char (2 bytes) + 1 TrieNode (8 bytes) + probeLength
        memory += 12; // RobinHoodHashing metadata (capacity, size, maxProbeLength)

        return memory;
    }
    
    /* Just a suggestion
     * (private int calcRobinHoodMem(RobinHoodHashing robinHoodHashing) {
        if (robinHoodHashing == null || robinHoodHashing.table == null) {
            return 0;
        }

        int memory = 0;

        // Memory for metadata
        memory += 12; // RobinHoodHashing metadata (capacity, size, maxProbeLength)

        // Memory for each non-null entry in the table
        for (RobinHoodHashing.Element e : robinHoodHashing.table) {
            if (e != null) {
                memory += 12; // Each entry: key (2 bytes), TrieNode reference (8 bytes), probeLength (4 bytes)
                memory += calcMem(e.trieNode); // Recursively add memory for the child TrieNode
            }
        }

        return memory;
    } */


    public static void main(String[] args) {
        TrieHashing trieHashing = new TrieHashing();

        // Load words into the Trie from a dictionary file
        trieHashing.loadFile("dictionary.txt");

        // Print all words in the Trie
        System.out.println("\nWords in Trie:");
        trieHashing.printWords();

        // Memory usage calculation
        System.out.println("\nNumber of words loaded: " + numOfWords);
        System.out.println("Memory used by Trie: " + trieHashing.calcMem() + " bytes");
    }
}