package example.po;

/**
 *  演示用的po
 *  在skydb中所有的操作都是针对po的
 *  注：所有的字段都必须为 public  非public的字段默认不解析
 */
public class User {

    public User(){

    }

    public User(Integer uid, String name, Integer age) {
        this.uid = uid;
        this.name = name;
        this.age = age;
    }

    public Integer uid;
    public String name;
    public Integer age;

}
