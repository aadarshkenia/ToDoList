package com.example.calenderquickstart;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Notebook;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aadarsh-ubuntu on 7/31/15.
 */
public class FetchNotebooksAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
    private MainActivity mActivity;

    FetchNotebooksAsyncTask(MainActivity activity){mActivity=activity;}

    @Override
    protected ArrayList<String> doInBackground(Void... params) {

        try {
            List<Notebook> notebooks = new ArrayList<>();
            List<String> notebookNames = new ArrayList<>();

            EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
            notebooks = noteStoreClient.listNotebooks();

            for(Notebook n : notebooks){
                notebookNames.add(n.getName());
            }
            return (ArrayList)notebookNames;
        }
        catch (Exception e)
        {
            System.out.println("Error while fetching notebooks.");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        if(result==null)
            System.out.println("NO NOTEBOOKS TO SHOW");
        else
        {
            ListView lv = (ListView) mActivity.findViewById(R.id.list);
            ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1, result);
            lv.setAdapter(arrayAdapter);
        }
    }
}
