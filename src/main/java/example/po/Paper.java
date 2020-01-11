package example.po;

public class Paper {

    public Paper(){

    }

    public Paper(Integer id, String refs) {
        this.id = id;
        this.refs = refs;
    }

    public Integer id;
    public String refs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRefs() {
        return refs;
    }

    public void setRefs(String refs) {
        this.refs = refs;
    }
}
