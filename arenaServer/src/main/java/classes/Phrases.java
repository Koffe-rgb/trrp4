package classes;

import java.io.Serializable;
import java.util.Random;

public class Phrases implements Serializable {
    private String[] usualPhrases = null;
    private String[] badPhrases = null;
    private String[] goodPhrases = null;
    private Random random = new Random();

    public Phrases(String[] usualPhrases, String[] badPhrases, String[] goodPhrases) {
        this.usualPhrases = usualPhrases;
        this.badPhrases = badPhrases;
        this.goodPhrases = goodPhrases;
    }
    public String getUsualPhrase(String god, String attacker, String defender){
        return getPhrase(god, attacker, defender, usualPhrases);
    }
    public String getBadPhrase(String god, String attacker, String defender){
        return getPhrase(god, attacker, defender, badPhrases);
    }
    public String getGoodPhrase(String god, String attacker, String defender){
        return getPhrase(god, attacker, defender, goodPhrases);

    }

    private String getPhrase(String god, String attacker, String defender, String[] phrases){
        int phraseNum = random.nextInt(phrases.length);
        String phrase = phrases[phraseNum];
        phrase = phrase.replaceFirst("attacker", attacker);
        phrase = phrase.replaceFirst("defender", defender);
        phrase = phrase.replaceFirst("god", god);

        return phrase;
    }


}
