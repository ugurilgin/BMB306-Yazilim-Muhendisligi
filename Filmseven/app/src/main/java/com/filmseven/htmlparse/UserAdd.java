package com.filmseven.htmlparse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.filmseven.htmlparse.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserAdd extends AppCompatActivity {
    Spinner spinner;
    Spinner spincategory;
    private AwesomeValidation awesomeValidation;
    private functions.Session session;
    List < String > list = new ArrayList < String > ();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);
        list.add("Admin");
        list.add("Editor");
        list.add("User");

        addItemsOnSpinner();
        addListenerOnSpinnerItemSelection();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        Button registerButton = (Button) findViewById(R.id.registerButton);
        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.username, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.usernameerror);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.password, "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.surname, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.surnameerror);
        registerButton.setOnClickListener(view -> dbwriteuser());





    }
    // add items into spinner dynamically
    public void addItemsOnSpinner() {
        spinner = (Spinner) findViewById(R.id.userauthorization);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt("User Authorization");
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.userauthorization);
        spincategory = (Spinner) findViewById(R.id.usereditorcategory);
        List<String> category = new ArrayList<String>();
        category.add("Aksiyon");
        category.add("Macera");
        category.add("Korku");
        ArrayAdapter<String> catAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, category);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spincategory.setAdapter(catAdapter);
        spincategory.setPrompt("Movie Category");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==1){
                    spincategory.setVisibility(View.VISIBLE);
                }else{
                    spincategory.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spincategory.setVisibility(View.INVISIBLE);
            }
        });
    }



    public void dbwriteuser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        TextView usernameT = (TextView)findViewById(R.id.username);
        String username=usernameT.getText().toString();
        TextView nameT = (TextView)findViewById(R.id.name);
        String name=nameT.getText().toString();
        TextView surnameT = (TextView)findViewById(R.id.surname);
        String surname=surnameT.getText().toString();
        TextView emailT = (TextView)findViewById(R.id.email);
        String email=emailT.getText().toString();
        TextView passwordT = (TextView)findViewById(R.id.password);
        String password=passwordT.getText().toString();
        if (awesomeValidation.validate()) {
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            user.put("username", username);
            user.put("name", name);
            user.put("surname", surname);
            user.put("email", email);
            user.put("password", password);
            user.put("registerdate", date);
            user.put("authorization", String.valueOf(spinner.getSelectedItem()));//1 admin 2 editor 3 user
            if (String.valueOf(spinner.getSelectedItem()).equals("Editor")){
                user.put("moviecategory", String.valueOf(spincategory.getSelectedItem()));
            }else{
                user.put("moviecategory","");
            }

            functions.dbcheck("users", "username", username, new functions.dbcheckCallback() {
                @Override
                public void onCallback(boolean isAlready) {
                    if (isAlready){
                        Toast.makeText(getApplicationContext(), "Username is allready exist.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        functions.dbcheck("users", "email", email, new functions.dbcheckCallback() {
                            @Override
                            public void onCallback(boolean isAlready) {
                                if (isAlready){
                                    Toast.makeText(getApplicationContext(), "E-mail is allready exist.",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    ProgressDialog progressDialog
                                            = new ProgressDialog(UserAdd.this);
                                    progressDialog.setTitle("User Adding...");
                                    progressDialog.show();
                                    db.collection("users").document(username)
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "User Added successfull",
                                                            Toast.LENGTH_LONG).show();
                                                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                                    startActivity(i);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "User Adding failed",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        });
                    }
                }
            });



        } else {
            Toast.makeText(getApplicationContext(), "Failed. Check your informations.",
                    Toast.LENGTH_LONG).show();
        }

    }
}
