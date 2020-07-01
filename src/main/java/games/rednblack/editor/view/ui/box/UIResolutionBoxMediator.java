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

package games.rednblack.editor.view.ui.box;

import com.badlogic.gdx.math.Vector3;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.puremvc.patterns.mediator.SimpleMediator;
import com.puremvc.patterns.observer.Notification;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.editor.HyperLap2DFacade;
import games.rednblack.editor.proxy.ProjectManager;
import games.rednblack.editor.proxy.ResolutionManager;
import games.rednblack.editor.renderer.data.ResolutionEntryVO;
import games.rednblack.editor.view.ui.dialog.CreateNewResolutionDialog;

/**
 * Created by sargis on 4/8/15.
 */
public class UIResolutionBoxMediator extends SimpleMediator<UIResolutionBox> {
    private static final String TAG = UIResolutionBoxMediator.class.getCanonicalName();
    public static final String NAME = TAG;
    private ProjectManager projectManager;

    public UIResolutionBoxMediator() {
        super(NAME, new UIResolutionBox());
    }

    @Override
    public void onRegister() {
        super.onRegister();
        facade = HyperLap2DFacade.getInstance();
        projectManager = facade.retrieveProxy(ProjectManager.NAME);
    }

    @Override
    public String[] listNotificationInterests() {
        return new String[]{
                ProjectManager.PROJECT_OPENED,
                UIResolutionBox.CHANGE_RESOLUTION_BTN_CLICKED,
                UIResolutionBox.DELETE_RESOLUTION_BTN_CLICKED,
                UIResolutionBox.REPACK_BTN_CLICKED,
                ResolutionManager.RESOLUTION_LIST_CHANGED,
				CreateNewResolutionDialog.CLOSE_DIALOG
        };
    }

    @Override
    public void handleNotification(Notification notification) {
        super.handleNotification(notification);
        Sandbox sandbox = Sandbox.getInstance();
        ResolutionEntryVO resolutionEntryVO;
        switch (notification.getName()) {
            case ResolutionManager.RESOLUTION_LIST_CHANGED:
			case ProjectManager.PROJECT_OPENED:
				viewComponent.update();
                break;
			case CreateNewResolutionDialog.CLOSE_DIALOG:
				viewComponent.setCurrentResolution();
				break;
			case UIResolutionBox.CHANGE_RESOLUTION_BTN_CLICKED:
                resolutionEntryVO = notification.getBody();
                float zoom = sandbox.getZoomPercent();
                Vector3 cameraPos = new Vector3(sandbox.getCamera().position);
                String name = sandbox.sceneControl.getCurrentSceneVO().sceneName;
                projectManager.openProjectAndLoadAllData(projectManager.getCurrentProjectPath(), resolutionEntryVO.name);
                sandbox.loadCurrentProject(name);
                sandbox.setZoomPercent(zoom);
                sandbox.getCamera().position.set(cameraPos);
                break;
            case UIResolutionBox.DELETE_RESOLUTION_BTN_CLICKED:
                resolutionEntryVO = notification.getBody();
                Dialogs.showConfirmDialog(sandbox.getUIStage(),
                        "Delete Resolution",
                        "Are you sure you want to delete '" + resolutionEntryVO.name + "' resolution?",
                        new String[]{"Cancel", "Delete"}, new Integer[]{0, 1},
                        result -> {
                            if (result == 1) {
                                ResolutionManager resolutionManager = facade.retrieveProxy(ResolutionManager.NAME);
                                resolutionManager.deleteResolution(resolutionEntryVO);
                                String sceneName = sandbox.sceneControl.getCurrentSceneVO().sceneName;
                                sandbox.loadCurrentProject(sceneName);
                            }
                        });
                break;

            case UIResolutionBox.REPACK_BTN_CLICKED:
                ResolutionManager resolutionManager = facade.retrieveProxy(ResolutionManager.NAME);
                resolutionManager.rePackProjectImagesForAllResolutions();
                String sceneName = sandbox.sceneControl.getCurrentSceneVO().sceneName;
                sandbox.loadCurrentProject(sceneName);
                break;
        }
    }
}
