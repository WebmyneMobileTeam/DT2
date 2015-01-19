package dk.danteater.danteater.tab_read;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import dk.danteater.danteater.Play.AssignedUsers;
import dk.danteater.danteater.Play.PlayLines;
import dk.danteater.danteater.Play.TextLines;
import dk.danteater.danteater.R;
import dk.danteater.danteater.customviews.WMTextView;
import dk.danteater.danteater.login.User;
import dk.danteater.danteater.model.ComplexPreferences;


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
    private User user;
    private Context ctx;

    public CellReadPlayRole(View view,Context ctx) {

        this.ctx = ctx;
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

        int w = ctx.getResources().getDisplayMetrics().widthPixels;
        lblAssignedName.setMaxWidth(w - (int)(w/2.75));
        lblAssignedName.setSingleLine(true);


    }

    public void setupForPlayLine(PlayLines playLine,int current_state,View view,boolean isEmptyPlayRole,boolean mark) {

        lblCharacterName.setBackgroundColor(Color.TRANSPARENT);
        if(mark == true){
            lblCharacterName.setBackgroundColor(Color.YELLOW);
        }
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);

        if(user.checkTeacherOrAdmin(user.getRoles())){
            btnAssignRole.setVisibility(View.VISIBLE);
        }else{
            btnAssignRole.setVisibility(View.INVISIBLE);
        }

        // for assigned role image

        Drawable retreived = btnAssignRole.getCompoundDrawables()[0];
        int l = retreived.getBounds().left;
        int r = retreived.getBounds().right;
        int t = retreived.getBounds().top;
        int b = retreived.getBounds().bottom;

       Drawable drawable;

        if(playLine.assignedUsersList == null || playLine.assignedUsersList.size() == 0){

             drawable = ctx.getResources().getDrawable( R.drawable.ic_number_of_participants_small );

        }else if(playLine.assignedUsersList.size() == 1){

             drawable = ctx.getResources().getDrawable( R.drawable.ic_assignmultiple );

        }else{
             drawable = ctx.getResources().getDrawable( R.drawable.ic_assignmultiple);
        }
        drawable.setBounds(l,t,r,b);
        btnAssignRole.setCompoundDrawables( drawable, null, null, null);


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

