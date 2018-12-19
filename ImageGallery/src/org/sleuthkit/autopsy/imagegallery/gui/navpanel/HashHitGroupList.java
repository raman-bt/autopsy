/*
 * Autopsy Forensic Browser
 *
 * Copyright 2016 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.imagegallery.gui.navpanel;

import java.util.function.Function;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.image.ImageView;
import org.openide.util.NbBundle;
import org.sleuthkit.autopsy.coreutils.ThreadConfined;
import org.sleuthkit.autopsy.imagegallery.FXMLConstructor;
import org.sleuthkit.autopsy.imagegallery.ImageGalleryController;
import org.sleuthkit.autopsy.imagegallery.datamodel.grouping.DrawableGroup;

/**
 * Shows only groups with hash hits in a flat list, with controls to adjust
 * sorting of list.
 */
final public class HashHitGroupList extends NavPanel<DrawableGroup> {

    @ThreadConfined(type = ThreadConfined.ThreadType.JFX)
    private final ListView<DrawableGroup> groupList = new ListView<>();

    private final ObservableList<DrawableGroup> groupsWithHashHits = FXCollections.observableArrayList();
    
    /**
     * sorted list of groups, setting a new comparator on this changes the
     * sorting in the ListView.
     */
    @ThreadConfined(type = ThreadConfined.ThreadType.JFX)
    private SortedList<DrawableGroup> sorted = new SortedList<>(FXCollections.observableArrayList(groupsWithHashHits));

    public HashHitGroupList(ImageGalleryController controller) {
        super(controller);
        FXMLConstructor.construct(this, "NavPanel.fxml"); //NON-NLS
    }

    @ThreadConfined(type = ThreadConfined.ThreadType.JFX)
    @Override
    SelectionModel<DrawableGroup> getSelectionModel() {
        return groupList.getSelectionModel();
    }

    @Override
    Function< DrawableGroup, DrawableGroup> getDataItemMapper() {
        return Function.identity(); // this view already works with groups, so do nothing
    }

    @Override
    void applyGroupComparator() {
        sorted.setComparator(getComparator());
    }

    @FXML
    @Override
    @NbBundle.Messages({"HashHitGroupList.displayName.onlyHashHits=Only Hash Hits"})
    void initialize() {
        super.initialize();
        groupList.setPlaceholder(new Label(Bundle.NavPanel_placeHolder_text()));
        setText(Bundle.HashHitGroupList_displayName_onlyHashHits());
        setGraphic(new ImageView("org/sleuthkit/autopsy/imagegallery/images/hashset_hits.png")); //NON-NLS

        getBorderPane().setCenter(groupList);
        
        sorted.setComparator(getDefaultComparator());
        
//        sorted = getController().getGroupManager().getAnalyzedGroups()
//                .filtered(group -> group.getHashSetHitsCount() > 0)
//                .sorted(getDefaultComparator());
        
        groupsWithHashHits.addAll(getController().getGroupManager().getAnalyzedGroups()
                                    .filtered(group -> group.getHashSetHitsCount() > 0));
        

        GroupCellFactory groupCellFactory = new GroupCellFactory(getController(), comparatorProperty());
        groupList.setCellFactory(groupCellFactory::getListCell);
        groupList.setItems(sorted);
        
        getController().getGroupManager().getAnalyzedGroups().addListener(new ListChangeListener<DrawableGroup>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DrawableGroup> c) {

                System.out.println("RAMAN: getAnalyzedGroups() listener is called... ");
                while (c.next()) {

                    c.getAddedSubList().forEach((DrawableGroup t) -> {
                        if (t.getHashSetHitsCount() > 0) {
                            System.out.println("RAMAN: Adding new group to groupsWithHashHits... " + t.getGroupKey());
                            Platform.runLater(() -> groupsWithHashHits.add(t));
                        }
                    });
                    c.getRemoved().forEach((DrawableGroup t) -> {
                         System.out.println("RAMAN: Adding new group to groupsWithHashHits... " + t.getGroupKey());
                         
                        Platform.runLater(()-> groupsWithHashHits.remove(t));
                    });

                 }
            }
      });
        
    }

    @ThreadConfined(type = ThreadConfined.ThreadType.JFX)
    @Override
    void setFocusedGroup(DrawableGroup grouping) {
        groupList.getSelectionModel().select(grouping);
    }

    @Override
    GroupComparators<Long> getDefaultComparator() {
        return GroupComparators.HIT_COUNT;
    }
}
