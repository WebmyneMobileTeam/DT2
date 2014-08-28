package wm.com.danteater.tab_info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import wm.com.danteater.R;


public class InfoFragment extends Fragment {
    private String Synopsis;
    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();

        return fragment;
    }
    public InfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bInfo=getArguments();
        Synopsis=bInfo.getString("infoData");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_info, container, false);
        WebView webview=(WebView) view.findViewById(R.id.infoPage);
        webview.loadData(Synopsis, "text/html", "charset=UTF-8");
        return view;
    }


}
