package com.example.mappe1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class HovedAktivitet extends AppCompatActivity {
    EditText tekst;
    String[] ord_listen;
    ArrayList<String> brukt_ord = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktivitet_hoved);
        Resources res = getResources();
        ord_listen = res.getStringArray(R.array.ord_liste);
        tekst = (EditText) findViewById(R.id.innlegg_felt);
        ((TextView)findViewById(R.id.antall_gjort)).setText("0/" + String.valueOf(ord_listen.length));
        TextView tv = (TextView) findViewById(R.id.funnet_ord);
        tv.setMovementMethod(new ScrollingMovementMethod());
    }


    public void leggTilBokstav(View v) {
        Button b  = (Button) v;
        tekst.setText(tekst.getText().toString() + b.getText());
    }

    @Override
    protected void onSaveInstanceState(Bundle outsate) {
        super.onSaveInstanceState(outsate);
        outsate.putStringArrayList("brukt_ord", brukt_ord);
        TextView tw = (TextView)findViewById(R.id.tekst_felt);
        outsate.putString("hint", tw.getText().toString());
        TextView fo = (TextView)findViewById(R.id.funnet_ord);
        outsate.putString("funnet_ord", fo.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        brukt_ord = savedInstanceState.getStringArrayList("brukt_ord");
        TextView tw = (TextView)findViewById(R.id.tekst_felt);
        tw.setText(savedInstanceState.getString("hint"));
        tw.setVisibility(TextView.VISIBLE);
        TextView fo = (TextView)findViewById(R.id.funnet_ord);
        fo.setText(savedInstanceState.getString("funnet_ord"));

        ((TextView)findViewById(R.id.antall_gjort)).setText(String.valueOf(brukt_ord.toArray().length) + "/" + String.valueOf(ord_listen.length));
    }

    public void slettInnleggData(View v) {
        tekst.setText("");
    }

    public void sjekkOrdet(View v) {
        TextView t = (TextView) findViewById(R.id.tekst_felt);
        Button b = (Button) findViewById(R.id.sentrum);
        String gjelendeTekst = tekst.getText().toString().toUpperCase();

        if (gjelendeTekst.length() < 4) {
            visTilbakeMelding("Ordet må være lengere enn 4!");
            return;
        }
        if (!gjelendeTekst.contains(b.getText())) {
            visTilbakeMelding("Ordet må inneholde " + b.getText() + "!");
            return;
        }
        boolean fantOrd = false;
        for (String ord : ord_listen) {
            if (ord.equals(gjelendeTekst)) {
                if (brukt_ord.contains(ord)) {
                    visTilbakeMelding("Du har allerede funnet dette ordet!");
                    fantOrd = true;
                    break;
                }
                visTilbakeMelding("Bra jobbet!");
                brukt_ord.add(ord);
                ((TextView)findViewById(R.id.antall_gjort)).setText(String.valueOf(brukt_ord.toArray().length) + "/" + String.valueOf(ord_listen.length));
                TextView fo = (TextView)findViewById(R.id.funnet_ord);
                fo.setText(fo.getText() + ord + ", ");
                tekst.setText("");
                fantOrd = true;
            }
        }
        if (!fantOrd) {
            tekst.setText("");
            visTilbakeMelding("Beklager, ordet finnes ikke i listen!");
        }
    }

    public void visHint(View v) {
        TextView t = (TextView) findViewById(R.id.tekst_felt);
        t.setVisibility(View.VISIBLE);

        StringBuilder hint = new StringBuilder(finnHint());
        if (!hint.toString().equals("")) {
            hint.setCharAt(1, '*');
            hint.setCharAt(2, '*');
        }
        t.setText("Hint: " + hint.toString());
    }

    private String finnHint() {
        if (brukt_ord.toArray().length == ord_listen.length) return "";
        String s = "";
        while (true) {
            s = ord_listen[new Random().nextInt(ord_listen.length)];
            if (!brukt_ord.contains(s)) return s;
        }
    }
    public void visTilbakeMelding(String tekst) {
        LayoutInflater infalter = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = infalter.inflate(R.layout.tilbake_melding, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);
        TextView v = (TextView) popupWindow.getContentView().findViewById(R.id.melding_tekst);
        v.setText(tekst);

        popupWindow.showAtLocation(findViewById(R.id.sjekk), Gravity.CENTER, 0, -500);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                popupWindow.dismiss();
            }
        }, 2 *1000);
    }
}