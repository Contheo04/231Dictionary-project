import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class randomWordGenerator {
    private static final Random random = new Random();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    public static String generateWordOfLengthN(int n) {
        StringBuilder word = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            char letter = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
            word.append(letter);
        }
        return word.toString();
    }

    private static int getLogNormalLength(double mu, double sigma) {
        double logNormalValue = Math.exp(mu + sigma * random.nextGaussian());
        return Math.min(30, Math.max(3, (int) Math.round(logNormalValue))); // Restrict length between 3 and 30
    }

    public static String generateWordWithLogNormalLength(double mu, double sigma) {
        int length = getLogNormalLength(mu, sigma);
        return generateWordOfLengthN(length);
    }

    public static void main(String[] args) {
        // Sizes for each dictionary file
        int[] dictionarySizes = { 1000, 5000, 10000, 100000, 300000, 500000 };
        int[] Nsize = { 3, 5, 8, 10, 15, 20 };

        // Flag for word generation type (0 = log-normal, 1 = fixed length)
        int flag = 1; // Change to 0 for log-normal generation

        // Parameters for log-normal distribution
        double muMin = 1.0; // Minimum average word length (~3 letters)
        double muMax = 3.4; // Maximum average word length (~30 letters)
        double sigmaMin = 0.2; // Minimum variability
        double sigmaMax = 0.6; // Maximum variability

        for (int i = 0; i < dictionarySizes.length; i++) {
            int dictionarySize = dictionarySizes[i];
            String fileName = "dictionary" + (i + 1) + ".txt";

            try (BufferedWriter dictionaryWriter = new BufferedWriter(new FileWriter(fileName))) {

                // Generate words for the current dictionary size
                for (int j = 0; j < dictionarySize; j++) {
                    String word;

                    if (flag == 0) { // Log-normal distribution
                        double mu = muMin + (muMax - muMin) * random.nextDouble(); // Randomly pick mu
                        double sigma = sigmaMin + (sigmaMax - sigmaMin) * random.nextDouble(); // Randomly pick sigma
                        word = generateWordWithLogNormalLength(mu, sigma);
                    } else if (flag == 1) { // Fixed length
                        word = generateWordOfLengthN(Nsize[i]);
                    } else {
                        throw new IllegalArgumentException("Invalid flag value: " + flag);
                    }

                    dictionaryWriter.write(word);
                    dictionaryWriter.newLine();
                }

                System.out.println(fileName + " created successfully!");

            } catch (IOException e) {
                System.err.println("Error writing to file " + fileName + ": " + e.getMessage());
            }
        }
    }
}
