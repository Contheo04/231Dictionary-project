import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Trie {

    static int numOfWords = 0; // Counter for total words added to the Trie

    private TrieNode root; // Root node of the Trie

    // Constructor to initialize the Trie
    public Trie() {
        this.root = new TrieNode();
    }

    // Inner class representing a node in the Trie
    private class TrieNode {
        private TrieNode[] children; // Array to hold child nodes for each letter
        private int wordLength; // Length of the word ending at this node
        private int importance; // Importance of the word

        TrieNode() {
            children = new TrieNode[26]; // Supports lowercase letters a-z
            wordLength = 0; // Default word length
            importance = 0; // Default importance
        }
    }

    // Method to insert a word into the Trie
    public void insert(String word, int index) {
        insert(word.toLowerCase(), index, root); // Normalize word to lowercase
    }

    // Helper method for recursive word insertion
    private void insert(String word, int index, TrieNode node) {
        if (index == word.length()) { // Base case: reached end of the word
            node.wordLength = word.length(); // Mark this node as end of the word
            return;
        }

        char c = word.charAt(index); // Current character
        int position = c - 'a'; // Index in children array

        if (node.children[position] == null) {
            node.children[position] = new TrieNode(); // Create new node if not present
        }

        insert(word, index + 1, node.children[position]); // Recursive call for next character
    }

    // Method to get the importance of a word
    public int getImportance(String word) {
        TrieNode node = searchNode(word.toLowerCase(), root, 0); // Find the node for the word
        return (node != null && node.wordLength > 0) ? node.importance : 0; // Return importance if word exists
    }

    // Helper method to search for a word's node
    private TrieNode searchNode(String word, TrieNode node, int index) {
        if (node == null || index == word.length()) {
            return node; // Return the node if found or null if not
        }

        char c = word.charAt(index); // Current character
        int position = c - 'a'; // Index in children array
        return searchNode(word, node.children[position], index + 1); // Recursive search
    }

    // Method to print all words and their importance in the Trie
    public void printWords() {
        printWords(root, ""); // Start printing from root node
    }

    // Helper method for recursive word printing
    private void printWords(TrieNode node, String prefix) {
        if (node.wordLength > 0) {
            System.out.println(prefix + " (Importance: " + node.importance + ")"); // Print word and its importance
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                char nextChar = (char) (i + 'a'); // Get the character for the child node
                printWords(node.children[i], prefix + nextChar); // Recursive call with updated prefix
            }
        }
    }

    // Method to search for a word in the Trie recursively
    public boolean searchRecursively(String word) {
        return searchRecursively(word.toLowerCase(), 0, root); // Normalize word and start search
    }

    // Helper method for recursive word search
    private boolean searchRecursively(String word, int index, TrieNode node) {
        if (index == word.length()) {
            return node != null && node.wordLength > 0; // Check if word ends at this node
        }

        char c = word.charAt(index); // Current character
        int position = c - 'a'; // Index in children array

        if (node.children[position] == null) {
            return false; // Return false if character not found
        }

        return searchRecursively(word, index + 1, node.children[position]); // Recursive search
    }

    // Method to load words from a file into the Trie
    public void loadFile(String filePath) {
        File file = new File(filePath); // Create file object

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim(); // Read and trim each line

                if (!word.isEmpty()) { // Ignore empty lines
                    insert(word.toLowerCase(), 0); // Insert word into the Trie
                    numOfWords++; // Increment word count
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath); // Handle file not found error
        }
    }

    // Method to calculate memory usage of the Trie
    public int calcMem() {
        return calcMem(root); // Start calculation from root
    }

    // Helper method for recursive memory calculation
    private int calcMem(TrieNode node) {
        if (node == null) {
            return 0; // Return 0 for null nodes
        }

        int memory = 26 * 8; // Memory for children array (26 pointers)
        memory += 4 + 4 + 4; // Memory for wordLength, importance, and extra to match the hashing version
        for (TrieNode child : node.children) {
            memory += calcMem(child); // Add memory of child nodes
        }

        return memory; // Return total memory
    }

    public static void main(String[] args) {
        // main is for calculations
        // Array of dictionary file names
        String[] dictionaryFiles = {
            "dictionary1.txt",
            "dictionary2.txt",
            "dictionary3.txt",
            "dictionary4.txt",
            "dictionary5.txt",
            "dictionary6.txt"
        };

        for (int i = 0; i < dictionaryFiles.length; i++) {
            Trie trie = new Trie(); // Create a new Trie

            String fileName = dictionaryFiles[i]; // Get file name
            trie.loadFile(fileName); // Load dictionary file into Trie

            // Print memory usage of the Trie
            System.out.println(trie.calcMem());
        }
    }
}
