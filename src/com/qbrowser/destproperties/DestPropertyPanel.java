/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qbrowser.destproperties;

import com.qbrowser.QBrowserV2;
import com.qbrowser.icons.QBrowserIconsFactory;
import com.qbrowser.util.QBrowserUtil;
import com.sun.messaging.jmq.admin.apps.broker.BrokerCmdOptionParser;
import com.sun.messaging.jmq.admin.apps.broker.BrokerCmdProperties;
import com.sun.messaging.jmq.util.DestLimitBehavior;
import com.sun.messaging.jmq.util.DestState;
import com.sun.messaging.jmq.util.DestType;
import com.sun.messaging.jmq.util.admin.DestinationInfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author takemura
 */
public class DestPropertyPanel {

    HashMap config_frames = new HashMap();
    //public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    static ResourceBundle resources = QBrowserV2.resources;
    static HashMap parameters = new HashMap();
    QBrowserV2 vqb2;
    static JFrame configFrame;




    public void showConfigPanel(DestProperty dp, QBrowserV2 qb2) {
        try {
        // Create popup
        vqb2 = qb2;
        if (dp == null) {
            return;
        }

        final String destname = dp.getDestName();


            //今回が初めての表示

           if (configFrame != null) {

               config_frames.remove(destname);
               configFrame.dispose();
               configFrame = null;
           }

           configFrame = new JFrame();

           //flag6.png
           Dimension d = new Dimension();

           d.setSize(600, 600);
           configFrame.setPreferredSize(d);
           configFrame.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "6.png").getImage());
           configFrame.setTitle(resources.getString("qkey.msg.msg382") + " " + destname);
           configFrame.setBackground(Color.white);
           configFrame.getContentPane().setLayout(new BorderLayout());

           JLabel expl = new JLabel(resources.getString("qkey.msg.msg383"));

            JPanel con_panel = new JPanel();

            GridBagLayout gbag = new GridBagLayout();
            con_panel.setLayout(gbag);
            GridBagConstraints vcs = new GridBagConstraints();

            DestinationInfo dsi = this.getStateOfDestination(dp.getDestType(),destname);


            JLabel dest_name = new JLabel(dsi.name);
            JLabel dest_type = new JLabel(DestType.toString(dsi.type));
            JLabel dest_status = new JLabel(DestState.toString(dsi.destState));


            JLabel current_message_number = new JLabel(String.valueOf(dsi.nMessages));
            JLabel current_messages_totalsize = new JLabel(String.valueOf(dsi.nMessageBytes));
            JLabel current_producers_number = new JLabel(String.valueOf(dsi.nProducers));
            JLabel current_activeconsumers_number = new JLabel(String.valueOf(dsi.naConsumers));
            JLabel current_backupconsumers_number = new JLabel(String.valueOf(dsi.nfConsumers));
            JLabel current_unackmessage_number = new JLabel(String.valueOf(dsi.nUnackMessages));

            


            //メッセージの最大数
            final JTextField max_messages = new JTextField(10);
            max_messages.setText(String.valueOf(dsi.maxMessages));


            JPanel max_messages_container = new JPanel();
            max_messages_container.setLayout(new BorderLayout());

            //unlimitedボタン
            JRadioButton jrb1 = new JRadioButton();
            JRadioButton jrb2 = new JRadioButton();
            ButtonGroup jbg = new ButtonGroup();
            jbg.add(jrb1);
            jbg.add(jrb2);
            final JLabel unlimited_label = new JLabel(resources.getString("qkey.msg.msg402"));



