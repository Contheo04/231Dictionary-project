//package test3;

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
        int dictionarySize = 500000; // Number of words in dictionary.txt

        // Adjusted parameters for log-normal distribution
        double muMin = 1.0; // Minimum average word length (~3 letters)  #Just a suggestion (I think 3 letters is a correct estimation)
        double muMax = 3.4; // Maximum average word length (~30 letters) #Just a suggestion (I think 20 letters is a but max estimation)
        double sigmaMin = 0.2; // Minimum variability #changed those 2 based on the before inits
        double sigmaMax = 0.6; // Maximum variability

        try (BufferedWriter dictionaryWriter = new BufferedWriter(new FileWriter("dictionary6.txt"))) {
            // Generate dictionary.txt with variable parameters
            for (int i = 0; i < dictionarySize; i++) {
                double mu = muMin + (muMax - muMin) * random.nextDouble(); // Randomly pick mu
                double sigma = sigmaMin + (sigmaMax - sigmaMin) * random.nextDouble(); // Randomly pick sigma
                String word = generateWordWithLogNormalLength(mu, sigma);
                dictionaryWriter.write(word);
                dictionaryWriter.newLine();
            }

            System.out.println("dictionary.txt created successfully!");

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}