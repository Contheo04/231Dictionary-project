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
        return Math.max(1, (int) Math.round(logNormalValue));
    }

    public static String generateWordWithLogNormalLength(double mu, double sigma) {
        int length = getLogNormalLength(mu, sigma);
        return generateWordOfLengthN(length);
    }

    public static void main(String[] args) {
        int dictionarySize = 1000; // Number of words in dictionary.txt USE FOR GRAPH
        int wordsSize = 500; // Number of words in words.txt

        // Wider range parameters for log-normal distribution
        double muMin = 1.5; // Minimum average word length (e.g., 4-5 characters)
        double muMax = 3.0; // Maximum average word length (e.g., 20 characters)
        double sigmaMin = 0.3; // Minimum variability
        double sigmaMax = 0.7; // Maximum variability

        try (BufferedWriter dictionaryWriter = new BufferedWriter(new FileWriter("dictionary.txt"));
                BufferedWriter wordsWriter = new BufferedWriter(new FileWriter("words.txt"))) {

            // Generate dictionary.txt with variable parameters
            for (int i = 0; i < dictionarySize; i++) {
                double mu = muMin + (muMax - muMin) * random.nextDouble(); // Randomly pick mu
                double sigma = sigmaMin + (sigmaMax - sigmaMin) * random.nextDouble(); // Randomly pick sigma
                String word = generateWordWithLogNormalLength(mu, sigma);
                dictionaryWriter.write(word);
                dictionaryWriter.newLine();
            }

            // Generate words.txt with variable parameters
            for (int i = 0; i < wordsSize; i++) {
                double mu = muMin + (muMax - muMin) * random.nextDouble(); // Randomly pick mu
                double sigma = sigmaMin + (sigmaMax - sigmaMin) * random.nextDouble(); // Randomly pick sigma
                String word = generateWordWithLogNormalLength(mu, sigma);
                wordsWriter.write(word);
                wordsWriter.newLine();
            }

            System.out.println("dictionary.txt and words.txt created successfully!");

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
