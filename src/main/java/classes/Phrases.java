package classes;

import java.util.Random;

public class Phrases {
    private String[] usualPhrases = null;
    private String[] badPhrases = null;
    private String[] goodPhrases = null;
    private Random random = new Random();

    public Phrases(String[] usualPhrases, String[] badPhrases, String[] goodPhrases) {
        this.usualPhrases = usualPhrases;
        this.badPhrases = badPhrases;
        this.goodPhrases = goodPhrases;
    }

    public String getPhrase(Player attacker, Player defender, String[] phrases){
        int phraseNum = random.nextInt(phrases.length);
        String phrase = phrases[phraseNum];
        phrase = phrase.replaceFirst("attacker", attacker.hero);
        phrase = phrase.replaceFirst("defender", defender.hero);

        return phrase;
    }
    public String getBadPhrase(Player attacker, Player defender){
        int phraseNum = random.nextInt(phrases.length);
        String phrase = phrases[phraseNum];
        phrase = phrase.replaceFirst("attacker", attacker.hero);
        phrase = phrase.replaceFirst("defender", defender.hero);

        return phrase;
    }
    public String getGoodPhrase(Player attacker, Player defender){
        int phraseNum = random.nextInt(phrases.length);
        String phrase = phrases[phraseNum];
        phrase = phrase.replaceFirst("attacker", attacker.hero);
        phrase = phrase.replaceFirst("defender", defender.hero);

        return phrase;
    }
}
