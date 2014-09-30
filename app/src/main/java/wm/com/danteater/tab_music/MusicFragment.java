package wm.com.danteater.tab_music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.SongFiles;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.PinnedHeaderListView;
import wm.com.danteater.customviews.SectionedBaseAdapter;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.tab_read.ViewHolder;

public class MusicFragment extends Fragment {

    private Play selectedPlay;
    private ArrayList<PlayLines> playLineses;
    private ArrayList<String> marrSectionTitles=new ArrayList<String>();
    private ArrayList<ArrayList<SongFiles>> marrSectionsWithContent=new ArrayList<ArrayList<SongFiles>>();
    private ArrayList<SongFiles> marrSongFilesMP3=new ArrayList<SongFiles>();
    public PinnedHeaderListView listMusic;
    public static int MP3_FILE = 0;
    public static int PDF_FILE = 1;
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();

        return fragment;
    }
    public MusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Log.e("Sections with contents:",marrSectionsWithContent+"");
        Log.e("section titles:",marrSectionTitles+"");
        Log.e("Song files:",marrSongFilesMP3+"");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView= inflater.inflate(R.layout.fragment_music, container, false);
        listMusic = (PinnedHeaderListView)convertView.findViewById(R.id.listViewMusic);
        listMusic.setAdapter(new MusicSectionedAdapter(getActivity()));
        listMusic.setFastScrollEnabled(true);
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

            return null;
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
            ViewHolderForMusic viewHolderForMusic=null;
            ViewHolderForPDF viewHolderForPDF=null;
            int type=getItemViewType(section,position);
            final ArrayList<SongFiles>  songFileses=marrSectionsWithContent.get(section);
            Log.e("type:",type+"");
            if(type==MP3_FILE) {
                if(convertView == null){
                    viewHolderForMusic = new ViewHolderForMusic();
                    convertView = mInflater.inflate(R.layout.item_music_table_view_cell, parent,false);
                    viewHolderForMusic.musicText=(WMTextView)convertView.findViewById(R.id.music_table_view_cell_title);
                    convertView.setTag(viewHolderForMusic);

                }else{
                    viewHolderForMusic = (ViewHolderForMusic)convertView.getTag();
                }

                viewHolderForMusic.musicText.setText("Instrumental");


            } else {
                if(convertView == null){
                    viewHolderForPDF = new ViewHolderForPDF();
                    convertView = mInflater.inflate(R.layout.item_script_table_view_cell, parent,false);

                    viewHolderForPDF.scriptTitle=(WMTextView)convertView.findViewById(R.id.script_table_view_cell_title);
                    convertView.setTag(viewHolderForPDF);

                }else{
                    viewHolderForPDF = (ViewHolderForPDF)convertView.getTag();
                }
                viewHolderForPDF.scriptTitle.setText(marrSectionTitles.get(section)+"");
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(getActivity(),ReadChord.class);
                        i.putExtra("file_url", songFileses.get(position).SongMp3Url+"" );
                        startActivity(i);
                    }
                });
            }

            return convertView;
        }
    }

    public class ViewHolderForMusic {
        private WMTextView musicText;
    }
    public class ViewHolderForPDF {
        private WMTextView scriptTitle;
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
