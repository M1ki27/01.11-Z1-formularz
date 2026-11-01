package com.example.projekt;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText etName, etEmail;
    Button btnDate, btnSubmit;
    Spinner spCourse;
    RadioGroup rgMode;
    SeekBar sbLevel;
    TextView tvLevel, tvSummary;
    CheckBox cbNewsletter, cbTerms;

    String birthDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        btnDate = findViewById(R.id.btnDate);
        btnSubmit = findViewById(R.id.btnSubmit);
        spCourse = findViewById(R.id.spCourse);
        rgMode = findViewById(R.id.rgMode);
        sbLevel = findViewById(R.id.sbLevel);
        tvLevel = findViewById(R.id.tvLevel);
        tvSummary = findViewById(R.id.tvSummary);
        cbNewsletter = findViewById(R.id.cbNewsletter);
        cbTerms = findViewById(R.id.cbTerms);

        ustawSpinner();
        ustawListenery();
    }

    private void ustawSpinner() {
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        this,
                        R.array.courses,
                        android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spCourse.setAdapter(adapter);
    }

    private void ustawListenery() {

        cbTerms.setOnCheckedChangeListener((button, checked) ->
                btnSubmit.setEnabled(checked));

        btnDate.setOnClickListener(v -> pokazDate());

        sbLevel.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar, int progress, boolean fromUser) {

                        tvLevel.setText("Poziom: " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}
                });

        btnSubmit.setOnClickListener(v -> {

            if (sprawdzFormularz()) {
                pokazPotwierdzenie();
            }
        });
    }

    private void pokazDate() {

        Calendar kalendarz = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    birthDate = day + "." + (month + 1) + "." + year;
                    btnDate.setText(birthDate);
                },
                kalendarz.get(Calendar.YEAR),
                kalendarz.get(Calendar.MONTH),
                kalendarz.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMaxDate(
                System.currentTimeMillis());

        dialog.show();
    }

    private boolean sprawdzFormularz() {

        String name = etName.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Podaj imię i nazwisko");
            etName.requestFocus();
            return false;
        }

        String email = etEmail.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Niepoprawny e-mail");
            etEmail.requestFocus();
            return false;
        }

        if (birthDate.isEmpty()) {
            Toast.makeText(this,
                    "Wybierz datę", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spCourse.getSelectedItemPosition() == 0) {
            Toast.makeText(this,
                    "Wybierz kurs", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (rgMode.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this,
                    "Wybierz tryb zajęć", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void pokazPotwierdzenie() {

        new AlertDialog.Builder(this)
                .setTitle("Wysłać zgłoszenie?")
                .setMessage("Czy na pewno chcesz wysłać formularz?")
                .setPositiveButton("Tak", (dialog, which) ->
                        wyslijFormularz())
                .setNegativeButton("Nie", null)
                .show();
    }

    private void wyslijFormularz() {

        String tryb = rgMode.getCheckedRadioButtonId()
                == R.id.rbOnsite ? "Stacjonarny" : "Zdalny";

        String newsletter = cbNewsletter.isChecked()
                ? "Tak" : "Nie";

        String podsumowanie =
                "Imię: " + etName.getText().toString() +
                        "\nE-mail: " + etEmail.getText().toString() +
                        "\nData: " + birthDate +
                        "\nKurs: " + spCourse.getSelectedItem() +
                        "\nTryb: " + tryb +
                        "\nPoziom: " + sbLevel.getProgress() +
                        "\nNewsletter: " + newsletter;

        tvSummary.setText(podsumowanie);

        Snackbar.make(
                        findViewById(R.id.btnSubmit),
                        "Zgłoszenie wysłane",
                        Snackbar.LENGTH_LONG)
                .setAction("Cofnij", v ->
                        tvSummary.setText(""))
                .show();
    }
}