/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qbrowser.tree;

import com.jidesoft.swing.ResizablePanel;
import com.qbrowser.QBrowserV2;
import com.qbrowser.icons.QBrowserIconsFactory;
import com.qbrowser.localstore.LocalStoreProperty;
import com.qbrowser.util.QBrowserUtil;
import java.awt.Component;
import javax.swing.JScrollPane;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.ImageIcon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class TreeIconPanel extends ResizablePanel
                          implements TreeSelectionListener {

    private JTree tree;
    private static boolean DEBUG = false;
    DefaultMutableTreeNode top = null;
    String hostandport = null;
    QBrowserV2 qb;
    JScrollPane treeView;

    public TreeIconPanel(String vhostandport, QBrowserV2 vqb) {
        //super(new GridLayout(1,0));
        qb = vqb;
        hostandport = vhostandport;
        //Create the nodes.
        top =
        //     new DefaultMutableTreeNode(hostandport);
          new DefaultMutableTreeNode(new DestInfo(hostandport,"BKR",null));

        createNodes(top);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);

        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Set the icon for leaf nodes.
        //ImageIcon leafIcon = createImageIcon("images/middle.gif");
        ImageIcon leafIconTPC = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Notsubscribed_topic);
        ImageIcon leafIconQUE = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "1.png");
        ImageIcon leafIconLS = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "7.png");
        ImageIcon globeIcon = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Globe);
        ImageIcon foIcon = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.FolderOpen);
        ImageIcon fcIcon = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.FolderClose);
        ImageIcon qIconp = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paused_queue);
        ImageIcon tIconp = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paused_topic);
        ImageIcon lIconp = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paused_localstore);
        ImageIcon tIcons = QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Subscribed_topic);
        QBTreeRenderer qbt = new QBTreeRenderer(tIcons, qIconp,tIconp,lIconp,foIcon, fcIcon,globeIcon ,leafIconQUE, leafIconTPC, leafIconLS);
        //DefaultTreeCellEditor dtce = new DefaultTreeCellEditor(tree, qbt);
        tree.setCellRenderer(qbt);
       

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        //Create the scroll pane and add the tree to it.
        treeView = new JScrollPane(tree);
        treeView.setBorder(BorderFactory.createEmptyBorder());


        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        add(treeView);
        setBorder(BorderFactory.createEmptyBorder());
    }

    public JScrollPane getTreeView() {
        return treeView;
    }

    public JTree getTree() {
        return tree;
    }


    public TreePath findTreePath(String dest_name, String dest_type) {
       tree.getRowCount();

       for (int i = 0 ; i < tree.getRowCount() ; i++) {
         TreePath tp = tree.getPathForRow(i);
         Object[] obj = tp.getPath();
         for (int j = 0 ; j < obj.length ; j++) {
             DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)obj[j];
             DestInfo di = (DestInfo)dmtn.getUserObject();
             
             if (di.destinationName.equals(dest_name) && di.destinationType.equals(dest_type)) {
                 //System.out.println("DN:" + di.destinationName + " DT:" + di.destinationType);
                 return tp;
             }
         }

       }

       return null;
    }


    public void refreshTree() {
        top.removeAllChildren();
        createNodes(top);
        tree.updateUI();
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public DestInfo getSelectedDestInfo() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node != null) {

        Object nodeInfo = node.getUserObject();
        if (nodeInfo instanceof DestInfo) {
            DestInfo dest = (DestInfo)nodeInfo;
            return dest;

        } else {
            return null;
        }

        } else {
            return null;
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            DestInfo dest = (DestInfo)nodeInfo;
        } 
    }

    public static class DestInfo {
        public String destinationName;
        public String destinationType;
        public String name_with_suffix;
        public String parent_with_suffix;

        public DestInfo(String vdest, String vtype, String vname_with_suffix) {
            destinationName = vdest;
            destinationType = vtype;
            name_with_suffix = vname_with_suffix;
        }
        

        @Override
        public String toString() {
            return destinationName;
        }
    }

    private class QBTreeRenderer extends DefaultTreeCellRenderer {
        Icon queueIcon;
        Icon topicIcon;
        Icon localstoreIcon;
        Icon globeIcon;
        Icon foIcon;
        Icon fcIcon;
        Icon queueIconp;
        Icon topicIconp;
        Icon lsIconp;
        Icon topicIcons;

        public QBTreeRenderer(Icon vtopicIcons, Icon vqueueIconp,Icon vtopicIconp, Icon vlsIconp ,Icon vfoIcon, Icon vfcIcon, Icon vglobeIcon, Icon vqueueIcon, Icon vtopicIcon, Icon vlocalstoreIcon) {
            queueIcon = vqueueIcon;
            topicIcon = vtopicIcon;
            localstoreIcon = vlocalstoreIcon;
            globeIcon = vglobeIcon;
            foIcon = vfoIcon;
            fcIcon = vfcIcon;
            queueIconp = vqueueIconp;
            topicIconp = vtopicIconp;
            lsIconp = vlsIconp;
            topicIcons = vtopicIcons;
        }

        @Override
        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean hasFocus) {

            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            hasFocus);
            //setPreferredSize(new Dimension(yokohaba, 20));
            if (row == 0) {

                setIcon(globeIcon);
                
            } else if (isQueue(value)) {
                //call_kaisu++;
                //System.out.println("isQueue" + call_kaisu);
                //if (isPaused(value)) {
               //   setIcon(queueIconp);
               // } else {
                  setIcon(queueIcon);
               // }
                setToolTipText("Queue");
            } else if (isTopic(value)){
                //if (isPaused(value)) {
                //  setIcon(topicIconp);
                //} else
                if (isSubscribed(value)){
                  setIcon(topicIcons);
                } else {
                  setIcon(topicIcon);
                }
                setToolTipText("Topic");
            } else if (isLocalStore(value)) {
                if (isLocalStorePaused(value)) {
                  setIcon(lsIconp);
                } else {
                  setIcon(localstoreIcon);
                }
                setToolTipText("LocalStore");
            } else if (!leaf) {
                setOpenIcon(foIcon);
                setClosedIcon(fcIcon);
            }

            return this;
        }

        protected boolean isQueue(Object value) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;
            
            Object obj = node.getUserObject();

            if (obj instanceof DestInfo) {

            DestInfo nodeInfo =
                    (DestInfo)obj;
            String type = nodeInfo.destinationType;
            if (type.equals(QBrowserV2.QUEUE_LITERAL)) {
                return true;
            }

            }

            return false;
        }

        /*
        protected boolean isPaused(Object value) {

            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;

            Object obj = node.getUserObject();

            if (obj instanceof DestInfo) {

              DestInfo nodeInfo =
                    (DestInfo)obj;
              String state = qb.getStateOfDestination(nodeInfo.destinationType, nodeInfo.destinationName);
              if (state.equals("PAUSED")) {
                  return true;
              }

            }

            return false;

        }
        */

        protected boolean isLocalStorePaused(Object value) {

            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;

            Object obj = node.getUserObject();

            if (obj instanceof DestInfo) {

              DestInfo nodeInfo =
                    (DestInfo)obj;
              try {
                if (!QBrowserV2.lsm.getLocalStoreProperty(nodeInfo.destinationName).isValid()) {
                    return true;
                } else {
                    return false;
                }
              } catch (Throwable thex) {
                  return false;
              }

            }

            return false;

        }

        protected boolean isSubscribed(Object value) {

            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;

            Object obj = node.getUserObject();

            if (obj instanceof DestInfo) {

              DestInfo nodeInfo =
                    (DestInfo)obj;

              if( qb.isSubscriberThreadRunning(nodeInfo.name_with_suffix)) {
                  return true;
              } else {
                  return false;
              }

            }

            return false;

        }

        protected boolean isTopic(Object value) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;

            Object obj = node.getUserObject();

            if (obj instanceof DestInfo) {

            DestInfo nodeInfo =
                    (DestInfo)(node.getUserObject());
            String type = nodeInfo.destinationType;
            if (type.equals(QBrowserV2.TOPIC_LITERAL) || type.equals(QBrowserV2.CHILD_TOPIC_LITERAL)) {
                return true;
            }

            }

            return false;
        }

        protected boolean isLocalStore(Object value) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)value;

            Object obj = node.getUserObject();

            if (obj instanceof DestInfo) {

            DestInfo nodeInfo =
                    (DestInfo)(node.getUserObject());
            String type = nodeInfo.destinationType;
            if (type.equals(QBrowserV2.LOCAL_STORE_LITERAL) || type.equals(QBrowserV2.CHILD_LOCAL_STORE_LITERAL)) {
                return true;
            }

            }

            return false;
        }
    }



    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode dest = null;

        category = new DefaultMutableTreeNode(new DestInfo("Queue","FD",null));
        top.add(category);


        for (int i = 0; i < QBrowserV2.destinationNamesForDisplayQueue.size() ; i++) {

          String qn = (String)QBrowserV2.destinationNamesForDisplayQueue.get(i);
          dest = new DefaultMutableTreeNode(new DestInfo
            (qn , QBrowserV2.QUEUE_LITERAL, qn + QBrowserV2.QUEUE_SUFFIX));
          category.add(dest);

        }


        category = new DefaultMutableTreeNode(new DestInfo("Topic", "FD", null));
        top.add(category);

        for (int i = 0; i < QBrowserV2.destinationNamesForDisplayTopic.size() ; i++) {

          String tn = (String)QBrowserV2.destinationNamesForDisplayTopic.get(i);
          dest = new DefaultMutableTreeNode(new DestInfo
            (tn, QBrowserV2.TOPIC_LITERAL, tn + QBrowserV2.TOPIC_SUFFIX));
          category.add(dest);

          String dest_with_suffix = tn + QBrowserV2.TOPIC_SUFFIX;

          //関連するローカルストアを出す
            ArrayList local_dests = QBrowserV2.lsm.getCopyToListOfTheDestination(dest_with_suffix);
            if (local_dests != null) {
                for (int j = 0; j < local_dests.size(); j++) {
                    String local_dest = (String) local_dests.get(j);
                    if (qb.isSubscriberThreadRunning(dest_with_suffix)){
                        //サブスクライブ中 = 配下のローカルストアも有効
                        DestInfo vdesti = new DestInfo
                           (local_dest, QBrowserV2.CHILD_LOCAL_STORE_LITERAL, local_dest + QBrowserV2.LOCAL_STORE_SUFFIX);
                        vdesti.parent_with_suffix = dest_with_suffix;
                        DefaultMutableTreeNode  ldest = new DefaultMutableTreeNode(vdesti);
                        dest.add(ldest);
                    } else {
                        //NOP
                    }
                }

            }

        }
        
        category = new DefaultMutableTreeNode(new DestInfo("LocalStore", "FD", null));
        top.add(category);

        Collection lspcol = QBrowserV2.lsm.listLocalStoreProperties();
        Iterator ilspcol = lspcol.iterator();
        while (ilspcol.hasNext()) {
            LocalStoreProperty lsp = (LocalStoreProperty) ilspcol.next();
            dest = new DefaultMutableTreeNode(new DestInfo
            (lsp.getDestName(), QBrowserV2.LOCAL_STORE_LITERAL, lsp.getDestName() + QBrowserV2.LOCAL_STORE_SUFFIX));
          category.add(dest);

          //ListenしているTopicを出す

            Iterator ilsp = lsp.getFromDests().keySet().iterator();
            while (ilsp.hasNext()) {
                String listen_dest_name_with_suffix = (String) ilsp.next();
                String listen_dest_name_without_suffix = QBrowserUtil.getPureDestName(listen_dest_name_with_suffix);
                if (QBrowserV2.destinationNamesForDisplayTopic.contains(listen_dest_name_without_suffix)) {
                  DestInfo vdesti = new DestInfo
                           (listen_dest_name_without_suffix, QBrowserV2.CHILD_TOPIC_LITERAL, listen_dest_name_with_suffix);
                  vdesti.parent_with_suffix = lsp.getDestNameWithSuffix();
                  DefaultMutableTreeNode  sdest = new DefaultMutableTreeNode(vdesti);
                  dest.add(sdest);
                } else {
                    //トピックがなくなってしまっている場合は関連をここで消す。
                    try {
                      lsp.removeFromDests(listen_dest_name_with_suffix);
                      QBrowserV2.lsm.updateAndSaveLocalStoreProperty(lsp);
                      QBrowserV2.lsm.removeRelatedEntryOfSubscribeDest(listen_dest_name_with_suffix);
                    } catch (Exception removee) {
                        removee.printStackTrace();
                    }
                }

            }

        }
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TreeIconPanel.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

}
