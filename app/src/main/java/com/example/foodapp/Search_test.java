package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.widget.TextView;

public class Search_test extends AppCompatActivity
{
    // Init needed views
    private Button sByingredient;
    private TextView screen;
    private List<Recipe> recipesWithMatchSize;
    private Query query;
    private EditText ingData;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_test);

        // Connecting the XML to our Objects
        sByingredient = (Button) findViewById(R.id.by_ing_buttn) ;
        ingData = (EditText)findViewById(R.id.ingData_input); // This is the field were ingredient input is comming from
        screen = (TextView)findViewById(R.id.test_screen);

        sByingredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1) Cast the Input into an  ArrayList<String> userInputIng

                String usrInput = ingData.getText().toString(); // This is the string from input

                usrInput = usrInput.replace(" " , ""); // Cutting off all the spaces for easier work.
                String[] splittedToArrayInput = usrInput.split(","); // Cut the string into an array of ingridients
                ArrayList<String> userInputIng = new ArrayList<>() ; // Init the list
                for( int i = 0 ; i < splittedToArrayInput.length ; i++)
                {
                    userInputIng.add(splittedToArrayInput[i]); // Fill the list
                }

                // Show the first ingredient on the screen
                // Yes the ingredients are formed well in the inputIng to the screen
                //screen.setText(userInputIng.get(0));



                // 2) Pass userInputIng to  The Search Function by Ingridients searchByIng and store it in matchedRecipeList
                List<Recipe> matchedRcipes = searchByIng(userInputIng);
                // 3) Pass an List of recipes to peller in the result_page by intent






            }
        });


    }

    // Search by Ingredient method

    public List<Recipe> searchByIng(ArrayList<String> userInputIng)
    {
        // 1) How many ingredients?
        int size = userInputIng.size();
        String strSize = String.valueOf(size);

        // 2) Querry all the recipes of size "size".

        query = FirebaseDatabase.getInstance().getReference("Recipe").orderByChild("numOfProducts").equalTo(strSize); // A query that get all the ingredient names
        query.addListenerForSingleValueEvent(valueEventListener2); // Here we push the ingredient names into the recipesWithMatchSize.

        //Now we can use recipesWithMatchSize as we please






        // 3) Extract the Recipe Id of all recipes with a perfect Ingridient match.
        // First option is that we intersect them from  recipesWithMatchSize

        // Intersection algorithem
        List<Recipe> recipeMatched = new ArrayList<Recipe>();
        for(Recipe recipe : recipesWithMatchSize){
            String[] ingForRecipe = recipe.splitIngredients();
            boolean notMatch = false;
            for(int index = 0; index < size && !notMatch; index++){
                boolean ingFound = false;
                for(int innerIndex = 0; innerIndex < ingForRecipe.length && !ingFound; innerIndex++){

                    if(userInputIng.get(index).equals(ingForRecipe[innerIndex])){
                        ingFound = true;
                    }

                    if(!ingFound && innerIndex+1 ==  ingForRecipe.length){
                        notMatch = true;

                    }
                }
            }
            if(!notMatch){
                recipeMatched.add(recipe);
            }
        }




        return recipeMatched;
    }

    //  Making the IngridientList filled with all the Ingredients from the DB
    ValueEventListener valueEventListener2 = new ValueEventListener() {// Insert the query results into the ingredientList
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            recipesWithMatchSize.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipesWithMatchSize.add(recipe);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error)
        {

        }
    };

}