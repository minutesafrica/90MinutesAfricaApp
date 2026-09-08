package com.ninetyminutes.africa;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        EditText contactName = findViewById(R.id.contactName);
        EditText contactEmail = findViewById(R.id.contactEmail);
        EditText contactMessage = findViewById(R.id.contactMessage);
        TextView contactSend = findViewById(R.id.contactSend);

        contactSend.setOnClickListener(v -> {

            String name = contactName.getText().toString().trim();
            String email = contactEmail.getText().toString().trim();
            String message = contactMessage.getText().toString().trim();

            if (name.isEmpty()) {
                contactName.setError("Andika jina lako");
                contactName.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                contactEmail.setError("Andika barua pepe");
                contactEmail.requestFocus();
                return;
            }

            if (message.isEmpty()) {
                contactMessage.setError("Andika ujumbe wako");
                contactMessage.requestFocus();
                return;
            }

            String subject = "Ujumbe kutoka 90' Minutes Africa";

            String body =
                    "Jina: " + name + "\n\n" +
                    "Email: " + email + "\n\n" +
                    "Ujumbe:\n" + message;

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:yaredtmganga@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(
                        ContactActivity.this,
                        "Hakuna email app iliyopatikana kwenye simu.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
