package com.filmseven.htmlparse;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.filmseven.htmlparse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MovieEditList extends AppCompatActivity {

    private NavigationView nav_view;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ListView lv;
    public ArrayList liste= new ArrayList();
    public ArrayList liste2= new ArrayList();
    public ArrayList renklist= new ArrayList();
    private ArrayAdapter<String> adapter;
    private functions.Session session;//global variable


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_edit_list);
        nav_view = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Movie");
        setSupportActionBar(toolbar);
        androidx.appcompat.app.ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                ,drawer,toolbar,0,0);
        //toggle ı drawer listenera ekledik
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View baslik = nav_view.inflateHeaderView(R.layout.nav_baslik);
        nav_view.setItemIconTintList(null);
        nav_view.inflateMenu(R.menu.nav_menu);
        session = new functions.Session(getApplicationContext());
        String username= session.getusername();
        Menu menu = nav_view.getMenu();
        menuyetki();
        lv=(ListView)findViewById(R.id.List);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s =  lv.getItemAtPosition( position ).toString();


                for(int i=0;i<liste.size();i++)
                {
                    if(liste.get( i )==s)
                    {

                        Intent editIntent = new Intent( MovieEditList.this, MovieEdit.class );
                        editIntent.putExtra( "sayfa",liste2.get( i ).toString() );
                        startActivity( editIntent );
                    }
                }

            }
        });
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,liste);

        dbreadmovies();
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
    public void dbreadmovies() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("movies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        session = new functions.Session(getApplicationContext());
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String value = document.getString("moviename");
                                String moviecategory = document.getString("moviecategory");
                                String movieuser = document.getString("movieuser");
                                String approved = document.getString("approved");
                                if (session.getauthorization().equals("Admin")){
                                    liste.add(value);
                                    liste2.add(document.getId());
                                    renklist.add(approved);
                                }else if (session.getauthorization().equals("Editor")){
                                    if(moviecategory.equals(session.getusermoviecategory())){
                                        liste.add(value);
                                        liste2.add(document.getId());
                                        renklist.add(approved);
                                    }
                                }else if (session.getauthorization().equals("User")){
                                    if(movieuser.equals(session.getid())){
                                        liste.add(value);
                                        liste2.add(document.getId());
                                        renklist.add(approved);
                                    }
                                }else{
                                    liste=null;
                                }

                                lv.setAdapter(new ArrayAdapter<String>(MovieEditList.this, android.R.layout.simple_list_item_1, liste) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View row = super.getView(position, convertView, parent);

                                        if(renklist.get(position).equals("0"))
                                        {
                                            // do something change color
                                            row.setBackgroundColor (Color.GRAY); // some color
                                        }
                                        else
                                        {
                                            // default state
                                            row.setBackgroundColor (Color.WHITE); // default coloe
                                        }
                                        return row;
                                    }
                                });
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void login(MenuItem item){
        Intent i = new Intent(this, Login.class);
        startActivity(i);
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
        drawer.closeDrawers();
    }
    public void deletemovie(MenuItem item){
        Intent i = new Intent(this, MovieDeleteList.class);
        startActivity(i);
    }
    public void showmovies(MenuItem item){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
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