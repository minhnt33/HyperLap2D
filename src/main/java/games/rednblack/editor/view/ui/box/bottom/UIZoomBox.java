/*
 * ******************************************************************************
 *  * Copyright 2015 See AUTHORS file.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package games.rednblack.editor.view.ui.box.bottom;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import games.rednblack.editor.event.KeyboardListener;
import games.rednblack.editor.view.ui.box.UIBaseBox;
import games.rednblack.h2d.common.view.ui.StandardWidgetsFactory;
import games.rednblack.puremvc.Facade;

/**
 * Created by sargis on 4/27/15.
 */
public class UIZoomBox extends UIBaseBox {

    private static final String prefix = "games.rednblack.editor.view.ui.box.bottom.UIZoomBox";

    public static final String ZOOM_SHIFT_REQUESTED = prefix + "ZOOM_SHIFT_REQUESTED";
    public static final String ZOOM_VALUE_CHANGED = prefix + "ZOOM_VALUE_CHANGED";

    private final Skin skin;
    private VisTextField percentValueField;
    private VisImageButton zoomInBtn;
    private VisImageButton zoomOutBtn;

    public UIZoomBox() {
        super();
        skin = VisUI.getSkin();
        init();
        setVisible(false);
    }

    @Override
    public void update() {
        setVisible(true);
    }

    private void init() {
        addSeparator(true).padRight(13).padLeft(13);
        add("Zoom:").padRight(4);
        //
        VisImageButton.VisImageButtonStyle zoominButtonStyle = new VisImageButton.VisImageButtonStyle(skin.get("dark", VisImageButton.VisImageButtonStyle.class));
        zoominButtonStyle.imageUp = skin.getDrawable("icon-zoomin");
        zoominButtonStyle.imageOver = skin.getDrawable("icon-zoomin-over");
        zoominButtonStyle.imageDisabled = skin.getDrawable("icon-zoomin-disabled");
        //
        zoomInBtn = new VisImageButton("dark");
        zoomInBtn.setStyle(zoominButtonStyle);
        zoomInBtn.addListener(new UIZoomBoxButtonClickListener(0.5f));
        add(zoomInBtn).padRight(6).height(25);
        //
        VisImageButton.VisImageButtonStyle zoomoutButtonStyle = new VisImageButton.VisImageButtonStyle(skin.get("dark", VisImageButton.VisImageButtonStyle.class));
        zoomoutButtonStyle.imageUp = skin.getDrawable("icon-zoomout");
        zoomoutButtonStyle.imageOver = skin.getDrawable("icon-zoomout-over");
        zoomoutButtonStyle.imageDisabled = skin.getDrawable("icon-zoomout-disabled");
        //
        percentValueField = StandardWidgetsFactory.createTextField("light");
        percentValueField.addListener(new KeyboardListener(ZOOM_VALUE_CHANGED));
		percentValueField.setAlignment(Align.center);
        add(percentValueField).width(50);
		//
		zoomOutBtn = new VisImageButton("dark");
		zoomOutBtn.setStyle(zoomoutButtonStyle);
		zoomOutBtn.addListener(new UIZoomBoxButtonClickListener(2f));
		add(zoomOutBtn).padLeft(6).height(25);
    }

    public String getCurrentZoom() {
        String percent = percentValueField.getText();
        if(!percent.substring(percent.length()-1, percent.length()).equals("%")) {
            percent+="%";
            percentValueField.setText(percent);
        }
        return percent.substring(0, percent.length()-1);
    }

    public void setCurrentZoom(String currentZoom) {
        percentValueField.setText(currentZoom + '%');
    }

    private static class UIZoomBoxButtonClickListener extends ClickListener {
        private final float zoomDevider;

        public UIZoomBoxButtonClickListener(float zoomDevider) {
            this.zoomDevider = zoomDevider;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            Facade facade = Facade.getInstance();
            facade.sendNotification(ZOOM_SHIFT_REQUESTED, zoomDevider);
        }
    }
}
