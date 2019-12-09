package com.javosoft.burgeron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#D00113"));
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Contact");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void twitterOnClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Javohir_777"));
        startActivity(browserIntent);
    }

    public void vkOnClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/javo_xabi"));
        startActivity(browserIntent);
    }

    public void whatsappOnClick(View v) {
        String url = "https://api.whatsapp.com/send?phone="+"+79021074202";
        String urlApp = "https://play.google.com/store/apps/details?id=com.whatsapp&hl=ru";
        boolean installed = appInstalledOrNot("com.whatsapp");
        if(installed) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            Intent a = new Intent(Intent.ACTION_VIEW);
            a.setData(Uri.parse(urlApp));
            startActivity(a);
        }

    }

    public void telegramOnClick(View v) {
        String url = "https://t.me/javo_xabi";
        String urlApp = "https://play.google.com/store/apps/details?id=org.telegram.messenger&hl=ru";
        boolean installed = appInstalledOrNot("org.telegram.messenger");
        if(installed) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            Intent a = new Intent(Intent.ACTION_VIEW);
            a.setData(Uri.parse(urlApp));
            startActivity(a);
        }

    }

    public void instagramOnClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/javosoft_games"));
        startActivity(browserIntent);
    }

    public void emailOnClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","ultracers200@gmail.com", null));
        startActivity(browserIntent);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return true;
    }
}
