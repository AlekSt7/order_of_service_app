package ru.dolphins_it.service.Auth;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.dolphins_it.service.Api.Api;
import ru.dolphins_it.service.Api.ApiService;
import ru.dolphins_it.service.DataBaseHelper;
import ru.dolphins_it.service.DigitalSignage.Digital_signageCalculator;
import ru.dolphins_it.service.News.NewsAdapter;
import ru.dolphins_it.service.News.news_full_activity;
import ru.dolphins_it.service.R;
import ru.dolphins_it.service.Session;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyData extends Fragment {


    Api api;
    Gson gson;
    Retrofit retrofit;
    ImageView back, exit, update_data, cart;
    Switch sms;
    SlidingUpPanelLayout mLayout;

    AlertDialog.Builder ad;

    ScrollView scrollView;
    RelativeLayout relativeLayout;

    DataBaseHelper dataBaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    ListView orders_list;

    Session session = new Session();

    JsonObject order_json = new JsonObject();
    JsonObject e_ocher_json = new JsonObject();
    JsonObject digital_signage_json = new JsonObject();

    TextInputEditText nameEditText, phone_numberEditText, organization_nameEditText, jobEditText, e_mailEditText;

    //String name, phone_number, organization_name, job, e_mail, sms_mailing;

    public MyData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        session.inicialize(getContext());

        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://37.204.111.194/dolphins/") // Базовый URL
                .addConverterFactory(GsonConverterFactory.create(gson)) // Конвертер JSON
                .build();

        api = retrofit.create(Api.class);

        dataBaseHelper = new DataBaseHelper(getContext());

        db = dataBaseHelper.getWritableDatabase();


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_data, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        scrollView = getView().findViewById(R.id.scroll_basket_content);
        relativeLayout = getView().findViewById(R.id.empty_content);

        orders_list = getView().findViewById(R.id.orders_list);

        mLayout = getView().findViewById(R.id.sliding_layout);

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        nameEditText = getView().findViewById(R.id.name);
        phone_numberEditText = getView().findViewById(R.id.number);
        organization_nameEditText = getView().findViewById(R.id.company);
        jobEditText = getView().findViewById(R.id.job);
        e_mailEditText = getView().findViewById(R.id.e_mail);
        back = getView().findViewById(R.id.back);
        exit = getView().findViewById(R.id.exit);
        update_data = getView().findViewById(R.id.update_data);
        sms = getView().findViewById(R.id.sms);
        cart = getView().findViewById(R.id.cart);

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.session_exist()){
                    String smsbool;
                    if(sms.isChecked()){smsbool = "1";}else{smsbool = "0";}

                    Call<Void> call = api.setMailing("sms_mailing", session.getToken(), smsbool);

                    call.enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) { //Запрос провалился

                        }

                    });
                }else{
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Login()).commit();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        update_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(session.session_exist()){

                   final JsonObject user_data = new JsonObject();

                   String phoneStr = String.valueOf(phone_numberEditText.getText());
                   String phone_number = phoneStr.replaceAll("[^0-9]", ""); //Убираем всё, кроме цифр

                   user_data.addProperty("name", nameEditText.getText().toString());
                   user_data.addProperty("phone_number", phone_number);
                   user_data.addProperty("organization_name", organization_nameEditText.getText().toString());
                   user_data.addProperty("job", jobEditText.getText().toString());
                   user_data.addProperty("e_mail", e_mailEditText.getText().toString());

                        Call<Void> call = api.userDataUpdate("user_data_update", session.getToken(), String.valueOf(user_data)); //Запрос на обновление данных пользователя

                        call.enqueue(new Callback<Void>() {

                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                Toast toast = Toast.makeText(getContext(),
                                        "Данные обновлены", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) { //Запрос провалился
                                Toast toast = Toast.makeText(getContext(),
                                        "Ошибка при выполнении запроса: "+t, Toast.LENGTH_LONG);
                                toast.show();
                            }

                        });
                }else{
                   getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Login()).commit();
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad = new AlertDialog.Builder(getContext());
                ad.setTitle("Выход");  // заголовок
                ad.setMessage("Вы уверены, что хотите выйти?");
                ad.setPositiveButton("Да",
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        session.session_unset();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Login()).commit();
                    }
                });
                ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                ad.show();
            }
        });

        final MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER); //Устанавливаем маску россиского номера телефона
        mask.setForbidInputWhenFilled(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(phone_numberEditText);


            Call<List<ApiService>> call = api.getUserData("get_user_data", session.getToken()); //Запрос на получение данных пользователя

            call.enqueue(new Callback<List<ApiService>>() {

                @Override
                public void onResponse(Call<List<ApiService>> call, Response<List<ApiService>> response) {

                    List<ApiService> list = response.body();

                    if (response.isSuccessful()) {

                        nameEditText.setText(list.get(0).getUserName());
                        phone_numberEditText.setText(list.get(0).getPhoneNumber());
                        organization_nameEditText.setText(list.get(0).getOrganizationName());
                        jobEditText.setText(list.get(0).getJob());
                        e_mailEditText.setText(list.get(0).getE_mail());

                        if(new String("0").equals(list.get(0).getSmsMailing())){
                            sms.setChecked(false);
                        }else{sms.setChecked(true);}


                    } else {
                        Toast toast = Toast.makeText(getContext(),
                                "Возникла ошибка при при загрузке данных", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }

                @Override
                public void onFailure(Call<List<ApiService>> call, Throwable t) { //Запрос провалился
                    Toast toast = Toast.makeText(getContext(),
                            "Возникла ошибка при выполнении запроса: " + t, Toast.LENGTH_SHORT);
                    toast.show();
                }

            });

        final ArrayList<String> order_id = new ArrayList<>();
        final ArrayList<String> status = new ArrayList<>();
        final ArrayList<String> status1 = new ArrayList<>();
        final ArrayList<String> date = new ArrayList<>();
        final ArrayList<String> reason_for_rejection = new ArrayList<>();

        Call<List<ApiService>> get_orders = api.getUserOrder("get_user_order", session.getToken()); //Запрос на получение заявок пользователя

        get_orders.enqueue(new Callback<List<ApiService>>() {

            @Override
            public void onResponse(Call<List<ApiService>> get_orders, Response<List<ApiService>> response) {

                List<ApiService> list = response.body();

                if (response.isSuccessful()) {

                    for(int i = 0; i < list.size(); i++) {

                        order_id.add(i, list.get(i).getOrderId());

                        status1.add(i, list.get(i).getOrderStatus());
                        reason_for_rejection.add(i, list.get(i).getReason_for_rejection());

                        if(list.get(i).getOrderStatus() == null){
                            status.add(i, "В рассмотрении");
                        }else if (String.valueOf("1").equals(list.get(i).getOrderStatus())){
                            status.add(i, "Принята "+list.get(i).getDate_of_adoption());
                        }else{
                            status.add(i, "Отклонена "+list.get(i).getDate_of_adoption());
                        }

                        date.add(i, "Заявка отправлена " + list.get(i).getOrderdate());
                    }

                    orders_list.setAdapter(new ListViewAdapter(getActivity(), order_id, status, date));
                    justifyListViewHeightBasedOnChildren(orders_list);

                } else {
                    Toast toast = Toast.makeText(getContext(),
                            "Возникла ошибка при получении данных о заявках", Toast.LENGTH_LONG);
                    toast.show();
                }

            }

            @Override
            public void onFailure(Call<List<ApiService>> get_orders, Throwable t) { //Запрос провалился
                Toast toast = Toast.makeText(getContext(),
                        "Возникла ошибка при выполнении запроса: " + t, Toast.LENGTH_SHORT);
                toast.show();
            }

        });

        orders_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getContext(), FullOrder.class);
                if(String.valueOf("0").equals(status1.get(position))) {
                intent.putExtra("reason_for_rejection", reason_for_rejection.get(position));
                }
                intent.putExtra("order_id", order_id.get(position));
                startActivity(intent);
            }

        });

        phone_numberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (phone_numberEditText != null) {
                    if (!s.toString().startsWith("+7 (")) {
                        phone_numberEditText.setText("+7 (");
                        Selection.setSelection((Spannable) phone_numberEditText.getText(), phone_numberEditText.getText().length());
                    }
                }
            }
        });

        if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.ORDER_ID + ") FROM " + dataBaseHelper.TABLE_ORDER, null) > 0) { //Если корзина не пустая

            relativeLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            ImageView delete_order;

            Button done = getView().findViewById(R.id.done);

            done.setOnClickListener(new View.OnClickListener() { //Запрос на отправку заказа на сервер
                @Override
                public void onClick(View v) {

                    Call<Void> call = api.addOrder("add_order", session.getToken(), String.valueOf(order_json), String.valueOf(e_ocher_json), String.valueOf(digital_signage_json)); //Запрос на отправку заказа на сервер

                    call.enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                            if(response.isSuccessful()){
                            Toast toast = Toast.makeText(getContext(),
                                    "Заявка отправлена", Toast.LENGTH_SHORT);
                            toast.show();}
                            else {
                                Toast toast = Toast.makeText(getContext(),
                                        response.errorBody().toString(), Toast.LENGTH_SHORT);
                                toast.show();
                                }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) { //Запрос провалился
                            Toast toast = Toast.makeText(getContext(),
                                    "Возникла ошибка при выполнении запроса: " + t, Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    });
                }
            });

            delete_order = getView().findViewById(R.id.delete_order);

            delete_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad = new AlertDialog.Builder(getContext());
                    ad.setTitle("Очистка");  // заголовок
                    ad.setMessage("Очистить корзину?");
                    ad.setPositiveButton("Да",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int arg1) {

                                    db.delete(dataBaseHelper.TABLE_ORDER, null, null);
                                    db.delete(dataBaseHelper.E_OCHER, null, null);
                                    db.delete(dataBaseHelper.DIGITAL_SIGNAGE, null, null);
                                    relativeLayout.setVisibility(View.VISIBLE);
                                    scrollView.setVisibility(View.GONE);

                                }
                            });
                    ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) { }
                    });
                    ad.show();
                }
            });

            final ConstraintLayout e_ocher_layout, ds_layout;
            final LinearLayout digital_consultant, tesosq;

            e_ocher_layout = getView().findViewById(R.id.e_ocher);
            ds_layout = getView().findViewById(R.id.digital_signage);
            digital_consultant = getView().findViewById(R.id.digital_consultant_layout);
            tesosq = getView().findViewById(R.id.tesosq_layout);

            if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.E_OCHER_ID + ") FROM " + dataBaseHelper.E_OCHER, null) > 0) { //Если в корзине есть услуга "Электронная очередь"

                order_json.addProperty("e_ocher","1");

                e_ocher_layout.setVisibility(View.VISIBLE);

                userCursor = db.query(dataBaseHelper.E_OCHER, new String[]{dataBaseHelper.NUMBER_OF_OPERATORS, dataBaseHelper.NUMBER_OF_TERMINALS, dataBaseHelper.NUMBER_OF_BOARDS,
                                dataBaseHelper.APPROXIMATE_COAST, dataBaseHelper.COMMENT},
                        null, null, null, null, null);

                TextView operator, terminal, signage, result, comment, commentHeader;

                ImageView delete;

                operator = getView().findViewById(R.id.operator_count);
                terminal = getView().findViewById(R.id.terminal_count);
                signage = getView().findViewById(R.id.signange_count);
                result = getView().findViewById(R.id.result);
                comment = getView().findViewById(R.id.comment);
                commentHeader = getView().findViewById(R.id.commentHeader);

                delete = getView().findViewById(R.id.delete_e_ocher);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ad = new AlertDialog.Builder(getContext());
                        ad.setTitle("Удаление");  // заголовок
                        ad.setMessage("Удалить услугу из корзины?");
                        ad.setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {

                                        userCursor = db.query(dataBaseHelper.TABLE_ORDER, new String[]{dataBaseHelper.ORDER_ID},
                                                null, null, null, null, null);

                                        String id;

                                        userCursor.moveToFirst();
                                        id = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.ORDER_ID));

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(dataBaseHelper.E_OCHER_ID, 0);
                                        db.update(DataBaseHelper.TABLE_ORDER, contentValues, dataBaseHelper.ORDER_ID + "= ?", new String[]{id}); //Обновляем заказ и удаляем услугу

                                        db.delete(dataBaseHelper.E_OCHER, null, null);

                                        e_ocher_layout.setVisibility(View.GONE);

                                        order_json.addProperty("e_ocher","0");

                                        clearBasket();

                                    }
                                });
                        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {

                            }
                        });
                        ad.show();

                    }
                });

                userCursor.moveToFirst();

                e_ocher_json.addProperty(dataBaseHelper.NUMBER_OF_OPERATORS, userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_OPERATORS)));
                e_ocher_json.addProperty(dataBaseHelper.NUMBER_OF_TERMINALS,userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_TERMINALS)));
                e_ocher_json.addProperty(dataBaseHelper.NUMBER_OF_BOARDS, userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_BOARDS)));
                e_ocher_json.addProperty(dataBaseHelper.APPROXIMATE_COAST,userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.APPROXIMATE_COAST)));

                operator.setText(String.valueOf(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_OPERATORS))));
                terminal.setText(String.valueOf(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_TERMINALS))));
                signage.setText(String.valueOf(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_BOARDS))));
                result.setText(toRuLocale(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.APPROXIMATE_COAST))) + "\u20BD");
                if(String.valueOf("").equals(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.COMMENT)))){
                    comment.setVisibility(View.GONE);
                    commentHeader.setVisibility(View.GONE);
                    e_ocher_json.addProperty(dataBaseHelper.COMMENT, 0);
                }else{
                    comment.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.COMMENT)));
                    e_ocher_json.addProperty(dataBaseHelper.COMMENT, userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.COMMENT)));
                }

            }else{ order_json.addProperty("e_ocher","0");}

            if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.DIGITAL_SIGNAGE_ID + ") FROM " + dataBaseHelper.DIGITAL_SIGNAGE, null) > 0) { //Если в корзине есть услуга "Цифровые вывески"

                order_json.addProperty("digital_signage","1");

                ds_layout.setVisibility(View.VISIBLE);

                userCursor = db.query(dataBaseHelper.DIGITAL_SIGNAGE, new String[]{dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS, dataBaseHelper.NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE, dataBaseHelper.SCREEN_SIZE,
                                dataBaseHelper.FASTENING_TYPE, dataBaseHelper.NUMBER_OF_SCREENS_OBJECT_ON, dataBaseHelper.TOUCH_SCREEN, dataBaseHelper.POSTING, dataBaseHelper.k_CONTENT,
                                dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT, dataBaseHelper.UNIQUE_CONTENT, dataBaseHelper.COMMENT},
                        null, null, null, null, null);

                TextView object_count, size_count, type, size, object_on, touch_screen, unique_content, k_content,
                        wire, installing_type, comment, commentHeader;

                ImageView delete;

                object_count = getView().findViewById(R.id.object_count);
                size_count = getView().findViewById(R.id.size_count);
                type = getView().findViewById(R.id.type);
                size = getView().findViewById(R.id.size);
                object_on = getView().findViewById(R.id.object_on);
                touch_screen = getView().findViewById(R.id.touch_screen);
                unique_content = getView().findViewById(R.id.unique_content);
                k_content = getView().findViewById(R.id.k_content);
                wire = getView().findViewById(R.id.wire);
                installing_type = getView().findViewById(R.id.installing_type);
                comment = getView().findViewById(R.id.comment1);
                commentHeader = getView().findViewById(R.id.commentHeader1);

                delete = getView().findViewById(R.id.delete_digital_signage);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ad = new AlertDialog.Builder(getContext());
                        ad.setTitle("Удаление");  // заголовок
                        ad.setMessage("Удалить услугу из корзины?");
                        ad.setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {

                                        userCursor = db.query(dataBaseHelper.TABLE_ORDER, new String[]{dataBaseHelper.ORDER_ID},
                                                null, null, null, null, null);

                                        String id;

                                        userCursor.moveToFirst();
                                        id = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.ORDER_ID));

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(dataBaseHelper.DIGITAL_SIGNAGE_ID, 0);
                                        db.update(DataBaseHelper.TABLE_ORDER, contentValues, dataBaseHelper.ORDER_ID + "= ?", new String[]{id}); //Обновляем заказ и удаляем услугу

                                        db.delete(dataBaseHelper.DIGITAL_SIGNAGE, null, null);

                                        ds_layout.setVisibility(View.GONE);

                                        order_json.addProperty("digital_signage","0");

                                        clearBasket();

                                    }
                                });
                        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {

                            }
                        });
                        ad.show();

                    }
                });

                userCursor.moveToFirst();

                digital_signage_json.addProperty(dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS, userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS)));
                digital_signage_json.addProperty(dataBaseHelper.NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE, userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE)));
                digital_signage_json.addProperty(dataBaseHelper.SCREEN_SIZE, userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.SCREEN_SIZE)));
                digital_signage_json.addProperty(dataBaseHelper.FASTENING_TYPE, userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.FASTENING_TYPE)));
                digital_signage_json.addProperty(dataBaseHelper.NUMBER_OF_SCREENS_OBJECT_ON, userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_SCREENS_OBJECT_ON)));
                digital_signage_json.addProperty(dataBaseHelper.TOUCH_SCREEN, userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.TOUCH_SCREEN)));
                digital_signage_json.addProperty(dataBaseHelper.POSTING, userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.POSTING)));
                digital_signage_json.addProperty(dataBaseHelper.k_CONTENT, userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.k_CONTENT)));
                digital_signage_json.addProperty(dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT, userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT)));
                digital_signage_json.addProperty(dataBaseHelper.UNIQUE_CONTENT, userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.UNIQUE_CONTENT)));
                digital_signage_json.addProperty(dataBaseHelper.COMMENT, userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.COMMENT)));

                if(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS)) != 0){
                   object_count.setText(String.valueOf(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_INSTALLED_OBJECTS))));}
                else{
                    object_count.setText("Консультация");
                }

                if(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE)) == 4){

                    LinearLayout ty_la, si_la, ob_la, to_la;

                    ty_la = getView().findViewById(R.id.type_la);
                    si_la = getView().findViewById(R.id.size_la);
                    ob_la = getView().findViewById(R.id.object_on_la);
                    to_la = getView().findViewById(R.id.touch_screen_la);

                    ty_la.setVisibility(View.GONE);
                    si_la.setVisibility(View.GONE);
                    ob_la.setVisibility(View.GONE);
                    to_la.setVisibility(View.GONE);

                    size_count.setText("Консультация");
                    type.setVisibility(View.GONE);
                    size.setVisibility(View.GONE);
                    object_on.setVisibility(View.GONE);
                    touch_screen.setVisibility(View.GONE);

                }else{

                    if(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.TOUCH_SCREEN)) == 1){touch_screen.setText("Да");}
                    else{touch_screen.setText("Нет");}

                    size_count.setText(String.valueOf(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_SCREENS_OF_DIFFEREND_SIZE))));

                    object_on.setText(String.valueOf(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_SCREENS_OBJECT_ON))));

                    size.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.SCREEN_SIZE)));

                    type.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.FASTENING_TYPE)));
                }

                unique_content.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.UNIQUE_CONTENT)));
                k_content.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.k_CONTENT)));
                wire.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.POSTING)));
                installing_type.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.TYPE_OF_INSTALLATION_PROJECT)));

                if(String.valueOf("").equals(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.COMMENT)))){
                    comment.setVisibility(View.GONE);
                    commentHeader.setVisibility(View.GONE);
                }else{ comment.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.COMMENT)));}
            }else{ order_json.addProperty("digital_signage","0");}

            userCursor = db.query(DataBaseHelper.TABLE_ORDER, null, dataBaseHelper.TESOSQ + "= ?", new String[]{"1"},
                    null, null, null, null);

            if (userCursor.getCount() > 0) { //Если есть услуга "СОКОК"

                ImageView delete = getView().findViewById(R.id.delete_tesosq);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ad = new AlertDialog.Builder(getContext());
                        ad.setTitle("Удаление");  // заголовок
                        ad.setMessage("Удалить услугу из корзины?");
                        ad.setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {

                                        userCursor = db.query(dataBaseHelper.TABLE_ORDER, new String[]{dataBaseHelper.ORDER_ID},
                                                null, null, null, null, null);

                                        String id;

                                        userCursor.moveToFirst();
                                        id = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.ORDER_ID));

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(dataBaseHelper.TESOSQ, 0);
                                        db.update(DataBaseHelper.TABLE_ORDER, contentValues, dataBaseHelper.ORDER_ID + "= ?", new String[]{id}); //Обновляем заказ и удаляем услугу

                                        db.delete(dataBaseHelper.DIGITAL_SIGNAGE, null, null);

                                        tesosq.setVisibility(View.GONE);

                                        order_json.addProperty("tesosq","0");
                                        order_json.remove("tesosq_comment");

                                        clearBasket();

                                    }
                                });
                        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {

                            }
                        });
                        ad.show();

                    }});

                tesosq.setVisibility(View.VISIBLE);

                TextView comment, commentHeader;

                comment = getView().findViewById(R.id.comment2);
                commentHeader = getView().findViewById(R.id.commentHeader2);

                userCursor.moveToFirst();

                order_json.addProperty("tesosq","1");
                order_json.addProperty("tesosq_comment",userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.TESOSQ_COMMENT)));

                if(String.valueOf("").equals(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.TESOSQ_COMMENT)))){
                        comment.setText("Комментарий отсутствует");
                }else{
                    comment.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.TESOSQ_COMMENT)));
                }

            }else{ order_json.addProperty("tesosq","0");}

            userCursor = db.query(DataBaseHelper.TABLE_ORDER, null, dataBaseHelper.DIGITAL_CONSULTANT + "= ?", new String[]{"1"},
                    null, null, null, null);

            if (userCursor.getCount() > 0) { //Если есть услуга "Электронный консультант"

                ImageView delete = getView().findViewById(R.id.delete_digital_consultant);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ad = new AlertDialog.Builder(getContext());
                        ad.setTitle("Удаление");  // заголовок
                        ad.setMessage("Удалить услугу из корзины?");
                        ad.setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {

                                        userCursor = db.query(dataBaseHelper.TABLE_ORDER, new String[]{dataBaseHelper.ORDER_ID},
                                                null, null, null, null, null);

                                        String id;

                                        userCursor.moveToFirst();
                                        id = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.ORDER_ID));

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(dataBaseHelper.DIGITAL_CONSULTANT, 0);
                                        db.update(DataBaseHelper.TABLE_ORDER, contentValues, dataBaseHelper.ORDER_ID + "= ?", new String[]{id}); //Обновляем заказ и удаляем услугу

                                        db.delete(dataBaseHelper.DIGITAL_SIGNAGE, null, null);

                                        digital_consultant.setVisibility(View.GONE);

                                        order_json.addProperty("digital_consultant","0");
                                        order_json.remove("digital_consultant_comment");

                                        clearBasket();

                                    }
                                });
                        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int arg1) {

                            }
                        });
                        ad.show();

                    }});

                digital_consultant.setVisibility(View.VISIBLE);

                TextView comment, commentHeader;

                comment = getView().findViewById(R.id.comment3);
                commentHeader = getView().findViewById(R.id.commentHeader3);

                userCursor.moveToFirst();

                order_json.addProperty("digital_consultant","1");
                order_json.addProperty("digital_consultant_comment", userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.DIGITAL_CONSULTANT_COMMENT)));

                if(String.valueOf("").equals(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.DIGITAL_CONSULTANT_COMMENT)))){
                    comment.setText("Комментарий отсутствует");
                }else{
                    comment.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.DIGITAL_CONSULTANT_COMMENT)));
                }

            }else{ order_json.addProperty("digital_consultant","0");}

        }else{ //Если корзина пуста
            relativeLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
    }

    void clearBasket(){

        userCursor = db.query(dataBaseHelper.TABLE_ORDER, new String[]{dataBaseHelper.DIGITAL_SIGNAGE_ID, dataBaseHelper.E_OCHER_ID,
                        dataBaseHelper.TESOSQ, dataBaseHelper.DIGITAL_CONSULTANT},
                null, null, null, null, null);

        userCursor.moveToFirst();

        if(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.DIGITAL_SIGNAGE_ID)) == 0 &&
                userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.E_OCHER_ID)) == 0 &&
                userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.TESOSQ)) == 0 &&
                userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.DIGITAL_CONSULTANT)) == 0){
            db.delete(dataBaseHelper.TABLE_ORDER, null, null);
            db.delete(dataBaseHelper.E_OCHER, null, null);
            db.delete(dataBaseHelper.DIGITAL_SIGNAGE, null, null);
            relativeLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
    }

    String toRuLocale(int coast){
        Locale loc = new Locale("ru");
        NumberFormat formatter = NumberFormat.getInstance(loc);
        String result1 = formatter.format(coast);
        return String.valueOf(result1);
    }

    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

}

class ListViewAdapter extends BaseAdapter
{
    Activity context;
    String title[];
    String description[];
    ArrayList<String> title1;
    ArrayList<String> title2;
    ArrayList<String> date;

    public ListViewAdapter(Activity context, ArrayList<String> title1, ArrayList<String> title2, ArrayList<String> date) {
        super();
        this.context = context;
        this.title1 = title1;
        this.title2 = title2;
        this.date = date;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return title1.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtViewTitle1;
        TextView txtViewTitle2;
        TextView txtViewDate;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list1_layout, null);
            holder = new ViewHolder();
            holder.txtViewTitle1 = (TextView) convertView.findViewById(R.id.title1);
            holder.txtViewTitle2 = (TextView) convertView.findViewById(R.id.title2);
            holder.txtViewDate = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle1.setText("Заказ №"+title1.get(position));
        holder.txtViewTitle2.setText(title2.get(position));
        holder.txtViewDate.setText(date.get(position));

        return convertView;
    }

}