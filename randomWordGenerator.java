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
        int dictionarySize = 1000; // Number of words in dictionary.txt
        int wordsSize = 500; // Number of words in words.txt

        
        double mu = 2.22; // ln(9.2)
        double sigma = 0.05; // ln(9.7) - ln(9.2)

        try (BufferedWriter dictionaryWriter = new BufferedWriter(new FileWriter("dictionary.txt"));
                BufferedWriter wordsWriter = new BufferedWriter(new FileWriter("words.txt"))) {

            // Generate dictionary.txt
            for (int i = 0; i < dictionarySize; i++) {
                String word = generateWordWithLogNormalLength(mu, sigma);
                dictionaryWriter.write(word);
                dictionaryWriter.newLine();
            }

            // Generate words.txt
            for (int i = 0; i < wordsSize; i++) {
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
