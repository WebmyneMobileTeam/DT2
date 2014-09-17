package wm.com.danteater.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
 * TODO - hasPlayWithPlayOrderIdText [playOrderIdText]
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


    public boolean hasPlayWithPlayOrderIdText(String playOrderIdText){

        boolean hasPlay = false;

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT COUNT(id_) AS cnt FROM plays WHERE play_order_id_text_ ="+playOrderIdText;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        try{
            System.out.println("Row count is : "+cursor.getCount());
        }catch(Exception e){}

        return hasPlay;
    }



}
