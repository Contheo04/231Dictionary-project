import java.util.Random;

public class randomWordGenerator {
    private static final Random random = new Random();
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwwxyz";

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
}
