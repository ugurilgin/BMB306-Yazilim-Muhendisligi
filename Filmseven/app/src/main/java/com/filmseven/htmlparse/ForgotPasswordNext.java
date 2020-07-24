package com.filmseven.htmlparse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.filmseven.htmlparse.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ForgotPasswordNext extends AppCompatActivity{
    private AwesomeValidation awesomeValidation;
    private Boolean alreadyExecuted=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpasswordnext);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        Button controlButton = (Button) findViewById(R.id.controlButton);
        controlButton.setOnClickListener(view -> dbcheckuserprc());

    }

    public void dbcheckuserprc(){
        awesomeValidation.addValidation(this, R.id.passwordresetcode, "^[0-9][0-9][0-9][0-9][0-9][0-9]$", R.string.passwordresetcodeerror);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = getIntent().getStringExtra("username");
        String ID = getIntent().getStringExtra("id");
        TextView passwordresetcodeT = (TextView)findViewById(R.id.passwordresetcode);
        String passwordresetcode=passwordresetcodeT.getText().toString();
        if (awesomeValidation.validate()) {
                Query mQuery = db.collection("users")
                        .whereEqualTo("username", username).whereEqualTo("resetpwdcode", passwordresetcode);

                mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if(documentSnapshots.isEmpty()) {
                            if(!alreadyExecuted) {
                                Toast.makeText(ForgotPasswordNext.this, "Failed. Check your password reset code.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            finish();
                            Intent editIntent = new Intent( ForgotPasswordNext.this, ForgotChangePassword.class );
                            editIntent.putExtra( "username",username);
                            editIntent.putExtra( "id",ID);
                            startActivity( editIntent );
                            alreadyExecuted = true;

                        }

                    }
                });

        } else {
            Toast.makeText(getApplicationContext(), "Failed. Check your informations.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
