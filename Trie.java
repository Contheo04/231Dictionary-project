import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Trie {

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    private class TrieNode {
        private TrieNode[] children;
        private int wordLength;
        private int importance;
        private int pos; // position

        TrieNode() {
            children = new TrieNode[26];
            wordLength = 0;
            importance = 0; // Initialize importance
            pos = 0;
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
            return node.wordLength > 0;
        }

        char c = word.charAt(index);
        int position = c - 'a';

        if (node.children[position] == null) {
            return false;
        }

        return searchRecursively(word, index + 1, node.children[position]);
    }

    // Load words from a dictionary file
    public void loadDictionary(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String word = scanner.nextLine().trim();
            if (!word.isEmpty()) {
                insert(word, 0);
            }
        }

        scanner.close();
    }

    public int calcMem() {
        int index = 0;
        // TO-DO
        return index;
    }

    // make a method that retrieves a text file that putss a new line every space

    public static void main(String[] args) {
        Trie trie = new Trie();

        // Load dictionary and importance files
        try {
            trie.loadDictionary("dictionary.txt"); // almost done
            // text maaybe bible or something idk

        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Print words in Trie
        System.out.println("Words in Trie:");
        trie.printWords();

        trie.insert("chatpattixis", 0);
        trie.insert("chatpattixis", 0);
        trie.insert("chatpattixis", 0);

        // Searching for words
        System.out.println("\nSearching for 'apple': " + trie.searchRecursively("apple"));
        System.out.println("Searching for 'chatpattixis': " + trie.searchRecursively("chatpattixis"));

        // Importance check
        System.out.println("\nImportance of 'apple': " + trie.getImportance("apple"));
        System.out.println("Importance of 'chatpattixis': " + trie.getImportance("chatpattixis"));
    }
}
