/*
 * Autopsy Forensic Browser
 * 
 * Copyright 2018 Basis Technology Corp.
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
package org.sleuthkit.autopsy.datamodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.sleuthkit.autopsy.casemodule.Case;
import org.sleuthkit.autopsy.coreutils.Logger;
import org.sleuthkit.datamodel.DataSource;
import org.sleuthkit.datamodel.SleuthkitCase;
import org.sleuthkit.datamodel.TskCoreException;

/**
 * Child factory for Data Source grouping nodes 
 * A node is created for each data source in the case
 * 
 */
public class DataSourceGroupingChildren extends Children.Keys<DataSource> {

    private static final Logger logger = Logger.getLogger(DataSourceGroupingChildren.class.getName());
    private final SleuthkitCase sleuthkitCase;

  

    /**
     * Constructs the factory object
     * 
     * @param tskCase - Case DB 
     */
    public DataSourceGroupingChildren(SleuthkitCase tskCase) {
        this.sleuthkitCase = tskCase;
    }

    /**
     * Listener for handling DATA_SOURCE_ADDED events.
     */
    private final PropertyChangeListener pcl = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String eventType = evt.getPropertyName();
            if (eventType.equals(Case.Events.DATA_SOURCE_ADDED.toString())) {
                reloadKeys();
            }
        }
    };

    private void reloadKeys() {
        try {
            List<DataSource> keys = sleuthkitCase.getDataSources();
            setKeys(keys);
        } catch (TskCoreException ex) {
            logger.log(Level.SEVERE, "Failed to get Datasources from DB", ex);
        }
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        Case.addEventTypeSubscriber(EnumSet.of(Case.Events.DATA_SOURCE_ADDED), pcl);
        reloadKeys();
    }

    @Override
    protected void removeNotify() {
        super.removeNotify();
        Case.removeEventTypeSubscriber(EnumSet.of(Case.Events.DATA_SOURCE_ADDED), pcl);
        setKeys(Collections.emptyList());
    }

    @Override
    protected Node[] createNodes(DataSource ds) {
        return new Node[]{createNodeForKey(ds)};
    }

    protected Node createNodeForKey(DataSource ds) {
        return new DataSourceGroupingNode(ds);
    }

}