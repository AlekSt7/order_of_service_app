package ru.dolphins_it.service.Auth;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.dolphins_it.service.Api.Api;
import ru.dolphins_it.service.Api.ApiService;
import ru.dolphins_it.service.DataBaseHelper;
import ru.dolphins_it.service.R;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {

    EditText number;
    EditText sms_number;
    Button login;
    Api api;
    Gson gson;
    Retrofit retrofit;
    SQLiteDatabase db;
    DataBaseHelper dataBaseHelper;
    Cursor userCursor;
    ImageView back;

    String phone_number;

    boolean sms = false; //С помощью этой переменной определяем отправлять номер телефона или sms-код по нажатию одной и той же кнопкм

    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://37.204.111.194/dolphins/") // Базовый URL
                .addConverterFactory(GsonConverterFactory.create(gson)) // Конвертер JSON
                .build();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        number = getView().findViewById(R.id.number);
        sms_number = getView().findViewById(R.id.sms_number);
        login = getView().findViewById(R.id.login);
        back = getView().findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        number.setText("+7 (");
        number.setFocusable(true);

        number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (number != null) {
                    if (!s.toString().startsWith("+7 (")) {
                        number.setText("+7 (");
                        Selection.setSelection((Spannable) number.getText(), number.getText().length());
                    }
                }
            }
        });

        sms_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sms_number.getText().length() == 5){sms();}
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        final MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER); //Устанавливаем маску россиского номера телефона
        mask.setForbidInputWhenFilled(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(number);

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("__-__");
        formatWatcher = new MaskFormatWatcher( //Форматировать текст будет вот он
                MaskImpl.createTerminated(slots)
        );
        formatWatcher.installOn(sms_number);

        login.setOnClickListener(new View.OnClickListener() { //Кнопка входа
            @Override
            public void onClick(View v) {

                if(!sms ) {//Отправляем номер телефона
                    if(number.getText().length() == 18) {
                        String phoneStr = String.valueOf(number.getText());
                        phone_number = phoneStr.replaceAll("[^0-9]", ""); //Убираем всё, кроме цифр

                        api = retrofit.create(Api.class);
                        Call<List<ApiService>> call = api.getSmsCode("sms_start", phone_number); //Запрос на генерацию номера

                        call.enqueue(new Callback<List<ApiService>>() {

                            @Override
                            public void onResponse(Call<List<ApiService>> call, Response<List<ApiService>> response) {

                                List<ApiService> list = response.body();

                                if (!response.isSuccessful()) {
                                    // сервер вернул ошибку
                                    Toast toast = Toast.makeText(getContext(),
                                            "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT);
                                    toast.show();
                                } else if (new String("ok").equals(list.get(0).getSmsResult())) {

                                    sms = true;
                                    number.setEnabled(false);
                                    number.setFocusable(false);
                                    sms_number.setEnabled(true);
                                    sms_number.setFocusable(true);

                                    Toast toast = Toast.makeText(getContext(),
                                            "На этот номер было отправлено sms с кодом авторизации, повторите попытку, если код не пришёл в течении 30 секунд", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }

                            @Override
                            public void onFailure(Call<List<ApiService>> call, Throwable t) { //Запрос провалился
                                Toast toast = Toast.makeText(getContext(),
                                        "Ошибка при выполнении запроса: " + t, Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        });
                    }else {

                        Toast toast = Toast.makeText(getContext(),
                                "Введён некорректный номер", Toast.LENGTH_SHORT);
                        toast.show();

                    }
                }else{//Отправляем sms-код
                        sms();
                }

            }
        });

    }

    void sms(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        String SmsCodeStr = String.valueOf(sms_number.getText());
        String sms_code = SmsCodeStr.replaceAll("[^0-9]", ""); //Убираем всё, кроме цифр


        api = retrofit.create(Api.class);
        Call<List<ApiService>> call = api.getToken("auth_start", sms_code, phone_number);

        call.enqueue(new Callback<List<ApiService>>() {

            @Override
            public void onResponse(Call<List<ApiService>> call, Response<List<ApiService>> response) {

                if (response.isSuccessful()) {
                    // запрос выполнился успешно, сервер вернул Status 200

                    List<ApiService> list = response.body();

                    Toast toast;

                    if(list.get(0).getToken() == null){
                        toast = Toast.makeText(getContext(),
                                "Введён неверный код из SMS", Toast.LENGTH_SHORT);
                        toast.show();
                    }else{ //Если усё нормально, записываем токен в базу


                        dataBaseHelper = new DataBaseHelper(getContext());
                        db = dataBaseHelper.getWritableDatabase();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(dataBaseHelper.USER_TOKEN, list.get(0).getToken());
                        contentValues.put(dataBaseHelper.USER_PHONE_NUMBER, phone_number);

                        //КОСТЫЛЬ, НЕ смотрите пожалуйста
                        if (DatabaseUtils.longForQuery(db, "SELECT COUNT("+dataBaseHelper.USER_TOKEN+") FROM " + dataBaseHelper.TABLE_USER, null) > 0) {
                            String tok = null;

                            userCursor = db.query(dataBaseHelper.TABLE_USER, new String[]{dataBaseHelper.USER_TOKEN},
                                    null, null, null, null, null);

                            if (userCursor.moveToFirst()) {
                                tok = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.USER_TOKEN));
                            }
                            db.update(DataBaseHelper.TABLE_USER, contentValues, dataBaseHelper.USER_TOKEN + "= ?", new String[]{tok}); //Обновляем токен в базе
                        }else {
                            db.insert(DataBaseHelper.TABLE_USER, null, contentValues); //Добавляем токен в базу
                        }

                        if(new String("0").equals(list.get(0).getPrivileges())) {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new MyData()).commit(); //Если обычный пользователь, заходим под обычным пользователем
                        }else{ //Если админ, то админ
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Admin()).commit();
                        }
                    }
                } else {
                    // сервер вернул ошибку
                    Toast toast = Toast.makeText(getContext(),
                            "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<ApiService>> call, Throwable t) { //Запрос провалился
                Toast toast = Toast.makeText(getContext(),
                        "Ошибка при выполнении запроса: "+t, Toast.LENGTH_LONG);
                toast.show();
            }

        });
    }
}
