package food.com.food;

/**
 * Created by Yash on 12-03-2018.
 */
public class RecipeOption {
    String name;
    String ingredients;
    String source;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RecipeOption(String name, String ingredients, String source, String id) {

        this.name = name;
        this.ingredients = ingredients;
        this.source = source;
        this.id = id;
    }

    String id;

}
