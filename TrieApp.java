package test3;

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

        public void insert(char key, TrieNode trieNode) {
            if (size > 0.9 * capacity) { // If size surpasses 90% of capacity, rehash
                rehash();
            }

            Element newElement = new Element(key, trieNode);
            int index = hash(key);
            int probe = 0;

            while (table[index] != null) {
                Element current = table[index];
                // Robin Hood technique to check the current probe with all the other probes
                if (current.probeLength < probe) {
                    table[index] = newElement; // Switch elements
                    newElement = current;
                }
                index = (index + 1) % capacity;
                probe++;
            }

            table[index] = newElement;
            newElement.probeLength = probe;
            size++;
            maxProbeLength = Math.max(maxProbeLength, probe);
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
                    break; // Early break if the probe exceeds the max probe length
            }

            return null;
        }
    }
    
    public class MinHeap {
        private String[] heap; // Array to store first k words
        private int[] importance; // Array to store the importances of each word
        private int size; // Current size of the heap
        private int capacity; // Maximum capacity of the heap

        public MinHeap(int capacity) {
            this.capacity = capacity;
            this.size = 0;
            this.heap = new String[capacity];
            this.importance = new int[capacity];
        }

        private int parent(int i) {
            return (i - 1) / 2;
        }

        private int leftChild(int i) {
            return 2 * i + 1;
        }

        private int rightChild(int i) {
            return 2 * i + 2;
        }

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

        public void insert(String word, int wordImportance) {
            if (contains(word)) {
                return; // Skip duplicates
            }

            if (size < capacity) {
                // Insert the new word if the heap is not full
                heap[size] = word;
                importance[size] = wordImportance;
                int current = size++;

                // Restore heap property (percolate up)
                while (current > 0 && importance[current] < importance[parent(current)]) {
                    swap(current, parent(current));
                    current = parent(current);
                }
            } else {
                // If the heap is full, compare with the root (minimum)
                if (wordImportance > importance[0]) {
                    heap[0] = word; // Replace the root
                    importance[0] = wordImportance;

                    // Restore heap property (heapify down)
                    heapify(0);
                }
            }
        }



        public String removeMin() {
            if (size == 0) {
                return null;
            }

            // Remove the root element
            String rootWord = heap[0];
            heap[0] = heap[size - 1];
            importance[0] = importance[size - 1];
            size--;

            // Restore heap property
            heapify(0);

            return rootWord;
        }

        private void heapify(int i) { //known as percolate downs
            int left = leftChild(i);
            int right = rightChild(i);
            int smallest = i;

            if (left < size && importance[left] < importance[smallest]) {
                smallest = left;
            }

            if (right < size && importance[right] < importance[smallest]) {
                smallest = right;
            }

            if (smallest != i) {
                swap(i, smallest);
                heapify(smallest);
            }
        }

        public String[] getTopKWords(int k) {
            String[] result = new String[Math.min(k, size)];
            for (int i = 0; i < result.length; i++) {
                result[i] = removeMin();
            }
            return result;
        }

        private boolean contains(String word) {
            for (int i = 0; i < size; i++) {
                if (heap[i].equals(word)) {
                    return true; // Word already exists in the heap
                }
            }
            return false; // Word not found
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

    public void loadFile(String filePath) {
        File file = new File(filePath);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toLowerCase();
                if (!word.isEmpty()) {
                    insertRecursively(word, 0); // Insert each word into the Trie
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath);
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    public MinHeap findTopKWords(String word, int k) {
        // Use a MinHeap to store the top k words
        MinHeap heap = new MinHeap(k);

        // Collect words by prefix
        collectWordsByPrefix(root, "", word, heap);

        // Collect words by exact length
        collectWordsByExactLength(root, "", word.length(), heap);

        // Collect words by approximate length
        collectWordsByApproximateLength(root, "", word, heap);

        return heap; // Return the MinHeap containing the top k words
    }

    private void collectWordsByPrefix(TrieNode node, String currentWord, String searchPrefix, MinHeap heap) {
        if (node == null) return;

        // If the current word starts with the search prefix and has a positive importance, add it to the heap
        if (currentWord.startsWith(searchPrefix) && node.wordLength > 0 && node.importance > 0) {
            heap.insert(currentWord, node.importance);
        }

        // Traverse all child nodes
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                collectWordsByPrefix(child, currentWord + c, searchPrefix, heap);
            }
        }
    }


    private void collectWordsByExactLength(TrieNode node, String currentWord, int targetLength, MinHeap heap) {
        if (node == null) return;

        // Add only if word length matches targetLength and importance > 0
        if (node.wordLength > 0 && currentWord.length() == targetLength && node.importance > 0) {
            heap.insert(currentWord, node.importance);
        }

        // Recursively traverse child nodes
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                collectWordsByExactLength(child, currentWord + c, targetLength, heap);
            }
        }
    }
    
    private void collectWordsByApproximateLength(TrieNode node, String currentWord, String targetWord, MinHeap heap) {
        if (node == null) return;

        // Calculate the length difference
        int lengthDifference = currentWord.length() - targetWord.length();

        // Allow words with length differences of -1, 0, 1, or 2
        if (node.wordLength > 0 && node.importance > 0 && 
            (lengthDifference >= -1 && lengthDifference <= 2) &&
            areCharactersSimilar(currentWord, targetWord)) {
            heap.insert(currentWord, node.importance);
        }

        // Traverse all child nodes
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                collectWordsByApproximateLength(child, currentWord + c, targetWord, heap);
            }
        }
    }

    private boolean areCharactersSimilar(String word1, String word2) {
        int[] freq1 = new int[30];
        int[] freq2 = new int[30];

        // Count character frequencies for word1
        for (char c : word1.toCharArray()) {
            if (c - 'a' < 30 && c - 'a' >= 0) { // Ensure within bounds
                freq1[c - 'a']++;
            }
        }

        // Count character frequencies for word2
        for (char c : word2.toCharArray()) {
            if (c - 'a' < 30 && c - 'a' >= 0) { // Ensure within bounds
                freq2[c - 'a']++;
            }
        }

        int shared = 0, total = 0;

        // Compare frequencies
        for (int i = 0; i < 30; i++) {
            shared += Math.min(freq1[i], freq2[i]); // Characters common to both words
            total += Math.max(freq1[i], freq2[i]); // Total unique characters in both words
        }

        // Define similarity threshold as at least 50% shared characters
        double similarityRatio = (double) shared / total;
        return similarityRatio >= 0.7;
    }


    public void importanceUpdate(File wordsFile) {
        try {
            // Read the entire content of the file
            Scanner scanner = new Scanner(wordsFile);
            StringBuilder content = new StringBuilder();

            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append(" ");
            }
            scanner.close();

            // Split the content into potential words
            String[] words = content.toString().split("\\s+");

            // Write the processed words back to the same file
            try (PrintWriter writer = new PrintWriter(wordsFile)) {
                for (String word : words) {
                    String processedWord = processWord(word); // Process the word based on rules
                    if (processedWord != null && !processedWord.isEmpty()) {
                        writer.println(processedWord);
                    }
                }
            }

            // Update importance for the reformatted file
            scanner = new Scanner(wordsFile);
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toLowerCase();
                if (!word.isEmpty()) {
                    TrieNode node = searchNode(word, root, 0);
                    if (node != null && node.wordLength > 0) {
                        node.importance++; // Increment importance for the word
                    }
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + wordsFile.getName());
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    private String processWord(String word) {
        word = word.toLowerCase(); // Ensure all words are in lowercase

        if (word.matches(".*[a-zA-Z][^a-zA-Z]+[a-zA-Z].*")) {
            // Rule 1: Discard words with special characters interspersed with letters
            return null;
        } else if (word.matches(".*[^a-zA-Z]+$")) {
            // Rule 2: Trim trailing special characters (e.g., BS!!!! becomes BS)
            return word.replaceAll("[^a-zA-Z]+$", "");
        } else if (word.matches("[a-zA-Z]+")) {
            // Rule 3: Retain pure alphabetic words
            return word;
        } else {
            // Default: Discard anything else
            return null;
        }
    }



    private TrieNode searchNode(String word, TrieNode node, int index) {
        if (node == null || index == word.length()) {
            return node;
        }

        char c = word.charAt(index);
        TrieNode child = node.children.search(c);
        return searchNode(word, child, index + 1);
    }
    

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java TrieApp <dictionary file> <words file>");
            return;
        }

        String dictionaryFilePath = args[0];
        String wordsFilePath = args[1];

        TrieApp trie = new TrieApp();

        trie.loadFile(dictionaryFilePath); // Load dictionary
        trie.importanceUpdate(new File(wordsFilePath)); // Update importance


        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.print("Enter a word for the search criteria (or type 'exit' to quit): ");
            String searchWord = input.nextLine().trim().toLowerCase();

            if (searchWord.equals("exit")) {
                break;
            }

            System.out.print("Enter the number k (top results): ");
            int k = input.nextInt();
            input.nextLine(); // Consume newline

            MinHeap topKWords = trie.findTopKWords(searchWord, k);

            System.out.println("Top " + k + " words matching the criteria by importance:");
            String[] result = topKWords.getTopKWords(k);
            for (String word : result) {
                System.out.println(word);
            }
        }

        input.close();
    }

}