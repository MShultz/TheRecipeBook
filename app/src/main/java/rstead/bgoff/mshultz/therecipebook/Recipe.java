package rstead.bgoff.mshultz.therecipebook;

/**
 * Created by Mary on 5/1/2017.
 */

public class Recipe {
    private int id;
    private String name, imageLink, ingredients, description, notes;

    public Recipe(int id, String name, String imageLink, String ingredients, String description, String notes) {
        this.setId(id);
        this.setName(name);
        this.setImageLink(imageLink);
        this.setIngredients(ingredients);
        this.setDescription(description);
        this.setNotes(notes);

    }

    public Recipe(String name, String imageLink, String ingredients, String description, String notes) {
        this.setName(name);
        this.setImageLink(imageLink);
        this.setIngredients(ingredients);
        this.setDescription(description);
        this.setNotes(notes);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
