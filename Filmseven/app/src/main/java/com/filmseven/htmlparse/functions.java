package com.filmseven.htmlparse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class functions{
    public interface dbcheckCallback {
        void onCallback(boolean isAlready);
    }
    public static void dbcheck(String collection, String value, String key, dbcheckCallback callback){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query mQuery = db.collection(collection)
                .whereEqualTo(value, key);
        mQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            boolean isExisting;
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for (DocumentSnapshot ds: documentSnapshots) {

                    if (ds != null) {
                        isExisting = true;
                    } else {
                        isExisting = false;
                    }
                }
                callback.onCallback(isExisting);
            }
        });
    }

    public static void deletemovie(String movietitle, Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("movies").document(movietitle)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, movietitle + " Deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Deleting Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static void deleteuser(String username, Context context){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(username)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, username + " Deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Deleting Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static class Session {

        private SharedPreferences prefs;

        public Session(Context cntx) {
            // TODO Auto-generated constructor stub
            prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        }

        public void setusername(String username) {
            prefs.edit().putString("username", username).commit();
        }

        public void setauthorization(String authorization) {
            prefs.edit().putString("authorization", authorization).commit();
        }

        public void setemail(String email) {
            prefs.edit().putString("email", email).commit();
        }

        public void setname(String name) {
            prefs.edit().putString("name", name).commit();
        }

        public void setsurname(String surname) {
            prefs.edit().putString("surname", surname).commit();
        }

        public void setregisterdate(String registerdate) {
            prefs.edit().putString("registerdate", registerdate).commit();
        }

        public void setsanswer(String sanswer) {
            prefs.edit().putString("sanswer", sanswer).commit();
        }

        public void setsquestion(String squestion) {
            prefs.edit().putString("squestion", squestion).commit();
        }

        public void setid(String id) {
            prefs.edit().putString("id", id).commit();
        }

        public void setusermoviecategory(String usermoviecategory) {
            prefs.edit().putString("usermoviecategory", usermoviecategory).commit();
        }

        public String getusername() {
            String username = prefs.getString("username","");
            return username;
        }

        public String getauthorization() {
            String authorization = prefs.getString("authorization","");
            return authorization;
        }

        public String getemail() {
            String email = prefs.getString("email","");
            return email;
        }

        public String getname() {
            String name = prefs.getString("name","");
            return name;
        }

        public String getsurname() {
            String surname = prefs.getString("surname","");
            return surname;
        }

        public String getregisterdate() {
            String registerdate = prefs.getString("registerdate","");
            return registerdate;
        }

        public String getsanswer() {
            String sanswer = prefs.getString("sanswer","");
            return sanswer;
        }

        public String getsquestion() {
            String squestion = prefs.getString("squestion","");
            return squestion;
        }

        public String getid() {
            String id = prefs.getString("id","");
            return id;
        }
        public String getusermoviecategory() {
            String usermoviecategory = prefs.getString("usermoviecategory","");
            return usermoviecategory;
        }


        public void logout() {
            // Clearing all data from Shared Preferences
            prefs.edit().clear();
            prefs.edit().commit();
            prefs.edit().putString("username", "").commit();
            prefs.edit().putString("authorization", "").commit();
            prefs.edit().putString("email", "").commit();
            prefs.edit().putString("name", "").commit();
            prefs.edit().putString("surname", "").commit();
            prefs.edit().putString("registerdate", "").commit();
            prefs.edit().putString("sanswer", "").commit();
            prefs.edit().putString("squestion", "").commit();
            prefs.edit().putString("id", "").commit();
            prefs.edit().putString("usermoviecategory", "").commit();
        }
    }




}
