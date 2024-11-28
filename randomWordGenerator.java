import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class randomWordGenerator {
    private static final Random random = new Random(); // Random object for generating random values
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz"; // Alphabet for word generation

    // Method to generate a word of fixed length n
    public static String generateWordOfLengthN(int n) {
        StringBuilder word = new StringBuilder(n); // Use StringBuilder for efficient concatenation
        for (int i = 0; i < n; i++) {
            char letter = ALPHABET.charAt(random.nextInt(ALPHABET.length())); // Randomly select a letter
            word.append(letter); // Append the letter to the word
        }
        return word.toString(); // Return the generated word
    }

    // Helper method to calculate the length of a word using log-normal distribution
    private static int getLogNormalLength(double mu, double sigma) {
        double logNormalValue = Math.exp(mu + sigma * random.nextGaussian()); // Generate log-normal value
        return Math.min(30, Math.max(3, (int) Math.round(logNormalValue))); // Restrict length between 3 and 30
    }

    // Method to generate a word with length based on log-normal distribution
    public static String generateWordWithLogNormalLength(double mu, double sigma) {
        int length = getLogNormalLength(mu, sigma); // Get word length using log-normal distribution
        return generateWordOfLengthN(length); // Generate a word of the calculated length
    }

    public static void main(String[] args) {
        // Define the sizes for each dictionary file
        int[] dictionarySizes = { 1000, 5000, 10000, 100000, 300000, 500000 };
        int[] Nsize = { 3, 5, 8, 10, 15, 20 }; // Fixed word lengths for each dictionary size

        // Flag for word generation type (0 = log-normal, 1 = fixed length)
        int flag = 1; // Change this to 0 for log-normal word length generation

        // Parameters for log-normal distribution
        double muMin = 1.0; // Minimum average word length (~3 letters)
        double muMax = 3.4; // Maximum average word length (~30 letters)
        double sigmaMin = 0.2; // Minimum variability
        double sigmaMax = 0.6; // Maximum variability

        // Loop through each dictionary size and create a corresponding file
        for (int i = 0; i < dictionarySizes.length; i++) {
            int dictionarySize = dictionarySizes[i]; // Number of words in the current dictionary
            String fileName = "dictionary" + (i + 1) + ".txt"; // Name of the file to create

            try (BufferedWriter dictionaryWriter = new BufferedWriter(new FileWriter(fileName))) {
                // Generate and write words for the current dictionary size
                for (int j = 0; j < dictionarySize; j++) {
                    String word;

                    if (flag == 0) { // Generate words using log-normal distribution
                        double mu = muMin + (muMax - muMin) * random.nextDouble(); // Randomly select mu
                        double sigma = sigmaMin + (sigmaMax - sigmaMin) * random.nextDouble(); // Randomly select sigma
                        word = generateWordWithLogNormalLength(mu, sigma);
                    } else if (flag == 1) { // Generate words of fixed length
                        word = generateWordOfLengthN(Nsize[i]);
                    } else {
                        throw new IllegalArgumentException("Invalid flag value: " + flag); // Handle invalid flag
                    }

                    dictionaryWriter.write(word); // Write the word to the file
                    dictionaryWriter.newLine(); // Add a new line
                }

                System.out.println(fileName + " created successfully!"); // Log successful creation of the file

            } catch (IOException e) {
                System.err.println("Error writing to file " + fileName + ": " + e.getMessage()); // Handle I/O errors
            }
        }
    }
}
