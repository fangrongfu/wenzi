package cn.edu.whu.irlab.service.languagemodel;

import java.util.ArrayList;

public class DocTerm {
    public Integer docId;
    public String rate;
    public String term;
    public ArrayList locate;
    public int tf;

    public Integer getDocId() {
        return docId;
    }
    public void setDocId(Integer docId) { this.docId = docId; }
    public String getRate() {
        return rate;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) { this.term = term; }
    public ArrayList getLocate() {
        return locate;
    }
    public void setLocate(ArrayList locate) {
        this.locate = locate;
    }
    public int getTf() {
        return tf;
    }
    public void setTf(int tf) {
        this.tf = tf;
    }
}
