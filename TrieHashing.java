import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrieHashing {

    private TrieNode root; // The root of the TrieHashing structure
    private static int numOfWords = 0; // Counter for the total number of words

    public TrieHashing() {
        this.root = new TrieNode(); // Initialize the root node
    }

    // Inner class representing a node in the Trie
    public class TrieNode {
        private RobinHoodHashing children; // RobinHoodHashing structure for child nodes
        private int wordLength; // Indicates if this node represents a complete word
        private int importance; // Optional importance field for the node

        public TrieNode() {
            this.children = new RobinHoodHashing(); // Initialize RobinHoodHashing for child nodes
            this.wordLength = 0; // Default to 0, indicating no complete word
            this.importance = 0; // Default importance
        }
    }

    // Inner class implementing Robin Hood Hashing
    public class RobinHoodHashing {
        private class Element {
            char key; // The character key for this element
            int probeLength; // Distance from the original hash index
            TrieNode trieNode; // Reference to the associated TrieNode

            Element(char key, TrieNode trieNode) {
                this.key = key; // Assign key
                this.trieNode = trieNode; // Associate TrieNode
                this.probeLength = 0; // Default probe length
            }
        }

        private Element[] table; // The hash table
        private int capacity; // Current capacity of the table
        private int size; // Number of elements in the table
        private int maxProbeLength; // Maximum probe length observed

        public RobinHoodHashing() {
            this.capacity = 5; // Initial size of the table
            this.size = 0; // Initially empty
            this.maxProbeLength = 0; // Default maximum probe length
            this.table = new Element[capacity]; // Initialize table
        }

        private int hash(char key) {
            return key % capacity; // Hash function based on character modulo table size
        }

        private void rehash() {
            Element[] oldTable = table; // Save the old table

            // Increase capacity
            if (capacity == 5) capacity = 11;
            else if (capacity == 11) capacity = 19;
            else if (capacity == 19) capacity = 29;

            // Create a new table with increased capacity
            table = new Element[capacity];
            size = 0; // Reset size
            maxProbeLength = 0; // Reset max probe length

            // Reinsert elements from the old table into the new table
            for (Element e : oldTable) {
                if (e != null) {
                    insert(e.key, e.trieNode);
                }
            }
        }

        public void insert(char key, TrieNode trieNode) {
            if (size > 0.9 * capacity) { // Rehash if load factor exceeds 90%
                rehash();
            }

            Element newElement = new Element(key, trieNode);
            int index = hash(key);
            int probe = 0;

            while (table[index] != null) {
                Element current = table[index];
                if (current.probeLength < probe) { // Apply Robin Hood Hashing
                    table[index] = newElement; // Swap elements
                    newElement = current;
                }
                index = (index + 1) % capacity; // Move to the next index
                probe++;
            }

            table[index] = newElement; // Place the element in the table
            newElement.probeLength = probe; // Update probe length
            size++; // Increase table size
            maxProbeLength = Math.max(maxProbeLength, probe); // Update max probe length
        }

        public TrieNode search(char key) {
            int index = hash(key); // Calculate hash index
            int probe = 0;

            while (table[index] != null) {
                if (table[index].key == key) {
                    return table[index].trieNode; // Return associated TrieNode if found
                }
                index = (index + 1) % capacity; // Move to the next index
                probe++;
                if (probe > maxProbeLength) break; // Stop search if probe exceeds max probe length
            }

            return null; // Return null if not found
        }
    }

    public void insertRecursively(String word, int index) {
        insertRecursively(word.toLowerCase(), index, root); // Normalize word to lowercase
    }

    private void insertRecursively(String word, int index, TrieNode node) {
        if (index == word.length()) {
            node.wordLength = word.length(); // Mark node as a complete word
            return;
        }

        char c = word.charAt(index); // Get current character
        TrieNode child = node.children.search(c); // Search for child node
        if (child == null) {
            child = new TrieNode(); // Create new child node if not found
            node.children.insert(c, child);
        }

        insertRecursively(word, index + 1, child); // Recursively insert the next character
    }

    public void printWords() {
        printWords(root, ""); // Start printing from the root node
    }

    private void printWords(TrieNode node, String prefix) {
        if (node.wordLength > 0) {
            System.out.println(prefix); // Print the word if this node represents a complete word
        }

        for (char c = 'a'; c <= 'z'; c++) { // Traverse all possible child nodes
            TrieNode child = node.children.search(c);
            if (child != null) {
                printWords(child, prefix + c); // Recursive call with updated prefix
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
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath);
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    public int calcMem() {
        return calcMem(root);
    }

    private int calcMem(TrieNode node) {
        if (node == null) {
            return 0; // Return 0 for null nodes
        }

        int memory = 4; // Memory for wordLength (4 bytes)
        memory += calcRobinHoodMem(node.children); // Calculate memory for Robin Hood Hashing

        for (RobinHoodHashing.Element e : node.children.table) {
            if (e != null && e.trieNode != null) {
                memory += calcMem(e.trieNode); // Recursively calculate memory for child nodes
            }
        }

        return memory; // Return total memory usage
    }

    private int calcRobinHoodMem(RobinHoodHashing robinHoodHashing) {
        if (robinHoodHashing == null || robinHoodHashing.table == null) {
            return 0; // Return 0 for null hashing tables
        }

        int memory = 12; // Memory for metadata (capacity, size, maxProbeLength)
        memory += robinHoodHashing.capacity * 16; // Estimate for table entries (16 bytes per entry)

        for (RobinHoodHashing.Element e : robinHoodHashing.table) {
            memory += calcElementMem(e); // Add memory for each element
        }

        return memory; // Return total memory for Robin Hood Hashing
    }

    private int calcElementMem(RobinHoodHashing.Element e) {
        return e != null ? 2 /* char */ + 4 /* probeLength */ + 8 /* trieNode reference */ : 0;
    }

    public static void main(String[] args) {
        String[] dictionaryFiles = {
                "dictionary1.txt",
                "dictionary2.txt",
                "dictionary3.txt",
                "dictionary4.txt",
                "dictionary5.txt",
                "dictionary6.txt"
        };

        for (String fileName : dictionaryFiles) {
            TrieHashing trieHashing = new TrieHashing();
            trieHashing.loadFile(fileName); // Load words from file into Trie
            System.out.println(trieHashing.calcMem()); // Print memory usage
        }
    }
}
