package com.filmseven.htmlparse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class Register extends AppCompatActivity {
    Spinner spinner;
    private AwesomeValidation awesomeValidation;
    private functions.Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        spinner = (Spinner) findViewById(R.id.squestion);
        List<String> list = new ArrayList<String>();
        list.add("İlkokul Arkadaşımın Adı");
        list.add("En Sevdiğim Renk");
        list.add("Evcil Hayvanımın Adı");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt("Security Question");
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.squestion);
        //spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
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
        TextView sanswerT = (TextView)findViewById(R.id.sanswer);
        String sanswer=sanswerT.getText().toString();
        if (awesomeValidation.validate()) {
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            user.put("username", username);
            user.put("name", name);
            user.put("surname", surname);
            user.put("email", email);
            user.put("password", password);
            user.put("registerdate", date);
            user.put("authorization", "User");//1 admin 2 editor 3 user
            user.put("squestion", String.valueOf(spinner.getSelectedItem()));
            user.put("sanswer", sanswer);
            user.put("resetpwdcode", "");
            user.put("moviecategory", "");

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
                                            = new ProgressDialog(Register.this);
                                    progressDialog.setTitle("Registering...");
                                    progressDialog.show();
                                    db.collection("users").document(username)
                                            .set(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    session = new functions.Session(getApplicationContext());
                                                    session.setid(username);
                                                    session.setusername(username);
                                                    session.setauthorization("User");
                                                    session.setemail(email);
                                                    session.setname(name);
                                                    session.setsurname(surname);
                                                    session.setregisterdate(date);
                                                    session.setsanswer(sanswer);
                                                    session.setsquestion(String.valueOf(spinner.getSelectedItem()));
                                                    Toast.makeText(getApplicationContext(), "Register successfull",
                                                            Toast.LENGTH_LONG).show();
                                                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                                    startActivity(i);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Register failed",
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
