package com.filmseven.htmlparse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserEdit extends AppCompatActivity {
    Spinner spinner;
    Spinner spincategory;
    private AwesomeValidation awesomeValidation;
    private functions.Session session;
    List < String > list = new ArrayList < String > ();
    List<String> category = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        list.add("User");
        list.add("Editor");
        list.add("Admin");

        category.add("Aksiyon");
        category.add("Macera");
        category.add("Korku");

        addItemsOnSpinner();
        addListenerOnSpinnerItemSelection();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        Button registerButton = (Button) findViewById(R.id.registerButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);
        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.username, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.usernameerror);
        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        //awesomeValidation.addValidation(this, R.id.password, "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.surname, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.surnameerror);
        registerButton.setOnClickListener(view -> dbwriteuser());
        deleteButton.setOnClickListener(view -> dbdeleteuser());


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String ID = getIntent().getStringExtra("user");
        DocumentReference docUsers = db.collection("users").document(ID);

        docUsers.get().addOnSuccessListener(new OnSuccessListener <DocumentSnapshot> () {
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

                spinner.setSelection(list.indexOf(user.getauthorization()));

                if (user.getauthorization().equals("Editor")){
                    spincategory.setSelection(category.indexOf(user.getmoviecategory()));
                }
            }
        });
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

    public void dbdeleteuser(){
        final CharSequence[] items = {"Yes", "No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(UserEdit.this);

        builder.setTitle("Are You Sure to Delete User");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                switch(item){
                    case 0:
                        String ID = getIntent().getStringExtra("user");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        CollectionReference MovieRef = db.collection("users");
                        functions.deleteuser(ID.toString(),UserEdit.this);
                        finish();
                        Intent i = new Intent(UserEdit.this, UserList.class);
                        startActivity(i);

                        break;

                    case 1:
                        Toast.makeText(UserEdit.this, "Cancelled", Toast.LENGTH_SHORT).show();//Silme iptal edildi
                        break;
                }
            }
        });
        builder.show();
    }

    public void dbwriteuser(){
        String ID = getIntent().getStringExtra("user");
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
            user.put("username", username);
            user.put("name", name);
            user.put("surname", surname);
            user.put("email", email);
            if (!password.isEmpty()){
                user.put("password", password);
            }
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
                        ProgressDialog progressDialog
                                = new ProgressDialog(UserEdit.this);
                        progressDialog.setTitle("Editing User...");
                        progressDialog.show();
                        db.collection("users").document(ID)
                                .update(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "User Updated",
                                                Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "User update failed",
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
