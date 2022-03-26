package vn.edu.hust.sis.phong.covid_19vaccinationcard;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.edu.hust.sis.phong.covid_19vaccinationcard.Model.User;
import vn.edu.hust.sis.phong.covid_19vaccinationcard.Model.Vaccination_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import vn.edu.hust.sis.phong.covid_19vaccinationcard.Model.Vaccine;


public class HomeScreen extends AppCompatActivity {

    ImageButton person_button;
    ImageButton setting_button;
    ImageButton logout_button;

    private ImageView qrCodeIV;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    List<Vaccination_info> vaccinationInfoList;

    private String str_dose_1_id;
    private String str_dose_2_id;
    public static final String msg_dose_1_id = "DOSE 1 ID";

    public static final String msg_dose_2_id = "DOSE 2 ID";

    String message;

    TextView title;
    TextView no_vacc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        person_button = findViewById(R.id.person_button);
        setting_button = findViewById(R.id.setting_button);
        logout_button = findViewById(R.id.logout_button);
        qrCodeIV = findViewById(R.id.idIVQrcode);
        title = findViewById(R.id.title);
        no_vacc = findViewById(R.id.no_vacc);
        vaccinationInfoList = new ArrayList<>();

        Intent intent = getIntent();
        message = intent.getStringExtra(LoginForm.EXTRA_MESSAGE);

        if (message.equals("None Vaccination card")) {

            no_vacc.setVisibility(View.VISIBLE);
            qrCodeIV.setVisibility(View.GONE);
        }

        displayQR();

        ListDoseGetter listDoseGetter = new ListDoseGetter();
        listDoseGetter.execute();
    }

    public void displayToast(String message) {
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_SHORT).show();
    }

    public void QR_detail(View view) {
        Intent intent_detail= new Intent(HomeScreen.this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(msg_dose_1_id,str_dose_1_id);
        bundle.putString(msg_dose_2_id,str_dose_2_id);
        intent_detail.putExtras(bundle);
        startActivity(intent_detail);
    }

    public void displayQR() {

            // below line is for getting
            // the windowmanager service.
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // initializing a variable for default display.
            Display display = manager.getDefaultDisplay();

            // creating a variable for point which
            // is to be displayed in QR Code.
            Point point = new Point();
            display.getSize(point);

            // getting width and
            // height of a point
            int width = point.x;
            int height = point.y;

            // generating dimension from width and height.
            int dimen = width < height ? width : height;
            dimen = dimen * 3 / 4;

            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
            qrgEncoder = new QRGEncoder("test QR code", null, QRGContents.Type.TEXT, dimen);
            try {
                // getting our qrcode in the form of bitmap.
                bitmap = qrgEncoder.encodeAsBitmap();
                // the bitmap is set inside our image
                // view using .setimagebitmap method.
                qrCodeIV.setImageBitmap(bitmap);
            } catch (WriterException e) {
                // this method is called for
                // exception handling.
                Log.e("Tag", e.toString());
            }

    }

    public void personal_modify(View view) {
        Intent intent_person = new Intent(HomeScreen.this, Personal.class);
        startActivity(intent_person);
    }

    public void logout(View view) {
        Intent intent_logout = new Intent(HomeScreen.this, LoginForm.class);
        startActivity(intent_logout);
        finish();

    }

    public void setting(View view) {
        displayToast("Not Available");
    }


    private class ListDoseGetter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2/laravel_api/public/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            API_Service retrofitAPI = retrofit.create(API_Service.class);

            Call<List<Vaccination_info>> call = retrofitAPI.getVaccination_info();

            try {
                vaccinationInfoList = call.execute().body();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            for (Vaccination_info vaccination_info_get_dose : vaccinationInfoList){
                if(message.equals(vaccination_info_get_dose.getVaccinationInfoId())){

                    str_dose_1_id = vaccination_info_get_dose.getVaccinationInjection1();


                    if  (vaccination_info_get_dose.getVaccinationInjection2() == null){

                        str_dose_2_id = "None Vaccine";

                    } else {
                        str_dose_2_id = vaccination_info_get_dose.getVaccinationInjection2();

                    }

                    Log.v("ID 1", String.valueOf(vaccination_info_get_dose.getVaccinationInjection2()));


                }

            }

        }
    }

}