package arieluniversity.vicationmusic;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest.permission;

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
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
//    private FirebaseAuth auth;
//    private FirebaseStorage storage;
//    private FirebaseDatabase database;
//    private FirebaseUser currentFirebaseUser;
    private MediaPlayer mMediaplayer, mMediaplayer2;
    //private ArrayList<String> songsUrlList, voicesUrlList;

    private Handler mHandler;
    private int count;
    private boolean getInfoRunTime;
    private SeekBar seekBar;
    private boolean pauseLastClick;
    EditText et;
    TextView tv, timeE, timeS,nameOfSong;
    Button play, pause,stop,uploadsongs,picksong,backwardB,forwardB;
    String[] path;
    boolean FromVoice;
    private Handler mRunnable;
    private String urlFromUploadFile;
    private MediaRecorder mRecorder;
    private MediaPlayer mpForUpload;
    private String mFileNameForRecord;
    private Uri FileFromActivityResult;
    private int countOfSongs;
    private String mFrom,mTo,mNumber;
    private View mview;
    private String urlToUploadFromYouTube;
    byte[] inputDataFile,inputDataRecord,WholeFile;
    private Song song;
    private FireBase fireBase;
    private static  Context context;
    private ListView listViewSongs;
    ArrayAdapter <String> itemsSongs;
    private Button loginLogout;

    public static String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getInfoRunTime = true;
        context = getApplicationContext();
        fireBase = new FireBase();
//        auth = FirebaseAuth.getInstance();
//        //auth.signOut();
//        storage = FirebaseStorage.getInstance();
//        database = FirebaseDatabase.getInstance();
        mMediaplayer = new MediaPlayer();
        mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaplayer2 = new MediaPlayer();
        mMediaplayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        play = (Button) findViewById(R.id.play);
        pause = (Button) findViewById(R.id.pause);
        loginLogout = (Button) findViewById(R.id.loginLogout);
        setProgressBarListener();
        count = 0;
        FromVoice = false;
        pauseLastClick = false;
        countOfSongs =0;
        et = (EditText) findViewById(R.id.user_name);
        //et.setText("r");

        tv = (TextView) findViewById(R.id.hellotext);
        timeS = (TextView) findViewById(R.id.timeStart);
        timeE = (TextView) findViewById(R.id.timeEnd);
        nameOfSong = (TextView) findViewById(R.id.nameOfSong);
        path = new String[2];
        urlFromUploadFile = "";
        HideAll();
        CheckForPermissions();
        try {
            //fireBase.getListOfUrlSongs(et.getText().toString());
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "התחברות לא הצליחה", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        CheckForPermissions();
//    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckForPermissions();
    }

    private  void CheckForPermissions(){
        CheckForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
//        CheckForPermission(android.Manifest.permission.RECORD_AUDIO,2);
//        CheckForPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,3);
//
//        CheckForPermission(android.Manifest.permission.INTERNET,4);
    }
    private void CheckForPermission(String type,int resultcode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,type) != PackageManager.PERMISSION_GRANTED  ){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{type},
                        resultcode);
        } else {
           // ShowToast(type + " ההרשאה ניתנה ");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                CheckForPermission(android.Manifest.permission.RECORD_AUDIO,2);
                return;
            }
            case 2:
                CheckForPermission(android.Manifest.permission.INTERNET,4);
                //CheckForPermission(permission.READ_EXTERNAL_STORAGE,3);
            case 3:

        }
    }

    private void HideAll() {
        uploadsongs = (Button) findViewById(R.id.uploadSongsBtn);
        picksong = (Button) findViewById(R.id.pickSongBtn);
        backwardB = (Button) findViewById(R.id.backward);
        forwardB = (Button) findViewById(R.id.forward);
        stop = (Button) findViewById(R.id.stop);

        uploadsongs.setVisibility(View.INVISIBLE);
        picksong.setVisibility(View.INVISIBLE);
        backwardB.setVisibility(View.INVISIBLE);
        forwardB.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.INVISIBLE);
        timeE.setVisibility(View.INVISIBLE);
        timeS.setVisibility(View.INVISIBLE);
        nameOfSong.setVisibility(View.INVISIBLE);

    }
    public  void ShowAll() {
        uploadsongs = (Button) findViewById(R.id.uploadSongsBtn);
        picksong = (Button) findViewById(R.id.pickSongBtn);
        backwardB = (Button) findViewById(R.id.backward);
        forwardB = (Button) findViewById(R.id.forward);
        stop = (Button) findViewById(R.id.stop);

        uploadsongs.setVisibility(View.VISIBLE);
        picksong.setVisibility(View.VISIBLE);
        backwardB.setVisibility(View.VISIBLE);
        forwardB.setVisibility(View.VISIBLE);
        play.setVisibility(View.VISIBLE);
        stop.setVisibility(View.VISIBLE);
        seekBar.setVisibility(View.VISIBLE);
        timeE.setVisibility(View.VISIBLE);
        timeS.setVisibility(View.VISIBLE);
        loginLogout.setText("התנתקות");
    }

    public void login(View view) {
        View view2 = this.getCurrentFocus();
        if (view2 != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (loginLogout.getText().toString().equals("התחברות")) {
            fireBase.getListOfUrlSongs(et.getText().toString(), MainActivity.this);
        }
        else{
            fireBase.SignOut();
            ClearHelloMsg();
            HideAll();
            FireBase.songsList.clear();
            loginLogout.setText("התחברות");

        }
    }







    public void pickSong(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        mview = getLayoutInflater().inflate(R.layout.pick_song, null);

        builder.setView(mview);
        final AlertDialog dialog = builder.create();
        ArrayList items_song = new ArrayList();
        for (int i=0;i<FireBase.songsList.size();i++){
            items_song.add(FireBase.songsList.get(i).getName() + " מ" + FireBase.songsList.get(i).getFromWho() + " ל" + FireBase.songsList.get(i).getToWho());
        }

        itemsSongs = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items_song);
        //listViewSongs = (ListView) findViewById(R.id.listView);
        ListView listView = (ListView) mview.findViewById(R.id.listView);
        listView.setAdapter(itemsSongs);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                count = position;
                dialog.hide();
                InitmMediaplayer();
                InitmMediaplayer2();
                PlayRecord(getUrl(true));
                getPauseVisible();

            }
        });









