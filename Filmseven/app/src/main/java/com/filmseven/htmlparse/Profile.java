package com.filmseven.htmlparse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {
    Spinner spinner;
    private AwesomeValidation awesomeValidation;
    private functions.Session session;
    List < String > SquestionList = new ArrayList < String > ();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        addItemsOnSpinner();
        addListenerOnSpinnerItemSelection();

        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        Button updateButton = (Button) findViewById(R.id.updateButton);
        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.username, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.usernameerror);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.password, "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.surname, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.surnameerror);
        updateButton.setOnClickListener(view -> dbupdateuser());

        session = new functions.Session(getApplicationContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String ID = session.getid();
        DocumentReference docUsers = db.collection("users").document(ID);

        docUsers.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                TextView username = (TextView) findViewById(R.id.username);
                username.setText(user.getusername());

                TextView name = (TextView) findViewById(R.id.name);
                name.setText(user.getname());

                TextView surname = (TextView) findViewById(R.id.surname);
                surname.setText(user.getsurname());

                TextView email = (TextView) findViewById(R.id.email);
                email.setText(user.getemail());

                spinner.setSelection(SquestionList.indexOf(user.getsquestion()));

                TextView sanswer = (TextView)findViewById(R.id.sanswer);
                sanswer.setText(user.getsanswer());
            }
        });
    }
    public void addItemsOnSpinner() {
        SquestionList.add("İlkokul Arkadaşımın Adı");
        SquestionList.add("En Sevdiğim Renk");
        SquestionList.add("Evcil Hayvanımın Adı");
        spinner = (Spinner) findViewById(R.id.squestion);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, SquestionList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt("Security Question");
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.squestion);
        //spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }

    public void dbupdateuser(){
        session = new functions.Session(getApplicationContext());
        String ID = session.getid();
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
            user.put("username", username);
            user.put("name", name);
            user.put("surname", surname);
            user.put("email", email);
            if (!password.isEmpty()){
                user.put("password", password);
            }
            user.put("squestion", String.valueOf(spinner.getSelectedItem()));
            user.put("sanswer", sanswer);

            functions.dbcheck("users", "username", username, new functions.dbcheckCallback() {
                @Override
                public void onCallback(boolean isAlready) {
                    if (isAlready){
                        ProgressDialog progressDialog
                                = new ProgressDialog(Profile.this);
                        progressDialog.setTitle("Editing Profile...");
                        progressDialog.show();
                        db.collection("users").document(ID)
                                .update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Profile Updated",
                                                Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Profile update failed",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {

                        Toast.makeText(getApplicationContext(), "Username is not exist.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });



        } else {
            Toast.makeText(getApplicationContext(), "Failed. Check your informations.",
                    Toast.LENGTH_LONG).show();
        }

    }
}
