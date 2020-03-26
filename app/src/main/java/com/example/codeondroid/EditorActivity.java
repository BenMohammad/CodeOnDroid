package com.example.codeondroid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;



public class EditorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    CustomKeyboard mCustomKeyboard;
    float x1,x2;
    float y1, y2;
    final int MYREQUEST = 11;

    EditText codebox, inputbox;
    String code;
    TextView outputbox;
    Spinner Lang;

    String langs[] = {"Cpp14", "C", "Java", "Python3"};
    String exts[] = {"cpp" , "c" , "java" , "py"};
    String openfilename, lang , ext;
    int langPos = 1;
    int MIN_DISTANCE = 150;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        try{
            File file = new File(EditorActivity.this.getFilesDir(), "java_default_template.java");
            FileWriter writer = new FileWriter(file);
            writer.append("import java.util.*;\n" +
                    "class Hello{\n" +
                    "public static void main(String args[]){\n" +
                    "Scanner sc = new Scanner(System.in);\n" +
                    "int a = sc.nextInt();\n" +
                    "System.out.println(a*a%3);}\n" +
                    "}");
            writer.flush();
            writer.close();
        } catch (Exception e) {

        }
        try{
            File file = new File(EditorActivity.this.getFilesDir(), "cpp_default_template.cpp");
            FileWriter writer = new FileWriter(file);
            writer.append("#include <iostream>\n" +
                    "    using namespace std;\n" +
                    "    int main() {\n\n\n\n" +
                    "        return 0;\n" +
                    "    }");

            writer.flush();
            writer.close();
        } catch (Exception e) {

        }


//        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        mCustomKeyboard = new CustomKeyboard(this, R.id.keyboardview, R.xml.keyboard);
        mCustomKeyboard.registerEditText(R.id.codebox);
//        mCustomKeyboard.registerEditText(R.id.outputbox);
        mCustomKeyboard.registerEditText(R.id.inputbox);

        codebox = findViewById(R.id.codebox);
        inputbox = findViewById(R.id.inputbox);
        outputbox = findViewById(R.id.outputbox);

        Lang = findViewById(R.id.Lang);
        ArrayAdapter<String> adap1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, langs);

        Lang.setAdapter(adap1);
        Lang.setOnItemSelectedListener(this);


