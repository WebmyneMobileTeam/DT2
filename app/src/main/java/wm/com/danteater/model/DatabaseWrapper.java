package wm.com.danteater.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mvnordic.mviddeviceconnector.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;

/**
 * Created by dhruvil on 16-09-2014.
 *
 * TODO - getPlays
 *
 * TODO - getPlaysForPerformance
 *
 * TODO - getPlaysForReview
 *
 * TODO - retrievePlayLinesForPlay [Play]
 *
 * TODO - retrieveAssignedUsers [playLine, playId]
 *
 * TODO - retrievePlayWithId [playID]
 *
 * TODO - getMyCastMatchesForUserId [userIdString, playId]
 *
 * TODO - getMyCastMatchesForRoleNames [roleNames, playId]
 *
 * TODO - retrievePlayLineForId [lineId, playId]
 *
 * TODO - retrieveTextLines [playLine]
 *
 * TODO - retrieveComments [playLine]
 *
 * TODO - retrieveSongFiles [playLine]
 *
 * TODO - insertPlay [play, wasPlayAlreadyOrderedForPreview]
 *
 * TODO - updatePlayLine [playLine, playId]
 *
 * TODO - getPlayLineTypeForLineId [playLineIdString]
 *
 * TODO - updateTextLine [textLine, playLineId, index]
 *
 * TODO - removeTextLinesForPlayLineId [playLineId]
 *
 * TODO - getPlayIdFromDBForOrderId [orderId]
 *
 * TODO - insertPlayLine [playLine, playId]
 *
 * TODO - insertPlayLine [playLine, playId]
 *
 * TODO - removeAssigedUsersForPlayLine [playLine, playId]
 *
 * TODO - insertAssignedUser [assignedUser, playLine, playId]
 *
 * TODO - insertTextLine [textLine, playLineId]
 *
 * TODO = removeAllCommentsForPlayLineId [playLineId]
 *
 * TODO - insertComment [comment, playLineId]
 *
 * TODO - insertSongFile [songFile, playLineId]
 *
 * TODO - updatePlayInfo [play]
 *
 * TOD - hasPlayWithPlayOrderIdText [playOrderIdText]
 *
 *
 */



public class DatabaseWrapper extends SQLiteOpenHelper{

    private static String TAG = "DATABASE_WRAPPER";
    private  String DB_PATH = "/data/data/wm.com.dt/databases/";
    private static String DB_NAME = "DanTeater.db";
    private SQLiteDatabase myDataBase = null;
    private Context myContext;

    public DatabaseWrapper(Context context) {
        super(context, DB_NAME,null,1);
        this.myContext = context;
    }


    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        if(dbExist){
            Log.v("log_tag", "database does exist");
        }else{
            Log.v("log_tag", "database does not exist");
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Error copying database");
            }
        }
    }

    private void copyDataBase() throws IOException{

        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;

        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private boolean checkDataBase(){

        File folder = new File(DB_PATH);
        if(!folder.exists()){
            folder.mkdir();
        }
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    public boolean openDataBase() throws SQLException
    {
        String mPath = DB_PATH + DB_NAME;
        //Log.v("mPath", mPath);
        myDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        return myDataBase != null;

    }
    @Override
    public synchronized void close()
    {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
   // All the methods for fetching specific data from local Database


    public boolean hasPlayWithPlayOrderIdText(String playOrderIdText) {
        // looping through all rows and adding to list
 /*       if (cursor.moveToFirst()) {
            do {

            } while (cursor.moveToNext());
        }
*/
        System.out.println("play orderidtext : " + playOrderIdText);
        boolean hasPlay = false;
        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM plays WHERE play_order_id_text_ ="+"\""+playOrderIdText.toString().trim()+"\"";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        if(cursor.getCount()>0){
            hasPlay = true;
        }else{
            hasPlay = false;
        }

        cursor.close();
        myDataBase.close();

        return hasPlay;
    }


    public void insertPlay(Play play,boolean wasPlayAlreadyOrderedForPreview){

        ContentValues cvPlays = new ContentValues();
        cvPlays.put("play_id_text_",play.PlayId);
        cvPlays.put("play_order_id_text_",play.OrderId.trim());
        cvPlays.put("title_",play.Title);
        cvPlays.put("subtitle_short_",play.SubtitleShort);
        cvPlays.put("subtitle_long_",play.SubtitleLong);
        cvPlays.put("author_",play.Author);
        cvPlays.put("actors_",play.Actors);
        cvPlays.put("age_",play.Age);
        cvPlays.put("music_",play.Music);
        cvPlays.put("song_count_",play.MusicCount);
        cvPlays.put("duration_",play.Duration);
        cvPlays.put("synopsis_",play.Synopsis);
        cvPlays.put("version_",play.PlayVersion);
        cvPlays.put("order_type_",play.OrderType);

        myDataBase = this.getWritableDatabase();
        myDataBase.insert("plays",null,cvPlays);
        myDataBase.close();

        // Add PlayLines

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM plays WHERE play_order_id_text_ ="+"\""+play.OrderId.toString().trim()+"\"";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        Log.e("Play Id in Database : ",""+cursor.getInt(cursor.getColumnIndex("id_")));
        int playID = cursor.getInt(cursor.getColumnIndex("id_"));

        for(PlayLines line : play.playLinesList){
            insertPlayLine(line,playID);
        }

    }

    public void insertPlayLine(PlayLines play_line,int playid){

        String castMatchesString = new String();
        for(String castMatch : play_line.castMatchesList){
                castMatchesString.concat(castMatch);
        }
        play_line.castMatchesString = castMatchesString;

        //

        ContentValues cvPlays = new ContentValues();
        cvPlays.put("line_number_",play_line.LineCount);
        cvPlays.put("playline_id_text_",play_line.LineID);
        cvPlays.put("play_id_",playid);
        cvPlays.put("role_name_",play_line.RoleName);
        cvPlays.put("show_role_highlight_",play_line.showRoleHighlight);
        cvPlays.put("allow_comments_",play_line.allowComments);
        cvPlays.put("allow_recording_",play_line.allowRecording);
        cvPlays.put("show_into_words_",play_line.showIntoWords);
        cvPlays.put("show_line_number_",play_line.showLineNumber);
        cvPlays.put("main_line_type_",play_line.MainLineType);
        cvPlays.put("role_line_count_",play_line.RoleLinesCount);
        cvPlays.put("is_last_song_line_",play_line.isLastSongLine);
        cvPlays.put("cast_matches_",play_line.castMatchesString);

        myDataBase = this.getWritableDatabase();
        myDataBase.insert("playlines",null,cvPlays);
        myDataBase.close();

        //
        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT id_ FROM playlines WHERE playline_id_text_ ="+"\""+play_line.LineID.toString().trim()+"\"";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int playLineId = cursor.getInt(cursor.getColumnIndex("id_"));

        // TODO remaining all the things

        if(play_line.playLineType() == PlayLines.PlayLType.PlayLineTypeRole){



        }


    }



}
