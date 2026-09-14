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
                        "Terms & Conditions\\n\\n" +
                        "Last updated: August 2026\\n\\n" +

                        "1. Utangulizi\\n\\n" +
                        "Karibu 90' Minutes Africa. Kwa kutumia website hii, unakubali kufuata Terms & Conditions hizi.\\n\\n" +

                        "2. Matumizi ya Website\\n\\n" +
                        "Website hii inatoa habari za soka, ratiba, matokeo, misimamo na taarifa nyingine zinazohusiana na mpira.\\n\\n" +

                        "3. Maudhui\\n\\n" +
                        "Tunajitahidi kuhakikisha taarifa tunazochapisha ni sahihi, lakini hatuhakikishi kwamba kila taarifa itakuwa sahihi au kamili wakati wote.\\n\\n" +

                        "4. Matumizi Yasiyoruhusiwa\\n\\n" +
                        "Hairuhusiwi kutumia website hii kwa shughuli haramu, kujaribu kuivamia, kuharibu huduma au kusambaza maudhui yenye madhara.\\n\\n" +

                        "5. Mabadiliko\\n\\n" +
                        "Tunaweza kubadilisha masharti haya wakati wowote. Masharti mapya yataanza kutumika yatakapowekwa kwenye website.\\n\\n" +

                        "6. Mawasiliano\\n\\n" +
                        "Ikiwa una swali kuhusu Terms & Conditions hizi, wasiliana na uongozi wa 90' Minutes Africa."
                );
                break;

            case "privacy":
                title.setText("PRIVACY POLICY");
                content.setText(
                        "Privacy Policy\\n\\n" +
                        "Last updated: August 2026\\n\\n" +

                        "1. Utangulizi\\n\\n" +
                        "90' Minutes Africa inaheshimu faragha ya watumiaji wake. Sera hii inaeleza aina ya taarifa zinazoweza kukusanywa unapotumia website yetu, namna tunavyotumia taarifa hizo na huduma za third party zinazoweza kutumika kwenye website.\\n\\n" +

                        "2. Taarifa Tunazokusanya\\n\\n" +
                        "Tunaweza kukusanya taarifa unazotupatia kwa hiari, kwa mfano unapowasiliana nasi kupitia Contact Us au unapowasilisha maoni kwenye maudhui yetu.\\n\\n" +
                        "Website inaweza pia kukusanya taarifa za kiufundi kama aina ya kifaa, browser, mfumo wa uendeshaji, anwani ya IP, kurasa ulizotembelea na taarifa zinazohusiana na matumizi ya website.\\n\\n" +

                        "3. Jinsi Tunavyotumia Taarifa\\n\\n" +
                        "Taarifa zinaweza kutumika kuendesha, kudumisha na kuboresha website, kuelewa namna wageni wanavyotumia huduma, kuboresha usalama, kujibu mawasiliano na kuboresha uzoefu wa mtumiaji.\\n\\n" +

                        "4. Google Analytics\\n\\n" +
                        "Tunatumia Google Analytics kusaidia kuelewa namna watumiaji wanavyotumia website, kama vile kurasa zinazotembelewa, idadi ya watumiaji na taarifa za matumizi ya website. Google Analytics inaweza kutumia cookies au teknolojia zinazofanana kulingana na mipangilio ya huduma hiyo.\\n\\n" +

                        "5. Google AdSense na Matangazo\\n\\n" +
                        "Website hii inaweza kutumia Google AdSense kuonyesha matangazo. Google na washirika wake wa matangazo wanaweza kutumia cookies, web beacons au teknolojia nyingine kusaidia kuonyesha na kupima matangazo, kulingana na mipangilio na chaguo za mtumiaji.\\n\\n" +
                        "Matangazo yanaweza kuwa personalized au non-personalized kulingana na eneo la mtumiaji, mipangilio ya faragha, ridhaa inayohitajika na sera za huduma husika.\\n\\n" +

                        "6. Third-Party Services\\n\\n" +
                        "Baadhi ya huduma kwenye website zinaweza kutolewa na third parties, ikiwa ni pamoja na huduma za analytics, advertising, hosting, database au social media. Third parties hao wanaweza kukusanya au kuchakata taarifa kulingana na sera zao za faragha.\\n\\n" +

                        "7. Cookies\\n\\n" +
                        "Tunatumia cookies na teknolojia zinazofanana kusaidia website kufanya kazi, kuhifadhi baadhi ya mipangilio, kuelewa matumizi ya website na kusaidia huduma za analytics na advertising.\\n\\n" +
                        "Unaweza kudhibiti au kufuta cookies kupitia mipangilio ya browser yako. Kuzima baadhi ya cookies kunaweza kuathiri utendaji wa baadhi ya vipengele vya website.\\n\\n" +

                        "8. Maoni na Mawasiliano\\n\\n" +
                        "Ikiwa utaacha maoni au kuwasiliana nasi, taarifa utakazotoa zinaweza kuhifadhiwa na kutumiwa kwa madhumuni ya kujibu mawasiliano yako, kudhibiti maudhui na kulinda website dhidi ya matumizi mabaya.\\n\\n" +

                        "9. Usalama wa Taarifa\\n\\n" +
                        "Tunachukua hatua zinazofaa kulinda taarifa zinazohusiana na huduma zetu. Hata hivyo, hakuna mfumo wa internet unaoweza kuhakikishwa kuwa salama kwa asilimia 100.\\n\\n" +

                        "10. Viungo vya Tovuti Nyingine\\n\\n" +
                        "Website inaweza kuwa na viungo vinavyoelekeza kwenye tovuti za watu wengine. Hatuwajibiki kwa sera za faragha, maudhui au taratibu za tovuti hizo. Tunashauri watumiaji kusoma sera zao za faragha kabla ya kutumia huduma zao.\\n\\n" +

                        "11. Faragha ya Watoto\\n\\n" +
                        "Website hii haikusudii kukusanya kwa makusudi taarifa binafsi kutoka kwa watoto. Ikiwa unaamini mtoto ametoa taarifa binafsi bila idhini inayofaa, tafadhali wasiliana nasi.\\n\\n" +

                        "12. Mabadiliko ya Privacy Policy\\n\\n" +
                        "Tunaweza kusasisha Privacy Policy hii mara kwa mara ili kuendana na mabadiliko ya huduma, teknolojia au mahitaji ya kisheria. Tarehe ya mwisho ya kusasishwa itaonyeshwa juu ya ukurasa huu.\\n\\n" +

                        "13. Mawasiliano\\n\\n" +
                        "Ikiwa una swali kuhusu Privacy Policy hii au namna tunavyotumia taarifa, unaweza kuwasiliana nasi kupitia ukurasa wa Contact Us."
                );
                break;

            case "cookies":
                title.setText("COOKIE POLICY");
                content.setText(
                        "Cookie Policy\\n\\n" +
                        "Last updated: August 2026\\n\\n" +

                        "1. Cookies ni nini?\\n\\n" +
                        "Cookies ni taarifa ndogo zinazoweza kuhifadhiwa kwenye kifaa chako wakati unapotembelea website.\\n\\n" +

                        "2. Tunazitumia kwa nini?\\n\\n" +
                        "Cookies zinaweza kusaidia website kukumbuka mipangilio, kuboresha utendaji na kuelewa namna watumiaji wanavyotumia huduma.\\n\\n" +

                        "3. Cookies za Third Party\\n\\n" +
                        "Baadhi ya huduma za third party kama analytics au matangazo zinaweza kutumia teknolojia zao za cookies kulingana na huduma zao.\\n\\n" +

                        "4. Kudhibiti Cookies\\n\\n" +
                        "Unaweza kudhibiti au kufuta cookies kupitia mipangilio ya browser yako.\\n\\n" +

                        "5. Mabadiliko\\n\\n" +
                        "Tunaweza kusasisha Cookie Policy hii wakati wowote."
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
