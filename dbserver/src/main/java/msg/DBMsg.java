package msg;

import java.io.Serializable;

public class DBMsg implements Serializable {
    private String[] usualPhrases = null;
    private String[] badPhrases = null;
    private String[] goodPhrases = null;

    public String[] getUsualPhrases() {
        return usualPhrases;
    }

    public void setUsualPhrases(String[] usualPhrases) {
        this.usualPhrases = usualPhrases;
    }

    public String[] getBadPhrases() {
        return badPhrases;
    }

    public void setBadPhrases(String[] badPhrases) {
        this.badPhrases = badPhrases;
    }

    public String[] getGoodPhrases() {
        return goodPhrases;
    }

    public void setGoodPhrases(String[] goodPhrases) {
        this.goodPhrases = goodPhrases;
    }
}