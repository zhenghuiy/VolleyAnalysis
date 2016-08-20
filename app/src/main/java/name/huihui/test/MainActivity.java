package name.huihui.test;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import org.apache.http.HttpResponse;

import name.huihui.volleyanalysis.R;
import name.huihui.volleyanalysis.Volley;

public class MainActivity extends Activity {
    private static final String TEST_URL = "http://huihui.name";

    private TextView mTvContent;

    private DialogFragment mDialog;

    private AsyncTask<String, Void, String> mAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initContentViews();
    }

    private void initContentViews() {
        mTvContent = (TextView) findViewById(R.id.content);
        mDialog = new DialogFragment();
        //test volley
        mAsyncTask = new AsyncTask<String, Void, String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mDialog.show(getFragmentManager(), null);
            }

            @Override
            protected String doInBackground(String... params) {
                if (params != null && params.length > 0) {
                    HttpResponse response = Volley.performRequest(params[0]);

                    if (response == null) {
                        return null;
                    }

                    try {
                        return String.valueOf(response.getStatusLine().getStatusCode());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mDialog.dismiss();
                mTvContent.setText(s);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAsyncTask.execute(TEST_URL);
    }
}
