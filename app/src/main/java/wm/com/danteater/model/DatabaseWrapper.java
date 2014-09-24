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
import java.util.ArrayList;
import java.util.Arrays;

import wm.com.danteater.Play.AssignedUsers;
import wm.com.danteater.Play.Comments;
import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.SongFiles;
import wm.com.danteater.Play.TextLines;

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
 * TOD - insertPlay [play, wasPlayAlreadyOrderedForPreview]
 *
 * TOD - updatePlayLine [playLine, playId]
 *
 * TOD - getPlayLineTypeForLineId [playLineIdString]
 *
 * TOD - updateTextLine [textLine, playLineId, index]
 *
 * TODO - removeTextLinesForPlayLineId [playLineId]
 *
 * TODO - getPlayIdFromDBForOrderId [orderId]
 *
 * TOD - insertPlayLine [playLine, playId]
 *
 * TOD - insertPlayLine [playLine, playId]
 *
 * TOD - removeAssigedUsersForPlayLine [playLine, playId]
 *
 * TOD - insertAssignedUser [assignedUser, playLine, playId]
 *
 * TOD - insertTextLine [textLine, playLineId]
 *
 * TOD = removeAllCommentsForPlayLineId [playLineId]
 *
 * TOD - insertComment [comment, playLineId]
 *
 * TOD - insertSongFile [songFile, playLineId]
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


        // assigned users
        if(play_line.playLineType() == PlayLines.PlayLType.PlayLineTypeRole){
            removeAssignedUsersForPlayLine(play_line,playid);
            for(AssignedUsers assignedUser : play_line.assignedUsersList){
                insertAssignedUser(assignedUser,play_line,playid);
            }
        }

        // textlines
        for(TextLines textLine : play_line.textLinesList){
            insertTextLine(textLine,playLineId);
        }

        //comments
        for(Comments comment : play_line.commentsList){
            insertComment(comment,playLineId);
        }

        //song file
        for(SongFiles songFile : play_line.songFilesList){
            insertSongFile(songFile,playLineId);
        }

    }

    private void insertSongFile(SongFiles songFile, int playLineId) {

        ContentValues cvLineText = new ContentValues();
        cvLineText.put("playline_id_",playLineId);
        cvLineText.put("title_",songFile.SongTitle);
        cvLineText.put("file_url_",songFile.SongMp3Url);
        cvLineText.put("file_type_",songFile.FileType);
        cvLineText.put("available_for_students_",songFile.FileAvailableForStudents);
        cvLineText.put("file_description_",songFile.FileDescription);

        myDataBase = this.getWritableDatabase();
        myDataBase.insert("songFiles",null,cvLineText);
        myDataBase.close();


    }

    private void insertComment(Comments comment, int playLineId) {

        ContentValues cvLineText = new ContentValues();
        cvLineText.put("playline_id_",playLineId);
        cvLineText.put("username_",comment.userName);
        cvLineText.put("text_",comment.commentText);
        cvLineText.put("private_",comment.isPrivate);

        myDataBase = this.getWritableDatabase();
        myDataBase.insert("comments",null,cvLineText);
        myDataBase.close();

    }

    private void insertTextLine(TextLines textLine, int playLineId) {

        ContentValues cvLineText = new ContentValues();
        cvLineText.put("playline_id_",playLineId);
        cvLineText.put("type_",textLine.LineType);
        cvLineText.put("original_text_",textLine.LineText);
        cvLineText.put("altered_text_",textLine.alteredLineText);

        myDataBase = this.getWritableDatabase();
        myDataBase.insert("textlines",null,cvLineText);
        myDataBase.close();

    }

    public void insertAssignedUser(AssignedUsers assignedUsers,PlayLines play_line, int playid) {

        if(play_line.RoleName == null || play_line.RoleName.equalsIgnoreCase("")){

            myDataBase = this.getWritableDatabase();
            String selectQuery = "SELECT role_name_ FROM playlines WHERE play_id_ ="+playid+" AND playline_id_text_ ="+"\""+play_line.LineID.toString().trim()+"\"";
            Cursor cursor = myDataBase.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            String roleName = null;
            if (cursor.moveToFirst()) {
                do {
                    roleName = new String();
                    roleName = cursor.getString(cursor.getColumnIndex("role_name_"));
                    play_line.RoleName = roleName;
                } while (cursor.moveToNext());
            }
            cursor.close();

            ContentValues cvInsertAssignedUsers = new ContentValues();
            cvInsertAssignedUsers.put("play_id_",playid);
            cvInsertAssignedUsers.put("role_name_",roleName);
            cvInsertAssignedUsers.put("assigned_user_id_",assignedUsers.AssignedUserId);
            cvInsertAssignedUsers.put("assigned_user_name_",assignedUsers.AssignedUserName);

            myDataBase = this.getWritableDatabase();
            myDataBase.insert("assigned_users",null,cvInsertAssignedUsers);
            myDataBase.close();

        }

    }

    public void removeAssignedUsersForPlayLine(PlayLines play_line,int playID) {

        if(play_line.RoleName == null || play_line.RoleName.equalsIgnoreCase("")){

            myDataBase = this.getWritableDatabase();
            String selectQuery = "SELECT role_name_ FROM playlines WHERE playline_id_text_ ="+"\""+play_line.LineID.toString().trim()+"\"";
            Cursor cursor = myDataBase.rawQuery(selectQuery, null);
            cursor.moveToFirst();

            String roleName = null;
            if (cursor.moveToFirst()) {
                do {
                      roleName = new String();
                      roleName = cursor.getString(cursor.getColumnIndex("role_name_"));
                      play_line.RoleName = roleName;
                } while (cursor.moveToNext());
            }
            cursor.close();

            // delete query
            myDataBase = this.getWritableDatabase();
            myDataBase.delete("assigned_users","play_id_" + " = ? AND " + "role_name_" + " = ?",new String[]{""+playID,roleName});
            myDataBase.close();

        }
    }

    public void updatePlayLine(PlayLines play_line,int playID){

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT id_ FROM playlines WHERE playline_id_text_ ="+"\""+play_line.LineID.toString().trim()+"\"";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        int playLineID = -1;
        if (cursor.moveToFirst()) {
            do {
                playLineID = cursor.getInt(cursor.getColumnIndex("id_"));
            } while (cursor.moveToNext());
        }

        if(playLineID == -1){
            myDataBase.close();
            return;
        }

        int index = 0;
        for(TextLines textLine : play_line.textLinesList){
            updateTextLine(textLine,playLineID,index);
        }

        if(play_line.commentsList != null && play_line.commentsList.size()>0){
            removeAllCommentsForPlayLineId(playLineID);
        }

        for(Comments comment : play_line.commentsList){
            insertComment(comment,playLineID);
        }

        //song file
        for(SongFiles songFile : play_line.songFilesList){
            insertSongFile(songFile,playLineID);
        }

        if(play_line.MainLineType == null){
            play_line.MainLineType = getPlayLineTypeForLineId(play_line.LineID);
        }

        if(play_line.playLineType() == PlayLines.PlayLType.PlayLineTypeRole){
            removeAssignedUsersForPlayLine(play_line,playID);
            for(AssignedUsers assignedUser : play_line.assignedUsersList){
                insertAssignedUser(assignedUser,play_line,playID);
            }
        }

        myDataBase.close();
    }



    public String getPlayLineTypeForLineId(String lineID) {

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT main_line_type_ FROM playlines WHERE playline_id_text_ ="+"\""+lineID+"\"";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        String mainLineType = null;
        if (cursor.moveToFirst()) {
            do {
               mainLineType = cursor.getString(cursor.getColumnIndex("main_line_type_"));
            } while (cursor.moveToNext());
        }

        return mainLineType;
    }

    public void removeAllCommentsForPlayLineId(int playLineID) {

        myDataBase = this.getWritableDatabase();
        myDataBase.delete("comments","WHERE playline_id_" + " = ?",new String[]{""+playLineID});
        myDataBase.close();

    }



    public void updateTextLine(TextLines textLine, int playLineID, int index) {
        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT id_ FROM textlines WHERE playline_id_ ="+playLineID;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        int textLineID = -1;
        int count = 0;

        if (cursor.moveToFirst()) {
            do {

                textLineID = cursor.getInt(cursor.getColumnIndex("id_"));

                if(index == count){

                    ContentValues cvTextLines = new ContentValues();
                    cvTextLines.put("altered_text_",textLine.alteredLineText);

                    myDataBase = this.getWritableDatabase();
                    myDataBase.update("textlines",cvTextLines,"WHERE id_ " + " = ?",new String[]{""+textLineID});
                    myDataBase.close();
                }
                count++;


            } while (cursor.moveToNext());
        }

    }


    public int getPlayIdFromDBForOrderId(String orderId) {

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT id_ FROM plays WHERE play_order_id_text_ ="+"\""+orderId+"\"";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        int playID = -1;
        if (cursor.moveToFirst()) {
            do {
             playID = cursor.getInt(cursor.getColumnIndex("id_"));
            } while (cursor.moveToNext());
        }
        myDataBase.close();

        return playID;

    }

    public void updatePlayInfo(Play play) {

        ContentValues cvTextLines = new ContentValues();
        cvTextLines.put("order_type_",play.OrderType);

        myDataBase = this.getWritableDatabase();
        myDataBase.update("plays",cvTextLines," play_order_id_text_ " + " = ?",new String[]{""+play.OrderId});
        myDataBase.close();
    }

    public ArrayList getMyCastMatchesForUserId(String userID,int playID){

        String mlt = "Rolle";

        ArrayList marrMyCastMatches = new ArrayList();
        myDataBase = this.getWritableDatabase();
        String rolenameQuery = "SELECT role_name_ FROM assigned_users WHERE play_id_ ="+playID+" AND assigned_user_id_ ="+"\""+userID+"\"";
        Cursor cursor = myDataBase.rawQuery(rolenameQuery, null);

        String roleNameForUserId = null;
        ArrayList<String> castMatchesStringsArray = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {

                roleNameForUserId = cursor.getString(cursor.getColumnIndex("role_name_"));
                if(roleNameForUserId != null){
                    marrMyCastMatches.add(roleNameForUserId);
                }

                String castMatchesQuery = "SELECT cast_matches_ FROM playlines WHERE role_name_ =\"+\"\\\"\"+roleNameForUserId+\"\\\"\" AND main_line_type_ ="+"\""+mlt+"\"";
                System.out.println("castMatces String query : "+castMatchesQuery);
                Cursor castMatchesCursor = myDataBase.rawQuery(castMatchesQuery, null);
                String castMatchesString = null;
                if (castMatchesCursor.moveToFirst()) {
                    do {
                        castMatchesString = castMatchesCursor.getString(cursor.getColumnIndex("cast_matches_"));
                        castMatchesStringsArray.add(castMatchesString);
                    } while (castMatchesCursor.moveToNext());
                }

            } while (cursor.moveToNext());

            for(String s : castMatchesStringsArray){

                String[] arrMAtchesForRole = s.split(";");
                if(arrMAtchesForRole.length>0){
                    marrMyCastMatches.addAll(new ArrayList(Arrays.asList(arrMAtchesForRole)));
                }

            }
        }


        return  marrMyCastMatches;
    }


}