//        Button file = (Button) mview.findViewById(R.id.btn_file);
//        Button record = (Button) mview.findViewById(R.id.btn_recoed);
//        //Button btn_connect = (Button) mview.findViewById(R.id.btn_connect);
//
//        final EditText from,to,number;
//        from = (EditText) mview.findViewById(R.id.from);
//        to = (EditText) mview.findViewById(R.id.to);
//        number = (EditText) mview.findViewById(R.id.number);

//        final Button saveBtn = (Button) mview.findViewById(R.id.saveBtn);
//        final Button playRecord = (Button) mview.findViewById(R.id.playrecordBtn);
//        final Button playFile = (Button) mview.findViewById(R.id.playFileBtn);




        dialog.show();

    }

    public void UploadBtn(View view){

        OpenFileUploadeWindow();
    }

    private void setProgressBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mMediaplayer != null && fromUser && mMediaplayer.isPlaying() && !mMediaplayer2.isPlaying()) {
                    mMediaplayer.seekTo(progress * 1000);
                }
                if (mMediaplayer2 != null && fromUser && !mMediaplayer.isPlaying() && mMediaplayer2.isPlaying()) {
                    mMediaplayer2.seekTo(progress * 1000);
                }

            }
        });
    }



    public void playSong(String url) {
        try {
            if (url.equals("")) {
                IOException e = new IOException("  אנא המתן מספר שניות" + System.getProperty("line.separator") + "         או הכנס מייל");
                throw e;
            }
            mMediaplayer.setOnCompletionListener(null);
            mMediaplayer.reset();
            //Toast.makeText(MainActivity.this, "השיר מיד יתחיל", Toast.LENGTH_SHORT).show();
            mMediaplayer.setDataSource(url);
            mMediaplayer.setOnPreparedListener(MainActivity.this);
            mMediaplayer.prepareAsync();
        } catch (IOException e) {
            getPlayVisible();
            pauseLastClick = false;
            Toast.makeText(MainActivity.this, "התחברות לא הצליחה", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void PlayRecord(String Recordurl) {
        fireBase.HandleSignIn(et.getText().toString());
        try {
            if (Recordurl.equals("")) {
                IOException e = new IOException("  אנא המתן מספר שניות" + System.getProperty("line.separator") + "         או הכנס מייל");
                throw e;
            }
            mMediaplayer.setOnCompletionListener(null);
            mMediaplayer2.reset();
            Toast.makeText(MainActivity.this, "השיר מיד יתחיל", Toast.LENGTH_SHORT).show();
//            Recordurl = "http://drive.google.com/uc?export=view&id=##here##";
//            1Y2J_VM-6P1aHE9VHgUSMkUZZmzV-Xt26;
//            1xyPaUWCEhnQlsSaYgCMSmodDSj-UOsSA;
//            1VeLuGHwi4t7N2nIlhqhobCIpmj6Vjpza;
//            1jyKKX55Jkl2_rr-YvZxDwHhtSN5lOAd8;
//            1PJUM4habKcZn_OgPIJ2hYxPevv6a8UzS;
            ShowNameOfSong();
            mMediaplayer2.setDataSource(Recordurl);
            mMediaplayer2.setOnPreparedListener(MainActivity.this);
            mMediaplayer2.prepareAsync();
        } catch (IOException e) {
            getPlayVisible();
            pauseLastClick = false;
            Toast.makeText(MainActivity.this, "התחברות לא הצליחה", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        FromVoice = true;
    }

    private void ShowNameOfSong() {
        nameOfSong.setVisibility(View.VISIBLE);
        nameOfSong.setText("עכשיו מתנגן : " + FireBase.songsList.get(count).getName() + " מ" + FireBase.songsList.get(count).getFromWho() + " ל" + FireBase.songsList.get(count).getToWho());
    }
    private void HideNameOfSong() {
        nameOfSong.setVisibility(View.INVISIBLE);
        nameOfSong.setText("");
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        if (getInfoRunTime) Toast.makeText(MainActivity.this, "מתחילים", Toast.LENGTH_SHORT).show();
        if (FromVoice) {
            seekBar.setMax(mMediaplayer2.getDuration() / 1000);
        } else {
            seekBar.setMax(mMediaplayer.getDuration() / 1000);
        }

        mHandler = new Handler();
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (FromVoice) {
                    if (mMediaplayer2 != null && mMediaplayer2.isPlaying()) {
                        int mCurrentPosition = mMediaplayer2.getCurrentPosition() / 1000;
                        int mDuration = mMediaplayer2.getDuration() / 1000;
                        setTimes(mCurrentPosition, mDuration);
                        seekBar.setProgress(mCurrentPosition);
                        if (mDuration - mCurrentPosition < 3) {
                            InitmMediaplayer();
                            playSong(getUrl(false));
                        }
                    }
                    mHandler.postDelayed(this, 1000);
                } else {
                    if (mMediaplayer != null) {
                        int mCurrentPosition = mMediaplayer.getCurrentPosition() / 1000;
                        setTimes(mCurrentPosition, mMediaplayer.getDuration() / 1000);
                        seekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 1000);
                }
            }
        });
        if (!FromVoice) {
            mMediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    InitmMediaplayer();
                    InitmMediaplayer2();
                    PlayRecord(getNextUrl(true));
                }
            });
        } else {
            mMediaplayer2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    FromVoice = false;
                    mMediaplayer2.setOnCompletionListener(null);
                    //InitmMediaplayer();
                }
            });
        }
        mp.start();
    }

    private void setTimes(int current, int duration) {
        int min, sec;
        min = current / 60;
        sec = current % 60;
        timeS.setText(padWithZero(min) + ":" + padWithZero(sec));
        timeE.setText(padWithZero(duration / 60 - min) + ":" + padWithZero(duration % 60 - sec));
    }

    private String padWithZero(int x) {
        if (x > 9) return "" + x;
        return "0" + x;
    }

    public String getUrl(boolean isRecordVoice) {
        if (isRecordVoice) {
            if (FireBase.songsList != null)
                if (FireBase.songsList.size() >= count && FireBase.songsList.size() != 0)
                    return FireBase.songsList.get(count).getUrlVoice();
        } else {
            if (FireBase.songsList != null)
                if (FireBase.songsList.size() >= count && FireBase.songsList.size() != 0)
                    return FireBase.songsList.get(count).getUrlSong();
        }
        return "";
    }

    public String getNextUrl(boolean isRecordVoice) {
        count++;
        if (isRecordVoice) {
            if (count == FireBase.songsList.size()) count = 0;
            if (FireBase.songsList != null)
                if (FireBase.songsList.size() >= count && FireBase.songsList.size() != 0)
                    return FireBase.songsList.get(count).getUrlVoice();
        } else {
            if (count == FireBase.songsList.size()) count = 0;
            if (FireBase.songsList != null)
                if (FireBase.songsList.size() >= count && FireBase.songsList.size() != 0)
                    return FireBase.songsList.get(count).getUrlSong();
        }
        return "";
    }

    public String getLastUrl(boolean isRecordVoice) {
        count--;
        if (isRecordVoice) {
            if (count < 0) count = FireBase.songsList.size() - 1;
            if (FireBase.songsList != null)
                if (FireBase.songsList.size() >= count && FireBase.songsList.size() != 0)
                    return FireBase.songsList.get(count).getUrlVoice();
        } else {
            if (count < 0) count = FireBase.songsList.size() - 1;
            if (FireBase.songsList != null)
                if (FireBase.songsList.size() >= count && FireBase.songsList.size() != 0)
                    return FireBase.songsList.get(count).getUrlSong();
        }
        return "";
    }


    private void InitmMediaplayer() {
        mMediaplayer.stop();
        mMediaplayer.reset();
    }

    private void InitmMediaplayer2() {
        mMediaplayer2.stop();
        mMediaplayer2.reset();
    }

    public void forwardClick(View view) {
        if (getInfoRunTime)
            Toast.makeText(MainActivity.this, "מקש קדימה", Toast.LENGTH_SHORT).show();
        getPauseVisible();
        pauseLastClick = false;
        InitmMediaplayer();
        InitmMediaplayer2();
        PlayRecord(getNextUrl(true));
    }

    public void backwardOnclick(View view) {
        if (getInfoRunTime)
            Toast.makeText(MainActivity.this, "מקש אחורה", Toast.LENGTH_SHORT).show();
        getPauseVisible();
        pauseLastClick = false;
        if (FromVoice) {
            if (mMediaplayer2.getCurrentPosition() > 4500) {
                mMediaplayer2.seekTo(1);
            } else {
                InitmMediaplayer();
                InitmMediaplayer2();
                PlayRecord(getLastUrl(true));
            }
        } else {
            if (mMediaplayer.getCurrentPosition() > 4500) {
                mMediaplayer.seekTo(1);
            } else {
                InitmMediaplayer();
                InitmMediaplayer2();
                PlayRecord(getLastUrl(true));
            }
        }
    }

    public void pauseClick(View view) {
        if (getInfoRunTime)
            Toast.makeText(MainActivity.this, "מקש השהה", Toast.LENGTH_SHORT).show();
        getPlayVisible();
        if (FromVoice) {
            mMediaplayer2.pause();
        } else {
            mMediaplayer.pause();
        }
        pauseLastClick = true;
    }

    public void stopClick(View view) {
        if (getInfoRunTime)
            Toast.makeText(MainActivity.this, "מקש הפסק", Toast.LENGTH_SHORT).show();
        getPlayVisible();
        HideNameOfSong();
        if (FromVoice) {
            mMediaplayer2.setOnCompletionListener(null);
            pauseLastClick = false;
            mMediaplayer2.pause();
            mMediaplayer2.seekTo(1);
        } else {
            mMediaplayer.setOnCompletionListener(null);
            pauseLastClick = false;
            mMediaplayer.pause();
            mMediaplayer.seekTo(1);
        }

    }

    public void playClick(View view) {
        getPauseVisible();
        if (pauseLastClick) {
            if (FromVoice) {
                mMediaplayer2.start();
            } else {
                mMediaplayer.start();
            }
            pauseLastClick = false;
        } else {
            InitmMediaplayer();
            PlayRecord(getUrl(true));
        }

    }







    public void setHelloMsg(String msg) {
        tv.setText(msg);
//        switch (et.getText().toString().trim().toLowerCase()) {
//            case "csvelner@gmail.com":
//                tv.setText("חגי שמרית מעיין עדי ונדבוש - חופשה נעימה");
//                break;
//            case "velnera@gmail.com":
//                tv.setText("אסף מוריה אילת עמיחי אלעד ואיתן חופשה נעימה");
//                break;
//            case "efratyt@gmail.com":
//                tv.setText("תמר ישראל הראל נעם שירי יעלה ויאיר חופשה נעימה");
//                break;
//            case "divelner@gmail.com":
//                tv.setText("איתי דפנה אביה יפעה וגפן חופשה נעימה");
//                break;
//            case "arielave@gmail.com":
//                tv.setText("אמא היקרה חופשה נעימה");
//                break;
//            case "elivelner@gmail.com":
//                tv.setText("אבא שלנו חופשה נעימה");
//                break;
//            case "roeyvelner@gmail.com":
//                tv.setText("רועי חופשה נעימה");
//                break;
//            case "ortalylana@gmail.com":
//                tv.setText("אורטל חופשה נעימה");
//                break;
//        }

    }
    private void ClearHelloMsg(){
        tv.setText("");
    }



    public void getPauseVisible() {
        pause.setVisibility(View.VISIBLE);
        play.setVisibility(View.INVISIBLE);
    }

    public void getPlayVisible() {
        pauseLastClick = true;
        play.setVisibility(View.VISIBLE);
        pause.setVisibility(View.INVISIBLE);
    }





    private void OpenFileUploadeWindow() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        mview = getLayoutInflater().inflate(R.layout.upload_file, null);
        Button file = (Button) mview.findViewById(R.id.btn_file);
        Button record = (Button) mview.findViewById(R.id.btn_recoed);
        //Button btn_connect = (Button) mview.findViewById(R.id.btn_connect);

        final EditText from,to,number;
        from = (EditText) mview.findViewById(R.id.from);
        to = (EditText) mview.findViewById(R.id.to);
        number = (EditText) mview.findViewById(R.id.number);

        final Button saveBtn = (Button) mview.findViewById(R.id.saveBtn);
        final Button playRecord = (Button) mview.findViewById(R.id.playrecordBtn);
        final Button playFile = (Button) mview.findViewById(R.id.playFileBtn);
        builder.setView(mview);
        final AlertDialog dialog = builder.create();
        //btn_connect.setOnClickListener(new View.OnClickListener() {
            //@Override
          //  public void onClick(View v) {
          //      connectFiles();
        //    }
        //});
        file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickFile();
                playFile.setVisibility(View.VISIBLE);

            }
        });
        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {//when press
                        RecordVoice(from.getText().toString(), to.getText().toString(), number.getText().toString());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {//when let go
                    boolean res = stopRecording();
                    if (res)
                        playRecord.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        playRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playRecord.getText().toString().equals("לשמיעת ההקלטה")){
                    playChoosenFile(mFileNameForRecord);
                    playRecord.setText("עצור");
                }
                else{
                    playRecord.setText("לשמיעת ההקלטה");
                    stopCoosenFile();
                }


            }
            private void playChoosenFile(String path) {
                try {
                    mpForUpload = new MediaPlayer();
                    mpForUpload.setDataSource(getApplicationContext(),Uri.parse(path));
                    mpForUpload.prepare();
                    mpForUpload.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playRecord.setText("לשמיעת ההקלטה");
                            mpForUpload.setOnCompletionListener(null);
                        }
                    });
                    mpForUpload.start();
                    mpForUpload.setVolume(10, 10);
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        playFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playFile.getText().toString().equals("לשמיעת הקובץ")){
                    playChoosenFile("");
                    playFile.setText("עצור");
                }
                else{
                    playFile.setText("לשמיעת הקובץ");
                    stopCoosenFile();
                }
                //playChoosenFile(mFileNameForRecord);
            }
            private void playChoosenFile(String path) {
                try {
                    mpForUpload = new MediaPlayer();
                    if (path!=""){
                        mpForUpload.setDataSource(path);
                    }
                    else {
                        mpForUpload.setDataSource(getApplicationContext(), FileFromActivityResult);
                    }
                    mpForUpload.prepare();
                    mpForUpload.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            playFile.setText("לשמיעת הקובץ");
                            mpForUpload.setOnCompletionListener(null);
                        }
                    });
                    mpForUpload.start();
                    mpForUpload.setVolume(10, 10);
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFrom=from.getText().toString();
                mNumber=number.getText().toString();
                mTo=to.getText().toString();
                song = new Song (mNumber,"","",mFrom,mTo);
                uploadFiles();
                dialog.hide();
