package test3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrieApp {

    private TrieNode root; // The root of the TrieHashing structure

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
            return key % capacity; // Hash function based on the ASCII value of the character modulo with the
                                   // capacity
        }

        private void rehash() {

            Element[] oldTable = table;

            // Update the capacity based on the current
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
                    table[index] = newElement; // if the if statement is correct switch the elements
                    newElement = current;
                }
                index = (index + 1) % capacity;
                probe++;
            }

            table[index] = newElement;
            newElement.probeLength = probe;
            size++;
            maxProbeLength = Math.max(maxProbeLength, probe); // initialize the maxProbeLength of the current element
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
                    break; // early break if the probe is greater than the max probe
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
            if (size == capacity) {
                // Replace root if the new importance is greater
                if (wordImportance > importance[0]) {
                    removeMin();
                } else {
                    return;
                }
            }

            // Insert new element at the end
            heap[size] = word;
            importance[size] = wordImportance;
            int current = size++;

            // Restore heap property
            while (current > 0 && importance[current] < importance[parent(current)]) {
                swap(current, parent(current));
                current = parent(current);
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

        private void heapify(int i) {
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

        public boolean isEmpty() {
            return size == 0;
        }

        public int getSize() {
            return size;
        }
    }
    
    public class DynamicArray {
        private String[] words;
        private int[] importances;
        private int size; // Number of elements currently in the array
        private int capacity; // Current maximum capacity of the array

        public DynamicArray() {
            this.capacity = 10; // Initial capacity
            this.size = 0;
            this.words = new String[capacity];
            this.importances = new int[capacity];
        }

        public void add(String word, int importance) {
            if (size == capacity) {
                resize();
            }
            words[size] = word;
            importances[size] = importance;
            size++;
        }

        private void resize() {
            capacity *= 2;
            String[] newWords = new String[capacity];
            int[] newImportances = new int[capacity];

            for (int i = 0; i < size; i++) {
                newWords[i] = words[i];
                newImportances[i] = importances[i];
            }

            words = newWords;
            importances = newImportances;
        }

        public String getWord(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Invalid index");
            }
            return words[index];
        }

        public int getImportance(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Invalid index");
            }
            return importances[index];
        }

        public int getSize() {
            return size;
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

    public boolean searchRecursively(String word) {
        return searchRecursively(word.toLowerCase(), 0, root);
    }

    private boolean searchRecursively(String word, int index, TrieNode node) {
        if (index == word.length()) {
            if (node.wordLength > 0) {
                node.importance++; // Increment importance for the word
            }
            return node.wordLength > 0; // Only return true if the node represents a complete word
        }

        char c = word.charAt(index);
        TrieNode child = node.children.search(c);
        if (child == null) {
            return false; // If the character does not exist, the word is not in the Trie
        }

        return searchRecursively(word, index + 1, child);
    }
   private TrieNode searchNode(String word, TrieNode node, int index) {
        if (node == null || index == word.length()) {
            return node;
        }

        char c = word.charAt(index);
        TrieNode child = node.children.search(c);
        return searchNode(word, child, index + 1);
    }

    public void importanceUpdate(File wordsFile) {
        try (Scanner scanner = new Scanner(wordsFile)) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toLowerCase();
                if (!word.isEmpty()) {
                    TrieNode node = searchNode(word, root, 0);
                    if (node != null && node.wordLength > 0) {
                        node.importance++; // Increment importance for the word
                    } 
                }
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + wordsFile.getName());
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
        }
    }

    public int getImportance(String word) {
        TrieNode node = searchNode(word.toLowerCase(), root, 0);
        return (node != null && node.wordLength > 0) ? node.importance : 0;
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
    
    private void collectWords(TrieNode node, String currentWord, DynamicArray results) {
        if (node == null) return;

        // Add only if this node marks a complete word
        if (node.wordLength > 0) { 
            results.add(currentWord, node.importance);
        }

        // Traverse all child nodes
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                collectWords(child, currentWord + c, results);
            }
        }
    }
    
    public DynamicArray searchByPrefix(String prefix) {
        TrieNode prefixNode = searchNode(prefix, root, 0);
        DynamicArray results = new DynamicArray();

        if (prefixNode == null) {
            return results; // Return empty results if prefix does not exist
        }

        // Collect only valid dictionary words starting with this prefix
        collectWords(prefixNode, prefix, results);
        return results;
    }

    
 // Method to search for words with the exact specified length
    public DynamicArray searchByExactLength(int length) {
        DynamicArray result = new DynamicArray(); // Store matching words and their importance
        searchByExactLengthHelper(root, "", length, result); // Recursive helper
        return result;
    }

    private void searchByExactLengthHelper(TrieNode node, String prefix, int targetLength, DynamicArray result) {
        if (node == null) return;

        // Add only if this node marks a complete word and the length matches
        if (node.wordLength > 0 && prefix.length() == targetLength) {
            result.add(prefix, node.importance);
        }

        // Traverse all child nodes
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                searchByExactLengthHelper(child, prefix + c, targetLength, result);
            }
        }
    }

    public DynamicArray approximateLengthSearch(String word) {
        DynamicArray results = new DynamicArray();
        approximateLengthSearchHelper(root, "", word, results);
        return results;
    }

    private void approximateLengthSearchHelper(TrieNode node, String currentWord, String targetWord, DynamicArray results) {
        if (node == null) return;

        // Check if this is a complete word and matches the length criteria
        int lengthDifference = currentWord.length() - targetWord.length();
        if (node.wordLength > 0 && (lengthDifference == 2 || lengthDifference == -1) && isSimilar(currentWord, targetWord)) {
            results.add(currentWord, node.importance);
        }

        // Traverse all child nodes
        for (char c = 'a'; c <= 'z'; c++) {
            TrieNode child = node.children.search(c);
            if (child != null) {
                approximateLengthSearchHelper(child, currentWord + c, targetWord, results);
            }
        }
    }

    private boolean isSimilar(String trieWord, String targetWord) {
        int[] trieFreq = new int[26];
        int[] targetFreq = new int[26];

        for (char c : trieWord.toCharArray()) {
            trieFreq[c - 'a']++;
        }

        for (char c : targetWord.toCharArray()) {
            targetFreq[c - 'a']++;
        }

        int mismatch = 0;
        for (int i = 0; i < 26; i++) {
            mismatch += Math.abs(trieFreq[i] - targetFreq[i]);
        }

        return mismatch <= 2; // Allowable mismatch threshold
    }

    private void mergeResults(DynamicArray source, DynamicArray target) {
        for (int i = 0; i < source.getSize(); i++) {
            String word = source.getWord(i);
            int importance = source.getImportance(i);

            // Check if word already exists in the target array
            boolean exists = false;
            for (int j = 0; j < target.getSize(); j++) {
                if (target.getWord(j).equals(word)) {
                    exists = true;
                    break;
                }
            }

            // Add the word only if it doesn't already exist
            if (!exists) {
                target.add(word, importance);
            }
        }
    }

    public MinHeap findTopKWords(String word, int k) {
        // Perform searches for the three criteria
        DynamicArray prefixResults = searchByPrefix(word);
        DynamicArray exactLengthResults = searchByExactLength(word.length());
        DynamicArray approximateResults = approximateLengthSearch(word);

        // Combine results into a single DynamicArray without duplicates
        DynamicArray combinedResults = new DynamicArray();
        mergeResults(prefixResults, combinedResults);
        mergeResults(exactLengthResults, combinedResults);
        mergeResults(approximateResults, combinedResults);

        // Use a MinHeap to store the top k words
        MinHeap heap = new MinHeap(k);
        for (int i = 0; i < combinedResults.getSize(); i++) {
            String currentWord = combinedResults.getWord(i);
            int importance = combinedResults.getImportance(i);
            heap.insert(currentWord, importance); // Insert into the heap
        }

        return heap; // Return the MinHeap containing the top k words
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
