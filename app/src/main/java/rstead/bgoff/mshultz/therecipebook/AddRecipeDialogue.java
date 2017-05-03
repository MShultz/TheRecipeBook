package rstead.bgoff.mshultz.therecipebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ryan on 5/2/2017.
 */

@TargetApi(25)
public class AddRecipeDialogue extends DialogFragment {

    public interface AddRecipeListener{
        public void onDoneClick(DialogFragment diagFrag);
    }

    ArrayList<String> measurementTypes = new ArrayList<>();

    ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1);

    private AddRecipeListener adl;

    private View activity;

    private String name, imageLink, ingredients, directions, notes, ingredientName, ingredientAmount, amountType;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        activity = inflater.inflate(R.layout.add_recipe_dialogue_layout, null);
        builder.setView(activity)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        adl.onDoneClick(AddRecipeDialogue.this);
                    }
                });


        return builder.create();
    }

    @Override
    public void onStart(){
        super.onStart();
        populateAdapter();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            adl = (AddRecipeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void setProps() {
        name = ((TextView)activity.findViewById(R.id.name_entry)).getText().toString();
        directions = ((TextView)activity.findViewById(R.id.direction_entry)).getText().toString();
        notes = ((TextView)activity.findViewById(R.id.notes_entry)).getText().toString();
        imageLink = "Custom";
        //Create new ingredient class, call toString for ingredients
        ingredients = "Not implemented";
    }

    private void populateAdapter(){
        spinAdapter.add("oz");
        spinAdapter.add("cup(s)");
        spinAdapter.add("lb(s)");
        spinAdapter.add("tsp");
        spinAdapter.add("tbs");
        spinAdapter.add("pt");
        spinAdapter.add("pinch");
        spinAdapter.add("N/A");
    }

    public Recipe createRecipe(){
        setProps();
        return new Recipe(name, imageLink, ingredients, directions, notes);
    }

    public void onAddIngredient(View view){
        //get info from current Ingredient, create an ingredient, add to ingredients list.
        
        LinearLayout parent = (LinearLayout)view.getParent();

        for(int i = 0; i < parent.getChildCount() - 1; i++){
            //ignore index 1, is a spinner and has its own changeListener
            View sibling = parent.getChildAt(i);
        }

    }

}
