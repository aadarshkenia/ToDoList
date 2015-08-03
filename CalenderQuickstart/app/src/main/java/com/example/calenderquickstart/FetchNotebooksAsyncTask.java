package com.example.calenderquickstart;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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

            EvernoteSession session = EvernoteSession.getInstance();
            EvernoteNoteStoreClient noteStoreClient = session.getEvernoteClientFactory().getNoteStoreClient();
            notebooks = noteStoreClient.listNotebooks();

            Notebook taskbook = null;
            for(Notebook n : notebooks){
                if(n.getName().equals("TODO"))
                {
                    taskbook=n;
                    break;
                }
            }
            //Retrieve all notes
            if(taskbook!=null){
                NoteFilter filter = new NoteFilter();
                filter.setNotebookGuid(taskbook.getGuid());

                NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
                spec.setIncludeTitle(true);

                NotesMetadataList notes = noteStoreClient.findNotesMetadata(filter, 0, 10, spec);

                //Get note names and return
                List<String> noteContents = new ArrayList<>();
                for (NoteMetadata note : notes.getNotes()){
                    // Do something with the notes we found
                    Note fullNote = noteStoreClient.getNote(note.getGuid(), true, true, false, false);
                    Document document = Jsoup.parse(fullNote.getContent());
                    Elements divs = document.select("div");
                    for(int i=0;i<divs.size();i++){
                        noteContents.add(divs.get(i).ownText());
                    }

                }
                return (ArrayList)noteContents;
            }

        }//try ends
        catch (Exception e)
        {
            System.out.println("Error while fetching notebooks.");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        if(result==null || result.size()==0)
            System.out.println("NO NOTES TO SHOW");
        else
        {
            MyUtility.displayItems(mActivity, R.id.list_evernote, result, MyUtility.event_evernote);
        }
    }
}
