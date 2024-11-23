public class TrieHashing {

    private TrieNode root; // The root of the TrieHashing structure

    public TrieHashing() {
        this.root = new TrieNode();
    }

    // Inner class representing a node in the Trie
    public class TrieNode {
        private RobinHoodHashing children; // RobinHoodHashing structure for storing child nodes
        private int wordLength; // Indicates if this node represents a complete word

        public TrieNode() {
            this.children = new RobinHoodHashing(); // Initializing RobinHoodHashing for child nodes
            this.wordLength = 0; // Default to 0, indicating no complete word
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
            return key % capacity; // Hash function based on the ASCII value of the character modulo with the capacity
        }

        private void rehash() {
        	// for debug purposes
        	System.out.println("Rehash triggered! Current capacity: " + capacity + ", Current size: " + size);
        	
            // Save the old table
            Element[] oldTable = table;

            //Update the capacity based on the current
            if (capacity == 5) capacity = 11;
            else if (capacity == 11) capacity = 19;
            else if (capacity == 19) capacity = 29;

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
            //for debug purposes
            System.out.println("Rehash completed! New capacity: " + capacity);
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
                //Robin Hood technique to check the current probe with all the other probes
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
            maxProbeLength = Math.max(maxProbeLength, probe); //initialize the maxProbeLength of the current element
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
                if (probe > maxProbeLength) break; //early break if the probe is greater than the max probe
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

    public boolean searchRecursively(String word) {
        return searchRecursively(word.toLowerCase(), 0, root);
    }

    private boolean searchRecursively(String word, int index, TrieNode node) {
        if (index == word.length()) {
            return node.wordLength > 0; // Only return true if the node represents a complete word
        }

        char c = word.charAt(index);
        TrieNode child = node.children.search(c);
        if (child == null) {
            return false; // If the character does not exist, the word is not in the Trie
        }

        return searchRecursively(word, index + 1, child);
    }
    
    public static void testInsertWords() {
        TrieHashing trieHashing = new TrieHashing();

        System.out.println("=== TEST INSERT WORDS ===");

        // Insert words
        trieHashing.insertRecursively("apple", 0);
        trieHashing.insertRecursively("banana", 0);
        trieHashing.insertRecursively("cherry", 0);

        // Verify insertion
        System.out.println("Inserted 'apple': " + trieHashing.searchRecursively("apple"));
        System.out.println("Inserted 'banana': " + trieHashing.searchRecursively("banana"));
        System.out.println("Inserted 'cherry': " + trieHashing.searchRecursively("cherry"));
        System.out.println("Inserted 'grape' (not inserted): " + trieHashing.searchRecursively("grape"));
        System.out.println();
    }

    public static void testSearchWords() {
        TrieHashing trieHashing = new TrieHashing();

        System.out.println("=== TEST SEARCH WORDS ===");

        // Insert words
        trieHashing.insertRecursively("apple", 0);
        trieHashing.insertRecursively("banana", 0);
        trieHashing.insertRecursively("cherry", 0);

        // Test searching
        System.out.println("Search for 'apple': " + trieHashing.searchRecursively("apple"));
        System.out.println("Search for 'banana': " + trieHashing.searchRecursively("banana"));
        System.out.println("Search for 'cherry': " + trieHashing.searchRecursively("cherry"));
        System.out.println("Search for 'grape' (not inserted): " + trieHashing.searchRecursively("grape"));
        System.out.println();
    }

    public static void testRehashDuringInsertRecursively() {
        TrieHashing trieHashing = new TrieHashing();

        System.out.println("=== TEST REHASH DURING INSERT RECURSIVELY ===");

        // Insert words to trigger rehashing
        trieHashing.insertRecursively("apple", 0);
        trieHashing.insertRecursively("banana", 0);
        trieHashing.insertRecursively("cherry", 0);
        trieHashing.insertRecursively("date", 0);
        trieHashing.insertRecursively("elderberry", 0); 
        trieHashing.insertRecursively("word", 0);

        System.out.println("Words inserted. Checking if all words are accessible:");

        // Verify words
        System.out.println("Search for 'apple': " + trieHashing.searchRecursively("apple"));
        System.out.println("Search for 'banana': " + trieHashing.searchRecursively("banana"));
        System.out.println("Search for 'cherry': " + trieHashing.searchRecursively("cherry"));
        System.out.println("Search for 'date': " + trieHashing.searchRecursively("date"));
        System.out.println("Search for 'elderberry': " + trieHashing.searchRecursively("elderberry"));
        System.out.println("Search for 'word': " + trieHashing.searchRecursively("word"));

        System.out.println("\nWords in TrieHashing:");
        trieHashing.printWords();
    }

    public static void testPrintWords() {
        TrieHashing trieHashing = new TrieHashing();

        System.out.println("=== TEST PRINT WORDS ===");

        // Insert words
        trieHashing.insertRecursively("apple", 0);
        trieHashing.insertRecursively("banana", 0);
        trieHashing.insertRecursively("cherry", 0);

        // Print words
        trieHashing.printWords();
        System.out.println();
    }

    public static void testSearchNonExistentWords() {
        TrieHashing trieHashing = new TrieHashing();

        System.out.println("=== TEST SEARCH NON-EXISTENT WORDS ===");

        // Insert words
        trieHashing.insertRecursively("apple", 0);
        trieHashing.insertRecursively("banana", 0);

        // Search for non-existent words
        System.out.println("Search for 'grape': " + trieHashing.searchRecursively("grape")); // false
        System.out.println("Search for 'orange': " + trieHashing.searchRecursively("orange")); // false
        System.out.println("Search for 'cherry' (not inserted yet): " + trieHashing.searchRecursively("cherry")); // false
        System.out.println();
    }

    public static void testComprehensive() {
        TrieHashing trieHashing = new TrieHashing();

        System.out.println("=== TEST COMPREHENSIVE ===");

        // Insert words
        trieHashing.insertRecursively("apple", 0);
        trieHashing.insertRecursively("banana", 0);
        trieHashing.insertRecursively("cherry", 0);
        trieHashing.insertRecursively("date", 0);
        trieHashing.insertRecursively("elderberry", 0); 
        


        // Verify words
        System.out.println("Search for 'apple': " + trieHashing.searchRecursively("apple"));
        System.out.println("Search for 'banana': " + trieHashing.searchRecursively("banana"));
        System.out.println("Search for 'elderberry': " + trieHashing.searchRecursively("elderberry"));
        System.out.println("Search for 'fig' (not inserted): " + trieHashing.searchRecursively("fig"));

        // Print words
        System.out.println("\nWords in TrieHashing:");
        trieHashing.printWords();
    }
    
    public static void testInsert19Words() {
        TrieHashing trieHashing = new TrieHashing();

        System.out.println("=== TEST INSERT 19 WORDS AND REHASH ===");

        // Insert 19 words to trigger multiple rehashes
        String[] words = {
            "apple", "banana", "cherry", "date", "elderberry",
            "fig", "grape", "honeydew", "kiwi", "lemon",
            "mango", "nectarine", "orange", "papaya", "quince",
            "raspberry", "strawberry", "tangerine", "watermelon"
        };

        for (String word : words) {
            trieHashing.insertRecursively(word, 0);
        }

        System.out.println("\nWords inserted. Verifying rehash sizes:");

        System.out.println("\nVerifying if all words are accessible:");
        for (String word : words) {
            System.out.println("Search for '" + word + "': " + trieHashing.searchRecursively(word));
        }

        System.out.println("\nPrinting all words in TrieHashing:");
        trieHashing.printWords();
    }


    public static void main(String[] args) {
        testInsertWords();
        testSearchWords();
        testRehashDuringInsertRecursively();
        testPrintWords();
        testSearchNonExistentWords();
        testComprehensive();
        testInsert19Words();
    }

}
