package org.example.entity;

import java.util.Map;

public class CaesarCiphertext {
    private String text;
    Map<String, Integer> letterFrequency;

    public CaesarCiphertext(){
        // Empty for Jackson library
    }

    public CaesarCiphertext(String text, Map<String, Integer> letterFrequency){
        this.text = text;
        this.letterFrequency = letterFrequency;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, Integer> getLetterFrequency() {
        return letterFrequency;
    }

    public void setLetterFrequency(Map<String, Integer> letterFrequency) {
        this.letterFrequency = letterFrequency;
    }
}
