package com.sessionm.smp_auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.sessionm.smp_auth.custom.CustomAuthActivity;
import com.sessionm.smp_auth.email.EmailPasswordActivity;
import com.sessionm.smp_auth.facebook.FacebookLoginActivity;
import com.sessionm.smp_auth.sessionm.SessionMTokenActivity;
import com.sessionm.smp_auth.webauth.WebAuthActivity;

/**
 * Simple list-based Activity to redirect to one of the other Activities. This Activity does not
 * contain any useful code related to SMP Authentication. You may want to start with
 * one of the following Files:
 * {@link WebAuthActivity}
 * {@link EmailPasswordActivity}
 * {@link SessionMTokenActivity}
 * {@link FacebookLoginActivity}
 * {@link CustomAuthActivity}
 */
public class ChooserActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final Class[] CLASSES = new Class[]{
            WebAuthActivity.class,
            EmailPasswordActivity.class,
            SessionMTokenActivity.class,
            //TODO: We don't support native login on backend yet
//            GoogleSignInActivity.class,
            FacebookLoginActivity.class
//            CustomAuthActivity.class
    };

    private static final int[] DESCRIPTION_IDS = new int[]{
            R.string.desc_webauth,
            R.string.desc_emailpassword,
            R.string.desc_sessionm_token,
            //TODO: We don't support native login on backend yet
//            R.string.desc_google_sign_in,
            R.string.desc_facebook_login
//            R.string.desc_custom_auth,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooser);

        // Set up ListView and Adapter
        ListView listView = (ListView) findViewById(R.id.list_view);

        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setDescriptionIds(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    public static class MyArrayAdapter extends ArrayAdapter<Class> {

        private Context mContext;
        private Class[] mClasses;
        private int[] mDescriptionIds;

        public MyArrayAdapter(Context context, int resource, Class[] objects) {
            super(context, resource, objects);

            mContext = context;
            mClasses = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

            ((TextView) view.findViewById(android.R.id.text1)).setText(mClasses[position].getSimpleName());
            ((TextView) view.findViewById(android.R.id.text2)).setText(mDescriptionIds[position]);

            return view;
        }

        public void setDescriptionIds(int[] descriptionIds) {
            mDescriptionIds = descriptionIds;
        }
    }
}
