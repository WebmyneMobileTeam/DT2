package wm.com.danteater.tab_music;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.SongFiles;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.PinnedHeaderListView;
import wm.com.danteater.customviews.SectionedBaseAdapter;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.tab_read.ViewHolder;

public class MusicFragment extends Fragment {

    private LinearLayout noMusicView;
    private TextView txtNoMusic;
    private WMTextView txtDownloadAllMusic;
    private ImageView imgDownloadAllMusic;
    LinearLayout listHeaderView;
    private Play selectedPlay;
    private ArrayList<PlayLines> playLineses;
    public static boolean isAnySongPlayed=false;

    MusicSectionedAdapter musicSectionedAdapter;
    private ArrayList<String> marrSectionTitles=new ArrayList<String>();
    private ArrayList<ArrayList<SongFiles>> marrSectionsWithContent=new ArrayList<ArrayList<SongFiles>>();
    private ArrayList<SongFiles> marrSongFilesMP3=new ArrayList<SongFiles>();
    public PinnedHeaderListView listMusic;
    private HUD dialog;
    public static int MP3_FILE = 0;
    public static int PDF_FILE = 1;


    public static MediaPlayer mediaPlayer = null;
     public static int CURRENT_PLAYING_POSITION = -1;
    public static int CURRENT_PLAYING_SECTION = -1;


    static MusicFragment fragment;
    public static MusicFragment newInstance(String param1, String param2) {
       fragment= new MusicFragment();
        return fragment;
    }

