package nationwide.co.uk.thesocietybot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ibm.watson.developer_cloud.assistant.v1.model.Context;

import java.sql.Timestamp;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private ChatAdapter mMessageAdapter;
    private static List<MessageContent> messageList = new ArrayList<MessageContent>();

    private MessageContent msgcontent = new MessageContent();
    private EditText msgEditor;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH.mm");
    private SimpleDateFormat sf = new SimpleDateFormat("HH.mm");
    private WatsonAssistant watsonAssistant = new WatsonAssistant();
    private Context context = new Context();
    private DrawerLayout mDrawerLayout;
    private ChatActivity chatActivity;
    private WatsonTask watsonTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_chatscreen);

        chatActivity = this;
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        if (menuItem.getItemId() == (R.id.nav_logout)){
                            messageList.clear();
                            Intent lintent = new Intent(chatActivity,LoginActivity.class);
                            startActivity(lintent);
                            mDrawerLayout.closeDrawers();
                        }

                        return true;
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        Intent intent = this.getIntent();

        mMessageRecycler = findViewById(R.id.recyclerview_message_list);
        msgcontent.setMessage("Hi "+intent.getStringExtra("username")+", Good Day to u \nWhat is your bank of choice today");
        msgcontent.setCreatedAt(sdf.format(Calendar.getInstance().getTime()));
        msgcontent.setUserId("received");
        messageList.add(msgcontent);
        context.put("ConvoStart","true");
        mMessageAdapter = new ChatAdapter(this, messageList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mMessageRecycler.setLayoutManager(llm);
        mMessageRecycler.setItemAnimator(new DefaultItemAnimator());
        mMessageRecycler.setAdapter(mMessageAdapter);

        msgEditor = (EditText) findViewById(R.id.edittext_chatbox);

        Button sendBtn = (Button) findViewById(R.id.button_chatbox_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageContent msgcont = new MessageContent();
                msgcont.setMessage(msgEditor.getText().toString());
                msgcont.setCreatedAt(sf.format(Calendar.getInstance().getTime()));
                msgcont.setUserId("Sender");
                msgcont.setContext(context);
                messageList.add(msgcont);
                msgEditor.getText().clear();
                mMessageAdapter.notifyDataSetChanged();
                mMessageRecycler.smoothScrollToPosition(messageList.size());
                //Push Data to watson & fetch the response

                callWatson(msgcont);

            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void callWatson (MessageContent msgcontent){
        watsonTask = new WatsonTask(msgcontent);
        watsonTask.execute((Void)null);
    }

    public class WatsonTask extends AsyncTask<Void, Void, Boolean> {

        private  MessageContent mContent;

        WatsonTask(MessageContent msgContent) {
            mContent = msgContent;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            /*try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }*/

            mContent = watsonAssistant.getWatsonResponse(mContent.getMessage(),context);
            context = mContent.getContext();
            if (context != null) {
                if ((context.get("Authorization")) != null) {
                    context.remove("ConvoStart");
                }
            }
            mContent.setCreatedAt(sf.format(Calendar.getInstance().getTime()));
            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            watsonTask = null;

            messageList.add(mContent);
            mMessageAdapter.notifyDataSetChanged();
            mMessageRecycler.smoothScrollToPosition(messageList.size());

        }

        @Override
        protected void onCancelled() {
            watsonTask = null;

        }

    }
}
