package ru.dolphins_it.service.Auth;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.dolphins_it.service.Api.Api;
import ru.dolphins_it.service.Api.ApiService;
import ru.dolphins_it.service.Api.OrderApi;
import ru.dolphins_it.service.R;
import ru.dolphins_it.service.Session;

public class FullOrder extends AppCompatActivity {

    ImageView back;
    LinearLayout reason_layout;
    TextView order_header, reason;

    Api api;
    Gson gson;
    Retrofit retrofit;
    protected List<OrderApi> list;

    Session session = new Session();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_order);

        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://37.204.111.194/dolphins/") // Базовый URL
                .addConverterFactory(GsonConverterFactory.create(gson)) // Конвертер JSON
                .build();

        api = retrofit.create(Api.class);

        session.inicialize(this);

        back = findViewById(R.id.back);
        order_header = findViewById(R.id.order_header);
        reason = findViewById(R.id.reason);
        reason_layout = findViewById(R.id.reason_layout);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();

        String order_id = bundle.getString("order_id"); //Получаем id заявки

        order_header.setText("Заказ №" + order_id);

        if(bundle.getString("reason_for_rejection") != null){
            reason_layout.setVisibility(View.VISIBLE);
            reason.setText(bundle.getString("reason_for_rejection"));
        }

        Call<List<OrderApi>> get_orders = api.getFullOrder("get_full_order", order_id); //Запрос на получение заявок пользователя

        get_orders.enqueue(new Callback<List<OrderApi>>() {

            @Override
            public void onResponse(Call<List<OrderApi>> get_orders, Response<List<OrderApi>> response) {

                list = response.body();

                ConstraintLayout e_ocher_layout, ds_layout;
                LinearLayout digital_consultant, tesosq;

                e_ocher_layout = findViewById(R.id.e_ocher);
                ds_layout = findViewById(R.id.digital_signage);
                digital_consultant = findViewById(R.id.digital_consultant_layout);
                tesosq = findViewById(R.id.tesosq_layout);

                if(list != null) {
                    if (list.get(0).getE_ocher_id() != null) { //Если есть услуга "Электронная очередь"

                        e_ocher_layout.setVisibility(View.VISIBLE);

                        int position = 0;

                        for(int i = 0; i < list.size(); i++){
                            if(list.get(i).getNumber_of_operators() != null){position = i; break;}
                        }

                        TextView operator, terminal, signage, result, comment, commentHeader;

                        operator = findViewById(R.id.operator_count);
                        terminal = findViewById(R.id.terminal_count);
                        signage = findViewById(R.id.signange_count);
                        result = findViewById(R.id.result);
                        comment = findViewById(R.id.comment);
                        commentHeader = findViewById(R.id.commentHeader);


                        operator.setText(list.get(position).getNumber_of_operators());
                        terminal.setText(list.get(position).getNumber_of_terminals());
                        signage.setText(list.get(position).getNumber_of_boards());
                        result.setText(toRuLocale(Integer.valueOf(list.get(position).getApproximate_coast())) + "\u20BD");
                        if (list.get(0).getComment() == null) {
                            comment.setVisibility(View.GONE);
                            commentHeader.setVisibility(View.GONE);
                        } else {
                            comment.setVisibility(View.VISIBLE);
                            commentHeader.setVisibility(View.VISIBLE);
                            comment.setText(list.get(position).getComment());
                        }

                    }

                    if (list.get(0).getDigital_signage_id() != null) { //Если есть услуга "Цифровые вывески"

                        ds_layout.setVisibility(View.VISIBLE);

                        int position = 0;

                        for(int i = 0; i < list.size(); i++){
                            if(list.get(i).getNumber_of_installed_objects() != null){position = i; break;}
                        }

                        TextView object_count, size_count, type, size, object_on, touch_screen, unique_content, k_content,
                                wire, installing_type, comment, commentHeader;

                        object_count = findViewById(R.id.object_count);
                        size_count = findViewById(R.id.size_count);
                        type = findViewById(R.id.type);
                        size = findViewById(R.id.size);
                        object_on = findViewById(R.id.object_on);
                        touch_screen = findViewById(R.id.touch_screen);
                        unique_content = findViewById(R.id.unique_content);
                        k_content = findViewById(R.id.k_content);
                        wire = findViewById(R.id.wire);
                        installing_type = findViewById(R.id.installing_type);
                        comment = findViewById(R.id.comment1);
                        commentHeader = findViewById(R.id.commentHeader1);


                        if (String.valueOf("0").equals(list.get(position).getNumber_of_installed_objects())) {
                            object_count.setText("Консультация");
                        } else {
                            object_count.setText(String.valueOf(list.get(position).getNumber_of_installed_objects()));
                        }

                        if (String.valueOf("4").equals(list.get(position).getNumber_of_screens_of_differend_size())) {

                            LinearLayout ty_la, si_la, ob_la, to_la;

                            ty_la = findViewById(R.id.type_la);
                            si_la = findViewById(R.id.size_la);
                            ob_la = findViewById(R.id.object_on_la);
                            to_la = findViewById(R.id.touch_screen_la);

                            ty_la.setVisibility(View.GONE);
                            si_la.setVisibility(View.GONE);
                            ob_la.setVisibility(View.GONE);
                            to_la.setVisibility(View.GONE);

                            size_count.setText("Консультация");
                            type.setVisibility(View.GONE);
                            size.setVisibility(View.GONE);
                            object_on.setVisibility(View.GONE);
                            touch_screen.setVisibility(View.GONE);

                        } else {

                            if (String.valueOf("1").equals(list.get(position).getTouch_screen())) {
                                touch_screen.setText("Да");
                            } else {
                                touch_screen.setText("Нет");
                            }

                            size_count.setText(list.get(position).getNumber_of_screens_of_differend_size());

                            object_on.setText(list.get(position).getNumber_of_screens_object_on());

                            size.setText(list.get(position).getScreen_size());

                            type.setText(list.get(position).getType_of_installation_project());
                        }

                        if(list.get(position).getUnique_content() == null){unique_content.setText("Консультация");}
                        else if(String.valueOf("0").equals(list.get(position).getUnique_content())){
                        unique_content.setText("Нет");}else{unique_content.setText("Да");}

                        if(list.get(position).getK_content() == null){k_content.setText("Консультация");}
                        else if(String.valueOf("0").equals(list.get(position).getK_content())){
                            k_content.setText("Нет");}else{k_content.setText("Да");}

                        wire.setText(list.get(position).getPosting());
                        installing_type.setText(list.get(position).getType_of_installation_project());

                        if (list.get(position).getComment() == null) {
                            comment.setVisibility(View.GONE);
                            commentHeader.setVisibility(View.GONE);
                        } else {
                            comment.setVisibility(View.VISIBLE);
                            commentHeader.setVisibility(View.VISIBLE);
                            comment.setText(list.get(position).getComment());
                        }
                    }

                    if (list.get(0).getTesosq() != null) { //Если есть услуга "СОКОК"

                        tesosq.setVisibility(View.VISIBLE);

                        TextView comment, commentHeader;

                        comment = findViewById(R.id.comment2);
                        commentHeader = findViewById(R.id.commentHeader2);

                        if (String.valueOf("").equals(list.get(0).getTesosq())) {
                            comment.setText("Комментарий отсутствует");
                        } else {
                            comment.setText(list.get(0).getTesosq());
                        }

                    }

                    if (list.get(0).getDigital_consultant() != null) { //Если есть услуга "Электронный консультант"


                        digital_consultant.setVisibility(View.VISIBLE);

                        TextView comment, commentHeader;

                        comment = findViewById(R.id.comment3);
                        commentHeader = findViewById(R.id.commentHeader3);

                        if (String.valueOf("").equals(list.get(0).getDigital_consultant())) {
                            comment.setText("Комментарий отсутствует");
                        } else {
                            comment.setText(list.get(0).getDigital_consultant());
                        }

                    }
                }

            }

            @Override
            public void onFailure(Call<List<OrderApi>> get_orders, Throwable t) { //Запрос провалился
                Toast toast = Toast.makeText(FullOrder.this,
                        "Возникла ошибка при выполнении запроса: " + t, Toast.LENGTH_SHORT);
                toast.show();
            }


});


    }

    String toRuLocale(int coast){
        Locale loc = new Locale("ru");
        NumberFormat formatter = NumberFormat.getInstance(loc);
        String result1 = formatter.format(coast);
        return String.valueOf(result1);
    }

}

