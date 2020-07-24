package com.filmseven.htmlparse;

import android.app.ProgressDialog;
import android.content.Intent;

import com.filmseven.htmlparse.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference MovieRef = db.collection("movies");
    static ImageView movieImageView;
    private MovieAdapter adapter, adapter2;
    private NavigationView nav_view;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    public static ProgressDialog dialog;

    private functions.Session session;//global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading Please Wait...");
        dialog.show();
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                ,drawer,toolbar,0,0);
        //toggle ı drawer listenera ekledik
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View baslik = nav_view.inflateHeaderView(R.layout.nav_baslik);
        nav_view.setItemIconTintList(null);
        nav_view.inflateMenu(R.menu.nav_menu);
        menuyetki();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Filtrelemek ve aramak için oluşturduğumuz menümüzün ana sayfa da görünmesini sağladuk
        getMenuInflater().inflate(R.menu.arama,menu);
        MenuItem item = menu.findItem(R.id.action_ara);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(MainActivity.this);//bu sınıfa bağladık
        searchView.setSubmitButtonEnabled(false);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setUpRecyclerViewarama(newText);
        adapter2.startListening();
        return false;
    }
    private void setUpRecyclerViewarama(String arama) {
        Query query = MovieRef.whereEqualTo("approved","1").orderBy("moviename", Query.Direction.ASCENDING).startAt(arama).endAt(arama + "\uf8ff");

        FirestoreRecyclerOptions<Movie> options = new FirestoreRecyclerOptions.Builder<Movie>()
                .setQuery(query, Movie.class)
                .build();
        adapter2 = new MovieAdapter(options, this);
        RecyclerView recyclerView2 = findViewById(R.id.recycler_view);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView2.setAdapter(adapter2);
    }


    private void setUpRecyclerView() {

        Query query = MovieRef.whereEqualTo("approved","1").orderBy("moviename", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Movie> options = new FirestoreRecyclerOptions.Builder<Movie>()
                .setQuery(query, Movie.class)
                .build();

        adapter = new MovieAdapter(options, this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpRecyclerView();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public void menuyetki(){
        session = new functions.Session(getApplicationContext());
        String username= session.getusername();
        Menu menu = nav_view.getMenu();
        if (session.getusername().equals("")){
            MenuItem login = menu.findItem(R.id.login);
            login.setVisible(true);
            MenuItem register = menu.findItem(R.id.register);
            register.setVisible(true);
            MenuItem logout = menu.findItem(R.id.logout);
            logout.setVisible(false);
            MenuItem addmovie = menu.findItem(R.id.addmovie);
            addmovie.setVisible(false);
            MenuItem editmovie = menu.findItem(R.id.editmovie);
            editmovie.setVisible(false);
            MenuItem deletemovie = menu.findItem(R.id.deletemovie);
            deletemovie.setVisible(false);
            MenuItem kulizin = menu.findItem(R.id.kulizin);
            kulizin.setVisible(false);
            MenuItem kuladd = menu.findItem(R.id.kuladd);
            kuladd.setVisible(false);
        }else{
            String authorization= session.getauthorization();
            MenuItem login = menu.findItem(R.id.login);
            login.setEnabled(true);
            login.setTitle("Profile " + username);
            MenuItem register = menu.findItem(R.id.register);
            register.setVisible(false);
            MenuItem logout = menu.findItem(R.id.logout);
            logout.setVisible(true);
            MenuItem addmovie = menu.findItem(R.id.addmovie);
            addmovie.setVisible(true);
            MenuItem editmovie = menu.findItem(R.id.editmovie);
            editmovie.setVisible(true);
            MenuItem deletemovie = menu.findItem(R.id.deletemovie);
            deletemovie.setVisible(true);
            MenuItem kulizin = menu.findItem(R.id.kulizin);
            MenuItem kuladd = menu.findItem(R.id.kuladd);
            if (authorization.equals("Admin")){
                kulizin.setVisible(true);
                kuladd.setVisible(true);
            } else{
                kulizin.setVisible(false);
                kuladd.setVisible(false);
            }

        }
    }
    public void login(MenuItem item){
        if (session.getusername().equals("")) {
            Intent i = new Intent(this, Login.class);
            startActivity(i);
        }else{
            Intent i = new Intent(this, Profile.class);
            startActivity(i);
        }
    }
    public void register(MenuItem item){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }
    public  void logout(MenuItem item){
        session.logout();
        Toast.makeText(getApplicationContext(), "Logout Success.", Toast.LENGTH_SHORT).show();
        Intent in = new Intent(this,MainActivity.class);
        startActivity(in);
    }

    public void addmovie(MenuItem item){
        Intent i = new Intent(this, addmovie.class);
        startActivity(i);
    }
    public void editmovie(MenuItem item){
        Intent i = new Intent(this, MovieEditList.class);
        startActivity(i);
    }
    public void deletemovie(MenuItem item){
        Intent i = new Intent(this, MovieDeleteList.class);
        startActivity(i);
    }
    public void showmovies(MenuItem item){
        drawer.closeDrawers();
    }
    public void showusers(MenuItem item){
        Intent i = new Intent(this, UserList.class);
        startActivity(i);
    }
    public void adduser(MenuItem item){
        Intent i = new Intent(this, UserAdd.class);
        startActivity(i);
    }
}
