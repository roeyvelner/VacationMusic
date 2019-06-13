package arieluniversity.vicationmusic;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class FireBase {
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private FirebaseUser currentFirebaseUser;
    private boolean ToLogRunTime;
    private final Song song;
    private int countOfSongs;
    public static ArrayList<Song> songsList;
    private String res;

    public FireBase(){
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        song = new Song();
        getCountOfSongs();
    }

    public void HandleSignIn(String user) {
        if (auth.getCurrentUser() == null) {
            SignIn(user);
        }

    }

    public void SignOut(){
        auth.signOut();
    }
    private void SignIn(String user) {
        if (!user.equals("")) {
            auth.signInWithEmailAndPassword(user.trim().toLowerCase(), "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (ToLogRunTime)
                            MainActivity.ShowToast("בוצעה התחברות כראוי");
                    } else {
                            MainActivity.ShowToast("לא בוצעה התחברות כראוי אולי כתובת המייל לא נכונה");
                    }
                }
            });
        }
    }

    public void getListOfUrlSongs(String user,final  MainActivity activity) {
        if (user.equals("")){
            MainActivity.ShowToast("יש להכניס כתובת מייל");
            return;
        }
        HandleSignIn(user);
        getMsgForScreen(user,activity);
        songsList = new ArrayList<>();
        DatabaseReference DBref = database.getReference();
        DBref = DBref.getRoot();
        DBref.child("Songs").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.ShowToast(" התחברות הצליחה");
                songsList.clear();
                countOfSongs = (int)dataSnapshot.getChildrenCount();
                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                for (DataSnapshot child : dataSnapshots) {
                    songsList.add(child.getValue(Song.class));
                }
                activity.ShowAll();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (ToLogRunTime)
                    MainActivity.ShowToast("קריאה ממסד נתונים נכשלה"   + "/n" + databaseError.getMessage());
            }
        });

    }


    public void AddStorageToFireBase(Uri pikedFile,final String mFileNameForRecord , final boolean isVoiceRecord,Song s) {
        String nameOfFile;
        song.setName(s.getName());
        song.setToWho(s.getToWho());
        song.setFromWho(s.getFromWho());
        if (song.getFromWho().equals(""))  song.setFromWho("פלוני "+countOfSongs);
        if (song.getName().equals(""))  song.setName("ללא שם "+countOfSongs);
        if (song.getToWho().equals(""))  song.setToWho("אלמוני "+countOfSongs);
        nameOfFile = song.getFromWho()+"_"+song.getToWho()+"_"+song.getName()+"_Song.mp3";
        StorageReference stgRef = storage.getReference();
        stgRef = stgRef.child(nameOfFile);
        stgRef.putFile(pikedFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        MainActivity.ShowToast("הוספת הקובץ הצליחה!");
                        song.setUrlSong(taskSnapshot.getDownloadUrl().toString());
                        String nameOfVoice = song.getFromWho()+"_"+song.getToWho()+"_"+song.getName()+"_Voice.mp3";
                        File file = new File(mFileNameForRecord);
                        Uri voice = Uri.fromFile(file);
                        StorageReference stgRef = storage.getReference();
                        stgRef = stgRef.child(nameOfVoice);
                        stgRef.putFile(voice).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                song.setUrlVoice(taskSnapshot.getDownloadUrl().toString());
                                AddUrlPathToFireBase();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception exception) {
                                        MainActivity.ShowToast("זוהתה בעיה - העלאה לא הצליחה : ");
                                        MainActivity.ShowToast(exception.getMessage());
                                    }
                                });

                        //AddUrlPathToFireBase(urlFromUploadFile,isVoiceRecord);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception exception) {
                        MainActivity.ShowToast("זוהתה בעיה - העלאה לא הצליחה : ");
                        MainActivity.ShowToast(exception.getMessage());
                    }
                });
    }

    private void AddUrlPathToFireBase() {
        DatabaseReference databaseRef = database.getReference();
            databaseRef = databaseRef.getRoot();
            databaseRef = databaseRef.child("Songs");
            databaseRef.child("" + (countOfSongs+1)).push();
            databaseRef.child("" + (countOfSongs+1)).setValue(song);
            databaseRef = databaseRef.getRoot();
            Integer x = countOfSongs+1;
            databaseRef.child("count").setValue(x);
    }


    public void addAuth(String email, String pass) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (ToLogRunTime)
                        MainActivity.ShowToast("אישור משתמש הצליח");
                    //FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                }
            }
        });
    }

    public void getCountOfSongs() {
        DatabaseReference DBref = database.getReference();
        DBref = DBref.getRoot();
        DBref.child("Songs").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (ToLogRunTime)
                    MainActivity.ShowToast("קריאה ממסד נתונים הצליחה");
                countOfSongs = (int)dataSnapshot.getChildrenCount();//Integer.parseInt(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (ToLogRunTime)
                    MainActivity.ShowToast("קריאה ממסד נתונים נכשלה");
                if (ToLogRunTime)
                    MainActivity.ShowToast(databaseError.getMessage());
            }
        });
    }


    public void getMsgForScreen(String mail,final  MainActivity activity) {
        DatabaseReference DBref = database.getReference();
        DBref = DBref.getRoot();
        String subMail =mail.substring(0,mail.indexOf('@'));
        DBref.child("titles").child(subMail.trim().toLowerCase()).child("msg").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
//                for (DataSnapshot child : dataSnapshots) {
//                    songsList.add(child.getValue(Song.class));
//                }
                activity.setHelloMsg(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (ToLogRunTime)
                    MainActivity.ShowToast("קריאה ממסד נתונים נכשלה"   + "/n" + databaseError.getMessage());
            }
        });

    }
}
