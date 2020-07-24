package com.filmseven.htmlparse;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;

public class ForgotChangePassword extends AppCompatActivity{
    private AwesomeValidation awesomeValidation2;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotchangepassword);
        awesomeValidation2 = new AwesomeValidation(ValidationStyle.BASIC);
        Button changeButton = (Button) findViewById(R.id.changeButton);
        changeButton.setOnClickListener(view -> dbupdatepassword());
    }

    public void dbupdatepassword(){
        awesomeValidation2.addValidation(this, R.id.password, "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", R.string.passworderror);
        String ID = getIntent().getStringExtra("id");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        TextView passwordT = (TextView)findViewById(R.id.password);
        String password=passwordT.getText().toString();
        if (awesomeValidation2.validate()) {
            user.put("resetpwdcode", "");
            user.put("password", password);
            db.collection("users").document(ID)
                    .update(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void emre) {
                            Toast.makeText(getApplicationContext(), "Password Changed Successfully. Please login",
                                    Toast.LENGTH_LONG).show();
                            Intent editIntent = new Intent( ForgotChangePassword.this, Login.class );
                            startActivity(editIntent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Mail Sending failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Failed. Check your informations.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
