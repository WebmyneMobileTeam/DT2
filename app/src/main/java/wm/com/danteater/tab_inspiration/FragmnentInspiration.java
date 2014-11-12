package wm.com.danteater.tab_inspiration;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.excercise.VideoPlay;
import wm.com.danteater.login.User;
import wm.com.danteater.model.AppConstants;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;

/**
 * Created by nirav on 02-10-2014.
 */
public class FragmnentInspiration extends Fragment implements ImageChooserListener {

    private int[] arrImagesTeacher = {R.drawable.teacher1, R.drawable.teacher2, R.drawable.teacher3};
    private int[] arrImagesPupils = {R.drawable.vid1, R.drawable.vid2, R.drawable.vid3, R.drawable.vid4};

    private int[] arrVideosTeachers = {R.raw.goderaad_converted, R.raw.vaerktoejer_converted, R.raw.forklaring_converted};
    private int[] arrVideosPupils = {R.raw.intro_converted, R.raw.fokus_converted, R.raw.taleteknik_converted, R.raw.foelelser_converted};

    private String[] arrCaptionsTeachers = {"Gode råd", "Værktøjer", "Forklaring"};
    private String[] arrCaptionsPupils = {"Intro", "Fokus", "Taleteknik", "Følelser"};

    private GridLayout gridTeachers;
    private GridLayout gridPupils;
    private GridLayout gridInspiration;

    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String filePath;

    private Play selectedPlay;

    private ArrayList<Inspiration> inspirations;
    private LinearLayout linearInspirationView;
    private LinearLayout linearTeacherInspirations;

    private User currentUser;


    public static FragmnentInspiration newInstance(String param1, String param2) {
        FragmnentInspiration fragment = new FragmnentInspiration();
        return fragment;
    }

    public FragmnentInspiration() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play", Play.class);
        currentUser = complexPreferences.getObject("current_user", User.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inspiration, container, false);


        gridTeachers = (GridLayout) view.findViewById(R.id.gridTeacherInspiration);
        gridPupils = (GridLayout) view.findViewById(R.id.gridPupilsInspiration);
        gridInspiration = (GridLayout)view.findViewById(R.id.gridInspiration);
        linearInspirationView = (LinearLayout)view.findViewById(R.id.linearInspirationView);
        linearInspirationView.setVisibility(View.GONE);
        linearTeacherInspirations = (LinearLayout)view.findViewById(R.id.linearTeacherInspirations);
        linearTeacherInspirations.setVisibility(View.GONE);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // fillTeachers();

        if(getArguments().getString("inspirationType").equalsIgnoreCase("tab")) {
            fetchAndDisplayInspirations();
            linearInspirationView.setVisibility(View.VISIBLE);
        }else{

        }

        gridTeachers.removeAllViews();
        gridPupils.removeAllViews();

