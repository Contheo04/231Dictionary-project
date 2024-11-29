//package test3;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrieApp {

    private TrieNode root; // The root of the Trie structure

    public TrieApp() {
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

            // Update the capacity
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

        // Insert a key (character) and its associated TrieNode into the hash table
        public void insert(char key, TrieNode trieNode) {
            // Check if rehashing is needed (when the table is more than 90% full)
            if (size > 0.9 * capacity) {
                rehash(); // Rehash the table to increase capacity and redistribute elements
            }

            // Create a new element with the provided key and TrieNode
            Element newElement = new Element(key, trieNode);
            int index = hash(key); // Calculate the initial hash index for the key
            int probe = 0; // Initialize probe length for tracking collision resolution

            // Resolve collisions using linear probing and Robin Hood Hashing
            while (table[index] != null) { // Continue if the current slot is occupied
                Element current = table[index];

                // Robin Hood technique: prioritize elements with shorter probe lengths
                if (current.probeLength < probe) {
                    // Swap the current element with the new element
                    table[index] = newElement;
                    newElement = current; // The displaced element will now be inserted elsewhere
                }

                // Move to the next slot (circularly, using modulo)
                index = (index + 1) % capacity;
                probe++; // Increment the probe length
            }

            // Place the new element in the appropriate slot
            table[index] = newElement;
            newElement.probeLength = probe; // Update the probe length for the new element
            size++; // Increment the size of the hash table
            maxProbeLength = Math.max(maxProbeLength, probe); // Update the maximum probe length
        }

        // Search for a TrieNode associated with a given character key
        public TrieNode search(char key) {
            int index = hash(key); // Calculate the initial hash index for the key
            int probe = 0; // Initialize probe length for tracking

            // Traverse the table to locate the key
            while (table[index] != null) { // Continue as long as the slot is not empty
                if (table[index].key == key) { // Check if the current slot contains the key
                    return table[index].trieNode; // Return the associated TrieNode if found
                }

                // Move to the next slot (circularly, using modulo)
                index = (index + 1) % capacity;
                probe++; // Increment the probe length

                // Stop searching if the probe length exceeds the maximum observed during
                // insertion
                if (probe > maxProbeLength) {
                    break; // Exit early to save time
                }
            }

            // If the key is not found, return null
            return null;
        }

    }

    public class MinHeap {
        private String[] heap; // Array to store words
        private int[] importance; // Array to store importance values for each word
        private int size; // Current number of elements in the heap
        private int capacity; // Maximum number of elements the heap can hold

        // Constructor to initialize the MinHeap with a given capacity
        public MinHeap(int capacity) {
            this.capacity = capacity;
            this.size = 0; // Initially, the heap is empty
            this.heap = new String[capacity]; // Initialize the array for words
            this.importance = new int[capacity]; // Initialize the array for importance values
        }

        // Get the index of the parent of the node at index `i`
        private int parent(int i) {
            return (i - 1) / 2;
        }

        // Get the index of the left child of the node at index `i`
        private int leftChild(int i) {
            return 2 * i + 1;
        }

        // Get the index of the right child of the node at index `i`
        private int rightChild(int i) {
            return 2 * i + 2;
        }

        // Swap the elements at indices `i` and `j` in the heap
        private void swap(int i, int j) {
            // Swap words
            String tempWord = heap[i];
            heap[i] = heap[j];
            heap[j] = tempWord;

            // Swap corresponding importance values
            int tempImportance = importance[i];
            importance[i] = importance[j];
            importance[j] = tempImportance;
        }

        // Insert a word and its importance value into the heap
        public void insert(String word, int wordImportance) {
            if (contains(word)) {
                return; // If the word already exists in the heap, skip insertion
            }

            if (size < capacity) {
                // If the heap is not full, insert the word at the end
                heap[size] = word;
                importance[size] = wordImportance;
                int current = size++; // Increment size after inserting

                // Restore the heap property by percolating the new element up
                while (current > 0 && importance[current] < importance[parent(current)]) {
                    swap(current, parent(current)); // Swap with the parent if necessary
                    current = parent(current); // Move to the parent's index
                }
            } else {
                // If the heap is full, compare with the root (minimum)
                if (wordImportance > importance[0]) {
                    // Replace the root with the new word if its importance is greater
                    heap[0] = word;
                    importance[0] = wordImportance;

                    // Restore the heap property by heapifying down
                    heapify(0);
                }
            }
        }

        // Remove and return the word with the minimum importance (root of the heap)
        public String removeMin() {
            if (size == 0) {
                return null; // If the heap is empty, return null
            }

            // Store the root word to return later
            String rootWord = heap[0];

            // Replace the root with the last element in the heap
            heap[0] = heap[size - 1];
            importance[0] = importance[size - 1];
            size--; // Decrease the size of the heap

            // Restore the heap property by heapifying down
            heapify(0);

            return rootWord; // Return the removed word
        }

        // Restore the min-heap property by percolating down from index `i`
        private void heapify(int i) {
            int left = leftChild(i); // Index of the left child
            int right = rightChild(i); // Index of the right child
            int smallest = i; // Assume the current node is the smallest

            // Check if the left child is smaller than the current node
            if (left < size && importance[left] < importance[smallest]) {
                smallest = left;
            }

            // Check if the right child is smaller than the smallest so far
            if (right < size && importance[right] < importance[smallest]) {
                smallest = right;
            }

            // If the smallest node is not the current node, swap and recurse
            if (smallest != i) {
                swap(i, smallest);
                heapify(smallest); // Recursively heapify the affected subtree
            }
        }

        // Get the top `k` words from the heap, in ascending order of importance
        public String[] getTopKWords(int k) {
            String[] result = new String[Math.min(k, size)]; // Create an array for the result
            for (int i = 0; i < result.length; i++) {
                result[i] = removeMin(); // Remove and add the smallest word to the result
            }
            return result;
        }

        // Check if a word already exists in the heap
        private boolean contains(String word) {
            for (int i = 0; i < size; i++) {
                if (heap[i].equals(word)) {
                    return true; // Return true if the word is found
                }
            }
            return false; // Return false if the word is not in the heap
        }
    }

    // Inserts a word into the Trie recursively
    public void insertRecursively(String word, int index) {
        insertRecursively(word.toLowerCase(), index, root); // Start from the root of the Trie
    }

    // Helper method to perform recursive insertion
    private void insertRecursively(String word, int index, TrieNode node) {
        if (index == word.length()) { // Base case: reached the end of the word
            node.wordLength = word.length(); // Mark the node as a complete word
            return;
        }

        char c = word.charAt(index); // Get the current character
        TrieNode child = node.children.search(c); // Search for the child node corresponding to the character
        if (child == null) { // If the child does not exist
            child = new TrieNode(); // Create a new TrieNode
            node.children.insert(c, child); // Insert the child into the current node's children
        }

        insertRecursively(word, index + 1, child); // Recur for the next character
    }

    // Loads a file and inserts all words into the Trie
    public void loadFile(String filePath) {
        File file = new File(filePath);

        try (Scanner scanner = new Scanner(file)) { // Open the file for reading
            while (scanner.hasNextLine()) { // Read each line
                String word = scanner.nextLine().trim().toLowerCase(); // Normalize the word
                if (!word.isEmpty()) { // Skip empty lines
                    insertRecursively(word, 0); // Insert the word into the Trie
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath);
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    // Finds the top k words matching the given word using a MinHeap
    public MinHeap findTopKWords(String word, int k) {
        MinHeap heap = new MinHeap(k); // Initialize a MinHeap to store the top k words

        collectWordsByPrefix(root, "", word, heap); // Collect words with the given prefix
        collectWordsByExactLength(root, "", word.length(), heap); // Collect words of the same length
        collectWordsByApproximateLength(root, "", word, heap); // Collect words of approximate length

        return heap; // Return the heap containing the top k words
    }

    // Collects words starting with a specific prefix
    private void collectWordsByPrefix(TrieNode node, String currentWord, String searchPrefix, MinHeap heap) {
        if (node == null)
            return; // Base case: null node

        // If the current word starts with the prefix and is a valid word, add it to the
        // heap
        if (currentWord.startsWith(searchPrefix) && node.wordLength > 0 && node.importance > 0) {
            heap.insert(currentWord, node.importance);
        }

        // Traverse all child nodes to explore more words
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                collectWordsByPrefix(child, currentWord + c, searchPrefix, heap); // Recur for the child node
            }
        }
    }

    // Collects words with an exact length
    private void collectWordsByExactLength(TrieNode node, String currentWord, int targetLength, MinHeap heap) {
        if (node == null)
            return; // Base case: null node

        // Add words matching the target length to the heap
        if (node.wordLength > 0 && currentWord.length() == targetLength && node.importance > 0) {
            heap.insert(currentWord, node.importance);
        }

        // Recur for all child nodes
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                collectWordsByExactLength(child, currentWord + c, targetLength, heap);
            }
        }
    }

    // Collects words with lengths approximately matching the target word
    private void collectWordsByApproximateLength(TrieNode node, String currentWord, String targetWord, MinHeap heap) {
        if (node == null)
            return; // Base case: null node

        // Calculate the length difference
        int lengthDifference = currentWord.length() - targetWord.length();

        // Include words with lengths within the allowed range and similar character
        // patterns
        if (node.wordLength > 0 && node.importance > 0 &&
                (lengthDifference >= -1 && lengthDifference <= 2) &&
                areCharactersSimilar(currentWord, targetWord)) {
            heap.insert(currentWord, node.importance);
        }

        // Recur for all child nodes
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                collectWordsByApproximateLength(child, currentWord + c, targetWord, heap);
            }
        }
    }

    // Determines whether two words have similar character distributions
    private boolean areCharactersSimilar(String word1, String word2) {
        int[] freq1 = new int[30]; // Frequency array for word1
        int[] freq2 = new int[30]; // Frequency array for word2

        // Count character frequencies for word1
        for (char c : word1.toCharArray()) {
            if (c - 'a' < 30 && c - 'a' >= 0) { // Ensure within valid range
                freq1[c - 'a']++;
            }
        }

        // Count character frequencies for word2
        for (char c : word2.toCharArray()) {
            if (c - 'a' < 30 && c - 'a' >= 0) { // Ensure within valid range
                freq2[c - 'a']++;
            }
        }

        int shared = 0, total = 0;

        // Calculate shared and total character counts
        for (int i = 0; i < 30; i++) {
            shared += Math.min(freq1[i], freq2[i]); // Shared characters
            total += Math.max(freq1[i], freq2[i]); // Total unique characters
        }

        // Determine if similarity ratio meets the threshold (>= 70%)
        double similarityRatio = (double) shared / total;
        return similarityRatio >= 0.7;
    }

    // Updates the importance of words in the Trie based on a given file
    public void importanceUpdate(File wordsFile) {
        try {
            // Read the entire content of the file
            Scanner scanner = new Scanner(wordsFile);
            StringBuilder content = new StringBuilder();

            // Append each line from the file to a single string
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append(" "); // Add a space to separate lines
            }
            scanner.close(); // Close the scanner after reading

            // Split the content into individual words
            String[] words = content.toString().split("\\s+"); // Split by whitespace

            // Write the processed words back to the file
            try (PrintWriter writer = new PrintWriter(wordsFile)) {
                for (String word : words) {
                    String processedWord = processWord(word); // Process each word based on specific rules
                    if (processedWord != null && !processedWord.isEmpty()) { // If valid, write to the file
                        writer.println(processedWord);
                    }
                }
            }

            // Update importance values in the Trie for the processed words
            scanner = new Scanner(wordsFile); // Reopen the file for reading
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toLowerCase(); // Normalize the word
                if (!word.isEmpty()) {
                    TrieNode node = searchNode(word, root, 0); // Search for the word in the Trie
                    if (node != null && node.wordLength > 0) {
                        node.importance++; // Increment importance if the word exists in the Trie
                    }
                }
            }
            scanner.close(); // Close the scanner after updating importance
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + wordsFile.getName());
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    // Processes a word based on specific rules
    private String processWord(String word) {
        word = word.toLowerCase(); // Convert the word to lowercase

        if (word.matches(".*[a-zA-Z][^a-zA-Z]+[a-zA-Z].*")) {
            // Rule 1: Discard words with special characters interspersed with letters
            return null;
        } else if (word.matches(".*[^a-zA-Z]+$")) {
            // Rule 2: Trim trailing special characters (e.g., "BS!!!!" becomes "BS")
            return word.replaceAll("[^a-zA-Z]+$", "");
        } else if (word.matches("[a-zA-Z]+")) {
            // Rule 3: Retain pure alphabetic words
            return word;
        } else {
            // Default case: Discard anything that doesn't match the above rules
            return null;
        }
    }

    // Searches for a node in the Trie corresponding to a given word
    private TrieNode searchNode(String word, TrieNode node, int index) {
        if (node == null || index == word.length()) { // Base case: reached the end of the word
            return node; // Return the current node (or null if not found)
        }

        char c = word.charAt(index); // Get the current character
        TrieNode child = node.children.search(c); // Search for the child node
        return searchNode(word, child, index + 1); // Recur for the next character
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TrieApp <dictionary file> <words file>");
            return;
        }

        TrieApp trie = new TrieApp();
        trie.loadFile(args[0]); // Load dictionary file
        trie.importanceUpdate(new File(args[1])); // Update importance using words file

        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.print("Enter a word (or 'exit' to quit): ");
            String searchWord = input.nextLine().trim().toLowerCase();

            if (searchWord.equals("exit"))
                break; // Exit condition

            System.out.print("Enter the number k (top results): ");
            int k = input.nextInt();
            input.nextLine(); // Consume newline

            // Find and display the top k words
            String[] result = trie.findTopKWords(searchWord, k).getTopKWords(k);
            System.out.println("Top " + k + " words:");
            for (String word : result) {
                System.out.println(word);
            }
        }

        input.close();
    }

}