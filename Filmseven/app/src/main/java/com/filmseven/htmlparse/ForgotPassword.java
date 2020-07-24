package com.filmseven.htmlparse;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ForgotPassword extends AppCompatActivity {
    Spinner spinner;
    private AwesomeValidation awesomeValidation;
    private functions.Session session;
    private boolean alreadyExecuted =false;
    public ArrayList liste= new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(view -> dbcheckuserfp());


        addItemsOnSpinner();
        addListenerOnSpinnerItemSelection();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.username, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.usernameerror);
        awesomeValidation.addValidation(this, R.id.sanswer, "^[a-zA-Z0-9]+[\\s\\W\\w]*$", R.string.sanswererror);
        //awesomeValidation.addValidation(this, R.id.squestion, "^[a-zA-Z0-9]+[\\s\\W\\w]*$", R.string.usernameerror);


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
    private void sendEmail(String email,String passwordresetcode) {
        //Getting content for email
        String subject = "Password Reset Code";
        String message = "Password Reset Code Is: " + passwordresetcode + " You can paste code to application. And type new password.";

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }
    public void dbcheckuserfp(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView usernameT = (TextView)findViewById(R.id.username);
        String username=usernameT.getText().toString();
        TextView sanswerT = (TextView)findViewById(R.id.sanswer);
        String sanswer=sanswerT.getText().toString();
        String squestion =String.valueOf(spinner.getSelectedItem());
        if (awesomeValidation.validate()) {

            Query mQuery = db.collection("users")
                    .whereEqualTo("username", username).whereEqualTo("sanswer", sanswer).whereEqualTo("squestion", squestion);

            mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if(documentSnapshots.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Failed. Check your informations.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DocumentSnapshot ds: documentSnapshots){
                            liste.add(ds.get("email").toString());
                            liste.add(ds.getId());
                            liste.add(ds.get("username").toString());
                            }
                        Map<String, Object> user = new HashMap<>();
                        final int random = new Random().nextInt(987654-123456) + 123456;
                        user.put("resetpwdcode", Integer.toString(random));
                        //Toast.makeText(getApplicationContext(), "Sending password reset code to your e-mail.", Toast.LENGTH_SHORT).show();
                        if(!alreadyExecuted) {
                            dbupdate(user, liste.get(1).toString(), liste.get(0).toString(), Integer.toString(random), liste.get(2).toString());
                            alreadyExecuted = true;
                        }
                    }

                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Failed. Check your informations.",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void dbupdate(Map user, String ID, String email, String random, String username){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(ID)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendEmail(email, random);
                        Intent editIntent = new Intent( ForgotPassword.this, ForgotPasswordNext.class );
                        editIntent.putExtra( "username",username);
                        editIntent.putExtra( "id",ID);
                        startActivity( editIntent );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Mail Sending failed",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }



}