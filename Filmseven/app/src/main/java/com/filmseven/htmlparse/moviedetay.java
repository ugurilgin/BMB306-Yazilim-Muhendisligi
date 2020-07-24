package com.filmseven.htmlparse;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import com.filmseven.htmlparse.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class moviedetay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_moviedetay);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String a=getIntent().getStringExtra( "sayfa" );

        DocumentReference docRef = db.collection("movies").document(a);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Movie movie = documentSnapshot.toObject(Movie.class);
                TextView title = (TextView) findViewById(R.id.synopsisTextView);
                title.setText(movie.getmoviename());

                TextView date = (TextView) findViewById(R.id.dateTextView);
                date.setText(movie.getmoviedate());

                TextView category = (TextView) findViewById(R.id.categoryTextView);
                category.setText(movie.getmoviecategory());

                TextView cast = (TextView) findViewById(R.id.castTextView);
                cast.setText(movie.getmoviecast());

                TextView overview = (TextView)findViewById(R.id.overviewText);
                overview.setText(movie.getmoviedetail());

                ImageView poster = (ImageView) findViewById(R.id.headerImageView);
                Picasso.get().load(movie.getmovieposter()).resize(poster.getMeasuredWidth(), poster.getMeasuredHeight()).into(poster);

            }
        });


    }
}
