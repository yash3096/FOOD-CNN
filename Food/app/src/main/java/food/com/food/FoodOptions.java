package food.com.food;

/**
 * Created by Yash on 11-03-2018.
 */
public class FoodOptions {
    public String  group,name,ndbno,type;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNdbno() {
        return ndbno;
    }
    public  String getType(){
        return type;
    }
    public void setNdbno(String ndbno) {
        this.ndbno = ndbno;
    }

    public FoodOptions(String group, String name, String ndbno, String t) {
        this.group = group;
        this.name = name;
        this.ndbno = ndbno;
        type=t;
    }
}
