package mobilancer.com.ulutekyemek;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import adapters.CommentAdapter;
import pojo.Comment;
import pojo.DailyMenu;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView commentListview;
    List<Comment> commentData;
    CommentAdapter commentAdapter;
    List<DailyMenu> dailyMenuList;
    DailyMenu dailyMenu;
    DataSnapshot dailySnapshot;
    DatabaseReference mDbRef;
    LinearLayout loadingLayout,menuLayout, emptyMenuLayout;
    TextView txtSoup, txtMainCourse, txtWarmStarter, txtDesert,txtDate;
    TextView txtLike, txtDislike;
    ImageView imgBtnLike, imgBtnDislike, imgBtnSendComment;
    EditText etComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        commentListview = (ListView) findViewById(R.id.lstComments);
        loadingLayout = (LinearLayout) findViewById(R.id.loadingLayout);
        menuLayout = (LinearLayout) findViewById(R.id.menuLayout);
        txtMainCourse = (TextView) findViewById(R.id.txtMainCourse);
        txtSoup = (TextView) findViewById(R.id.txtSoup);
        txtWarmStarter = (TextView) findViewById(R.id.txtWarmStarter);
        txtDesert = (TextView) findViewById(R.id.txtDesert);
        emptyMenuLayout = (LinearLayout) findViewById(R.id.emptyMenuLayout);
        txtDate = (TextView) findViewById(R.id.txtDate);
        imgBtnLike = (ImageView) findViewById(R.id.imgbtnLike);
        imgBtnDislike = (ImageView) findViewById(R.id.imgBtnDisLike);
        imgBtnSendComment = (ImageView) findViewById(R.id.imgbtnSendComment);
        etComment = (EditText) findViewById(R.id.etComment);
        txtLike = (TextView) findViewById(R.id.txtLike);
        txtDislike = (TextView) findViewById(R.id.txtDislike);


        setSupportActionBar(toolbar);
        Calendar today  = Calendar.getInstance();
        GregorianCalendar greg = new GregorianCalendar(today.get(Calendar.YEAR), today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, EEEE");
        Date date = new Date(greg.getTimeInMillis());
        txtDate.setText(dateFormat.format(date));

        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mainRef = fbDatabase.getReference("menus");

        mDbRef = FirebaseDatabase.getInstance().getReference();

        /*
        mDbRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dailyMenuList == null){
                            dailyMenuList = new ArrayList<DailyMenu>();
                            Map<String,DailyMenu> data =  (Map<String,DailyMenu>) dataSnapshot.getValue();
                            for(Map.Entry<String, DailyMenu> entry : data.entrySet()){
                                DailyMenu currentDm = dataSnapshot.child(entry.getKey()).getValue(DailyMenu.class);
                                dailyMenuList.add(currentDm);
                            }
                        }

                        //Toast.makeText(MainActivity.this, "Menü alındı : "+dailyMenuList.size(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, "Veri alındı", Toast.LENGTH_SHORT).show();
                        //Get map of users in datasnapshot

                        //commentData = dataSnapshot.child("-KWjQ3w17E0RoBnHhFX-").getValue(DailyMenu.class).getComments();
                        //commentAdapter = new CommentAdapter(getApplicationContext(),commentData);
                        //commentListview.setAdapter(commentAdapter);
                        //List<Comment> comments = dm.getComments();
                        //int x = 0;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                     //   handle databaseError
                    }
                });
                */

        mDbRef.child("menus").orderByChild("dateInfo").equalTo(greg.getTimeInMillis()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    dailySnapshot = postSnapshot;
                }
                loadingLayout.setVisibility(View.GONE);

                if(dailySnapshot == null){
                    emptyMenuLayout.setVisibility(View.VISIBLE);
                }else{

                    DatabaseReference refComm = FirebaseDatabase.getInstance().getReference("menus/"+dailySnapshot.getKey());
                    refComm.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dailyMenu = dataSnapshot.getValue(DailyMenu.class);

                            // Setting Like and Dislike values
                            txtLike.setText(String.valueOf(dailyMenu.getLike()));
                            txtDislike.setText(String.valueOf(dailyMenu.getDislike()));

                            // Setting main course as MainCourse | AlternativeMainCourse depending on the alternative mc is exist.
                            if(dailyMenu.getAlternativeMainCourse() == null || dailyMenu.getAlternativeMainCourse().isEmpty()){
                                txtMainCourse.setText(dailyMenu.getMainCourse().toUpperCase());
                            }else{
                                txtMainCourse.setText(dailyMenu.getMainCourse().toUpperCase() + " | "+dailyMenu.getAlternativeMainCourse().toUpperCase());
                            }
                            txtWarmStarter.setText(dailyMenu.getWarmStarter());
                            txtSoup.setText(dailyMenu.getSoup());

                            // Setting desert as desert | AlternativeDeser depending on the alternative desert is exist.
                            if(dailyMenu.getAlternativeDesert() == null || dailyMenu.getAlternativeDesert().isEmpty()){
                                txtDesert.setText(dailyMenu.getDesert());
                            }else{
                                txtDesert.setText(dailyMenu.getDesert() + " | "+dailyMenu.getAlternativeDesert());
                            }

                            menuLayout.setVisibility(View.VISIBLE);
                            dailySnapshot = dataSnapshot;

                            // Setting comments
                            List<Comment> commentList = dailyMenu.getComments();
                            if(commentAdapter == null){
                                commentData = new ArrayList<Comment>();
                                if(commentList != null)
                                    commentData.addAll(commentList);

                                commentAdapter = new CommentAdapter(getApplicationContext(),commentData);
                                commentListview.setAdapter(commentAdapter);
                            }else{
                                commentData.clear();
                                if(commentList != null)
                                    commentData.addAll(commentList);
                                commentAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        imgBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyMenu.setLike(dailyMenu.getLike() + 1);
                txtLike.setText(dailyMenu.getLike()+"");
                dailySnapshot.getRef().setValue(dailyMenu);

            }
        });

        imgBtnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyMenu.setDislike(dailyMenu.getDislike() + 1);
                txtDislike.setText(dailyMenu.getDislike()+"");
                dailySnapshot.getRef().setValue(dailyMenu);
            }
        });

        imgBtnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comment comment = new Comment();
                comment.setCommentText(etComment.getText().toString());
                comment.setNickname("Admin");
                List<Comment> comments =  dailyMenu.getComments();
                if(comments == null){
                    comments = new ArrayList<Comment>();
                }
                comments.add(comment);
                dailyMenu.setComments(comments);
                dailySnapshot.getRef().setValue(dailyMenu);
                Toast.makeText(MainActivity.this, "Yorumunuz gönderildi", Toast.LENGTH_SHORT).show();
            }
        });


        //String[] dt = new String[]{"Lorem ipsum dolor sit amet","Bu bir başka yorum","Olaylar olaylar yorumları","Can sıkıntısı yorumu 3","Deneme birki","Yorum metni deneme","bu yemekler bir harika dostum","ne biçim yemek","yemek yememek"};
        //ArrayAdapter<String> aa = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,dt);
        //commentListview.setAdapter(aa);
    /*
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("message");
            myRef.setValue("Tecrübe ;)");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String value = dataSnapshot.getValue(String.class);
                    Toast.makeText(MainActivity.this, "Değişen değer : "+value, Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "Value is: " + value);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    //Log.w(TAG, "Failed to read value.", error.toException());
                    Toast.makeText(MainActivity.this, "Değişen değer okunamadı", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            Toast.makeText(this, "Hata oluştu", Toast.LENGTH_SHORT).show();
            Log.e("log-mobilancer",e.getMessage());
        }*/
    }

    public void onclickMain(View v){
        /*

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mainRef = database.getReference();
        DatabaseReference menuRef = mainRef.child("menus");

        GregorianCalendar greg;
        List<DailyMenu> dmL = new ArrayList<DailyMenu>();


        DailyMenu dm1 = new DailyMenu();
        dm1.setSoup("Domates Çorbası");
        dm1.setMainCourse("Piliç Kavurma");
        dm1.setAlternativeMainCourse("Misket Köfte");
        dm1.setWarmStarter("Bulgur Pilavı");
        dm1.setDesert("Brownie");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,16);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);

        dm1 = new DailyMenu();
        dm1.setSoup("Mercimek Çorbası");
        dm1.setMainCourse("Ispanak Sufle");
        dm1.setAlternativeMainCourse("Sosis Sote");
        dm1.setWarmStarter("Kavurma Erişte");
        dm1.setDesert("Kalburabastı");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,17);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);

        dm1 = new DailyMenu();
        dm1.setSoup("Yayla Çorbası");
        dm1.setMainCourse("Kanat Izgara");
        dm1.setAlternativeMainCourse("Hasanpaşa Köfte");
        dm1.setWarmStarter("Pirinç Pilavı");
        dm1.setDesert("Keşkül");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,18);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);

        dm1 = new DailyMenu();
        dm1.setSoup("Mercimek Çorbası");
        dm1.setMainCourse("Kuru Fasulye");
        dm1.setAlternativeMainCourse("Ispanak Yoğurtlu");
        dm1.setWarmStarter("Pirinç Pilavı");
        dm1.setDesert("Şekerpare");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,21);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);

        dm1 = new DailyMenu();
        dm1.setSoup("Tarhana Çorbası");
        dm1.setMainCourse("Püreli Kebap");
        dm1.setAlternativeMainCourse("Arnavut Ciğer");
        dm1.setWarmStarter("Patatesli Börek");
        dm1.setDesert("Kadayıflı Muhallebi");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,22);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);


        dm1 = new DailyMenu();
        dm1.setSoup("Domates Çorba");
        dm1.setMainCourse("Piliç Izgara");
        dm1.setAlternativeMainCourse("Izgara Köfte");
        dm1.setWarmStarter("Makarna");
        dm1.setDesert("Krem Karamel");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,23);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);

        dm1 = new DailyMenu();
        dm1.setSoup("Tavuksuyu Çorba");
        dm1.setMainCourse("Kıymalı Yumurta");
        dm1.setAlternativeMainCourse("Patates Musakka");
        dm1.setWarmStarter("Bulgur Pilavı");
        dm1.setDesert("Peynir Tatlısı");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,24);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);

        dm1 = new DailyMenu();
        dm1.setSoup("Ezogelin Çorba");
        dm1.setMainCourse("Bahçivan Köfte");
        dm1.setAlternativeMainCourse("Piliç Samanyolu");
        dm1.setWarmStarter("Spagetti");
        dm1.setDesert("Sakızlı Muhallebi");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,25);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);

        dm1 = new DailyMenu();
        dm1.setSoup("Mantar Çorba");
        dm1.setMainCourse("Nohut");
        dm1.setAlternativeMainCourse("Etli Kapuska");
        dm1.setWarmStarter("Pirinç Pilavı");
        dm1.setDesert("Süt Helvası");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,28);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);


        dm1 = new DailyMenu();
        dm1.setSoup("Tarhana Çorba");
        dm1.setMainCourse("Piliç Izgara");
        dm1.setAlternativeMainCourse("Domates Soslu Köfte");
        dm1.setWarmStarter("Bulgur Pilavı");
        dm1.setDesert("Tiramisu");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,29);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);

        dm1 = new DailyMenu();
        dm1.setSoup("Tavuksuyu Çorba");
        dm1.setMainCourse("Tavuklu Bezelye");
        dm1.setAlternativeMainCourse("Patlıcan Musakka");
        dm1.setWarmStarter("Makarna");
        dm1.setDesert("Şöbiyet");
        dm1.setAlternativeDesert("Meyve");
        dm1.setComments(new ArrayList<Comment>());
        greg = new GregorianCalendar(2016, Calendar.NOVEMBER,30);
        dm1.setDateInfo(greg.getTime().getTime());
        dm1.setLike(0);
        dm1.setDislike(0);
        dmL.add(dm1);
        
        try {

            for(DailyMenu dm : dmL){
                DatabaseReference newRecordRef = menuRef.push();
                newRecordRef.setValue(dm);
            }

            Toast.makeText(this, "Kayıtlar atıldı", Toast.LENGTH_SHORT).show();


        }catch (Exception e){
            e.printStackTrace();
        }

        */

    }

}
