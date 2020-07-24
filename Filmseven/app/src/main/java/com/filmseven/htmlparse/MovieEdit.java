package com.filmseven.htmlparse;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieEdit extends AppCompatActivity {
    private Button btnUpload, btnApprove;
    private ImageView imgView;
    EditText moviedate;
    TextView myImageViewText;
    Context context = MovieEdit.this;
    List < String > list = new ArrayList < String > ();
    private functions.Session session;//global variable
    // view for image view
    private ImageView imageView;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;
    Spinner spinner;
    private AwesomeValidation awesomeValidation;
    String imageurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_edit);

        session = new functions.Session(getApplicationContext());
        list.add("Aksiyon");
        list.add("Macera");
        list.add("Korku");

        // initialise views
        imgView = findViewById(R.id.imgView);
        btnUpload = findViewById(R.id.btnUpload);
        btnApprove = findViewById(R.id.btnApprove);
        if (session.getauthorization().equals("Admin")||session.getauthorization().equals("Editor")){
            btnApprove.setVisibility(View.VISIBLE);
        }


        imageView = findViewById(R.id.imgView);
        moviedate = (EditText) findViewById(R.id.moviedate);
        addItemsOnSpinner();
        addListenerOnSpinnerItemSelection();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        TextInputLayout movienameLayout = findViewById(R.id.movienameLayout);

        movienameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You can not edit movie title!",
                        Toast.LENGTH_LONG).show();
            }
        });


        //adding validation to edittexts
        awesomeValidation.addValidation(this, R.id.moviename, "^[a-zA-Z0-9]+[\\s\\W\\w]*$", R.string.moviename);
        awesomeValidation.addValidation(this, R.id.moviedate, "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", R.string.moviedate);
        awesomeValidation.addValidation(this, R.id.moviecast, "^[a-zA-Z0-9]+[\\s\\W\\w]*$", R.string.moviecast);
        awesomeValidation.addValidation(this, R.id.moviedetail, "^[a-zA-Z0-9]+[\\s\\W\\w]*$", R.string.moviedetail);
        // on pressing btnSelect SelectImage() is called
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        moviedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Şimdiki zaman bilgilerini alıyoruz. güncel yıl, güncel ay, güncel gün.
                final Calendar takvim = Calendar.getInstance();
                int yil = takvim.get(Calendar.YEAR);
                int ay = takvim.get(Calendar.MONTH);
                int gun = takvim.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                month += 1;
                                moviedate.setText(dayOfMonth + "/" + month + "/" + year);

                            }
                        }, yil, ay, gun);
                dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Select", dpd);
                dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dpd);
                dpd.show();

            }
        });
        btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map < String, Object > movie = new HashMap < > ();
                String ID = getIntent().getStringExtra("sayfa");
                    ProgressDialog progressDialog
                            = new ProgressDialog(context);
                    progressDialog.setTitle("Movie Approving...");
                    progressDialog.show();
                    movie.put("approved", "1");
                    db.collection("movies").document(ID)
                            .update(movie)
                            .addOnSuccessListener(new OnSuccessListener < Void > () {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Movie approved successfully",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                    Intent i = new Intent(MovieEdit.this, MovieEditList.class);
                                    startActivity(i);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Movie couldn't approve",
                                            Toast.LENGTH_LONG).show();
                                }
                            });

                }
        });

        // on pressing btnUpload uploadImage() is called
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });




        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String ID = getIntent().getStringExtra("sayfa");
        DocumentReference docRef = db.collection("movies").document(ID);
        docRef.get().addOnSuccessListener(new OnSuccessListener < DocumentSnapshot > () {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Movie movie = documentSnapshot.toObject(Movie.class);
                TextView title = (TextView) findViewById(R.id.moviename);
                title.setText(movie.getmoviename());

                TextView date = (TextView) findViewById(R.id.moviedate);
                date.setText(movie.getmoviedate());

                spinner.setSelection(list.indexOf(movie.getmoviecategory()));

                TextView cast = (TextView) findViewById(R.id.moviecast);
                if (movie.getmoviecast() != null) {
                    cast.setText(movie.getmoviecast());
                }

                TextView overview = (TextView) findViewById(R.id.moviedetail);
                overview.setText(movie.getmoviedetail());

                ImageView poster = (ImageView) findViewById(R.id.imgView);
                Picasso.get().load(movie.getmovieposter()).resize(poster.getMeasuredWidth(), poster.getMeasuredHeight()).into(poster);
                imageurl = movie.getmovieposter();
            }
        });



    }
    // add items into spinner dynamically
    public void addItemsOnSpinner() {

        spinner = (Spinner) findViewById(R.id.moviecategory);

        ArrayAdapter < String > dataAdapter = new ArrayAdapter < String > (this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt("Movie Category");
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.moviecategory);
        //spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    // Select Image method
    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);

    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
                TextView myImageViewText = (TextView) findViewById(R.id.myImageViewText);
                myImageViewText.setText("");
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage() {
        if (awesomeValidation.validate()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map < String, Object > movie = new HashMap < > ();
            TextView movienameT = (TextView) findViewById(R.id.moviename);
            String moviename = movienameT.getText().toString();
            TextView moviedateT = (TextView) findViewById(R.id.moviedate);
            String moviedate = moviedateT.getText().toString();
            TextView moviecastT = (TextView) findViewById(R.id.moviecast);
            String moviecast = moviecastT.getText().toString();
            TextView moviedetailT = (TextView) findViewById(R.id.moviedetail);
            String moviedetail = moviedetailT.getText().toString();
            movie.put("moviename", moviename);
            movie.put("moviedate", moviedate);
            movie.put("moviecast", moviecast);
            movie.put("moviedetail", moviedetail);
            String authorization= session.getauthorization();
            if (authorization.equals("Editor")||authorization.equals("Admin")){
                movie.put("approved", "1");
            }else{
                movie.put("approved", "0");
            }
            movie.put("movieuser", session.getid()); //username gelecek null yerine
            movie.put("moviecategory", String.valueOf(spinner.getSelectedItem()));
            String ID = getIntent().getStringExtra("sayfa");
            if (filePath != null) {
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                StorageReference riversRef = storageReference.child("images/" + ID + ".jpg");
                ProgressDialog progressDialog
                        = new ProgressDialog(context);
                progressDialog.setTitle("Movie Updating...");
                progressDialog.show();
                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener < UploadTask.TaskSnapshot > () {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content

                                Toast
                                        .makeText(context,
                                                "Image Uploaded!!",
                                                Toast.LENGTH_SHORT)
                                        .show();

                                Task < Uri > uri = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uri.isComplete());
                                Uri url = uri.getResult();
                                movie.put("movieposter", url.toString());
                                db.collection("movies").document(ID)
                                        .update(movie)
                                        .addOnSuccessListener(new OnSuccessListener < Void > () {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(context, "Movie added successfully",
                                                        Toast.LENGTH_LONG).show();
                                                finish();
                                                Intent i = new Intent(MovieEdit.this, MovieEditList.class);
                                                startActivity(i);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(context, "Movie couldn't add",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                progressDialog.dismiss();
                                Toast
                                        .makeText(context,
                                                "Failed " + exception.getMessage(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });


            } else {
                ProgressDialog progressDialog
                        = new ProgressDialog(context);
                progressDialog.setTitle("Movie Updating...");
                progressDialog.show();
                movie.put("movieposter", imageurl);
                db.collection("movies").document(ID)
                        .update(movie)
                        .addOnSuccessListener(new OnSuccessListener < Void > () {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Movie updated successfully",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                Intent i = new Intent(MovieEdit.this, MovieEditList.class);
                                startActivity(i);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "Movie couldn't update",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

            }
        }
    }



}