    public MusicFragment() {
        // Required empty public constructor
    }
    public static ArrayList<String> playingMusic = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
        setHasOptionsMenu(true);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play", Play.class);
        playLineses=selectedPlay.playLinesList;
//        Log.e("Play Lines List:",playLineses+"");
        for(PlayLines playLines: playLineses){
            if(playLines.MainLineType.equalsIgnoreCase("Song")){
//                Log.e("playLines.songFilesList: ",playLines.songFilesList+"");
                marrSectionsWithContent.add(playLines.songFilesList);
                marrSectionTitles.add(playLines.textLinesList.get(0).LineText);
                for(SongFiles songFiles: playLines.songFilesList){
                    if(songFiles.FileType.equalsIgnoreCase("mp3")) {
                        marrSongFilesMP3.add(songFiles);
                    }
                }
            }
        }
//        Log.e("Sections with contents:",marrSectionsWithContent+"");
//        Log.e("section titles:",marrSectionTitles+"");
//        Log.e("Song files:",marrSongFilesMP3+"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView= inflater.inflate(R.layout.activity_choose_music, container, false);
        listHeaderView = (LinearLayout)inflater.inflate(R.layout.item_music_header_view, null);
        musicSectionedAdapter=new MusicSectionedAdapter(getActivity());
        noMusicView= (LinearLayout)convertView.findViewById(R.id.noMusicView);
        txtNoMusic=(TextView)convertView.findViewById(R.id.txtNoMusic);
        txtDownloadAllMusic=(WMTextView)listHeaderView.findViewById(R.id.txtDownloadAllMusic);
        imgDownloadAllMusic=(ImageView)listHeaderView.findViewById(R.id.imgDownloadAllMusic);
        listMusic = (PinnedHeaderListView)convertView.findViewById(R.id.listViewMusic);
        listMusic.addHeaderView(listHeaderView);
        listMusic.setAdapter(musicSectionedAdapter);
        imgDownloadAllMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                dialog.title("Henter alle sange. De vil blive vist løbende.");
                dialog.setCancelable(true);
                dialog.show();
                ArrayList<SongFiles> songFilesArrayList=new ArrayList<SongFiles>();
                for(SongFiles songFile:marrSongFilesMP3) {
                    FileDescriptor fd = null;
                    File fileDir = new File(Environment.getExternalStorageDirectory() + "/danteater");
                    String audioPath = fileDir.getAbsolutePath() +"/"+songFile.SongMp3Url.substring(songFile.SongMp3Url.lastIndexOf("/")+1);
                    if(!(new File(audioPath).exists())) {
                        songFilesArrayList.add(songFile);
                    }
                }
                for(SongFiles songFiles: songFilesArrayList) {
                downloadAllMusic(songFiles);
                }
                new CountDownTimer(1500, 1000) {

                    @Override
                    public void onFinish() {
                        dialog.dismissWithStatus(R.drawable.ic_navigation_accept, "Henter alle sange. De vil blive vist løbende.");

                    }

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }
                }.start();


            }
        });
        if(marrSectionTitles.size()==0 && marrSongFilesMP3.size()==0) {
            noMusicView.setVisibility(View.VISIBLE);
            listMusic.setVisibility(View.GONE);
            txtDownloadAllMusic.setText("");
            txtDownloadAllMusic.setVisibility(View.GONE);
            imgDownloadAllMusic.setVisibility(View.GONE);
        } else if(marrSongFilesMP3.size()==0) {
            noMusicView.setVisibility(View.GONE);
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("OBS");
            alert.setMessage("Sangene i dette stykker er kendte melodier." + "\n" + "Du skal derfor selv finde indspilninger eller noder." + "\n" + "Husk at indberette brugen til KODA.");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.show();
            listMusic.setVisibility(View.VISIBLE);
            txtDownloadAllMusic.setText("");
            txtDownloadAllMusic.setVisibility(View.GONE);
            imgDownloadAllMusic.setVisibility(View.GONE);
            musicSectionedAdapter.notifyDataSetChanged();
        } else if(songFilesMP3ToBeDownloaded().size()==0) {
            noMusicView.setVisibility(View.GONE);
            listMusic.setVisibility(View.VISIBLE);
            txtDownloadAllMusic.setText("Al musik er hentet");
            imgDownloadAllMusic.setVisibility(View.GONE);
            txtDownloadAllMusic.setVisibility(View.VISIBLE);
            musicSectionedAdapter.notifyDataSetChanged();
        }else  {
            noMusicView.setVisibility(View.GONE);
            listMusic.setVisibility(View.VISIBLE);
            txtDownloadAllMusic.setText("Hent al musik");
            imgDownloadAllMusic.setVisibility(View.VISIBLE);
            txtDownloadAllMusic.setVisibility(View.VISIBLE);
            musicSectionedAdapter.notifyDataSetChanged();
        }

        return convertView;
    }

    public class MusicSectionedAdapter extends SectionedBaseAdapter {

        private LayoutInflater mInflater;

        public MusicSectionedAdapter(Context context) {
//            Log.e("inside adapter","yahooo");
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getItem(int section, int position) {

            return  null;
        }

        @Override
        public long getItemId(int section, int position) {

            return 0;
        }

        @Override
        public int getSectionCount() {
//            Log.e("section count: ",marrSectionsWithContent.size()+"");
            return marrSectionsWithContent.size();
        }

        @Override
        public int getCountForSection(int section) {

            ArrayList<SongFiles> songFileses=marrSectionsWithContent.get(section);
//            Log.e("child for section count: ",songFileses.size()+"");
            return songFileses.size();

        }


        public class SectionHeaderHolder {
             TextView headerTitle;
        }
        @Override
        public View getSectionHeaderView(final int section, View convertView, ViewGroup parent) {
            View view = convertView;
            SectionHeaderHolder sectionHeaderHolder;
            if (view == null) {
                sectionHeaderHolder=new SectionHeaderHolder();
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflator.inflate(R.layout.item_music_table_section, parent,false);
                sectionHeaderHolder.headerTitle=(TextView)view.findViewById(R.id.music_table_section_title);
                view.setTag(sectionHeaderHolder);
            } else {
                sectionHeaderHolder=(SectionHeaderHolder)view.getTag();
            }
            sectionHeaderHolder.headerTitle.setText(marrSectionTitles.get(section));
            return view;
        }

        @Override
        public int getItemViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int section, int position) {

            ArrayList<SongFiles> songFileses=marrSectionsWithContent.get(section);
            SongFiles songFiles=songFileses.get(position);

                if(songFiles.FileType.equalsIgnoreCase("mp3")) {
                    return MP3_FILE;
                } else {
                   return PDF_FILE;
                }

        }

        @Override
        public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {

            ViewHolder.ViewHolderForMusic viewHolderForMusic=null;
            ViewHolder.ViewHolderForPDF viewHolderForPDF=null;
            int type=getItemViewType(section,position);
            final SongFiles songFile = marrSectionsWithContent.get(section).get(position);
//            Log.e("type:",type+"");
            if(type==MP3_FILE) {

                if(convertView == null){
                    viewHolderForMusic = new ViewHolder().new ViewHolderForMusic();
                    convertView = mInflater.inflate(R.layout.item_music_table_view_cell, parent,false);
                    viewHolderForMusic.cellMusicTableView=new CellMusicTableView(convertView,getActivity());
                    convertView.setTag(viewHolderForMusic);

                }else{
                    viewHolderForMusic = (ViewHolder.ViewHolderForMusic)convertView.getTag();
                }



                File fileDir = new File(Environment.getExternalStorageDirectory() + "/danteater");
                final  String audioPath = fileDir.getAbsolutePath() +"/"+songFile.SongMp3Url.substring(songFile.SongMp3Url.lastIndexOf("/")+1);
                int fCount = 0;
                int cCount = 0;
                if(new File(audioPath).exists()){
                    try {
                        FileInputStream fis = new FileInputStream(audioPath);
                        FileDescriptor fd = fis.getFD();
                        MediaPlayer mp = new MediaPlayer();
                        mp.setDataSource(fd);
                        mp.prepare();
                        fCount = mp.getDuration();
                        cCount = mp.getCurrentPosition();
                    }catch(FileNotFoundException e){}
                    catch (IOException e){}
                }else{
                }

                viewHolderForMusic.cellMusicTableView.setCurrentPlayingPosition(CURRENT_PLAYING_POSITION,CURRENT_PLAYING_SECTION);
                viewHolderForMusic.cellMusicTableView.setCounts(cCount,fCount);
                viewHolderForMusic.cellMusicTableView.setUpSongFile(songFile,getActivity(),section,position,selectedPlay.Title);

                viewHolderForMusic.cellMusicTableView.setReloadClicked(new CellMusicTableView.setOnReload() {
                    @Override
                    public void onReload() {
                        musicSectionedAdapter.notifyDataSetChanged();
                    }
                });

                viewHolderForMusic.cellMusicTableView.setMusicSongStopClicked(new SetStopMusic() {
                    @Override
                    public void onStopMusic(MediaPlayer media) {
                        mediaPlayer = media;
                    }
                });



                viewHolderForMusic.cellMusicTableView.setOnClick(new CellMusicTableView.setOnPlayClick() {
                    @Override
                    public void onPlayClicked() {

                        if(mediaPlayer != null && mediaPlayer.isPlaying()){
                            //mediaPlayer.stop();
                            mediaPlayer.pause();
                        }else{


                            CURRENT_PLAYING_POSITION = position;
                            CURRENT_PLAYING_SECTION = section;
                            try {
                                FileInputStream fis = new FileInputStream(audioPath);
                                FileDescriptor fd = fis.getFD();
                                mediaPlayer.reset();
                                mediaPlayer.setDataSource(fd);
                                mediaPlayer.prepare();
                                mediaPlayer.start();


                            }catch(FileNotFoundException e){}
                            catch (IOException e){}

                        }
                        musicSectionedAdapter.notifyDataSetChanged();
                    }
                });



            } else {
                if(convertView == null){
                    viewHolderForPDF = new ViewHolder().new ViewHolderForPDF();
                    convertView = mInflater.inflate(R.layout.item_script_table_view_cell, parent,false);
                    viewHolderForPDF.cellScriptTableView=new CellScriptTableView(convertView,getActivity());
                    convertView.setTag(viewHolderForPDF);
                }else{
                    viewHolderForPDF = (ViewHolder.ViewHolderForPDF)convertView.getTag();
                }
                viewHolderForPDF.cellScriptTableView.setUpScriptFile(songFile,marrSectionTitles.get(section),getActivity());
            }

            return convertView;
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);

//        getActivity().getMenuInflater().inflate(R.menu.menu_read,menu);

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

             if(mediaPlayer !=null && mediaPlayer.isPlaying()) {
                  mediaPlayer.pause();
                  mediaPlayer.stop();
                    mediaPlayer = null;

                  }

                getActivity().finish();


                break;


        }

        return true;
    }



    @Override
    public void onPause() {
        super.onPause();

       /* if(mediaPlayer !=null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.release();


        }*/

    }



    public ArrayList<SongFiles> songFilesMP3ToBeDownloaded() {
        FileDescriptor fd = null;
        File fileDir = new File(Environment.getExternalStorageDirectory() + "/danteater");

        ArrayList<SongFiles> songFilesArrayList=new ArrayList<SongFiles>();
        for(SongFiles songFile:marrSongFilesMP3) {
            String audioPath = fileDir.getAbsolutePath() +"/"+songFile.SongMp3Url.substring(songFile.SongMp3Url.lastIndexOf("/")+1);
            if(!(new File(audioPath).exists())) {
                songFilesArrayList.add(songFile);
            }
        }
        return songFilesArrayList;
    }

    public void downloadAllMusic(final SongFiles songFiles) {

        new AsyncTask<Void,Integer,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                int count;
                try {
                    URL url = new URL(songFiles.SongMp3Url.replace(" ","%20"));
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
//                    Log.e("lenghtOfFile: ", lenghtOfFile + "");
                    File fileDir=new File(Environment.getExternalStorageDirectory()+"/danteater");
                    if(!fileDir.exists()) {
                        fileDir.mkdir();
//                        Log.e("directory:","created");
                    } else {
//                        Log.e("directory:","already exist");
                    }
                    File file = new File(fileDir,songFiles.SongMp3Url.substring(songFiles.SongMp3Url.lastIndexOf("/")+1));
                    FileOutputStream output = new FileOutputStream(file);
                    InputStream input = conexion.getInputStream();
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress((int)(total*100/lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
//                    updateViewAfterDownLoad(position);
                } catch (Exception e) {
                    e.printStackTrace();
//                    Log.e("error:",e+"");
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
//                Log.e("progress: ",values[0].toString()+"");
                if(values[0].toString().equalsIgnoreCase("100")) {
                    musicSectionedAdapter.notifyDataSetChanged();
                    if (songFilesMP3ToBeDownloaded().size() == 0) {
                        noMusicView.setVisibility(View.GONE);
                        listMusic.setVisibility(View.VISIBLE);
                        txtDownloadAllMusic.setText("Al musik er hentet");
                        imgDownloadAllMusic.setVisibility(View.GONE);
                        txtDownloadAllMusic.setVisibility(View.VISIBLE);
//                        dialog.dismissWithStatus(R.drawable.ic_navigation_accept, "Henter alle sange. De vil blive vist løbende.");
                        musicSectionedAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();

    }


}
