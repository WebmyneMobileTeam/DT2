package wm.com.danteater.tab_read;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import wm.com.danteater.Play.AssignedUsers;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;

/**
 * Created by dhruvil on 29-09-2014.
 */
public class CellReadPlayRole {


    private TextView tvRoleDescription;
    private WMTextView lblCharacterName;
    private TextView lblAssignedName;
    private TextView lblNumberOfSongsAndLines;
    private WMTextView btnAssignRole;
    private setOnAssignButtonClicked setClicked;

    public CellReadPlayRole(View view) {

        tvRoleDescription = (TextView)view.findViewById(R.id.readPlayRoleDescription);
        lblAssignedName = (TextView)view.findViewById(R.id.readPlayRoleNotAssigned);
        lblCharacterName = (WMTextView)view.findViewById(R.id.readPlayRoleTitle);
        lblNumberOfSongsAndLines = (TextView)view.findViewById(R.id.readPlayRoleSongTitle);
        btnAssignRole = (WMTextView)view.findViewById(R.id.readPlayroleAssignedButton);
        btnAssignRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClicked.onAssignButtonClicked();
            }
        });
        lblCharacterName.setBold();

    }

    public void setupForPlayLine(PlayLines playLine,int current_state,View view,boolean isEmptyPlayRole) {

        if (isEmptyPlayRole == true) {
            tvRoleDescription.setVisibility(View.GONE);
        }

        if (playLine.RoleLinesCount.length() > 0) {
            lblNumberOfSongsAndLines.setText(playLine.RoleLinesCount);
        } else {
            lblNumberOfSongsAndLines.setText("");
        }

        if (playLine.assignedUsersList.size() == 0) {

            lblAssignedName.setText("Ikke tildelt");


        } else {

            if (playLine.assignedUsersList.size() == 1) {

                String text = playLine.assignedUsersList.get(0).AssignedUserName;
                if (text == null) {
                    text = playLine.assignedUsersList.get(0).AssignedUserId;
                }
                lblAssignedName.setText(text);


            } else {

                StringBuffer text = new StringBuffer("");

                for (AssignedUsers au : playLine.assignedUsersList) {
                    String textName = au.AssignedUserName;
                    if (textName == null) {
                        textName = au.AssignedUserId;
                    }
                    text.append(textName);
                    text.append(", ");
                }

                lblAssignedName.setText(text.toString());
            }
        }

        if (playLine.textLinesList == null || playLine.textLinesList.size() == 0) {
            return;
        } else if (playLine.textLinesList.size() == 1) {

            TextLines textLine = playLine.textLinesList.get(0);
            lblCharacterName.setText(playLine.RoleName);

            // todo for pupil btn
            tvRoleDescription.setText(textLine.LineText);


        }


    }


   public void setAssignClicked(setOnAssignButtonClicked s){
        setClicked = s;
    }

    }

  interface setOnAssignButtonClicked{

    public void onAssignButtonClicked();

}

    



