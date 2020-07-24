package com.filmseven.htmlparse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.filmseven.htmlparse.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {
    private AwesomeValidation awesomeValidation;
    private functions.Session session;//global variable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new functions.Session(getApplicationContext());
        String username= session.getusername();
        if (username.isEmpty()){
            awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            Button loginButton = (Button) findViewById(R.id.loginButton);
            loginButton.setOnClickListener(view -> dbcheckuserlogin());
            Button registerButton = (Button) findViewById(R.id.registerButton);
            Button forgotpwButton = (Button) findViewById(R.id.forgotpwButton);

            awesomeValidation.addValidation(this, R.id.usernameEditText, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.usernameerror);
            awesomeValidation.addValidation(this, R.id.passwordEditText, "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", R.string.passworderror);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View paramV) {
                    Intent i = new Intent(getApplicationContext(),Register.class);
                    startActivity(i);
                }
            });

            forgotpwButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View paramV) {
                    Intent i = new Intent(getApplicationContext(),ForgotPassword.class);
                    startActivity(i);
                }
            });

        } else {
            finish();
        }



    }
    public void dbcheckuserlogin(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        TextView usernameT = (TextView)findViewById(R.id.usernameEditText);
        String username=usernameT.getText().toString();
        TextView passwordT = (TextView)findViewById(R.id.passwordEditText);
        String password=passwordT.getText().toString();

        if (awesomeValidation.validate()) {

            Query mQuery = db.collection("users")
                    .whereEqualTo("username", username).whereEqualTo("password", password);

            mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (!documentSnapshots.isEmpty()){
                        for (DocumentSnapshot ds: documentSnapshots){
                                Toast.makeText(getApplicationContext(), "Login Success.", Toast.LENGTH_SHORT).show();
                                session = new functions.Session(getApplicationContext());
                            if (username != null){
                                session.setusername(username);
                            }
                            if (ds.getId() != null){
                                session.setid(ds.getId());
                            }
                            if (ds.get("authorization") != null){
                                session.setauthorization(ds.get("authorization").toString());
                            }
                            if (ds.get("email") != null){
                                session.setemail(ds.get("email").toString());
                            }
                            if (ds.get("name") != null){
                                session.setname(ds.get("name").toString());
                            }
                            if (ds.get("surname") != null){
                                session.setsurname(ds.get("surname").toString());
                            }
                            if (ds.get("registerdate") != null){
                                session.setregisterdate(ds.get("registerdate").toString());
                            }
                            if (ds.get("sanswer") != null){
                                session.setsquestion(ds.get("squestion").toString());
                            }

                            if (ds.get("authorization").equals("Editor")){
                                session.setusermoviecategory(ds.get("moviecategory").toString());
                            }


                                Intent in = new Intent(Login.this,MainActivity.class);
                                startActivity(in);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed. Check your informations.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Failed. Check your informations.",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void onClick(View view) {
        dbcheckuserlogin();
    }
}
