package ru.dolphins_it.service.E_Ocher;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

import ru.dolphins_it.service.DataBaseHelper;
import ru.dolphins_it.service.R;
import ru.dolphins_it.service.Session;


/**
 * A simple {@link Fragment} subclass.
 */
public class E_ocherCalculator extends Fragment {

    private SeekBar seekBar1;
    private SeekBar seekBar2;
    private SeekBar seekBar3;

    private TextInputLayout inputLayout;
    private TextInputEditText inputEditText;

    private TextView operator_count;
    private TextView terminal_count;
    private TextView signage_count;
    private TextView result;
    public static TextView header;
    private Button order;

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;
    private Cursor userCursor;

    private Session session = new Session();

    public E_ocherCalculator() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataBaseHelper = new DataBaseHelper(getContext());
        db = dataBaseHelper.getWritableDatabase();
        session.inicialize(getContext());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_e_ocher_calculator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { //Иницициализация элементов во фрагменте

        header = getView().findViewById(R.id.name);
        header.startAnimation( AnimationUtils.loadAnimation(getContext(), R.anim.text_blink));

        inputLayout = getView().findViewById(R.id.comment_til);
        inputEditText = getView().findViewById(R.id.comment);

        seekBar1 = getView().findViewById(R.id.seekBar1);
        seekBar2 = getView().findViewById(R.id.seekBar2);
        seekBar3 = getView().findViewById(R.id.seekBar3);

        operator_count = getView().findViewById(R.id.operator_count);
        terminal_count = getView().findViewById(R.id.terminal_count);
        signage_count = getView().findViewById(R.id.signange_count);
        result = getView().findViewById(R.id.result);

        order = getView().findViewById(R.id.button);

        if (session.session_exist()) {
            order.setText("Добавить в корзину");
            inputLayout.setVisibility(View.VISIBLE);

        if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.E_OCHER_ID + ") FROM " + dataBaseHelper.E_OCHER, null) > 0) { //Проверяем существоваание именно этой услуги в корзине
            order.setText("Добавлено в корзину");
            order.setEnabled(false);

            userCursor = db.query(dataBaseHelper.E_OCHER, new String[]{dataBaseHelper.NUMBER_OF_OPERATORS, dataBaseHelper.NUMBER_OF_TERMINALS, dataBaseHelper.NUMBER_OF_BOARDS,
                            dataBaseHelper.APPROXIMATE_COAST, dataBaseHelper.COMMENT},
                    null, null, null, null, null);

            userCursor.moveToFirst();
            int s1, s2, s3;
            s1 = userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_OPERATORS));
            s2 = userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_TERMINALS));
            s3 = userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.NUMBER_OF_BOARDS));
            seekBar1.setProgress(s1);
            operator_count.setText(String.valueOf(s1));
            seekBar2.setProgress(s2);
            terminal_count.setText(String.valueOf(s2));
            seekBar3.setProgress(s3);
            signage_count.setText(String.valueOf(s3));
            result.setText("Примерная стоимость: " + toRuLocale(userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.APPROXIMATE_COAST))) + "\u20BD");
            inputEditText.setText(userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.COMMENT)));
        }
        }

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Добавлениие этой услуги в корзину

                if (session.session_exist()) {
                ContentValues contentValues = new ContentValues();

                if (DatabaseUtils.longForQuery(db, "SELECT COUNT(" + dataBaseHelper.ORDER_ID + ") FROM " + dataBaseHelper.TABLE_ORDER, null) > 0) { //Проверяем наличие заказов в базе данных

                    userCursor = db.query(dataBaseHelper.TABLE_ORDER, new String[]{dataBaseHelper.ORDER_ID},
                            null, null, null, null, null);

                    String id;

                    userCursor.moveToFirst();
                    id = userCursor.getString(userCursor.getColumnIndex(dataBaseHelper.ORDER_ID));


                    contentValues.put(dataBaseHelper.E_OCHER_ID, add_e_ocher());
                    db.update(DataBaseHelper.TABLE_ORDER, contentValues, dataBaseHelper.ORDER_ID + "= ?", new String[]{id}); //Обновляем заказ и добавляем услугу

                    }else{ //Если закозов нет
                    contentValues.put(dataBaseHelper.E_OCHER_ID, add_e_ocher());
                    db.insert(DataBaseHelper.TABLE_ORDER, null, contentValues); //Добавляем услугу к заказу
                }
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    order.setText("Добавлено в корзину");
                    order.setEnabled(false);
            }else{
                    Toast toast = Toast.makeText(getContext(),
                            "Авторизируйтесь или зарегистрируйтесь, чтобы оставить заявку", Toast.LENGTH_SHORT);
                    toast.show();
            }}
        });

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                operator_count.setText(String.valueOf(seekBar1.getProgress()));
                result.setText("Примерная стоимость: "+toRuLocale(priceTotal(seekBar1.getProgress(), seekBar2.getProgress(), seekBar3.getProgress()))+"\u20BD");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                terminal_count.setText(String.valueOf(seekBar2.getProgress()));
                result.setText("Примерная стоимость: "+toRuLocale(priceTotal(seekBar1.getProgress(), seekBar2.getProgress(), seekBar3.getProgress()))+"\u20BD");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                signage_count.setText(String.valueOf(seekBar3.getProgress()));
                result.setText("Примерная стоимость: "+toRuLocale(priceTotal(seekBar1.getProgress(), seekBar2.getProgress(), seekBar3.getProgress()))+"\u20BD");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    int add_e_ocher(){

        ContentValues contentValues = new ContentValues();
        contentValues.put(dataBaseHelper.NUMBER_OF_OPERATORS, seekBar1.getProgress());
        contentValues.put(dataBaseHelper.NUMBER_OF_TERMINALS, seekBar2.getProgress());
        contentValues.put(dataBaseHelper.NUMBER_OF_BOARDS,    seekBar3.getProgress());
        contentValues.put(dataBaseHelper.APPROXIMATE_COAST,   priceTotal(seekBar1.getProgress(), seekBar2.getProgress(), seekBar3.getProgress()));
        contentValues.put(dataBaseHelper.COMMENT,             inputEditText.getText().toString());
        db.insert(DataBaseHelper.E_OCHER, null, contentValues); //Добавляем услугу в таблицу

        userCursor = db.query(dataBaseHelper.E_OCHER, new String[]{dataBaseHelper.E_OCHER_ID},
                null, null, null, null, null);

        userCursor.moveToFirst();

        return userCursor.getInt(userCursor.getColumnIndex(dataBaseHelper.E_OCHER_ID));

    }

    int priceTotal(int priceOperator, int priceTerminal, int priceSignange){
        return (priceOperator*10000) + (priceTerminal*65000) + (priceSignange*20000);
    }

    String toRuLocale(int coast){

        Locale loc = new Locale("ru");
        NumberFormat formatter = NumberFormat.getInstance(loc);
        String result1 = formatter.format(coast);

        return String.valueOf(result1);

    }

}
