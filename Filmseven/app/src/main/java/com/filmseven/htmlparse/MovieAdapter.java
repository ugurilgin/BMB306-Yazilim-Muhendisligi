package com.filmseven.htmlparse;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.filmseven.htmlparse.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends FirestoreRecyclerAdapter<Movie, MovieAdapter.MovieHolder> {
    private Context context;
    private functions.Session session;//global variable
    public MovieAdapter(@NonNull FirestoreRecyclerOptions<Movie> options,Context context) {
        super(options);
        this.context = context;
    }
    @Override
    protected void onBindViewHolder(@NonNull MovieHolder holder, int position, @NonNull Movie model) {
    //if (model.getapproved().equals("1")) {
        Picasso.get().load(model.getmovieposter()).fit().into(holder.movieImageView);
        String movieID = getSnapshots().getSnapshot(position).getId();
        holder.movieLabelText.setText(model.getmoviename());
        holder.movieDateText.setText(model.getmoviedate());
        holder.movieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, moviedetay.class);
                intent.putExtra("sayfa", movieID.toString());
                context.startActivity(intent);
            }
        });

        holder.movieDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, moviedetay.class);
                intent.putExtra("sayfa", movieID.toString());
                context.startActivity(intent);
            }

        });

        session = new functions.Session(context);
        if (session.getauthorization().equals("Admin")) {

            holder.movieImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence[] items = {"Edit", "Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Select The Action");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            switch (item) {
                                case 0:
                                    Intent editIntent = new Intent(context, MovieEdit.class);
                                    editIntent.putExtra("sayfa", movieID);
                                    context.startActivity(editIntent);

                                    break;

                                case 1:
                                    final CharSequence[] items = {"Yes", "No"};

                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);

                                    builder2.setTitle("Are You Sure to Delete Movie");
                                    builder2.setItems(items, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int item) {

                                            switch (item) {
                                                case 0:
                                                    functions.deletemovie(model.getmoviename(), context);

                                                    break;

                                                case 1:
                                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();//Silme iptal edildi
                                                    break;
                                            }
                                        }
                                    });
                                    builder2.show();
                                    break;
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
            });


            holder.movieDateText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final CharSequence[] items = {"Edit", "Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Select The Action");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {

                            switch (item) {
                                case 0:
                                    Intent editIntent = new Intent(context, MovieEdit.class);
                                    editIntent.putExtra("sayfa", movieID);
                                    context.startActivity(editIntent);

                                    break;

                                case 1:
                                    final CharSequence[] items = {"Yes", "No"};

                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(context);

                                    builder2.setTitle("Are You Sure to Delete Movie");
                                    builder2.setItems(items, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int item) {

                                            switch (item) {
                                                case 0:
                                                    Toast.makeText(context, "Deleted" + model.getmoviename().toString(), Toast.LENGTH_SHORT).show();//Filmi silmek için gerekli kodlar

                                                    break;

                                                case 1:
                                                    Toast.makeText(context, "Cancel", Toast.LENGTH_SHORT).show();//Silme iptal edildi
                                                    break;
                                            }
                                        }
                                    });
                                    builder2.show();
                                    break;
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }
   // }
    }


    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                parent, false);
        MainActivity.dialog.dismiss();
        return new MovieHolder(v);


    }

    class MovieHolder extends RecyclerView.ViewHolder {
        ImageView movieImageView;
        TextView movieLabelText;
        TextView movieDateText;

        public MovieHolder(View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.movieImageView);
            movieLabelText = itemView.findViewById(R.id.movieLabelText);
            movieDateText = itemView.findViewById(R.id.movieDateText);


        }
    }
}
