/*
 * Autopsy Forensic Browser
 * 
 * Copyright 2011 Basis Technology Corp.
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

/*
 * HashDbSimplePanel.java
 *
 * Created on May 7, 2012, 10:38:26 AM
 */
package org.sleuthkit.autopsy.hashdatabase;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author dfickling
 */
public class HashDbSimplePanel extends javax.swing.JPanel {
    
    private static final Logger logger = Logger.getLogger(HashDbSimplePanel.class.getName());
    private HashTableModel knownBadTableModel;
    private NSRLTableModel nsrlTableModel;
    private HashDb nsrl;
    private static boolean ingestRunning = false;

    /** Creates new form HashDbSimplePanel */
    public HashDbSimplePanel() {
        knownBadTableModel = new HashTableModel();
        nsrlTableModel = new NSRLTableModel();
        initComponents();
        customizeComponents();
    }
    
    static void setIngestRunning(boolean running) {
        ingestRunning = running;
    }
    
    private void customizeComponents() {
        final HashDbXML xmlHandle = HashDbXML.getCurrent();
        if(xmlHandle.getNSRLSet()==null && xmlHandle.getKnownBadSets().isEmpty()) {
            calcHashesButton.setEnabled(true);
            calcHashesButton.setSelected(true);
            xmlHandle.setCalculate(true);
        } else {
            calcHashesButton.setEnabled(false);
            calcHashesButton.setSelected(false);
            xmlHandle.setCalculate(false);
        }
        calcHashesButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(calcHashesButton.isSelected()) {
                    xmlHandle.setCalculate(true);
                } else {
                    xmlHandle.setCalculate(false);
                }
            }
            
        });
        
        notableHashTable.setModel(knownBadTableModel);
        jTable1.setModel(nsrlTableModel);
        
        notableHashTable.setTableHeader(null);
        jTable1.setTableHeader(null);
        notableHashTable.setRowSelectionAllowed(false);
        jTable1.setRowSelectionAllowed(false);
        //customize column witdhs
        final int width1 = jScrollPane1.getPreferredSize().width;
        final int width2 = jScrollPane2.getPreferredSize().width;
        TableColumn column1 = null;
        TableColumn column2 = null;
        for (int i = 0; i < notableHashTable.getColumnCount(); i++) {
            column1 = notableHashTable.getColumnModel().getColumn(i);
            column2 = jTable1.getColumnModel().getColumn(i);
            if (i == 0) {
                column1.setPreferredWidth(((int) (width1 * 0.15)));
                column2.setPreferredWidth(((int) (width2 * 0.15)));
            } else {
                column1.setPreferredWidth(((int) (width1 * 0.84)));
                column2.setPreferredWidth(((int) (width2 * 0.84)));
            }
        }
        
        reloadSets();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        notableHashTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        calcHashesButton = new javax.swing.JCheckBox();

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        notableHashTable.setBackground(new java.awt.Color(240, 240, 240));
        notableHashTable.setShowHorizontalLines(false);
        notableHashTable.setShowVerticalLines(false);
        jScrollPane1.setViewportView(notableHashTable);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(HashDbSimplePanel.class, "HashDbSimplePanel.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(HashDbSimplePanel.class, "HashDbSimplePanel.jLabel2.text")); // NOI18N

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jTable1.setBackground(new java.awt.Color(240, 240, 240));
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jScrollPane2.setViewportView(jTable1);

        calcHashesButton.setText(org.openide.util.NbBundle.getMessage(HashDbSimplePanel.class, "HashDbSimplePanel.calcHashesButton.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(calcHashesButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(calcHashesButton)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox calcHashesButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable notableHashTable;
    // End of variables declaration//GEN-END:variables

    private void reloadSets() {
        nsrl = HashDbXML.getCurrent().getNSRLSet();
        nsrlTableModel.resync();
        knownBadTableModel.resync();
    }
    
    private class NSRLTableModel extends AbstractTableModel {
        
        private void resync() {
            fireTableDataChanged();
        }
        
        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return "";
            } else {
                if(nsrl == null) {
                    return "Not Configured";
                } else {
                    return nsrl.getName();
                }
            }
        }
    }

    private class HashTableModel extends AbstractTableModel {
        
        private HashDbXML xmlHandle = HashDbXML.getCurrent();
        
        private void resync() {
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            int size = xmlHandle.getKnownBadSets().size();
            return size == 0 ? 1 : size;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (xmlHandle.getKnownBadSets().isEmpty()) {
                if (columnIndex == 0) {
                    return "";
                } else {
                    return "Not Configured";
                }
            } else {
                HashDb db = xmlHandle.getKnownBadSets().get(rowIndex);
                if (columnIndex == 0) {
                    return db.getUseForIngest();
                } else {
                    return db.getName();
                }
            }
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return !ingestRunning && columnIndex == 0;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if(columnIndex == 0){
                HashDb db = xmlHandle.getKnownBadSets().get(rowIndex);
                if(((Boolean) getValueAt(rowIndex, columnIndex)) || IndexStatus.isIngestible(db.status())) {
                        db.setUseForIngest((Boolean) aValue);
                } else {
                        JOptionPane.showMessageDialog(HashDbSimplePanel.this, "Databases must be indexed before they can be used for ingest");
                }
            }
        }
        
        @Override
        public Class<?> getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        
    }
}
