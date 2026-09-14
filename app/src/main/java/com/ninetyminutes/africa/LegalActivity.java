package com.ninetyminutes.africa;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LegalActivity extends AppCompatActivity {

    private TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal);

        TextView back = findViewById(R.id.legalBack);
        TextView title = findViewById(R.id.legalTitle);
        content = findViewById(R.id.legalContent);

        back.setOnClickListener(v -> finish());

        String page = getIntent().getStringExtra("page");

        if (page == null) {
            page = "about";
        }

        switch (page) {

            case "terms":
                title.setText("TERMS & CONDITIONS");

                setLegalContent(
                        "Terms & Conditions",
                        "Last updated: August 2026",

                        "1. Utangulizi",
                        "Karibu 90' Minutes Africa. Kwa kutumia website au app ya 90' Minutes Africa, unakubali kufuata Terms & Conditions hizi. Ikiwa hukubaliani na masharti haya, tafadhali usitumie huduma zetu.",

                        "2. Matumizi ya Huduma",
                        "90' Minutes Africa hutoa habari za soka, ratiba za michezo, matokeo, misimamo, taarifa za usajili na maudhui mengine yanayohusiana na mpira wa miguu. Unakubali kutumia huduma hizi kwa madhumuni halali na kwa kuheshimu watumiaji wengine.",

                        "3. Maudhui",
                        "Tunajitahidi kuhakikisha taarifa tunazochapisha ni sahihi na zinaaminika. Hata hivyo, taarifa zinaweza kubadilika au kuwa na makosa bila kukusudia. 90' Minutes Africa haihakikishi kwamba kila taarifa itakuwa sahihi, kamili au ya sasa wakati wote.",

                        "4. Matumizi Yasiyoruhusiwa",
                        "Hairuhusiwi kutumia website au app hii kwa shughuli haramu, kujaribu kuvamia au kuharibu mfumo, kusambaza maudhui yenye madhara, kuingilia utendaji wa huduma au kutumia maudhui kwa njia inayokiuka sheria.",

                        "5. Viungo vya Tovuti Nyingine",
                        "Huduma zetu zinaweza kuwa na viungo vinavyoelekeza kwenye tovuti au huduma za watu wengine. Hatuwajibiki kwa maudhui, sera za faragha au matumizi ya tovuti hizo za third party.",

                        "6. Mabadiliko ya Masharti",
                        "Tunaweza kusasisha Terms & Conditions hizi wakati wowote. Mabadiliko yataanza kutumika baada ya kuwekwa kwenye website au app.",

                        "7. Mawasiliano",
                        "Ikiwa una swali kuhusu Terms & Conditions hizi, unaweza kuwasiliana na 90' Minutes Africa kupitia ukurasa wa Contact Us."
                );
                break;

            case "privacy":
                title.setText("PRIVACY POLICY");

                setLegalContent(
                        "Privacy Policy",
                        "Last updated: August 2026",

                        "1. Utangulizi",
                        "90' Minutes Africa inaheshimu faragha ya watumiaji wake. Sera hii inaeleza aina ya taarifa zinazoweza kukusanywa unapotumia website au huduma zetu, namna tunavyotumia taarifa hizo na huduma za third party zinazoweza kutumika.",

                        "2. Taarifa Tunazokusanya",
                        "Tunaweza kukusanya taarifa unazotupatia kwa hiari, kwa mfano unapowasiliana nasi kupitia Contact Us au unapowasilisha maoni kwenye maudhui yetu.\n\nWebsite inaweza pia kukusanya taarifa za kiufundi kama aina ya kifaa, browser, mfumo wa uendeshaji, anwani ya IP, kurasa ulizotembelea na taarifa zinazohusiana na matumizi ya website.",

                        "3. Jinsi Tunavyotumia Taarifa",
                        "Taarifa zinaweza kutumika kuendesha, kudumisha na kuboresha website, kuelewa namna wageni wanavyotumia huduma, kuboresha usalama, kujibu mawasiliano na kuboresha uzoefu wa mtumiaji.",

                        "4. Google Analytics",
                        "Tunatumia Google Analytics kusaidia kuelewa namna watumiaji wanavyotumia website, kama vile kurasa zinazotembelewa, idadi ya watumiaji na taarifa za matumizi ya website. Google Analytics inaweza kutumia cookies au teknolojia zinazofanana kulingana na mipangilio ya huduma hiyo.",

                        "5. Google AdSense na Matangazo",
                        "Website hii inaweza kutumia Google AdSense kuonyesha matangazo. Google na washirika wake wa matangazo wanaweza kutumia cookies, web beacons au teknolojia nyingine kusaidia kuonyesha na kupima matangazo, kulingana na mipangilio na chaguo za mtumiaji.\n\nMatangazo yanaweza kuwa personalized au non-personalized kulingana na eneo la mtumiaji, mipangilio ya faragha, ridhaa inayohitajika na sera za huduma husika.",

                        "6. Third-Party Services",
                        "Baadhi ya huduma kwenye website zinaweza kutolewa na third parties, ikiwa ni pamoja na huduma za analytics, advertising, hosting, database au social media. Third parties hao wanaweza kukusanya au kuchakata taarifa kulingana na sera zao za faragha.",

                        "7. Cookies",
                        "Tunatumia cookies na teknolojia zinazofanana kusaidia website kufanya kazi, kuhifadhi baadhi ya mipangilio, kuelewa matumizi ya website na kusaidia huduma za analytics na advertising.\n\nUnaweza kudhibiti au kufuta cookies kupitia mipangilio ya browser yako. Kuzima baadhi ya cookies kunaweza kuathiri utendaji wa baadhi ya vipengele vya website.",

                        "8. Maoni na Mawasiliano",
                        "Ikiwa utaacha maoni au kuwasiliana nasi, taarifa utakazotoa zinaweza kuhifadhiwa na kutumiwa kwa madhumuni ya kujibu mawasiliano yako, kudhibiti maudhui na kulinda website dhidi ya matumizi mabaya.",

                        "9. Usalama wa Taarifa",
                        "Tunachukua hatua zinazofaa kulinda taarifa zinazohusiana na huduma zetu. Hata hivyo, hakuna mfumo wa internet unaoweza kuhakikishwa kuwa salama kwa asilimia 100.",

                        "10. Viungo vya Tovuti Nyingine",
                        "Website inaweza kuwa na viungo vinavyoelekeza kwenye tovuti za watu wengine. Hatuwajibiki kwa sera za faragha, maudhui au taratibu za tovuti hizo. Tunashauri watumiaji kusoma sera zao za faragha kabla ya kutumia huduma zao.",

                        "11. Faragha ya Watoto",
                        "Website hii haikusudii kukusanya kwa makusudi taarifa binafsi kutoka kwa watoto. Ikiwa unaamini mtoto ametoa taarifa binafsi bila idhini inayofaa, tafadhali wasiliana nasi.",

                        "12. Mabadiliko ya Privacy Policy",
                        "Tunaweza kusasisha Privacy Policy hii mara kwa mara ili kuendana na mabadiliko ya huduma, teknolojia au mahitaji ya kisheria. Tarehe ya mwisho ya kusasishwa itaonyeshwa juu ya ukurasa huu.",

                        "13. Mawasiliano",
                        "Ikiwa una swali kuhusu Privacy Policy hii au namna tunavyotumia taarifa, unaweza kuwasiliana nasi kupitia ukurasa wa Contact Us."
                );
                break;

            case "cookies":
                title.setText("COOKIE POLICY");

                setLegalContent(
                        "Cookie Policy",
                        "Last updated: August 2026",

                        "1. Cookies ni nini?",
                        "Cookies ni taarifa ndogo zinazoweza kuhifadhiwa kwenye kifaa chako wakati unapotembelea website.",

                        "2. Tunazitumia kwa nini?",
                        "Cookies zinaweza kusaidia website kukumbuka mipangilio, kuboresha utendaji na kuelewa namna watumiaji wanavyotumia huduma.",

                        "3. Cookies za Third Party",
                        "Baadhi ya huduma za third party kama analytics au matangazo zinaweza kutumia teknolojia zao za cookies kulingana na huduma zao.",

                        "4. Kudhibiti Cookies",
                        "Unaweza kudhibiti au kufuta cookies kupitia mipangilio ya browser yako. Kuzima baadhi ya cookies kunaweza kuathiri utendaji wa baadhi ya vipengele vya website.",

                        "5. Mabadiliko",
                        "Tunaweza kusasisha Cookie Policy hii wakati wowote."
                );
                break;

            case "disclaimer":
                title.setText("DISCLAIMER");

                setLegalContent(
                        "Disclaimer",
                        "Last updated: August 2026",

                        "1. Madhumuni ya Taarifa",
                        "90' Minutes Africa hutoa habari za michezo kwa madhumuni ya taarifa kwa umma.",

                        "2. Usahihi wa Taarifa",
                        "Ingawa tunajitahidi kuhakikisha taarifa ni sahihi na za wakati, hatuwezi kuhakikisha kwamba kila taarifa itakuwa sahihi au kamili wakati wote.",

                        "3. Uthibitishaji",
                        "Watumiaji wanashauriwa kuthibitisha taarifa muhimu kupitia vyanzo rasmi kabla ya kufanya maamuzi yanayotegemea taarifa hizo."
                );
                break;

            case "contact":
                title.setText("CONTACT US");

                setLegalContent(
                        "Wasiliana Nasi",
                        "90' Minutes Africa",

                        "1. Mawasiliano",
                        "Kwa maswali, maoni, taarifa za habari au ushirikiano, unaweza kuwasiliana nasi kupitia njia rasmi za mawasiliano za 90' Minutes Africa.",

                        "2. WhatsApp Channel",
                        "https://whatsapp.com/channel/0029Vb6mtUXDjiOZZOGkCw00"
                );
                break;

            default:
                title.setText("ABOUT US");

                setLegalContent(
                        "Kuhusu 90' Minutes Africa",
                        "Habari • Ratiba • Matokeo • Msimamo",

                        "1. Sisi ni Nani?",
                        "90' Minutes Africa ni chanzo cha habari za mpira Tanzania, Afrika na duniani.",

                        "2. Tunachokuletea",
                        "Tunakuletea habari, matokeo, ratiba, misimamo, usajili na taarifa mbalimbali za soka kwa wakati.",

                        "3. Lengo Letu",
                        "Lengo letu ni kuwafikishia mashabiki wa soka taarifa muhimu na matukio ya uwanjani kwa haraka na kwa urahisi."
                );
                break;
        }
    }

    private void setLegalContent(String heading, String intro, String... sections) {

        SpannableStringBuilder builder = new SpannableStringBuilder();

        appendHeading(builder, heading, true);
        appendIntro(builder, intro);

        for (int i = 0; i < sections.length; i += 2) {

            String sectionTitle = sections[i];
            String sectionText = sections[i + 1];

            appendHeading(builder, sectionTitle, false);
            appendParagraph(builder, sectionText);
        }

        content.setText(builder);
    }

    private void appendHeading(
            SpannableStringBuilder builder,
            String text,
            boolean mainHeading) {

        int start = builder.length();

        builder.append(text);
        builder.append("\n");

        int end = builder.length();

        builder.setSpan(
                new StyleSpan(Typeface.BOLD),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        builder.setSpan(
                new RelativeSizeSpan(mainHeading ? 1.35f : 1.08f),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        if (mainHeading) {
            builder.append("\n");
        } else {
            builder.append("\n");
        }
    }

    private void appendIntro(
            SpannableStringBuilder builder,
            String text) {

        builder.append(text);
        builder.append("\n\n");
    }

    private void appendParagraph(
            SpannableStringBuilder builder,
            String text) {

        builder.append(text);
        builder.append("\n\n");
    }
}
