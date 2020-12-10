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
    public String getUsualPhrase(String attacker, String defender){
        return getPhrase(attacker, defender, usualPhrases);
    }
    public String getBadPhrase(String attacker, String defender){
        return getPhrase(attacker, defender, badPhrases);
    }
    public String getGoodPhrase(String attacker, String defender){
        return getPhrase(attacker, defender, goodPhrases);

    }

    public String getPhrase(String attacker, String defender, String[] phrases){
        int phraseNum = random.nextInt(phrases.length);
        String phrase = phrases[phraseNum];
        phrase = phrase.replaceFirst("attacker", attacker);
        phrase = phrase.replaceFirst("defender", defender);

        return phrase;
    }

}
