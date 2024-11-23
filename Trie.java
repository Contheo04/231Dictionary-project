import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Trie {

    static int numOfWords = 0;

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    private class TrieNode {
        private TrieNode[] children;
        private int wordLength;
        private int importance;

        TrieNode() {
            children = new TrieNode[26]; // Supports only lowercase letters a-z
            wordLength = 0;
            importance = 0; // Initialize importance
        }
    }

    // Insert a word recursively
    public void insert(String word, int index) {
        insert(word.toLowerCase(), index, root);
    }

    private void insert(String word, int index, TrieNode node) {
        if (index == word.length()) {
            node.wordLength = word.length();
            return;
        }

        char c = word.charAt(index);
        int position = c - 'a';

        if (node.children[position] == null) {
            node.children[position] = new TrieNode();
        }

        insert(word, index + 1, node.children[position]);
    }

    // Get the importance of a word
    public int getImportance(String word) {
        TrieNode node = searchNode(word.toLowerCase(), root, 0);
        return (node != null && node.wordLength > 0) ? node.importance : 0;
    }

    private TrieNode searchNode(String word, TrieNode node, int index) {
        if (node == null || index == word.length()) {
            return node;
        }

        char c = word.charAt(index);
        int position = c - 'a';
        return searchNode(word, node.children[position], index + 1);
    }

    // Print words and their importance
    public void printWords() {
        printWords(root, "");
    }

    private void printWords(TrieNode node, String prefix) {
        if (node.wordLength > 0) {
            System.out.println(prefix + " (Importance: " + node.importance + ")");
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                char nextChar = (char) (i + 'a');
                printWords(node.children[i], prefix + nextChar);
            }
        }
    }

    // Search for a word recursively
    public boolean searchRecursively(String word) {
        return searchRecursively(word.toLowerCase(), 0, root);
    }

    private boolean searchRecursively(String word, int index, TrieNode node) {
        if (index == word.length()) {
            return node != null && node.wordLength > 0;
        }

        char c = word.charAt(index);
        int position = c - 'a';

        if (node.children[position] == null) {
            return false;
        }

        return searchRecursively(word, index + 1, node.children[position]);
    }

    public void checkAndInsert(String word) {
        // Normalize the word to lowercase for consistency
        word = word.toLowerCase();

        // Check if the word already exists in the Trie
        if (searchRecursively(word)) {
            System.out.println("The word \"" + word + "\" already exists in the Trie.");
        } else {
            // If the word does not exist, insert it
            insert(word, 0);
            System.out.println("The word \"" + word + "\" was inserted into the Trie.");
        }
    }

    // Update importance based on words in a file
    public void importanceUpdate(File wordsFile) {
        try (Scanner scanner = new Scanner(wordsFile)) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toLowerCase(); // Normalize to lowercase
                if (!word.isEmpty()) {
                    TrieNode node = searchNode(word, root, 0);
                    if (node != null && node.wordLength > 0) {
                        node.importance++; // Increment importance for the word if it exists in the Trie
                        System.out.println("Importance updated for word: " + word);
                    } else {
                        System.out.println("Word not found in Trie: " + word);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Words file not found.");
        }
    }

    // Load words from a file into the Trie
    public void loadFile(String filePath) {
        File file = new File(filePath);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim();

                // Normalize word (convert to lowercase, ignore empty lines)
                if (!word.isEmpty()) {
                    insert(word.toLowerCase(), 0);
                    numOfWords++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath);
        }
    }

    public int calcMem() {
        return calcMem(root);
    }

    private int calcMem(TrieNode node) {
        if (node == null) {
            return 0;
        }

        int memory = 26 * 8; // Size of children array (26 pointers, assuming 4 bytes each)
        memory += 4 + 4 + 4; // Sizes of wordLength, importance, and other metadata
        for (TrieNode child : node.children) {
            memory += calcMem(child);
        }

        return memory;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();

        // Load dictionary file into Trie
        trie.loadFile("dictionary.txt");

        // Update importance for words in Words.txt
        // for experiment COMMENT THIS SHIT PLEASE >:(
        
        /*
         * File wordsFile = new File("words.txt");
         * 
         * trie.importanceUpdate(wordsFile);
         * 
         * // Print words and their importance
         * System.out.println("\nWords in Trie:");
         * trie.printWords();
         * 
         * // Test importance retrieval
         * System.out.println("\nImportance of 'apple': " +
         * trie.getImportance("apple"));
         * System.out.println("Importance of 'banana': " +
         * trie.getImportance("banana"));
         * System.out.println("Importance of 'chatpattixis': " +
         * trie.getImportance("chatpattixis"));
         */

        // Memory calculation
        System.out.println("\nNumbers found in dictionary " + numOfWords + " Memory used by Trie: " + trie.calcMem() + " bytes");

    }
}
