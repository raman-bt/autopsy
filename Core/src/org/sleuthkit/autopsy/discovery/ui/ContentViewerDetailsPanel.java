/*
 * Autopsy
 *
 * Copyright 2020 Basis Technology Corp.
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
package org.sleuthkit.autopsy.discovery.ui;

import org.openide.nodes.Node;
import org.sleuthkit.autopsy.corecomponents.DataContentPanel;
import org.sleuthkit.autopsy.coreutils.ThreadConfined;
import org.sleuthkit.autopsy.datamodel.BlackboardArtifactNode;
import org.sleuthkit.datamodel.BlackboardArtifact;

/**
 * Details panel for displaying the collection of content viewers.
 */
final class ContentViewerDetailsPanel extends AbstractArtifactDetailsPanel {

    private static final long serialVersionUID = 1L;
    private final DataContentPanel contentViewer = DataContentPanel.createInstance();

    /**
     * Creates new form ContentViewerDetailsPanel
     */
    @ThreadConfined(type = ThreadConfined.ThreadType.AWT)
    ContentViewerDetailsPanel() {
        initComponents();
        add(contentViewer);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    @ThreadConfined(type = ThreadConfined.ThreadType.AWT)
    @Override
    public void setArtifact(BlackboardArtifact artifact) {
        Node node = Node.EMPTY;
        if (artifact != null) {
            node = new BlackboardArtifactNode(artifact);
        }
        contentViewer.setNode(node);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}