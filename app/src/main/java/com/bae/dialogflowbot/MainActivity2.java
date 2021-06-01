package com.bae.dialogflowbot;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bae.dialogflowbot.adapters.ChatAdapter;
import com.bae.dialogflowbot.helpers.SendMessageInBg;
import com.bae.dialogflowbot.interfaces.BotReply;
import com.bae.dialogflowbot.models.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import android.speech.tts.TextToSpeech;

public class MainActivity2 extends AppCompatActivity implements BotReply {

    RecyclerView chatView;
    ChatAdapter chatAdapter;
    List<Message> messageList = new ArrayList<>();
    EditText editMessage;
    ImageButton btnSend;

    //dialogFlow
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private String TAG = "mainactivity";

    private EditText mEditText;

    private AlarmManager alarmManager;
    private GregorianCalendar mCalender;

    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;


    int no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        chatAdapter = new ChatAdapter(messageList, this);
        chatView.setAdapter(chatAdapter);

        SoundPlayer.initSounds(getApplicationContext());

        Intent intent = getIntent();
        no = intent.getIntExtra("daily", 0);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String message = editMessage.getText().toString();
                if (!message.isEmpty()) {
                    messageList.add(new Message(message, false));
                    editMessage.setText("");
                    sendMessageToBot(message);
                    Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                    Objects.requireNonNull(chatView.getLayoutManager())
                            .scrollToPosition(messageList.size() - 1);
                } else {
                    Toast.makeText(MainActivity2.this, "Please enter text!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        setUpBot();

        System.out.println(no);
        if (no == 1){
            String str = "daily";
            sendMessageToBot(str);
        }

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        mCalender = new GregorianCalendar();

        Log.v("HelloAlarmActivity", mCalender.getTime().toString());
    }


    private void setAlarm() {
        //AlarmReceiver에 값 전달
        Intent receiverIntent = new Intent(MainActivity2.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity2.this, 0, receiverIntent, 0);

        //String from = "2021-06-01 17:20:00"; //임의로 날짜와 시간을 지정

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        //SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

        System.out.println("current: " + df.format(cal.getTime()));

        cal.add(Calendar.DATE, 7);
        System.out.println("after: " + df.format(cal.getTime()));

        String getDate = df.format(cal.getTime());
        String from = getDate + " 10:00:00";
        System.out.println("from: " + from);


        //날짜 포맷을 바꿔주는 소스코드
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datetime = null;
        try {
            datetime = dateFormat.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datetime);

        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),pendingIntent);
    }


    private void setUpBot() {
        try {
            InputStream stream = this.getResources().openRawResource(R.raw.chatbot2_credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    private void sendMessageToBot(String message) {
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
        new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
    }


    String botReply;



    //Emergency Pop-up
    Dialog emergencydialog;
    public void showDialog01(){
        emergencydialog.show(); // 다이얼로그 띄우기

        TextView txt = emergencydialog.findViewById(R.id.txtText);
        txt.setText("전문적인 치료가 필요할 것 같아요. 전화를 연결하시겠어요?");
        /* 이 함수 안에 원하는 디자인과 기능을 구현하면 된다. */

        // 위젯 연결 방식은 각자 취향대로~
        // '아래 아니오 버튼'처럼 일반적인 방법대로 연결하면 재사용에 용이하고,
        // '아래 네 버튼'처럼 바로 연결하면 일회성으로 사용하기 편함.
        // *주의할 점: findViewById()를 쓸 때는 -> 앞에 반드시 다이얼로그 이름을 붙여야 한다.

        // 아니오 버튼
        Button noBtn = emergencydialog.findViewById(R.id.Button2);
        noBtn.setText("종료하기");
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                emergencydialog.dismiss();
                finish(); // 앱 종료
            }
        });
        // 네 버튼
        Button yesBtn = emergencydialog.findViewById(R.id.Button1);
        yesBtn.setText("전화하기");
        emergencydialog.findViewById(R.id.Button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 원하는 기능 구현
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel: 119"));
                startActivity(intent);
                emergencydialog.dismiss();
                finish();
            }
        });
    }


    @Override
    public void callback(DetectIntentResponse returnResponse) {
        if(returnResponse!=null) {
            botReply = returnResponse.getQueryResult().getFulfillmentText();

            if(!botReply.isEmpty()) {
                ArrayList<String> colors = new ArrayList<>(Arrays.asList("positive", "negative", "no emotion"));
                ArrayList<String> emos = new ArrayList<>(Arrays.asList("positive", "negative", "no emotion"));

                if (no == 1) {
                    if (botReply.equals("start daily") || botReply.equals("노래 추천")) {
                    }
                    else if (!emos.contains(botReply) && botReply.contains("http")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(botReply));
                        startActivity(intent);
                    }
                    else if(botReply.contains("사진 출력")){
                        messageList.add(new Message(botReply, true));
                        chatAdapter.notifyDataSetChanged();
                        Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
                    }
                    else {
                        if (botReply.equals("positive")) {

                            SoundPlayer.play(SoundPlayer.POSITIVE);
                            botReply = "다행이네요, 좋아보이셔서 저도 행복해지네요!";
                        }
                        if (botReply.equals("negative")) {
                            SoundPlayer.play(SoundPlayer.NEGATIVE);
                            botReply = "아이고, 괜찮아요. 곧 모든 게 나아질 거에요! 이 사운드를 듣고 기분이 풀렸으면 좋겠어요.";
                        }
                        if (botReply.equals("no emotion")) {
                            SoundPlayer.play(SoundPlayer.NO_EMOTION);
                            botReply = "그렇군요! 제가 내담자님에게 조금이라도 도움이 되었으면 좋겠어요!";
                        }
                        messageList.add(new Message(botReply, true));
                        chatAdapter.notifyDataSetChanged();
                        Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
                    }

                }
                else {
                    if (botReply.equals("Warning!")) {
                        emergencydialog = new Dialog(MainActivity2.this);       // Dialog 초기화
                        emergencydialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
                        emergencydialog.setContentView(R.layout.activity_popup);

                        showDialog01();
                        messageList.add(new Message(botReply, true));
                    }
                    else {
                        messageList.add(new Message(botReply, true));
                        chatAdapter.notifyDataSetChanged();
                        Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
                    }

                }
            }else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "failed to connect!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        setAlarm();
    }
}
