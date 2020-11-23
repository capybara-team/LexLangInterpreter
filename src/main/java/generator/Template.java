package generator;

import org.stringtemplate.v4.ST;

public class Template {
    ST stringTemplate = null;
    String text = null;

    public Template(ST stringTemplate) {
        this.stringTemplate = stringTemplate;
    }

    public Template(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        if(stringTemplate == null)
            return text;
        return stringTemplate.render();
    }
}