        jrb1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                max_messages.setEnabled(false);
                unlimited_label.setEnabled(true);
            }
        });

        jrb2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                unlimited_label.setEnabled(false);
                max_messages.setEnabled(true);
            }
        });

            if (dsi.maxMessages < 1) {
                jrb1.setSelected(true);
                max_messages.setEnabled(false);
                unlimited_label.setEnabled(true);
            } else {
                jrb2.setSelected(true);
                unlimited_label.setEnabled(false);
                max_messages.setEnabled(true);
            }


            GridBagLayout gbag2 = new GridBagLayout();
            max_messages_container.setLayout(gbag2);
            GridBagConstraints vcs2 = new GridBagConstraints();

            int count_CC = 0;
            QBrowserUtil.addRadioButtonAndValueComponent(vcs2, max_messages_container, gbag2, jrb1, unlimited_label, count_CC++);
            QBrowserUtil.addRadioButtonAndValueComponent(vcs2, max_messages_container, gbag2, jrb2, max_messages, count_CC++);



            //メッセージの最大合計サイズ
            final JTextField max_size_of_messages = new JTextField(10);
            long maxMS = dsi.maxMessageBytes;
            //if(maxMS < 1) {
            //    maxMS = -1;
            //}
            max_size_of_messages.setText(String.valueOf(maxMS));

            JPanel max_size_of_messages_container = new JPanel();
            max_size_of_messages_container.setLayout(new BorderLayout());

            //unlimitedラジオボタン
            JRadioButton jrb3 = new JRadioButton();
            JRadioButton jrb4 = new JRadioButton();
            ButtonGroup jbg2 = new ButtonGroup();
            jbg2.add(jrb3);
            jbg2.add(jrb4);
            final JLabel unlimited_label2 = new JLabel(resources.getString("qkey.msg.msg402"));

        jrb3.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                max_size_of_messages.setEnabled(false);
                unlimited_label2.setEnabled(true);
            }
        });

        jrb4.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                unlimited_label2.setEnabled(false);
                max_size_of_messages.setEnabled(true);
            }
        });

            if (dsi.maxMessageBytes < 1) {
                jrb3.setSelected(true);
                max_size_of_messages.setEnabled(false);
                unlimited_label2.setEnabled(true);
            } else {
                jrb4.setSelected(true);
                unlimited_label2.setEnabled(false);
                max_size_of_messages.setEnabled(true);
            }


            GridBagLayout gbag3 = new GridBagLayout();
            max_size_of_messages_container.setLayout(gbag3);
            GridBagConstraints vcs3 = new GridBagConstraints();

            int count_CC2 = 0;
            QBrowserUtil.addRadioButtonAndValueComponent(vcs3, max_size_of_messages_container, gbag3, jrb3, unlimited_label2, count_CC2++);
            QBrowserUtil.addRadioButtonAndValueComponent(vcs3, max_size_of_messages_container, gbag3, jrb4, max_size_of_messages, count_CC2++);

            //1メッセージ当たりの最大サイズ
            final JTextField max_size_per_1_message = new JTextField(10);
            long max1Bytes = dsi.maxMessageSize;

            if (max1Bytes == -1) {
             max_size_per_1_message.setText("0");
            } else {
             max_size_per_1_message.setText(String.valueOf(max1Bytes));
            }

            JPanel max_size_per_1_message_container = new JPanel();
            max_size_per_1_message_container.setLayout(new BorderLayout());

            //unlimitedボタン
            JRadioButton jrb5 = new JRadioButton();
            JRadioButton jrb6 = new JRadioButton();
            ButtonGroup jbg3 = new ButtonGroup();
            jbg3.add(jrb5);
            jbg3.add(jrb6);

            final JLabel unlimited_label3 = new JLabel(resources.getString("qkey.msg.msg402"));

        jrb5.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                max_size_per_1_message.setEnabled(false);
                unlimited_label3.setEnabled(true);
            }
        });

        jrb6.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                unlimited_label3.setEnabled(false);
                max_size_per_1_message.setEnabled(true);
            }
        });

            if (dsi.maxMessageSize < 1) {
                jrb5.setSelected(true);
                max_size_per_1_message.setEnabled(false);
                unlimited_label3.setEnabled(true);
            } else {
                jrb6.setSelected(true);
                unlimited_label3.setEnabled(false);
                max_size_per_1_message.setEnabled(true);
            }


            GridBagLayout gbag4 = new GridBagLayout();
            max_size_per_1_message_container.setLayout(gbag4);
            GridBagConstraints vcs4 = new GridBagConstraints();

            int count_CC3 = 0;
            QBrowserUtil.addRadioButtonAndValueComponent(vcs4, max_size_per_1_message_container, gbag4, jrb5, unlimited_label3, count_CC3++);
            QBrowserUtil.addRadioButtonAndValueComponent(vcs4, max_size_per_1_message_container, gbag4, jrb6, max_size_per_1_message, count_CC3++);

            //プロデューサの最大数
            final JTextField max_producers = new JTextField(10);
            max_producers.setText(String.valueOf(dsi.maxProducers));

            JPanel max_producers_container = new JPanel();
            //max_producers_container.setLayout(new BorderLayout());

            //unlimitedボタン
            JRadioButton jrb7 = new JRadioButton();
            JRadioButton jrb8 = new JRadioButton();
            ButtonGroup jbg4 = new ButtonGroup();
            jbg4.add(jrb7);
            jbg4.add(jrb8);

            final JLabel unlimited_label4 = new JLabel(resources.getString("qkey.msg.msg402"));

        jrb7.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                max_producers.setEnabled(false);
                unlimited_label4.setEnabled(true);
            }
        });

        jrb8.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                unlimited_label4.setEnabled(false);
                max_producers.setEnabled(true);
            }
        });

            if (dsi.maxProducers < 1) {
                jrb7.setSelected(true);
                max_producers.setEnabled(false);
                unlimited_label4.setEnabled(true);
            } else {
                jrb8.setSelected(true);
                unlimited_label4.setEnabled(false);
                max_producers.setEnabled(true);
            }


            GridBagLayout gbag5 = new GridBagLayout();
            max_producers_container.setLayout(gbag5);
            GridBagConstraints vcs5 = new GridBagConstraints();

            int count_CC4 = 0;
            QBrowserUtil.addRadioButtonAndValueComponent(vcs5, max_producers_container, gbag5, jrb7, unlimited_label4, count_CC4++);
            QBrowserUtil.addRadioButtonAndValueComponent(vcs5, max_producers_container, gbag5, jrb8, max_producers, count_CC4++);

            //アクティブなコンシューマの最大数
            final JTextField max_active_consumers = new JTextField(10);
            max_active_consumers.setText(String.valueOf(dsi.maxActiveConsumers));

            JPanel max_active_consumers_container = new JPanel();
            //max_producers_container.setLayout(new BorderLayout());

            //unlimitedボタン
            JRadioButton jrb9 = new JRadioButton();
            JRadioButton jrb10 = new JRadioButton();
            ButtonGroup jbg5 = new ButtonGroup();
            jbg5.add(jrb9);
            jbg5.add(jrb10);

            final JLabel unlimited_label5 = new JLabel(resources.getString("qkey.msg.msg402"));

        jrb9.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                max_active_consumers.setEnabled(false);
                unlimited_label5.setEnabled(true);
            }
        });

        jrb10.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                unlimited_label5.setEnabled(false);
                max_active_consumers.setEnabled(true);
            }
        });

            if (dsi.maxActiveConsumers < 1) {
                jrb9.setSelected(true);
                max_active_consumers.setText("0");
                max_active_consumers.setEnabled(false);
                unlimited_label5.setEnabled(true);
            } else {
                jrb10.setSelected(true);
                unlimited_label5.setEnabled(false);
                max_active_consumers.setEnabled(true);
            }


            GridBagLayout gbag6 = new GridBagLayout();
            max_active_consumers_container.setLayout(gbag6);
            GridBagConstraints vcs6 = new GridBagConstraints();

            int count_CC5 = 0;
            QBrowserUtil.addRadioButtonAndValueComponent(vcs6, max_active_consumers_container, gbag6, jrb9, unlimited_label5, count_CC5++);
            QBrowserUtil.addRadioButtonAndValueComponent(vcs6, max_active_consumers_container, gbag6, jrb10, max_active_consumers, count_CC5++);

            //バックアップコンシューマの最大数
            final JTextField max_backup_consumers = new JTextField(10);
            max_backup_consumers.setText(String.valueOf(dsi.maxFailoverConsumers));

            JPanel max_backup_consumers_container = new JPanel();
            //max_producers_container.setLayout(new BorderLayout());

            //unlimitedボタン
            JRadioButton jrb11 = new JRadioButton();
            JRadioButton jrb12 = new JRadioButton();
            ButtonGroup jbg6 = new ButtonGroup();
            jbg6.add(jrb11);
            jbg6.add(jrb12);

            final JLabel unlimited_label6 = new JLabel(resources.getString("qkey.msg.msg402"));

        jrb11.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                max_backup_consumers.setEnabled(false);
                unlimited_label5.setEnabled(true);
            }
        });

        jrb12.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                unlimited_label6.setEnabled(false);
                max_backup_consumers.setEnabled(true);
            }
        });

            if (dsi.maxFailoverConsumers < 0) {
                jrb11.setSelected(true);
                max_backup_consumers.setText("0");
                max_backup_consumers.setEnabled(false);
                unlimited_label6.setEnabled(true);
            } else {
                jrb12.setSelected(true);
                unlimited_label6.setEnabled(false);
                max_backup_consumers.setEnabled(true);
            }


            GridBagLayout gbag7 = new GridBagLayout();
            max_backup_consumers_container.setLayout(gbag7);
            GridBagConstraints vcs7 = new GridBagConstraints();

            int count_CC6 = 0;
            QBrowserUtil.addRadioButtonAndValueComponent(vcs7, max_backup_consumers_container, gbag7, jrb11, unlimited_label6, count_CC6++);
            QBrowserUtil.addRadioButtonAndValueComponent(vcs7, max_backup_consumers_container, gbag7, jrb12, max_backup_consumers, count_CC6++);

            //制限動作
            JComboBox limit_behavior = new JComboBox();
            limit_behavior.addItem("REJECT_NEWEST");
            limit_behavior.addItem("FLOW_CONTROL");
            limit_behavior.addItem("REMOVE_OLDEST");
            limit_behavior.addItem("REMOVE_LOW_PRIORITY");

            limit_behavior.setSelectedItem(DestLimitBehavior.getString(dsi.destLimitBehavior));

            //デッドメッセージキューを使うか
            JCheckBox  use_DMQ = new JCheckBox();
            if (dsi.useDMQ()) {
              use_DMQ.setSelected(true);
            }


            HashMap ps = new HashMap();
            ps.put("dest_name", dest_name.getText());
            ps.put("dest_type", dest_type.getText());
            ps.put("max_messages", max_messages);
            ps.put("max_size_of_messages", max_size_of_messages);
            ps.put("max_size_per_1_message", max_size_per_1_message);
            ps.put("max_producers", max_producers);
            ps.put("max_active_consumers", max_active_consumers);
            ps.put("max_backup_consumers", max_backup_consumers);
            ps.put("limit_behavior", limit_behavior);
            ps.put("use_DMQ", use_DMQ);

            ps.put("jrb1", jrb1);
            //ps.put("jrb2", jrb2);
            ps.put("jrb3", jrb3);
            //ps.put("jrb4", jrb4);
            ps.put("jrb5", jrb5);
            //ps.put("jrb6", jrb6);
            ps.put("jrb7", jrb7);
            //ps.put("jrb8", jrb8);
            ps.put("jrb9", jrb9);
            //ps.put("jrb10", jrb10);
            ps.put("jrb11", jrb11);
            //ps.put("jrb12", jrb12);



            parameters.put(destname, ps);

            int countY = 0;
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg384") + "  ", dest_name, countY++);
            con_panel.setBorder(BorderFactory.createEtchedBorder());
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg385") + "  ", dest_type, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg386") + "  ", dest_status, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg387") + "  ", current_message_number, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg388") + "  ", current_messages_totalsize, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg389") + "  ", current_producers_number, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg390") + "  ", current_activeconsumers_number, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg391") + "  ", current_backupconsumers_number, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg401") + "  ", current_unackmessage_number, countY++);
            QBrowserUtil.addBlankRow(vcs, con_panel, gbag, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg392") + "  ", max_messages_container, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg393") + "  ", max_size_of_messages_container, countY++);
            
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg394") + "  ", max_size_per_1_message_container, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg395") + "  ", max_producers_container, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg396") + "  ", max_active_consumers_container, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg397") + "  ", max_backup_consumers_container, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg398") + "  ", limit_behavior, countY++);
            //QBrowserUtil.addBlankRow(vcs, con_panel, gbag, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg399") + "  ", use_DMQ, countY++);


            configFrame.getContentPane().add(BorderLayout.NORTH, expl);
            configFrame.getContentPane().add(BorderLayout.CENTER, con_panel);
            JButton okbutton1 = new JButton("                            " + resources.getString("qkey.msg.msg400") + "                              ");
            okbutton1.addActionListener(new ConfigOKListener(destname));
            JButton cancelbutton = new JButton("  " + resources.getString("qkey.msg.msg008") + "  ");
            cancelbutton.addActionListener(new ConfigCancelListener(destname));

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, okbutton1);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            JPanel temppanel = new JPanel();
            temppanel.setLayout(new BorderLayout());
            temppanel.add(BorderLayout.SOUTH, pbuttonpanel);

            configFrame.getContentPane().add(BorderLayout.SOUTH, temppanel);
            configFrame.pack();

            config_frames.put(destname, configFrame);

           configFrame.setLocationRelativeTo(qb2);
           configFrame.setVisible(true);
     
        
        } catch (Exception e) {
            //QBrowserUtil.popupErrorMessageDialog(e, qb2);
            JTextArea jta = new JTextArea();
            jta.append(resources.getString("qkey.msg.err.Q0028"));

            //qb2.popupMessageDialog("error", jta, null)
            qb2.popupMessageDialog(resources.getString("qkey.msg.err.Q0027"), jta,
                                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));
        }


    }


    class ConfigOKListener implements ActionListener {

        String dest_name;

        public ConfigOKListener(String value) {
            dest_name = value;
        }

       boolean checkIfLongValue(String value) {
           try {

             Long.parseLong(value);

           } catch (Exception e) {
               return false;
           }

           return true;
       }

       String internalruncommand(String destType, String targetname, String command_body , StringBuffer result)  {

            String cmd = "update dst -t " + destType + " -n " + targetname + " -b " + QBrowserV2.serverHost + ":" + QBrowserV2.serverPort + " -u " + QBrowserV2.serverUser + " -passfile ";
            //System.out.println(cmd);
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(QBrowserV2.real_passfile_path);

            StringTokenizer st2 = new StringTokenizer(command_body);
            while (st2.hasMoreTokens()) {
                ar.add(st2.nextToken());
            }

            String[] args = new String[ar.size()];
            ar.toArray(args);

            //
            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                oe.printStackTrace();
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);
            return exitcode;
        }

        public void actionPerformed(ActionEvent e) {

            //System.out.println("UPDATE Button pushed.");

            JFrame frame = (JFrame)config_frames.get(dest_name);

            HashMap ps = (HashMap)parameters.get(dest_name);

            StringBuilder sbs = new StringBuilder();

            //最大メッセージ数
            //jrb1とjrb2のラジオボタンをまずチェック
            JRadioButton jrb1 = (JRadioButton)ps.get("jrb1");

            //もし、jrb1(unlimited)が選択されていれば-1にセット
            if (jrb1.isSelected()) {
                sbs.append("-f -o maxNumMsgs=-1 ");
            } else {

                JTextField max_messages = (JTextField) ps.get("max_messages");
                if (max_messages.getText().length() == 0) {
                    QBrowserUtil.popupErrorMessageDialog(new Exception( resources.getString("qkey.msg.err.Q0005")), frame.getRootPane());
                    return;
                }



                if (!checkIfLongValue(max_messages.getText())) {
                  QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.msg392") + max_messages.getText() + "\n\n" + resources.getString("qkey.msg.err.Q0026")), frame.getRootPane());
                    return;
                }

                sbs.append("-f -o maxNumMsgs=" + max_messages.getText() + " ");

            }

            JRadioButton jrb3 = (JRadioButton)ps.get("jrb3");

            if (jrb3.isSelected()) {
                sbs.append("-o maxTotalMsgBytes=0 ");
            } else {

                JTextField max_size_of_messages = (JTextField) ps.get("max_size_of_messages");
                if (max_size_of_messages.getText().length() == 0) {
                    QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.err.Q0005")), frame.getRootPane());
                    return;
                }

                sbs.append("-o maxTotalMsgBytes=" + max_size_of_messages.getText() + " ");

            }

            JRadioButton jrb5 = (JRadioButton)ps.get("jrb5");

            if (jrb5.isSelected()) {
                sbs.append("-o maxBytesPerMsg=-1 ");
            } else {
                JTextField max_size_per_1_message = (JTextField) ps.get("max_size_per_1_message");
                if (max_size_per_1_message.getText().length() == 0) {
                    QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.err.Q0005")), frame.getRootPane());
                    return;
                }

                sbs.append("-o maxBytesPerMsg=" + max_size_per_1_message.getText() + " ");

            }

            JRadioButton jrb7 = (JRadioButton) ps.get("jrb7");

            if (jrb7.isSelected()) {
                sbs.append("-o maxNumProducers=-1 ");
            } else {
                JTextField max_producers = (JTextField) ps.get("max_producers");
                if (max_producers.getText().length() == 0) {
                    QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.err.Q0005")), frame.getRootPane());
                    return;
                }

                if (!checkIfLongValue(max_producers.getText())) {
                  QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.msg395") + max_producers.getText() + "\n\n" + resources.getString("qkey.msg.err.Q0026")), frame.getRootPane());
                    return;
                }

                sbs.append("-o maxNumProducers=" + max_producers.getText() + " ");
            }

            JRadioButton jrb9 = (JRadioButton) ps.get("jrb9");

            if (jrb9.isSelected()) {
                sbs.append("-o maxNumActiveConsumers=-1 ");
            } else {
                JTextField max_active_consumers = (JTextField) ps.get("max_active_consumers");
                if (max_active_consumers.getText().length() == 0) {
                    QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.err.Q0005")), frame.getRootPane());
                    return;
                }

                if (!checkIfLongValue(max_active_consumers.getText())) {
                  QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.msg396") + max_active_consumers.getText() + "\n\n" + resources.getString("qkey.msg.err.Q0026")), frame.getRootPane());
                    return;
                }

                sbs.append("-o maxNumActiveConsumers=" + max_active_consumers.getText() + " ");
            }

            JRadioButton jrb11 = (JRadioButton) ps.get("jrb11");

            if (jrb11.isSelected()) {
                sbs.append("-o maxNumBackupConsumers=-1 ");
            } else {
                JTextField max_backup_consumers = (JTextField) ps.get("max_backup_consumers");
                if (max_backup_consumers.getText().length() == 0) {
                    QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.err.Q0005")), frame.getRootPane());
                    return;
                }

                if (!checkIfLongValue(max_backup_consumers.getText())) {
                  QBrowserUtil.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.msg397") + max_backup_consumers.getText() + "\n\n" + resources.getString("qkey.msg.err.Q0026")), frame.getRootPane());
                    return;
                }

                sbs.append("-o maxNumBackupConsumers=" + max_backup_consumers.getText() + " ");
            }

            JComboBox limit_behavior = (JComboBox)ps.get("limit_behavior");

            sbs.append("-o limitBehavior=" + limit_behavior.getSelectedItem().toString() + " ");


            JCheckBox use_DMQ = (JCheckBox)ps.get("use_DMQ");
            sbs.append("-o useDMQ=" + use_DMQ.isSelected() + " ");
            
            String dest_name_v = (String)ps.get("dest_name");
            String dest_type_v = (String)ps.get("dest_type");

            String cmd_type = "t";
            if (dest_type_v.trim().equalsIgnoreCase("QUEUE")) {
                cmd_type = "q";
            }

            StringBuffer errorst = new StringBuffer();
            String exitcode = internalruncommand(cmd_type, dest_name_v, sbs.toString(), errorst);
            //System.out.println(exitcode);
            int exit_code_int = -1;
            try {
                exit_code_int = Integer.parseInt(exitcode);
            } catch (Exception eeie) {}
            //System.out.println("exitcode = " + exit_code_int);
            if (exit_code_int != 0) {
             TextArea ta = new TextArea("", 10, 50, TextArea.SCROLLBARS_NONE);
             ta.setEditable(false);
             ta.append(errorst.toString());
             ta.append("\n");
                vqb2.popupMessageDialog("ERROR", ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));
                
            }

            frame.setVisible(false);
            


        }
    }

    class ConfigCancelListener implements ActionListener {

        String dest_name;

        public ConfigCancelListener(String vdest_name) {
            dest_name = vdest_name;
        }

        public void actionPerformed(ActionEvent e) {

           JFrame frame = (JFrame)config_frames.get(dest_name);
           frame.setVisible(false);

        }
    }


      public DestinationInfo getStateOfDestination(String destType, String targetname) {

            if (destType.equals(QBrowserV2.QUEUE_LITERAL)) {
                destType = "q";
            } else if (destType.equals(QBrowserV2.TOPIC_LITERAL)) {
                destType = "t";
            }

            DestinationInfo retval = null;
            String cmd = "query dst -t " + destType + " -n " + targetname + " -b " + QBrowserV2.serverHost + ":" + QBrowserV2.serverPort + " -u " + QBrowserV2.serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(QBrowserV2.real_passfile_path);

            String[] args = new String[ar.size()];
            ar.toArray(args);

            //
            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            retval = cmdRunner.getSpecifiedDestinationInfo();

            return retval;
        }


}