//                Intent i = new Intent(mview.getContext(),WebService.class);
//                startActivityForResult(i,2);
//                Toast.makeText(MainActivity.this,urlToUploadFromYouTube,Toast.LENGTH_LONG).show();

            }
//           protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//               MainActivity.super.onActivityResult(requestCode, resultCode, data);
//               if (resultCode == RESULT_OK && data != null) {
//                   switch (requestCode) {
//                       case 1:
//                           FileFromActivityResult  = data.getData();
//                           break;
//                   }
//               }
//           }
        }
        );



        dialog.show();


    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void connectFiles() {
        try {


            File file = new File(mFileNameForRecord);
            int size = (int) file.length();
            inputDataRecord  = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(inputDataRecord , 0, inputDataRecord .length);
                buf.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }




            WholeFile = new byte[inputDataRecord.length+ inputDataFile.length];
            for (int i=0; i<inputDataRecord.length;i++){
                WholeFile[i]= inputDataRecord[i];
            }
            for (int i=inputDataRecord.length; i<inputDataFile.length+inputDataRecord.length;i++){
                WholeFile[i]= inputDataFile[i-inputDataRecord.length];
            }
            File f = new File(mFileNameForRecord);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
            bos.write(WholeFile);
            bos.flush();
            bos.close();


        }
        catch (Exception e){

        }
        ;

    }

    private void uploadFiles() {
        fireBase.AddStorageToFireBase(FileFromActivityResult,mFileNameForRecord,false,song);
        //AddStorageToFireBase(Uri.parse(mFileNameForRecord), true);
    }


    private void RecordVoice(String from,String to,String number) {
        if (from.equals("")) from = "def";
        if (to.equals("")) to = "def";
        if (number.equals("")) number = "1";
        mFileNameForRecord = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Vacation";
        File f = new File(mFileNameForRecord);
        if (!f.exists()) f.mkdir();
        mFileNameForRecord += "/"+ from+"_"+to+"_"+number+".mp3";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileNameForRecord);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "בעיה בהקלטה", Toast.LENGTH_SHORT).show();
        }

        mRecorder.start();

    }

    private boolean stopRecording() {
    try{
        if (mRecorder!=null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        return true;
    }
    catch (Exception e){
        File file = new File(mFileNameForRecord);
        if (file.exists()) {
            file.delete();
        }
        Toast.makeText(MainActivity.this,"החזק את הכפתור להקלטה, שחרר לשמירה",Toast.LENGTH_LONG).show();
        return false;



    }

    }

//    private String getRealPathFromURI(Uri contentUri) {
//        String[] proj = { MediaStore.Audio.Media.DATA };
//        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
//        Cursor cursor = loader.loadInBackground();
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }

    private void stopCoosenFile() {
        if (mpForUpload != null){
            mpForUpload.stop();
            mpForUpload.release();
        }
    }

    private void PickFile(){
        try {
            //Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Files.getContentUri("external"));
//            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.getMediaScannerUri());
//            startActivityForResult(i, 1);
            Intent intent_upload = new Intent();
            intent_upload.setType("audio/*");
            intent_upload.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent_upload,1);
        } catch (Exception e) {
            String err = e.getMessage();
            Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 1:
                    FileFromActivityResult  = data.getData();
                    try {
                        InputStream iStream = getContentResolver().openInputStream(FileFromActivityResult);
                        inputDataFile = getBytes(iStream);
                    }
                    catch (Exception e){
                        Toast.makeText(MainActivity.this, "המרה של קובץ לבייטים לא הצליחה", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    urlToUploadFromYouTube = data.getExtras().getString( "url");
            }

        }
    }

    private byte[] getBytes(InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, "המרה של קובץ לבייטים לא הצליחה", Toast.LENGTH_SHORT).show();
        }
        return byteBuffer.toByteArray();
    }

    public static void ShowToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }


}

