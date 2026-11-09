package com.example.chat;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import java.util.Random;

public class MainActivity extends Activity {
    LinearLayout list;
    ScrollView scroll;
    EditText input;
    SharedPreferences prefs;
    JSONArray history = new JSONArray();
    String[] replies = {
        "Intéressant, dis-m'en plus !", "Je suis d'accord.",
        "Hmm, je réfléchis...", "Ah oui ?", "Super !", "Raconte-moi la suite."
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("chat", MODE_PRIVATE);
        try { history = new JSONArray(prefs.getString("h", "[]")); } catch (Exception e) {}

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        scroll = new ScrollView(this);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(16, 16, 16, 16);
        scroll.addView(list);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        LinearLayout bar = new LinearLayout(this);
        input = new EditText(this);
        input.setHint("Message...");
        Button send = new Button(this);
        send.setText("Envoyer");
        bar.addView(input, new LinearLayout.LayoutParams(0, -2, 1f));
        bar.addView(send);
        root.addView(bar);
        setContentView(root);

        for (int i = 0; i < history.length(); i++) {
            String s = history.optString(i);
            if (s.length() > 2) show(s.substring(2), s.startsWith("1"));
        }

        send.setOnClickListener(v -> {
            String t = input.getText().toString().trim();
            if (t.isEmpty()) return;
            input.setText("");
            add(t, true);
            new Handler(Looper.getMainLooper()).postDelayed(
                () -> add(replies[new Random().nextInt(replies.length)], false), 600);
        });

        send.setOnLongClickListener(v -> {
            history = new JSONArray();
            prefs.edit().remove("h").apply();
            list.removeAllViews();
            return true;
        });
    }

    void add(String text, boolean mine) {
        history.put((mine ? "1:" : "0:") + text);
        prefs.edit().putString("h", history.toString()).apply();
        show(text, mine);
    }

    void show(String text, boolean mine) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(16);
        tv.setPadding(28, 18, 28, 18);
        tv.setTextColor(mine ? Color.WHITE : Color.BLACK);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(40);
        bg.setColor(mine ? Color.parseColor("#1976D2") : Color.parseColor("#E0E0E0"));
        tv.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.gravity = mine ? Gravity.END : Gravity.START;
        lp.setMargins(0, 8, 0, 8);
        list.addView(tv, lp);
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }
          }
