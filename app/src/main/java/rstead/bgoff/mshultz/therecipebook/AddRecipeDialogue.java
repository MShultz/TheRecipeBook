package rstead.bgoff.mshultz.therecipebook;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Ryan on 5/2/2017.
 */

public class AddRecipeDialogue extends DialogFragment {

    public interface AddRecipeListener{
        public void onDoneClick(DialogFragment diagFrag);
    }

    private AddRecipeListener adl;

    private View activity;

    private Recipe recipe;

    private String name, imageLink, ingredients, directions, notes;

    //(String name, String imageLink, String ingredients, String description, String notes

    public void setProps() {
        name = ((TextView)activity.findViewById(R.id.name_entry)).getText().toString();
        directions = ((TextView)activity.findViewById(R.id.direction_entry)).getText().toString();
        notes = ((TextView)activity.findViewById(R.id.notes_entry)).getText().toString();
        imageLink = null;
        ingredients = "Not implemented";
    }

    public Recipe createRecipe(){
        setProps();
        return new Recipe(name, imageLink, ingredients, directions, notes);
    }

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            adl = (AddRecipeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
