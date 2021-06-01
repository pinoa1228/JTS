package com.bae.dialogflowbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FrontPageActivity extends AppCompatActivity {

    int daily = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        ImageButton b =findViewById(R.id.imageButton);
        ImageButton b2=findViewById(R.id.imageButton2);

        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:119"));
                startActivity(intent);

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

                Intent intent =new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:1577-0199"));
                startActivity(intent);



            }
        });

    }
    public void clickBtn(View view){
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("data", "음성 기능을 사용하시겠어요?");
        daily = 0;
        startActivityForResult(intent, 1);
    }

    public void clickDailyBtn(View view){
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("data", "음성 기능을 사용하시겠어요?");
        daily = 1;
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int RESULT_Voice = 0;
        if (requestCode == 1) {
            if (resultCode == RESULT_Voice) {
                //데이터 받기
                Intent intent = new Intent(this, MainActivity.class);
                if(daily == 1){
                    intent.putExtra("daily", 1) ;
                }
                else{
                    intent.putExtra("daily", 0) ;
                }
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(this, MainActivity2.class);
                if(daily == 1){
                    intent.putExtra("daily", 1) ;
                }
                else{
                    intent.putExtra("daily", 0) ;
                }
                startActivity(intent);
            }
        }
    }

}