public class Trie {

    private TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    private class TrieNode {
        private TrieNode[] children;
        private int wordLength;

        TrieNode() {
            children = new TrieNode[26];
            wordLength = 0;
        }
    }

    public void insertRecursively(String word, int index) {
        insertRecursively(word.toLowerCase(), index, root);
    }

    private void insertRecursively(String word, int index, TrieNode node) {
        if (index == word.length()) {
            node.wordLength = word.length();
            return;
        }

        char c = word.charAt(index);

        int position = c - 'a';
        if (node.children[position] == null) {
            node.children[position] = new TrieNode();
        }

        insertRecursively(word, index + 1, node.children[position]);
    }

    public void printWords() {
        printWords(root, "");
    }

    private void printWords(TrieNode node, String prefix) {
        if (node.wordLength > 0) {
            System.out.println(prefix);
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                char nextChar = (char) (i + 'a');
                printWords(node.children[i], prefix + nextChar);
            }
        }
    }

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
        trie.insertRecursively("apple", 0);
        trie.insertRecursively("App", 0);
        trie.insertRecursively("ChatPattixis", 0);

        System.out.println("Words in Trie:");
        trie.printWords();

        System.out.println("Searching for 'apple': " + trie.searchRecursively("apple")); // Should print true
        System.out.println("Searching for 'app': " + trie.searchRecursively("app")); // Should print true
        System.out.println("Searching for 'appl': " + trie.searchRecursively("appl")); // Should print false
        System.out.println("Searching for 'ChatPattixis': " + trie.searchRecursively("ChatPattixis"));// Should print
        System.out.println("Searching for 'ban': " + trie.searchRecursively("ban")); // Should print false
        System.out.println("Searching for 'grape': " + trie.searchRecursively("grape")); // Should print false
    }
}