package rstead.bgoff.mshultz.therecipebook;

/**
 * Created by Mary on 5/2/2017.
 */

public class Ingredient {

    String name, amountType;
    Fraction amount;
    private final int AMOUNT_INDEX = 0;
    private final int AMOUNT_TYPE_INDEX = 1;
    private final int NAME_INDEX = 3;

    public Ingredient(String name, Fraction amount, String amountType){
        this.setName(name);
        this.setAmount(amount);
        this.setAmountType(amountType);
    }

    public Ingredient(String ingredient){
        String[] information = ingredient.split(":");
        this.setName(information[NAME_INDEX]);
        this.setAmountType(information[AMOUNT_TYPE_INDEX]);
        //FRACTION CONSTRUCTOR NEEDS CHANGING.
        this.setAmount(new Fraction(information[AMOUNT_INDEX]));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        this.amountType = (amountType.equals("N/A"))? "" : amountType;
    }

    public Fraction getAmount() {
        return amount;
    }

    public void setAmount(Fraction amount) {
        this.amount = amount;
    }

    @Override
    public String toString(){
        return amount.toString() + ":" + amountType + ":" + name;
    }
}

