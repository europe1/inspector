package net.cararea.inspector;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ErrorInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_info);

        TextView errorCode = findViewById(R.id.error_info_code);
        TextView errorText = findViewById(R.id.error_info_text);
        TextView errorDescription = findViewById(R.id.error_info_description);

        errorCode.setText(getIntent().getExtras().getString("errorCode"));
        errorText.setText(getIntent().getExtras().getString("errorText"));
    }
}
