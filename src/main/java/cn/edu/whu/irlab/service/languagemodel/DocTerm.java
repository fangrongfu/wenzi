package cn.edu.whu.irlab.service.languagemodel;

public class DocTerm {
    // public Object setTerm;
    public Integer docId;
    public String rate;
    String term;


    public Integer getDocId() {
        return docId;
    }
    public void setDocId(Integer docId) { this.docId = docId;
    }
    public String getRate() {
        return rate;
    }
    public void setRate(String rate) {
        this.rate = rate;
    }
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
}
