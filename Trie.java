public class Trie {

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    private class TrieNode {
        private TrieNode[] children;
        private int wordLength;
        private int importance;

        TrieNode() {
            children = new TrieNode[26];
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
            node.importance = 1; // Set importance for a new word
            return;
        }

        char c = word.charAt(index);
        int position = c - 'a';
        if (node.children[position] == null) {
            node.children[position] = new TrieNode();
        }

        insert(word, index + 1, node.children[position]);
    }

    // Update the importance of a word
    public void updateImportance(String word, int increment) {
        TrieNode node = searchNode(word.toLowerCase(), root, 0);
        if (node != null && node.wordLength > 0) {
            node.importance += increment;
        }
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

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple", 0);
        trie.insert("App", 0);
        trie.insert("ChatPattixis", 0);

        System.out.println("Words in Trie:");
        trie.printWords();

        // Updating importance
        trie.updateImportance("apple", 5);
        trie.updateImportance("chatpattixis", 3);

        // Printing after importance update
        System.out.println("\nWords after updating importance:");
        trie.printWords();

        // Searching for words
        System.out.println("\nSearching for 'apple': " + trie.searchRecursively("apple")); // true
        System.out.println("Searching for 'app': " + trie.searchRecursively("app")); // true
        System.out.println("Searching for 'appl': " + trie.searchRecursively("appl")); // false
        System.out.println("Searching for 'ChatPattixis': " + trie.searchRecursively("ChatPattixis")); // true
        System.out.println("Searching for 'ban': " + trie.searchRecursively("ban")); // false
        System.out.println("Searching for 'grape': " + trie.searchRecursively("grape")); // false

        // Importance check
        System.out.println("\nImportance of 'apple': " + trie.getImportance("apple")); // 6
        System.out.println("Importance of 'chatpattixis': " + trie.getImportance("chatpattixis")); // 4
    }
}
