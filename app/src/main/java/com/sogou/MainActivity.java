package com.sogou;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.sogou.daemon.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setEnabled(false);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, MainActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, i,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//
//                new NotifyResidentService.Builder(MainActivity.this)
//                        .smallIconId(R.drawable.notify_panel_notification_icon_bg)
//                        .title(getApplicationInfo().loadLabel(getPackageManager()))
//                        .text("Hello, world!")
//                        .importance(NotificationManager.IMPORTANCE_NONE)
//                        .pendingIntent(pi)
//                        .fire();
            }
        });
    }
}
