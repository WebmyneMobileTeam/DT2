/*
 * Copyright 2014 - learnNcode (learnncode@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package wm.com.danteater.tab_read;

import android.widget.TextView;

import wm.com.danteater.tab_music.CellMusicTableView;
import wm.com.danteater.tab_music.CellScriptTableView;

public class ViewHolder {


    public class HolderRecordPlayRoleCell extends ViewHolder{}

    public class HolderEmptyPreviewPlayRoleCell extends ViewHolder{
           public CellPreviewPlayRole cellEmptyPreviewPlayRole;
    }

   // public class HolderPreviewPlayRoleCell extends ViewHolder{} // dont need .. managed

    public class HolderEmptyPlayRoleCell extends ViewHolder{}

    public class HolderReadPlayRoleCell extends ViewHolder{
        public CellReadPlayRole cellReadPlayRole;
        public TextView lblCharacterName;
    }

    public class HolderRecordPlayPlayLineCell extends ViewHolder{
        public CellRecordPlayPlayLine cellRecordPlayPlayLine;
    }

    public class HolderPreviewPlayPlayLineCell extends ViewHolder{}

    public class HolderReadPlayPlayLineCell extends ViewHolder{
        public CellReadPlayPlayLine cellReadPlayPlayLine;
    }

    public class HolderPreviewReadPlayNoteCell extends ViewHolder{}

    public class HolderReadPlayNoteCell extends ViewHolder{
        public CellReadPlayNote cellReadPlayNote;
    }

    public class HolderReadPlayInfoCell extends ViewHolder{
        public CellReadPlayInfo cellReadPlayInfo;
    }

    public class HolderReadPlayPictureCell extends ViewHolder{
        public CellReadPlayPicture cellReadPlayPicture;
    }

    public class HolderReadPlaySongCell extends ViewHolder{
        public CellReadPlaySong cellReadPlaySong;
    }

    public class HolderReadPlaySongLineCell extends ViewHolder{
        public CellReadPlaySongLine cellReadPlaySongLine;
    }

    public class ViewHolderForMusic extends ViewHolder {
        public CellMusicTableView cellMusicTableView;
    }

    public class ViewHolderForPDF extends ViewHolder {
        public CellScriptTableView cellScriptTableView;
    }


}
