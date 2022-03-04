package nl.tijsbeek;

public class Application {
    public static void main(final String[] args) {
        if (args.length <= 0) {
            throw new IllegalArgumentException("Missing token!");
        }

        String token = args[0];


    }
}
