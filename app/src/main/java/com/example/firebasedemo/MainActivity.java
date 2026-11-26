package com.example.firebasedemo;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText etName, etMessage;
    private Button btnSave, btnLoad;
    private ListView listView;

    private DatabaseReference dbRef;
    private final ArrayList<String> entries = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etMessage = findViewById(R.id.etMessage);
        btnSave = findViewById(R.id.btnSave);
        btnLoad = findViewById(R.id.btnLoad);
        listView = findViewById(R.id.listView);

        // If your database is not in the US region, use:
        // FirebaseDatabase.getInstance("https://YOUR-DB-URL").getReference("entries");
        dbRef = FirebaseDatabase.getInstance().getReference("entries");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);
        listView.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveEntry());
        btnLoad.setOnClickListener(v -> loadEntries());
    }

    private void saveEntry() {
        String name = etName.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (name.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String key = dbRef.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Failed to save record", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.child(key).setValue(new Entry(name, message))
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
                    etName.setText("");
                    etMessage.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadEntries() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                entries.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    Entry entry = child.getValue(Entry.class);
                    if (entry != null) {
                        entries.add(entry.name + ": " + entry.message);
                    }
                }

                adapter.notifyDataSetChanged();

                if (entries.isEmpty()) {
                    Toast.makeText(MainActivity.this, "No records found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this,
                        "Failed to load: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
