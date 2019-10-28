package ru.dolphins_it.service.Auth;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import ru.dolphins_it.service.R;
import ru.dolphins_it.service.Session;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class Admin extends Fragment {

    Api api;
    Gson gson;
    Retrofit retrofit;
    ImageView back, exit, update_data, cart;
    Switch sms;

    AlertDialog.Builder ad;

    ScrollView scrollView;
    RelativeLayout relativeLayout;

    ListView orders_list;

    Session session = new Session();

    TextInputEditText nameEditText, phone_numberEditText, jobEditText, e_mailEditText;

    public Admin() {
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        scrollView = getView().findViewById(R.id.scroll_basket_content);
        relativeLayout = getView().findViewById(R.id.empty_content);

        orders_list = getView().findViewById(R.id.orders_list);

        nameEditText = getView().findViewById(R.id.name);
        phone_numberEditText = getView().findViewById(R.id.number);
        jobEditText = getView().findViewById(R.id.job);
        e_mailEditText = getView().findViewById(R.id.e_mail);
        back = getView().findViewById(R.id.back);
        exit = getView().findViewById(R.id.exit);
        update_data = getView().findViewById(R.id.update_data);
        sms = getView().findViewById(R.id.sms);

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
                user_data.addProperty("organization_name", "");
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
    final ArrayList<String> user = new ArrayList<>();
    final ArrayList<String> user_phone = new ArrayList<>();
    final ArrayList<String> user_mail = new ArrayList<>();
    final ArrayList<String> organization = new ArrayList<>();
    final ArrayList<String> reason_for_rejection = new ArrayList<>();

    Call<List<ApiService>> get_orders = api.getOrder("get_order"); //Запрос на получение заявок пользователя

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
                    }else if (String.valueOf("0").equals(list.get(i).getOrderStatus())){
                        status.add(i, "Отклонена "+list.get(i).getDate_of_adoption());
                    }

                    date.add(i, "Заявка отправлена " + list.get(i).getOrderdate());
                    user.add(i, list.get(i).getUserName());
                    user_mail.add(i, list.get(i).getE_mail());
                    user_phone.add(i, list.get(i).getPhoneNumber());
                    organization.add(i, list.get(i).getOrganizationName());

                }
                orders_list.setAdapter(new ListViewAdapterAdmin(getActivity(), order_id, status, date, user, user_phone, user_mail));
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
            Intent intent = new Intent(getContext(), FullOrderAdmin.class);
            intent.putExtra("order_id", order_id.get(position));
            intent.putExtra("status", status.get(position));
            intent.putExtra("date", date.get(position));
            intent.putExtra("user", user.get(position));
            intent.putExtra("user_phone", user_phone.get(position));
            intent.putExtra("user_mail", user_mail.get(position));
            intent.putExtra("organization", organization.get(position));

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

class ListViewAdapterAdmin extends BaseAdapter
{
    Activity context;
    String title[];
    String description[];
    ArrayList<String> title1;
    ArrayList<String> title2;
    ArrayList<String> date;
    ArrayList<String> user;
    ArrayList<String> user_phone;
    ArrayList<String> user_mail;

    public ListViewAdapterAdmin(Activity context, ArrayList<String> title1, ArrayList<String> title2, ArrayList<String> date,
                                ArrayList<String> user, ArrayList<String> user_phone, ArrayList<String> user_mail) {
        super();
        this.context = context;
        this.title1 = title1;
        this.title2 = title2;
        this.date = date;
        this.user = user;
        this.user_phone = user_phone;
        this.user_mail = user_mail;
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
        TextView txtViewUser;
        TextView txtViewUserPhone;
        TextView txtViewUserMail;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list2_layout, null);
            holder = new ViewHolder();
            holder.txtViewTitle1 = (TextView) convertView.findViewById(R.id.title1);
            holder.txtViewTitle2 = (TextView) convertView.findViewById(R.id.title2);
            holder.txtViewDate = (TextView) convertView.findViewById(R.id.date);
            holder.txtViewUser = (TextView) convertView.findViewById(R.id.user);
            holder.txtViewUserPhone = (TextView) convertView.findViewById(R.id.user_phone);
            holder.txtViewUserMail = (TextView) convertView.findViewById(R.id.user_mail);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewTitle1.setText("Заказ №"+title1.get(position));
        holder.txtViewTitle2.setText(title2.get(position));
        holder.txtViewDate.setText(date.get(position));
        holder.txtViewUser.setText(user.get(position));
        holder.txtViewUserMail.setText(user_mail.get(position));
        holder.txtViewUserPhone.setText(user_phone.get(position));

        return convertView;
    }

}