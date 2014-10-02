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
    MediaPlayer mediaPlayer;
    MusicSectionedAdapter musicSectionedAdapter;
    private ArrayList<String> marrSectionTitles=new ArrayList<String>();
    private ArrayList<ArrayList<SongFiles>> marrSectionsWithContent=new ArrayList<ArrayList<SongFiles>>();
    private ArrayList<SongFiles> marrSongFilesMP3=new ArrayList<SongFiles>();
    public PinnedHeaderListView listMusic;
    private HUD dialog;
    public static int MP3_FILE = 0;
    public static int PDF_FILE = 1;


    public static MusicFragment newInstance(String param1, String param2) {
    MusicFragment fragment = new MusicFragment();

        return fragment;
    }
    public MusicFragment() {
        // Required empty public constructor
    }

    public static ArrayList<String> playingMusic = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

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
        Log.e("Sections with contents:",marrSectionsWithContent+"");
        Log.e("section titles:",marrSectionTitles+"");
        Log.e("Song files:",marrSongFilesMP3+"");


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
        listMusic.setFastScrollEnabled(true);


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
        } else  { // TODO else if condition for all download
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
            Log.e("inside adapter","yahooo");
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
            Log.e("section count: ",marrSectionsWithContent.size()+"");
            return marrSectionsWithContent.size();
        }

        @Override
        public int getCountForSection(int section) {

            ArrayList<SongFiles> songFileses=marrSectionsWithContent.get(section);
            Log.e("child for section count: ",songFileses.size()+"");
            return songFileses.size();

        }

        @Override
        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflator.inflate(R.layout.item_music_table_section, parent,false);
            } else {
                view = convertView;
            }
            ((TextView) view.findViewById(R.id.music_table_section_title)).setText(marrSectionTitles.get(section));
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
        public View getItemView(int section, final int position, View convertView, ViewGroup parent) {

            ViewHolder.ViewHolderForMusic viewHolderForMusic=null;
            ViewHolder.ViewHolderForPDF viewHolderForPDF=null;

            int type=getItemViewType(section,position);

            final ArrayList<SongFiles>  songFileses=marrSectionsWithContent.get(section);
            Log.e("type:",type+"");
            if(type==MP3_FILE) {

                if(convertView == null){
                    viewHolderForMusic = new ViewHolder().new ViewHolderForMusic();
                    convertView = mInflater.inflate(R.layout.item_music_table_view_cell, parent,false);
                    viewHolderForMusic.cellMusicTableView=new CellMusicTableView(convertView,getActivity());
                    convertView.setTag(viewHolderForMusic);

                }else{
                    viewHolderForMusic = (ViewHolder.ViewHolderForMusic)convertView.getTag();
                }
                viewHolderForMusic.cellMusicTableView.setUpSongFile(songFileses.get(position),marrSectionTitles.get(section),getActivity(),section,position);

                viewHolderForMusic.cellMusicTableView.setReloadClicked(new setOnReload() {
                    @Override
                    public void onReload() {
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
                viewHolderForPDF.cellScriptTableView.setUpScriptFile(songFileses.get(position),marrSectionTitles.get(section),getActivity());
            }

            return convertView;
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.menu_read,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                getActivity().finish();

                break;


        }

        return super.onOptionsItemSelected(item);
    }



}