        int w = getResources().getDisplayMetrics().widthPixels;
        if(currentUser.checkTeacherOrAdmin(currentUser.getRoles())){

            linearTeacherInspirations.setVisibility(View.VISIBLE);
            for (int i = 0; i < arrVideosTeachers.length; i++) {

                final int play = i;
                View vIT = getActivity().getLayoutInflater().inflate(R.layout.item_inspiration, null, false);
                ImageView iv = (ImageView) vIT.findViewById(R.id.imgItemInspiration);
                TextView tv = (TextView) vIT.findViewById(R.id.txtItemInspiration);
                iv.setImageResource(arrImagesTeacher[i]);
                tv.setText(arrCaptionsTeachers[i]);
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(new ViewGroup.MarginLayoutParams(w / 2, w / 4));

                vIT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ia = new Intent(getActivity(), VideoPlay.class);
                        ia.putExtra("video_path", arrVideosTeachers[play]);
                        startActivity(ia);
                    }
                });
                gridTeachers.addView(vIT, layoutParams);


            }



        }




            for (int k = 0; k < arrVideosPupils.length; k++) {

                final int Another = k;
                View vit = getActivity().getLayoutInflater().inflate(R.layout.item_inspiration, null, false);
                ImageView ivn = (ImageView) vit.findViewById(R.id.imgItemInspiration);
                TextView tvn = (TextView) vit.findViewById(R.id.txtItemInspiration);
                ivn.setImageResource(arrImagesPupils[k]);
                tvn.setText(arrCaptionsPupils[k]);
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(new ViewGroup.MarginLayoutParams(w / 2, w / 4));
                vit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent ia = new Intent(getActivity(), VideoPlay.class);
                        ia.putExtra("video_path", arrVideosPupils[Another]);
                        startActivity(ia);
                    }
                });
                gridPupils.addView(vit, layoutParams);


            }


        //todo fetch






        }

    public void fetchAndDisplayInspirations() {

           inspirations = new ArrayList<Inspiration>();
           inspirations.clear();

           gridInspiration.removeAllViews();

            int w = getResources().getDisplayMetrics().widthPixels;
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(new ViewGroup.MarginLayoutParams(w / 2, w / 4));

            final ImageView iv = new ImageView(getActivity());
            iv.setScaleType(ImageView.ScaleType.CENTER);
            iv.setImageResource(R.drawable.cameraadd);
            gridInspiration.addView(iv, 0, layoutParams);

            registerForContextMenu(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    getActivity().openContextMenu(iv);

                }
            });





        final HUD dialog = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("Henter inspirationer");
        dialog.show();

        Log.e("Order ID ",selectedPlay.PlayId);
        new CallWebService("http://api.danteater.dk/api/Inspiration/"+selectedPlay.PlayId,CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {

                Log.e("Response from inspiration ",response);

                Type listType = new TypeToken<List<Inspiration>>() {}.getType();
                inspirations = new GsonBuilder().create().fromJson(response,listType);
                dialog.dismiss();
                fillInspirations();

            }
            @Override
            public void error(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();

            }
        }.start();

    }

    private void fillInspirations() {


        int w = getResources().getDisplayMetrics().widthPixels;

        for(int i=0;i<inspirations.size();i++){

            final int showInsp = i;
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(new ViewGroup.MarginLayoutParams(w / 2, w / 4));
            final ImageView ivi = new ImageView(getActivity());
            ivi.setScaleType(ImageView.ScaleType.CENTER);
             ivi.setImageResource(R.drawable.camerax);
            gridInspiration.addView(ivi,layoutParams);

            ivi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    InspirationDetailsView inspView = new InspirationDetailsView(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                    inspView.setTypeView();
                    inspView.setupExistingInspiration(inspirations.get(showInsp));
                    inspView.show();
                }
            });
        }





    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0,1, Menu.NONE,"Camera");
        menu.add(0,2,Menu.NONE,"Choose from gallery");


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {



        switch (item.getItemId()){

            case 1:
                takePicture();
                break;

            case 2:
                chooseImage();
                break;


        }

        return super.onContextItemSelected(item);
    }

    private void chooseImage() {

        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, "cabkab", true);
        imageChooserManager.setImageChooserListener(this);
        try {
            //pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {

        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, "cabkab", true);
        imageChooserManager.setImageChooserListener(this);
        try {
            //  pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }

            imageChooserManager.submit(requestCode, data);
        } else if(resultCode == getActivity().RESULT_CANCELED){


        }else{

        }

    }

    private void reinitializeImageChooser() {

        imageChooserManager = new ImageChooserManager(this, chooserType,"danteater", true);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }
    @Override
    public void onImageChosen(final ChosenImage chosenImage) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                InspirationDetailsView inspirationView = new InspirationDetailsView(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                String userName = currentUser.getFirstName()+" "+currentUser.getLastName();
                inspirationView.setupAfterImageChoose(chosenImage,userName+","+currentUser.getDomain());
                inspirationView.setSelectedListner(new InspirationDetailsView.setSelectedListner() {
                    @Override
                    public void selected(final String filePath, final String message) {



                        new AsyncTask<Void,Void,Void>(){

                            HUD hud;

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();

                                hud = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                                hud.title("Sending...");
                                hud.show();

                            }

                            @Override
                            protected Void doInBackground(Void... voids) {

                                try {
                                    uploadtoServer(filePath,message);

                                }catch(Exception e){}




                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);

                                hud.dismiss();
                                fetchAndDisplayInspirations();

                            }
                        }.execute();

                    }
                });
                inspirationView.show();

            }
        });
    }


    private void uploadtoServer(String filePath, String message) throws Exception{

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost("http://api.danteater.dk/api/Inspiration");
        String boundary = "--"+"62cd4a08872da000cf5892ad65f1ebe6";
        httppost.setHeader("Content-type","multipart/form-data; boundary="+boundary);

        Bitmap b = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG,85, baos);
        byte[] imageBytes = baos.toByteArray();
        ByteArrayBody bab = new ByteArrayBody(imageBytes,new File(filePath).getName()+".jpg");


        HttpEntity entity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setBoundary(boundary)
                .addPart("PlayId",new StringBody(URLEncoder.encode(selectedPlay.PlayId,"UTF-8")))
                .addPart("UserName",new StringBody(currentUser.getFirstName()+" "+currentUser.getLastName()))
                .addPart("UserId",new StringBody(URLEncoder.encode(currentUser.getUserId(),"UTF-8")))
                .addPart("SchoolId",new StringBody(URLEncoder.encode(currentUser.getDomain(),"UTF-8")))
                .addPart("SchoolName",new StringBody(URLEncoder.encode(currentUser.getDomain(),"UTF-8")))
                .addPart("MessageText",new StringBody(message))
                .addPart("userfile",bab)
                .build();

        httppost.setEntity(entity);
        try {
            HttpResponse response = httpclient.execute(httppost);

        }catch(Exception e){

        }


    }

    @Override
    public void onError(String s) {


    }
}


