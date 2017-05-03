package rstead.bgoff.mshultz.therecipebook;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

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

    ArrayList<Ingredient> ingredientList = new ArrayList<>();

    ArrayAdapter<String> spinAdapter;

    private AddRecipeListener adl;

    private View activity;

    private String name, imageLink, ingredients, directions, notes, ingredientName, ingredientAmount, amountType;

    public Recipe createRecipe(){
        setProps();
        return new Recipe(name, imageLink, ingredients, directions, notes, null);
    }    @Override
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
        spinAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1);
        populateAdapter();
        ((Button)activity.findViewById(R.id.button_add_item)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddIngredient(v);
            }
        });
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
        imageLink = null;
        ingredients = "";
        for(Ingredient i : ingredientList){
            ingredients += i.toString() + ",";
        }
        ingredients = ingredients.substring(0, ingredients.length()-1);
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
        ((Spinner)activity.findViewById(R.id.amount_type_dropdown)).setAdapter(spinAdapter);
        ((Spinner)activity.findViewById(R.id.amount_type_dropdown)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AddRecipeDialogue.this.onItemSelected(view, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void onItemSelected(View view, int position) {
        ((Spinner)view.getParent()).setSelection(position);
        amountType = ((Spinner)view.getParent()).getSelectedItem().toString();
        Log.e("CHOSEN TYPE:", amountType);
    }

    public void onAddIngredient(View view){

        EditText amountView = (EditText)activity.findViewById(R.id.ingredient_amount);
        int amountHeight = amountView.getHeight();
        int amountWidth = amountView.getWidth();

        Spinner spinView = (Spinner)activity.findViewById(R.id.amount_type_dropdown);
        int spinHeight = spinView.getHeight();
        int spinWidth = spinView.getWidth();

        EditText nameView = (EditText)activity.findViewById(R.id.ingredient_name);
        int nameHeight = nameView.getHeight();
        int nameWidth = nameView.getWidth();

        //get info from current Ingredient, create an ingredient, add to ingredients list.
        LinearLayout ingredientContainer = (LinearLayout)activity.findViewById(R.id.ingredient_list_layout);

        LinearLayout parent = (LinearLayout)view.getParent();

        //Delete previous add button
        //((LinearLayout)ingredientContainer.getChildAt(ingredientContainer.getChildCount()-1)).getChildAt(3).setVisibility(View.GONE);

        for(int i = 0; i < parent.getChildCount() - 1; i += 2){
            //ignore index 1, is a spinner and has its own changeListener
            TextView sibling = (TextView)parent.getChildAt(i);
            if(i == 0){
                ingredientAmount = sibling.getText().toString();
            }else{
                ingredientName = sibling.getText().toString();
            }
        }

        parent = new LinearLayout(getContext());

        EditText amountInput = new EditText(getContext());
        LinearLayout.LayoutParams amountInputLayoutParams = new LinearLayout.LayoutParams(amountWidth, amountHeight);
        amountInput.setLayoutParams(amountInputLayoutParams);
        amountInput.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        ViewGroup.LayoutParams spinnerLayoutParams = new ViewGroup.LayoutParams(spinWidth, spinHeight);
        Spinner spin = new Spinner(getContext());
        spin.setAdapter(spinAdapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AddRecipeDialogue.this.onItemSelected(view, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin.setLayoutParams(spinnerLayoutParams);

        EditText ingredientNameInput = new EditText(getContext());
        ViewGroup.LayoutParams ingredientLayoutParams = new ViewGroup.LayoutParams(nameWidth, nameHeight);
        ingredientNameInput.setLayoutParams(ingredientLayoutParams);

        ViewGroup.LayoutParams buttonParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button addButton = new Button(getContext());
        addButton.setText("Add");
        addButton.setLayoutParams(buttonParams);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRecipeDialogue.this.onAddIngredient(v);
            }
        });

        parent.addView(amountInput);
        parent.addView(spin);
        parent.addView(ingredientNameInput);
        parent.addView(addButton);

        ingredientContainer.addView(parent);

        Ingredient ingredient = new Ingredient(ingredientAmount + ":" + amountType + ":" + ingredientName);
        ingredientList.add(ingredient);

    }

}
