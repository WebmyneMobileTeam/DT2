package wm.com.danteater.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mvnordic.mviddeviceconnector.L;

import org.w3c.dom.Comment;

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
 *
 * TODO - getPlaysForPerformance
 *
 * TODO - getPlaysForReview
 *
 *
 * TOD - retrievePlayLinesForPlay [Play]
 *
 * TOD - retrieveAssignedUsers [playLine, playId]
 *
 * TOD - retrievePlayWithId [playID]
 *
 * TOD - getMyCastMatchesForUserId [userIdString, playId]
 *
 * TODO - getMyCastMatchesForRoleNames [roleNames, playId]
 *
 * TODO - retrievePlayLineForId [lineId, playId]
 *
 * TOD - retrieveTextLines [playLine]
 *
 * TOD - retrieveComments [playLine]
 *
 * TOD - retrieveSongFiles [playLine]
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
 * TOD - getPlayIdFromDBForOrderId [orderId]
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
 * TOD - updatePlayInfo [play]
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
    private StateManager state = StateManager.getInstance();

    public DatabaseWrapper(Context context) {
        super(context, DB_NAME,null,1);
        this.myContext = context;
    }


    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();
        if(dbExist){
//            Log.v("log_tag", "database does exist");
        }else{
//            Log.v("log_tag", "database does not exist");
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
//        Log.e("Play Id in Database : ",""+cursor.getInt(cursor.getColumnIndex("id_")));

        int playID = cursor.getInt(cursor.getColumnIndex("id_"));

        SharedPreferences preferences = myContext.getSharedPreferences("Plays", myContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("playid",playID);
        editor.commit();
        state.playID = playID;


        for(PlayLines line : play.playLinesList){
            insertPlayLine(line,playID);
        }

    }

    public void insertPlayLine(PlayLines play_line,int playid){


        StringBuffer castMatchesString = new StringBuffer("");
//        Log.i("Cast Main list ",""+play_line.castMatchesList);
        for(int i=0;i<play_line.castMatchesList.size();i++){

            String s = play_line.castMatchesList.get(i).toString();
//            Log.i("Cast child  ",s);
            if(i== play_line.castMatchesList.size()-1){
                castMatchesString.append(s.toString());
            }else{
                castMatchesString.append(s.toString()+";");
            }
        }

        play_line.castMatchesString = castMatchesString.toString();
//        Log.i("Cast matches string  ",castMatchesString.toString());
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

        int playLineId = -1;

        if(cursor.moveToFirst()){
            do {
                playLineId = cursor.getInt(cursor.getColumnIndex("id_"));
            }while (cursor.moveToNext());
        }


        if(playLineId == -1){
            return;
        }

        // assigned users
        if(play_line.playLineType() == PlayLines.PlayLType.PlayLineTypeRole){

            removeAssignedUsersForPlayLine(play_line,playid);

//            Log.e("No of assigned user",""+play_line.assignedUsersList.size());
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


//        Log.i("In insert comments ","insert comments for the ");

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
        String roleName = null;
//        Log.e("PlayID while assign user inserted ",""+playid);
        if(play_line.RoleName == null || play_line.RoleName.equalsIgnoreCase("")){

            myDataBase = this.getWritableDatabase();
            String selectQuery = "SELECT role_name_ FROM playlines WHERE play_id_ ="+playid+" AND playline_id_text_ ="+"\""+play_line.LineID.toString().trim()+"\"";
//            Log.e("Query for inserting assigned user",selectQuery);
            Cursor cursor = myDataBase.rawQuery(selectQuery, null);
            cursor.moveToFirst();

            if (cursor.moveToFirst()) {
                do {
                    roleName = new String();
                    roleName = cursor.getString(cursor.getColumnIndex("role_name_"));
//                    Log.e("RoleNameAfterFetching",roleName);
                    play_line.RoleName = roleName;
                } while (cursor.moveToNext());
            }
            cursor.close();



        }

        ContentValues cvInsertAssignedUsers = new ContentValues();
        cvInsertAssignedUsers.put("play_id_",playid);
        cvInsertAssignedUsers.put("role_name_",play_line.RoleName);
        cvInsertAssignedUsers.put("assigned_user_id_",assignedUsers.AssignedUserId);
        cvInsertAssignedUsers.put("assigned_user_name_",assignedUsers.AssignedUserName);

        myDataBase = this.getWritableDatabase();
        myDataBase.insert("assigned_users",null,cvInsertAssignedUsers);
        myDataBase.close();

    }

    public void removeAssignedUsersForPlayLine(PlayLines play_line,int playID) {

//        Log.e("---------------------------------------------","In delete");
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
//            Log.e("---------------------------------------------","Before delete");
            // delete query
            myDataBase = this.getWritableDatabase();
            myDataBase.delete("assigned_users","play_id_" + " = ? AND " + "role_name_" + " = ?",new String[]{""+playID,roleName});
            myDataBase.close();
//            Log.e("---------------------------------------------","After delete");

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
//                Log.e("playLineID for update ",""+playLineID);
            } while (cursor.moveToNext());
        }

        if(playLineID == -1){

            return;
        }

        int index = 0;

        try{
            for(TextLines textLine : play_line.textLinesList){
                updateTextLine(textLine,playLineID,index);
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        if(play_line.commentsList != null && play_line.commentsList.size()>0){
            removeAllCommentsForPlayLineId(playLineID);
        }

        if(play_line.commentsList != null && play_line.commentsList.size()>0){
            for(Comments comment : play_line.commentsList){
                insertComment(comment,playLineID);
            }
        }

        if(play_line.songFilesList != null && play_line.songFilesList.size()>0){
            removeAllSongsForPlayLineId(playLineID);
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

    private void removeAllSongsForPlayLineId(int playLineID) {

        myDataBase = this.getWritableDatabase();
        myDataBase.delete("songfiles","playline_id_" + " = ?",new String[]{""+playLineID});
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
        myDataBase.delete("comments","playline_id_" + " = ?",new String[]{""+playLineID});
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
                    myDataBase.update("textlines",cvTextLines,"id_ " + " = ?",new String[]{""+textLineID});
                    myDataBase.close();
                }
                count++;


            } while (cursor.moveToNext());
        }

    }


    public int getPlayIdFromDBForOrderId(String orderId) {

//        Log.e("Database ","getPlayIdFromDBForOrderId");
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
        String rolenameQuery = "SELECT role_name_ FROM assigned_users WHERE play_id_ = "+playID+" AND assigned_user_id_ ="+"\""+userID+"\"";
//       Log.e("Role name query database ",rolenameQuery);
        Cursor cursor = myDataBase.rawQuery(rolenameQuery, null);
        cursor.moveToFirst();
        String roleNameForUserId = null;

        ArrayList<String> castMatchesStringsArray = new ArrayList<String>();

        if (cursor.moveToFirst()) {
            do {

                roleNameForUserId = cursor.getString(cursor.getColumnIndex("role_name_"));
                if(roleNameForUserId != null){
                    marrMyCastMatches.add(roleNameForUserId);
                }

                myDataBase = this.getWritableDatabase();


                       // "role_name_" + " = ? AND " + "main_line_type_" + " = ?"

                String wher = "role_name_" + " = ? AND " + "main_line_type_" + " = ?";
                String[] FIELDS = { "cast_matches_" };

              //  myDataBase.qu
              //  String castMatchesQuery = "SELECT cast_matches_ FROM playlines WHERE role_name_ ="+"\""+roleNameForUserId+"\""+"AND main_line_type_ ="+"\""+mlt+"\"";
              //  System.out.println("castMatces String query : "+castMatchesQuery);
                //Cursor castMatchesCursor = myDataBase.rawQuery(castMatchesQuery, null);
                Cursor castMatchesCursor =   myDataBase.query("playlines", FIELDS, wher, new String[]{roleNameForUserId,mlt}, null, null, null);
                String castMatchesString = null;
                castMatchesCursor.moveToFirst();

                if (castMatchesCursor.moveToFirst()) {
                    do {
                        castMatchesString = castMatchesCursor.getString(castMatchesCursor.getColumnIndex("cast_matches_"));
                        castMatchesStringsArray.add(castMatchesString);
//                        Log.e("!!!!!castMatchesString",castMatchesString);
                    } while (castMatchesCursor.moveToNext());
                }



            } while (cursor.moveToNext());

//            Log.e("MyCastMatch in database ",""+castMatchesStringsArray);

            for(String s : castMatchesStringsArray){

                String[] arrMAtchesForRole = s.split(";");

                if(arrMAtchesForRole.length>0){


                    for(int z=0;z<arrMAtchesForRole.length;z++){
                        marrMyCastMatches.add(arrMAtchesForRole[z]);
                    }


                  //  marrMyCastMatches.addAll(new ArrayList(Arrays.asList(arrMAtchesForRole)));
                }

            }
        }


        return  marrMyCastMatches;
    }

    public Play retrievePlayWithId(int playID){

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM plays WHERE id_ = "+playID;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
//        Log.e("Count for play is : ",""+cursor.getCount());

        Play ply = new Play();

        if(cursor.moveToFirst()){
            do {


                ply.pID = cursor.getInt(cursor.getColumnIndex("id_"));
                ply.PlayId = cursor.getString(cursor.getColumnIndex("play_id_text_"));
                ply.OrderId = cursor.getString(cursor.getColumnIndex("play_order_id_text_"));
                ply.Title = cursor.getString(cursor.getColumnIndex("title_"));
                ply.SubtitleShort = cursor.getString(cursor.getColumnIndex("subtitle_short_"));
                ply.SubtitleLong = cursor.getString(cursor.getColumnIndex("subtitle_long_"));
                ply.Author = cursor.getString(cursor.getColumnIndex("author_"));
                ply.Actors = cursor.getString(cursor.getColumnIndex("actors_"));
                ply.Age = cursor.getString(cursor.getColumnIndex("age_"));
                ply.Music = cursor.getString(cursor.getColumnIndex("music_"));
                ply.MusicCount = ""+cursor.getInt(cursor.getColumnIndex("song_count_"));
                ply.Duration = ""+cursor.getInt(cursor.getColumnIndex("duration_"));
                ply.Synopsis = cursor.getString(cursor.getColumnIndex("synopsis_"));
                ply.PlayVersion = ""+cursor.getInt(cursor.getColumnIndex("version_"));
                ply.OrderType = cursor.getString(cursor.getColumnIndex("order_type_"));


            }while (cursor.moveToNext());

            retrievePlayLinesForPlay(ply);

        }

        cursor.close();


        return ply;

    }

    public void retrievePlayLinesForPlay(Play play){

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM playlines WHERE play_id_ ="+play.pID;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(play.playLinesList == null){
            play.playLinesList = new ArrayList<PlayLines>();
        }

        if(cursor.moveToFirst()){
            do {

                PlayLines playlineModelObject = new PlayLines();


                playlineModelObject.lID = cursor.getInt(cursor.getColumnIndex("id_"));
                playlineModelObject.LineCount = cursor.getInt(cursor.getColumnIndex("line_number_"));
                playlineModelObject.LineID = cursor.getString(cursor.getColumnIndex("playline_id_text_"));
                playlineModelObject.MainLineType = cursor.getString(cursor.getColumnIndex("main_line_type_"));
                playlineModelObject.RoleName = cursor.getString(cursor.getColumnIndex("role_name_"));

                playlineModelObject.castMatchesString = cursor.getString(cursor.getColumnIndex("cast_matches_"));

                String[] ar = playlineModelObject.castMatchesString.split(";");
                playlineModelObject.castMatchesList = new ArrayList<String>(Arrays.asList(ar));


                if(cursor.getInt(cursor.getColumnIndex("show_role_highlight_")) == 0){
                    playlineModelObject.showRoleHighlight = false;
                }else{
                    playlineModelObject.showRoleHighlight = true;
                }


                if(cursor.getInt(cursor.getColumnIndex("allow_comments_")) == 0){
                    playlineModelObject.allowComments = false;
                }else{
                    playlineModelObject.allowComments = true;
                }


                if(cursor.getInt(cursor.getColumnIndex("allow_recording_")) == 0){
                    playlineModelObject.allowRecording = false;
                }else{
                    playlineModelObject.allowRecording = true;
                }


                if(cursor.getInt(cursor.getColumnIndex("show_into_words_")) == 0){
                    playlineModelObject.showIntoWords = false;
                }else{
                    playlineModelObject.showIntoWords = true;
                }

                if(cursor.getInt(cursor.getColumnIndex("show_line_number_")) == 0){
                    playlineModelObject.showLineNumber = false;
                }else{
                    playlineModelObject.showLineNumber = true;
                }

                playlineModelObject.RoleLinesCount = cursor.getString(cursor.getColumnIndex("role_line_count_"));

                if(cursor.getInt(cursor.getColumnIndex("show_line_number_")) == 0){
                    playlineModelObject.showLineNumber = false;
                }else{
                    playlineModelObject.showLineNumber = true;
                }

                if(cursor.getInt(cursor.getColumnIndex("is_last_song_line_")) == 0){
                    playlineModelObject.isLastSongLine = false;
                }else{
                    playlineModelObject.isLastSongLine = true;
                }




                // 1
                retrieveAssignedUsers(playlineModelObject,play.pID);

                //2
                retrieveTextLines(playlineModelObject);

                //3
                retrieveComments(playlineModelObject);

                //4
                retrieveSongFiles(playlineModelObject);

                // add play to playline
                play.playLinesList.add(playlineModelObject);

            }while(cursor.moveToNext());
        }






    }

    public void retrieveSongFiles(PlayLines playlineModelObject) {



        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM songfiles WHERE playline_id_ ="+playlineModelObject.lID;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(playlineModelObject.songFilesList == null){
            playlineModelObject.songFilesList = new ArrayList<SongFiles>();
        }

        if(cursor.moveToFirst()){
            do {
/*
                SongFile *songfileModelObject = [SongFile new];

                songfileModelObject.songFileId = [results intForColumn:@"id_"];
                songfileModelObject.title = [results stringForColumn:@"title_"];
                songfileModelObject.fileUrl = [results stringForColumn:@"file_url_"];
                songfileModelObject.fileType = [results stringForColumn:@"file_type_"];
                songfileModelObject.fileDescription = [results stringForColumn:@"file_description_"];
                songfileModelObject.isAvailableForPupils = [results boolForColumn:@"available_for_students_"];

                [playLine.songFiles addObject:songfileModelObject];*/

                SongFiles songfileModelObject = new SongFiles();
                songfileModelObject.Id = ""+cursor.getInt(cursor.getColumnIndex("id_"));

                songfileModelObject.SongTitle = cursor.getString(cursor.getColumnIndex("title_"));

                songfileModelObject.SongMp3Url = cursor.getString(cursor.getColumnIndex("file_url_"));
                songfileModelObject.FileType = cursor.getString(cursor.getColumnIndex("file_type_"));
                songfileModelObject.FileDescription = cursor.getString(cursor.getColumnIndex("file_description_"));

                if(cursor.getInt(cursor.getColumnIndex("available_for_students_")) == 0){
                    songfileModelObject.FileAvailableForStudents = false;
                }else{
                    songfileModelObject.FileAvailableForStudents = true;
                }
                playlineModelObject.songFilesList.add(songfileModelObject);
            }while (cursor.moveToNext());
        }


        cursor.close();


    }

    public void retrieveComments(PlayLines playlineModelObject) {

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM comments WHERE playline_id_ ="+playlineModelObject.lID;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(playlineModelObject.commentsList == null){
            playlineModelObject.commentsList = new ArrayList<Comments>();
        }

        if(cursor.moveToFirst()){
            do {

/*                Comment *commentModelObject = [Comment new];
                commentModelObject.commentId = [results intForColumn:@"id_"];
                commentModelObject.username = [results stringForColumn:@"username_"];
                commentModelObject.text = [results stringForColumn:@"text_"];
                commentModelObject.isPrivate = [results boolForColumn:@"private_"];

                [playLine.comments addObject:commentModelObject];*/

                Comments commentModelObject = new Comments();
                commentModelObject.comment_id = ""+cursor.getInt(cursor.getColumnIndex("id_"));
                commentModelObject.userName = cursor.getString(cursor.getColumnIndex("username_"));
                commentModelObject.commentText = cursor.getString(cursor.getColumnIndex("text_"));
                if(cursor.getInt(cursor.getColumnIndex("private_")) == 0){
                    commentModelObject.isPrivate = false;
                }else{
                    commentModelObject.isPrivate = true;
                }

                playlineModelObject.commentsList.add(commentModelObject);



            }while (cursor.moveToNext());
        }

        cursor.close();


    }

    public void retrieveTextLines(PlayLines playlineModelObject) {

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM textlines WHERE playline_id_ ="+playlineModelObject.lID;
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(playlineModelObject.textLinesList == null){
            playlineModelObject.textLinesList = new ArrayList<TextLines>();
        }

        if(cursor.moveToFirst()){
            do {

                TextLines textlineModelObject = new TextLines();
                textlineModelObject.textLineId = cursor.getInt(cursor.getColumnIndex("id_"));
                textlineModelObject.LineType = cursor.getString(cursor.getColumnIndex("type_"));
                textlineModelObject.LineText = cursor.getString(cursor.getColumnIndex("original_text_"));
                textlineModelObject.alteredLineText = cursor.getString(cursor.getColumnIndex("altered_text_"));
                playlineModelObject.textLinesList.add(textlineModelObject);

            }while (cursor.moveToNext());
        }

        cursor.close();



    }

    public void retrieveAssignedUsers(PlayLines playlineModelObject, int pID) {

        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM assigned_users WHERE play_id_ ="+pID+" AND role_name_ ="+"\""+playlineModelObject.RoleName+"\"";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);
        cursor.moveToFirst();

        if(playlineModelObject.assignedUsersList == null){
            playlineModelObject.assignedUsersList = new ArrayList<AssignedUsers>();
        }

        if(cursor.moveToFirst()){
            do {
                AssignedUsers assignedUserModelObject = new AssignedUsers();
                assignedUserModelObject.uID = cursor.getInt(cursor.getColumnIndex("id_"));
                assignedUserModelObject.AssignedUserId = cursor.getString(cursor.getColumnIndex("assigned_user_id_"));
                assignedUserModelObject.AssignedUserName = cursor.getString(cursor.getColumnIndex("assigned_user_name_"));

                playlineModelObject.assignedUsersList.add(assignedUserModelObject);

            }while (cursor.moveToNext());
        }





    }

    public ArrayList<String> getMyCastMatchesForRoleNames(ArrayList<String> rolenames,int playid){
        myDataBase = this.getWritableDatabase();
        String mlt = "Rolle";
        ArrayList<String> marrMyCastMatches = new ArrayList<String>();

        ArrayList<String> castMatchesStringArray = new ArrayList<String>();

        for(String roleName : rolenames){

            String wher = "role_name_" + " = ? AND " + "main_line_type_" + " = ?";
            String[] FIELDS = { "cast_matches_" };
            Cursor castMatchesCursor =   myDataBase.query("playlines", FIELDS, wher, new String[]{roleName,mlt}, null, null, null);
            String castMatchesString = null;
            castMatchesCursor.moveToFirst();

            if (castMatchesCursor.moveToFirst()) {
                do {
                    castMatchesString = castMatchesCursor.getString(castMatchesCursor.getColumnIndex("cast_matches_"));
                    castMatchesStringArray.add(castMatchesString);
//                    Log.e("!!!!!castMatchesString",castMatchesString);
                } while (castMatchesCursor.moveToNext());
            }

        }


        for(String s : castMatchesStringArray){

            String[] arrMAtchesForRole = s.split(";");

            if(arrMAtchesForRole.length>0){

                for(int z=0;z<arrMAtchesForRole.length;z++){
                    marrMyCastMatches.add(arrMAtchesForRole[z]);
                }

            }

        }

        return  marrMyCastMatches;

    }


    public boolean IsDatabseEmpty() {
        boolean isDatabaseEmpty;
        myDataBase = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM plays";
        Cursor cursor = myDataBase.rawQuery(selectQuery, null);

        if(cursor.getCount()>0){
            isDatabaseEmpty=false;
        }else{
            isDatabaseEmpty=true;
        }
        cursor.close();
        myDataBase.close();

        return  isDatabaseEmpty;
    }


}
