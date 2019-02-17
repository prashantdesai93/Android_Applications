package com.mad.practice.hw03;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hp on 2/15/2018.
 */

public class Question implements Serializable {



    private int questionNumber;
    private String question;
    private String imgUrl;
    private List<String> options;
    private int ans;

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getAns() {
        return ans;
    }

    public void setAns(int ans) {
        this.ans = ans;
    }

    public Question() {
    }


    @Override
    public String toString() {
        return "Question{" +
                "questionNumber=" + questionNumber +
                ", question='" + question + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", options=" + options +
                ", ans=" + ans +
                '}';
    }
}
