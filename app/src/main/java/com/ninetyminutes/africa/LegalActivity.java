package com.ninetyminutes.africa;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LegalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        TextView back = findViewById(R.id.legalBack);
        TextView title = findViewById(R.id.legalTitle);
        TextView content = findViewById(R.id.legalContent);

        back.setOnClickListener(v -> finish());

        String page = getIntent().getStringExtra("page");

        if (page == null) {
            page = "about";
        }

        switch (page) {
            case "terms":
                title.setText("TERMS & CONDITIONS");
                content.setText(
                        "Masharti na Vigezo\n\n" +
                        "Kwa kutumia 90' Minutes Africa, unakubali kutumia huduma hii kwa njia halali na yenye kuheshimu watumiaji wengine.\n\n" +
                        "Maudhui yote yanayotolewa kwenye jukwaa hili yanalenga kutoa habari na taarifa za michezo. 90' Minutes Africa inaweza kubadilisha au kuboresha huduma na maudhui wakati wowote.\n\n" +
                        "Mtumiaji anawajibika kuhakikisha matumizi yake ya huduma hayakiuki sheria au haki za wengine."
                );
                break;

            case "privacy":
                title.setText("PRIVACY POLICY");
                content.setText(
                        "Sera ya Faragha\n\n" +
                        "90' Minutes Africa inaheshimu faragha ya watumiaji wake.\n\n" +
                        "Taarifa zinazokusanywa zinaweza kutumika kuboresha huduma, kuelewa matumizi ya jukwaa na kutoa uzoefu bora kwa mtumiaji.\n\n" +
                        "Hatutumii taarifa zako kwa madhumuni yasiyohusiana na huduma bila sababu halali au idhini inapohitajika."
                );
                break;

            case "cookies":
                title.setText("COOKIE POLICY");
                content.setText(
                        "Sera ya Cookies\n\n" +
                        "Website ya 90' Minutes Africa inaweza kutumia cookies na teknolojia zinazofanana kusaidia kuboresha utendaji wa website, kuelewa matumizi ya wageni na kuboresha huduma.\n\n" +
                        "Unaweza kudhibiti cookies kupitia mipangilio ya browser yako."
                );
                break;

            case "disclaimer":
                title.setText("DISCLAIMER");
                content.setText(
                        "Disclaimer\n\n" +
                        "90' Minutes Africa hutoa habari za michezo kwa madhumuni ya taarifa kwa umma.\n\n" +
                        "Ingawa tunajitahidi kuhakikisha taarifa ni sahihi na za wakati, hatuwezi kuhakikisha kwamba kila taarifa itakuwa sahihi au kamili wakati wote.\n\n" +
                        "Watumiaji wanashauriwa kuthibitisha taarifa muhimu kupitia vyanzo rasmi."
                );
                break;

            case "contact":
                title.setText("CONTACT US");
                content.setText(
                        "Wasiliana Nasi\n\n" +
                        "90' Minutes Africa\n\n" +
                        "Kwa maswali, maoni, taarifa za habari au ushirikiano, unaweza kuwasiliana nasi kupitia njia rasmi za mawasiliano za 90' Minutes Africa.\n\n" +
                        "WhatsApp Channel:\n" +
                        "https://whatsapp.com/channel/0029Vb6mtUXDjiOZZOGkCw00"
                );
                break;

            default:
                title.setText("ABOUT US");
                content.setText(
                        "Kuhusu 90' Minutes Africa\n\n" +
                        "90' Minutes Africa ni chanzo cha habari za mpira Tanzania, Afrika na duniani.\n\n" +
                        "Tunakuletea habari, matokeo, ratiba, misimamo, usajili na taarifa mbalimbali za soka kwa wakati.\n\n" +
                        "Lengo letu ni kuwafikishia mashabiki wa soka taarifa muhimu na matukio ya uwanjani kwa haraka na kwa urahisi."
                );
                break;
        }
    }
}