//        Toast.makeText(getApplicationContext(),langPos + "\t" + lang, Toast.LENGTH_LONG).show();
//
//        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//
//        float batteryPct = level * 100 / (float)scale;
//
//        if(batteryPct < 25){
//            Toast.makeText(getApplicationContext(), "BAttery's dying!!\nSave Your Code\n", Toast.LENGTH_LONG).show();
//        }

        SharedPreferences sf1=getSharedPreferences("myfile1", Context.MODE_PRIVATE);
        openfilename = sf1.getString("filename","NA");
        if(true){
            Log.d("TAG", "onCreate: " + openfilename);

            SharedPreferences sf=getSharedPreferences("myfile", Context.MODE_PRIVATE);
            lang = sf.getString("favLang","NA");
            try{
                ext = openfilename.substring(openfilename.indexOf(".") + 1);
//                Log.d("TAG", "onCreate: myextension " + ext );
                for(int i = 0;i < exts.length; i++)
                {
                    if(exts[i].equals((ext)))
                    {
                        langPos = i;
                        lang = langs[i];
                    }
                }

                int spinnerPosition = adap1.getPosition(lang);
                Lang.setSelection(spinnerPosition);
            }
            catch (Exception e){
                Log.d("TAG", "onCreate: myextension " + "exsnkjefjsf"  + e + openfilename);
                for(int i = 0;i < langs.length; i++)
                {
                    if(langs[i].equals((lang)))
                    {
                        langPos = i;
                    }
                }
                int spinnerPosition = adap1.getPosition(lang);
                Lang.setSelection(spinnerPosition);
            }






            try {
                String yourFilePath = getApplicationContext().getFilesDir() + "/" + openfilename;
                FileInputStream fin=new FileInputStream(yourFilePath);
                InputStreamReader isr = new InputStreamReader(fin);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line = "";
                while (true) {
                    try {
                        if (!((line = bufferedReader.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sb.append(line + "\n");
                }
                Log.d("TAG", "onCreate: " + sb.toString()  + "\n") ;
                setTitle(openfilename);
                codebox.setText(sb.toString() + " ");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            Log.d("TAG", "onCreate: " + "Empty");
        }




    }





    public void compileCode() {

        outputbox.setText(inputbox.getText().toString() + "\n" + "in is out on error");
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest sr = new StringRequest(Request.Method.POST,"https://ide.geeksforgeeks.org/main.php/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Volley", "onResponse: " + response);
                try {
                    JSONObject res = new JSONObject(response);
                    String output = res.getString("output");
                    outputbox.setText(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                queue.getCache().clear();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Volley", "onErrorResponse: " + error);
            }
        }){
            @Override
            protected Map<String,String> getParams(){

                Log.d("code", "getParams: " + code);
                Map<String,String> params = new HashMap<String, String>();
                params.put("lang" , langs[langPos]);
                params.put("code" , codebox.getText().toString());
                params.put("input" , inputbox.getText().toString());
                params.put("save" , "false");
//                Toast.makeText(getApplicationContext(),langPos + "", Toast.LENGTH_LONG).show();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }

    @Override public void onBackPressed() {
        // NOTE Trap the back key: when the CustomKeyboard is still visible hide it, only when it is invisible, finish activity
        if( mCustomKeyboard.isCustomKeyboardVisible() ) mCustomKeyboard.hideCustomKeyboard(); else this.finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent touchevent)
    {
        switch (touchevent.getAction())
        {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN:
            {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                x2 = touchevent.getX();
                y2 = touchevent.getY();
                View focusCurrent = getWindow().getCurrentFocus();
                if (x2-x1>=MIN_DISTANCE)
                {
                    //Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_LONG).show();
                    if (mCustomKeyboard.curr_layout>0)
                    {
                        mCustomKeyboard.curr_layout-=1;
                        mCustomKeyboard.change_keyboard( mCustomKeyboard.keylayouts[mCustomKeyboard.curr_layout]);
                    }
                }

                // if right to left sweep event on screen
                if (x1-x2>=MIN_DISTANCE)
                {
                    //Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_LONG).show();
                    if (mCustomKeyboard.curr_layout< mCustomKeyboard.kbcount-1)
                    {
                        mCustomKeyboard.curr_layout+=1;
                        mCustomKeyboard.change_keyboard(mCustomKeyboard.keylayouts[mCustomKeyboard.curr_layout]);
                    }
                }

                break;
            }
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("TAG", "onItemSelected: " + position + " " + id);
        langPos = position;
        mCustomKeyboard.change_keyboard(R.xml.keyboard);
        mCustomKeyboard.curr_layout=1;
        SharedPreferences sf=getSharedPreferences("myfile2", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sf.edit();
        edit.clear(); // remove existing entries
        edit.putString("selLang",langs[langPos]);
        edit.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

//        Toast.makeText(getApplicationContext(),langPos + "", Toast.LENGTH_LONG).show();
        Log.d("TAG", "onNothingSelected: ");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void saveCode() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(getApplicationContext());
        if(openfilename.equals("NA")){

            edittext.setText("." + exts[langPos]);
        }
        else{
            edittext.setText(openfilename);
        }

        edittext.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        alert.setTitle("Save Your Code");
        alert.setMessage("Enter Your File Name to save");

        alert.setView(edittext);


        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String filename = edittext.getText().toString();

                try {
                    File file = new File(EditorActivity.this.getFilesDir(), "" + filename);
                    FileWriter writer = new FileWriter(file);
                    writer.append(codebox.getText().toString() + " ");
                    writer.flush();
                    writer.close();
                    Toast.makeText(getApplicationContext(),"Saved your code as " + filename, Toast.LENGTH_LONG).show();
                    setTitle(filename);
                } catch (Exception e) {}

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();








    }

    public void share_code() {

//        File file = new File(EditorActivity.this.getFilesDir() + "/shared");
//        if(!file.exists()){
//            file.mkdir();
//        }
//        try {
//            file = new File(EditorActivity.this.getFilesDir() + "/shared", "" + filename.getText().toString());
//            FileWriter writer = new FileWriter(file);
//            writer.append(codebox.get Text().toString());
//            writer.flush();
//            writer.close();
////            Toast.makeText(getApplicationContext(),"Save your code as " + filename.getText().toString(), Toast.LENGTH_LONG).show();
//        } catch (Exception e) {}
//
//        String myFilePath = getApplicationContext().getFilesDir() + "/shared/" + filename.getText().toString();
//        if(filename.getText().toString().equals(""))
//        {
//            Toast.makeText(this, "" + "Give filename to share" , Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Toast.makeText(this, "" + myFilePath , Toast.LENGTH_SHORT).show();
//        }
        String getText = codebox.getText().toString();
        if (!getText.equals("") && getText.length() != 0)
            shareText(getText);
        else
            Toast.makeText(this,
                    "Please enter something to share.", Toast.LENGTH_SHORT)
                    .show();

    }

    private void shareText(String text) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");// Plain format text

        // You can add subject also
        /*
         * sharingIntent.putExtra( android.content.Intent.EXTRA_SUBJECT,
         * "Subject Here");
         */
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivityForResult(Intent.createChooser(sharingIntent, "Share Text Using"),0);
    }


    public void undo_function() {
        mCustomKeyboard.undo_action();
    }

    public void redo_function() {
        mCustomKeyboard.redo_action();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.savecodebutton:
                saveCode();
                return true;
            case R.id.sharecodebutton:
                share_code();
                return true;
            case R.id.undocodebutton:
                undo_function();
                return true;
            case R.id.redocodebutton:
                redo_function();
                return true;
            case R.id.runcodebutton:
                compileCode();
                return true;
            case R.id.clipbutton:
                openclip();
                return true;
            case R.id.snippetscode:
                opensnippet();
                return true;
            default:
                return false;

        }
    }

    private void opensnippet() {
        Intent i = new Intent(this,code_snippets.class);
        startActivityForResult(i,MYREQUEST);
    }

    private void openclip() {
        String text = "";
        text = getClipboardDataForHoney(getApplicationContext());
        EditText edittext = findViewById(R.id.codebox);
        Editable editable = edittext.getText();
        int start = edittext.getSelectionStart();
        editable.insert(start, text);
    }
    private static String getClipboardDataForHoney(Context mContext) {
        ClipboardManager clipboard = (ClipboardManager) mContext
                .getSystemService(Context.CLIPBOARD_SERVICE);//get Clipboard manager
        ClipData abc = clipboard.getPrimaryClip();//Get Primary clip
        //Toast.makeText(mContext,abc.getItemCount()+"",Toast.LENGTH_SHORT).show();
        ClipData.Item item = abc.getItemAt(0);//Get item from clip data

        return item.getText().toString();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MYREQUEST)
        {
            if(resultCode == RESULT_OK) {
                String snip = data.getStringExtra("snip");
                EditText edittext = findViewById(R.id.codebox);
                Editable editable = edittext.getText();
                int start = edittext.getSelectionStart();
                editable.insert(start,snip);
            }
        }
    }



}
