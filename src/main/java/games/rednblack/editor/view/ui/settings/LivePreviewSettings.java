package games.rednblack.editor.view.ui.settings;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.h2d.common.MsgAPI;
import games.rednblack.h2d.common.view.SettingsNodeValue;
import games.rednblack.h2d.common.view.ui.StandardWidgetsFactory;
import games.rednblack.h2d.common.view.ui.widget.HyperLapColorPicker;
import games.rednblack.h2d.common.view.ui.widget.TintButton;
import games.rednblack.h2d.common.vo.ProjectVO;
import games.rednblack.puremvc.Facade;

public class LivePreviewSettings extends SettingsNodeValue<ProjectVO> {

    private final TintButton tintButton;
    private final VisCheckBox box2dDebug;

    public LivePreviewSettings() {
        super("Live Preview", Facade.getInstance());

        getContentTable().add("Window").left().row();
        getContentTable().addSeparator();

        getContentTable().add("Render").left().row();
        getContentTable().addSeparator();
        box2dDebug = StandardWidgetsFactory.createCheckBox("Box2D debug render");
        getContentTable().add(box2dDebug).left().padTop(5).padLeft(8).row();

        getContentTable().add("Background").left().padTop(10).row();
        getContentTable().addSeparator();
        VisTable tintTable = new VisTable();
        tintTable.add("Color:").padRight(5).left();
        tintButton = StandardWidgetsFactory.createTintButton();
        tintTable.add(tintButton).left().padRight(5);

        tintButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                HyperLapColorPicker picker = new HyperLapColorPicker(new ColorPickerAdapter() {
                    @Override
                    public void finished(Color newColor) {
                        tintButton.setColorValue(newColor);
                        getSettings().backgroundColor.set(newColor);
                    }

                    @Override
                    public void canceled(Color oldColor) {
                        tintButton.setColorValue(oldColor);
                        getSettings().backgroundColor.set(oldColor);
                    }

                    @Override
                    public void reset(Color previousColor, Color newColor) {
                        tintButton.setColorValue(previousColor);
                        getSettings().backgroundColor.set(previousColor);
                    }
                });

                picker.setColor(getSettings().backgroundColor);
                Sandbox.getInstance().getUIStage().addActor(picker.fadeIn());
            }
        });

        VisTextButton resetButton = StandardWidgetsFactory.createTextButton("Reset");
        resetButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getSettings().backgroundColor.set(0, 0, 0, 1.0f);
                tintButton.setColorValue(getSettings().backgroundColor);
            }
        });
        tintTable.add(resetButton);

        getContentTable().add(tintTable).padLeft(8).left().row();
    }

    @Override
    public void translateSettingsToView() {
        tintButton.setColorValue(getSettings().backgroundColor);
        box2dDebug.setChecked(getSettings().box2dDebugRender);
    }

    @Override
    public void translateViewToSettings() {
        getSettings().box2dDebugRender = box2dDebug.isChecked();
        facade.sendNotification(MsgAPI.SAVE_EDITOR_CONFIG);
    }

    @Override
    public boolean validateSettings() {
        return true;
    }
}
