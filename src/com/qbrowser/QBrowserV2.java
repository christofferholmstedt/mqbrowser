/* Copyright (C) 2000-2009

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA */
package com.qbrowser;

/*
 * @(#)QBrowserV2.java
 *
 */
import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.swing.AutoResizingTextArea;
import com.jidesoft.swing.FolderChooser;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.JideTabbedPane;
import com.jidesoft.swing.ResizablePanel;
import com.jidesoft.swing.Searchable;
import com.jidesoft.swing.SearchableBar;
import com.jidesoft.swing.SearchableUtils;
import com.qbrowser.QBrowserV2.FileChooseOKListener3;
import com.qbrowser.QBrowserV2.FileLoadingButtonListener2;
import com.qbrowser.QBrowserV2.MapMessageTypeComboBoxItemListener;
import com.qbrowser.QBrowserV2.StreamMessageTypeComboBoxItemListener;
import com.qbrowser.clipboard.ClipBoardManager;
import com.qbrowser.consumer.table.MessageRecordProperty;
import com.qbrowser.consumer.table.MessageRecordTable;
import com.qbrowser.container.MessageContainer;
import com.qbrowser.destproperties.DestProperty;
import com.qbrowser.destproperties.DestPropertyPanel;
import com.qbrowser.display.DisplayDialogThreadPool;
import com.qbrowser.display.DisplayDialogThreadPoolForShowDetails;
import com.qbrowser.display.DisplayMsgDialogFactory;
import com.qbrowser.editor.DownloadCellEditor;
import com.qbrowser.editor.ListCellEditor;
import com.qbrowser.icons.QBrowserIconsFactory;
import com.qbrowser.key.OSDetector;
import com.qbrowser.localstore.LocalMessageContainer;
import com.qbrowser.localstore.LocalMsgTable;
import com.qbrowser.localstore.LocalStoreConfigPanel;
import com.qbrowser.localstore.LocalStoreManager;
import com.qbrowser.localstore.LocalStoreProperty;
import com.qbrowser.localstore.genericdest.LocalDestination;
import com.qbrowser.localstore.genericdest.LocalQueue;
import com.qbrowser.localstore.genericdest.LocalTopic;
import com.qbrowser.persist.BytesMessagePersister;
import com.qbrowser.persist.BytesMessageReader;
import com.qbrowser.persist.MapMessagePersister;
import com.qbrowser.persist.MapMessageReader;
import com.qbrowser.persist.MessagePersister;
import com.qbrowser.persist.ObjectMessagePersister;
import com.qbrowser.persist.ObjectMessageReader;
import com.qbrowser.persist.PersistedMessageReader;
import com.qbrowser.persist.StreamMessagePersister;
import com.qbrowser.persist.StreamMessageReader;
import com.qbrowser.persist.TextMessagePersister;
import com.qbrowser.persist.TextMessageReader;
import com.qbrowser.property.BytesForDownloadPropertyTable;
import com.qbrowser.property.HeaderPropertyTable;
import com.qbrowser.property.InputProperty;
import com.qbrowser.property.MapMessageAllProperties;
import com.qbrowser.property.MapMessageAllPropertiesTable;
import com.qbrowser.property.MapMessageInputProperty;
import com.qbrowser.property.MapMessageInputTable;
import com.qbrowser.property.PropTableCellEditor;
import com.qbrowser.property.Property;
import com.qbrowser.property.PropertyInputTable;
import com.qbrowser.property.PropertyUtil;
import com.qbrowser.property.QBrowserPropertyException;
import com.qbrowser.property.ReadOnlyHeaderPropertyTable;
import com.qbrowser.property.ReadOnlyPropertyTable;
import com.qbrowser.property.StreamMessageAllProperties;
import com.qbrowser.property.StreamMessageAllPropertiesTable;
import com.qbrowser.property.StreamMessageBytesForDownloadPropertyTable;
import com.qbrowser.property.StreamMessageInputProperty;
import com.qbrowser.property.StreamMessageInputTable;
import com.qbrowser.render.CellRenderer0;
import com.qbrowser.render.CellRenderer1;
import com.qbrowser.render.DownloadCellRenderer;
import com.qbrowser.render.HeaderRenderer01;
import com.qbrowser.render.StripeTableRenderer;
import com.qbrowser.render.StripeTableRendererForProperty;
import com.qbrowser.table.QBTable;
import com.qbrowser.tree.TreeIconPanel;
import com.qbrowser.tree.TreeIconPanel.DestInfo;
import com.qbrowser.util.QBrowserCache;
import com.qbrowser.util.QBrowserUtil;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import com.sun.messaging.jmq.Version;
import com.sun.messaging.jmq.admin.apps.broker.BrokerCmdOptionParser;
import com.sun.messaging.jmq.admin.apps.broker.BrokerCmdProperties;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.util.*;
import java.text.*;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MenuEvent;
import javax.swing.table.*;

import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.jms.MessageConsumer;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.DeliveryMode;
import javax.jms.StreamMessage;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.BytesMessage;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.jms.MessageEOFException;
import javax.jms.MessageProducer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CellEditorListener;
import javax.swing.event.MenuListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import jp.sun.util.BrokerUtil;
import jp.sun.util.NewMessageInfo;

/**
 * The QBrowserV2 is a GUI application that lets you visually
 * examine the contents of a JMS Queue. It is written using javax.swing.
 * By default QBrowserV2 will connect to the imqbrokerd running
 * on localhost:7676. You can use the -b and -p options to change
 * the host and port:
 *
 *     java QBrowserV2 -b localhost -p 7676 [-user admin] [-password admin]
 *
 * 2007/08  modified by takemura@sun microsystems
 * add delete / create new message / send new message functionality
 * 2008/03/31 modified by takemura@sun microsystems
 * fix message deletion func to apply multiple session.
 * new message destination defaults to a destination name selected on QBrowser main panel.
 * after sending new message(s), the queue to whitch new messages are sent will be browsed automatically.
 * enlarge OK button on progress confirmation popup panel.
 * modify progress confirmation popup window like 'tail -f'
 * when destination selected on main panel, the contents of the destination will be browsed automatically.
 * fix when close a delete confirmation window using X button, currentDeleteTarget should be cleared.
 * 2008/04/01 modified by takemura@sun microsystems
 * modify to be able to call various imqcmd from QBrowser
 * revision upgrade to 2.1.0 due to addition of several new functionality.
 * 2008/04/03
 * retrofit reconnection func from ver 2.0.1.
 * ability to indicate username and password externally.
 * add command template dynamically created for the selected destination.
 * 2009/04/18
 * add message selection to destination browsing.
 * 2009/05/04
 * renew whole UI using JIDE
 * add tabbed message panels.
 * - add automatic table refresh and display when destination name in dest combo box is selected.
 * add auto complition for Queue name when ': Queue' is omitted.
 * Now destination name combo box can display TOPIC name when subscriber is running.
 * add destination list refresh functionality.
 * speed up message deletion process.
 * add TOPIC subscription , start / stop subscribing
 * select all menu will change to deselect when all rows are already selected.
 * Look and Feel can be changed dynamically.
 * add Forward Message functionality
 * can now be used with WebLogic MQ (use run_wls_mq.bat)
 * i18n
 * other bug fixes.
 * 2009/05/10 (2.3.0.6)
 * add delivery mode selection box to new message panel.
 * add compress mode selection box to new message panel.
 * 2009/05/24 (2.3.1)
 * add connect/disconnect menu
 * now QBrowserV2 need not to have connection paramters in the start.bat file.
 * add connection/search/command history persistence in disk.
 * If user close the window, next time launched, history data automatically restored from user.home to QBrowserV2.
 * 2009/06/15
 * add MapMessage support.
 * renew property panel.
 * create new message panel with an existed message.
 * other fixes.
 * 2009/07
 * add StreamMessage support (create / view)
 * add Message support (plain)
 * other fixes.
 * 
 */
public class QBrowserV2 extends JPanel {

    public static JFrame oya_frame = null;
    static FolderChooser _folderChooser;
    public JMenuItem shutdown_bkr_itm = null;
    public JMenuItem restart_bkr_itm = null;
    JMenuItem pause_dest_itm = null;
    JMenuItem resume_dest_itm = null;
    JMenuItem pause_dest_itm2 = null;
    JMenuItem resume_dest_itm2 = null;
    JMenuItem pause_localstore_itm = null;
    JMenuItem resume_localstore_itm = null;
    JMenuItem remove_child_local_store_itm = null;
    JMenuItem remove_child_topic_itm = null;
    public JMenuItem lsdelete_menu = null;
    public JMenuItem config_localstore_menu = null;
    public JMenuItem reload_ls_menu = null;
    public JMenuItem purgeqItemfortree = null;
    public JMenuItem subscribe_on_tree = null;
    public JMenuItem addListenToLocalStoreItem = null;
    public JMenuItem addListenToLocalStoreItem2 = null;
    public JSeparator topic_separator1 = new JSeparator();
    public JMenuItem addLocalstoreSubscriptionItem = null;
    public JMenuItem addLocalstoreSubscriptionItem2 = null;
    public JMenuItem create_ls_item = null;
    public JMenuItem create_ls_item2 = null;
    public JMenuItem create_queue_itm = null;
    public JMenuItem create_topic_itm = null;
    public JMenuItem delete_queue_itm = null;
    public JMenuItem delete_topic_itm = null;
    public JMenuItem copyToLocalStoreListItem = null;
    public JMenuItem copyToLocalStoreListItem2 = null;
    public JMenuItem localstoreSubscriptionListItem = null;
    public JMenuItem localstoreSubscriptionListItem2 = null;
    public JMenuItem exit_item = null;
    public JMenuItem connect_item = null;
    public JMenuItem disconnect_item = null;
    public JMenuItem openmessage_item = null;
    public JMenuItem open_multimessage_item = null;
    public JMenuItem newmessage_item = null;
    public JMenuItem newmessage_item2 = null;
    public JMenuItem newmessage_item3 = null;
    public JMenuItem newmessage_item4 = null;
    public JMenuItem newmessage_item5 = null;
    public JMenuItem updateproperty_item = null;
    public JMenuItem newmessage_from_file_item = null;
    public JMenuItem selectall_item = null;
    public JMenuItem version_item = null;
    public JMenuItem dest_item = null;
    public JMenuItem query_dest_item = null;
    public JMenuItem query_dest_item2 = null;
    public JMenuItem query_dest_item3 = null;
    public JMenuItem purge_dest_item = null;
    public JMenuItem list_txn_item = null;
    public JMenuItem filter_txn_item = null;
    public JMenuItem list_cxn_item = null;
    public JMenuItem list_svc_item = null;
    public JMenuItem query_svc_item = null;
    public JMenuItem query_bkr_item = null;
    public JMenuItem config_printer_item = null;
    public JMenuItem cmdw_item = null;
    public JMenuItem subscribe_item = null;
    public JMenuItem refresh_dest_combobox_item = null;
    public JMenuItem vsnet_laf_item = null;
    public JMenuItem office2003_laf_item = null;
    public JMenuItem eclipse_laf_item = null;
    public JMenuItem eclipse3x_laf_item = null;
    public JMenuItem xerto_laf_item = null;
    ArrayList couldnotdelete = null;
    CmdRunnerThread crthread = null;
    JPanel bodycontainer = null;
    JPanel temppanel = null;
    JPanel temppanelf = null;
    JPanel savemsgPanel = null;
    public ResizablePanel tree_location = null;
    TreeIconPanel treePane = null;
    JLabel qLabel = null;
    JComboBox qBox = null;
    JComboBox tqBox = null;
    JComboBox subBox = null;
    JComboBox forwardBox = null;
    JComboBox txnStateBox = null;
    JComboBox cmdTemplateBox = null;
    JComboBox cmdTemplateBoxForSave = new JComboBox();
    JComboBox searchTemplateBox = null;
    JComboBox connectionTemplateBox = null;
    JComboBox subscribeTemplateBox = null;
    JComboBox mqBox = null;
    boolean newmessage1stpanelok = true;
    boolean newmessage1stpanel_user_props_ok = true;
    boolean newmessage1stpanel_mapm_props_ok = true;
    boolean newmessage1stpanel_sm_props_ok = true;
    JFileChooser mfilechooser = null;
    NewMessageInfo nmi = null;
    ButtonGroup detailbg = null;
    JLabel cmessagefooter = null;
    JFrame fcframe = null;
    JTextField mfilepath = null;
    JComboBox message_type = null;
    JComboBox encoding_type = null;
    JPanel penc = null;
    JPanel southpanel = null;
    JPanel downloadbodyPanel = null;
    JTextField matesakiname = null;
    JTextField filechoose_file_path = null;
    JTextField folderchoose_file_path = null;
    JButton qBrowse = null;
    QBrowserCache qbrowsercache = null;
    JButton qSearch = null;
    JButton msgconfirmbutton = null;
    JButton filechoose_okbutton = null;
    JButton folderchoose_okbutton = null;
    JTable hhTable = null;
    JTable ppTable = null;
    JTable msgTable = null;
    JLabel footerLabel = null;
    JPanel footerPanel = null;
    JPanel details_body_current = null;
    JPanel qbuttonpanel = null;
    JTextField soufukosu = null;
    Session session = null;
    Connection connection = null;
    Topic metricTopic = null;
    MessageConsumer metricSubscriber = null;
    JFrame detailsFrame = null;
    public static JFrame newmessageFrame = null;
    JPanel menu_button_container = null;
    public static LocalStoreManager lsm = null;
    ClipBoardManager cbm = null;
    JDialog confirmDialog = null;
    JDialog sendconfirmDialog = null;
    JDialog errDialog = null;
    JDialog msgDialog = null;
    JDialog stringEditDialog = null;
    JDialog saveDialog = null;
    JTextArea string_edit_area = null;
    JDialog cmdmsgDialog = null;
    JDialog searchmsgDialog = null;
    JDialog filechooseDialog = null;
    JDialog folderchooseDialog = null;
    JButton okbutton = null;
    JButton delete = null;
    JButton details = null;
    JPanel  localstore_button_panel = null;
    JDialog deleteconfirmDialog = null;
    JDialog purgedestconfirmDialog = null;
    JDialog filterTxnDialog = null;
    JDialog cmdDialog = null;
    JDialog searchDialog = null;
    JDialog connectionDialog = null;
    JDialog subscribeDialog = null;
    JDialog forwardDialog = null;
    JTextField save_file_path = null;
    long rclick = 0;
    JTextField cmdtextfield = null;
    JTextField searchtextfield = null;
    JTextField connectiontext_host = null;
    JTextField connectiontext_port = null;
    JTextField connectiontext_user = null;
    JPasswordField connectiontext_password = null;

    JTextField subscribetextfield = null;
    JTextField forwardtextfield = null;
    PropertyPanel headerPanel = null, propertyPanel = null, bodyPanel = null;
    PropertyPanel mheaderPanel = null, mpropertyPanel = null, mbodyPanel = null;
    JPanel mfilebodyPanel = null;
    //JButton downloadbutton = null;
    //MessageContainer currentDownloadTargetMsg = null;
    //HashMap currentDownloadTargetMsgMap = new HashMap();
    JLabel downloadmsg = null;
    JPanel msgPanel = null;
    JPanel cmdmsgPanel = null;
    JPanel searchmsgPanel = null;
    JPanel connectionmsgPanel = null;
    JPanel subscribemsgPanel = null;
    JPanel forwardmsgPanel = null;
    JPanel delmsg = null;
    JPanel filtermsg = null;
    JPanel cmdmsg = null;
    //JTextField downloadfilepath = null;
    String selector = null;
    public JMenu localstore_on_menu = null;
    public JMenu localstoremenu = null;
    public JMenu versionmenu = null;
    public JMenu cmdmenu = null;
    public JMenu destcmdmenu = null;
    public JMenu txncmdmenu = null;
    public JMenu rawcmdmenu = null;
    public JMenu editmenu = null;
    public JMenu displaymenu = null;
    public JMenu newmmenu = null;
    public JMenu menu = null;
    public JMenu subscribemenu = null;
    public JMenu lafmenu = null;
    JButton del_okbutton1 = null;
    JPanel filechoosemsgPanel = null;
    JPanel folderchoosemsgPanel = null;
    JMenuBar menubar = null;
    JideButton new_button = null;
    JideButton new_buttonf = null;
    JideButton open_message_button = null;
    JideButton open_multi_message_button = null;
    JideButton createlocalstore_button = null;
    JideButton select_all_button = null;
    JideButton copy_msg_button = null;
    JideButton paste_msg_button = null;
    JideButton connection_list_button = null;
    JideButton services_list_button = null;
    JideButton services_details_button = null;
    JideButton broker_details_button = null;
    JideButton config_printer_button = null;
    JideButton atesaki_info_button = null;
    JideButton atesaki_details_button = null;
    JideButton all_txn_button = null;
    JideButton purge_atesaki_button = null;
    JideButton txn_filter_button = null;
    JideButton cmd_input_button = null;
    JideButton delete_button = null;
    JideButton search_button = null;
    JideButton subscribe_button = null;

    JideTabbedPane tabbedPane = null;
    JButton unsubscribe_button = null;
    JButton subscribe_resume_button = null;
    JButton lsclear_button = null;
    JButton lsdelete_button = null;
    JButton reload_button = null;
    JButton config_localstore_button = null;
    ArrayList popup_threads = new ArrayList();
    JPanel subscribe_b_panel = null;
    JLabel filechooselabel = null;
    JLabel folderchooselabel = null;
    JidePopupMenu popup = null;
    HashMap jtableins = new HashMap();
    JLabel localstorelabel = null;
    int flagN = 0;
    boolean connected = false;
    DisConnectionListener disconl = null;
    int[] targetX = null;
    AtesakiComboBoxItemListener acbil = null;
    ReadOnlyHeaderPropertyTable details_headertable = null;
    Message ext_message = null;
    ArrayList ext_messages = new ArrayList();
    HashMap subscribe_thread_status = new HashMap();
    HashMap subscribe_thread_count = new HashMap();
    HashMap subscribe_threads = new HashMap();
    JComboBox cdeliverymode = null;
    JComboBox ccompressmode = null;
    String last_jmsheader_validate_error = null;
    String last_user_prop_validate_error = null;
    String last_mapmessage_prop_validate_error = null;
    String last_streammessage_prop_validate_error = null;
    JComboBox mapptc = null;
    JComboBox hptc = null;
    QBrowserV2 oya = null;
    JPopupMenu popupMenuX = null;
    JPopupMenu popupMenuX2 = null;
    JPopupMenu popupMenuXForTab = null;
    JPopupMenu popupMenuXForQTab = null;
    JPopupMenu popupMenuXForLSTab = null;
    JPopupMenu popupMenuForBrokerFolder = null;

    JPopupMenu popupMenuTForQueue = null;
    JPopupMenu popupMenuTForTopic = null;
    JPopupMenu popupMenuTForLocalStore = null;

    JPopupMenu popupMenuForQueueFolder = null;
    JPopupMenu popupMenuForTopicFolder = null;
    JPopupMenu popupMenuForLocalStoreFolder = null;

    public JMenuItem forwardmsgItem = null;
    public JMenuItem forwardmsgItem2 = null;
    public JMenuItem deletemsgItem  = null;
    public JMenuItem deletemsgItem2  = null;
    public JMenuItem createmsgasItem  = null;
    public JMenuItem movemsgItem = null;
    public JMenuItem movemsgItem2 = null;
    public JMenuItem savemsgItem = null;
    public JMenuItem savemsgItem2 = null;
    public JMenuItem copymsgItem = null;
    public JMenuItem copymsgItem2 = null;
    public JMenuItem purgelsmsgItem = null;
    public JMenuItem purgelsmsgItem2 = null;
    public JMenuItem purgeqItem = null;
    public JMenuItem pastemsgItem = null;
    public JMenuItem pastemsgItem2 = null;
    public JMenuItem pastemsgItem3 = null;
    public JMenuItem pastemsgItem4 = null;
    public JMenuItem pastemsgItem5 = null;
    public JMenuItem pastemsgItem6 = null;
    JPanel currentBodyPanel = null;
    int last_right_click_X = 0;
    int last_right_click_Y = 0;
    BytesMessage passthrough_bytesmessage = null;
    boolean reconnect_runner_started = false;
    public static boolean isBrokerEE = true;
    PropertyInputTable property_table = null;
    MapMessageInputTable mapm_property_table = null;
    StreamMessageInputTable sm_property_table = null;
    //BytesForDownloadPropertyTable mapm_download_property_table = null;
    //MapMessageAllPropertiesTable mapm_all_property_table = null;
    //StreamMessageBytesForDownloadPropertyTable sm_download_property_table = null;
    //StreamMessageAllPropertiesTable sm_all_property_table = null;
    HeaderPropertyTable header_table = null;
    PropTableCellEditor hdce2 = null;
    DefaultCellEditor pdce1 = null;
    ListCellEditor pdce3 = null;
    DefaultCellEditor mapmdce0 = null;
    ListCellEditor mapmdce3 = null;
    ListCellEditor smdce3 = null;
    JPanel messagesentakupanel = null;
    MapMessagePropertyPanel mapmBodyPanel = null;
    StreamMessagePropertyPanel smBodyPanel = null;
    //MapMessagePropertyForDownloadPanel mapmBodyForDownloadPanel = null;
    //MapMessageAllPropertiesPanel mapmBodyForAPPanel = null;
    //StreamMessagePropertyForDownloadPanel smBodyForDownloadPanel = null;
    //StreamMessageAllPropertiesPanel smBodyForAPPanel = null;
    JTable hTable = null;
    JTable pTable = null;
    QBTable mTable = null;
    QBTable sTable = null;
    JTable mdTable = null;
    JTable maTable = null;
    //JTable sdTable = null;
    JTable saTable = null;
    JLabel errlabel = null;
    JComboBox matesakiBox1 = null;
    JComboBox matesakiBox2 = null;
    JComboBox matesakiBox3 = null;
    JComboBox localstoreBox = null;
    static ArrayList currentDeleteTarget = new ArrayList();
    int[] delete_from_cache_rows;
    static File _currentFolder = null;
    static List<String> _recentList = new ArrayList<String>();
    static JFileChooser file_chooser = null;
    static boolean isStartingSuccessful = true;
    static final String DEFAULT_BROKER_HOST = "localhost";
    static final int DEFAULT_BROKER_PORT = 7676;
    static final String DEFAULT_BROKER_ADMIN_USER = "admin";
    static final String DEFAULT_BROKER_PASSWORD = "admin";
    static final String DEST_LIST_TOPIC_NAME = "mq.metrics.destination_list";
    public static String version = "V2.5.2.8";
    public static String title = "QBrowser light " + version;
    public static String bkr_instance_name = "";
    public static String serverHost = DEFAULT_BROKER_HOST;
    public static int serverPort = DEFAULT_BROKER_PORT;
    public static String serverUser = DEFAULT_BROKER_ADMIN_USER;
    public static String serverPassword = DEFAULT_BROKER_PASSWORD;
    public static String real_passfile_path = null;
    public static String[] pad = {"", "0", "00", "000", "0000"};
    public static ResourceBundle resources = ResourceBundle.getBundle("com.qbrowser.resourcebase");
    public final static String TOPIC_SUFFIX = " : Topic";
    public final static String QUEUE_SUFFIX = " : Queue";
    public final static String LOCAL_STORE_SUFFIX  = " : LOCAL_STORE";
    public final static String QUEUE_LITERAL = "QUEUE";
    public final static String TOPIC_LITERAL = "TOPIC";
    public final static String LOCAL_STORE_LITERAL = "LOCAL_STORE";
    public final static String CHILD_LOCAL_STORE_LITERAL = "C_LOCAL_STORE";
    public final static String CHILD_TOPIC_LITERAL = "C_TOPIC";
    public final static String TEXTMESSAGE = "TextMessage";
    public final static String BYTESMESSAGE = "BytesMessage";
    public final static String STREAMMESSAGE = "StreamMessage";
    public final static String MAPMESSAGE = "MapMessage";
    public final static String OBJECTMESSAGE = "ObjectMessage";
    public final static String MESSAGE = "Message";
    public final static String TMPWORK_LOCALSTORE = "TMPWORK" + LOCAL_STORE_SUFFIX;
    public final static String TABNAME = "TABNAME";
    public final static String CURRENTLOCALSTORE = "CURRENTLS";
    public static final String PERSISTENT = resources.getString("qkey.msg.msg122");
    public static final String NONPERSISTENT = resources.getString("qkey.msg.msg123");
    public static final String msg108 = resources.getString("qkey.msg.msg108");
    public static final String msg109 = resources.getString("qkey.msg.msg109");
    public static final String msg110 = resources.getString("qkey.msg.msg110");
    public static final String msg111 = resources.getString("qkey.msg.msg111");
    public static final String MAGIC_SEPARATOR = "@@@HzRpWr@@";
    public static final String QBBUTTONROWPOSITION = "QB_BUTTON_ROW_POSITION";
    public static ArrayList    destinationNamesForDisplayQueue = new ArrayList();
    public static ArrayList    destinationNamesForDisplayTopic = new ArrayList();

    QBrowserV2() {

        super(true);

        if (!checkJDKVersion()) {
            TextArea ta = new TextArea(8, 50);
            ta.append(resources.getString("qkey.msg.msg234"));
            ta.append(resources.getString("qkey.msg.msg236"));
            ta.append(System.getProperty("java.version"));
            ta.append(resources.getString("qkey.msg.msg235"));

            popupSimpleExitMessageDialog(resources.getString("qkey.msg.msg233"), ta, new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    System.exit(1);
                }
            },
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));
        } else {

        qbrowsercache = new QBrowserCache();

        setBorder(BorderFactory.createEtchedBorder());
        setLayout(new BorderLayout());

        // Create menu bar
        menubar = new JMenuBar();

        menu = new JMenu("qkey.menu.item.cm");
        exit_item = new JMenuItem("qkey.menu.item.exit",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));


        exit_item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Window w = (Window) SwingUtilities.getRoot(QBrowserV2.this);
                w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
            }
        });

        //Connection Menu
        connect_item = new JMenuItem("qkey.menu.item.connect",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Connect));

        connect_item.addActionListener(new ConnectionListener());

        //Disconnection Menu
        disconnect_item = new JMenuItem("qkey.menu.item.disconnect",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Disconnect));

        disconl = new DisConnectionListener();
        disconnect_item.addActionListener(disconl);

        openmessage_item = new JMenuItem("qkey.menu.item.openmessage",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.OpenFile));

        OpenMessageFromFileListener omffl = new OpenMessageFromFileListener();
        openmessage_item.addActionListener(omffl);

        open_multimessage_item = new JMenuItem("qkey.menu.item.openmultimessage",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.OpenMultiFile));

        OpenMessageFromFolderListener omfdl = new OpenMessageFromFolderListener();
        open_multimessage_item.addActionListener(omfdl);

        newmmenu = new JMenu("qkey.menu.item.newmessage");

        newmessage_item = new JMenuItem("qkey.menu.item.createnewandsend",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg));



        NewMessageListener nml = new NewMessageListener();
        newmessage_item.addActionListener(nml);

        newmessage_from_file_item = new JMenuItem("qkey.menu.item.createnewfandsend",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMessageFromFile));

        NewMessageFromFileListener nmff = new NewMessageFromFileListener();
        newmessage_from_file_item.addActionListener(nmff);

        newmmenu.add(newmessage_item);
        newmmenu.add(newmessage_from_file_item);

        editmenu = new JMenu("qkey.menu.item.edit");
        editmenu.addMenuListener(new SelectAllMenuListener());


        selectall_item = new JMenuItem("qkey.menu.item.selectall",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ChkAll));
        SelectAllMessageListener salml = new SelectAllMessageListener();
        selectall_item.addActionListener(salml);

        editmenu.add(selectall_item);

        //Display
        displaymenu = new JMenu("qkey.menu.item.display");
        refresh_dest_combobox_item = new JMenuItem("qkey.menu.item.refreshdestnamebox",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Refresh));
        refresh_dest_combobox_item.addActionListener(new RefreshDestNames());
        displaymenu.add(refresh_dest_combobox_item);

        //LocalStore
        localstoremenu = new JMenu("qkey.menu.localstore");
        create_ls_item = new JMenuItem("qkey.menu.item.createlocalstore",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "6.png"));
        NewLocalStoreListener nlsl = new NewLocalStoreListener();
        create_ls_item.addActionListener(nlsl);
        localstoremenu.add(create_ls_item);
        localstoremenu.add(new JSeparator());

        create_ls_item2 = new JMenuItem("qkey.menu.item.createlocalstore",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "6.png"));
        create_ls_item2.addActionListener(nlsl);

        create_queue_itm = new JMenuItem("qkey.msg.msg346",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "1.png"));
        create_queue_itm.addActionListener(new CreateQueueListener());

        create_topic_itm = new JMenuItem("qkey.msg.msg348",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "6.png"));
        create_topic_itm.addActionListener(new CreateTopicListener());

        delete_queue_itm = new JMenuItem("qkey.msg.msg356",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "1.png"));
        delete_queue_itm.addActionListener(new DeleteQueueListener());

        delete_topic_itm = new JMenuItem("qkey.msg.msg357",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "6.png"));
        delete_topic_itm.addActionListener(new DeleteTopicListener());



        popupMenuTForLocalStore = new JPopupMenu();
        popupMenuTForLocalStore.setBorderPainted(true);


        popupMenuForBrokerFolder = new JPopupMenu();
        popupMenuForBrokerFolder.setBorderPainted(true);


        shutdown_bkr_itm = new JMenuItem("qkey.msg.msg368",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Shutdown));
        shutdown_bkr_itm.addActionListener(new BrokerCommandListener(false));

        restart_bkr_itm = new JMenuItem("qkey.msg.msg369",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Restart));
        restart_bkr_itm.addActionListener(new BrokerCommandListener(true));

        popupMenuForBrokerFolder.add(shutdown_bkr_itm);
        popupMenuForBrokerFolder.add(restart_bkr_itm);

        popupMenuForLocalStoreFolder = new JPopupMenu();
        //popupMenuForLocalStoreFolder.addSeparator();
        popupMenuForLocalStoreFolder.setBorderPainted(true);

        popupMenuForLocalStoreFolder.add(create_ls_item2);

        popupMenuForQueueFolder = new JPopupMenu();
        //popupMenuForQueueFolder.addSeparator();
        popupMenuForQueueFolder.setBorderPainted(true);
        popupMenuForQueueFolder.add(create_queue_itm);

        popupMenuForTopicFolder = new JPopupMenu();
        //popupMenuForTopicFolder.addSeparator();
        popupMenuForTopicFolder.setBorderPainted(true);
        popupMenuForTopicFolder.add(create_topic_itm);

        localstore_on_menu = new JMenu("qkey.menu.item.showlocalstore");
        localstoremenu.add(localstore_on_menu);

        //Subscribe
        subscribemenu = new JMenu("qkey.menu.subscribe");
        subscribe_item = new JMenuItem("qkey.menu.item.subscribe",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Subscribe));
        SubscribeListener sls = new SubscribeListener();
        subscribe_item.addActionListener(sls);
        subscribemenu.add(subscribe_item);

        //LookAndFeel
        oya = this;
        lafmenu = new JMenu("qkey.menu.lookandfeel");
        vsnet_laf_item = new JMenuItem("qkey.menu.item.laf1");
        vsnet_laf_item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                    LookAndFeelFactory.installJideExtension(LookAndFeelFactory.VSNET_STYLE);
                    SwingUtilities.updateComponentTreeUI(oya);
                } catch (Exception ee) {
                    //NOP
                }
            }
        });

        office2003_laf_item = new JMenuItem("qkey.menu.item.laf2");
        office2003_laf_item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                    LookAndFeelFactory.installJideExtension(LookAndFeelFactory.OFFICE2003_STYLE);
                    SwingUtilities.updateComponentTreeUI(oya);
                } catch (Exception ee) {
                    //NOP
                }
            }
        });

        eclipse_laf_item = new JMenuItem("qkey.menu.item.laf3");
        eclipse_laf_item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                    LookAndFeelFactory.installJideExtension(LookAndFeelFactory.ECLIPSE_STYLE);
                    SwingUtilities.updateComponentTreeUI(oya);
                } catch (Exception ee) {
                    //NOP
                }
            }
        });

        eclipse3x_laf_item = new JMenuItem("qkey.menu.item.laf4");
        eclipse3x_laf_item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                    LookAndFeelFactory.installJideExtension(LookAndFeelFactory.ECLIPSE3X_STYLE);
                    SwingUtilities.updateComponentTreeUI(oya);
                } catch (Exception ee) {
                    //NOP
                }
            }
        });

        xerto_laf_item = new JMenuItem("qkey.menu.item.laf5");
        xerto_laf_item.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
                    LookAndFeelFactory.installJideExtension(LookAndFeelFactory.XERTO_STYLE);
                    SwingUtilities.updateComponentTreeUI(oya);

                } catch (Exception ee) {
                    //NOP
                }

            }
        });

        lafmenu.add(vsnet_laf_item);
        lafmenu.add(office2003_laf_item);
        lafmenu.add(eclipse_laf_item);
        lafmenu.add(eclipse3x_laf_item);
        lafmenu.add(xerto_laf_item);


        //Brokerのバージョン
        versionmenu = new JMenu("qkey.menu.clientversion");

        version_item = new JMenuItem("qkey.menu.item.clientversion",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ClientVersion));
        version_item.addActionListener(new VersionListener());

        versionmenu.add(version_item);

        //全宛先情報リスト
        cmdmenu = new JMenu("qkey.menu.item.querycmd");

        //全宛先情報リスト
        destcmdmenu = new JMenu("qkey.menu.item.destinationcmd");

        //トランザクション
        txncmdmenu = new JMenu("qkey.menu.item.transactions");

        //コマンド入力ウィンドウ
        rawcmdmenu = new JMenu("qkey.menu.item.inputcmd");
        //CmdListener

        cmdw_item = new JMenuItem("qkey.menu.item.inputcmdwindow",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.CmdInput));
        CmdListener cl = new CmdListener();
        cmdw_item.addActionListener(cl);

        rawcmdmenu.add(cmdw_item);

        dest_item = new JMenuItem("qkey.menu.item.alldestlist",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ListAtesaki));
        ListDestCmdListener ldcl = new ListDestCmdListener();
        dest_item.addActionListener(ldcl);

        destcmdmenu.add(dest_item);

        query_dest_item = new JMenuItem("qkey.menu.item.destdetail",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails));
        QueryDestCmdListener qdcl = new QueryDestCmdListener();
        query_dest_item.addActionListener(qdcl);

        query_dest_item2 = new JMenuItem("qkey.menu.item.displaydestdetail",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails));
        QueryDestCmdListener2 qdcl2 = new QueryDestCmdListener2();
        query_dest_item2.addActionListener(qdcl2);

        query_dest_item3 = new JMenuItem("qkey.menu.item.displaydestdetail",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails));
        query_dest_item3.addActionListener(qdcl2);

        destcmdmenu.add(query_dest_item);
        destcmdmenu.add(new JSeparator());

        purge_dest_item = new JMenuItem("qkey.menu.item.purgedest",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));
        PurgeDestCmdListener pdcl = new PurgeDestCmdListener();
        purge_dest_item.addActionListener(pdcl);
        
        purgeqItem = new JMenuItem("qkey.msg.msg338",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));
        purgeqItem.addActionListener(pdcl);

        PurgeDestCmdListener2 pdcl2 = new PurgeDestCmdListener2();
        purgeqItemfortree = new JMenuItem("qkey.msg.msg338",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));
        purgeqItemfortree.addActionListener(pdcl2);



        destcmdmenu.add(purge_dest_item);


        filter_txn_item = new JMenuItem("qkey.menu.item.filteredtxnlist",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.FilteredTxn));
        FilteredListTxnCmdListener fltcl = new FilteredListTxnCmdListener();
        filter_txn_item.addActionListener(fltcl);

        txncmdmenu.add(filter_txn_item);

        //ListTxnCmdListener
        list_txn_item = new JMenuItem("qkey.menu.item.alltxnlist",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AllTxn));
        ListTxnCmdListener ltcl = new ListTxnCmdListener();
        list_txn_item.addActionListener(ltcl);

        txncmdmenu.add(list_txn_item);

        //FilteredListTxnCmdListener
        list_cxn_item = new JMenuItem("qkey.menu.item.connlist",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConnList));
        ListCxnCmdListener lccl = new ListCxnCmdListener();
        list_cxn_item.addActionListener(lccl);

        cmdmenu.add(list_cxn_item);
        cmdmenu.add(new JSeparator());

        list_svc_item = new JMenuItem("qkey.menu.item.servicelist",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.SvcList));
        ListSvcCmdListener lscl = new ListSvcCmdListener();
        list_svc_item.addActionListener(lscl);

        cmdmenu.add(list_svc_item);

        //QuerySvcCmdListener
        query_svc_item = new JMenuItem("qkey.menu.item.servicedetail",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.SvcDetails));
        QuerySvcCmdListener qscl = new QuerySvcCmdListener();
        query_svc_item.addActionListener(qscl);

        cmdmenu.add(query_svc_item);
        cmdmenu.add(new JSeparator());

        //QueryBkrCmdListener
        query_bkr_item = new JMenuItem("qkey.menu.item.brokerdetail",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.BkrDetails));
        QueryBkrCmdListener qbcl = new QueryBkrCmdListener();
        query_bkr_item.addActionListener(qbcl);

        cmdmenu.add(query_bkr_item);

        //ConfigPrinterCmdListener
        config_printer_item = new JMenuItem("qkey.menu.item.configprinter",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter));
        ConfigPrinterListener cpl = new ConfigPrinterListener();
        config_printer_item.addActionListener(cpl);
        cmdmenu.add(config_printer_item);

        menu.add(connect_item);
        menu.add(disconnect_item);
        menu.add(new JSeparator());
        menu.add(openmessage_item);
        menu.add(open_multimessage_item);
        menu.add(new JSeparator());
        menu.add(exit_item);
        
        menubar.add(menu);
        menubar.add(newmmenu);
        menubar.add(editmenu);
        menubar.add(localstoremenu);
        menubar.add(displaymenu);
        menubar.add(subscribemenu);
        menubar.add(cmdmenu);
        menubar.add(destcmdmenu);
        menubar.add(txncmdmenu);
        menubar.add(rawcmdmenu);
        menubar.add(lafmenu);
        menubar.add(versionmenu);

        //menu button container
        menu_button_container = new JPanel();
        new_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg));
        new_button.setToolTipText(resources.getString("qkey.menu.item.newmessage"));
        new_button.addActionListener(nml);

        new_buttonf = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMessageFromFile));
        new_buttonf.setToolTipText(resources.getString("qkey.menu.item.createnewfandsend"));
        new_buttonf.addActionListener(nmff);
        
        open_message_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.OpenFile));
        open_message_button.setToolTipText(resources.getString("qkey.menu.item.openmessage"));
        open_message_button.addActionListener(omffl);

        open_multi_message_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.OpenMultiFile));
        open_multi_message_button.setToolTipText(resources.getString("qkey.menu.item.openmultimessage"));
        open_multi_message_button.addActionListener(omfdl);

        createlocalstore_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "6.png"));
        createlocalstore_button.setToolTipText(resources.getString("qkey.menu.item.createlocalstore"));
        createlocalstore_button.addActionListener(nlsl);


        select_all_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ChkAll));
        select_all_button.setToolTipText(resources.getString("qkey.menu.item.selectall"));
        select_all_button.addActionListener(salml);
        select_all_button.addMouseListener(new SelectAllButtonMouseListener());

        copy_msg_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Copy));
        copy_msg_button.setToolTipText(resources.getString("qkey.msg.msg326"));
        copy_msg_button.addActionListener(new CopyXListener());

        paste_msg_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paste));
        paste_msg_button.setToolTipText(resources.getString("qkey.msg.msg327"));
        paste_msg_button.addActionListener(new PasteActionListener());

        subscribe_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Subscribe));
        subscribe_button.setToolTipText(resources.getString("qkey.menu.item.subscribe"));
        subscribe_button.addActionListener(sls);

        delete_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.DelMsg));
        delete_button.setToolTipText(resources.getString("qkey.msg.msg002"));
        DeleteListener dlis = new DeleteListener();
        delete_button.addActionListener(dlis);

        search_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails));
        search_button.setToolTipText("Search");
        SearchListener schl = new SearchListener();
        search_button.addActionListener(schl);

        connection_list_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConnList));
        connection_list_button.setToolTipText(resources.getString("qkey.menu.item.connlist"));
        connection_list_button.addActionListener(lccl);

        services_list_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.SvcList));
        services_list_button.setToolTipText(resources.getString("qkey.menu.item.servicelist"));
        services_list_button.addActionListener(lscl);

        services_details_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.SvcDetails));
        services_details_button.setToolTipText(resources.getString("qkey.menu.item.servicedetail"));
        services_details_button.addActionListener(qscl);

        broker_details_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.BkrDetails));
        broker_details_button.setToolTipText(resources.getString("qkey.menu.item.brokerdetail"));
        broker_details_button.addActionListener(qbcl);

        config_printer_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter));
        config_printer_button.setToolTipText("Config Printer");
        config_printer_button.addActionListener(cpl);

        atesaki_info_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ListAtesaki));
        atesaki_info_button.setToolTipText(resources.getString("qkey.menu.item.alldestlist"));
        atesaki_info_button.addActionListener(ldcl);

        atesaki_details_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails));
        atesaki_details_button.setToolTipText(resources.getString("qkey.menu.item.destdetail"));
        atesaki_details_button.addActionListener(qdcl);

        purge_atesaki_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));
        purge_atesaki_button.setToolTipText(resources.getString("qkey.menu.item.purgedest"));
        purge_atesaki_button.addActionListener(pdcl);

        all_txn_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AllTxn));
        all_txn_button.setToolTipText(resources.getString("qkey.menu.item.alltxnlist"));
        all_txn_button.addActionListener(ltcl);

        txn_filter_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.FilteredTxn));
        txn_filter_button.setToolTipText(resources.getString("qkey.menu.item.filteredtxnlist"));
        txn_filter_button.addActionListener(fltcl);

        cmd_input_button = new JideButton(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.CmdInput));
        cmd_input_button.addActionListener(cl);
        cmd_input_button.setToolTipText(resources.getString("qkey.menu.item.inputcmdwindow"));

        menu_button_container.setLayout(new FlowLayout());
        menu_button_container.add(new_button);
        menu_button_container.add(new_buttonf);
        menu_button_container.add(open_message_button);
        menu_button_container.add(open_multi_message_button);
        menu_button_container.add(select_all_button);
        menu_button_container.add(copy_msg_button);
        menu_button_container.add(paste_msg_button);
        menu_button_container.add(createlocalstore_button);
        menu_button_container.add(subscribe_button);
        menu_button_container.add(delete_button);
        menu_button_container.add(search_button);
        menu_button_container.add(connection_list_button);
        menu_button_container.add(services_list_button);
        menu_button_container.add(services_details_button);
        menu_button_container.add(broker_details_button);
        menu_button_container.add(config_printer_button);
        menu_button_container.add(atesaki_info_button);
        menu_button_container.add(atesaki_details_button);
        menu_button_container.add(purge_atesaki_button);
        menu_button_container.add(all_txn_button);
        menu_button_container.add(txn_filter_button);
        menu_button_container.add(cmd_input_button);

        cbm = new ClipBoardManager();
        
        //popup
        popupMenuX = new JPopupMenu();
        popupMenuX.setBorderPainted(true);

        popupMenuX2 = new JPopupMenu();

        popupMenuXForTab = new JPopupMenu();
        popupMenuXForTab.setBorderPainted(true);

        popupMenuXForQTab = new JPopupMenu();
        popupMenuXForQTab.setBorderPainted(true);

        popupMenuXForLSTab = new JPopupMenu();
        popupMenuXForLSTab.setBorderPainted(true);

        popupMenuTForQueue = new JPopupMenu();
        popupMenuTForQueue.setBorderPainted(true);

        popupMenuTForTopic = new JPopupMenu();
        popupMenuTForTopic.setBorderPainted(true);

        newmessage_item2 = new JMenuItem("qkey.msg.msg370",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg));
        newmessage_item2.addActionListener(nml);
        newmessage_item3 = new JMenuItem("qkey.msg.msg370",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg));
        newmessage_item3.addActionListener(nml);
        newmessage_item4 = new JMenuItem("qkey.msg.msg370",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg));
        newmessage_item4.addActionListener(nml);
        newmessage_item5 = new JMenuItem("qkey.msg.msg370",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg));
        newmessage_item5.addActionListener(nml);

        popupMenuTForQueue.add(newmessage_item2);
        popupMenuTForQueue.add(purgeqItemfortree);
        popupMenuTForQueue.add(delete_queue_itm);
        popupMenuTForQueue.add(query_dest_item2);
       
/*
        updateproperty_item = new JMenuItem("qkey.msg.msg381",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg));



        popupMenuTForQueue.add(updateproperty_item);
*/
        popupMenuTForTopic.add(newmessage_item3);
        

        subscribe_on_tree = new JMenuItem("qkey.msg.msg340",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Subscribe));

        subscribe_on_tree.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    
                    TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();
                    //System.out.println("DEST NAME = " + di.destinationName);
                    showSubscribeWindow(di.destinationName);

                }
            });

        popupMenuTForTopic.add(subscribe_on_tree);
        popupMenuTForTopic.add(delete_topic_itm);
        popupMenuTForTopic.add(query_dest_item3);
     

        
        forwardmsgItem = new JMenuItem("qkey.msg.msg135",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Forward));
        forwardmsgItem2 = new JMenuItem("qkey.msg.msg135",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Forward));

        deletemsgItem = new JMenuItem("qkey.msg.msg153",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));

        deletemsgItem2 = new JMenuItem("qkey.msg.msg153",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));

        createmsgasItem = new JMenuItem("qkey.msg.msg217",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Recycle));

        movemsgItem = new JMenuItem("qkey.msg.msg223",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Move));

        movemsgItem2 = new JMenuItem("qkey.msg.msg223",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Move));

        savemsgItem = new JMenuItem("qkey.msg.msg246",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Save));

        savemsgItem2 = new JMenuItem("qkey.msg.msg246",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Save));

        copymsgItem = new JMenuItem("qkey.msg.msg326",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Copy));
        copymsgItem.addActionListener(new CopyXListener());
        
        copymsgItem2 = new JMenuItem("qkey.msg.msg326",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Copy));
        copymsgItem2.addActionListener(new CopyXListener());
        
        purgelsmsgItem = new JMenuItem("qkey.msg.msg338",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));
        purgelsmsgItem.addActionListener(new PurgeLSListener());

        purgelsmsgItem2 = new JMenuItem("qkey.msg.msg338",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));
        purgelsmsgItem2.addActionListener(new PurgeLSListener2());

        pastemsgItem = new JMenuItem("qkey.msg.msg327",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paste));
        pastemsgItem.addActionListener(new PasteActionListener());

        pastemsgItem2 = new JMenuItem("qkey.msg.msg327",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paste));
        pastemsgItem2.addActionListener(new PasteActionListener());

        pastemsgItem3 = new JMenuItem("qkey.msg.msg327",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paste));
        pastemsgItem3.addActionListener(new PasteActionListener());

        pastemsgItem4 = new JMenuItem("qkey.msg.msg327",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paste));
        pastemsgItem4.addActionListener(new PasteToTreeActionListener());

        pastemsgItem5 = new JMenuItem("qkey.msg.msg327",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paste));
        pastemsgItem5.addActionListener(new PasteToTreeActionListener());

        pastemsgItem6 = new JMenuItem("qkey.msg.msg327",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Paste));
        pastemsgItem6.addActionListener(new PasteToTreeActionListener());

        //popupMenuX2.add(pastemsgItem2);
        popupMenuX2.add(newmessage_item5);

        popupMenuX.add(forwardmsgItem);
        popupMenuX.add(movemsgItem);
        popupMenuX.add(new JSeparator());
        popupMenuX.add(createmsgasItem);
        popupMenuX.add(new JSeparator());
        popupMenuX.add(savemsgItem);
        popupMenuX.add(deletemsgItem2);
        popupMenuX.add(new JSeparator());

        popupMenuXForQTab.add(purgeqItem);

      
        editmenu.add(copymsgItem);
        editmenu.add(pastemsgItem3);
        editmenu.add(new JSeparator());
        editmenu.add(deletemsgItem);
        editmenu.add(new JSeparator());
        editmenu.add(savemsgItem2);
        editmenu.add(new JSeparator());
        editmenu.add(forwardmsgItem2);
        editmenu.add(movemsgItem2);

        addListenToLocalStoreItem = new JMenuItem("qkey.msg.msg311",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Forward));
        addListenToLocalStoreItem.addActionListener(new AddListenToLocalStoreListener());
        addListenToLocalStoreItem2 = new JMenuItem("qkey.msg.msg311",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Forward));
        addListenToLocalStoreItem2.addActionListener(new AddListenToLocalStoreListener());

        copyToLocalStoreListItem = new JMenuItem("qkey.msg.msg307",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.RecordList));
        copyToLocalStoreListItem.addActionListener(new CopyToLocalStoreListListener());
        copyToLocalStoreListItem2 = new JMenuItem("qkey.msg.msg307",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.RecordList));
        copyToLocalStoreListItem2.addActionListener(new CopyToLocalStoreListListener());
        popupMenuTForTopic.add(addListenToLocalStoreItem2);
        popupMenuTForTopic.add(copyToLocalStoreListItem2);
        
        popupMenuXForTab.add(addListenToLocalStoreItem);
        popupMenuXForTab.add(copyToLocalStoreListItem);

        addLocalstoreSubscriptionItem = new JMenuItem("qkey.msg.msg314",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Copyin));
        addLocalstoreSubscriptionItem.addActionListener(new AddLocalStoreSubscriptionListener());

        //for tree
        addLocalstoreSubscriptionItem2 = new JMenuItem("qkey.msg.msg314",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Copyin));
        addLocalstoreSubscriptionItem2.addActionListener(new AddLocalStoreSubscriptionListener());
        popupMenuTForLocalStore.add(newmessage_item4);
        popupMenuTForLocalStore.add(addLocalstoreSubscriptionItem2);


        popupMenuXForLSTab.add(addLocalstoreSubscriptionItem);
        localstoreSubscriptionListItem = new JMenuItem("qkey.msg.msg308",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.RecordList));
        localstoreSubscriptionListItem.addActionListener(new LocalStoreSubscriptionListListener());

        //for tree
        localstoreSubscriptionListItem2 = new JMenuItem("qkey.msg.msg308",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.RecordList));
        localstoreSubscriptionListItem2.addActionListener(new LocalStoreSubscriptionListListener());

        popupMenuTForLocalStore.add(new JSeparator());
        popupMenuTForLocalStore.add(localstoreSubscriptionListItem2);
        popupMenuTForLocalStore.add(new JSeparator());
        popupMenuTForLocalStore.add(purgelsmsgItem2);
        
        
        popupMenuXForLSTab.add(localstoreSubscriptionListItem);
        popupMenuXForLSTab.add(purgelsmsgItem);

        
        
        forwardmsgItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                collectCurrentSelectedRows();
                                showForwardWindow(last_right_click_X, last_right_click_Y, false);
                            }
                        });

        forwardmsgItem2.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                collectCurrentSelectedRows();
                                showForwardWindow(last_right_click_X, last_right_click_Y, false);
                            }
                        });

        movemsgItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                collectCurrentSelectedRows();
                                showForwardWindow(last_right_click_X, last_right_click_Y, true);
                            }
                        });

        movemsgItem2.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                                collectCurrentSelectedRows();
                                showForwardWindow(last_right_click_X, last_right_click_Y, true);
                            }
                        });

                        
        deletemsgItem.addActionListener(new DeleteXListener());
        deletemsgItem2.addActionListener(new DeleteXListener());

        createmsgasItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {
                               collectCurrentSelectedRows();
                               if (!ext_messages.isEmpty()) {
                                  Object mobj = ext_messages.get(0);
                                  if (mobj instanceof LocalMessageContainer) {
                                      showNewMessagePanelAsMessageCopy((LocalMessageContainer)mobj);
                                  } else {
                                      showNewMessagePanelAsMessageCopy((MessageContainer)mobj);
                                  }
                               }
                            }
                        });

        savemsgItem.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {

                               collectCurrentSelectedRows();
                               showSaveToWindow(resources.getString("qkey.msg.msg247"),
                                       resources.getString("qkey.msg.msg248"));
                            }
                        });

        savemsgItem2.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent event) {

                               collectCurrentSelectedRows();
                               showSaveToWindow(resources.getString("qkey.msg.msg247"),
                                       resources.getString("qkey.msg.msg248"));
                            }
                        });

        //banner
        BannerPanel headerPanel2 = new BannerPanel(resources.getString("qkey.tool.title"), "",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.QBIcon));

        headerPanel2.setFont(new Font("Tahoma", Font.PLAIN, 11));
        headerPanel2.setForeground(Color.WHITE);
        headerPanel2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        headerPanel2.setTitleIconLocation(SwingConstants.LEADING);

        //midnight blue gradation
        headerPanel2.setGradientPaint(Color.WHITE, new Color(25, 25, 112), false);

        // Create panel to hold input area for Q name and Browse button
        JPanel qPanel = new JPanel();
        qPanel.setLayout(new BorderLayout());

        //メニューは、北にメニューバー
        //南にボタンバーとする
        JPanel menu_container = new JPanel();
        menu_container.setLayout(new BorderLayout());
        menubar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        menu_container.add(BorderLayout.NORTH, menubar);
        JPanel pmenu_button_container = new JPanel();
        pmenu_button_container.setLayout(new BorderLayout());
        pmenu_button_container.add(BorderLayout.WEST, menu_button_container);

        menu_container.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        menu_container.add(BorderLayout.SOUTH, pmenu_button_container);
        menu_container.setBorder(BorderFactory.createRaisedBevelBorder());

        qPanel.add(BorderLayout.NORTH, menu_container);

        JPanel qnamec = new JPanel();
        qnamec.setLayout(new BorderLayout());

        qLabel = new JLabel(resources.getString("qkey.menu.item.queuename") + " ");


        qBox = new JComboBox();


        acbil = new AtesakiComboBoxItemListener();
        qBox.addItemListener(acbil);

        Dimension d = qBox.getPreferredSize();
        d.setSize(10 * d.getWidth() + 50, d.getHeight());
        qBox.setPreferredSize(d);
        qBox.setEditable(true);

        qBrowse = new JButton("Browse");
        qBrowse.addActionListener(new BrowseListener());

        //searchボタン
        qSearch = new JButton("Search");
        qSearch.addActionListener(schl);
        JPanel bbpanel001 = new JPanel(new FlowLayout());
        bbpanel001.add(qBrowse);
        bbpanel001.add(qSearch);

        qnamec.add(BorderLayout.WEST, qLabel);
        qnamec.add(BorderLayout.CENTER, qBox);
        qnamec.add(BorderLayout.EAST, bbpanel001);

        JPanel ppContainer = new JPanel();
        ppContainer.add(BorderLayout.SOUTH, qnamec);


        qPanel.add(BorderLayout.EAST, ppContainer);
        qPanel.add(BorderLayout.WEST, headerPanel2);
        qPanel.updateUI();

        add(BorderLayout.NORTH, qPanel);

        // Create panel to hold table of messages
        JPanel tPanel = new JPanel();
        tPanel.setLayout(new BorderLayout());

        msgTable = new JTable(new MsgTable());

        msgTable.setDefaultRenderer(Object.class, new StripeTableRenderer());

        msgTable.addMouseListener(new TableMouseListener());
        msgTable.setAlignmentX(JTable.CENTER_ALIGNMENT);
        msgTable.setAlignmentY(JTable.CENTER_ALIGNMENT);

        msgTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        TableColumn column = msgTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);
        column.sizeWidthToFit();
        column.setCellRenderer(new CellRenderer0());
        HeaderRenderer01 hr = new HeaderRenderer01();
        column.setHeaderRenderer(hr);

        column = msgTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(330);
        column.setCellRenderer(new CellRenderer1());
        column.setHeaderRenderer(hr);

        column = msgTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(150);
        column.setHeaderRenderer(hr);

        column = msgTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(90);
        column.setHeaderRenderer(hr);

        column = msgTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);

        column = msgTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);

        column = msgTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);

        JScrollPane tablePane = new JScrollPane(msgTable);

        tabbedPane = new JideTabbedPane(JideTabbedPane.TOP);

        tabbedPane.setShowCloseButton(true);
        tabbedPane.setShowCloseButtonOnTab(true);

        tabbedPane.setCloseAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

                JScrollPane jscp = (JScrollPane)e.getSource();
                String tabname = (String)jscp.getClientProperty(TABNAME);
                int tindex = tabbedPane.indexOfTab(tabname);
                
                tabbedPane.removeTabAt(tindex);
                int selidx = tabbedPane.getSelectedIndex();
                if (selidx != -1) {
                  refreshTableOnCurrentSelectedTab();
                  tabname = tabbedPane.getTitleAt(selidx);
                  qBox.setSelectedItem(tabname);
                }

            } });

        tabbedPane.addMouseListener(new SelectTabMouseListener());
        tabbedPane.setPreferredSize(new Dimension(880, 500));
        tabbedPane.setMinimumSize(new Dimension(100, 100));

        JPanel middle_panel = new JPanel();
        middle_panel.setLayout(new BorderLayout());
        tree_location = new ResizablePanel();
        tree_location.setPreferredSize(new Dimension(180, 500));
        tree_location.setBorder(BorderFactory.createEmptyBorder());
        middle_panel.add(BorderLayout.WEST, tree_location);
        middle_panel.add(BorderLayout.CENTER,tabbedPane);
        middle_panel.setBorder(BorderFactory.createEtchedBorder());
        add(BorderLayout.CENTER, middle_panel);

        // Create footer
        footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());
        footerLabel = new JLabel("");
        footerPanel.add(BorderLayout.CENTER, footerLabel);

        lsdelete_button = new JButton(resources.getString("qkey.menu.item.lsdelete"));
        lsdelete_button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String lsn_with_suffix = (String) lsdelete_button.getClientProperty(CURRENTLOCALSTORE);
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    JTextArea jta = new JTextArea("", 3, 15 + lsn_with_suffix.length());
                    jta.setText(resources.getString("qkey.msg.msg303") + lsn_with_suffix + resources.getString("qkey.msg.msg304"));
                    jta.setEditable(false);
                    jta.setBackground(Color.WHITE);
                    JScrollPane jsp = new JScrollPane(jta);
                    jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    panel.add(BorderLayout.CENTER, jsp);
                    JPanel cbpanel = new JPanel();
                    final JCheckBox del_file = new JCheckBox();
                    del_file.setSelected(false);
                    cbpanel.setLayout(new BorderLayout());
                    JLabel dellabel = new JLabel(resources.getString("qkey.msg.msg339"));
                    cbpanel.add(BorderLayout.WEST, dellabel);
                    cbpanel.add(BorderLayout.CENTER, del_file);
                    panel.add(BorderLayout.SOUTH, cbpanel);

                    popupConfirmationDialog(resources.getString("qkey.msg.msg302"), panel,
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                            new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    confirmDialog.setVisible(false);
                                    String lsn_with_suffix = (String) lsdelete_button.getClientProperty(CURRENTLOCALSTORE);

                                    try {
                                        LocalStoreProperty lsp = lsm.getLocalStoreProperty(getPureDestName(lsn_with_suffix));
                                        if (lsp == null) {
                                            throw new Exception("Local store not found in LocalStoreManager");
                                        }
                                        if (del_file.getSelectedObjects() != null) {
                                           lsm.clearLocalStore(getPureDestName(lsn_with_suffix));
                                        }
                                        lsm.removeLocalStoreProperty(lsp);
                                        //タブも消す
                                        int target_tab_index = tabbedPane.indexOfTab(lsn_with_suffix);
                                        jtableins.remove(lsn_with_suffix);
                                        tabbedPane.remove(target_tab_index);
                                        collectDestination();
                                        refreshLocalStoresOnMenu();
                                        initTreePane();
                                    } catch (Exception ree) {
                                        ree.printStackTrace();
                                    }

                                }
                            });

                }
            });



        lsdelete_menu = new JMenuItem("qkey.menu.item.lsdelete",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));
        lsdelete_menu.addActionListener(new DeleteLSListener2());
        


        lsclear_button = new JButton(resources.getString("qkey.menu.item.clear_localstore"));
            lsclear_button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    String lsn_with_suffix = (String) lsclear_button.getClientProperty(CURRENTLOCALSTORE);
                    JTextArea jta = new JTextArea("", 3, 25 + lsn_with_suffix.length());
                    jta.setText(resources.getString("qkey.msg.msg300") + lsn_with_suffix + resources.getString("qkey.msg.msg305"));
                    jta.setEditable(false);
                    jta.setBackground(Color.WHITE);

                    popupConfirmationDialog(resources.getString("qkey.msg.msg301"), jta,
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                            new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    confirmDialog.setVisible(false);
                                    String lsn_with_suffix = (String) lsclear_button.getClientProperty(CURRENTLOCALSTORE);
                                    try {
                                        lsm.clearLocalStore(getPureDestName(lsn_with_suffix));
                                    } catch (IOException ioe) {
                                        popupErrorMessageDialog(ioe);
                                    }
                                    refreshLocalStoreMsgTableWithFileReloading(lsn_with_suffix);

                                }
                            });

                }
            });

        reload_button = new JButton(resources.getString("qkey.menu.item.reload"));
        reload_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String lsn_with_suffix =  (String)reload_button.getClientProperty(CURRENTLOCALSTORE);
                refreshLocalStoreMsgTableWithFileReloading(lsn_with_suffix);
            }
        });

        //for tree
        reload_ls_menu  = new JMenuItem("qkey.msg.msg360",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Refresh));

            reload_ls_menu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (treePane != null) {
                        TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();
                        if (di != null) {
                            String lsn_with_suffix = di.name_with_suffix;
                            refreshLocalStoreMsgTableWithFileReloading(lsn_with_suffix);
                        }
                    }
                }
            });

        popupMenuTForLocalStore.add(new JSeparator());
        popupMenuTForLocalStore.add(reload_ls_menu);
        
        config_localstore_button = new JButton(resources.getString("qkey.menu.item.config_localstore"));
        config_localstore_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String lsn_with_suffix =  (String)config_localstore_button.getClientProperty(CURRENTLOCALSTORE);
                String puredestname = getPureDestName(lsn_with_suffix);
                LocalStoreProperty lsp = lsm.getLocalStoreProperty(puredestname);
                LocalStoreConfigPanel lscp = new LocalStoreConfigPanel();
                lscp.showConfigPanel(lsp, lsm , oya);
            }
        });

        config_localstore_menu  = new JMenuItem("qkey.menu.item.config_localstore",
                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.SvcDetails));

            config_localstore_menu.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (treePane != null) {
                        TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();
                        if (di != null) {
                            LocalStoreProperty lsp = lsm.getLocalStoreProperty(di.destinationName);
                            LocalStoreConfigPanel lscp = new LocalStoreConfigPanel();
                            lscp.showConfigPanel(lsp, lsm, oya);
                        }
                    }
                }
            });

        popupMenuTForLocalStore.add(config_localstore_menu);
        popupMenuTForLocalStore.add(lsdelete_menu);

        unsubscribe_button = new JButton(resources.getString("qkey.menu.item.unsubscribe"));
        unsubscribe_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                //Running→Destroyed

                //unsubscribe処理
                int sel_tab_index = tabbedPane.getSelectedIndex();
                String dispName = tabbedPane.getTitleAt(sel_tab_index);
                Boolean isRunning = (Boolean) subscribe_thread_status.get(dispName);
                JTable cTable = (JTable) jtableins.get(dispName);
                if ((isRunning != null) && isRunning.booleanValue()) {
                    SubscriberThread sthread = (SubscriberThread) subscribe_threads.get(dispName);
                   
                    

                        if (hasComponent(qbuttonpanel, unsubscribe_button))
                        qbuttonpanel.remove(unsubscribe_button);
                        qbuttonpanel.add(BorderLayout.WEST, subscribe_resume_button);
                        qbuttonpanel.updateUI();

                        try {
                          sthread.destroy();
                        } catch (Throwable td) { }
                        subscribe_thread_status.put(dispName, new Boolean(false));
                        subscribe_threads.remove(dispName);

                        String state_string = resources.getString("qkey.msg.msg137");
                        initTreePane();
                        setFooter(dispName + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(cTable.getRowCount()) + " " + state_string);

                }

            }
        });

        subscribe_resume_button = new JButton(resources.getString("qkey.menu.item.resumesubscribe"));
        subscribe_resume_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //Destroyed→Running
                //subscribe resume処理

                int sel_tab_index = tabbedPane.getSelectedIndex();
                String dispName = tabbedPane.getTitleAt(sel_tab_index);
                Boolean isRunning = (Boolean) subscribe_thread_status.get(dispName);
                JTable cTable = (JTable) jtableins.get(dispName);
                if ((isRunning == null) || !isRunning.booleanValue()) {

                    try {

                        SubscriberRunner srun = new SubscriberRunner();
                        srun.dest_full_name = dispName;
                        SubscriberThread sth = new SubscriberThread(srun);
                        sth.start();

                        subscribe_thread_status.put(dispName, new Boolean(true));
                        subscribe_threads.put(dispName, sth);

                        if (hasComponent(qbuttonpanel, subscribe_resume_button))
                        qbuttonpanel.remove(subscribe_resume_button);
                        qbuttonpanel.add(BorderLayout.WEST, unsubscribe_button);
                        qbuttonpanel.updateUI();
                        String state_string = resources.getString("qkey.msg.msg136");
                        initTreePane();
                        setFooter(dispName + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(cTable.getRowCount()) + " " + state_string);

                    } catch (Throwable tex) {
                        System.err.println(tex.getMessage());
                    }

                }

            }
        });

        qbuttonpanel = new JPanel();
        qbuttonpanel.setLayout(new BorderLayout());

        details = new JButton(resources.getString("qkey.menu.item.detail"));
        details.addActionListener(new DetailsListener());
        qbuttonpanel.add(BorderLayout.CENTER, details);

        footerPanel.add(BorderLayout.EAST, qbuttonpanel);

        add(BorderLayout.SOUTH, footerPanel);
        setFooter(resources.getString("qkey.msg.msg001"));

        //遅延コネクト

        if (isBrokerEE) {
            delete = new JButton(resources.getString("qkey.msg.msg002"));
            delete.addActionListener(dlis);
            qbuttonpanel.add(BorderLayout.EAST, delete);
        }


        setNotConnected();
        setFooter(resources.getString("qkey.msg.msg165"));
        //最初はどこにもつながっていないので、切断は使えない。
        disconnect_item.setEnabled(false);

        QBrowserUtil.setPanel_Parent(this);


      }


    }

    public void initQBrowserKey() {

        QBrowserKey qbk = new QBrowserKey();
        qbk.apply(this);

    }

    public void collectCurrentSelectedRows() {

        ext_messages = new ArrayList();
        targetX = null;

        int tabindex = tabbedPane.getSelectedIndex();

        if (tabindex != -1) {

            String tkey = tabbedPane.getTitleAt(tabindex);
            JTable cTable = (JTable) jtableins.get(tkey);

            int selrow = cTable.getSelectedRow();

            //複数行一括転送対応
            if (selrow != -1) {
                selrow = cTable.convertRowIndexToModel(selrow);

                TableModel mo = cTable.getModel();

                if (mo instanceof LocalMsgTable) {
                    LocalMsgTable mt = (LocalMsgTable) mo;

                    int[] target = cTable.getSelectedRows();
                    //int[] rows = cTable.getSelectedRows();
                    for (int i = 0; i < target.length; i++) {
                        target[i] = cTable.convertRowIndexToModel(target[i]);
                    }


                    targetX = cTable.getSelectedRows();
                    for (int i = 0; i < targetX.length; i++) {
                        targetX[i] = cTable.convertRowIndexToModel(targetX[i]);
                    }

                    for (int i = 0; i < target.length; i++) {
                        //コピーを入れる

                        ext_messages.add(mt.getMessageAtRow(target[i]));

                    }

                } else if (mo instanceof MsgTable) {
                    MsgTable mt = (MsgTable) mo;

                    int[] target = cTable.getSelectedRows();
                    //int[] rows = cTable.getSelectedRows();
                    for (int i = 0; i < target.length; i++) {
                        target[i] = cTable.convertRowIndexToModel(target[i]);
                    }


                    targetX = cTable.getSelectedRows();
                    for (int i = 0; i < targetX.length; i++) {
                        targetX[i] = cTable.convertRowIndexToModel(targetX[i]);
                    }

                    for (int i = 0; i < target.length; i++) {
                        //コピーを入れる

                        ext_messages.add(mt.getMessageAtRow(target[i]));

                    }

                }

            }

        }

    }

    public void initLocalStoreManager() {
        //ローカルストアプロパティ読込み&定義に従ってローカルストア復元
        lsm = new LocalStoreManager("openmq");
        refreshLocalStoresOnMenu();
    }

    public void refreshLocalStoresOnMenu() {
        localstore_on_menu.removeAll();
        Collection lspcol = lsm.listLocalStoreProperties();
        Iterator ilspcol = lspcol.iterator();
        while (ilspcol.hasNext()) {
            LocalStoreProperty lsp = (LocalStoreProperty) ilspcol.next();
            JMenuItem submenu = null;
            if (lsp.isValid()) {
                submenu = new JMenuItem(lsp.getDestName(),
                        QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Playing));
            } else {
                submenu = new JMenuItem(lsp.getDestName(),
                        QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Stopped));
            }
            submenu.addActionListener(new SelectLS_SubMenuListener(lsp.getDestName()));
            localstore_on_menu.add(submenu);
        }

    }

    AbstractButton createSaveChooseButton() {
        final JButton button = new JButton("...");
        button.setMnemonic('B');
        button.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (save_file_path.getText().length() > 0) {
                    _currentFolder = _folderChooser.getFileSystemView().createFileObject(save_file_path.getText());
                }
                _folderChooser.setCurrentDirectory(_currentFolder);
                _folderChooser.setRecentList(_recentList);
                _folderChooser.setFileHidingEnabled(true);
                int result = _folderChooser.showOpenDialog(button.getTopLevelAncestor());
                if (result == FolderChooser.APPROVE_OPTION) {
                    _currentFolder = _folderChooser.getSelectedFile();
                    if (_recentList.contains(_currentFolder.toString())) {
                        _recentList.remove(_currentFolder.toString());
                    }
                    _recentList.add(0, _currentFolder.toString());
                    File selectedFile = _folderChooser.getSelectedFile();
                    if (selectedFile != null) {
                        save_file_path.setText(selectedFile.toString());
                    }
                    else {
                        save_file_path.setText("");
                    }
                }
            }
        });
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        return button;
    }

    AbstractButton createFolderChooseButton() {
        final JButton button = new JButton("...");
        button.setMnemonic('B');
        button.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (folderchoose_file_path.getText().length() > 0) {
                    _currentFolder = _folderChooser.getFileSystemView().createFileObject(folderchoose_file_path.getText());
                }
                _folderChooser.setCurrentDirectory(_currentFolder);
                _folderChooser.setRecentList(_recentList);
                _folderChooser.setFileHidingEnabled(true);
                int result = _folderChooser.showOpenDialog(button.getTopLevelAncestor());
                if (result == FolderChooser.APPROVE_OPTION) {
                    _currentFolder = _folderChooser.getSelectedFile();
                    if (_recentList.contains(_currentFolder.toString())) {
                        _recentList.remove(_currentFolder.toString());
                    }
                    _recentList.add(0, _currentFolder.toString());
                    File selectedFile = _folderChooser.getSelectedFile();
                    if (selectedFile != null) {
                        folderchoose_file_path.setText(selectedFile.toString());
                    }
                    else {
                        folderchoose_file_path.setText("");
                    }
                }
            }
        });
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        return button;
    }


    boolean checkJDKVersion() {
        int jversion = QBrowserUtil.getVersionAsInt();
        //int jversion = 15;
        //System.out.println(jversion);
        if (jversion >= 16) {
            return true;
        } else {
            return false;
        }
    }


    void setNotConnected() {
        newmmenu.setEnabled(false);
        editmenu.setEnabled(false);
        displaymenu.setEnabled(false);
        openmessage_item.setEnabled(false);
        open_multimessage_item.setEnabled(false);
        localstoremenu.setEnabled(false);
        subscribemenu.setEnabled(false);
        cmdmenu.setEnabled(false);
        destcmdmenu.setEnabled(false);
        txncmdmenu.setEnabled(false);
        rawcmdmenu.setEnabled(false);

        qBrowse.setEnabled(false);
        qSearch.setEnabled(false);
        qBox.setEnabled(false);

        new_button.setEnabled(false);
        new_buttonf.setEnabled(false);
        open_message_button.setEnabled(false);
        open_multi_message_button.setEnabled(false);
        createlocalstore_button.setEnabled(false);
        select_all_button.setEnabled(false);
        copy_msg_button.setEnabled(false);
        paste_msg_button.setEnabled(false);
        subscribe_button.setEnabled(false);
        delete_button.setEnabled(false);
        search_button.setEnabled(false);

        connection_list_button.setEnabled(false);
        services_list_button.setEnabled(false);
        services_details_button.setEnabled(false);
        broker_details_button.setEnabled(false);
        config_printer_button.setEnabled(false);
        atesaki_info_button.setEnabled(false);
        atesaki_details_button.setEnabled(false);
        all_txn_button.setEnabled(false);
        purge_atesaki_button.setEnabled(false);
        txn_filter_button.setEnabled(false);
        cmd_input_button.setEnabled(false);

        delete.setEnabled(false);
        details.setEnabled(false);
    }

    void setConnected() {
        newmmenu.setEnabled(true);
        editmenu.setEnabled(true);
        displaymenu.setEnabled(true);
        openmessage_item.setEnabled(true);
        open_multimessage_item.setEnabled(true);
        localstoremenu.setEnabled(true);
        subscribemenu.setEnabled(true);
        cmdmenu.setEnabled(true);
        destcmdmenu.setEnabled(true);
        txncmdmenu.setEnabled(true);
        rawcmdmenu.setEnabled(true);

        qBrowse.setEnabled(true);
        qSearch.setEnabled(true);
        qBox.setEnabled(true);

        new_button.setEnabled(true);
        new_buttonf.setEnabled(true);
        open_message_button.setEnabled(true);
        open_multi_message_button.setEnabled(true);
        createlocalstore_button.setEnabled(true);
        select_all_button.setEnabled(true);
        copy_msg_button.setEnabled(true);
        paste_msg_button.setEnabled(true);
        subscribe_button.setEnabled(true);
        delete_button.setEnabled(true);
        search_button.setEnabled(true);

        connection_list_button.setEnabled(true);
        services_list_button.setEnabled(true);
        services_details_button.setEnabled(true);
        broker_details_button.setEnabled(true);
        config_printer_button.setEnabled(true);
        atesaki_info_button.setEnabled(true);
        atesaki_details_button.setEnabled(true);
        all_txn_button.setEnabled(true);
        purge_atesaki_button.setEnabled(true);
        txn_filter_button.setEnabled(true);
        cmd_input_button.setEnabled(true);

        delete.setEnabled(true);
        details.setEnabled(true);
    }

    void setOyaFrame(JFrame oya) {
        oya_frame = oya;
    }

    void shutdownJMS() {
        try {
            connection.close();
        } catch (JMSException e) {
            System.err.println("Exception closing JMS connection: " + e);
        } catch (Throwable tex) {
            System.err.println(tex.getMessage());
        }
    }

    int getNextFlagNum() {
        if (flagN == 7) {
            flagN = 0;
        }


        return ++flagN;
    }

    MessageContainer copyMessageContainer(MessageContainer srcmsg) throws Exception {
        if (srcmsg == null) return null;

        MessageContainer newmc = new MessageContainer();
        Message frommsg = srcmsg.getMessage();
        Message tomsg = null;
        if (frommsg != null) {
            tomsg = copyMessage(frommsg);
        }
        newmc.setMessage(tomsg);
        newmc.setDest_name_with_suffix(srcmsg.getDest_name_with_suffix());
        newmc.setVmsgid(srcmsg.getVmsgid());
        newmc.setVcorrelationid(srcmsg.getVcorrelationid());
        newmc.setVdeliverymode(srcmsg.getVdeliverymode());
        newmc.setVexpiration(srcmsg.getVexpiration());
        newmc.setVjms_type(srcmsg.getVjms_type());
        newmc.setVpriority(srcmsg.getVpriority());
        newmc.setVredelivered(srcmsg.isVredelivered());
        newmc.setVdest(srcmsg.getVdest());
        newmc.setVreplyto(srcmsg.getVreplyto());

        return newmc;

    }

    Message copyMessage(Message srcmsg) throws JMSException {
        
        if (srcmsg == null) return null;

        Message copyresultmsg = null;

        //タイプ別に応じてメッセージフレーム作成とボディ特有データコピー
        if (srcmsg instanceof TextMessage) {

           TextMessage txtmsg = session.createTextMessage();
           TextMessage fromtxtmsg = (TextMessage)srcmsg;
           txtmsg.setText(fromtxtmsg.getText());
           copyresultmsg = txtmsg;

        } else if (srcmsg instanceof BytesMessage) {

           BytesMessage bmsg = session.createBytesMessage();
           BytesMessage frombmsg = (BytesMessage)srcmsg;
           frombmsg.reset();

           byte[] bibi = new byte[1024];
           int len = 0;

           while ((len = frombmsg.readBytes(bibi)) != -1) {
                        bmsg.writeBytes(bibi, 0, len);
           }

           copyresultmsg = bmsg;

        } else if (srcmsg instanceof MapMessage) {

           MapMessage mmsg = session.createMapMessage();
           MapMessage frommmsg = (MapMessage)srcmsg;

           for (Enumeration enu = frommmsg.getMapNames();
                    enu.hasMoreElements();) {
                String name = (enu.nextElement()).toString();
                Object obj = frommmsg.getObject(name);

                if (obj instanceof String) {

                      mmsg.setString(name, (String)obj);

                } else if (obj instanceof Integer) {

                      mmsg.setInt(name, (Integer)obj);
                    
                } else if (obj instanceof Boolean) {

                      mmsg.setBoolean(name, (Boolean)obj);
                    
                } else if (obj instanceof Byte) {

                      mmsg.setByte(name, (Byte)obj);

                } else if (obj instanceof Double) {

                      mmsg.setDouble(name, (Double)obj);

                } else if (obj instanceof Float) {

                      mmsg.setFloat(name, (Float)obj);

                } else if (obj instanceof Long) {

                      mmsg.setLong(name, (Long)obj);

                } else if (obj instanceof Short) {

                      mmsg.setShort(name, (Short)obj);

                } else if (obj instanceof byte[]) {

                      mmsg.setBytes(name, (byte[])obj);

                } else {
                      mmsg.setObject(name, obj);
                }

            }

            copyresultmsg = mmsg;


        } else if (srcmsg instanceof StreamMessage) {

           StreamMessage smsg = session.createStreamMessage();
           StreamMessage fromsmsg = (StreamMessage)srcmsg;
           fromsmsg.reset();

                Object ro = null;
                try {
                while ((ro = ((StreamMessage)fromsmsg).readObject()) != null) {

                    if (ro instanceof Boolean) {

                       smsg.writeBoolean(((Boolean)ro).booleanValue());

                    } else if (ro instanceof Byte) {

                       smsg.writeByte(((Byte)ro).byteValue());

                    } else if (ro instanceof Short) {

                       smsg.writeShort(((Short)ro).shortValue());

                    } else if (ro instanceof java.lang.Character) {

                       smsg.writeChar(((Character)ro).charValue());

                    } else if (ro instanceof java.lang.Integer) {

                      smsg.writeInt(((Integer)ro).intValue());

                    } else if (ro instanceof java.lang.Long) {

                      smsg.writeLong(((Long)ro).longValue());

                    } else if (ro instanceof java.lang.Float) {

                      smsg.writeFloat(((Float)ro).floatValue());

                    } else if (ro instanceof java.lang.Double) {

                      smsg.writeDouble(((Double)ro).doubleValue());

                    } else if (ro instanceof java.lang.String) {

                      smsg.writeString((String)ro);

                    } else if (ro instanceof byte[]) {

                        byte[] bytearray = (byte[])ro;
                        smsg.writeBytes(bytearray);

                    }
                }
                } catch (MessageEOFException eof) {
                    //NOP
                } catch (Throwable thex) {
                    //NOP
                }

           copyresultmsg = smsg;

        } else if (srcmsg instanceof ObjectMessage) {

           ObjectMessage omsg = session.createObjectMessage();
           ObjectMessage fromomsg = (ObjectMessage)srcmsg;
           omsg.setObject(fromomsg.getObject());

           copyresultmsg = omsg;
            
        } else if (srcmsg instanceof Message) {
            Message omsg = session.createMessage();
            copyresultmsg = omsg;
        }

        if (copyresultmsg != null) {

            //ヘッダ情報コピー
            QBrowserUtil.copyMessageHeaders(srcmsg, copyresultmsg);

            //ユーザプロパティコピー
            QBrowserUtil.copyUserProperties(srcmsg, copyresultmsg);

        }

       
        return copyresultmsg;
    }

    void setHeadersToMessageProducer(Message srcmsg, MessageProducer sender) throws JMSException  {
         if (srcmsg.getJMSDeliveryMode() != srcmsg.DEFAULT_DELIVERY_MODE) {
             sender.setDeliveryMode(srcmsg.getJMSDeliveryMode());
         }

         if (srcmsg.getJMSPriority() != srcmsg.DEFAULT_PRIORITY) {
             sender.setPriority(srcmsg.getJMSPriority());
         }

         if (srcmsg.getJMSExpiration() != srcmsg.DEFAULT_TIME_TO_LIVE) {
             sender.setTimeToLive(srcmsg.getJMSExpiration());
         }
         
    }

    void setLocalMessageContainerHeadersToMessageProducer(LocalMessageContainer mc, MessageProducer sender) throws Exception  {

        Message imsg = mc.getMessage();
        if (imsg == null) {
            imsg = mc.getRealMessage(session);
        }

        if (mc.getVdeliverymode() != imsg.DEFAULT_DELIVERY_MODE) {
             sender.setDeliveryMode(mc.getVdeliverymode());
         }

         if (mc.getVpriority() != imsg.DEFAULT_PRIORITY) {
             sender.setPriority(mc.getVpriority());
         }

         if (mc.getVexpiration() != imsg.DEFAULT_TIME_TO_LIVE) {
             sender.setTimeToLive(mc.getVexpiration());
         }

    }



    void copyUserProperties(Message srcmsg, Message destmsg) throws JMSException {

        if ((srcmsg == null) || (destmsg == null)) {
            return;
        }

        for (Enumeration enu = srcmsg.getPropertyNames();
                enu.hasMoreElements();) {

            String name = (enu.nextElement()).toString();
            Object propvalueobj = srcmsg.getObjectProperty(name);
            if (propvalueobj instanceof String) {

                destmsg.setStringProperty(name, (String)propvalueobj);

            } else if (propvalueobj instanceof Integer) {

                destmsg.setIntProperty(name, (Integer)propvalueobj);

            } else if (propvalueobj instanceof Boolean) {

                destmsg.setBooleanProperty(name, (Boolean)propvalueobj);

            } else if (propvalueobj instanceof Byte) {

                destmsg.setByteProperty(name, (Byte)propvalueobj);

            } else if (propvalueobj instanceof Double) {

                destmsg.setDoubleProperty(name, (Double)propvalueobj);

            } else if (propvalueobj instanceof Float) {

                destmsg.setFloatProperty(name, (Float)propvalueobj);

            } else if (propvalueobj instanceof Long) {

                destmsg.setLongProperty(name, (Long)propvalueobj);

            } else if (propvalueobj instanceof Short) {

                destmsg.setShortProperty(name, (Short)propvalueobj);

            } else {
                
                destmsg.setObjectProperty(name, propvalueobj);
            }
        }

    }

    private File[] checkAcceptable(Transferable transfer) {
        try {
            if (transfer.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

                List filelist = (List) transfer.getTransferData(DataFlavor.
                        javaFileListFlavor);
                File[] files = (File[]) filelist.toArray(new File[0]);
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        //
                        return null;
                    }
                }
                return files;
            }
            return null;
        } catch (IOException ex) {
            return null;
        } catch (UnsupportedFlavorException ex) {
            return null;
        }
    }

    void initDestinationList() throws Exception {
        destinationNamesForDisplayQueue = new ArrayList();
        destinationNamesForDisplayTopic = new ArrayList();

        collectDestination();
    }

    /**
     * Initialize JMS by creating Connection and Session.
     */
    void initJMS() throws JMSException {
        ConnectionFactory cf = null;


        try {

        cf = new com.sun.messaging.ConnectionFactory();

        ((com.sun.messaging.ConnectionFactory) cf).setProperty(
                com.sun.messaging.ConnectionConfiguration.imqBrokerHostName,
                serverHost);
        ((com.sun.messaging.ConnectionFactory) cf).setProperty(
                com.sun.messaging.ConnectionConfiguration.imqBrokerHostPort,
                String.valueOf(serverPort));

       
        connection = cf.createConnection(serverUser, serverPassword);
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        destinationNamesForDisplayQueue = new ArrayList();
        destinationNamesForDisplayTopic = new ArrayList();

        collectDestination();

        } catch (Throwable tt) {
            System.err.println("initJMS : " + tt.getMessage());
        }
    }

    /**
     * Setup a consumer that listens on the Message Queue monitoring topic
     * that sends out lists of destinations.
     */
    public void initDestListConsumer() throws JMSException {
        
        //リスト一覧取得方法変更
        /*
        metricTopic = session.createTopic(DEST_LIST_TOPIC_NAME);
        metricSubscriber = session.createConsumer(metricTopic);
        metricSubscriber.setMessageListener(this);
        */
    }

    /*
    public void reinitDestListConsumer() throws JMSException {
        metricSubscriber = session.createConsumer(metricTopic);
        metricSubscriber.setMessageListener(this);
    }
    */

    /**
     * Set text on footer
     */
    void setFooter(String s) {
        footerLabel.setText(s);
        footerLabel.paintImmediately(footerLabel.getBounds());
    }

    javax.jms.Queue getQueue(String purename) throws Exception {
          return session.createQueue(purename);
    }

    void createAndStartSubscriberThread(String compl_subscribename) {
        SubscriberRunner srun = new SubscriberRunner();
        srun.dest_full_name = compl_subscribename;

        SubscriberThread sth = new SubscriberThread(srun);
        sth.start();

        subscribe_thread_status.put(compl_subscribename, new Boolean(true));
        subscribe_threads.put(compl_subscribename, sth);
    }

    void restartSubscriberThreadAlongWithCurrentStatus(String compl_subscribename) {
              boolean cu_status = isSubscriberThreadRunning(compl_subscribename);
              stopSubscriberThread(compl_subscribename);
              //今現在購読中の場合のみスレッドを再作成
              if (cu_status)
              createAndStartSubscriberThread(compl_subscribename);
    }

    public boolean isSubscriberThreadRunning(String compl_subscribename) {
        Boolean isRunningT = (Boolean)subscribe_thread_status.get(compl_subscribename);
        if ((isRunningT != null) && (isRunningT.booleanValue())) {
            return true;
        } else {
            return false;
        }
    }


    void closeAllAdditionalDetailWindows(String msgid) {
        DisplayMsgDialogFactory.closeAllCurrentMsgDialog(msgid);
        JPanel curr_panel = qbrowsercache.getCurrentBodyPanel(msgid);
        if ((curr_panel != null) && (curr_panel instanceof MapMessageAllPropertiesPanel)) {
         MapMessageAllPropertiesPanel mmap = (MapMessageAllPropertiesPanel)curr_panel;
         MapMessageAllPropertiesTable mapm_all_property_table = mmap.getMapm_all_property_table();
         if (mapm_all_property_table != null) {
            mapm_all_property_table.cleanupAllPropertyDownloadPanels();
         }
        }
    }

    void stopSubscriberThread(String compl_subscribename) {
        SubscriberThread sthread = (SubscriberThread) subscribe_threads.get(compl_subscribename);
             if (sthread != null)
             sthread.destroy();

             subscribe_thread_status.put(compl_subscribename, new Boolean(false));
             subscribe_threads.remove(compl_subscribename);
    }

    void cleanupSubscriberThreads() {
        Collection col = subscribe_threads.values();
        Iterator icol = col.iterator();
        while (icol.hasNext()) {
            try {
              SubscriberThread sth = (SubscriberThread)icol.next();
              sth.destroy();
            } catch (Throwable e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /*
    void clearShowDetails() {
            headerPanel = null;
            mapmBodyForAPPanel = null;
            smBodyForAPPanel = null;
            propertyPanel = null;
            bodyPanel = null;
            bodycontainer = null;
            downloadbodyPanel = null;
            downloadfilepath = null;
            downloadmsg = null;
            downloadbutton = null;
            detailbg = null;
            mapm_all_property_table = null;
            maTable = null;
            sm_all_property_table = null;
            saTable = null;
    }
   */


    void showDetails(LocalMessageContainer msg, int msgno) {
        try {
            Message imsg = msg.getMessage();
            if (imsg == null) {
                imsg = msg.getRealMessage(session);
            }
            showDetails((MessageContainer) msg, msgno);
            ReadOnlyHeaderPropertyTable details_headertable =
                    qbrowsercache.getCurrentReadOnlyHeaderTable(msg.getVmsgid());
            details_headertable.loadAllProperties(msg);
        } catch (Exception e) {
            popupErrorMessageDialog(e);
        }
    }

    /**
     * Show the contents of a message in a seperate popup window
     */
    void showDetails(final MessageContainer msg, int msgno) {
        // Create popup


        //if ((detailsFrame != null) && detailsFrame.isShowing()) {
        //    detailsFrame.dispose();
        //    clearShowDetails();
        //}

        Message imsg = msg.getMessage();
        if (imsg == null) {
            try {
            Queue queue = getQueue(msg.getPureDest_name());
            imsg = msg.getRealMessageFromBroker(session, queue);
            } catch (Exception reale) {
                reale.printStackTrace();
            }
        }
                
        final JFrame detailsFrame = new JFrame();
        detailsFrame.setTitle(resources.getString("qkey.msg.msg160"));
        detailsFrame.setBackground(Color.white);
        detailsFrame.getContentPane().setLayout(new BorderLayout());
        detailsFrame.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter).getImage());

        detailsFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                closeAllAdditionalDetailWindows(msg.getVmsgid());
                qbrowsercache.clearMsgIdRelatedCache(msg.getVmsgid());
                DisplayDialogThreadPoolForShowDetails.cleanupDisplayThreads(msg.getVmsgid());
                
            }
        });

        //cache frame
        qbrowsercache.setCurrentDetailFrame(msg.getVmsgid(), detailsFrame);

        ReadOnlyHeaderPropertyTable details_headertable = new ReadOnlyHeaderPropertyTable(0);
        qbrowsercache.setCurrentReadOnlyHeaderTable(msg.getVmsgid(), details_headertable);
        JTable hhTable = new JTable(details_headertable);
        //System.out.println("msg = " + msg);
        int retv = details_headertable.loadAllProperties(msg);
        if (retv == -1) {
            this.popupErrorMessageDialog(new Exception(resources.getString("qkey.msg.err.Q0029")));
            return;
        }
        TableRowSorter trs = new TableRowSorter(details_headertable);

        hhTable.setRowSorter(trs);
        hhTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        hhTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());
        hhTable.setPreferredScrollableViewportSize(new Dimension(500,161));

        TableColumn hcolumn = hhTable.getColumnModel().getColumn(0);
        hcolumn.setPreferredWidth(120);
        hcolumn.sizeWidthToFit();
        HeaderRenderer01 hhr = new HeaderRenderer01();
        hcolumn.setHeaderRenderer(hhr);

        hcolumn = hhTable.getColumnModel().getColumn(1);
        hcolumn.setPreferredWidth(380);
        hcolumn.setHeaderRenderer(hhr);

        JScrollPane htablePane = new JScrollPane(hhTable);
        JPanel hh = new JPanel();
        hh.setLayout(new BorderLayout());
        JLabel hh_header = new JLabel(resources.getString("qkey.msg.jmsheader"));
        hh.add(hh_header, BorderLayout.NORTH);
        hh.add(htablePane, BorderLayout.CENTER);

        detailsFrame.getContentPane().add(BorderLayout.NORTH, hh);

        final ReadOnlyPropertyTable propertytable = new ReadOnlyPropertyTable();
        JTable ppTable = new JTable(propertytable);
        RowSorter.SortKey sk = new RowSorter.SortKey(0, SortOrder.ASCENDING);
        ArrayList keys = new ArrayList();
        keys.add(sk);
        TableRowSorter trs2 = new TableRowSorter(propertytable);
        trs2.setSortKeys(keys);
        ppTable.setRowSorter(trs2);
        TableColumn column = ppTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(200);
        column.sizeWidthToFit();
        HeaderRenderer01 hr = new HeaderRenderer01();
        column.setHeaderRenderer(hr);

        column = ppTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(100);
        column.setHeaderRenderer(hr);

        column = ppTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(200);
        column.setHeaderRenderer(hr);

        ListCellEditor pdce2 = new ListCellEditor();
        pdce2.setClickCountToStart(0);
        column.setCellRenderer(new com.qbrowser.render.ListCellRenderer());
        column.setCellEditor(pdce2);

        
        propertytable.load(msg);
        final HashMap phm = new HashMap();

        for (int ri = 0; ri < propertytable.getRowCount(); ri++) {
                    Property py = (Property) propertytable.getPropertyAtRow(ri);
                    if (py.getProperty_type().equals(Property.STRING_TYPE)) {
                        JTextArea jta = (JTextArea) py.getProperty_value();
                        phm.put(jta.getText(), py.getKey());

                        jta.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mousePressed(MouseEvent e) {
                                if (SwingUtilities.isLeftMouseButton(e)) {

                                      //一定期間内の再クリックは無効に
                                      if ((rclick + 10000000) < System.nanoTime()) {
                                      rclick = System.nanoTime();

                                      JTextArea ta = new JTextArea();
                                      ta.setColumns(90);
                                      ta.setRows(30);
                                      ta.setLineWrap(true);


                                      try {

                                        String gett = ((JTextArea) e.getSource()).getText();
                                        String pkey = (String)phm.get(gett);
                                        String recovered_string = propertytable.getInnerMessage().getStringProperty(pkey);
                                        ta.setText(recovered_string);

                                      } catch (Throwable thex) { thex.printStackTrace();}



                                      DisplayMsgDialogFactory.popupDisposalMessageDialog(msg.getVmsgid(),resources.getString("qkey.msg.msg232"), createSearchableTextArea(ta),
                                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter), oya_frame);


                                      }


                                }

                            }
                        });

                    }
                }

        ppTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        ppTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());
        ppTable.setPreferredScrollableViewportSize(new Dimension(500,130));
        JScrollPane tablePane = new JScrollPane(ppTable);
        JPanel pp = new JPanel();
        pp.setLayout(new BorderLayout());
        JLabel pp_header = new JLabel(resources.getString("qkey.msg.msg161"));
        pp.add(pp_header, BorderLayout.NORTH);
        pp.add(tablePane, BorderLayout.CENTER);

        detailsFrame.getContentPane().add(BorderLayout.CENTER, pp);

        PropertyPanel bodyPanel = new TextMessageBodyPanel();
        bodyPanel.setTitle(resources.getString("qkey.msg.msg159"));

        JPanel bodycontainer = new JPanel();
        qbrowsercache.setCurrentBodyContainerPanel(msg.getVmsgid(), bodycontainer);
        
        bodycontainer.setLayout(new BorderLayout());

        bodycontainer.add(BorderLayout.SOUTH, bodyPanel);

        //details_body_current = bodyPanel;
        qbrowsercache.setCurrentBodyPanel(msg.getVmsgid(), bodyPanel);

        //バイトメッセージの場合はダウンロード選択ラジオボタンを追加
        if ((QBrowserV2.messageType(msg.getMessage()) != null)) {
            if (QBrowserV2.messageType(msg.getMessage()).equals(TEXTMESSAGE)) {
                bodyPanel.load(jmsMsgBodyAsString(msg.getMessage()));
            } else if (QBrowserV2.messageType(msg.getMessage()).equals(BYTESMESSAGE)) {
                //ヘクサ内容表示
                bodyPanel.load(jmsMsgBodyAsString(msg.getMessage()));

                JLabel jl03 = new JLabel(resources.getString("qkey.msg.msg003") + " Hex  ");

                JPanel bodytypepanel = new JPanel();
                bodytypepanel.setLayout(new BorderLayout());

                bodytypepanel.add(jl03, BorderLayout.WEST);

                JPanel pbodytypepanel = new JPanel();
                pbodytypepanel.setLayout(new BorderLayout());
                pbodytypepanel.add(bodytypepanel, BorderLayout.WEST);

                bodycontainer.add(pbodytypepanel,BorderLayout.NORTH);

                JPanel downloadbodyPanel = new JPanel();
                downloadbodyPanel.setLayout(new BorderLayout());

                JTextField downloadfilepath = new JTextField(20);
                //System.err.println("vmsgid = " + msg.getVmsgid());
                qbrowsercache.setCurrentDownloadFilePath(msg.getVmsgid(), downloadfilepath);

                JPanel downloadmsgpanel = new JPanel();
                JLabel downloadmsg = new JLabel("");
                qbrowsercache.setCurrentDownloadMsgLabel(msg.getVmsgid(), downloadmsg);
                

                downloadmsg.setForeground(Color.RED);
                downloadmsgpanel.add(downloadmsg);
                downloadbodyPanel.add(downloadmsgpanel, BorderLayout.SOUTH);

                
                downloadbodyPanel.add(downloadfilepath, BorderLayout.CENTER);

                JLabel jl04 = new JLabel(resources.getString("qkey.msg.msg004"));
                downloadbodyPanel.add(jl04, BorderLayout.WEST);

                JButton downloadbutton = new JButton(resources.getString("qkey.msg.msg005"));
                qbrowsercache.setCurrentDownloadButton(msg.getVmsgid(), downloadbutton);
                downloadfilepath.addCaretListener(new DownloadpathInputListener(msg.getVmsgid()));

                downloadbutton.setEnabled(false);
                downloadbutton.addActionListener(new DownloadListener(msg.getVmsgid()));
                downloadbodyPanel.add(downloadbutton, BorderLayout.EAST);
                downloadbodyPanel.setBorder(BorderFactory.createEtchedBorder());
                bodytypepanel.add(downloadbodyPanel, BorderLayout.EAST);
                //MessageContainer currentDownloadTargetMsg = msg;
                //currentDownloadTargetMsgMap.put(msg.getVmsgid(), msg);
                qbrowsercache.setCurrentDownloadTargetMsg(msg);

            //MAPMESSGEの処理
            } else if (QBrowserV2.messageType(msg.getMessage()).equals(MAPMESSAGE)) {

                //いきなりつけかえる。
                JRadioButton ritem00 = new JRadioButton(resources.getString("qkey.msg.msg197"));
                ritem00.addActionListener(new MapMessageBodyInputTypeListener(msg));
                ritem00.setSelected(true);
                JRadioButton ritem01 = new JRadioButton(resources.getString("qkey.msg.msg189"));
                ritem01.addActionListener(new MapMessageBodyInputTypeListener(msg));
                JRadioButton ritem02 = new JRadioButton(resources.getString("qkey.msg.msg188"));
                ritem02.addActionListener(new MapMessageBodyInputTypeListener(msg));

                ButtonGroup detailbg = new ButtonGroup();
                detailbg.add(ritem00);
                detailbg.add(ritem01);
                detailbg.add(ritem02);

                JLabel jl03 = new JLabel(resources.getString("qkey.msg.msg003"));

                JPanel bodytypepanel = new JPanel();

                bodytypepanel.add(jl03);
                bodytypepanel.add(ritem00);
                bodytypepanel.add(ritem01);
                bodytypepanel.add(ritem02);

                JPanel pbodytypepanel = new JPanel();
                pbodytypepanel.setLayout(new BorderLayout());
                pbodytypepanel.add(BorderLayout.WEST, bodytypepanel);

                bodycontainer.add(BorderLayout.NORTH, pbodytypepanel);

                //Display all properties as table format.

                //カレントbody_Panelの取得
                JPanel current_body = qbrowsercache.getCurrentBodyPanel(msg.getVmsgid());
                bodycontainer.remove(current_body);
                //createMapMessageAllPropertiesPanel();
                MapMessageAllPropertiesPanel mapmBodyForAPPanel = new MapMessageAllPropertiesPanel();
                maTable = mapmBodyForAPPanel.getMaTable();


                TableColumn column2 = maTable.getColumnModel().getColumn(2);
                ListCellEditor lce2 = new ListCellEditor();
                lce2.setClickCountToStart(0);
                column2.setCellEditor(lce2);
                column2.setCellRenderer(new com.qbrowser.render.ListCellRenderer());


                TableColumn column3 = maTable.getColumnModel().getColumn(3);
                DownloadCellEditor dce3 = new DownloadCellEditor();
                DownloadCellRenderer dcr3 = new DownloadCellRenderer();
                dce3.setClickCountToStart(0);
                column3.setCellEditor(dce3);
                column3.setCellRenderer(dcr3);
                //データ挿入、全プロパティ
                MapMessageAllPropertiesTable mapm_all_property_table = mapmBodyForAPPanel.getMapm_all_property_table();
                int rt = mapm_all_property_table.load(msg);
                final HashMap hm = new HashMap();

                for (int ri = 0; ri < mapm_all_property_table.getRowCount(); ri++) {
                    MapMessageAllProperties mmap = (MapMessageAllProperties) mapm_all_property_table.getPropertyAtRow(ri);
                    if (mmap.getProperty_type().equals(Property.STRING_TYPE)) {
                        JTextField jtf = (JTextField) mmap.getProperty_value();
                        hm.put(jtf.getText(), mmap.getKey());


                        jtf.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mousePressed(final MouseEvent e) {
                                //System.out.println("mousePressed called.");
                                if (SwingUtilities.isLeftMouseButton(e)) {

                                      //一定期間内の再クリックは無効に
                                      if (e.getClickCount() == 1) {

                                      final JTextArea ta = new JTextArea();
                                      ta.setColumns(90);
                                      ta.setRows(30);
                                      ta.setLineWrap(true);

                                      String gett = ((JTextField) e.getSource()).getText();
                                      String mpkey = (String)hm.get(gett);

                                      DisplayPropRunner dpr = new DisplayPropRunner(msg.getVmsgid() ,mpkey, ta, e.getSource().toString(), detailsFrame);
                                      Thread dprth = new Thread(dpr);
                                      //display_threads.add(dprth);
                                      DisplayDialogThreadPoolForShowDetails.addDisplayThread(msg.getVmsgid(),dpr, dprth);
                                      dprth.start();

                                    }

                                    
                                }

                            }
                        });

                    }
                }

                qbrowsercache.setCurrentMapmBodyForAPPanel(msg.getVmsgid(), mapmBodyForAPPanel);

                bodycontainer.add(BorderLayout.CENTER, mapmBodyForAPPanel);
                qbrowsercache.setCurrentBodyPanel(msg.getVmsgid(), mapmBodyForAPPanel);
                //details_body_current = mapmBodyForAPPanel;
                JPanel downloadmsgpanel = new JPanel();
                JLabel downloadmsg = new JLabel("");
                qbrowsercache.setCurrentDownloadMsgLabel(msg.getVmsgid(), downloadmsg);

                downloadmsgpanel.add(downloadmsg);
                bodycontainer.add(BorderLayout.SOUTH, downloadmsgpanel);
                bodycontainer.updateUI();
                //currentDownloadTargetMsg = msg;
                qbrowsercache.setCurrentDownloadTargetMsg(msg);
                
            } else if (QBrowserV2.messageType(msg.getMessage()).equals(STREAMMESSAGE)) {
                //いきなりつけかえる。
                JRadioButton ritem00 = new JRadioButton(resources.getString("qkey.msg.msg197"));
                ritem00.addActionListener(new StreamMessageBodyInputTypeListener(msg.getVmsgid(), msg.getMessage()));
                ritem00.setSelected(true);
                JRadioButton ritem01 = new JRadioButton(resources.getString("qkey.msg.msg189"));
                ritem01.addActionListener(new StreamMessageBodyInputTypeListener(msg.getVmsgid(), msg.getMessage()));
                JRadioButton ritem02 = new JRadioButton(resources.getString("qkey.msg.msg188"));
                ritem02.addActionListener(new StreamMessageBodyInputTypeListener(msg.getVmsgid(), msg.getMessage()));

                ButtonGroup detailbg = new ButtonGroup();
                detailbg.add(ritem00);
                detailbg.add(ritem01);
                detailbg.add(ritem02);

                JLabel jl03 = new JLabel(resources.getString("qkey.msg.msg003"));

                JPanel bodytypepanel = new JPanel();

                bodytypepanel.add(jl03);
                bodytypepanel.add(ritem00);
                bodytypepanel.add(ritem01);
                bodytypepanel.add(ritem02);

                JPanel pbodytypepanel = new JPanel();
                pbodytypepanel.setLayout(new BorderLayout());
                pbodytypepanel.add(BorderLayout.WEST, bodytypepanel);

                bodycontainer.add(BorderLayout.NORTH, pbodytypepanel);

                //Display all properties as table format.
                JPanel current_body = qbrowsercache.getCurrentBodyPanel(msg.getVmsgid());
                bodycontainer.remove(current_body);

                //createStreamMessageAllPropertiesPanel();
                StreamMessageAllPropertiesPanel smBodyForAPPanel = new StreamMessageAllPropertiesPanel();
                JTable saTable = smBodyForAPPanel.getSaTable();

                TableColumn column0 = saTable.getColumnModel().getColumn(0);
                column0.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

                TableColumn column2 = saTable.getColumnModel().getColumn(2);
                ListCellEditor lce2 = new ListCellEditor();
                lce2.setClickCountToStart(0);
                column2.setCellEditor(lce2);
                column2.setCellRenderer(new com.qbrowser.render.ListCellRenderer());


                TableColumn column3 = saTable.getColumnModel().getColumn(3);
                DownloadCellEditor dce3 = new DownloadCellEditor();
                DownloadCellRenderer dcr3 = new DownloadCellRenderer();
                dce3.setClickCountToStart(0);
                column3.setCellEditor(dce3);
                column3.setCellRenderer(dcr3);
                //データ挿入、全プロパティ
                StreamMessageAllPropertiesTable sm_all_property_table = smBodyForAPPanel.getStreamMessageAllPropertiesTable();
                int rt = sm_all_property_table.load(msg);
                final HashMap hm = new HashMap();

                for (int ri = 0; ri < sm_all_property_table.getRowCount(); ri++) {
                    StreamMessageAllProperties smap = (StreamMessageAllProperties) sm_all_property_table.getPropertyAtRow(ri);
                    if (smap.getProperty_type().equals(Property.STRING_TYPE)) {
                        JTextField jtf = (JTextField) smap.getProperty_value();
                        hm.put(jtf.getText(), smap.getKey());


                        jtf.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mousePressed(final MouseEvent e) {
                                //System.out.println("mousePressed called.");
                                if (SwingUtilities.isLeftMouseButton(e)) {

                                      //一定期間内の再クリックは無効に
                                      if (e.getClickCount() == 1) {

                                      final JTextArea ta = new JTextArea();
                                      ta.setColumns(90);
                                      ta.setRows(30);
                                      ta.setLineWrap(true);

                                      String gett = ((JTextField) e.getSource()).getText();
                                      String mpkey = (String)hm.get(gett);

                                      DisplayPropRunner2 dpr = new DisplayPropRunner2(msg.getVmsgid() ,mpkey, ta, e.getSource().toString(), detailsFrame.getRootPane());
                                      Thread dprth = new Thread(dpr);
                                      //display_threads.add(dprth);
                                      DisplayDialogThreadPoolForShowDetails.addDisplayThread(msg.getVmsgid(),dpr, dprth);
                                      dprth.start();

                                    }


                                }

                            }
                        });

                    }
                }

                qbrowsercache.setCurrentSmBodyForAPPanel(msg.getVmsgid(), smBodyForAPPanel);

                bodycontainer.add(BorderLayout.CENTER, smBodyForAPPanel);
                //details_body_current = smBodyForAPPanel;
                qbrowsercache.setCurrentBodyPanel(msg.getVmsgid(), smBodyForAPPanel);
                JPanel downloadmsgpanel = new JPanel();
                JLabel downloadmsg = new JLabel("");
                qbrowsercache.setCurrentDownloadMsgLabel(msg.getVmsgid(), downloadmsg);

                downloadmsgpanel.add(downloadmsg);
                bodycontainer.add(BorderLayout.SOUTH, downloadmsgpanel);
                bodycontainer.updateUI();

                //currentDownloadTargetMsg = msg;
                qbrowsercache.setCurrentDownloadTargetMsg(msg);
            } else {

                bodyPanel.load(jmsMsgBodyAsString(msg.getMessage()));

            }

        }

        detailsFrame.getContentPane().add(BorderLayout.SOUTH, bodycontainer);
        detailsFrame.pack();

        // Load message properties
        HashMap props = new HashMap();
        // Get all message properties and stuff into a hash table
        try {
            for (Enumeration enu = imsg.getPropertyNames();
                    enu.hasMoreElements();) {

                StringBuilder sb = new StringBuilder();
                String name = (enu.nextElement()).toString();
                sb.append(resources.getString("qkey.msg.msg184")).append(" : ").append(name);
                String decorated_name = sb.toString();
                sb = new StringBuilder();
                Object propvalueobj = imsg.getObjectProperty(name);
                sb.append(PropertyUtil.selfDescribe(propvalueobj));
                props.put(decorated_name, sb.toString());
            }
        } catch (JMSException ex) {
            setFooter("Error: " + ex.getMessage());
        }

        // Load message body
        bodyPanel.setTitle(resources.getString("qkey.msg.msg164") + QBrowserV2.messageType(imsg) + ")");
        //テキストパネルが要求されるまで遅延させる

        detailsFrame.setLocationRelativeTo(oya);
        detailsFrame.setVisible(true);
    }

    void popupErrorMessageDialog(Exception e) {

        if (errDialog != null && errDialog.isShowing()) {
            errDialog.dispose();
        }
        errDialog = new JDialog();
        errDialog.setLocation(120, 120);
        TextArea ta = new TextArea("", 10, 50, TextArea.SCROLLBARS_NONE);
        ta.setEditable(true);
        ta.append(e.getMessage());
        ta.append("\n");
        e.printStackTrace();
        //ta.append(e.toString());


        JPanel mainmsg = new JPanel();
        mainmsg.add(ta);

        errDialog.getContentPane().setLayout(new BorderLayout());

        JButton confirmbutton = new JButton("OK");
        confirmbutton.addActionListener(new ErrorConfirmedListener());

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());
        buttonpanel.add(BorderLayout.EAST, confirmbutton);

        errDialog.getContentPane().add(BorderLayout.SOUTH, buttonpanel);
        errDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        errDialog.pack();
        errDialog.setLocationRelativeTo(oya);
        errDialog.setVisible(true);

    }

    void popupErrorMessageDialog(Throwable e) {

        if (errDialog != null && errDialog.isShowing()) {
            errDialog.dispose();
        }
        errDialog = new JDialog();
        errDialog.setLocation(120, 120);
        TextArea ta = new TextArea("", 10, 50, TextArea.SCROLLBARS_NONE);
        ta.setEditable(true);
        ta.append(e.getMessage());
        ta.append("\n");
        ta.append(e.toString());

        JPanel mainmsg = new JPanel();
        mainmsg.add(ta);

        errDialog.getContentPane().setLayout(new BorderLayout());

        JButton confirmbutton = new JButton("OK");
        confirmbutton.addActionListener(new ErrorConfirmedListener());

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());
        buttonpanel.add(BorderLayout.EAST, confirmbutton);

        errDialog.getContentPane().add(BorderLayout.SOUTH, buttonpanel);
        errDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        errDialog.pack();
        errDialog.setLocationRelativeTo(oya);
        errDialog.setVisible(true);
    }


    public JDialog popupMessageDialog(String title, JPanel panel, ImageIcon icon) {

        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dispose();
        }
        msgDialog = new JDialog();
        msgDialog.setIconImage(icon.getImage());
        msgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                cleanupCommandThread();
            }
        });
        msgDialog.setTitle(title);

        msgDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new MsgConfirmedListener());

        msgDialog.getContentPane().add(BorderLayout.NORTH, panel);
        msgDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        msgDialog.pack();
        msgDialog.setLocationRelativeTo(oya);
        msgDialog.setVisible(true);
        return msgDialog;
    }

    class DisplayMsgDialogRunner implements Runnable {

        String title;
        TextArea ta;
        ImageIcon icon;
        String sourceid;
        JDialog cm;
        boolean start = false;
        JPanel textPanel;
        JFrame oya;

        public DisplayMsgDialogRunner(String sourceidv ,String titlev, TextArea tav, ImageIcon iconv, JFrame voya) {
            title = titlev;
            ta = tav;
            icon = iconv;
            sourceid = sourceidv;
            oya = voya;
        }

        public boolean isStarted() {
            return start;
        }

        public JDialog getMessageDialog() {
            return cm;
        }

        public JPanel getTextPanel() {
            return textPanel;
        }

        public void run() {

            try {

                textPanel = createSimpleTextAreaPane(ta);
                cm = DisplayMsgDialogFactory.popupDisposalMessageDialog(sourceid, title, textPanel , icon, oya);
                start = true;
                DisplayDialogThreadPool.removeDisplayThread(this);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    public JDialog popupMessageDialog(String title, JTextArea ta, ImageIcon icon) {

        //ta.setDoubleBuffered(false);

        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dispose();
        }
        msgDialog = new JDialog();
        msgDialog.setIconImage(icon.getImage());
        msgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                cleanupCommandThread();
            }
        });
        msgDialog.setTitle(title);
        msgDialog.setLocation(250, 150);

        JPanel mainmsg = new JPanel();
        mainmsg.add(new JScrollPane(ta));

        msgDialog.getContentPane().setLayout(new BorderLayout());

        msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new MsgConfirmedListener());

        msgDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        msgDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        msgDialog.pack();
        msgDialog.setLocationRelativeTo(oya);
        msgDialog.setVisible(true);
        return msgDialog;
    }

    public JDialog popupMessageDialog(String title, TextArea ta, ImageIcon icon) {

        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dispose();
        }
        msgDialog = new JDialog();
        msgDialog.setIconImage(icon.getImage());
        msgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                cleanupCommandThread();
            }
        });
        msgDialog.setTitle(title);
        msgDialog.setLocation(250, 150);

        JPanel mainmsg = new JPanel();
        mainmsg.add(ta);

        msgDialog.getContentPane().setLayout(new BorderLayout());

        msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new MsgConfirmedListener());

        msgDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        msgDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        msgDialog.pack();
        msgDialog.setLocationRelativeTo(oya);
        msgDialog.setVisible(true);
        return msgDialog;
    }

    public JDialog popupStringEditDialog(String title, final JPanel panel, ImageIcon icon ,final int row) {

        if (stringEditDialog != null && stringEditDialog.isShowing()) {
            stringEditDialog.dispose();
        }
        stringEditDialog = new JDialog();
        stringEditDialog.setIconImage(icon.getImage());
        stringEditDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
        stringEditDialog.setTitle(title);
        stringEditDialog.setLocation(250, 150);

        stringEditDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                 mapmdce3.stopCellEditing();

                 Object gv = mapm_property_table.getValueAt(row, 2);
                 if (gv instanceof JTextArea) {
                     JTextArea jgv = (JTextArea)gv;
                     jgv.setText(string_edit_area.getText());
                     mapm_property_table.setValueAt(jgv, row, 2);
                     mapm_property_table.fireTableDataChanged();

                 }

                 stringEditDialog.dispose();
                 stringEditDialog = null;
            }
        });

        stringEditDialog.getContentPane().add(BorderLayout.NORTH, panel);
        stringEditDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        stringEditDialog.pack();
        stringEditDialog.setVisible(true);
        return stringEditDialog;
    }

    public JDialog popupStringEditDialogForStreamMessage(String title, final JPanel panel, ImageIcon icon ,final int row) {

        if (stringEditDialog != null && stringEditDialog.isShowing()) {
            stringEditDialog.dispose();
        }
        stringEditDialog = new JDialog();
        stringEditDialog.setIconImage(icon.getImage());
        stringEditDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
        stringEditDialog.setTitle(title);
        stringEditDialog.setLocation(250, 150);

        stringEditDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                 smdce3.stopCellEditing();

                 Object gv = sm_property_table.getValueAt(row, 2);
                 if (gv instanceof JTextArea) {
                     JTextArea jgv = (JTextArea)gv;
                     jgv.setText(string_edit_area.getText());
                     sm_property_table.setValueAt(jgv, row, 2);
                     sm_property_table.fireTableDataChanged();

                 }

                 stringEditDialog.dispose();
                 stringEditDialog = null;
            }
        });

        stringEditDialog.getContentPane().add(BorderLayout.NORTH, panel);
        stringEditDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        stringEditDialog.pack();
        stringEditDialog.setVisible(true);
        return stringEditDialog;
    }

    public JDialog popupStringEditDialogForUserProperty(String title, final JPanel panel, ImageIcon icon ,final int row) {

        if (stringEditDialog != null && stringEditDialog.isShowing()) {
            stringEditDialog.dispose();
        }
        stringEditDialog = new JDialog();
        stringEditDialog.setIconImage(icon.getImage());
        stringEditDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
        stringEditDialog.setTitle(title);
        stringEditDialog.setLocation(250, 150);

        stringEditDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                 pdce3.stopCellEditing();

                 Object gv = property_table.getValueAt(row, 2);
                 if (gv instanceof JTextArea) {
                     JTextArea jgv = (JTextArea)gv;
                     jgv.setText(string_edit_area.getText());
                     property_table.setValueAt(jgv, row, 2);
                     property_table.fireTableDataChanged();

                 }

                 stringEditDialog.dispose();
                 stringEditDialog = null;
            }
        });

        stringEditDialog.getContentPane().add(BorderLayout.NORTH, panel);
        stringEditDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        stringEditDialog.pack();
        stringEditDialog.setVisible(true);
        return stringEditDialog;
    }

    public JDialog popupMessageDialog(String title, TextArea ta, ActionListener listener, ImageIcon icon) {
        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dispose();
        }
        msgDialog = new JDialog();
        msgDialog.setIconImage(icon.getImage());
        msgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                cleanupCommandThread();
            }
        });
        msgDialog.setTitle(title);
        msgDialog.setLocation(250, 100);

        JPanel mainmsg = new JPanel();
        mainmsg.add(ta);

        msgDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new MsgConfirmedListener());

        JButton refleshbutton = new JButton(resources.getString("qkey.msg.msg006"));
        refleshbutton.addActionListener(listener);

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());
        buttonpanel.add(BorderLayout.SOUTH, msgconfirmbutton);
        buttonpanel.add(BorderLayout.NORTH, refleshbutton);

        msgDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        msgDialog.getContentPane().add(BorderLayout.SOUTH, buttonpanel);
        msgDialog.pack();
        msgDialog.setLocationRelativeTo(oya);
        msgDialog.setVisible(true);

        return msgDialog;
    }

    public JDialog popupSimpleExitMessageDialog(String title, TextArea ta, ActionListener listener, ImageIcon icon) {
        if (msgDialog != null && msgDialog.isShowing()) {
            msgDialog.dispose();
        }
        msgDialog = new JDialog();
        msgDialog.setIconImage(icon.getImage());
        msgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(1);
            }
        });
        msgDialog.setTitle(title);
        msgDialog.setLocation(250, 100);

        JPanel mainmsg = new JPanel();
        mainmsg.add(ta);

        msgDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(listener);

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());
        buttonpanel.add(BorderLayout.SOUTH, msgconfirmbutton);

        msgDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        msgDialog.getContentPane().add(BorderLayout.SOUTH, buttonpanel);
        msgDialog.pack();
        msgDialog.setLocationRelativeTo(oya);
        msgDialog.setVisible(true);

        return msgDialog;
    }

    void collectAllDestinationFromOpenMQ() {
            String cmd = "list dst -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                oe.printStackTrace();
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            destinationNamesForDisplayQueue = cmdRunner.retrieveDestinations(true);
            destinationNamesForDisplayTopic = cmdRunner.retrieveDestinations(false);
    }

    public void collectDestination() throws Exception {


        //宛先（Queue,Topic)を全部見つけ出す
        collectAllDestinationFromOpenMQ();

        try {
            String nowselected = (String) qBox.getSelectedItem();

            //Topic退避
            ArrayList taihi = new ArrayList();

            for (int iq = 0; iq < qBox.getItemCount(); iq++) {
                String cdest = (String) qBox.getItemAt(iq);
                if (cdest != null) {
                    if (cdest.indexOf(TOPIC_SUFFIX) != -1) {
                        taihi.add(cdest);
                    } 
                }
            }

            qBox.removeItemListener(acbil);
            qBox.removeAllItems();

            // Add sorted names to combo box menu
            //さっき保存した選択済みの名前が新しいリストにあるかチェック
            boolean sakki_found = false;


            Collections.sort(destinationNamesForDisplayQueue);
            Collections.sort(destinationNamesForDisplayTopic);

            for (int i = 0; i < destinationNamesForDisplayQueue.size(); i++) {
                String destfordisp = (String) destinationNamesForDisplayQueue.get(i) + " : Queue";
                addDestToMenu(destfordisp);
                if (destfordisp.equals(nowselected)) {
                    sakki_found = true;
                }
            }

            //LocalStoreここで入れ込み
            ArrayList lsnames = lsm.getAllLocalStoreNames();
            for (int i = 0; i < lsnames.size(); i++) {
                String local_store_name = (String)lsnames.get(i);
                addDestToMenu(local_store_name + LOCAL_STORE_SUFFIX);
            }

            //退避Topicを戻す
            for (int i = 0; i < taihi.size(); i++) {
                String taihied_key = (String) taihi.get(i);
                addDestToMenu(taihied_key);
            }

            qBox.addItemListener(acbil);
            int browseindex = 0;

                if (sakki_found) {
                    browseindex = tabbedPane.indexOfTab(nowselected);
                    if (browseindex == -1) {
                        browseindex = 0;
                    }

                }
            refreshMsgTableWithDestName();

        } catch (Exception e) {
            System.err.println("collectDestination: Exception caught: " + e);
        }
    }

    void showDeleteConfirmation(int[] rows) {
        // Create popup
        if (deleteconfirmDialog != null && deleteconfirmDialog.isShowing()) {
            deleteconfirmDialog.dispose();
        }
        deleteconfirmDialog = new JDialog();
        deleteconfirmDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest).getImage());

        deleteconfirmDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                currentDeleteTarget.clear();
            }
        });

        deleteconfirmDialog.setLocation(120, 120);
        deleteconfirmDialog.getContentPane().setLayout(new BorderLayout());

        msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        delmsg = new JPanel();
        deleteconfirmDialog.setSize(500, 100);
        deleteconfirmDialog.setTitle(resources.getString("qkey.msg.msg007"));

        AutoResizingTextArea ta = new AutoResizingTextArea(3,15,42);

        ta.setEditable(false);

        int tabindex = tabbedPane.getSelectedIndex();
        String tkey = tabbedPane.getTitleAt(tabindex);
        JTable cTable = (JTable) jtableins.get(tkey);

        MsgTable mt = (MsgTable) cTable.getModel();

        StringBuilder mediumbuffer = new StringBuilder();

        for (int i = 0; i < rows.length; i++) {
            try {
                SimpleDateFormat df =
                        new SimpleDateFormat("yyyy/MM/dd:kk:mm:ss z");

                MessageContainer msg = mt.getMessageAtRow(rows[i]);

                mediumbuffer.append("MsgID = " + msg.getVmsgid() + "\n");
                currentDeleteTarget.add(msg);
            
            } catch (Exception messagee) {
                //NOP
                //messagee.printStackTrace();
            }

        }

        ta.append(mediumbuffer.toString());

        delmsg.add(new JScrollPane(ta));
        msgPanel.add(BorderLayout.NORTH, delmsg);
        del_okbutton1 = new JButton("                     OK                       ");
        del_okbutton1.addActionListener(new DeleteOKListener());
        JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg008"));
        cancelbutton.addActionListener(new DeleteCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, del_okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        msgPanel.add(BorderLayout.SOUTH, pbuttonpanel);

        deleteconfirmDialog.getContentPane().add(BorderLayout.NORTH, msgPanel);
        deleteconfirmDialog.pack();
        deleteconfirmDialog.setLocationRelativeTo(oya);

        deleteconfirmDialog.setVisible(true);

        ta.append(resources.getString("qkey.msg.msg009"));
    }

    void showDeleteFromTopicCacheConfirmation(int[] rows) {
        // Create popup
        if (deleteconfirmDialog != null && deleteconfirmDialog.isShowing()) {
            deleteconfirmDialog.dispose();
        }
        deleteconfirmDialog = new JDialog();
        deleteconfirmDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest).getImage());

        deleteconfirmDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                currentDeleteTarget.clear();
            }
        });

        deleteconfirmDialog.setLocation(120, 120);
        deleteconfirmDialog.getContentPane().setLayout(new BorderLayout());

        msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        delmsg = new JPanel();
        deleteconfirmDialog.setSize(500, 100);
        deleteconfirmDialog.setTitle(resources.getString("qkey.msg.msg138"));

        AutoResizingTextArea ta = new AutoResizingTextArea(3,15,42);

        ta.setEditable(false);

        int tabindex = tabbedPane.getSelectedIndex();
        String tkey = tabbedPane.getTitleAt(tabindex);
        JTable cTable = (JTable) jtableins.get(tkey);

        MsgTable mt = (MsgTable) cTable.getModel();

        StringBuilder mediumbuffer = new StringBuilder();
        delete_from_cache_rows = rows;

        for (int i = 0; i < rows.length; i++) {
            try {
                SimpleDateFormat df =
                        new SimpleDateFormat("yyyy/MM/dd:kk:mm:ss z");

                MessageContainer msg = mt.getMessageAtRow(rows[i]);
                mediumbuffer.append("MsgID = " + msg.getVmsgid() + "\n");
           
            } catch (Exception messagee) {
                //NOP
            }

        }

        ta.append(mediumbuffer.toString());

        delmsg.add(new JScrollPane(ta));
        msgPanel.add(BorderLayout.NORTH, delmsg);
        del_okbutton1 = new JButton("                 OK                  ");
        del_okbutton1.addActionListener(new DeleteFromCacheOKListener());
        JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg008"));
        cancelbutton.addActionListener(new DeleteCancelListener());


        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, del_okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        msgPanel.add(BorderLayout.SOUTH, pbuttonpanel);


        deleteconfirmDialog.getContentPane().add(BorderLayout.NORTH, msgPanel);
        deleteconfirmDialog.pack();
        deleteconfirmDialog.setLocationRelativeTo(oya);

        deleteconfirmDialog.setVisible(true);

        ta.append(resources.getString("qkey.msg.msg139"));

    }

    void showDeleteFromLocalStoreConfirmation(int[] rows) {
        // Create popup
        if (deleteconfirmDialog != null && deleteconfirmDialog.isShowing()) {
            deleteconfirmDialog.dispose();
        }
        deleteconfirmDialog = new JDialog();
        deleteconfirmDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest).getImage());

        deleteconfirmDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                currentDeleteTarget.clear();
            }
        });

        deleteconfirmDialog.setLocation(120, 120);
        deleteconfirmDialog.getContentPane().setLayout(new BorderLayout());

        msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        delmsg = new JPanel();
        deleteconfirmDialog.setSize(500, 100);
        deleteconfirmDialog.setTitle(resources.getString("qkey.msg.msg264"));

        AutoResizingTextArea ta = new AutoResizingTextArea(3,15,42);

        ta.setEditable(false);

        int tabindex = tabbedPane.getSelectedIndex();
        String tkey = tabbedPane.getTitleAt(tabindex);
        JTable cTable = (JTable) jtableins.get(tkey);

        LocalMsgTable mt = (LocalMsgTable) cTable.getModel();

        StringBuilder mediumbuffer = new StringBuilder();
        delete_from_cache_rows = rows;

        for (int i = 0; i < rows.length; i++) {
            try {
                SimpleDateFormat df =
                        new SimpleDateFormat("yyyy/MM/dd:kk:mm:ss z");

                LocalMessageContainer msg = mt.getMessageAtRow(rows[i]);
                mediumbuffer.append("MsgID = " + msg.getVmsgid() + "\n");

            } catch (Exception messagee) {
                //NOP
            }

        }

        ta.append(mediumbuffer.toString());

        delmsg.add(new JScrollPane(ta));
        msgPanel.add(BorderLayout.NORTH, delmsg);
        del_okbutton1 = new JButton("                OK                 ");
        del_okbutton1.addActionListener(new DeleteFromLocalStoreOKListener());
        JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg008"));
        cancelbutton.addActionListener(new DeleteCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, del_okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        msgPanel.add(BorderLayout.SOUTH, pbuttonpanel);


        deleteconfirmDialog.getContentPane().add(BorderLayout.NORTH, msgPanel);
        deleteconfirmDialog.pack();
        deleteconfirmDialog.setLocationRelativeTo(oya);

        deleteconfirmDialog.setVisible(true);

        ta.append(resources.getString("qkey.msg.msg265"));

    }

    public void showPurgeDestConfirmation() {
        // Create popup
        if (purgedestconfirmDialog != null && purgedestconfirmDialog.isShowing()) {
            purgedestconfirmDialog.dispose();
        }
        purgedestconfirmDialog = new JDialog();
        purgedestconfirmDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest).getImage());

        purgedestconfirmDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

        purgedestconfirmDialog.setLocation(120, 120);
        purgedestconfirmDialog.getContentPane().setLayout(new BorderLayout());

        msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        delmsg = new JPanel();
        purgedestconfirmDialog.setSize(100, 100);
        purgedestconfirmDialog.setTitle(resources.getString("qkey.msg.msg010"));

        TextArea ta = new TextArea("", 2, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);

        //ComboBoxEditor editor = qBox.getEditor();
        //String name = getPureDestName((String) editor.getItem());
        int selidx = tabbedPane.getSelectedIndex();
        String name = getPureDestName(tabbedPane.getTitleAt(selidx));
        ta.append(resources.getString("qkey.msg.msg011"));
        ta.append(name);
        ta.append(resources.getString("qkey.msg.msg012"));

        ta.setColumns(30 + name.length());
        ta.setEditable(false);
        ta.setBackground(Color.WHITE);

        delmsg.add(ta);
        msgPanel.add(BorderLayout.NORTH, delmsg);
        JButton okbutton1 = new JButton("               OK                ");
        okbutton1.addActionListener(new PurgeDestOKListener());
        JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg013"));
        cancelbutton.addActionListener(new PurgeDestCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        msgPanel.add(BorderLayout.SOUTH, pbuttonpanel);

        purgedestconfirmDialog.getContentPane().add(BorderLayout.NORTH, msgPanel);
        purgedestconfirmDialog.pack();
        purgedestconfirmDialog.setLocationRelativeTo(oya);
        purgedestconfirmDialog.setVisible(true);
    }

    //for tree menu
    public void showPurgeDestConfirmation2() {
        // Create popup
        if (purgedestconfirmDialog != null && purgedestconfirmDialog.isShowing()) {
            purgedestconfirmDialog.dispose();
        }
        purgedestconfirmDialog = new JDialog();
        purgedestconfirmDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest).getImage());

        purgedestconfirmDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

        purgedestconfirmDialog.setLocation(120, 120);
        purgedestconfirmDialog.getContentPane().setLayout(new BorderLayout());

        msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        delmsg = new JPanel();
        purgedestconfirmDialog.setSize(100, 100);
        purgedestconfirmDialog.setTitle(resources.getString("qkey.msg.msg010"));

        TextArea ta = new TextArea("", 2, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);

        //int selidx = tabbedPane.getSelectedIndex();
        //String name = getPureDestName(tabbedPane.getTitleAt(selidx));
        TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();
        String name = di.destinationName;
        ta.append(resources.getString("qkey.msg.msg011"));
        ta.append(name);
        ta.append(resources.getString("qkey.msg.msg012"));

        ta.setColumns(30 + name.length());
        ta.setEditable(false);
        ta.setBackground(Color.WHITE);

        delmsg.add(ta);
        msgPanel.add(BorderLayout.NORTH, delmsg);
        JButton okbutton1 = new JButton("                OK                 ");
        okbutton1.addActionListener(new PurgeDestOKListener());
        JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg013"));
        cancelbutton.addActionListener(new PurgeDestCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        msgPanel.add(BorderLayout.SOUTH, pbuttonpanel);

        purgedestconfirmDialog.getContentPane().add(BorderLayout.NORTH, msgPanel);
        purgedestconfirmDialog.pack();
        purgedestconfirmDialog.setLocationRelativeTo(oya);
        purgedestconfirmDialog.setVisible(true);
    }

    void tableCopy(JTable fromT, JTable toT) {

        MsgTable from = (MsgTable) fromT.getModel();
        MsgTable to = (MsgTable) toT.getModel();
        to.init();
        for (int i = 0; i < from.getRowCount(); i++) {
            MessageContainer msg = from.getMessageAtRow(i);
            to.add_one_row(msg);
        }
        reNumberCTable(toT);
        toT.updateUI();
    }

    void localTableCopy(JTable fromT, JTable toT) {

        LocalMsgTable from = (LocalMsgTable) fromT.getModel();
        LocalMsgTable to = (LocalMsgTable) toT.getModel();
        to.init();
        for (int i = 0; i < from.getRowCount(); i++) {
            LocalMessageContainer msg = from.getMessageAtRow(i);
            to.add_one_row(msg);
        }
        reNumberLocalCTable(toT);
        toT.updateUI();
    }

    void tableCopyWithoutIndicatedRows(JTable fromT, JTable toT, HashSet rows) {

        MsgTable from = (MsgTable) fromT.getModel();
        MsgTable to = (MsgTable) toT.getModel();
        to.init();
        for (int i = 0; i < from.getRowCount(); i++) {
            if (!rows.contains(new Integer(i))) {
                MessageContainer msg = from.getMessageAtRow(i);
                to.add_one_row(msg);
            }
        }
        reNumberCTable(toT);
        toT.updateUI();
    }

    void localTableCopyWithoutIndicatedRows(JTable fromT, JTable toT, HashSet rows) {

        LocalMsgTable from = (LocalMsgTable) fromT.getModel();
        LocalMsgTable to = (LocalMsgTable) toT.getModel();
        to.init();
        for (int i = 0; i < from.getRowCount(); i++) {
            if (!rows.contains(new Integer(i))) {
                LocalMessageContainer msg = from.getMessageAtRow(i);
                to.add_one_row(msg);
            }
        }
        reNumberLocalCTable(toT);
        toT.updateUI();
    }

    public JDialog popupCmdMessageDialog(String title, TextArea ta) {

        if (cmdmsgDialog != null && cmdmsgDialog.isShowing()) {
            cmdmsgDialog.dispose();
        }
        cmdmsgDialog = new JDialog();
        cmdmsgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                cleanupCommandThread();
            }
        });
        cmdmsgDialog.setTitle(title);
        cmdmsgDialog.setLocation(190, 265);

        JPanel mainmsg = new JPanel();
        mainmsg.add(ta);

        cmdmsgDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new MsgConfirmedListener());

        cmdmsgDialog.getContentPane().add(BorderLayout.NORTH, mainmsg);
        cmdmsgDialog.getContentPane().add(BorderLayout.SOUTH, msgconfirmbutton);
        cmdmsgDialog.pack();
        cmdmsgDialog.setLocationRelativeTo(oya);
        cmdmsgDialog.setVisible(true);
        return cmdmsgDialog;
    }

    public JDialog popupCmdMessageDialog(String title, JPanel source, ActionListener listener) {
        if (cmdmsgDialog != null && cmdmsgDialog.isShowing()) {
            cmdmsgDialog.dispose();
        }
        cmdmsgDialog = new JDialog();
        cmdmsgDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                cleanupCommandThread();
            }
        });
        cmdmsgDialog.setTitle(title);
        cmdmsgDialog.setLocation(190, 265);
        cmdmsgDialog.getContentPane().setLayout(new BorderLayout());

        JButton msgconfirmbutton = new JButton("OK");
        msgconfirmbutton.addActionListener(new CmdMsgConfirmedListener());

        JButton refleshbutton = new JButton(resources.getString("qkey.msg.msg014"));
        refleshbutton.addActionListener(listener);

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());
        buttonpanel.add(BorderLayout.SOUTH, msgconfirmbutton);
        buttonpanel.add(BorderLayout.NORTH, refleshbutton);

        cmdmsgDialog.getContentPane().add(BorderLayout.NORTH, source);
        cmdmsgDialog.getContentPane().add(BorderLayout.SOUTH, buttonpanel);
        cmdmsgDialog.pack();
        cmdmsgDialog.setLocationRelativeTo(oya);
        cmdmsgDialog.setVisible(false);

        return cmdmsgDialog;
    }

    void hideNewMessagePanel() {
        if (newmessageFrame != null) {
            newmessageFrame.setVisible(false);
        }
    }

    public void showTxnFilter() {

        // Create popup
        if (filterTxnDialog != null && filterTxnDialog.isShowing()) {
            filterTxnDialog.dispose();
        }
        filterTxnDialog = new JDialog();
        filterTxnDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.FilteredTxn).getImage());

        filterTxnDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                //currentDeleteTarget.clear();
            }
        });

        filterTxnDialog.setLocation(280, 100);
        filterTxnDialog.getContentPane().setLayout(new BorderLayout());

        msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());

        filtermsg = new JPanel();
        filterTxnDialog.setSize(200, 200);
        filterTxnDialog.setTitle(resources.getString("qkey.msg.msg015"));

        txnStateBox = new JComboBox();

        DefaultComboBoxModel model = (DefaultComboBoxModel) txnStateBox.getModel();
        model.addElement(resources.getString("qkey.msg.msg016"));
        model.addElement("INCOMPLETE");
        model.addElement("FAILED");
        model.addElement("PREPARED");
        model.addElement("COMPLETE");
        model.addElement("STARTED");
        model.addElement("COMMITED");
        model.addElement("ROLLEDBACK");
        model.addElement("CREATED");
        
        filtermsg.add(txnStateBox);
        msgPanel.add(BorderLayout.NORTH, filtermsg);
        JButton okbutton1 = new JButton("               OK               ");
        okbutton1.addActionListener(new FilterTxnOKListener());
        JButton cancelbutton = new JButton("        " + resources.getString("qkey.msg.msg017") + "        ");
        cancelbutton.addActionListener(new FilterTxnCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);
        msgPanel.add(BorderLayout.SOUTH, pbuttonpanel);

        filterTxnDialog.getContentPane().add(BorderLayout.NORTH, msgPanel);
        filterTxnDialog.pack();
        filterTxnDialog.setLocationRelativeTo(oya);
        filterTxnDialog.setVisible(true);
    }

    public int convertTxnStateStringtoInt(String statename) {

        if (statename.equals("CREATED")) {
            return 0;

        } else if (statename.equals("STARTED")) {
            return 1;

        } else if (statename.equals("FAILED")) {
            return 2;

        } else if (statename.equals("INCOMPLETE")) {
            return 3;

        } else if (statename.equals("COMPLETE")) {
            return 4;

        } else if (statename.equals("PREPARED")) {
            return 5;

        } else if (statename.equals("COMMITED")) {
            return 6;

        } else if (statename.equals("ROLLEDBACK")) {
            return 7;

        } else if (statename.equals("STARTED以外")) {
            return 8;

        }

        return -1;
    }

    public void popupConfirmationDialog(String title, JTextArea ta, ImageIcon icon, ActionListener lsnr) {
        // Create popup
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dispose();
        }
        confirmDialog = new JDialog();
        confirmDialog.setIconImage(icon.getImage());

        confirmDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

        confirmDialog.setLocation(120, 120);
        confirmDialog.getContentPane().setLayout(new BorderLayout());

        JPanel cmsgPanel = new JPanel();
        cmsgPanel.setLayout(new BorderLayout());
        JPanel cmsg = new JPanel();
        confirmDialog.setSize(100, 100);
        confirmDialog.setTitle(title);
        JScrollPane jsp = new JScrollPane(ta);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        cmsg.add(jsp);
        cmsgPanel.add(BorderLayout.NORTH, cmsg);
        JButton okbutton1 = new JButton("              OK              ");
        okbutton1.addActionListener(lsnr);
        JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg013"));
        cancelbutton.addActionListener(new ConfirmDialogCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        cmsgPanel.add(BorderLayout.SOUTH, pbuttonpanel);

        confirmDialog.getContentPane().add(BorderLayout.NORTH, cmsgPanel);
        confirmDialog.pack();
        confirmDialog.setLocationRelativeTo(oya);
        confirmDialog.setVisible(true);
    }

public void popupConfirmationDialog(String title, JPanel panel, ImageIcon icon, ActionListener lsnr) {
        // Create popup
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dispose();
        }
        confirmDialog = new JDialog();
        confirmDialog.setIconImage(icon.getImage());

        confirmDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

        confirmDialog.setLocation(120, 120);
        confirmDialog.getContentPane().setLayout(new BorderLayout());

        JPanel cmsgPanel = new JPanel();
        cmsgPanel.setLayout(new BorderLayout());
        JPanel cmsg = new JPanel();
        confirmDialog.setSize(100, 100);
        confirmDialog.setTitle(title);
 
        cmsg.add(panel);
        cmsgPanel.add(BorderLayout.NORTH, cmsg);
        JButton okbutton1 = new JButton("                OK               ");
        okbutton1.addActionListener(lsnr);
        JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg013"));
        cancelbutton.addActionListener(new ConfirmDialogCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        cmsgPanel.add(BorderLayout.SOUTH, pbuttonpanel);

        confirmDialog.getContentPane().add(BorderLayout.NORTH, cmsgPanel);
        confirmDialog.pack();
        confirmDialog.setLocationRelativeTo(oya);
        confirmDialog.setVisible(true);
    }

    public void cleanupCommandThread() {
        if (crthread != null) {

            try {
                if (crthread.cmdRunner != null) {
                    crthread.cmdRunner.cancelFlusherTask();
                }
                crthread.stop();
                crthread = null;

            } catch (Exception the) {
                //NOP
                //the.printStackTrace();
            }
        }
    }

    public void showCommandWindow() {

        // Create popup
        if (cmdDialog != null && cmdDialog.isShowing()) {
            cmdDialog.dispose();
        }
        cmdDialog = new JDialog();
        cmdDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.CmdInput).getImage());

        cmdDialog.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

        cmdDialog.setLocation(50, 100);
        cmdDialog.getContentPane().setLayout(new BorderLayout());

        cmdmsgPanel = new JPanel();
        cmdmsgPanel.setLayout(new BorderLayout());

        JPanel cmdmsg = new JPanel();
        cmdDialog.setSize(200, 200);
        cmdDialog.setTitle(resources.getString("qkey.msg.msg018"));

        cmdtextfield = new JTextField(36);

        JLabel cmdlabel = new JLabel(resources.getString("qkey.msg.msg019"));
        JLabel cmdlabel2 = new JLabel(resources.getString("qkey.msg.msg020"));
        JPanel expl = new JPanel();
        expl.setLayout(new BorderLayout());
        expl.add(BorderLayout.NORTH, cmdlabel);
        expl.add(BorderLayout.CENTER, cmdlabel2);

        String dispDest = (String) qBox.getSelectedItem();
        String selectedDest = getPureDestName(dispDest);
        String dtype = "q";
        if (dispDest.indexOf(TOPIC_SUFFIX) != -1) {
            dtype = "t";
        }

        //テンプレート
        cmdTemplateBox = new JComboBox();
        cmdTemplateBox.addItemListener(new CmdTemplateItemListener());

        ArrayList extracted_history = QBrowserUtil.getHistoryFromFile("command_history");
        QBrowserUtil.ArrayListToJComboBox(extracted_history, cmdTemplateBox);
        QBrowserUtil.ArrayListToJComboBox(extracted_history, cmdTemplateBoxForSave);

       
        DefaultComboBoxModel model = (DefaultComboBoxModel) cmdTemplateBox.getModel();
        
        model.addElement("update dst -t " + dtype + " -n " + selectedDest + " -o  maxTotalMsgBytes=1000");
        model.addElement("update dst -t " + dtype + " -n " + selectedDest + " -o  maxNumProducers=1000");
        model.addElement("update dst -t " + dtype + " -n " + selectedDest + " -o  maxNumMsgs=1000");
        model.addElement("update dst -t " + dtype + " -n " + selectedDest + " -o  maxBytesPerMsg=1000");
        model.addElement("update dst -t " + dtype + " -n " + selectedDest + " -o  limitBehavior=FLOW_CONTROL");
        model.addElement("update bkr -n imqbroker -o imq.autocreate.queue.maxNumActiveConsumers=100");
        model.addElement("query dst -t " + dtype + " -n " + selectedDest);
        model.addElement("purge dst -t " + dtype + " -n " + selectedDest);
        model.addElement("pause dst -t " + dtype + " -n " + selectedDest);
        model.addElement("destroy msg -t " + dtype + " -n " + selectedDest + " -msgID <msgid not enclose '>");
        model.addElement("query msg -t " + dtype + " -n " + selectedDest + " -msgID <msgid not enclose '>");
        model.addElement("resume dst -t " + dtype + " -n " + selectedDest);
        model.addElement("metrics dst -t " + dtype + " -n " + selectedDest + " -int 3");
        model.addElement("destroy dst -t " + dtype + " -n " + selectedDest);
        model.addElement("compact dst -t " + dtype + " -n " + selectedDest);
        model.addElement("rollback txn -n 12345678");
        model.addElement("commit txn -n 12345678");
        model.addElement("list dst");
        model.addElement("purge dur -n Topic1 -c cId");
        model.addElement("metrics bkr -int 3 -msp 10");
        model.addElement("restart bkr");
        model.addElement("shutdown bkr");
        model.addElement("._kill bkr");

        cmdmsg.add(cmdtextfield);
        cmdmsgPanel.add(BorderLayout.NORTH, expl);
        cmdmsgPanel.add(BorderLayout.CENTER, cmdmsg);
        JButton okbutton1 = new JButton("               OK               ");
        okbutton1.addActionListener(new CmdOKListener(this));
        JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg021") + "             ");
        cancelbutton.addActionListener(new CmdCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        JPanel temppanel = new JPanel();
        temppanel.setLayout(new BorderLayout());
        JLabel templabel = new JLabel(resources.getString("qkey.msg.msg022"));

        temppanel.add(BorderLayout.NORTH, templabel);
        temppanel.add(BorderLayout.CENTER, cmdTemplateBox);
        temppanel.add(BorderLayout.SOUTH, pbuttonpanel);

        cmdmsgPanel.add(BorderLayout.SOUTH, temppanel);

        cmdDialog.getContentPane().add(BorderLayout.NORTH, cmdmsgPanel);
        cmdDialog.pack();
        cmdDialog.setLocationRelativeTo(oya);

        cmdDialog.setVisible(true);
    }
    

    private JComboBox getMapMessagePropTypeComboBox() {
        if (mapptc != null) {
            return mapptc;
        } else {
            initMapMessagePropTypeComboBox(new JComboBox());
            //ここにリスナを。
            mapptc.addItemListener(new MapMessageTypeComboBoxItemListener());
            return mapptc;
        }

    }

    private void initMapMessagePropTypeComboBox(JComboBox value) {
        //MapMessage Property Types
        mapptc = value;
        mapptc.addItem(Property.INT_TYPE);
        mapptc.addItem(Property.STRING_TYPE);
        mapptc.addItem(Property.BOOLEAN_TYPE);
        mapptc.addItem(Property.BYTE_TYPE);
        mapptc.addItem(Property.BYTES_TYPE);
        mapptc.addItem(Property.DOUBLE_TYPE);
        mapptc.addItem(Property.FLOAT_TYPE);
        mapptc.addItem(Property.LONG_TYPE);
        mapptc.addItem(Property.SHORT_TYPE);
        mapptc.setSelectedIndex(0);

    }

    JComboBox getHeaderPropTypeComboBox() {
        if (hptc != null) {
            return hptc;
        } else {
            initHeaderPropTypeComboBox(new JComboBox());
            return hptc;
        }

    }

    private void initHeaderPropTypeComboBox(JComboBox value) {
        //User Property Types
        hptc = value;
        hptc.addItem("JMSExpiration");
        hptc.addItem("JMSPriority");
        hptc.addItem("JMSReplyTo");
        hptc.addItem("JMSCorrelationID");
        hptc.addItem("JMSType");
        hptc.setSelectedIndex(0);

    }

    void cleanupNewMessagePanelObjects() {
        
        header_table = null;
        hTable = null;
        hdce2 = null;
        mqBox = null;
        matesakiname = null;
        property_table = null;
        pTable = null;
        pdce3 = null;
        southpanel = null;
        mbodyPanel = null;
        cdeliverymode = null;
        ccompressmode = null;
        soufukosu = null;
        okbutton = null;
        cmessagefooter = null;
        mfilepath = null;
        passthrough_bytesmessage = null;
        mfilebodyPanel = null;
        mapmBodyPanel = null;
        mapm_property_table = null;
        mTable = null;
        mapmdce0 = null;
        mapmdce3 = null;
        hptc = null;

    }

    void showNewMessagePanel(boolean cleanupmode) {

        //cleanupモードでは、外側のフレームは残して中を一新する
        //newmessageFrame == nullの時は完全新規作成（再利用なし）
        //newmessageFrame != null で !cleanupmodeのときは、作成を全部飛ばす

        if (newmessageFrame != null && !cleanupmode) {

        } else {

          if (newmessageFrame == null) {
            // Create popup
            newmessageFrame = new JFrame();
            newmessageFrame.setTitle(resources.getString("qkey.msg.msg023"));
            newmessageFrame.setBackground(Color.white);
            newmessageFrame.getContentPane().setLayout(new BorderLayout());
            newmessageFrame.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg).getImage());

          }

            if (cleanupmode) {
                JPanel newcp = new JPanel();
                newcp.setLayout(new BorderLayout());
                newmessageFrame.setContentPane(newcp);

            }

            newmessageFrame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    if (stringEditDialog != null) {
                        stringEditDialog.dispose();
                        stringEditDialog = null;
                    }
                }
            });


            JPanel northpanel = new JPanel();
            northpanel.setLayout(new BorderLayout());

            //宛先入力はコンボボックスに変更
            matesakiBox1 = new JComboBox();
            
            Dimension dm = matesakiBox1.getPreferredSize();
            dm.setSize(10 * dm.getWidth(), dm.getHeight());
            matesakiBox1.setPreferredSize(dm);
            matesakiBox1.setEditable(true);

            //ヘッダパネル
            header_table = new HeaderPropertyTable(0);
            hTable = new JTable(header_table);

            
            hTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            hTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());

            hTable.setPreferredScrollableViewportSize(new Dimension(500,65));

            DefaultCellEditor hdce = new DefaultCellEditor(this.getHeaderPropTypeComboBox());
            //System.out.println(header_table.getRowCount());
            TableColumn hcolumn = hTable.getColumnModel().getColumn(0);
            hdce.setClickCountToStart(0);
            hcolumn.setCellEditor(hdce);

            hdce2 = new PropTableCellEditor();
            TableColumn hcolumn2 = hTable.getColumnModel().getColumn(1);
            hdce2.setClickCountToStart(0);
            hdce2.addCellEditorListener(new CellEditorListener() {

                public void editingStopped(ChangeEvent e) {
                    //今表の中にある全部の行をvalidateする

                    try {

                         //重複チェック用
                        HashSet keycheck = new HashSet();

                        for (int hi = 0; hi < header_table.getRowCount(); hi++) {
                            Property hpr = header_table.getPropertyAtRow(hi);
                            String key = hpr.getKey();
                            Object val = hpr.getProperty_value();

                            if (key != null) {
                                if (keycheck.contains(key)) {
                                    throw new QBrowserPropertyException("Q0020" + MAGIC_SEPARATOR + key + MAGIC_SEPARATOR + hpr.getProperty_type() + MAGIC_SEPARATOR + val);
                                } else {
                                    keycheck.add(key);
                                }
                            }


                            PropertyUtil.validateJMSHeaderValueType(key, val);

                        }
                        newmessage1stpanelok = true;
                    } catch (QBrowserPropertyException qpe) {
                        //cmessagefooter.setText(qpe.getMessage());

                        last_jmsheader_validate_error = qpe.getMessage();

                        newmessage1stpanelok = false;
                    }

                }

                public void editingCanceled(ChangeEvent e) {}
            });

            hcolumn2.setCellEditor(hdce2);

            JScrollPane htablePane = new JScrollPane(hTable);
            JPanel hp = new JPanel();
            hp.setLayout(new BorderLayout());

            //プロパティヘッダパネル
            JPanel header_header_container = new JPanel();
            header_header_container.setLayout(new BorderLayout());

            JLabel header_header_label = new JLabel(resources.getString("qkey.msg.msg024"));
            JPanel hbutton_container = new JPanel();
            JButton hplus_button = new JButton("+");
            hplus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    header_table.add_one_empty_row();
            }

            });

            JButton hminus_button = new JButton("-");
            hminus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    int sel_row = hTable.getSelectedRow();
                    if (sel_row < 0) sel_row = 0;
                    if (header_table.getRowCount() > 0)
                      header_table.deletePropertyAtRow(sel_row);
            }

            });
            hbutton_container.add(hplus_button);
            hbutton_container.add(hminus_button);

            header_header_container.setBorder(BorderFactory.createEtchedBorder());

            header_header_container.add(header_header_label, BorderLayout.CENTER);
            header_header_container.add(hbutton_container, BorderLayout.EAST);

            hp.setBorder(BorderFactory.createEtchedBorder());

            hp.add(header_header_container, BorderLayout.NORTH);
            hp.add(htablePane, BorderLayout.CENTER);
            

            northpanel.add(BorderLayout.SOUTH, hp);

            mqBox = new JComboBox();
            mqBox.addItemListener(new SendAtesakiComboBoxItemListener());
            Dimension d = mqBox.getPreferredSize();
            d.setSize(10 * d.getWidth(), d.getHeight());
            mqBox.setPreferredSize(d);
            mqBox.setEditable(false);

            DefaultComboBoxModel model = (DefaultComboBoxModel) mqBox.getModel();
            model.addElement(QUEUE_LITERAL);
            model.addElement(TOPIC_LITERAL);
            model.addElement(LOCAL_STORE_LITERAL);

            JLabel jl01 = new JLabel(resources.getString("qkey.msg.msg025"));
            northpanel.add(BorderLayout.WEST, jl01);
            northpanel.add(BorderLayout.EAST, mqBox);

            //宛先名入力エリア
            JPanel atesaki = new JPanel();
            atesaki.setLayout(new BorderLayout());
            JLabel jl02 = new JLabel(resources.getString("qkey.msg.msg026"));
            atesaki.add(BorderLayout.WEST, jl02);
            //データ入れ込み。デフォルトはQUEUE
            importQueueNamesToMATESAKIBOX1();
            matesakiBox1.setEditable(true);

            atesaki.add(BorderLayout.CENTER, matesakiBox1);

            //atesaki.add(BorderLayout.CENTER, matesakiname);
            northpanel.add(BorderLayout.NORTH, atesaki);

            newmessageFrame.getContentPane().add(BorderLayout.NORTH, northpanel);

            property_table = new PropertyInputTable(0);
            pTable = new JTable(property_table);
            pTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            pTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());
            pTable.setColumnSelectionAllowed(false);
            pTable.setRowHeight(20);

            pTable.setPreferredScrollableViewportSize(new Dimension(500,120));

            TableColumn column0 = pTable.getColumnModel().getColumn(0);
            pdce1 = new DefaultCellEditor(new JTextField());
            pdce1.setClickCountToStart(0);
            column0.setCellEditor(pdce1);


            TableColumn column = pTable.getColumnModel().getColumn(1);

            column.setPreferredWidth(10);
            ListCellEditor plce2 = new ListCellEditor();
            plce2.setClickCountToStart(0);
            column.setCellEditor(plce2);
            column.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

            //pdce3 = new PropTableCellEditor();
            pdce3 = new ListCellEditor();
            TableColumn pcolumn3 = pTable.getColumnModel().getColumn(2);
            pdce3.setClickCountToStart(0);

            pcolumn3.setCellEditor(pdce3);
            pcolumn3.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

            JScrollPane tablePane = new JScrollPane(pTable);
            JPanel pp = new JPanel();
            pp.setLayout(new BorderLayout());

            //プロパティヘッダパネル
            JPanel prop_header_container = new JPanel();
            prop_header_container.setLayout(new BorderLayout());

            JLabel prop_header_label = new JLabel(resources.getString("qkey.msg.msg158"));
            JPanel button_container = new JPanel();
            JButton plus_button = new JButton("+");
            plus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    property_table.add_one_empty_row();
                    property_table.setItemListenerInComboBoxAt((property_table.getRowCount() - 1)
                            , new UserPropertyTypeComboBoxItemListener());
            }

            });

            JButton minus_button = new JButton("-");
            minus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    int sel_row = pTable.getSelectedRow();
                    if (sel_row < 0) sel_row = 0;
                    if (property_table.getRowCount() > 0)
                      property_table.deletePropertyAtRow(sel_row);
            }

            });
            button_container.add(plus_button);
            button_container.add(minus_button);

            prop_header_container.setBorder(BorderFactory.createEtchedBorder());

            prop_header_container.add(prop_header_label, BorderLayout.CENTER);
            prop_header_container.add(button_container, BorderLayout.EAST);

            pp.setBorder(BorderFactory.createEtchedBorder());

            pp.add(prop_header_container, BorderLayout.NORTH);
            pp.add(tablePane, BorderLayout.CENTER);

            newmessageFrame.getContentPane().add(BorderLayout.CENTER, pp);

            southpanel = new JPanel();

            mbodyPanel = new TextMessageInputBodyPanel();
            mbodyPanel.setTitle(resources.getString("qkey.msg.msg159"));
            southpanel.setLayout(new BorderLayout());

            southpanel.add(BorderLayout.CENTER, mbodyPanel);
            currentBodyPanel = mbodyPanel;

            //選択されたラジオボタンにしたがって
            //入力パネルが変更される
            JPanel txtorfilepanel = new JPanel();
            message_type = new JComboBox();
            message_type.setPreferredSize(new Dimension(120, 20));
            message_type.addItem(TEXTMESSAGE);
            message_type.addItem(BYTESMESSAGE);
            message_type.addItem(MAPMESSAGE);
            message_type.addItem(STREAMMESSAGE);
            message_type.addItem(MESSAGE);
            message_type.setSelectedIndex(0);
            message_type.addItemListener(new MessageTypeListener());

            JLabel jl03 = new JLabel(resources.getString("qkey.msg.msg028"));

            txtorfilepanel.add(jl03);
            txtorfilepanel.add(message_type);

            //Encoding
            penc = new JPanel();
            JLabel jlenc = new JLabel(resources.getString("qkey.msg.msg404"));
            penc.add(jlenc);
            encoding_type = new JComboBox();
            encoding_type.setPreferredSize(new Dimension(100, 20));
            encoding_type.setEditable(true);
            String default_encoding = resources.getString("qkey.msg.msg405");
            encoding_type.addItem(default_encoding);
            //encode_before = default_encoding;
            encoding_type.addItem("UTF8");
            //デフォルト
            //encoding_type.addItem("SJIS");
            encoding_type.addItem("ISO2022JP");
            encoding_type.addItem("EUCJP");
            encoding_type.addItem("UTF-16");
            encoding_type.addItemListener(new MessageEncodingTypeListener());
            penc.add(encoding_type);

            //DeliveryMode
            JPanel pdeliverymode = new JPanel();
            
            JLabel ldeliverymode = new JLabel(resources.getString("qkey.msg.msg154"));
            cdeliverymode = new JComboBox();
            cdeliverymode.addItem(resources.getString("qkey.msg.msg122"));
            cdeliverymode.addItem(resources.getString("qkey.msg.msg123"));
            cdeliverymode.setPreferredSize(new Dimension(110, 18));
            pdeliverymode.add(ldeliverymode);
            pdeliverymode.add(cdeliverymode);

            //CompressMode
            JPanel pcompressmode = new JPanel();

            JLabel lcompressmode = new JLabel(resources.getString("qkey.msg.msg155"));
            ccompressmode = new JComboBox();
            ccompressmode.addItem(resources.getString("qkey.msg.msg156"));
            ccompressmode.addItem(resources.getString("qkey.msg.msg157"));
            ccompressmode.setPreferredSize(new Dimension(110, 18));
            pcompressmode.add(lcompressmode);
            pcompressmode.add(ccompressmode);


            //メッセージ送付回数
            JPanel msgkosupanel = new JPanel();
            msgkosupanel.setLayout(new BorderLayout());
            JLabel jl08 = new JLabel(resources.getString("qkey.msg.msg029"));
            msgkosupanel.add(BorderLayout.CENTER, jl08);

            soufukosu = new JTextField(5);
            soufukosu.addCaretListener(new SoufukosuInputListener());
            soufukosu.setText("1");

            msgkosupanel.add(BorderLayout.EAST, soufukosu);

            messagesentakupanel = new JPanel();
            messagesentakupanel.setLayout(new BorderLayout());
            messagesentakupanel.add(BorderLayout.WEST, txtorfilepanel);
            messagesentakupanel.add(BorderLayout.CENTER,penc);
            messagesentakupanel.setBorder(BorderFactory.createEtchedBorder());
            JPanel modecontainer = new JPanel();

            JButton clearbutton = new JButton(resources.getString("qkey.msg.msg216"));
            clearbutton.addActionListener(new NewMessageClearListener());

            modecontainer.add(pdeliverymode);
            modecontainer.add(pcompressmode);
            modecontainer.add(clearbutton);

            messagesentakupanel.add(BorderLayout.EAST, msgkosupanel);

            southpanel.add(BorderLayout.NORTH, messagesentakupanel);

            okbutton = new JButton("         " + resources.getString("qkey.msg.msg125") + "         ");

            matesakiBox1.addItemListener(new AtesakiInputListener());
            okbutton.addActionListener(new NewMessageOKListener());

            JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg030"));
            cancelbutton.addActionListener(new NewMessageCancelListener());



            JPanel buttonpanel = new JPanel();
            buttonpanel.setLayout(new BorderLayout());


            JPanel pbuttonpanel = new JPanel();
            
            pbuttonpanel.add(okbutton);
            pbuttonpanel.add(cancelbutton);

            buttonpanel.add(BorderLayout.EAST, pbuttonpanel);
            cmessagefooter = new JLabel();
            buttonpanel.add(BorderLayout.CENTER, cmessagefooter);

            JPanel bbcontainer = new JPanel();
            bbcontainer.setBorder(BorderFactory.createEtchedBorder());

            buttonpanel.setBorder(BorderFactory.createEtchedBorder());
            bbcontainer.setLayout(new BorderLayout());
            bbcontainer.add(BorderLayout.WEST, modecontainer);
            bbcontainer.add(BorderLayout.SOUTH, buttonpanel);

            southpanel.add(BorderLayout.SOUTH, bbcontainer);

            newmessageFrame.getContentPane().add(BorderLayout.SOUTH, southpanel);
            newmessageFrame.pack();
            if (cleanupmode) {
                mbodyPanel.updateUI();
            }
        }

        //今ブラウズモードで選択されているあて先名を補完する
        //ツリーペインが選択されている場合はそこ優先
        DestInfo dico = treePane.getSelectedDestInfo();
        if (dico != null)  {

            if (dico.destinationType.equals(TOPIC_LITERAL)) {
                mqBox.setSelectedItem(TOPIC_LITERAL);
            } else
            if (dico.destinationType.equals(QUEUE_LITERAL)) {
                mqBox.setSelectedItem(QUEUE_LITERAL);
            } else {
                mqBox.setSelectedItem(LOCAL_STORE_LITERAL);
            }

            matesakiBox1.setSelectedItem(dico.destinationName);

        } else {


        ComboBoxEditor editor = qBox.getEditor();
        String orig_name = (String) editor.getItem();
        String name = getPureDestName(orig_name);

            if (orig_name.indexOf(TOPIC_SUFFIX) != -1) {
                mqBox.setSelectedItem(TOPIC_LITERAL);
            } else if (orig_name.indexOf(QUEUE_SUFFIX) != -1) {
                mqBox.setSelectedItem(QUEUE_LITERAL);
            } else {
                mqBox.setSelectedItem(LOCAL_STORE_LITERAL);
            }

        matesakiBox1.setSelectedItem(name);

        }

        newmessageFrame.setLocationRelativeTo(oya);
        newmessageFrame.setVisible(true);
    }

    public String getPureDestName(String orig) {
        //ABC : Queue
        //DEF : Topic

        int index = orig.indexOf(" :");

        if (index != -1) {
            return orig.substring(0, index);
        } else {

            index = orig.toLowerCase().indexOf("topic://");
            if (index != -1) {
                return orig.substring(index + 8);
            } else {
                index = orig.toLowerCase().indexOf("queue://");
                if (index != -1) {
                    return orig.substring(index + 8);
                } else {
                    return orig;
                }
            }

        }

    }

    /*
     public String getPureDestName(String orig) {
        //ABC : Queue
        //DEF : Topic

        int index = orig.indexOf(" :");

        if (index != -1) {
            return orig.substring(0, index);
        } else {

            index = orig.toLowerCase().indexOf("topic://");
            if (index != -1) {
                return orig.substring(index + 8);
            } else {
                index = orig.toLowerCase().indexOf("queue://");
                if (index != -1) {
                    return orig.substring(index + 8);
                } else {
                    return orig;
                }
            }

        }

    }
*/

    public String complementDestName(String orig) {

        if ((orig == null) || (orig.length() == 0)) {
            return "";
        }

        if (orig.indexOf(":") == -1) {
            //補完時は
            String rval = orig + QUEUE_SUFFIX;
            //System.out.println("ss008");
            //qBox.setSelectedItem(rval);
            return rval;
        } else {
            return orig;
        }
    }

    public String complementTopicName(String orig) {

        if ((orig == null) || (orig.length() == 0)) {
            return "";
        }

        if (orig.indexOf(":") == -1) {
            //補完時は
            String rval = orig + TOPIC_SUFFIX;
            return rval;
        } else {
            return orig;
        }
    }

    public void showSearchWindow() {

        // Create popup
        if (searchDialog == null) {
            searchDialog = new JDialog();
            searchDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails).getImage());
            searchDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    //
                }
            });

            searchDialog.setLocation(380, 95);
            searchDialog.getContentPane().setLayout(new BorderLayout());

            searchmsgPanel = new JPanel();
            searchmsgPanel.setLayout(new BorderLayout());

            JPanel searchmsg = new JPanel();
            searchDialog.setSize(200, 200);
            searchDialog.setTitle(resources.getString("qkey.msg.msg031"));

            searchtextfield = new JTextField(36);

            JLabel searchlabel = new JLabel(resources.getString("qkey.msg.msg032"));
            JLabel searchlabel2 = new JLabel(resources.getString("qkey.msg.msg033"));
            JPanel expl = new JPanel();
            expl.setLayout(new BorderLayout());
            tqBox = new JComboBox();
            tqBox.setEditable(true);

            JPanel tqboxpanel = new JPanel();
            tqboxpanel.setLayout(new BorderLayout());
            JLabel txboxlabel = new JLabel(resources.getString("qkey.msg.msg034"));
            tqboxpanel.add(BorderLayout.WEST, txboxlabel);
            tqboxpanel.add(BorderLayout.CENTER, tqBox);
            expl.add(BorderLayout.NORTH, tqboxpanel);
            expl.add(BorderLayout.CENTER, searchlabel);
            expl.add(BorderLayout.SOUTH, searchlabel2);

            String selectedDest = (String) tqBox.getSelectedItem();

            //テンプレート
            searchTemplateBox = new JComboBox();
            searchTemplateBox.addItemListener(new SearchTemplateItemListener());

            ArrayList extracted_history = QBrowserUtil.getHistoryFromFile("search_history");
            QBrowserUtil.ArrayListToJComboBox(extracted_history, searchTemplateBox);

            if (extracted_history.size() == 0) {
                DefaultComboBoxModel model = (DefaultComboBoxModel) searchTemplateBox.getModel();
                model.addElement("abc = 'abc' AND efg = 'efg' ");
                model.addElement("JMSPriority > 4 ");
                model.addElement("JMSDeliveryMode = 'PERSISTENT'");
                model.addElement("JMSDeliveryMode = 'NON_PERSISTENT'");
                model.addElement("JMS_SUN_COMPRESS = true");
                model.addElement("Country IN ('UK', 'US', 'France') ");
                model.addElement("Country NOT= 'UK'");
                model.addElement("phone LIKE '12%3' ");
                model.addElement("word LIKE 'l_se' ");
                model.addElement("prop_name IS NULL");
                model.addElement("prop_name IS NOT NULL");
                model.addElement("JMSTimestamp = 1240042958265");
            }



            searchmsg.add(searchtextfield);
            searchmsgPanel.add(BorderLayout.NORTH, expl);
            searchmsgPanel.add(BorderLayout.CENTER, searchmsg);
            JButton okbutton1 = new JButton("              OK              ");
            okbutton1.addActionListener(new SearchOKListener(this));
            JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg035") + "             ");
            cancelbutton.addActionListener(new SearchCancelListener());

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, okbutton1);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            JPanel temppanel = new JPanel();
            temppanel.setLayout(new BorderLayout());
            JLabel templabel = new JLabel(resources.getString("qkey.msg.msg036"));

            temppanel.add(BorderLayout.NORTH, templabel);
            temppanel.add(BorderLayout.CENTER, searchTemplateBox);
            temppanel.add(BorderLayout.SOUTH, pbuttonpanel);

            searchmsgPanel.add(BorderLayout.SOUTH, temppanel);
            searchDialog.getContentPane().add(BorderLayout.NORTH, searchmsgPanel);
            searchDialog.setLocationRelativeTo(oya);
            searchDialog.pack();

        }

        copyComboBox();

        searchDialog.setVisible(true);

    }

    void remove_localstore_buttons() {
        if (localstore_button_panel != null) {
            if (hasComponent(footerPanel, localstore_button_panel)) {
              try {
              footerPanel.remove(localstore_button_panel);
              footerPanel.updateUI();
              tree_location.updateUI();
              } catch (Throwable thex) {
                  //NOP
                  thex.printStackTrace();
              }
            }
        }
    }

    void set_localstore_buttons(String localstore_name_with_suffix) {
        try {
        JTable cTable = (JTable) jtableins.get(localstore_name_with_suffix);

        if (cTable != null) {
            if (localstore_button_panel == null) {

            localstore_button_panel = new JPanel();
            localstore_button_panel.setBorder(BorderFactory.createEtchedBorder());
            localstore_button_panel.setLayout(new BorderLayout());

            if (localstorelabel == null)
            localstorelabel = new JLabel();
            JPanel dp0 = new JPanel();
            JPanel dp = new JPanel();
            dp0.add(localstorelabel);
            dp.add(reload_button);
            dp.add(config_localstore_button);
            dp.add(lsclear_button);
            dp.add(lsdelete_button);
            localstore_button_panel.add(BorderLayout.WEST, dp0);
            localstore_button_panel.add(BorderLayout.EAST, dp);
            }

            lsdelete_button.putClientProperty(CURRENTLOCALSTORE, new String(localstore_name_with_suffix));
            lsclear_button.putClientProperty(CURRENTLOCALSTORE, new String(localstore_name_with_suffix));
            reload_button.putClientProperty(CURRENTLOCALSTORE, new String(localstore_name_with_suffix));
            config_localstore_button.putClientProperty(CURRENTLOCALSTORE, new String(localstore_name_with_suffix));
            
            footerPanel.add(BorderLayout.NORTH, localstore_button_panel);

            setLocalStoreFooterMessages(getPureDestName(localstore_name_with_suffix));

        } else {
            setFooter(resources.getString("qkey.msg.msg142") + " " + localstore_name_with_suffix + " " + resources.getString("qkey.msg.msg143"));
        }

        } catch (Throwable tex) {
            //NOP
            tex.printStackTrace();
        }
    }

    void remove_sub_button() {
        if (subscribe_button != null) {
            if (hasComponent(qbuttonpanel, unsubscribe_button)) {
              try {
              qbuttonpanel.remove(unsubscribe_button);
              } catch (Throwable thex) {
                  //NOP
                  thex.printStackTrace();
              }
            }
        }
        if (subscribe_resume_button != null) {
            if (hasComponent(qbuttonpanel, subscribe_resume_button)) {
               try {
               qbuttonpanel.remove(subscribe_resume_button);
               } catch (Throwable thex) {
                 //NOP
                 thex.printStackTrace();
               }
            }
        }
    }

    boolean hasComponent(JPanel panel, Component target) {
        Component[] children = panel.getComponents();
        for (int i = 0 ; i < children.length; i++) {
            if (children[i] == target) {
                return true;
            }
        }

        return false;
    }

    void set_sub_button(String dispName) {

        try {
        JTable cTable = (JTable) jtableins.get(dispName);

        if (cTable != null) {
        Boolean isRunning = (Boolean) subscribe_thread_status.get(dispName);
        if ((isRunning != null) && isRunning.booleanValue()) {
            String state_string = resources.getString("qkey.msg.msg136");

            //footer
            if (hasComponent(qbuttonpanel, subscribe_resume_button))
            qbuttonpanel.remove(subscribe_resume_button);
            qbuttonpanel.add(BorderLayout.WEST, unsubscribe_button);
            qbuttonpanel.updateUI();
            setFooter(dispName + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(cTable.getRowCount()) + " " + state_string);
        } else {
            String state_string = resources.getString("qkey.msg.msg137");
            if (hasComponent(qbuttonpanel, unsubscribe_button))
            qbuttonpanel.remove(unsubscribe_button);
            qbuttonpanel.add(BorderLayout.WEST, subscribe_resume_button);
            qbuttonpanel.updateUI();
            setFooter(dispName + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(cTable.getRowCount()) + " " + state_string);
        }

        } else {
            setFooter(resources.getString("qkey.msg.msg142") + " " + dispName + " " + resources.getString("qkey.msg.msg143"));
        }

        } catch (Throwable tex) {
            //NOP
            tex.printStackTrace();
        }
    }

    public void showSubscribeWindow(String selectedTopicName) {

        if (subscribeDialog != null) {
            subscribeDialog.dispose();
            subscribeDialog = null;
        }

         subscribeDialog = new JDialog();
         subscribeDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Subscribe).getImage());


         subscribeDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });

         subscribeDialog.setLocation(380,95);
         subscribeDialog.getContentPane().setLayout(new BorderLayout());

         subscribemsgPanel = new JPanel();
         subscribemsgPanel.setLayout(new BorderLayout());

         JPanel subscribemsg = new JPanel();
         subscribeDialog.setSize(200, 200);
         subscribeDialog.setTitle(resources.getString("qkey.msg.msg131"));

         subscribetextfield = new JTextField(36);
         matesakiBox3 = new JComboBox();
         matesakiBox3.setPreferredSize((new Dimension(250, 20)));

         importTopicNamesToMATESAKIBOX3();
         matesakiBox3.setEditable(true);



         JLabel subscribelabel = new JLabel(resources.getString("qkey.msg.msg126"));
         JPanel expl = new JPanel();
         expl.setLayout(new BorderLayout());

         JPanel tqboxpanel = new JPanel();
         tqboxpanel.setLayout(new BorderLayout());
        expl.add(BorderLayout.NORTH, tqboxpanel);
        expl.add(BorderLayout.CENTER, subscribelabel);

         //テンプレート
        if (subscribeTemplateBox == null) {
          subscribeTemplateBox = new JComboBox();
          subscribeTemplateBox.addItemListener(new SubscribeTemplateItemListener());
          DefaultComboBoxModel model = (DefaultComboBoxModel) subscribeTemplateBox.getModel();

          subscribeTemplateBox.setPreferredSize(new Dimension(250, 20));

          ArrayList extracted_history = QBrowserUtil.getHistoryFromFile("subscription_history");
          QBrowserUtil.ArrayListToJComboBox(extracted_history, subscribeTemplateBox);
        }

         subscribemsg.add(matesakiBox3);

         subscribemsgPanel.add(BorderLayout.NORTH, expl);
         subscribemsgPanel.add(BorderLayout.CENTER, subscribemsg);
         JButton okbutton1 = new JButton("               OK               ");
         okbutton1.addActionListener(new SubscribeOKListener());
         JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg129") + "             ");
         cancelbutton.addActionListener(new SubscribeCancelListener());

         JPanel pbuttonpanel = new JPanel();
         pbuttonpanel.setLayout(new BorderLayout());
         pbuttonpanel.add(BorderLayout.WEST, okbutton1);
         pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

         JPanel temppanel = new JPanel();
         temppanel.setLayout(new BorderLayout());
         JLabel templabel = new JLabel(resources.getString("qkey.msg.msg130"));



         temppanel.add(BorderLayout.NORTH, templabel);
         JPanel tdpanel = new JPanel();
         tdpanel.add(subscribeTemplateBox);
         temppanel.add(BorderLayout.CENTER, tdpanel);

         if (selectedTopicName != null) {
             matesakiBox3.setSelectedItem(selectedTopicName);
             matesakiBox3.setEnabled(false);
             subscribeTemplateBox.setEnabled(false);
         } else {
             subscribeTemplateBox.setEnabled(true);
         }
         

         JPanel centerPanel = new JPanel();
         centerPanel.setLayout(new BorderLayout());
         centerPanel.add(BorderLayout.NORTH, temppanel);

         localstoreBox = new JComboBox();
         localstoreBox.setPreferredSize(new Dimension(250, 20));
         importLocalStoreNamesToLOCALSTOREBOX();

         JPanel temppanel2 = new JPanel();
         temppanel2.setLayout(new BorderLayout());
         JLabel templabel2 = new JLabel(resources.getString("qkey.msg.msg274"));
         temppanel2.add(BorderLayout.NORTH, templabel2);
         JPanel dpanel = new JPanel();
         dpanel.add(localstoreBox);
         temppanel2.add(BorderLayout.CENTER, dpanel);
         temppanel2.add(BorderLayout.SOUTH, pbuttonpanel);
         centerPanel.add(BorderLayout.CENTER, temppanel2);


         subscribemsgPanel.add(BorderLayout.SOUTH, centerPanel);

         subscribeDialog.getContentPane().add(BorderLayout.NORTH, subscribemsgPanel);
         subscribeDialog.pack();

        
         subscribeDialog.setLocationRelativeTo(oya);
         subscribeDialog.setVisible(true);
    }

    public void showConnectionWindow() {

        // Create popup
        if (connectionDialog == null) {
            connectionDialog = new JDialog();
            connectionDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Connect).getImage());
            connectionDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    //
                }
            });

            connectionDialog.getContentPane().setLayout(new BorderLayout());

            connectionmsgPanel = new JPanel();
            connectionmsgPanel.setLayout(new BorderLayout());

            connectionDialog.setSize(200, 200);
            connectionDialog.setTitle(resources.getString("qkey.msg.msg166"));

            connectiontext_host = new JTextField(12);
            connectiontext_port = new JTextField(12);
            connectiontext_user = new JTextField(12);
            connectiontext_password = new JPasswordField(12);

            JLabel connectionlabel = new JLabel(resources.getString("qkey.msg.msg168"));
            JLabel connectionlabel2 = new JLabel(resources.getString("qkey.msg.msg169"));
            JPanel expl = new JPanel();
            expl.setLayout(new BorderLayout());

            expl.add(BorderLayout.CENTER, connectionlabel);
            expl.add(BorderLayout.SOUTH, connectionlabel2);

            //テンプレート
            connectionTemplateBox = new JComboBox();
            connectionTemplateBox.addItemListener(new ConnectionTemplateItemListener());
            ArrayList extracted_history = QBrowserUtil.getHistoryFromFile("connect_history");
            QBrowserUtil.ArrayListToJComboBox(extracted_history, connectionTemplateBox);

            //過去一回もやったことのない場合もしくは履歴ファイルがない場合はデフォルトで１つ追加
            if (extracted_history.size() == 0) {
              DefaultComboBoxModel model = (DefaultComboBoxModel) connectionTemplateBox.getModel();
              model.addElement("host = localhost port = 7676 user = admin password = admin ");
            }

            JPanel con_panel = new JPanel();


            GridBagLayout gbag = new GridBagLayout();
            con_panel.setLayout(gbag);
            GridBagConstraints vcs = new GridBagConstraints();
            
            int countY = 0;
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg174") + "  ", connectiontext_host, countY++);
            QBrowserUtil.addBlankRow(vcs, con_panel, gbag, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg175") + "  ", connectiontext_port, countY++);
            QBrowserUtil.addBlankRow(vcs, con_panel, gbag, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg176") + "  ", connectiontext_user, countY++);
            QBrowserUtil.addBlankRow(vcs, con_panel, gbag, countY++);
            QBrowserUtil.addLabelAndValueComponent(vcs, con_panel, gbag, resources.getString("qkey.msg.msg177") + "  ", connectiontext_password, countY++);

            connectionmsgPanel.add(BorderLayout.NORTH, expl);
            connectionmsgPanel.add(BorderLayout.CENTER, con_panel);
            JButton okbutton1 = new JButton("              OK             ");
            okbutton1.addActionListener(new ConnectionOKListener());
            JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg035") + "             ");
            cancelbutton.addActionListener(new ConnectionCancelListener());

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, okbutton1);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            JPanel temppanel = new JPanel();
            temppanel.setLayout(new BorderLayout());
            JLabel templabel = new JLabel(resources.getString("qkey.msg.msg167"));

            temppanel.add(BorderLayout.NORTH, templabel);
            temppanel.add(BorderLayout.CENTER, connectionTemplateBox);
            temppanel.add(BorderLayout.SOUTH, pbuttonpanel);

            connectionmsgPanel.add(BorderLayout.SOUTH, temppanel);
            connectionDialog.getContentPane().add(BorderLayout.NORTH, connectionmsgPanel);
            connectionDialog.setLocation(oya_frame.getX() + 340, oya_frame.getY() + 250);

            connectionDialog.pack();

        }

        connectionDialog.setVisible(true);
    }

    public void showForwardWindow(int x, int y, boolean deleteSrcMessageAfterForward) {

        if (forwardDialog != null) {
            forwardDialog.dispose();
            forwardDialog = null;
        }

        // Create popup
        if (forwardDialog == null) {
            forwardDialog = new JDialog();

            if (deleteSrcMessageAfterForward) {
              forwardDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg).getImage());
            } else {
              forwardDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Move).getImage());
            }


            forwardDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    //
                }
            });

            forwardDialog.getContentPane().setLayout(new BorderLayout());

            forwardmsgPanel = new JPanel();
            forwardmsgPanel.setLayout(new BorderLayout());

            JPanel forwardmsg = new JPanel();
            forwardDialog.setSize(200, 200);

            JLabel forwardlabel = null;
            if (deleteSrcMessageAfterForward) {
                forwardDialog.setTitle(resources.getString("qkey.msg.msg224"));
                forwardlabel = new JLabel(resources.getString("qkey.msg.msg225"));
            } else {
                forwardDialog.setTitle(resources.getString("qkey.msg.msg133"));
                forwardlabel = new JLabel(resources.getString("qkey.msg.msg134"));
            }

       forwardtextfield = new JTextField(36);
        matesakiBox2 = new JComboBox();
        Dimension dm = matesakiBox2.getPreferredSize();
        dm.setSize(10 * dm.getWidth(), dm.getHeight());
        matesakiBox2.setPreferredSize(dm);
        
        importTopicNamesToMATESAKIBOX2();
        matesakiBox2.setEditable(true);

        JPanel expl = new JPanel();
        expl.setLayout(new BorderLayout());

        forwardBox = new JComboBox();
        forwardBox.addItemListener(new SendForwardAtesakiComboBoxItemListener());
        Dimension d = forwardBox.getPreferredSize();
        d.setSize(110 , d.getHeight());
        forwardBox.setPreferredSize(d);
        forwardBox.setEditable(false);

        DefaultComboBoxModel model = (DefaultComboBoxModel) forwardBox.getModel();
        model.addElement(QUEUE_LITERAL);
        model.addElement(TOPIC_LITERAL);
        model.addElement(LOCAL_STORE_LITERAL);

        JPanel tqboxpanel = new JPanel();
        tqboxpanel.setLayout(new BorderLayout());
        JLabel txboxlabel = new JLabel(resources.getString("qkey.msg.msg044"));
        tqboxpanel.add(BorderLayout.WEST,txboxlabel);
        JPanel dp = new JPanel(); dp.setLayout(new BorderLayout());
        dp.add(BorderLayout.WEST, forwardBox);
        tqboxpanel.add(BorderLayout.CENTER, dp);
        expl.add(BorderLayout.NORTH, tqboxpanel);
        expl.add(BorderLayout.CENTER, forwardlabel);

        forwardmsg.add(matesakiBox2);
        forwardmsgPanel.add(BorderLayout.NORTH, expl);
        forwardmsgPanel.add(BorderLayout.CENTER, forwardmsg);
        JButton okbutton1 = new JButton("               OK               ");
        okbutton1.addActionListener(new ForwardOKListener(deleteSrcMessageAfterForward));
        JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg129") + "             ");
        cancelbutton.addActionListener(new ForwardCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        forwardmsgPanel.add(BorderLayout.SOUTH, pbuttonpanel);


         forwardDialog.getContentPane().add(BorderLayout.NORTH, forwardmsgPanel);
         forwardDialog.setLocationRelativeTo(oya);

         forwardDialog.pack();

        }

        forwardDialog.setVisible(true);

    }

    void showMessageSendConfirmation(String bodyinputtype) {

        // Create popup
        if (sendconfirmDialog != null && sendconfirmDialog.isShowing()) {
            sendconfirmDialog.dispose();
        }
        sendconfirmDialog = new JDialog();
        sendconfirmDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg).getImage());
        sendconfirmDialog.setLocation(120, 120);

        sendconfirmDialog.getContentPane().setLayout(new BorderLayout());

        JPanel sendmsgPanel = new JPanel();
        sendmsgPanel.setLayout(new BorderLayout());
        JPanel sendmsg = new JPanel();
        sendconfirmDialog.setSize(200, 100);
        sendconfirmDialog.setTitle(resources.getString("qkey.msg.msg037"));

        AutoResizingTextArea ta = new AutoResizingTextArea(3,15,36);

        ta.setEditable(false);

        StringBuffer sb = new StringBuffer();
        sb.append(resources.getString("qkey.msg.msg038"));
        sb.append("\n\n");

        nmi = new NewMessageInfo();


        //**********************************************************************************
        //ヘッダー情報取得
        if (header_table != null) {
            for (int i = 0 ; i < header_table.getRowCount(); i++) {

                Property header_prop = header_table.getPropertyAtRow(i);

                if (header_prop.getKey() != null)
                nmi.addHeaderinfo(header_prop);
            }

               Property delvprop = new Property();
               delvprop.setKey("JMSDeliverMode");
               delvprop.setProperty_type(Property.INT_TYPE);
            
            if (cdeliverymode.getSelectedIndex() == 1) {
               delvprop.setProperty_value(1);
               
            } else {
               delvprop.setProperty_value(2);
            }

               nmi.addHeaderinfo(delvprop);

            if (!nmi.getHeaderinfos().isEmpty()) {
                sb.append(resources.getString("qkey.msg.msg039")).append("\n");
                ArrayList input_header_info = nmi.getHeaderinfos();
                for (int i = 0; i < input_header_info.size(); i++) {
                    Property pkey = (Property) input_header_info.get(i);
                    sb.append(pkey.getKey()).append(" = ").append(pkey.getProperty_value()).append("\n");
                }

                sb.append(resources.getString("qkey.msg.msg040")).append("\n").append("\n");

            }
        }

        //ユーザプロパティ情報取得
        if (property_table != null) {
            for (int i = 0 ; i < property_table.getRowCount(); i++) {
                Property user_prop = property_table.getPropertyAtRow(i);

                if((user_prop.getKey() != null) && (user_prop.getKey().length() != 0))
                nmi.addUserproperty(user_prop);
            }

            if ((ccompressmode != null) && (ccompressmode.getSelectedIndex() == 1)) {
               Property cmpprop = new Property();
               cmpprop.setKey("JMS_SUN_COMPRESS");
               cmpprop.setProperty_type(Property.BOOLEAN_TYPE);
               cmpprop.setProperty_value(true);
               //前のパネルから来たプロパティではないため、ここで自己検証する。
               try { cmpprop.selfValidate(); } catch (QBrowserPropertyException pbpe) {}
               nmi.addUserproperty(cmpprop);
            }

            if (!nmi.getUserproperties().isEmpty()) {
                sb.append(resources.getString("qkey.msg.msg041")).append("\n");
                ArrayList input_userprop_info = nmi.getUserproperties();
                for (int i = 0; i < input_userprop_info.size(); i++) {
                    Property pkey = (Property) input_userprop_info.get(i);
                    sb.append(pkey.getKey()).append("(").append(pkey.getProperty_type()).append(") = ");
                    Object pvs = pkey.getProperty_value();

                    if (pkey.getProperty_type().equals(Property.STRING_TYPE)) {
                        
                        if (pvs instanceof JTextArea) {
                            sb.append(((JTextArea)pvs).getText());
                        } else if (pvs instanceof String) {
                            sb.append((String)pvs);
                        }
                    } else
                    if (pkey.getProperty_type().equals(Property.BOOLEAN_TYPE)) {
                        if(pvs instanceof JComboBox) {
                          sb.append((String)((JComboBox)pkey.getProperty_value()).getSelectedItem());
                        } else if (pvs instanceof Boolean) {
                          sb.append((Boolean)pkey.getProperty_value());
                        }
                    } else {

                        if (pvs instanceof JTextArea) {
                            sb.append(((JTextArea)pvs).getText());
                        } else if  (pvs instanceof JTextField) {
                            sb.append(((JTextField)pvs).getText());
                        } else {
                            sb.append(pkey.getProperty_value());
                        }
                    }

                    sb.append("\n");
                }

                sb.append(resources.getString("qkey.msg.msg042")).append("\n").append("\n");

            }
        }

        //宛先名取得

        String dest_name = matesakiname.getText();
        nmi.setDest(dest_name);
        sb.append(resources.getString("qkey.msg.msg043")).append(" ").append(dest_name).append("\n");

        //宛先タイプ取得
        String dest_type = (String) mqBox.getSelectedItem();
        nmi.setDest_type(dest_type);
        sb.append(resources.getString("qkey.msg.msg044")).append(" ").append(dest_type).append("\n\n");

        //入力タイプ別にボディ情報を入手
        if (bodyinputtype.equals(TEXTMESSAGE)) {
            //Text
            nmi.setBody_inputtype(TEXTMESSAGE);
            String data = mbodyPanel.textArea.getText();
            nmi.setBody_text(data);
            sb.append(resources.getString("qkey.msg.msg045")).append(" Text Input").append("\n");
            sb.append(resources.getString("qkey.msg.msg046")).append(" ").append(data).append("\n");
        } else if (bodyinputtype.equals(BYTESMESSAGE)) {
            //BytesMessage

            
            nmi.setBody_inputtype(BYTESMESSAGE);
            if (mfilepath.getText().equals(resources.getString("qkey.msg.msg219"))) {
                //sendの時までbytes文字列操作遅延
                try {
                sb.append(resources.getString("qkey.msg.msg218")).append(passthrough_bytesmessage.getBodyLength()).append("\n");
                } catch (JMSException jmse) {}
            } else {
                File ff = new File(mfilepath.getText());
                nmi.setBody_file(ff);

                sb.append(resources.getString("qkey.msg.msg047")).append(" File Input").append("\n");
                sb.append(resources.getString("qkey.msg.msg048")).append(" ").append(ff.getAbsolutePath()).append("\n");
                long byte_size = 0L;

                sb.append(resources.getString("qkey.msg.msg049"));
                if (ff.length() > 1023) {

                    byte_size = ff.length() / 1024;
                    sb.append(byte_size).append("KB");
                } else {
                    byte_size = ff.length();
                    sb.append(byte_size);
                }
                sb.append("\n");
            }
        } else if (bodyinputtype.equals(MAPMESSAGE)) {
            //MapMessage
            nmi.setBody_inputtype(MAPMESSAGE);
            sb.append(resources.getString("qkey.msg.msg045")).append(" MapMessage").append("\n");
            if (mapm_property_table != null) {
                sb.append(resources.getString("qkey.msg.msg203")).append("\n");
                for (int i = 0; i < mapm_property_table.getRowCount(); i++) {
                    MapMessageInputProperty pkey = mapm_property_table.getPropertyAtRow(i);
                        String kkey = pkey.getKey();
                        String ktype = pkey.getProperty_type();
                        Object kvalue = pkey.getProperty_value();

                    if ((kkey != null) && (kkey.length() != 0)) {

                        sb.append(kkey).append("(").append(ktype).append(") = ");
                        if (ktype.equals(Property.STRING_TYPE)) {

                            if (kvalue instanceof JTextArea) {

                                sb.append(((JTextArea)kvalue).getText());

                            } else
                            if (kvalue instanceof JTextField) {

                                sb.append(((JTextField)kvalue).getText());

                            } else
                            if (kvalue instanceof String) {
                                sb.append((String)kvalue);
                            }

                        } else
                        if (ktype.equals(Property.BYTES_TYPE)) {

                            if (kvalue instanceof byte[]) {
                                sb.append(resources.getString("qkey.msg.msg221"));
                                byte[] bv = (byte[]) kvalue;
                                sb.append(bv.length);

                            } else if (kvalue instanceof JComboBox) {
                                sb.append((String)((JComboBox)kvalue).getSelectedItem());
                            } else {
                                sb.append(resources.getString("qkey.msg.msg205"));
                                sb.append(kvalue);
                            }

                        } else if (ktype.equals(Property.BOOLEAN_TYPE)) {
                            if (kvalue instanceof JComboBox) {
                                sb.append((String)((JComboBox)kvalue).getSelectedItem());
                            } else {
                                sb.append(kvalue);
                            }
                        } else {
                            if (kvalue instanceof JTextArea) {

                                sb.append(((JTextArea) kvalue).getText());

                            } else if (kvalue instanceof JTextField) {

                                sb.append(((JTextField) kvalue).getText());

                            } else {
                                sb.append(kvalue);
                            }
                            
                        }

                        sb.append("\n");

                    }
                }
                sb.append(resources.getString("qkey.msg.msg204")).append("\n").append("\n");
            }
        } else if (bodyinputtype.equals(STREAMMESSAGE)) {
            //StreamMessage
            nmi.setBody_inputtype(STREAMMESSAGE);
            sb.append(resources.getString("qkey.msg.msg045")).append(" StreamMessage").append("\n");
            if (sm_property_table != null) {
                sb.append(resources.getString("qkey.msg.msg238")).append("\n");
                for (int i = 0; i < sm_property_table.getRowCount(); i++) {
                    StreamMessageInputProperty pkey = sm_property_table.getPropertyAtRow(i);
                        String kkey = String.valueOf(pkey.getSmKey());
                        String ktype = pkey.getProperty_type();
                        Object kvalue = pkey.getProperty_value();

                    if ((kkey != null) && (kkey.length() != 0)) {

                        sb.append(kkey).append("(").append(ktype).append(") = ");
                        if (ktype.equals(Property.STRING_TYPE)) {

                            if (kvalue instanceof JTextArea) {

                                sb.append(((JTextArea)kvalue).getText());

                            } else
                            if (kvalue instanceof JTextField) {

                                sb.append(((JTextField)kvalue).getText());

                            } else
                            if (kvalue instanceof String) {
                                sb.append((String)kvalue);
                            }

                        } else
                        if (ktype.equals(Property.BYTES_TYPE)) {

                            if (kvalue instanceof byte[]) {
                                sb.append(resources.getString("qkey.msg.msg221"));
                                byte[] bv = (byte[]) kvalue;
                                sb.append(bv.length);

                            } else if (kvalue instanceof JComboBox) {
                                sb.append((String)((JComboBox)kvalue).getSelectedItem());
                            } else {
                                sb.append(resources.getString("qkey.msg.msg205"));
                                sb.append(kvalue);
                            }

                        } else if (ktype.equals(Property.BOOLEAN_TYPE)) {
                            if (kvalue instanceof JComboBox) {
                                sb.append((String)((JComboBox)kvalue).getSelectedItem());
                            } else {
                                sb.append(kvalue);
                            }
                        } else {
                            if (kvalue instanceof JTextArea) {

                                sb.append(((JTextArea) kvalue).getText());

                            } else if (kvalue instanceof JTextField) {

                                sb.append(((JTextField) kvalue).getText());

                            } else {
                                sb.append(kvalue);
                            }

                        }

                        sb.append("\n");

                    }
                }
                sb.append(resources.getString("qkey.msg.msg239")).append("\n").append("\n");
            }
        } else if (bodyinputtype.equals(MESSAGE)) {
            //Message
            nmi.setBody_inputtype(MESSAGE);
            sb.append(resources.getString("qkey.msg.msg045")).append(" Message").append("\n");
            sb.append(resources.getString("qkey.msg.msg244")).append("\n");

        }

        int soufukaisu = 1;
        try {
            soufukaisu = Integer.parseInt(soufukosu.getText().trim());
        } catch (Exception nfe) {
            //NOP
        }
        sb.append(resources.getString("qkey.msg.msg050")).append(soufukaisu);
        nmi.setSoufukosu(soufukaisu);

        //**********************************************************************************

        ta.setText(sb.toString());


        sendmsg.add(new JScrollPane(ta));

        sendmsgPanel.add(BorderLayout.NORTH, sendmsg);
        JButton okbutton1 = new JButton("              " + resources.getString("qkey.msg.msg051") + "              ");
        okbutton1.addActionListener(new SendOKListener());
        JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg052"));
        cancelbutton.addActionListener(new SendCancelListener());

        JPanel pbuttonpanel = new JPanel();
        pbuttonpanel.setLayout(new BorderLayout());
        pbuttonpanel.add(BorderLayout.WEST, okbutton1);
        pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

        sendmsgPanel.add(BorderLayout.CENTER, pbuttonpanel);

        sendconfirmDialog.getContentPane().add(BorderLayout.NORTH, sendmsgPanel);
        sendconfirmDialog.pack();

        if (newmessageFrame == null) {
          sendconfirmDialog.setLocationRelativeTo(oya);
        } else {
          sendconfirmDialog.setLocationRelativeTo(newmessageFrame);
        }

        sendconfirmDialog.setVisible(true);
    }

    void processLocalStoreDir(String puredest, File dir, LocalMsgTable vmt, TextArea ta) {

            File[] files = dir.listFiles();
            for (int i = 0 ; i < files.length ;i++) {
                if (files[i].isFile()) {
            try {
              addLocalStoreCTableFromSpecifiedFilePath(puredest ,files[i].getAbsolutePath(), vmt, ta);
           } catch (Exception ex) {
               ta.append(resources.getString("qkey.msg.msg273") + " " + ex.getMessage());
           }
                } else if (files[i].isDirectory()) {
                    processLocalStoreDir(puredest, files[i], vmt, ta);
                }
            }

    }

    public void prepareLocalStoreTab(String localstorename_with_suffix) {
        int current_tab_index = 0;

        //まだタブがないとき
        if (!isNamedTabAlreadyCreated(localstorename_with_suffix)) {

            //先にキャッシュにあるかを判定する
            JTable cTable = (JTable) jtableins.get(localstorename_with_suffix);
            JTable taihiTable = new JTable(new LocalMsgTable());
            
            if (cTable != null) {
                 //キャッシュにある場合は、旧データを退避しておく
                 localTableCopy(cTable, taihiTable);
            }
            current_tab_index = createNewLocalMsgPane(localstorename_with_suffix);

            if (cTable == null) {

                
                cTable = (JTable) jtableins.get(localstorename_with_suffix);
                LocalMsgTable imt = (LocalMsgTable)cTable.getModel();
                imt.init();
                //キャッシュにもない場合=完全にまっさらなのでlsmからデータを抽出する
                //ローカルストアの場所(DIR)をチェックして、メッセージファイルが含まれていたら復元
                String puredestname = getPureDestName(localstorename_with_suffix);
                LocalStoreProperty lsp = lsm.getLocalStoreProperty(puredestname);

                if (lsp.isValid()) {

                     String local_store_path = lsp.getReal_file_directory();
                     File td = new File(local_store_path);

                     if (td.exists()) {
                       processLocalStoreDir(puredestname, td , imt, new TextArea());
                     } else {
                       td.mkdirs();
                     }

                }

                imt.fireTableDataChanged();
            }

            localTableCopy(taihiTable, cTable);
            jtableins.put(localstorename_with_suffix, cTable);
            tabbedPane.setSelectedIndex(current_tab_index);

        } else {
            current_tab_index = tabbedPane.indexOfTab(localstorename_with_suffix);
            tabbedPane.setSelectedIndex(current_tab_index);
        }

    }


    boolean isDestNameInDestNameComboBox(String destName) {
        for (int i = 0; i < qBox.getItemCount(); i++) {
            if (destName.equals((String) qBox.getItemAt(i))) {
                return true;
            }
        }
        return false;
    }

    void validateAllUserProperties() {
        //重複チェック用
        HashSet keycheck = new HashSet();

        try {
            for (int hi = 0; hi < property_table.getRowCount(); hi++) {
                InputProperty hpr = property_table.getPropertyAtRow(hi);

                if (hpr.getKey() != null) {
                    if (keycheck.contains(hpr.getKey())) {
                        throw new QBrowserPropertyException("Q0019" + MAGIC_SEPARATOR + hpr.getKey() + MAGIC_SEPARATOR + hpr.getProperty_type() + MAGIC_SEPARATOR + hpr.getProperty_value());
                    } else {
                        //System.out.println("abc");
                        keycheck.add(hpr.getKey());
                    }
                }

                hpr.selfValidate();

            }
            newmessage1stpanel_user_props_ok = true;
        } catch (QBrowserPropertyException qpe) {
            last_user_prop_validate_error = qpe.getMessage();
            newmessage1stpanel_user_props_ok = false;
        }
    }

    void validateAllStreamMessageData() {

        try {

            //重複チェック用
            HashSet keycheck = new HashSet();

            for (int hi = 0; hi < sm_property_table.getRowCount(); hi++) {
                StreamMessageInputProperty hpr = sm_property_table.getPropertyAtRow(hi);

                JComboBox jcb = (JComboBox) hpr.getType_combo_box();
                hpr.setProperty_type((String) jcb.getSelectedItem());

                if (hpr.getKey() != null) {
                    if (keycheck.contains(hpr.getKey())) {
                        throw new QBrowserPropertyException("Q0019" + MAGIC_SEPARATOR + hpr.getKey() + MAGIC_SEPARATOR + hpr.getProperty_type() + MAGIC_SEPARATOR + hpr.getProperty_value());
                    } else {
                        //System.out.println("abc");
                        keycheck.add(hpr.getKey());
                    }
                }

                hpr.selfValidate();

            }

            newmessage1stpanel_sm_props_ok = true;
        } catch (QBrowserPropertyException qpe) {
            last_streammessage_prop_validate_error = qpe.getMessage();
            newmessage1stpanel_sm_props_ok = false;
        }
    }

    void sendMessage() throws Exception {
        //MQへメッセージを送信する
        //宛先と、メッセージタイプにより作成するメッセージオブジェクトが異なる
        //Text入力→TextMessageクラス
        //File入力→BytesMessageクラス
        //他のメッセージクラスに対応するにはここに追加
        //2009-05 MapMessage追加
        //2009-06 StreamMessage追加
        //2009-06 Message追加

        Message message = null;

        //メッセージタイプ判定
        if (nmi.getBody_inputtype().equals(TEXTMESSAGE)) {

            message = session.createTextMessage(nmi.getBody_text());

        } else if (nmi.getBody_inputtype().equals(BYTESMESSAGE)) {

            //BytesMessage
            BytesMessage bmsg = session.createBytesMessage();

            if (!mfilepath.getText().equals(resources.getString("qkey.msg.msg219"))) {

                java.io.FileInputStream fi = new FileInputStream(nmi.getBody_file());

                byte buf[] = new byte[1024];
                int len = 0;

                int filesizecount = 0;

                while ((len = fi.read(buf)) != -1) {
                    filesizecount += buf.length;
                    bmsg.writeBytes(buf, 0, len);
                }

                fi.close();

            } else {
                //BytesMessageからBytesMessageへ
                //コピーする

                if (passthrough_bytesmessage != null) {

                    passthrough_bytesmessage.reset();

                    byte[] bibi = new byte[1024];
                    int len = 0;
                    long readfilesize = 0;

                    while ((len = passthrough_bytesmessage.readBytes(bibi)) != -1) {
                        bmsg.writeBytes(bibi, 0, len);
                        readfilesize += len;
                    }

                }
            }

            message = bmsg;

        } else if (nmi.getBody_inputtype().equals(MAPMESSAGE)) {
            //MapMessage
            MapMessage mapmsg = session.createMapMessage();
            if (mapm_property_table != null) {

             try {
              for (int i = 0 ; i < mapm_property_table.getRowCount(); i++) {
                Property mapm_body_data = mapm_property_table.getPropertyAtRow(i);

                String key = mapm_body_data.getKey();

                //keyがnullのものについては、未入力と判定する
                if (key != null) {

                switch (mapm_body_data.validated_type) {
                    case Property.INVALID_TYPE_INT:
                       //VALIDではない、セットスキップ
                       break;

                    case Property.PASSTHROUGH_TYPE_INT:
                        mapmsg.setBytes(key, mapm_body_data.getProperty_valueASBytes());
                       break;

                    case Property.STRING_TYPE_INT:
                       mapmsg.setString(key, mapm_body_data.getProperty_valueASString());
                       break;
                       
                    case Property.BOOLEAN_TYPE_INT:
                       mapmsg.setBoolean(key, mapm_body_data.getProperty_valueASBoolean());
                       break;
                       
                    case Property.INT_TYPE_INT:
                       mapmsg.setInt(key, mapm_body_data.getProperty_valueASInt());
                       break;
                       
                    case Property.BYTE_TYPE_INT:
                       mapmsg.setByte(key, mapm_body_data.getProperty_valueASByte());
                       break;

                    case Property.BYTES_TYPE_INT:

                       byte[] bytesarray = QBrowserUtil.extractBytes(mapm_body_data.getProperty_valueASString());
                       if(bytesarray == null) {
                           throw new Exception("Q0021");
                       }
                       mapmsg.setBytes(key, bytesarray);
                       break;
                       
                    case Property.DOUBLE_TYPE_INT:
                       mapmsg.setDouble(key, mapm_body_data.getProperty_valueASDouble());
                       break;
                       
                    case Property.FLOAT_TYPE_INT:
                       mapmsg.setFloat(key, mapm_body_data.getProperty_valueASFloat());
                       break;
                       
                    case Property.LONG_TYPE_INT:
                       mapmsg.setLong(key, mapm_body_data.getProperty_valueASLong());
                       break;
                       
                    case Property.SHORT_TYPE_INT:
                       mapmsg.setShort(key, mapm_body_data.getProperty_valueASShort());
                       break;
                       
                    default :
                       //NOP
                        break;
                }



                }

               } //end for


                } catch (Exception msgstex) {
                    String errmsg = "";
                    TextArea ta = new TextArea("", 5, 50, TextArea.SCROLLBARS_NONE);
                    try {
                    String errmsge = resources.getString("qkey.msg.err." + msgstex.getMessage());
                    ta.append(errmsge);

                    } catch (Exception eex) {}
                    popupMessageDialog(resources.getString("qkey.msg.msg206"), ta,
                                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));
                    return;
                }
            }
            
            message = mapmsg;


        } else if (nmi.getBody_inputtype().equals(STREAMMESSAGE)) {
            //StreamMessage
            StreamMessage smsg = session.createStreamMessage();
            if (sm_property_table != null) {

             try {
              for (int i = 0 ; i < sm_property_table.getRowCount(); i++) {
                StreamMessageInputProperty sm_body_data = sm_property_table.getPropertyAtRow(i);

                String key = String.valueOf(sm_body_data.getSmKey());

                switch (sm_body_data.validated_type) {
                    case Property.INVALID_TYPE_INT:
                       //VALIDではない、セットスキップ
                       break;

                    case Property.PASSTHROUGH_TYPE_INT:
                        smsg.writeBytes(sm_body_data.getProperty_valueASBytes());
                       break;

                    case Property.STRING_TYPE_INT:
                       smsg.writeString(sm_body_data.getProperty_valueASString());
                       break;

                    case Property.BOOLEAN_TYPE_INT:
                       smsg.writeBoolean(sm_body_data.getProperty_valueASBoolean());
                       break;

                    case Property.INT_TYPE_INT:
                       smsg.writeInt(sm_body_data.getProperty_valueASInt());
                       break;

                    case Property.BYTE_TYPE_INT:
                       smsg.writeByte(sm_body_data.getProperty_valueASByte());
                       break;

                    case Property.CHARACTER_TYPE_INT:
                       smsg.writeChar(sm_body_data.getProperty_valueASCharacter());
                       break;

                    case Property.BYTES_TYPE_INT:

                       byte[] bytesarray = QBrowserUtil.extractBytes(sm_body_data.getProperty_valueASString());
                       if(bytesarray == null) {
                           throw new Exception("Q0021");
                       }
                       smsg.writeBytes(bytesarray);
                       break;

                    case Property.DOUBLE_TYPE_INT:
                       smsg.writeDouble(sm_body_data.getProperty_valueASDouble());
                       break;

                    case Property.FLOAT_TYPE_INT:
                       smsg.writeFloat(sm_body_data.getProperty_valueASFloat());
                       break;

                    case Property.LONG_TYPE_INT:
                       smsg.writeLong(sm_body_data.getProperty_valueASLong());
                       break;

                    case Property.SHORT_TYPE_INT:
                       smsg.writeShort(sm_body_data.getProperty_valueASShort());
                       break;

                    default :
                       //NOP
                        break;
                }

               } //end for


                } catch (Exception msgstex) {
                    String errmsg = "";
                    TextArea ta = new TextArea("", 5, 50, TextArea.SCROLLBARS_NONE);
                    try {
                    String errmsge = resources.getString("qkey.msg.err." + msgstex.getMessage());
                    ta.append(errmsge);
                    } catch (Exception eex) {}
                    popupMessageDialog(resources.getString("qkey.msg.msg240"), ta,
                                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));
                    return;
                }
            }

            message = smsg;

        } else if (nmi.getBody_inputtype().equals(MESSAGE)) {
            message = session.createMessage();
        }//message作成処理終了


        setUserPropertyInMessage(message);

        //宛先別に送りを作成
        if (nmi.getDest_type().equals(QUEUE_LITERAL)) {
            //Queue
            String cjp = getPureDestName(nmi.getDest());
            javax.jms.Queue queue = session.createQueue(cjp);

            MessageProducer sender = session.createProducer(queue);

            setHeaderInfoInMessage(sender, message);

            TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
            ta.setEditable(false);
            ta.setBackground(Color.WHITE);

            int size = nmi.getSoufukosu();

            int appropriaterowsize = size + 3;
            if (appropriaterowsize > 15) {
                appropriaterowsize = 15;
            }

            //Queue送信進捗情報
            ta.setRows(appropriaterowsize);

            String dispid = nmi.toString();
            DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg053"), ta,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg), oya_frame);
            Thread dprth = new Thread(dpr);
            DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
            dprth.start();

            while (!dpr.isStarted()) {
                try {
                    Thread.sleep(100);
                } catch (Throwable thex) {}
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
            String datef = df.format(new Date(System.currentTimeMillis()));
            ta.append(resources.getString("qkey.msg.msg054") + nmi.getDest() + resources.getString("qkey.msg.msg055") + "\n");
            ta.append(resources.getString("qkey.msg.msg056") + datef + "\n");

            //int caretcount = 0;
            StringBuilder mediumbuffer = new StringBuilder();
            String msg057 = resources.getString("qkey.msg.msg057");
            String msg058 = resources.getString("qkey.msg.msg058");
            String msg059 = resources.getString("qkey.msg.msg059");
            String msg060 = resources.getString("qkey.msg.msg060");

            for (int i = 0; i < size; i++) {

                sender.send(message);
                
                mediumbuffer.append(msg057 + " " + message.getJMSMessageID() + " " + msg058);
                mediumbuffer.append((i + 1) + msg059 + size + msg060 + "\n");

                //ta.append(mediumbuffer.toString());
                //ta.setCaretPosition(ta.getText().length());
                //mediumbuffer = new StringBuilder();
            }

            sender.close();

            ta.append(mediumbuffer.toString());
            ta.setCaretPosition(ta.getText().length());
            ta.append(resources.getString("qkey.msg.msg061"));

            ifnotyetDestNameInQueueDisplayBoxThenAdd(cjp);

        } else if (nmi.getDest_type().equals(TOPIC_LITERAL)) {
            //Topic

            String cjp = getPureDestName(nmi.getDest());
            javax.jms.Topic topic = session.createTopic(cjp);
            MessageProducer publisher = session.createProducer(topic);


            setHeaderInfoInMessage(publisher, message);

            TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
            ta.setEditable(false);
            ta.setBackground(Color.WHITE);
            int size = nmi.getSoufukosu();

            int appropriaterowsize = size + 3;
            if (appropriaterowsize > 15) {
                appropriaterowsize = 15;
            }

            //Topic送信進捗情報
            ta.setRows(appropriaterowsize);
            StringBuffer sb = new StringBuffer();
            String dispid = nmi.toString();
            DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg062"), ta,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg), oya_frame);
            Thread dprth = new Thread(dpr);
            DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
            dprth.start();

            while (!dpr.isStarted()) {
                try {
                    Thread.sleep(100);
                } catch (Throwable thex) {}
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
            String datef = df.format(new Date(System.currentTimeMillis()));
            ta.append(resources.getString("qkey.msg.msg063") + nmi.getDest() + resources.getString("qkey.msg.msg064") + "\n");
            ta.append(resources.getString("qkey.msg.msg065") + datef + "\n");
            StringBuilder mediumbuffer = new StringBuilder();

            String msg066 = resources.getString("qkey.msg.msg066");
            String msg067 = resources.getString("qkey.msg.msg067");
            String msg068 = resources.getString("qkey.msg.msg068");
            String msg069 = resources.getString("qkey.msg.msg069");

            for (int i = 0; i < nmi.getSoufukosu(); i++) {
                publisher.send(message);
                mediumbuffer.append(msg066 + message.getJMSMessageID() + msg067);
                mediumbuffer.append((i + 1) + msg068 + size + msg069 + "\n");
                
            }

            publisher.close();
            ta.append(mediumbuffer.toString());
            ta.setCaretPosition(ta.getText().length());
            ta.append(resources.getString("qkey.msg.msg070"));

            ifnotyetDestNameInTopicDisplayBoxThenAdd(cjp);
        } else if (nmi.getDest_type().equals(LOCAL_STORE_LITERAL)) {
              //転送ターゲット変数：forward_target_name
              String cjp = getPureDestName(nmi.getDest());
              //TODO
              //宛先→ローカルストアへの転送とは、コピーのこと。
              //ただのコピーではなく、メモリ上のメッセージ→一旦ダウンロード
              //ターゲットローカルストアのフォルダへ物理コピー、
              //メモリ上のメッセージもコピーの手順となる。
              LocalStoreManager.LocalStore localstore = lsm.getLocalStoreInstance(cjp);
              TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
              ta.setEditable(false);
              ta.setBackground(Color.WHITE);
            int size = nmi.getSoufukosu();

            int appropriaterowsize = size * 8;
            if (appropriaterowsize > 30) {
                appropriaterowsize = 30;
            }

              ta.setRows(appropriaterowsize);

                    String dispid = "ForwardMessageProgresDialog";
                    DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg276"), ta,
                                  QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg), oya_frame);
                    Thread dprth = new Thread(dpr);
                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
            String datef = df.format(new Date(System.currentTimeMillis()));
            ta.append(resources.getString("qkey.msg.msg278") + cjp + resources.getString("qkey.msg.msg279") + "\n");
            ta.append(resources.getString("qkey.msg.msg280") + datef + "\n");

            StringBuilder mediumbuffer = new StringBuilder();
            String msg057 = resources.getString("qkey.msg.msg281");
            String msg058 = resources.getString("qkey.msg.msg282");
            String msg059 = resources.getString("qkey.msg.msg283");
            String msg060 = resources.getString("qkey.msg.msg284");

            String dest_name_with_suffix = cjp + LOCAL_STORE_SUFFIX;
            prepareLocalStoreTab(dest_name_with_suffix);
            JTable targetTable = (JTable) jtableins.get(dest_name_with_suffix);
            LocalMsgTable lmt = (LocalMsgTable)targetTable.getModel();            

            QBrowserUtil.copyMessageHeaders(nmi.getHeaderinfos(), message);

            for (int i = 0; i < size; i++) {

                    LocalMessageContainer lmc = new LocalMessageContainer();

                    long ts = System.currentTimeMillis();
                    QBrowserUtil.populateHeadersOfLocalMessageContainer(nmi.getHeaderinfos(), lmc);
                    Message to = copyMessage(message);
                    to.setJMSTimestamp(ts);
                    String msid = "Local_Message" + ts;
                    to.setJMSMessageID(msid);
                    LocalQueue lq = new LocalQueue(cjp);
                    to.setJMSDestination(lq);
                    lmc.setMessage(to);
                    lmc.setVreplyto(convertVendorDestinationToLocalDestination(lmc.getVreplyto()));

                    lmc.setVtimestamp(ts);
                    lmc.setVmsgid(msid);
                    lmc.setVdest(lq);
                    lmc.setDest_name_with_suffix(dest_name_with_suffix);

                File saved_message = localstore.localMessageToFile(session, lmc, mediumbuffer, dpr.getMessageDialog());
                LocalStoreManager.addMsgIndex(cjp, to.getJMSMessageID(), saved_message.getAbsolutePath());

                mediumbuffer.append(msg057 + " " + msid + msg058);
                mediumbuffer.append((i + 1) + msg059 + size + msg060 + "\n");
                //ta.append(mediumbuffer.toString());
                //ta.setCaretPosition(ta.getText().length());
                //mediumbuffer = new StringBuilder();

            }

            refreshLocalStoreMsgTableWithFileReloading(dest_name_with_suffix);
            

            ta.append(mediumbuffer.toString());
            ta.setCaretPosition(ta.getText().length());
            ta.append(resources.getString("qkey.msg.msg285"));


         }
    }

    void setUserPropertyInMessage(Message message) {
        ArrayList userprops = nmi.getUserproperties();
        for (int i = 0 ; i < userprops.size(); i++) {

            try {
                Property userprop = (Property) userprops.get(i);
                String key = userprop.getKey();
                switch (userprop.validated_type) {
                    case Property.INVALID_TYPE_INT:
                       //VALIDではない、セットスキップ
                       break;

                    case Property.STRING_TYPE_INT:
                       message.setStringProperty(key, userprop.getProperty_valueASString());
                       break;

                    case Property.BOOLEAN_TYPE_INT:
                       message.setBooleanProperty(key, userprop.getProperty_valueASBoolean());
                       break;

                    case Property.INT_TYPE_INT:
                       message.setIntProperty(key, userprop.getProperty_valueASInt());
                       break;

                    case Property.BYTE_TYPE_INT:
                       message.setByteProperty(key, userprop.getProperty_valueASByte());
                       break;

                    case Property.DOUBLE_TYPE_INT:
                       message.setDoubleProperty(key, userprop.getProperty_valueASDouble());
                       break;

                    case Property.FLOAT_TYPE_INT:
                       message.setFloatProperty(key, userprop.getProperty_valueASFloat());
                       break;

                    case Property.LONG_TYPE_INT:
                       message.setLongProperty(key, userprop.getProperty_valueASLong());
                       break;

                    case Property.SHORT_TYPE_INT:
                       message.setShortProperty(key, userprop.getProperty_valueASShort());
                       break;

                    default :
                       break;

                }

            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    String eliminateQuotes(String value) {
        //文の前後の'または"を排除
        if (value.startsWith("'") || value.startsWith("\"")) {
            value = value.substring(1);
        }

        if (value.endsWith("'") || value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }

        return value;
    }

    void setHeaderInfoInMessage(MessageProducer pro, Message message) {

        ArrayList jms_headers = nmi.getHeaderinfos();
        boolean isDestinationSpecified = false;
        Destination specifieddestination = null;


        for (int i = 0; i < jms_headers.size(); i++) {
            Property jms_header = (Property) jms_headers.get(i);
            String key = jms_header.getKey();

            try {

                if (key.equalsIgnoreCase("JMSDestination")) {
                    String dest = jms_header.getProperty_valueASString();
                    if (isTopic(dest) || dest.trim().toLowerCase().startsWith("topic://")) {
                        Destination ttdest = session.createTopic(getPureDestName(dest));
                        message.setJMSDestination(ttdest);

                    } else {
                        Destination ttdest = session.createQueue(getPureDestName(dest));
                        message.setJMSDestination(ttdest);
                    }
                } else if (key.equalsIgnoreCase("JMSTimestamp")) {
                } else if (key.equalsIgnoreCase("JMSRedelivered")) {
                } else if (key.equalsIgnoreCase("JMSExpiration")) {
                    message.setJMSExpiration(jms_header.getProperty_valueASInt());
                    pro.setTimeToLive(jms_header.getProperty_valueASInt());
                } else if (key.equalsIgnoreCase("JMSDeliverMode")) {
                    pro.setDeliveryMode(jms_header.getProperty_valueASInt());
                } else if (key.equalsIgnoreCase("JMSType")) {
                    message.setJMSType(jms_header.getProperty_valueASString());
                } else if (key.equalsIgnoreCase("JMSMessageID")) {
                } else if (key.equalsIgnoreCase("JMSCorrelationID")) {
                    message.setJMSCorrelationID(jms_header.getProperty_valueASString());
                } else if (key.equalsIgnoreCase("JMSReplyTo")) {

                    // : Topicがつくかtopic://が頭につくとトピック、それ以外はキュー
                    String reply_dest = jms_header.getProperty_valueASString();

                    if (isTopic(reply_dest) || reply_dest.trim().toLowerCase().startsWith("topic://")) {
                        Destination ttdest = session.createTopic(getPureDestName(reply_dest));
                        message.setJMSReplyTo(ttdest);

                    } else {
                        Destination ttdest = session.createQueue(getPureDestName(reply_dest));
                        message.setJMSReplyTo(ttdest);
                    }                    
                    

                } else if (key.equalsIgnoreCase("JMSPriority")) {

                    pro.setPriority(jms_header.getProperty_valueASInt());
   
                }

            } catch (Exception e) {
                //NOP
                //e.printStackTrace();
            }

        }
    }

    public void setMainDestComboBox(String selectitem) {
        qBox.setSelectedItem(selectitem);
    }

    void forwardMessage(ArrayList messages, String from_msg_table_with_suffix ,String forward_target_name, String forward_target_type, boolean deleteSrcMessageAfterForward, boolean showMessageDialog) throws Exception {
        //メッセージを転送する

        //FROMのパネルを判別する
        //int tabindex = tabbedPane.getSelectedIndex();
        //String from_msg_table_with_suffix = tabbedPane.getTitleAt(tabindex);
        JTable fromTable = (JTable) jtableins.get(from_msg_table_with_suffix);
            


        //宛先別に送りを作成
        if (forward_target_type.equals(QUEUE_LITERAL)) {
            //Queue
            javax.jms.Queue queue = session.createQueue(forward_target_name);
            MessageProducer sender = session.createProducer(queue);

            TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
            ta.setEditable(true);
            int size = messages.size();
            int appropriaterowsize = size + 2;
                if (appropriaterowsize > 15) {
                        appropriaterowsize = 14;
                }

             ta.setRows(appropriaterowsize);


            //Queue送信進捗情報
             if (showMessageDialog) {

                    String dispid = "ForwardMessageProgresDialog";
                    DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg053"), ta,
                                  QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg), oya_frame);
                    Thread dprth = new Thread(dpr);
                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }

             }
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
            String datef = df.format(new Date(System.currentTimeMillis()));
            ta.append(resources.getString("qkey.msg.msg054") + forward_target_name + resources.getString("qkey.msg.msg055") + "\n");
            ta.append(resources.getString("qkey.msg.msg056") + datef + "\n");

            StringBuilder mediumbuffer = new StringBuilder();
            String msg057 = resources.getString("qkey.msg.msg057");
            String msg058 = resources.getString("qkey.msg.msg058");
            String msg059 = resources.getString("qkey.msg.msg059");
            String msg060 = resources.getString("qkey.msg.msg060");

            ArrayList<Integer> del_targets = new ArrayList();
            ArrayList<Integer> del_tpc_targets = new ArrayList();

            for (int i = 0; i < size; i++) {
                //Fromが本物のメッセージか、ローカルでリプレイされたかによって挙動を変える

                Object mobj = messages.get(i);
                Message from_message = null;
                Message to_message = null;
                Destination from_destination = null;
                if (mobj instanceof com.qbrowser.localstore.LocalMessageContainer) {

                    LocalMessageContainer lmc = (LocalMessageContainer)mobj;
                    
                    from_message = lmc.getMessage();
                    if (from_message == null) {
                        //lazy load
                        from_message = lmc.getRealMessage(session);
                    }

                    //他のJMSからもってきた宛先はたぶんエラー。その場合はそのまま。
                    try {
                       convertAllLocalDestinationInMessageToVendorDestination(lmc);
                    } catch (Throwable convere) {
                        //NOP
                    }
                    MessageContainer tomc = copyMessageContainer(lmc);


                    setLocalMessageContainerHeadersToMessageProducer(lmc, sender);

                    Destination des1 = tomc.getVdest();
                    if (des1 != null) {
                        if (des1 instanceof Topic) {
                            Topic tdes1 = (Topic)des1;
                        }
                    }

                    to_message = tomc.getMessage();
                    sender.send(tomc.getMessage());

                    if (deleteSrcMessageAfterForward) {
                        //移動の場合は元ネタを消去する(失敗したときは、消されないだけ（暫定）
                        try {

                            String dt_msg = lmc.getVmsgid();
                            LocalMsgTable mt = (LocalMsgTable) fromTable.getModel();
                            int row_no = mt.getRealRowNoFromMsgId(dt_msg);
                            del_targets.add(new Integer(row_no));
                            //実ファイルはここで消す
                            lmc.deleteRealMessageFile();
                        } catch (Throwable thex) {
                            thex.printStackTrace();
                        }
                    }
                } else
                if (mobj instanceof MessageContainer) {

                    MessageContainer frommc = (MessageContainer) mobj;
                    
                    from_message = frommc.getMessage();
                    if (from_message == null) {
                        try {
                          Queue rq = getQueue(getPureDestName(getPureDestName(from_msg_table_with_suffix)));
                          from_message = frommc.getRealMessageFromBroker(session, rq);
                        } catch (Exception reale) {
                            reale.printStackTrace();
                        }
                    }
                    try {
                      convertAllLocalDestinationInMessageToVendorDestination(frommc);
                    } catch (Exception converte) {
                        //NOP
                    }

                    MessageContainer tomc = copyMessageContainer(frommc);

                    from_destination = tomc.getVdest();
                    tomc.setVdest(queue);

                    setHeadersToMessageProducer(from_message, sender);

                    to_message = tomc.getMessage();
                    sender.send(to_message);

                    if (deleteSrcMessageAfterForward) {
                        if (isQueue(from_msg_table_with_suffix)) {
                            //転送元：QUEUE

                            //移動の場合は元ネタを消去する(失敗したときは、消されないだけ（暫定）
                            try {
                                String dselector = "JMSMessageID ='" + from_message.getJMSMessageID() + "'";
                                MessageConsumer mc = session.createConsumer(frommc.getVdest() , dselector, false);
                                Message delm = mc.receive(3000L);
                                DeleteCleanup dcp = new DeleteCleanup();
                                dcp.imc = mc;
                                Thread th1 = new Thread(dcp);
                                th1.start();
                            } catch (Throwable thex) {
                                thex.printStackTrace();
                            }

                        } else if (isTopic(from_msg_table_with_suffix)) {
                            //転送元：TOPIC
                            try {
                                String dt_msg = from_message.getJMSMessageID();
                                MsgTable mt = (MsgTable) fromTable.getModel();
                                int row_no = mt.getRealRowNoFromMsgId(dt_msg);
                                del_tpc_targets.add(new Integer(row_no));
                            } catch (Throwable thex) {
                                thex.printStackTrace();
                            }
                        }
                    }

                }
                


                mediumbuffer.append(msg057 + " " + to_message.getJMSMessageID() + " " +  msg058);
                mediumbuffer.append((i + 1) + msg059 + size + msg060 + "\n");

            }

            sender.close();

            //TPCキャッシュテーブル中からの行削除
            if (del_targets.size() != 0) {
                deleteIndicatedRowsFromLocalMsgTable(from_msg_table_with_suffix , del_targets);
            } else if (del_tpc_targets.size() != 0) {
                deleteIndicatedRowsFromSubscriberCacheTable(from_msg_table_with_suffix , del_tpc_targets);
            }

            ta.append(mediumbuffer.toString());
            ta.setCaretPosition(ta.getText().length());
            ta.append(resources.getString("qkey.msg.msg061"));
            ifnotyetDestNameInQueueDisplayBoxThenAdd(forward_target_name);
            

        } else if (forward_target_type.equals(TOPIC_LITERAL)) {
            //Topic
            javax.jms.Topic topic = session.createTopic(forward_target_name);
            MessageProducer publisher = session.createProducer(topic);

            TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
            ta.setEditable(true);
            int size = messages.size();
            int appropriaterowsize = size + 2;
                if (appropriaterowsize > 15) {
                        appropriaterowsize = 15;
                }

             ta.setRows(appropriaterowsize);

            //Topic送信進捗情報

            StringBuffer sb = new StringBuffer();

            if(showMessageDialog) {

                    String dispid = "ForwardMessageProgresDialog";
                    DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg062"), ta,
                                  QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg), oya_frame);
                    Thread dprth = new Thread(dpr);
                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }

            }

            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
            String datef = df.format(new Date(System.currentTimeMillis()));
            ta.append(resources.getString("qkey.msg.msg063") + forward_target_name + resources.getString("qkey.msg.msg064") + "\n");
            ta.append(resources.getString("qkey.msg.msg065") + datef + "\n");
            //int caretcount = 0;
            StringBuilder mediumbuffer = new StringBuilder();

            String msg066 = resources.getString("qkey.msg.msg066");
            String msg067 = resources.getString("qkey.msg.msg067");
            String msg068 = resources.getString("qkey.msg.msg068");
            String msg069 = resources.getString("qkey.msg.msg069");
           

            ArrayList<Integer> del_targets = new ArrayList();
            ArrayList<Integer> del_tpc_targets = new ArrayList();

            for (int i = 0; i < size; i++) {
               Object mobj = messages.get(i);
                Message from_message = null;
                Message to_message   = null;
                Destination from_destination = null;
                //転送元がQueueかTopicの場合

                if (mobj instanceof com.qbrowser.localstore.LocalMessageContainer) {

                    LocalMessageContainer lmc = (LocalMessageContainer)mobj;
                    

                    from_message = lmc.getMessage();
                    if (from_message == null) {
                        from_message = lmc.getRealMessage(session);
                    }
                    try {
                      convertAllLocalDestinationInMessageToVendorDestination(lmc);
                    } catch (Exception converte) {
                        //NOP
                    }
                    MessageContainer tomc = copyMessageContainer(lmc);
                    setLocalMessageContainerHeadersToMessageProducer(lmc, publisher);

                    to_message = tomc.getMessage();
                    publisher.send(to_message);

                    if (deleteSrcMessageAfterForward) {
                        //移動の場合は元ネタを消去する(失敗したときは、消されないだけ（暫定）
                        try {

                            String dt_msg = lmc.getVmsgid();
                            LocalMsgTable mt = (LocalMsgTable) fromTable.getModel();
                            int row_no = mt.getRealRowNoFromMsgId(dt_msg);
                            del_targets.add(new Integer(row_no));
                            lmc.deleteRealMessageFile();
                        } catch (Throwable thex) {
                            thex.printStackTrace();
                        }
                    }
                } else
               if (mobj instanceof MessageContainer) {

                    MessageContainer frommc = (MessageContainer) mobj;
                    
                    from_message = frommc.getMessage();

                    if (from_message == null) {
                        try {
                          Queue rq = getQueue(getPureDestName(getPureDestName(from_msg_table_with_suffix)));
                          from_message = frommc.getRealMessageFromBroker(session, rq);
                        } catch (Exception reale) {
                            reale.printStackTrace();
                        }
                    }
                    try {
                      convertAllLocalDestinationInMessageToVendorDestination(frommc);
                    } catch (Exception converte) {
                        //NOP
                    }


                    MessageContainer tomc = copyMessageContainer(frommc);

                    from_destination = tomc.getVdest();
                    tomc.setVdest(topic);

                    setHeadersToMessageProducer(from_message, publisher);

                    to_message = tomc.getMessage();
                    publisher.send(to_message);

                    if (deleteSrcMessageAfterForward) {
                        if (isQueue(from_msg_table_with_suffix)) {
                            //転送元：QUEUE

                            //移動の場合は元ネタを消去する(失敗したときは、消されないだけ（暫定）
                            try {
                                String dselector = "JMSMessageID ='" + from_message.getJMSMessageID() + "'";
                                MessageConsumer mc = session.createConsumer(frommc.getVdest() , dselector, false);
                                Message delm = mc.receive(3000L);
                                DeleteCleanup dcp = new DeleteCleanup();
                                dcp.imc = mc;
                                Thread th1 = new Thread(dcp);
                                th1.start();
                            } catch (Throwable thex) {
                                thex.printStackTrace();
                            }

                        } else if (isTopic(from_msg_table_with_suffix)) {
                            //転送元：TOPIC
                            try {
                                String dt_msg = from_message.getJMSMessageID();
                                MsgTable mt = (MsgTable) fromTable.getModel();
                                int row_no = mt.getRealRowNoFromMsgId(dt_msg);
                                del_tpc_targets.add(new Integer(row_no));
                            } catch (Throwable thex) {
                                thex.printStackTrace();
                            }
                        }
                    }

                }

                mediumbuffer.append(msg066 + to_message.getJMSMessageID() + msg067);
                mediumbuffer.append((i + 1) + msg068 + size + msg069 + "\n");
            }

            publisher.close();
            
            //TPCキャッシュテーブル中からの行削除
            if (del_targets.size() != 0) {
                deleteIndicatedRowsFromLocalMsgTable(from_msg_table_with_suffix , del_targets);
            } else if (del_tpc_targets.size() != 0) {
                deleteIndicatedRowsFromSubscriberCacheTable(from_msg_table_with_suffix , del_tpc_targets);
            }


            ta.append(mediumbuffer.toString());
            ta.setCaretPosition(ta.getText().length());
            ta.append(resources.getString("qkey.msg.msg070"));
            ifnotyetDestNameInTopicDisplayBoxThenAdd(forward_target_name);
         } else if (forward_target_type.equals(LOCAL_STORE_LITERAL)) {
              //転送ターゲット変数：forward_target_name
              //宛先→ローカルストアへの転送とは、コピーのこと。
              //ただのコピーではなく、メモリ上のメッセージ→一旦ダウンロード
              //ターゲットローカルストアのフォルダへ物理コピー、
              //メモリ上のメッセージもコピーの手順となる。
              LocalStoreManager.LocalStore localstore = lsm.getLocalStoreInstance(forward_target_name);

              TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
              ta.setEditable(true);
              int size = messages.size();
              int appropriaterowsize = size + 8;
                if (appropriaterowsize > 25) {
                        appropriaterowsize = 25;
                }

              ta.setRows(appropriaterowsize);
              JDialog msgdl = null;

              if (showMessageDialog) {

                    String dispid = "ForwardMessageProgresDialog";
                    DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg276"), ta,
                                  QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg), oya_frame);
                    
                    Thread dprth = new Thread(dpr);
                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }
                    msgdl = dpr.getMessageDialog();
            } else {
                  msgdl = new JDialog();
            }
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
            String datef = df.format(new Date(System.currentTimeMillis()));
            ta.append(resources.getString("qkey.msg.msg278") + forward_target_name + resources.getString("qkey.msg.msg279") + "\n");
            ta.append(resources.getString("qkey.msg.msg280") + datef + "\n");

            StringBuilder mediumbuffer = new StringBuilder();
            String msg057 = resources.getString("qkey.msg.msg281");
            String msg058 = resources.getString("qkey.msg.msg282");
            String msg059 = resources.getString("qkey.msg.msg283");
            String msg060 = resources.getString("qkey.msg.msg284");

            
            String dest_name_with_suffix =  forward_target_name + LOCAL_STORE_SUFFIX;
            prepareLocalStoreTab(dest_name_with_suffix);
            JTable targetTable = (JTable) jtableins.get(dest_name_with_suffix);
            LocalMsgTable lmt = (LocalMsgTable)targetTable.getModel();

            ArrayList<Integer> del_targets = new ArrayList();
            ArrayList<Integer> del_tpc_targets = new ArrayList();

            for (int i = 0; i < size; i++) {
                Object mobj = messages.get(i);
                String jmsgid = null;
                Message from_message = null;
                Message to_message   = null;

                //転送元：ローカルストア 転送先：ローカルストア
                if (mobj instanceof com.qbrowser.localstore.LocalMessageContainer) {

                    LocalMessageContainer lmc = (LocalMessageContainer)mobj;

                    from_message = lmc.getMessage();
                    if (from_message == null) {
                        from_message = lmc.getRealMessage(session);
                    }
                    //ローカル→ローカルなので実宛先は必要なし
                    //convertAllLocalDestinationInMessageToVendorDestination(lmc);
                    to_message = copyMessage(from_message);

                    //宛先と送り元が同じ場合は、msgidを変更する
                    String msid = null;
                    long ts = -1;
                    if (dest_name_with_suffix.equals(from_msg_table_with_suffix)) {

                      ts = System.currentTimeMillis();
                      to_message.setJMSTimestamp(ts);
                      msid = "Local_Message" + ts;
                      to_message.setJMSMessageID(msid);

                    }

                    if (lmt != null) {
                      LocalMessageContainer newlmc = new LocalMessageContainer();
                      newlmc.setMessage(to_message);

                      QBrowserUtil.populateHeadersOfLocalMessageContainer(lmc, newlmc);
                      newlmc.setVdest(convertVendorDestinationToLocalDestination(newlmc.getVdest()));
                      newlmc.setVreplyto(convertVendorDestinationToLocalDestination(newlmc.getVreplyto()));
                      newlmc.setDest_name_with_suffix(dest_name_with_suffix);


                      if (msid != null) {
                        newlmc.setVmsgid(msid);
                      }
                      if (ts != -1) {
                        newlmc.setVtimestamp(ts);
                      }

                      lmt.add_one_row_ifexists_update(newlmc);
                      if  (msid != null) {

                          File saved_message = localstore.localMessageToFile(session, newlmc, mediumbuffer, msgdl);
                          LocalStoreManager.addMsgIndex(forward_target_name, newlmc.getVmsgid() , saved_message.getAbsolutePath());
                      } else {

                          File fromf = new File(lmc.getReal_file_path());
                          LocalStoreProperty lsp = lsm.getLocalStoreProperty(forward_target_name);
                          File tof = new File(lsp.getReal_file_directory() + fromf.getName());
                          QBrowserUtil.copy(fromf, tof);
                          LocalStoreManager.addMsgIndex(forward_target_name, newlmc.getVmsgid(), tof.getAbsolutePath());
                      }

                      jmsgid = newlmc.getVmsgid();
                      
                    }

                    //転送元：ローカルストアに対する転送後消し。
                    if (deleteSrcMessageAfterForward) {
                        //移動の場合は元ネタを消去する(失敗したときは、消されないだけ（暫定）
                        try {

                            String dt_msg = lmc.getVmsgid();
                            LocalMsgTable mt = (LocalMsgTable) fromTable.getModel();
                            int row_no = mt.getRealRowNoFromMsgId(dt_msg);
                            del_targets.add(new Integer(row_no));
                            //実ファイル削除
                            lmc.deleteRealMessageFile();
                        } catch (Throwable thex) {
                            thex.printStackTrace();
                        }
                    }
                } else
                //転送元：Queue
                //転送元：Topic
                if (mobj instanceof MessageContainer) {

                    MessageContainer frommc = (MessageContainer) mobj;
                    Message from_m = frommc.getMessage();
                    if (from_m == null) {
                        
                        try {
                          Queue rq = getQueue(getPureDestName(getPureDestName(from_msg_table_with_suffix)));
                          from_m = frommc.getRealMessageFromBroker(session,rq);
                        } catch (Exception reale) {
                            reale.printStackTrace();
                        }
                    }
                    LocalMessageContainer newlmc = new LocalMessageContainer();
                    QBrowserUtil.populateHeadersOfLocalMessageContainer(from_m , newlmc);
                    to_message = copyMessage(from_m);
                    newlmc.setMessage(to_message);
                    newlmc.setVdest(convertVendorDestinationToLocalDestination(newlmc.getVdest()));
                    newlmc.setVreplyto(convertVendorDestinationToLocalDestination(newlmc.getVreplyto() ));
                    newlmc.setDest_name_with_suffix(dest_name_with_suffix);

                    File saved_message = localstore.localMessageToFile(session, newlmc, mediumbuffer, msgdl);
                    LocalStoreManager.addMsgIndex(forward_target_name, from_m.getJMSMessageID() , saved_message.getAbsolutePath());

                    if (lmt != null) {
                      lmt.add_one_row_ifexists_update(newlmc);
                    }

                    jmsgid = newlmc.getVmsgid();

                    if (deleteSrcMessageAfterForward) {
                        if (isQueue(from_msg_table_with_suffix)) {
                            //転送元：QUEUE

                            //移動の場合は元ネタを消去する(失敗したときは、消されないだけ（暫定）
                            try {
                                String dselector = "JMSMessageID ='" + from_m.getJMSMessageID() + "'";
                                MessageConsumer mc = session.createConsumer(convertLocalDestinationToVendorDestination(frommc.getVdest()), dselector, false);
                                Message delm = mc.receive(3000L);
                                DeleteCleanup dcp = new DeleteCleanup();
                                dcp.imc = mc;
                                Thread th1 = new Thread(dcp);
                                th1.start();
                            } catch (Throwable thex) {
                                thex.printStackTrace();
                            }

                        } else if (isTopic(from_msg_table_with_suffix)) {
                            //転送元：TOPIC
                            try {
                                String dt_msg = from_m.getJMSMessageID();
                                MsgTable mt = (MsgTable) fromTable.getModel();
                                int row_no = mt.getRealRowNoFromMsgId(dt_msg);
                                del_tpc_targets.add(new Integer(row_no));
                            } catch (Throwable thex) {
                                thex.printStackTrace();
                            }
                        }
                    }



                }
                
                mediumbuffer.append(msg057 + " " + jmsgid + " " + msg058);
                mediumbuffer.append((i + 1) + msg059 + size + msg060 + "\n");

            }

            //TPCキャッシュテーブル中からの行削除
            if (del_targets.size() != 0) {
                deleteIndicatedRowsFromLocalMsgTable(from_msg_table_with_suffix , del_targets);
            } else if (del_tpc_targets.size() != 0) {
                deleteIndicatedRowsFromSubscriberCacheTable(from_msg_table_with_suffix , del_tpc_targets);
            }

            refreshLocalStoreMsgTableWithFileReloading(dest_name_with_suffix);

            ta.append(mediumbuffer.toString());
            ta.setCaretPosition(ta.getText().length());
            ta.append(resources.getString("qkey.msg.msg285"));         

         }
        ext_messages = null;
        
        refreshTableOnCurrentSelectedTab();
    }

    void refreshTableOnCurrentSelectedTab() {
        int selindex = tabbedPane.getSelectedIndex();
        String tab_title = tabbedPane.getTitleAt(selindex);
        if (isLocalStore(tab_title)) {
            remove_sub_button();
            set_localstore_buttons(tab_title);
            doBrowseLocalStore(selindex);
        } else if (!isTopic(tab_title)) {
            remove_sub_button();
            remove_localstore_buttons();
            doBrowse(selindex);
        } else {
            doBrowseSubscriberCache(selindex);
            remove_localstore_buttons();
            set_sub_button(tab_title);
        }
    }

    void showNewMessagePanelAsMessageCopy(LocalMessageContainer srcmsg) {
        Message imsg = srcmsg.getMessage();
        try {
            if (imsg == null) {
                imsg = srcmsg.getRealMessage(session);
            }
        } catch (Exception e) {
            popupErrorMessageDialog(e);
        }
        showNewMessagePanelAsMessageCopy((MessageContainer) srcmsg);
        header_table.load(srcmsg);
    }

    void showNewMessagePanelAsMessageCopy(MessageContainer srcmsg) {

        if (newmessageFrame != null) {
            newmessageFrame.dispose();
            newmessageFrame = null;
        }

        int tabindex = tabbedPane.getSelectedIndex();
        String from_msg_table_with_suffix = tabbedPane.getTitleAt(tabindex);

        cleanupNewMessagePanelObjects();

        Message imsg = srcmsg.getMessage();
        if (imsg == null) {
            try {
              Queue rq = getQueue(getPureDestName(getPureDestName(from_msg_table_with_suffix)));
              imsg = srcmsg.getRealMessageFromBroker(session, rq);
            } catch (Exception reale) {
                reale.printStackTrace();
            }
        }

            // Create popup
            newmessageFrame = new JFrame();
            newmessageFrame.setTitle(resources.getString("qkey.msg.msg023"));
            newmessageFrame.setBackground(Color.white);
            newmessageFrame.getContentPane().setLayout(new BorderLayout());
            newmessageFrame.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.NewMsg).getImage());

            newmessageFrame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    if (stringEditDialog != null) {
                        stringEditDialog.dispose();
                        stringEditDialog = null;
                    }
                }
            });

            JPanel northpanel = new JPanel();
            northpanel.setLayout(new BorderLayout());
            //宛先入力はコンボボックスに変更
            matesakiBox1 = new JComboBox();
            
            Dimension dm = matesakiBox1.getPreferredSize();
            dm.setSize(10 * dm.getWidth(), dm.getHeight());
            matesakiBox1.setPreferredSize(dm);
            matesakiBox1.setEditable(true);

            //ヘッダパネル
            header_table = new HeaderPropertyTable(1);
            hTable = new JTable(header_table);
            header_table.load(srcmsg);
            hTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            hTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());

            //TODO プロパティの多さによって最大200くらいまで動的拡張するようにする
            hTable.setPreferredScrollableViewportSize(new Dimension(500,65));

            DefaultCellEditor hdce = new DefaultCellEditor(getHeaderPropTypeComboBox());
            TableColumn hcolumn = hTable.getColumnModel().getColumn(0);
            hdce.setClickCountToStart(0);
            hcolumn.setCellEditor(hdce);

            hdce2 = new PropTableCellEditor();
            TableColumn hcolumn2 = hTable.getColumnModel().getColumn(1);
            hdce2.setClickCountToStart(0);
            hdce2.addCellEditorListener(new CellEditorListener() {

                public void editingStopped(ChangeEvent e) {
                    //今表の中にある全部の行をvalidateする

                    try {

                         //重複チェック用
                        HashSet keycheck = new HashSet();

                        for (int hi = 0; hi < header_table.getRowCount(); hi++) {
                            Property hpr = header_table.getPropertyAtRow(hi);
                            String key = hpr.getKey();
                            Object val = hpr.getProperty_value();

                            if (key != null) {
                                if (keycheck.contains(key)) {
                                    throw new QBrowserPropertyException("Q0020" + MAGIC_SEPARATOR + key + MAGIC_SEPARATOR + hpr.getProperty_type() + MAGIC_SEPARATOR + val);
                                } else {
                                    keycheck.add(key);
                                }
                            }


                            PropertyUtil.validateJMSHeaderValueType(key, val);

                        }
                        newmessage1stpanelok = true;
                    } catch (QBrowserPropertyException qpe) {

                        last_jmsheader_validate_error = qpe.getMessage();
                        newmessage1stpanelok = false;
                    }

                }

                public void editingCanceled(ChangeEvent e) {}
            });

            hcolumn2.setCellEditor(hdce2);

            JScrollPane htablePane = new JScrollPane(hTable);
            JPanel hp = new JPanel();
            hp.setLayout(new BorderLayout());

            //プロパティヘッダパネル
            JPanel header_header_container = new JPanel();
            header_header_container.setLayout(new BorderLayout());

            JLabel header_header_label = new JLabel(resources.getString("qkey.msg.msg024"));
            JPanel hbutton_container = new JPanel();
            JButton hplus_button = new JButton("+");
            hplus_button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    header_table.add_one_empty_row();
                }
            });

            JButton hminus_button = new JButton("-");
            hminus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    int sel_row = hTable.getSelectedRow();
                    if (sel_row < 0) sel_row = 0;
                    if (header_table.getRowCount() > 0)
                    header_table.deletePropertyAtRow(sel_row);
            }

            });
            hbutton_container.add(hplus_button);
            hbutton_container.add(hminus_button);

            header_header_container.setBorder(BorderFactory.createEtchedBorder());

            header_header_container.add(header_header_label, BorderLayout.CENTER);
            header_header_container.add(hbutton_container, BorderLayout.EAST);

            hp.setBorder(BorderFactory.createEtchedBorder());

            hp.add(header_header_container, BorderLayout.NORTH);
            hp.add(htablePane, BorderLayout.CENTER);


            northpanel.add(BorderLayout.SOUTH, hp);

            mqBox = new JComboBox();
            mqBox.addItemListener(new SendAtesakiComboBoxItemListener());
            Dimension d = mqBox.getPreferredSize();
            d.setSize(10 * d.getWidth(), d.getHeight());
            mqBox.setPreferredSize(d);
            mqBox.setEditable(false);

            DefaultComboBoxModel model = (DefaultComboBoxModel) mqBox.getModel();
            model.addElement(QUEUE_LITERAL);
            model.addElement(TOPIC_LITERAL);
            model.addElement(LOCAL_STORE_LITERAL);

            JLabel jl01 = new JLabel(resources.getString("qkey.msg.msg025"));
            northpanel.add(BorderLayout.WEST, jl01);
            northpanel.add(BorderLayout.EAST, mqBox);

            //宛先名入力エリア
            JPanel atesaki = new JPanel();
            atesaki.setLayout(new BorderLayout());
            JLabel jl02 = new JLabel(resources.getString("qkey.msg.msg026"));
            atesaki.add(BorderLayout.WEST, jl02);
            //データ入れ込み。デフォルトはQUEUE
            importQueueNamesToMATESAKIBOX1();
            matesakiBox1.setEditable(true);

            atesaki.add(BorderLayout.CENTER, matesakiBox1);
            northpanel.add(BorderLayout.NORTH, atesaki);

            newmessageFrame.getContentPane().add(BorderLayout.NORTH, northpanel);

            property_table = new PropertyInputTable(0);

            pTable = new JTable(property_table);
            pTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            pTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());
            property_table.load(srcmsg);
            for (int pii = 0 ; pii < property_table.getRowCount(); pii++) {
                property_table.setItemListenerInComboBoxAt(pii, new UserPropertyTypeComboBoxItemListener());
                property_table.setMouseListenerInTextAreaAt(pii, new UserPropertyStringPropertyMouseListener());
            }

            pTable.setRowHeight(20);

            pTable.setPreferredScrollableViewportSize(new Dimension(500,120));

            TableColumn column0 = pTable.getColumnModel().getColumn(0);
            pdce1 = new DefaultCellEditor(new JTextField());
            pdce1.setClickCountToStart(0);
            column0.setCellEditor(pdce1);

            TableColumn column = pTable.getColumnModel().getColumn(1);

            column.setPreferredWidth(10);
            ListCellEditor plce2 = new ListCellEditor();
            plce2.setClickCountToStart(0);
            column.setCellEditor(plce2);
            column.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

            pdce3 = new ListCellEditor();
            TableColumn pcolumn3 = pTable.getColumnModel().getColumn(2);
            pdce3.setClickCountToStart(0);
            pdce3.addCellEditorListener(new CellEditorListener() {

                public void editingStopped(ChangeEvent e) {
                    //今表の中にある全部の行をvalidateする

                    //重複チェック用
                    HashSet keycheck = new HashSet();

                    try {
                        for (int hi = 0; hi < property_table.getRowCount(); hi++) {
                            InputProperty hpr = property_table.getPropertyAtRow(hi);

                            if (hpr.getKey() != null) {
                            if (keycheck.contains(hpr.getKey())) {
                                throw new QBrowserPropertyException("Q0019" + MAGIC_SEPARATOR + hpr.getKey() + MAGIC_SEPARATOR + hpr.getProperty_type() +  MAGIC_SEPARATOR + hpr.getProperty_value());
                            } else {
                                //System.out.println("abc");
                                keycheck.add(hpr.getKey());
                            }
                            }

                            hpr.selfValidate();

                        }
                        newmessage1stpanel_user_props_ok = true;
                    } catch (QBrowserPropertyException qpe) {
                        last_user_prop_validate_error = qpe.getMessage();
                        newmessage1stpanel_user_props_ok = false;
                    }

                }

                public void editingCanceled(ChangeEvent e) {}
            });
            pcolumn3.setCellEditor(pdce3);
            pcolumn3.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

            JScrollPane tablePane = new JScrollPane(pTable);
            JPanel pp = new JPanel();
            pp.setLayout(new BorderLayout());

            //プロパティヘッダパネル
            JPanel prop_header_container = new JPanel();
            prop_header_container.setLayout(new BorderLayout());

            JLabel prop_header_label = new JLabel(resources.getString("qkey.msg.msg158"));
            JPanel button_container = new JPanel();
            JButton plus_button = new JButton("+");
            plus_button.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                        property_table.add_one_empty_row();
                        property_table.setItemListenerInComboBoxAt((property_table.getRowCount() - 1)
                                , new UserPropertyTypeComboBoxItemListener());
                }

            });

            JButton minus_button = new JButton("-");
            minus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    int sel_row = pTable.getSelectedRow();
                    if (sel_row < 0) sel_row = 0;
                    if (property_table.getRowCount() > 0)
                    property_table.deletePropertyAtRow(sel_row);
            }

            });
            button_container.add(plus_button);
            button_container.add(minus_button);

            prop_header_container.setBorder(BorderFactory.createEtchedBorder());

            prop_header_container.add(prop_header_label, BorderLayout.CENTER);
            prop_header_container.add(button_container, BorderLayout.EAST);

            pp.setBorder(BorderFactory.createEtchedBorder());

            pp.add(prop_header_container, BorderLayout.NORTH);
            pp.add(tablePane, BorderLayout.CENTER);

            newmessageFrame.getContentPane().add(BorderLayout.CENTER, pp);

            southpanel = new JPanel();

            mbodyPanel = new TextMessageInputBodyPanel();
            mbodyPanel.setTitle(resources.getString("qkey.msg.msg159"));
            southpanel.setLayout(new BorderLayout());

            southpanel.add(BorderLayout.CENTER, mbodyPanel);
            currentBodyPanel = mbodyPanel;

            //選択されたラジオボタンにしたがって
            //入力パネルが変更される
            JPanel txtorfilepanel = new JPanel();
            message_type = new JComboBox();
            message_type.setPreferredSize(new Dimension(120, 20));
            message_type.addItem(TEXTMESSAGE);
            message_type.addItem(BYTESMESSAGE);
            message_type.addItem(MAPMESSAGE);
            message_type.addItem(STREAMMESSAGE);
            message_type.addItem(MESSAGE);
            message_type.setSelectedIndex(0);
            message_type.addItemListener(new MessageTypeListener());


            JLabel jl03 = new JLabel(resources.getString("qkey.msg.msg028"));

            txtorfilepanel.add(jl03);
            txtorfilepanel.add(message_type);

            //Encoding
            penc = new JPanel();
            JLabel jlenc = new JLabel(resources.getString("qkey.msg.msg404"));
            penc.add(jlenc);
            encoding_type = new JComboBox();
            encoding_type.setPreferredSize(new Dimension(100, 20));
            encoding_type.setEditable(true);
            String default_encoding = resources.getString("qkey.msg.msg405");
            encoding_type.addItem(default_encoding);
            //encode_before = default_encoding;
            encoding_type.addItem("UTF8");
            //デフォルト
            //encoding_type.addItem("SJIS");
            encoding_type.addItem("ISO2022JP");
            encoding_type.addItem("EUCJP");
            encoding_type.addItem("UTF-16");
            encoding_type.addItemListener(new MessageEncodingTypeListener());
            penc.add(encoding_type);
            
            //DeliveryMode
            JPanel pdeliverymode = new JPanel();



            JLabel ldeliverymode = new JLabel(resources.getString("qkey.msg.msg154"));
            cdeliverymode = new JComboBox();
            cdeliverymode.addItem(resources.getString("qkey.msg.msg122"));
            cdeliverymode.addItem(resources.getString("qkey.msg.msg123"));
            cdeliverymode.setPreferredSize(new Dimension(110, 18));
            pdeliverymode.add(ldeliverymode);
            pdeliverymode.add(cdeliverymode);
            
            try {
              Message message = srcmsg.getMessage();
              int deliv_mode = message.getJMSDeliveryMode();
              message = null;

            if (deliv_mode == 1) {
                cdeliverymode.setSelectedIndex(1);
            }

            } catch (Exception nme) {
                //NOP
            }

            //CompressMode
            JPanel pcompressmode = new JPanel();

            JLabel lcompressmode = new JLabel(resources.getString("qkey.msg.msg155"));
            ccompressmode = new JComboBox();
            ccompressmode.addItem(resources.getString("qkey.msg.msg156"));
            ccompressmode.addItem(resources.getString("qkey.msg.msg157"));
            ccompressmode.setPreferredSize(new Dimension(110, 18));
            pcompressmode.add(lcompressmode);
            pcompressmode.add(ccompressmode);

            //メッセージ送付回数
            JPanel msgkosupanel = new JPanel();
            msgkosupanel.setLayout(new BorderLayout());
            JLabel jl08 = new JLabel(resources.getString("qkey.msg.msg029"));
            msgkosupanel.add(BorderLayout.CENTER, jl08);

            soufukosu = new JTextField(5);
            soufukosu.addCaretListener(new SoufukosuInputListener());
            soufukosu.setText("1");

            msgkosupanel.add(BorderLayout.EAST, soufukosu);

            messagesentakupanel = new JPanel();
            messagesentakupanel.setLayout(new BorderLayout());
            messagesentakupanel.add(BorderLayout.WEST, txtorfilepanel);
            messagesentakupanel.add(BorderLayout.CENTER,penc);
            messagesentakupanel.setBorder(BorderFactory.createEtchedBorder());
            JPanel modecontainer = new JPanel();

            JButton clearbutton = new JButton(resources.getString("qkey.msg.msg216"));
            clearbutton.addActionListener(new NewMessageClearListener());

            modecontainer.add(pdeliverymode);
            modecontainer.add(pcompressmode);
            modecontainer.add(clearbutton);

            messagesentakupanel.add(BorderLayout.EAST, msgkosupanel);

            southpanel.add(BorderLayout.NORTH, messagesentakupanel);

            okbutton = new JButton("            " + resources.getString("qkey.msg.msg125") + "           ");

            matesakiBox1.addItemListener(new AtesakiInputListener());
            okbutton.addActionListener(new NewMessageOKListener());

            JButton cancelbutton = new JButton(resources.getString("qkey.msg.msg030"));
            cancelbutton.addActionListener(new NewMessageCancelListener());

            JPanel buttonpanel = new JPanel();
            buttonpanel.setLayout(new BorderLayout());


            JPanel pbuttonpanel = new JPanel();
            
            pbuttonpanel.add(okbutton);
            pbuttonpanel.add(cancelbutton);

            buttonpanel.add(BorderLayout.EAST, pbuttonpanel);
            cmessagefooter = new JLabel();
            buttonpanel.add(BorderLayout.CENTER, cmessagefooter);

            JPanel bbcontainer = new JPanel();
            bbcontainer.setBorder(BorderFactory.createEtchedBorder());

            buttonpanel.setBorder(BorderFactory.createEtchedBorder());
            bbcontainer.setLayout(new BorderLayout());
            bbcontainer.add(BorderLayout.WEST, modecontainer);
            bbcontainer.add(BorderLayout.SOUTH, buttonpanel);

            southpanel.add(BorderLayout.SOUTH, bbcontainer);

            newmessageFrame.getContentPane().add(BorderLayout.SOUTH, southpanel);
            newmessageFrame.pack();


            
            //メッセージ種別ごとにBodyデータをローディングする
            if (imsg instanceof TextMessage) {
                message_type.setSelectedItem(TEXTMESSAGE);
                southpanel.remove(currentBodyPanel);
                try {
                mbodyPanel.textArea.setText(((TextMessage)imsg).getText());
                } catch (JMSException jmsinputex) {}
                southpanel.add(BorderLayout.CENTER, mbodyPanel);

                currentBodyPanel = mbodyPanel;
                southpanel.updateUI();
            } else if (imsg instanceof BytesMessage) {
                message_type.setSelectedItem(BYTESMESSAGE);
                southpanel.remove(currentBodyPanel);
                createBytesMessageBodyPanel();
                mfilepath.setText(resources.getString("qkey.msg.msg219"));
                passthrough_bytesmessage = (BytesMessage)imsg;
                southpanel.add(BorderLayout.CENTER, mfilebodyPanel);
                currentBodyPanel = mfilebodyPanel;
                southpanel.updateUI();
                
            } else if (imsg instanceof MapMessage) {
                message_type.setSelectedItem(MAPMESSAGE);
                southpanel.remove(currentBodyPanel);
                createMapMessageBodyPanel((MapMessage)imsg);
                southpanel.add(BorderLayout.CENTER, mapmBodyPanel);
                currentBodyPanel = mapmBodyPanel;
                southpanel.updateUI();
            } else if (imsg instanceof StreamMessage) {
                message_type.setSelectedItem(STREAMMESSAGE);
                southpanel.remove(currentBodyPanel);
                createStreamMessageBodyPanel((StreamMessage)imsg);
                southpanel.add(BorderLayout.CENTER, smBodyPanel);
                currentBodyPanel = smBodyPanel;
                southpanel.updateUI();
            } else if (imsg instanceof Message) {
                //Message
                message_type.setSelectedItem(MESSAGE);
                southpanel.remove(currentBodyPanel);
                JPanel plain_panel = new JPanel();
                JLabel message_label = new JLabel();
                message_label.setText(resources.getString("qkey.msg.msg244"));
                plain_panel.add(BorderLayout.CENTER ,message_label);
                southpanel.add(plain_panel);
                currentBodyPanel = plain_panel;
                southpanel.updateUI();
            }

        //今ブラウズモードで選択されているあて先名を補完する
        //ツリーペインが選択されている場合はそこ優先
        DestInfo dico = treePane.getSelectedDestInfo();
        if (dico != null)  {

            if (dico.destinationType.equals(TOPIC_LITERAL)) {
                mqBox.setSelectedItem(TOPIC_LITERAL);
            } else
            if (dico.destinationType.equals(QUEUE_LITERAL)) {
                mqBox.setSelectedItem(QUEUE_LITERAL);
            } else {
                mqBox.setSelectedItem(LOCAL_STORE_LITERAL);
            }

            matesakiBox1.setSelectedItem(dico.destinationName);

        } else {


        ComboBoxEditor editor = qBox.getEditor();
        String orig_name = (String) editor.getItem();
        String name = getPureDestName(orig_name);

            if (orig_name.indexOf(TOPIC_SUFFIX) != -1) {
                mqBox.setSelectedItem(TOPIC_LITERAL);
            } else if (orig_name.indexOf(QUEUE_SUFFIX) != -1) {
                mqBox.setSelectedItem(QUEUE_LITERAL);
            } else {
                mqBox.setSelectedItem(LOCAL_STORE_LITERAL);
            }

        matesakiBox1.setSelectedItem(name);

        }

        newmessageFrame.setLocationRelativeTo(oya);
        newmessageFrame.setVisible(true);
    }

    void refreshMsgTableWithDestName() {
        ComboBoxEditor editor = qBox.getEditor();
        String name = (String) editor.getItem();
        int selindex = tabbedPane.indexOfTab(name);
        //System.out.println("refreshMsgTableWithDestName : selindex :" + selindex);

        if (isLocalStore(name)) {

            if(isDestNameInDestNameComboBox(name)) {
              remove_sub_button();
              set_localstore_buttons(name);

            //タブが×で消されたなどで、見つからない。
            if (selindex == -1) {
                JTable cTable = (JTable) jtableins.get(name);
                JTable taihiTable = new JTable(new LocalMsgTable());
                if (cTable != null) {
                    localTableCopy(cTable, taihiTable);
                    selindex = createNewLocalMsgPane(name);
                    //今オブジェクト
                    cTable = (JTable) jtableins.get(name);
                    localTableCopy(taihiTable, cTable);
                    jtableins.put(name, cTable);
                } else {
                    //jtableinsに入ってない
                    selindex = createNewLocalMsgPane(name);
                    cTable = (JTable) jtableins.get(name);
                    LocalMsgTable mt = (LocalMsgTable)cTable.getModel();
                    mt.init();
                    //キャッシュにもない場合=完全にまっさらなのでlsmからデータを抽出する
                        LocalStoreProperty lsp = (LocalStoreProperty) lsm.getLocalStoreProperty(getPureDestName(name));
                        //ローカルストアの場所(DIR)をチェックして、メッセージファイルが含まれていたら復元

                        if (lsp.isValid()) {

                            String local_store_path = lsp.getReal_file_directory();
                            File td = new File(local_store_path);

                            if (td.exists()) {
                                processLocalStoreDir(getPureDestName(name), td, mt, new TextArea());
                            } else {
                                td.mkdirs();
                            }

                        }
                    //System.out.println("rn5");
                    //reNumberCTable(cTable);rn
                    //mt.fireTableDataChanged();
                }
                doBrowseLocalStore(selindex);

            }
            tabbedPane.setSelectedIndex(selindex);
         } else {
           //System.out.println("ないよ！");
           doBrowseWithEmptyData();
           setFooter(name + " " + resources.getString("qkey.msg.msg144"));
         }

        } else
        if (!isTopic(name)) {
            if(isDestNameInDestNameComboBox(name)) {
              remove_sub_button();
              remove_localstore_buttons();
              if (selindex == -1) {
                  //まだタブが一度も作成されていない（宛先のみに入っている）
                  doBrowse();
              } else {
                  doBrowse(selindex);
              }
            } else {
                setFooter(name + " " + resources.getString("qkey.msg.msg143"));
            }
        } else {
            //TOPICの場合は、一度でもsubscribeされていればqBoxにエントリがあるはず
            if(isDestNameInDestNameComboBox(name)) {
               remove_localstore_buttons();
            //タブが×で消されたなどで、見つからない。
            if (selindex == -1) {
                JTable cTable = (JTable) jtableins.get(name);
                JTable taihiTable = new JTable(new MsgTable());
                if (cTable != null) {
                    tableCopy(cTable, taihiTable);

                    selindex = createNewMsgPane(name);
                    //今オブジェクト
                    cTable = (JTable) jtableins.get(name);
                    tableCopy(taihiTable, cTable);
                    jtableins.put(name, cTable);
                }
                doBrowseSubscriberCache(selindex);
                //新しいテーブルを作っているので、
                //SubscriberThreadとテーブルのリンクが切れる
                //Threadは新タブと共に作り直し
                restartSubscriberThreadAlongWithCurrentStatus(name);
                /*
                boolean cu_status = isSubscriberThreadRunning(name);
                stopSubscriberThread(name);
                //今現在購読中の場合のみスレッドを再作成
                if (cu_status)
                createAndStartSubscriberThread(name);
                */

            }
            set_sub_button(name);
            tabbedPane.setSelectedIndex(selindex);
         } else {
           //System.out.println("ないよ！");
           doBrowseWithEmptyData();
           setFooter(name + " " + resources.getString("qkey.msg.msg144"));
         }
        }

    }

    boolean isDestNameTopic(String dispDest) {
        if (dispDest.indexOf(TOPIC_SUFFIX) != -1) {
            return true;
        } else {
            return false;
        }
    }

    public void convertAllLocalDestinationInMessageToVendorDestination (MessageContainer srcmsg) throws Exception {

            srcmsg.setVdest(convertLocalDestinationToVendorDestination(srcmsg.getVdest()));
            srcmsg.setVreplyto(convertLocalDestinationToVendorDestination(srcmsg.getVreplyto()));
            Message imsg = srcmsg.getMessage();
            
            imsg.setJMSDestination(srcmsg.getVdest());
            imsg.setJMSReplyTo(srcmsg.getVreplyto());

    }

    void cleanupQBrowser() {

        qBox.removeItemListener(acbil);
        qBox.removeAllItems();
        qBox.addItemListener(acbil);
        
        tabbedPane.removeAll();
        
        try {
            
        if (session != null)
            session.close();

        if (connection != null)
            connection.close();

        } catch (Throwable thex) {
            System.err.println(thex.getMessage());
        }
       

    }

    void connect() throws JMSException {
       
            setFooter("Connecting to " + serverHost + ":" +
                    serverPort + "...");

            cleanupQBrowser();

            initJMS();

            try {
                initDestListConsumer();
            } catch (JMSException e) {
                // If we can't subscribe to the mq.metrics topic then we
                // are probably not running against an EE broker. That's
                // OK. It just means we can't populate the Destination
                // combo-box on the GUI.

                if (e.getMessage().indexOf("[C4077]") != -1) {
                    isBrokerEE = false;
                }
            }
            connection.start();
            setFooter(resources.getString("qkey.msg.msg245") + " " + serverHost + ":" + serverPort);
            
        
    }


    class ReconnectRunner implements Runnable {

        public Thread direct_parent;

        public ReconnectRunner() {
            reconnect_runner_started = true;
        }
        

        public void run() {
        System.out.println(resources.getString("qkey.msg.msg071"));

        try {
        if (session != null) {
            session.close();
            session = null;
        }

        if (connection != null) {

            connection.close();
            connection = null;

        }

        } catch (JMSException jmse) {
            System.err.println(jmse.getMessage());
        }


        setFooter(resources.getString("qkey.msg.msg145") + " " + serverHost + ":" +
                serverPort);

        try {
            initJMS();
            connection.start();
            System.out.println(resources.getString("qkey.msg.msg072"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            try {

                setFooter(resources.getString("qkey.msg.msg146"));
                direct_parent.currentThread().sleep(5000);
                run();

            } catch (Exception ie) {
            }
        }
        setFooter(resources.getString("qkey.msg.msg147") + " " + serverHost + ":" + serverPort);
        }

    }

    String convertFullDestName(String orig, String target_type) {
            if (target_type.equals(QUEUE_LITERAL)) {
                return orig + QUEUE_SUFFIX;
            } else if (target_type.equals(TOPIC_LITERAL)) {
                return orig + TOPIC_SUFFIX;
            } else {
                return orig + LOCAL_STORE_SUFFIX;
            }
    }

    public void removeNamedTabbedPane(String dest_with_suffix) {
        int index = tabbedPane.indexOfTab(dest_with_suffix);
        if (index != -1) {
            tabbedPane.remove(index);
        }
    }

    public void removeDestRelatedCache(String dest_with_suffix) {
        try {
          removeNamedTabbedPane(dest_with_suffix);
          jtableins.remove(dest_with_suffix);
          qBox.removeItem(dest_with_suffix);

          if (isQueue(dest_with_suffix)) {
              destinationNamesForDisplayQueue.remove(getPureDestName(dest_with_suffix));
          } else if (isTopic(dest_with_suffix)) {
              destinationNamesForDisplayTopic.remove(getPureDestName(dest_with_suffix));
          }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void reconnect() throws JMSException {


        System.out.println(resources.getString("qkey.msg.msg071"));
        if (session != null) {
            session.close();
            session = null;
        }

        if (connection != null) {

            connection.close();
            connection = null;

        }


        setFooter(resources.getString("qkey.msg.msg145") + " " + serverHost + ":" +
                serverPort);

        try {
            initJMS();
            connection.start();
            System.out.println(resources.getString("qkey.msg.msg072"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            try {
                
                setFooter(resources.getString("qkey.msg.msg146"));

                if (!reconnect_runner_started) {
                  
                  Thread recrth = null;
                  ReconnectRunner recr = new ReconnectRunner();
                  recrth = new Thread(recr);
                  recr.direct_parent = recrth;
                  recrth.start();
                }

            } catch (Exception ie) {
            }
        }
        setFooter(resources.getString("qkey.msg.msg147") + " " + serverHost + ":" + serverPort);
    }

    boolean isLocalStore(String title) {
        if ((title == null) || (title.length() == 0)) {
            return false;
        }

        if (title.indexOf(LOCAL_STORE_SUFFIX) != -1) {
            return true;
        } else {
            return false;
        }
    }


   boolean isTopic(String title) {
        if ((title == null) || (title.length() == 0)) {
            return false;
        }

        if (title.toLowerCase().indexOf(QBrowserUtil.TOPIC_SUFFIX_LOWER) != -1) {
            return true;
        } else {
            return false;
        }
    }

   boolean isQueue(String title) {
        if ((title == null) || (title.length() == 0)) {
            return false;
        }

        if (title.toLowerCase().indexOf(QBrowserUtil.QUEUE_SUFFIX_LOWER) != -1) {
            return true;
        } else {
            return false;
        }
    }

    void checkAndchangeSelectAllMenuText() {

        int tabindex = tabbedPane.getSelectedIndex();
        if (tabindex != -1) {
            String tkey = tabbedPane.getTitleAt(tabindex);
            JTable cTable = (JTable) jtableins.get(tkey);

            if ((cTable.getSelectedRows().length == cTable.getRowCount()) &&
                    (cTable.getRowCount() != 0)) {
                String deselstring = resources.getString("qkey.menu.item.deselectall");
                selectall_item.setText(deselstring);
                select_all_button.setToolTipText(deselstring);
            } else {
                String selstring = resources.getString("qkey.menu.item.selectall");
                selectall_item.setText(selstring);
                select_all_button.setToolTipText(selstring);
            }

        }
    }

    boolean isNamedTabAlreadyCreated(String name) {
        int index = tabbedPane.indexOfTab(name);
        if (index > -1) {
            return true;
        } else {
            return false;
        }
    }

    public void doBrowseWithEmptyData() {

        ComboBoxEditor editor = qBox.getEditor();

        String name = complementDestName((String) editor.getItem());
        setFooter(name + resources.getString("qkey.msg.msg073"));

        int current_tab_index = 0;

        if (!isNamedTabAlreadyCreated(name)) {

            current_tab_index = createNewMsgPane(name);

        } else {
            current_tab_index = tabbedPane.indexOfTab(name);
            tabbedPane.setSelectedIndex(current_tab_index);
        }

        JTable cTable = (JTable) jtableins.get(name);
        name = getPureDestName(name);

            // Load messages into table
            MsgTable mt = (MsgTable) cTable.getModel();
            ArrayList tc = new ArrayList();
            int n = mt.load(tc);

        reNumberCTable(cTable);

    }

    /**
     * Browse the queue
     */
    public void doBrowse() {

        ComboBoxEditor editor = qBox.getEditor();

        String name = complementDestName((String) editor.getItem());
        setFooter(name + resources.getString("qkey.msg.msg073"));

        int current_tab_index = 0;

        if (!isNamedTabAlreadyCreated(name)) {
            
            current_tab_index = createNewMsgPane(name);

        } else {
            current_tab_index = tabbedPane.indexOfTab(name);
            tabbedPane.setSelectedIndex(current_tab_index);
        }

        JTable cTable = (JTable) jtableins.get(name);
        String fname = new String(name);
        name = getPureDestName(name);
        QueueBrowser qb = null;
        // Browse queue
        try {
            //String selector = null;
            Queue q = session.createQueue(name);

            
            if (selector == null) {
                qb = session.createBrowser(q);
            } else {
                qb = session.createBrowser(q, selector);
                //セレクタ文字列は検索ごとにリセット
                selector = null;
            }
            // Load messages into table
            MsgTable mt = (MsgTable) cTable.getModel();
            Enumeration emt = qb.getEnumeration();
            ArrayList tc = new ArrayList();
            while (emt.hasMoreElements()) {
                Message imsg = (Message)emt.nextElement();
                MessageContainer mc = new MessageContainer();
                //mc.setMessage(imsg);
                mc.setMessageFromBrokerWithLazyLoad(imsg);
                mc.setDest_name_with_suffix(fname);
                

                try {
                 mc.setVdest(convertVendorDestinationToLocalDestination(imsg.getJMSDestination()));
                 mc.setVreplyto(convertVendorDestinationToLocalDestination(imsg.getJMSReplyTo()));
                } catch (Exception mce) { mce.printStackTrace();}

                tc.add(mc);
                imsg = null;
            }
            int n = mt.load(tc);

            setFooter(name + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(n) + " " + resources.getString("qkey.msg.msg075"));
            qb.close();
        } catch (JMSException ex) {
            setFooter(ex.getMessage());
            //ブローカの再起動時などセッション切れの場合は自動リコネクト
            if ((ex.getMessage().indexOf("C4059") != -1) ||
                (ex.getMessage().indexOf("C4056") != -1) ||
                (session == null)) {
                try {
                    //System.out.println(ex.getMessage());
                    //System.out.println("trying to reconnect...");
                    reconnect();
                    doBrowse();
                } catch (Exception recex) {
                    setFooter(recex.getMessage());
                }
            }
        } finally {
            if (qb != null) {
                try {
                qb.close();
                } catch (JMSException jmes) {}
                qb = null;
                System.gc();
            }
        }
        reNumberCTable(cTable);

    }

    void reNumberLocalCTable(JTable cTable) {

        LocalMsgTable mt = (LocalMsgTable) cTable.getModel();

        for (int i = 0 ; i < cTable.getRowCount(); i++) {
            //実際のモデルをポイント
            int model_index = cTable.convertRowIndexToModel(i);
            mt.setDisplayNumberAt(i+1, model_index);

        }

        mt.fireTableDataChanged();
        cTable.updateUI();
    }

    void reNumberCTable(JTable cTable) {

        MsgTable mt = (MsgTable) cTable.getModel();

        for (int i = 0 ; i < cTable.getRowCount(); i++) {
            //実際のモデルをポイント
            int model_index = cTable.convertRowIndexToModel(i);
            mt.setDisplayNumberAt(i+1, model_index);
            
        }

        mt.fireTableDataChanged();
        cTable.updateUI();
    }


    /**
     * Browse the queue
     */
    public void doBrowse(int tabindex) {
        //System.out.println("tabindex : " + tabindex);

        //-1の時は出さない
        if (tabindex != -1) {

            String dispName = tabbedPane.getTitleAt(tabindex);
            String name = getPureDestName(dispName);
            setFooter(name + resources.getString("qkey.msg.msg073"));

            JTable cTable = (JTable) jtableins.get(dispName);

            QueueBrowser qb = null;
            // Browse queue
            try {
                //System.out.println("zzz" + selector);
                Queue q = session.createQueue(name);
                
                if (selector == null) {
                    qb = session.createBrowser(q);
                } else {
                    qb = session.createBrowser(q, selector);
                    //セレクタ文字列は検索ごとにリセット
                    selector = null;
                }
                // Load messages into table
                MsgTable mt = (MsgTable) cTable.getModel();
                ArrayList tc = new ArrayList();
                Enumeration emt = qb.getEnumeration();
            while (emt.hasMoreElements()) {
                Message imsg = (Message)emt.nextElement();
                MessageContainer mc = new MessageContainer();
                //mc.setMessage(imsg);
                mc.setMessageFromBrokerWithLazyLoad(imsg);
                mc.setDest_name_with_suffix(dispName);
                

                try {
                 mc.setVdest(convertVendorDestinationToLocalDestination(imsg.getJMSDestination()));
                 mc.setVreplyto(convertVendorDestinationToLocalDestination(imsg.getJMSReplyTo()));
                } catch (Exception mce) { mce.printStackTrace();}

                tc.add(mc);
                imsg = null;
            }
                int n = mt.load(tc);

                tabbedPane.setSelectedIndex(tabindex);
                setFooter(name + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(n) + " " + resources.getString("qkey.msg.msg075"));
                qb.close();
            } catch (Exception ex) {
                setFooter(ex.getMessage());
                //ブローカの再起動時などセッション切れの場合は自動リコネクト
            if ((ex.getMessage().indexOf("C4059") != -1) ||
                (ex.getMessage().indexOf("C4056") != -1) ||
                (session == null)) {
                    try {
                        reconnect();
                        doBrowse();
                    } catch (Exception recex) {
                        setFooter("Exception : " + recex.getMessage());
                    }
                }

            } finally {
            if (qb != null) {
                try {
                qb.close();
                } catch (JMSException jmes) {}
                qb = null;
                System.gc();
            }
        }
            reNumberCTable(cTable);
        }

    }

    public void doBrowseLocalStore(int tabindex) {

        if (tabindex != -1) {

            String dispName = tabbedPane.getTitleAt(tabindex);
            String name = getPureDestName(dispName);
            setFooter(name + resources.getString("qkey.msg.msg073"));

            JTable cTable = (JTable) jtableins.get(dispName);

            if (cTable == null) {
                //宛先一覧ボックスには名前があるが（スタート時に追加されたなどで）
                //まだ実体が読み込まれていない場合
                //LocalStoreManagerからデータを新規で読み込んでくる。
                prepareLocalStoreTab(dispName);
            }

            reNumberLocalCTable(cTable);

            setLocalStoreFooterMessages(name);

        }

    }

    public void setLocalStoreFooterMessages(String local_store_name_without_suffix) {
            String local_store_dir = "";
            LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_store_name_without_suffix);
            String status = resources.getString("qkey.msg.msg292");
            if (lsp != null) {
                local_store_dir = QBrowserUtil.eliminateEndFileSeparator(lsp.getReal_file_directory());
                if (!lsp.isValid()) {
                    status = resources.getString("qkey.msg.msg293");
                }
            }

            if(localstorelabel == null) {
              localstorelabel = new JLabel();
            }

            JTable cTable = (JTable) jtableins.get(local_store_name_without_suffix + QBrowserV2.LOCAL_STORE_SUFFIX);

            localstorelabel.setText(resources.getString("qkey.msg.msg277") + local_store_dir);
            if (cTable != null) {
              setFooter(local_store_name_without_suffix + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(cTable.getRowCount()) + " " + resources.getString("qkey.msg.msg266") + " / " + resources.getString("qkey.msg.msg291") + status);
            }
            footerPanel.updateUI();
            treePane.getTreeView().setPreferredSize(new Dimension(180, 490));
            //下のほうに表示されていたら、少し上にスクロールさせる
            TreePath tp = treePane.getTree().getSelectionPath();
        if (tp != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
            Object nodeInfo = node.getUserObject();
            TreeIconPanel.DestInfo di = (TreeIconPanel.DestInfo) nodeInfo;
            double pointY = treePane.getTree().getPathBounds(tp).getY();
            //System.out.println("pointY : " + pointY);
            if (pointY > 460.0) {
                int row_count = treePane.getTree().getRowCount();
                int current_row = treePane.getTree().getRowForPath(tp);

                if (row_count > current_row + 1) {
                    //スクロールの余地あり。
                    treePane.getTree().scrollRowToVisible(current_row + 1);
                    treePane.getTreeView().getViewport().scrollRectToVisible(treePane.getTree().getRowBounds(current_row + 1));
                }
            }
        }
    }

    public void doBrowseSubscriberCache(int tabindex) {


        if (tabindex != -1) {

            String dispName = tabbedPane.getTitleAt(tabindex);
            String name = getPureDestName(dispName);
            setFooter(name + resources.getString("qkey.msg.msg073"));

            JTable cTable = (JTable) jtableins.get(dispName);

            //サブスクライブ状態管理
            Boolean isRunning = (Boolean) subscribe_thread_status.get(dispName);
            String state_string = null;
            if ((isRunning != null) && isRunning.booleanValue()) {
                state_string = resources.getString("qkey.msg.msg136");
                //footer
                try {
                if (hasComponent(qbuttonpanel, subscribe_resume_button))
                qbuttonpanel.remove(subscribe_resume_button);
                qbuttonpanel.add(BorderLayout.WEST, unsubscribe_button);
                } catch (Throwable thex) { thex.printStackTrace();}
            } else {
                state_string = resources.getString("qkey.msg.msg137");
                try {
                if (hasComponent(qbuttonpanel, unsubscribe_button))
                qbuttonpanel.remove(unsubscribe_button);
                qbuttonpanel.add(BorderLayout.WEST, subscribe_resume_button);
                } catch (Throwable thex) { thex.printStackTrace();}
            }

            setFooter(name + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(cTable.getRowCount()) + " " + state_string);
        }

    }

    public void doBrowseSubscriberCacheAndCreatePanel(String dispName) {

        String name = getPureDestName(dispName);
        setFooter(name + resources.getString("qkey.msg.msg073"));

        JTable cTable = (JTable) jtableins.get(dispName);

        //サブスクライブ状態管理
        Boolean isRunning = (Boolean) subscribe_thread_status.get(dispName);
        String state_string = null;
        if ((isRunning != null) && isRunning.booleanValue()) {
            state_string = resources.getString("qkey.msg.msg136");
            //footer

            if (!hasComponent(qbuttonpanel, unsubscribe_button))
            qbuttonpanel.add(BorderLayout.WEST, unsubscribe_button);
        } else {
            state_string = resources.getString("qkey.msg.msg137");

            if (!hasComponent(qbuttonpanel, subscribe_resume_button))
            qbuttonpanel.add(BorderLayout.WEST, subscribe_resume_button);
        }

        setFooter(name + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(cTable.getRowCount()) + " " + state_string);
    }

    public void refreshLocalStoreMsgTableWithFileReloading(String lsn_with_suffix) {

        if (lsn_with_suffix == null) {
            return;
        }

        if (!lsn_with_suffix.endsWith(LOCAL_STORE_SUFFIX)) {
            return;
        }

                JTable cTable = (JTable) jtableins.get(lsn_with_suffix);

                if (cTable == null) {
                    prepareLocalStoreTab(lsn_with_suffix);
                }


                LocalMsgTable imt = (LocalMsgTable)cTable.getModel();
                imt.init();
                String puredestname = getPureDestName(lsn_with_suffix);
                LocalStoreProperty lsp = lsm.getLocalStoreProperty(puredestname);

                if (lsp.isValid()) {

                     String local_store_path = lsp.getReal_file_directory();
                     File td = new File(local_store_path);

                     if (td.exists()) {
                       processLocalStoreDir(puredestname, td , imt, new TextArea());
                     }

                }

                imt.fireTableDataChanged();
                reNumberLocalCTable(cTable);
                //setFooter(puredestname + " " + resources.getString("qkey.msg.msg074") + " " + String.valueOf(cTable.getRowCount()) + " " + resources.getString("qkey.msg.msg266"));
                refreshTableOnCurrentSelectedTab();
    }

    void deleteIndicatedRowsFromLocalMsgTable(String target_localstore_with_suffix,ArrayList delete_from_ls_rows) {
        int[] intarray = new int[delete_from_ls_rows.size()];
        for (int i = 0; i < delete_from_ls_rows.size(); i++) {
            intarray[i] = ((Integer)delete_from_ls_rows.get(i)).intValue();
        }

        deleteIndicatedRowsFromLocalMsgTable(target_localstore_with_suffix, intarray);
    }

    void deleteIndicatedRowsFromSubscriberCacheTable(String target_topic_with_suffix, ArrayList delete_from_cache_rows) {
        int[] intarray = new int[delete_from_cache_rows.size()];
        for (int i = 0; i < delete_from_cache_rows.size(); i++) {
            intarray[i] = ((Integer)delete_from_cache_rows.get(i)).intValue();
        }

        deleteIndicatedRowsFromSubscriberCacheTable(target_topic_with_suffix, intarray);
    }


    void deleteIndicatedRowsFromSubscriberCacheTable(String target_topic_with_suffix, int[] delete_from_cache_rows) {

                    JTable cTable = (JTable) jtableins.get(target_topic_with_suffix);
                    JTable taihiTable = new JTable(new MsgTable());

                    MsgTable mt = (MsgTable) cTable.getModel();
                    HashSet deletekey = new HashSet();

                    for (int i = 0; i < delete_from_cache_rows.length; i++) {
                        try {
                            //caretcount++;
                            int tgt = delete_from_cache_rows[i];
                            deletekey.add(new Integer(tgt));
                            MessageContainer msg = mt.getMessageAtRow(tgt);

                            String msgid = msg.getVmsgid();


                        } catch (Exception ee) {

                            popupErrorMessageDialog(ee);

                        }
                    } //roop end

                    //like jvm gc...
                    tableCopyWithoutIndicatedRows(cTable, taihiTable, deletekey);
                    tableCopy(taihiTable, cTable);

                    int tabindex = tabbedPane.indexOfTab(target_topic_with_suffix);
                    doBrowseSubscriberCache(tabindex);
        
    }


    void deleteIndicatedRowsFromLocalMsgTable(String target_localstore_with_suffix,int[] delete_from_ls_rows) {

                    JTable cTable = (JTable) jtableins.get(target_localstore_with_suffix);
                    JTable taihiTable = new JTable(new LocalMsgTable());

                    LocalMsgTable mt = (LocalMsgTable) cTable.getModel();
                    HashSet deletekey = new HashSet();

                    for (int i = 0; i < delete_from_ls_rows.length; i++) {
                        try {
                            //caretcount++;
                            int tgt = delete_from_ls_rows[i];
                            deletekey.add(new Integer(tgt));
                            LocalMessageContainer msg = mt.getMessageAtRow(tgt);

                            String msgid = msg.getVmsgid();
                             //ここで実ファイルも削除する
                            mt.msgids.remove(msgid);
                            //lsm.deleteLocalStoreData(msgid, getPureDestName(target_localstore_with_suffix));
                            msg.deleteRealMessageFile();

                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    } //roop end

                    //like jvm gc...
                    localTableCopyWithoutIndicatedRows(cTable, taihiTable, deletekey);
                    localTableCopy(taihiTable, cTable);

                    LocalMsgTable mta = (LocalMsgTable) cTable.getModel();
                    mta.fireTableDataChanged();
    }

      public String getStateOfDestination(String destType, String targetname) {

            if (destType.equals(QUEUE_LITERAL)) {
                destType = "q";
            } else if (destType.equals(TOPIC_LITERAL)) {
                destType = "t";
            }

            String retval = null;
            String cmd = "query dst -t " + destType + " -n " + targetname + " -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

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
            retval = cmdRunner.getStateOfSpecifiedDestination();

            return retval;
        }

    private String retrieveBrokerInstanceName() {
            String retval = null;
            String cmd = "query bkr -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            retval = cmdRunner.retrieveInstanceName();

            return retval;
    }

    /**
     * Add a name to the "Queue Name" combo box menu
     */
    void addDestToMenu(String name) {

        DefaultComboBoxModel model = (DefaultComboBoxModel) qBox.getModel();
        if (model.getIndexOf(name) < 0) {
            //Name is not in menu. Add it.
            model.addElement(name);
        }
    }

    void configMRTable(JTable mrTable) {
                                mrTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                                mrTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());
                                TableColumn column0 = mrTable.getColumnModel().getColumn(0);
                                column0.setPreferredWidth(200);
                                column0.sizeWidthToFit();
                                HeaderRenderer01 hr = new HeaderRenderer01();
                                column0.setHeaderRenderer(hr);

                                TableColumn column1 = mrTable.getColumnModel().getColumn(1);
                                column1.setPreferredWidth(120);
                                column1.setHeaderRenderer(hr);

                                TableColumn column2 = mrTable.getColumnModel().getColumn(2);
                                column2.setPreferredWidth(80);
                                column2.setHeaderRenderer(hr);
                                column2.setCellRenderer(new com.qbrowser.render.ListCellRenderer2());

                                TableColumn column3 = mrTable.getColumnModel().getColumn(3);
                                column3.setPreferredWidth(100);
                                column3.setHeaderRenderer(hr);
                                column3.setCellRenderer(new com.qbrowser.render.ListCellRenderer());
                                com.qbrowser.editor.ListCellEditor lce = new com.qbrowser.editor.ListCellEditor();
                                lce.setClickCountToStart(0);
                                column3.setCellEditor(lce);
                                mrTable.setPreferredScrollableViewportSize(new Dimension(500,120));
    }

    int createNewMsgPane(String name) {

      try {
        MsgTable tm = new MsgTable();
        final JTable cTable = new JTable(tm);
        cTable.setDragEnabled(true);
        
        

        RowSorter.SortKey sk = new RowSorter.SortKey(2, SortOrder.ASCENDING);
        ArrayList keys = new ArrayList();
        keys.add(sk);

        TableRowSorter trs = new TableRowSorter(tm);
        trs.setSortKeys(keys);

        cTable.setRowSorter(trs);



        cTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                //System.out.println("mousePressed called.");
                if (SwingUtilities.isLeftMouseButton(e)) {
                    //int selrow = cTable.getSelectedRow();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    int selrow = cTable.getSelectedRow();

                    //複数行一括転送対応
                    if (selrow != -1) {

                        last_right_click_X = e.getX();
                        last_right_click_Y = e.getY();
                        popupMenuX.remove(copymsgItem2);
                        popupMenuX.remove(pastemsgItem);

                        popupMenuX.add(copymsgItem2);
                        if (cbm.hasClipBoardValidData()) {
                            popupMenuX.add(pastemsgItem);
                        }

                        popupMenuX.show(e.getComponent(), last_right_click_X, last_right_click_Y);

                    } else {

                        popupMenuX2.remove(pastemsgItem);
                        if (cbm.hasClipBoardValidData()) {
                            popupMenuX2.add(pastemsgItem);
                        }
                            last_right_click_X = e.getX();
                            last_right_click_Y = e.getY();
                            //System.out.println("X=" + last_right_click_X + " Y=" + last_right_click_Y + cbm.getClipBoardData());
                            popupMenuX2.show(e.getComponent(), last_right_click_X, last_right_click_Y);
                        

                    }

                }
            }
        });


        cTable.setDefaultRenderer(Object.class, new StripeTableRenderer());

        cTable.addMouseListener(new TableMouseListener());
        cTable.setAlignmentX(JTable.CENTER_ALIGNMENT);
        cTable.setAlignmentY(JTable.CENTER_ALIGNMENT);

        cTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);



        TableColumn column = cTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);
        column.sizeWidthToFit();
        column.setCellRenderer(new CellRenderer0());
        HeaderRenderer01 hr = new HeaderRenderer01();
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(330);
        column.setCellRenderer(new CellRenderer1());
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(150);
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(90);
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);


        JScrollPane tablePane = new JScrollPane(cTable);
        tablePane.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                //System.out.println("mousePressed called.");
                if (SwingUtilities.isRightMouseButton(e)) {

                        popupMenuX2.remove(pastemsgItem);
                        if (cbm.hasClipBoardValidData()) {
                            popupMenuX2.add(pastemsgItem);
                        }

                            last_right_click_X = e.getX();
                            last_right_click_Y = e.getY();
                            popupMenuX2.show(e.getComponent(), last_right_click_X, last_right_click_Y);

                }
            }
        });


        tablePane.putClientProperty(TABNAME, name);

        String icon_name = QBrowserIconsFactory.Flagbase + "1.png";
        if (name.indexOf(TOPIC_SUFFIX) != -1) {
            icon_name = QBrowserIconsFactory.Flagbase + "4.png";
        } else if (name.indexOf(LOCAL_STORE_SUFFIX) != -1) {
            icon_name = QBrowserIconsFactory.Flagbase + "7.png";
        }
        
        


        tabbedPane.addTab(name, QBrowserIconsFactory.getImageIcon(icon_name), tablePane);

        int size = tabbedPane.getTabCount();
        jtableins.put(name, cTable);
        tabbedPane.setSelectedIndex(size - 1);
        addDropTargetListenerToComponents(new QBrowserDropTargetListener3(), tablePane);
        
        return size - 1;

        } catch (Throwable gtex) {
            return 0;
        }
    }

    void copyComboBox() {
        tqBox.removeAllItems();
        DefaultComboBoxModel qmodel = (DefaultComboBoxModel) qBox.getModel();
        for (int i = 0; i < qmodel.getSize(); i++) {
            String key = (String) qmodel.getElementAt(i);
            tqBox.addItem(key);
        }
        ComboBoxEditor editor = qBox.getEditor();
        String name = getPureDestName((String) editor.getItem());
        tqBox.setSelectedItem(name);
    }

   int createNewLocalMsgPane(String name) {

      try {
        LocalMsgTable tm = new LocalMsgTable();
        final JTable cTable = new JTable(tm);
        cTable.setDragEnabled(true);

        RowSorter.SortKey sk = new RowSorter.SortKey(2, SortOrder.ASCENDING);
        ArrayList keys = new ArrayList();
        keys.add(sk);

        TableRowSorter trs = new TableRowSorter(tm);
        trs.setSortKeys(keys);

        cTable.setRowSorter(trs);



        cTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                //System.out.println("mousePressed called.");
                if (SwingUtilities.isLeftMouseButton(e)) {
                    //int selrow = cTable.getSelectedRow();
                } else if (SwingUtilities.isRightMouseButton(e)) {

                    if (cTable.getSelectedRow() != -1) {

                        last_right_click_X = e.getX();
                        last_right_click_Y = e.getY();
                        popupMenuX.remove(copymsgItem2);
                        popupMenuX.remove(pastemsgItem);

                        popupMenuX.add(copymsgItem2);
                        if (cbm.hasClipBoardValidData()) {
                            popupMenuX.add(pastemsgItem);
                        }
                        popupMenuX.show(e.getComponent(), last_right_click_X, last_right_click_Y);

                    } else {

                        popupMenuX2.remove(pastemsgItem);
                        if (cbm.hasClipBoardValidData()) {
                            popupMenuX2.add(pastemsgItem);
                        }

                            last_right_click_X = e.getX();
                            last_right_click_Y = e.getY();
                            popupMenuX2.show(e.getComponent(), last_right_click_X, last_right_click_Y);

                    }

                }

            }
            
        });


        cTable.setDefaultRenderer(Object.class, new StripeTableRenderer());

        cTable.addMouseListener(new LocalTableMouseListener());
        cTable.setAlignmentX(JTable.CENTER_ALIGNMENT);
        cTable.setAlignmentY(JTable.CENTER_ALIGNMENT);

        cTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);



        TableColumn column = cTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);
        column.sizeWidthToFit();
        column.setCellRenderer(new CellRenderer0());
        HeaderRenderer01 hr = new HeaderRenderer01();
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(330);
        column.setCellRenderer(new CellRenderer1());
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(150);
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(90);
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);

        column = cTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(50);
        column.setHeaderRenderer(hr);


        JScrollPane tablePane = new JScrollPane(cTable);
        tablePane.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(final MouseEvent e) {
                //System.out.println("mousePressed called.");
                if (SwingUtilities.isRightMouseButton(e)) {

                        popupMenuX2.remove(pastemsgItem);
                        if (cbm.hasClipBoardValidData()) {
                            popupMenuX2.add(pastemsgItem);
                        }

                            last_right_click_X = e.getX();
                            last_right_click_Y = e.getY();
                            popupMenuX2.show(e.getComponent(), last_right_click_X, last_right_click_Y);
                        

                }
            }
        });
        tablePane.putClientProperty(TABNAME, name);

        String icon_name = QBrowserIconsFactory.Flagbase + "1.png";
        if (name.indexOf(TOPIC_SUFFIX) != -1) {
            icon_name = QBrowserIconsFactory.Flagbase + "4.png";
        } else if (name.indexOf(LOCAL_STORE_SUFFIX) != -1) {
            icon_name = QBrowserIconsFactory.Flagbase + "7.png";
        }


        tabbedPane.addTab(name, QBrowserIconsFactory.getImageIcon(icon_name), tablePane);

        int size = tabbedPane.getTabCount();
        jtableins.put(name, cTable);
        tabbedPane.setSelectedIndex(size - 1);
        addDropTargetListenerToComponents(new QBrowserDropTargetListener3(), tablePane);
        return size - 1;

        } catch (Throwable gtex) {
            return 0;
        }
    }

    /**
     * Main
     */
    public static void main(String args[]) {

        try {
            LookAndFeelFactory.installDefaultLookAndFeelAndExtension();

            UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
            LookAndFeelFactory.installJideExtension(LookAndFeelFactory.VSNET_STYLE);
        } catch (Exception lafe) {
            System.err.println(lafe.getMessage());
            LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        }

        // スプラッシュの取得
        SplashScreen splash = SplashScreen.getSplashScreen();

        // スプラッシュに描画を行う
        Graphics2D g = splash.createGraphics();
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.setBackground(Color.WHITE);
        
        g.drawString("QBrowser", 28, 60);
        g.drawString("light", 48, 85);


        // スプラッシュの更新
        splash.update();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {


                try {

                    JFrame frame = new JFrame();
                    Dimension d = new Dimension();
                    d.setSize(1080, 700);
                    frame.setPreferredSize(d);
                    frame.setTitle(QBrowserV2.title + " - " + resources.getString("qkey.msg.msg173"));
                    frame.setBackground(Color.white);
                    frame.getContentPane().setLayout(new BorderLayout());

                    final QBrowserV2 qb = new QBrowserV2();
                    qb.initLocalStoreManager();
                    qb.initQBrowserKey();

                    frame.getContentPane().add("Center", qb);
                    qb.setOyaFrame(frame);
                    frame.addWindowListener(new WindowAdapter() {

                        @Override
                        public void windowClosing(WindowEvent e) {
                            qb.cleanupSubscriberThreads();
                            qb.shutdownJMS();
                            qb.cbm.copyToClipBoard("");
                            System.exit(0);
                        }
                    });
                    if (!QBrowserV2.isBrokerEE) {
                        frame.setTitle(QBrowserV2.title + resources.getString("qkey.msg.msg076") + " - " + serverHost + ":" + serverPort);
                    }
                    frame.pack();
                    JideSwingUtilities.globalCenterWindow(frame);

                    if (isStartingSuccessful) {
                        frame.setVisible(true);
                    }
                    java.net.URL imageURL = QBrowserV2.class.getResource("icons/icons/network16.png");
                    ImageIcon icon = new ImageIcon(imageURL);
                    frame.setIconImage(icon.getImage());
                    //qb.qBox.requestFocusInWindow();
                    qb.tabbedPane.requestFocusForVisibleComponent();

                

                } catch (Throwable globaltex) {
                    globaltex.printStackTrace();
                    System.err.println(globaltex.getMessage());
                }

            }
        });

    }

    static String getUsageString() {
        return "usage: QBrowser [-b <brokerhost>] [-p <brokerport>]" + "\n" +
                "   <brokerhost>    Host to connect to. Default is " +
                DEFAULT_BROKER_HOST +
                "\n" +
                "   <brokerport>    Port to connect to. Default is " +
                DEFAULT_BROKER_PORT;
    }

    void ifnotyetDestNameInQueueDisplayBoxThenAdd(String pureQueueName) {
        String queue_for_display = pureQueueName;
        if(!destinationNamesForDisplayQueue.contains(queue_for_display)) {
            destinationNamesForDisplayQueue.add(queue_for_display);
            Collections.sort(destinationNamesForDisplayQueue);
            newmessageFrame = null;
        }

        


    }

    void ifnotyetDestNameInTopicDisplayBoxThenAdd(String pureTopicName) {
        String topic_for_display = pureTopicName;
        if(!destinationNamesForDisplayTopic.contains(topic_for_display)) {
            destinationNamesForDisplayTopic.add(topic_for_display);
            Collections.sort(destinationNamesForDisplayTopic);
            newmessageFrame = null;
            subscribeDialog = null;
        }



    }

    void importQueueNamesToMATESAKIBOX1() {
        matesakiBox1.removeAllItems();
        for (int i = 0; i < destinationNamesForDisplayQueue.size(); i++) {
            String queuename = (String)destinationNamesForDisplayQueue.get(i);
            this.addSendDest1(queuename);
        }
        //matesakiBox1.setEditable(true);
    }

    void importTopicNamesToMATESAKIBOX1() {
        matesakiBox1.removeAllItems();
        for (int i = 0; i < destinationNamesForDisplayTopic.size(); i++) {
            String queuename = (String)destinationNamesForDisplayTopic.get(i);
            this.addSendDest1(queuename);
        }
        //matesakiBox1.setEditable(true);
    }

    void importLocalStoreNamesToMATESAKIBOX1() {
        matesakiBox1.removeAllItems();
        ArrayList local_store_names = lsm.getAllValidLocalStoreNames();
        for (int i = 0; i < local_store_names.size(); i++) {
            String lsname = (String)local_store_names.get(i);
            this.addSendDest1(lsname);
        }
        matesakiBox1.setEditable(false);
    }

    void importQueueNamesToMATESAKIBOX2() {
        matesakiBox2.removeAllItems();
        for (int i = 0; i < destinationNamesForDisplayQueue.size(); i++) {
            String queuename = (String)destinationNamesForDisplayQueue.get(i);
            this.addSendDest2(queuename);
        }
        //matesakiBox2.setEditable(true);
    }

    void importTopicNamesToMATESAKIBOX2() {
        matesakiBox2.removeAllItems();
        for (int i = 0; i < destinationNamesForDisplayTopic.size(); i++) {
            String queuename = (String)destinationNamesForDisplayTopic.get(i);
            this.addSendDest2(queuename);
        }
        //matesakiBox2.setEditable(true);
    }

    void importLocalStoreNamesToMATESAKIBOX2() {
        matesakiBox2.removeAllItems();
        ArrayList local_store_names = lsm.getAllValidLocalStoreNames();
        for (int i = 0; i < local_store_names.size(); i++) {
            String lsname = (String)local_store_names.get(i);
            this.addSendDest2(lsname);
        }
        matesakiBox2.setEditable(false);
    }

    void importQueueNamesToMATESAKIBOX3() {
        matesakiBox3.removeAllItems();
        for (int i = 0; i < destinationNamesForDisplayQueue.size(); i++) {
            String queuename = (String)destinationNamesForDisplayQueue.get(i);
            this.addSendDest3(queuename);
        }
        //matesakiBox3.setEditable(true);
    }

    void importTopicNamesToMATESAKIBOX3() {
        matesakiBox3.removeAllItems();
        for (int i = 0; i < destinationNamesForDisplayTopic.size(); i++) {
            String queuename = (String)destinationNamesForDisplayTopic.get(i);
            this.addSendDest3(queuename);
        }
        //matesakiBox3.setEditable(true);
    }

    void importLocalStoreNamesToMATESAKIBOX3() {
        matesakiBox3.removeAllItems();
        ArrayList local_store_names = lsm.getAllValidLocalStoreNames();
        for (int i = 0; i < local_store_names.size(); i++) {
            String lsname = (String)local_store_names.get(i);
            this.addSendDest3(lsname);
        }
        matesakiBox3.setEditable(false);
    }

    void importLocalStoreNamesToLOCALSTOREBOX() {
        localstoreBox.removeAllItems();
        addLSDest(resources.getString("qkey.msg.msg275"));
        ArrayList local_store_names = lsm.getAllValidLocalStoreNames();
        for (int i = 0; i < local_store_names.size(); i++) {
            String lsname = (String)local_store_names.get(i);
            addLSDest(lsname);
        }
        localstoreBox.setEditable(false);
    }

    public javax.jms.Destination convertVendorDestinationToLocalDestination(javax.jms.Destination dest) throws Exception {

        if (dest == null) {
            return null;
        }

        if (dest instanceof LocalDestination) {
            return dest;
        } else {

            if (dest instanceof Topic) {
                String destname = ((Topic) dest).getTopicName();
                LocalTopic lt = new LocalTopic(destname);
                lt.setOriginalDestinationWithSuffix(destname + TOPIC_SUFFIX);
                return lt;
            } else {
                String destname = ((Queue) dest).getQueueName();
                LocalQueue lq = new LocalQueue(destname);
                lq.setOriginalDestinationWithSuffix(destname + QUEUE_SUFFIX);
                return lq;
            }

        }
    }

    public javax.jms.Destination convertLocalDestinationToVendorDestination(javax.jms.Destination dest) throws Exception {


        if (dest == null) {
            return null;
        }

        if (dest instanceof LocalDestination) {

            if (dest instanceof LocalTopic) {
                String destname = ((Topic) dest).getTopicName();
                return session.createTopic(destname);
            } else {
                String destname = ((Queue) dest).getQueueName();
                return session.createQueue(destname);
            }

        } else {
            return dest;
        }
    }

    void addSendDest1(String name) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) matesakiBox1.getModel();

        if (model.getIndexOf(name) < 0) {
            // Name is not in menu. Add it.
            model.addElement(name);
        }
    }

    void addSendDest2(String name) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) matesakiBox2.getModel();

        if (model.getIndexOf(name) < 0) {
            // Name is not in menu. Add it.
            model.addElement(name);
        }
    }

    void addSendDest3(String name) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) matesakiBox3.getModel();

        if (model.getIndexOf(name) < 0) {
            // Name is not in menu. Add it.
            model.addElement(name);
        }
    }

    void addLSDest(String name) {
        DefaultComboBoxModel model = (DefaultComboBoxModel) localstoreBox.getModel();

        if (model.getIndexOf(name) < 0) {
            // Name is not in menu. Add it.
            model.addElement(name);
        }
    }

    static void usage() {
        System.out.println(getUsageString());
        System.exit(1);
    }

    public static void dumpException(Exception e) {
        Exception linked = null;
        if (e instanceof JMSException) {
            linked = ((JMSException) e).getLinkedException();
        }

        if (linked == null) {
            e.printStackTrace();
        } else {
            System.err.println(e.toString());
            linked.printStackTrace();
        }
    }

    /**
     * Return a string description of the type of JMS message
     */
    public static String messageType(Message m) {

        if (m instanceof TextMessage) {
            return "TextMessage";
        } else if (m instanceof BytesMessage) {
            return "BytesMessage";
        } else if (m instanceof MapMessage) {
            return "MapMessage";
        } else if (m instanceof ObjectMessage) {
            return "ObjectMessage";
        } else if (m instanceof StreamMessage) {
            return "StreamMessage";
        } else if (m instanceof Message) {
            return "Message";
        } else {
            // Unknown Message type
            String type = m.getClass().getName();
            StringTokenizer st = new StringTokenizer(type, ".");
            String s = null;
            while (st.hasMoreElements()) {
                s = st.nextToken();
            }
            return s;
        }
    }

    static String displayFloatValueKirisute(float value, int maxsyousutenketa) {
        StringBuilder result = new StringBuilder();

        Float ft = new Float(value);
        char[] chars = ft.toString().toCharArray();
        int kcount = 0;
        boolean is_syosu = false;

        for (int i = 0; chars.length > i; i++) {

            if (chars[i] == '.') {
                is_syosu = true;
            } else {

                if (is_syosu) {
                    kcount++;
                }
            }

            result.append(chars[i]);

            if (kcount == maxsyousutenketa) {
                return result.toString();
            }

        }

        return result.toString();
    }

    public static String messageBodyLengthAsString(Message m) {
        String result = "N/A";
        boolean kbflag = false;
        try {
            float lengthr = messageBodyLength(m);
            if (lengthr > 1023) {
                lengthr = lengthr / 1024;
                kbflag = true;
            }

            if (lengthr != -1) {
                result = displayFloatValueKirisute(lengthr, 2);
            }

            //.0は切る
            if (result.endsWith(".0")) {
                result = result.substring(0, result.length() - 2);
            }

            if (kbflag) {
                result = result + " KB";
            } else {
                result = result + " byte";
            }

        } catch (Exception e) {
            //NOP
            //e.printStackTrace();
        }




        return result;

    }

    /**
     * Return a string description of the type of JMS message
     */
    static float messageBodyLength(Message m) throws Exception {

        if (m instanceof TextMessage) {
            float result = 0;

            try {
                TextMessage temp = (TextMessage) m;
                result = temp.getText().length();

                if ((result == 0) && (temp.getText().length() != 0)) {
                    result = 1;
                }
            } catch (Exception e) {
                //NOP
                //e.printStackTrace();
            }
            return result;
        } else if (m instanceof BytesMessage) {
            float result = 0;
            try {
                BytesMessage temp = (BytesMessage) m;
                //temp.reset();
                result = temp.getBodyLength();

                if ((result == 0) && (temp.getBodyLength() != 0)) {
                    result = 1;
                }
            } catch (Exception e) {
                //NOP
                //e.printStackTrace();
            }
            return result;
        } else if (m instanceof MapMessage) {
            return -1;
        } else if (m instanceof ObjectMessage) {
            return -1;
        } else if (m instanceof StreamMessage) {
            return -1;
        } else if (m instanceof Message) {
            return -1;
        } else {
            return -1;
        }
    }

    /**
     * Return a string representation of the body of a JMS
     * bytes message. This is basically a hex dump of the body.
     * Note, this only looks at the first 1K of the message body.
     */
    static String jmsBytesBodyAsString(Message m) {
        byte[] body = new byte[1024];
        int n = 0;

        if (m instanceof BytesMessage) {
            try {
                ((BytesMessage) m).reset();
                n = ((BytesMessage) m).readBytes(body);


            } catch (JMSException ex) {
                return (ex.toString());
            }
        } else if (m instanceof StreamMessage) {
            try {
                ((StreamMessage) m).reset();
                StringBuilder sb = new StringBuilder();
                Object ro = null;
                int count = 0;
                try {
                while ((ro = ((StreamMessage)m).readObject()) != null) {
                    count++;
                    sb.append(resources.getString("qkey.msg.msg241")).append(" = ").append(count).append(" / ");
                    sb.append(PropertyUtil.selfDescribe(ro)).append("\n");
                }
                } catch (MessageEOFException eof) {
                    return sb.toString();
                } catch (Throwable thex) {
                    return sb.toString();
                }
            } catch (JMSException ex) {
                return (ex.toString());
            } catch (Throwable thex2) {
                return thex2.toString();
            }
        }

        if (n <= 0) {
            return "<empty body>";
        } else {
            return (QBrowserUtil.toHexDump(body, n) +
                    ((n >= body.length) ? "\n. . ." : ""));
        }
    }

    /**
     * Return a string representation of a JMS message body
     */
    static String jmsMsgBodyAsString(Message m) {

        if (m instanceof TextMessage) {
            try {
                return ((TextMessage) m).getText();
            } catch (JMSException ex) {
                return ex.toString();
            }
        } else if (m instanceof BytesMessage) {
            return jmsBytesBodyAsString(m);
        } else if (m instanceof MapMessage) {
            MapMessage msg = (MapMessage) m;
            // Get all MapMessage properties and stuff into a hash table
            try {
                StringBuilder sb = new StringBuilder();
                for (Enumeration enu = msg.getMapNames();
                        enu.hasMoreElements();) {
                    String name = (enu.nextElement()).toString();
                    sb.append(resources.getString("qkey.msg.msg184")).append(" = ").append(name).append(" / ");
                    Object obj = msg.getObject(name);
                    sb.append(PropertyUtil.selfDescribe(obj)).append("\n");
                }

                return sb.toString();
            } catch (JMSException ex) {
                return (ex.toString());
            }
        } else if (m instanceof ObjectMessage) {
            ObjectMessage msg = (ObjectMessage) m;
            Object obj = null;
            try {
                obj = msg.getObject();
                if (obj != null) {
                    return obj.toString();
                } else {
                    return "null";
                }
            } catch (Exception ex) {
                return (ex.toString());
            }
        } else if (m instanceof StreamMessage) {
            return jmsBytesBodyAsString(m);
        } else if (m instanceof Message) {
            return resources.getString("qkey.msg.msg242");
        }
        return resources.getString("qkey.msg.msg243") + " " + m;
    }

    JPanel createSimpleTextAreaPane(TextArea textArea) {
        JPanel panel = new JPanel();
        panel.add(textArea);
        return panel;
    }

    JPanel createSearchableTextArea(JTextComponent textArea) {
        final JPanel panel = new JPanel(new BorderLayout());
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setSize(new Dimension(500,300));
        panel.add(jsp, BorderLayout.CENTER);
        Searchable searchable = SearchableUtils.installSearchable(textArea);
        
        searchable.setRepeats(true);
        SearchableBar _textAreaSearchableBar = SearchableBar.install(searchable, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), new SearchableBar.Installer() {

            public void openSearchBar(SearchableBar searchableBar) {
                panel.add(searchableBar, BorderLayout.AFTER_LAST_LINE);
                panel.invalidate();
                panel.revalidate();
            }

            public void closeSearchBar(SearchableBar searchableBar) {
                panel.remove(searchableBar);
                panel.invalidate();
                panel.revalidate();
            }
        });
        _textAreaSearchableBar.getInstaller().openSearchBar(_textAreaSearchableBar);
        return panel;
    }

    /**
     * Takes the JMS header fields of a JMS message and puts them in
     * a HashMap
     */
    static HashMap jmsHeadersToHashMap(Message m) throws JMSException {
        HashMap hdrs = new HashMap();
        String s = null;

        s = m.getJMSCorrelationID();
        hdrs.put("JMSCorrelationID", s);

        s = String.valueOf(m.getJMSDeliveryMode());
        hdrs.put("JMSDeliverMode", s);

        Destination d = m.getJMSDestination();
        if (d != null) {
            if (d instanceof Queue) {
                s = ((Queue) d).getQueueName();
            } else {
                s = ((Topic) d).getTopicName();
            }
        } else {
            s = "";
        }
        hdrs.put("JMSDestination", s);

        s = String.valueOf(m.getJMSExpiration());
        hdrs.put("JMSExpiration", s);

        s = m.getJMSMessageID();
        hdrs.put("JMSMessageID", s);

        s = String.valueOf(m.getJMSPriority());
        hdrs.put("JMSPriority", s);

        s = String.valueOf(m.getJMSRedelivered());
        hdrs.put("JMSRedelivered", s);

        d = m.getJMSReplyTo();
        if (d != null) {
            if (d instanceof Queue) {
                s = ((Queue) d).getQueueName();
            } else {
                s = ((Topic) d).getTopicName();
            }
        } else {
            s = "";
        }
        hdrs.put("JMSReplyTo", s);

        s = String.valueOf(m.getJMSTimestamp());
        hdrs.put("JMSTimestamp", s);

        s = m.getJMSType();
        hdrs.put("JMSType", s);

        return hdrs;
    }


    class OptionListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            //System.out.println("ItemEvent");
        }
    }

    class FileCButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            cmessagefooter.setText("");
            fcframe = new JFrame();

            JPanel fcc = new JPanel();
            JLabel jlc = new JLabel(resources.getString("qkey.msg.msg077"));
            mfilechooser = new JFileChooser();
            mfilechooser.addActionListener(new FileChooserActionListener());
            fcc.setLayout(new BorderLayout());
            fcc.add(BorderLayout.NORTH, jlc);
            fcc.add(BorderLayout.CENTER, mfilechooser);
            fcframe.getContentPane().add(fcc);
            fcframe.pack();
            fcframe.setLocationRelativeTo(mfilebodyPanel);
            fcframe.setVisible(true);
        }
    }

    void createMapMessageBodyPanel() {
              mapmBodyPanel = new MapMessagePropertyPanel(null);
              mapmBodyPanel.label.setText(resources.getString("qkey.msg.msg164") + MAPMESSAGE + ")");

    }
    
    void createMapMessageBodyPanel(MapMessage srcmsg) {
              mapmBodyPanel = new MapMessagePropertyPanel(srcmsg);
              mapmBodyPanel.label.setText(resources.getString("qkey.msg.msg164") + MAPMESSAGE + ")");

    }

    void createBytesMessageBodyPanel() {
          if (mfilebodyPanel == null) {
            mfilebodyPanel = new JPanel();
            mfilebodyPanel.setSize(new Dimension(500, 231));
            mfilebodyPanel.setLayout(new BorderLayout());

            mfilepath = new JTextField();
            mfilepath.addFocusListener(new TFocusListener());
            mfilebodyPanel.add(BorderLayout.CENTER, mfilepath);
            JButton filecbutton = new JButton("...");
            filecbutton.addActionListener(new FileCButtonListener());

            mfilebodyPanel.add(BorderLayout.EAST, filecbutton);
            JLabel jl04 = new JLabel(resources.getString("qkey.msg.msg027"));
            mfilebodyPanel.add(BorderLayout.WEST, jl04);
          }

    }

    void createStreamMessageBodyPanel() {

              smBodyPanel = new StreamMessagePropertyPanel(null);
              smBodyPanel.label.setText(resources.getString("qkey.msg.msg164") + STREAMMESSAGE + ")");
          

    }

    void createStreamMessageBodyPanel(StreamMessage srcmsg) {

              smBodyPanel = new StreamMessagePropertyPanel(srcmsg);
              smBodyPanel.label.setText(resources.getString("qkey.msg.msg164") + STREAMMESSAGE + ")");

    }

    class MessageTypeListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {



            if (e.getStateChange() == e.SELECTED) {
           

            if (e.getItem().equals(TEXTMESSAGE)) {
                //TextMessage
                southpanel.remove(currentBodyPanel);
                southpanel.add(BorderLayout.CENTER, mbodyPanel);
                messagesentakupanel.add(BorderLayout.CENTER,penc);
                currentBodyPanel = mbodyPanel;
                southpanel.updateUI();
            } else if (e.getItem().equals(BYTESMESSAGE)) {
                //BytesMessage
                southpanel.remove(currentBodyPanel);
                createBytesMessageBodyPanel();
                messagesentakupanel.remove(penc);
                mbodyPanel.textAreaBK = null;
                southpanel.add(BorderLayout.CENTER, mfilebodyPanel);
                currentBodyPanel = mfilebodyPanel;
                southpanel.updateUI();
            } else if (e.getItem().equals(MAPMESSAGE)) {
                //MapMessage
                southpanel.remove(currentBodyPanel);
                createMapMessageBodyPanel();
                messagesentakupanel.add(BorderLayout.CENTER,penc);
                //messagesentakupanel.remove(penc);
                mbodyPanel.textAreaBK = null;
                southpanel.add(BorderLayout.CENTER, mapmBodyPanel);
                currentBodyPanel = mapmBodyPanel;
                southpanel.updateUI();

            } else if (e.getItem().equals(STREAMMESSAGE)) {
                //StreamMessage
                southpanel.remove(currentBodyPanel);
                createStreamMessageBodyPanel();
                messagesentakupanel.add(BorderLayout.CENTER,penc);
                //messagesentakupanel.remove(penc);
                mbodyPanel.textAreaBK = null;
                southpanel.add(BorderLayout.CENTER, smBodyPanel);
                currentBodyPanel = smBodyPanel;
                southpanel.updateUI();

            } else if (e.getItem().equals(MESSAGE)) {
                //Message
                southpanel.remove(currentBodyPanel);
                messagesentakupanel.remove(penc);
                mbodyPanel.textAreaBK = null;
                JPanel plain_panel = new JPanel();
                JLabel message_label = new JLabel();
                message_label.setText(resources.getString("qkey.msg.msg244"));
                plain_panel.add(BorderLayout.CENTER ,message_label);
                southpanel.add(plain_panel);
                currentBodyPanel = plain_panel;
                southpanel.updateUI();
            }

            }

        }
    }

    class MessageEncodingTypeListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if(e.getStateChange() == e.SELECTED) {
            

            String indicated_encode = (String)e.getItem();
            String selected = (String)encoding_type.getSelectedItem();

            if (indicated_encode.equals(selected)) {

            try {
                byte[] body_bytes = null;
                  if ((mbodyPanel.textAreaBK != null) && (mbodyPanel.textAreaBK.length != 0)) {
                    body_bytes = mbodyPanel.textAreaBK;
                  } else {
                    body_bytes = mbodyPanel.textArea.getText().getBytes();
                    mbodyPanel.textAreaBK = body_bytes;
                  }

                String newbody = new String(body_bytes, Charset.forName(indicated_encode));

                mbodyPanel.textArea.setText(newbody);
                //encode_before = indicated_encode;

            } catch (Exception toe) {
                //NOP
            }

            }

            }

        }
    }

    class BytesMessageBodyInputTypeListener implements ActionListener {

        javax.jms.Message msg = null;

        public BytesMessageBodyInputTypeListener(javax.jms.Message value) {
            msg = value;
        }

        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand().equals("Hex")) {
                //Hexa


                bodycontainer.remove(details_body_current);

                bodycontainer.add(BorderLayout.CENTER, bodyPanel);
                details_body_current = bodyPanel;
                bodycontainer.updateUI();
            } else {
                //Download

                bodycontainer.remove(details_body_current);
                bodycontainer.add(BorderLayout.CENTER, downloadbodyPanel);
                details_body_current = downloadbodyPanel;
                JPanel downloadmsgpanel = new JPanel();
                downloadmsg = new JLabel("");
                downloadmsgpanel.add(downloadmsg);
                bodycontainer.add(BorderLayout.SOUTH, downloadmsgpanel);
                bodycontainer.updateUI();
            }

        }
    }

    /*
    void createMapMessageBodyForDownloadPanel() {
          if (mapmBodyForDownloadPanel == null) {
              mapmBodyForDownloadPanel = new MapMessagePropertyForDownloadPanel();
          }

    }

    void createMapMessageAllPropertiesPanel() {
          if (mapmBodyForAPPanel == null) {
              mapmBodyForAPPanel = new MapMessageAllPropertiesPanel();
          }

    }

    void createStreamMessageBodyForDownloadPanel() {
          if (smBodyForDownloadPanel == null) {
              smBodyForDownloadPanel = new StreamMessagePropertyForDownloadPanel();
          }

    }

    void createStreamMessageAllPropertiesPanel() {
          if (smBodyForAPPanel == null) {
              smBodyForAPPanel = new StreamMessageAllPropertiesPanel();
          }


    }
    */

    class MapMessageBodyInputTypeListener implements ActionListener {

        MessageContainer mc = null;
        JPanel bodycontainer = null;
        JPanel details_body_current = null;
        PropertyPanel bodyPanel = null;
        JFrame detailsFrame = null;

        public MapMessageBodyInputTypeListener(MessageContainer value) {
            mc = value;
        }

        public void actionPerformed(ActionEvent e) {

            String msgid = mc.getVmsgid();
            JPanel curr_body_container = qbrowsercache.getCurrentBodyContainerPanel(msgid);
            JPanel curr_body_panel = (JPanel)qbrowsercache.getCurrentBodyPanel(msgid);
            final JFrame dFrame = qbrowsercache.getCurrentDetailFrame(msgid);

            if (e.getActionCommand().equals(resources.getString("qkey.msg.msg188"))) {
                //Normal

                PropertyPanel textBodyPanel = new TextMessageBodyPanel();
                //bodycontainer.remove(details_body_current);
                //bodycontainer.updateUI();
                curr_body_container.remove(curr_body_panel);
                curr_body_container.updateUI();

                //if (bodyPanel.textArea.getText().length() == 0) {
                //    bodyPanel.load(jmsMsgBodyAsString(msg));
                //}
                if (textBodyPanel.textArea.getText().length() == 0) {
                    textBodyPanel.load(jmsMsgBodyAsString(mc.getMessage()));
                }
                curr_body_container.add(BorderLayout.CENTER, textBodyPanel);
                
                //details_body_current = bodyPanel;
                qbrowsercache.setCurrentBodyPanel(msgid, textBodyPanel);
                //bodyPanel.updateUI();
                curr_body_panel.updateUI();
                
                dFrame.getRootPane().updateUI();
            } else if (e.getActionCommand().equals(resources.getString("qkey.msg.msg189"))) {
                //Bytes Property Download



                curr_body_container.remove(curr_body_panel);
                curr_body_container.updateUI();
                //bodycontainer.remove(details_body_current);
                //bodycontainer.updateUI();
                //createMapMessageBodyForDownloadPanel();
                MapMessagePropertyForDownloadPanel mapmBodyForDownloadPanel = new MapMessagePropertyForDownloadPanel();

                //データ挿入、Bytesプロパティのみ
                BytesForDownloadPropertyTable
                mapm_download_property_table = mapmBodyForDownloadPanel.getMapm_download_property_table();
                mapm_download_property_table.load(mc);
                mdTable = mapmBodyForDownloadPanel.getMdTable();

                TableColumn column2 = mdTable.getColumnModel().getColumn(2);
                DownloadCellEditor dce2 = new DownloadCellEditor();
                DownloadCellRenderer dcr2 = new DownloadCellRenderer();
                dce2.setClickCountToStart(0);
                column2.setCellEditor(dce2);
                column2.setCellRenderer(dcr2);

                //bodycontainer.add(BorderLayout.CENTER, mapmBodyForDownloadPanel);
                curr_body_container.add(BorderLayout.CENTER, mapmBodyForDownloadPanel);
                //details_body_current = mapmBodyForDownloadPanel;
                qbrowsercache.setCurrentBodyPanel(msgid, mapmBodyForDownloadPanel);
                JPanel downloadmsgpanel = new JPanel();
                JLabel downloadmsg = new JLabel("");
                qbrowsercache.setCurrentDownloadMsgLabel(msgid, downloadmsg);
                downloadmsgpanel.add(downloadmsg);
                //bodycontainer.add(BorderLayout.SOUTH, downloadmsgpanel);
                //bodycontainer.updateUI();
                //detailsFrame.getRootPane().updateUI();
                curr_body_container.add(BorderLayout.SOUTH, downloadmsgpanel);
                curr_body_container.updateUI();
                dFrame.getRootPane().updateUI();
            } else if (e.getActionCommand().equals(resources.getString("qkey.msg.msg197"))) {
                //Display all properties as table format.

                curr_body_container.remove(curr_body_panel);
                curr_body_container.updateUI();

                MapMessageAllPropertiesPanel
                mapmBodyForAPPanel = (MapMessageAllPropertiesPanel)qbrowsercache.getCurrentMapmBodyForAPPanel(msgid);

                curr_body_container.add(BorderLayout.CENTER, mapmBodyForAPPanel);
                //details_body_current = mapmBodyForAPPanel;
                qbrowsercache.setCurrentBodyPanel(msgid, mapmBodyForAPPanel);
                JPanel downloadmsgpanel = new JPanel();
                JLabel downloadmsg = new JLabel("");
                downloadmsgpanel.add(downloadmsg);
                qbrowsercache.setCurrentDownloadMsgLabel(msgid, downloadmsg);
                curr_body_container.add(BorderLayout.SOUTH, downloadmsgpanel);
                curr_body_container.updateUI();
                dFrame.getRootPane().updateUI();

            }

        }
    }

    class StreamMessageBodyInputTypeListener implements ActionListener {

        javax.jms.Message msg = null;
        String m_msgid = null;

        public StreamMessageBodyInputTypeListener(String msgid, Message value) {
            msg = value;
            m_msgid = msgid;
        }

        public void actionPerformed(ActionEvent e) {

            //JFrame dFrame = qbrowsercache.getCurrentDetailFrame(m_msgid);
            //PropertyPanel curr_panel = (PropertyPanel)qbrowsercache.getCurrentBodyPanelAndRemoveFromCache(m_msgid);
            //JPanel curr_body_container = qbrowsercache.getCurrentBodyContainerPanel(m_msgid);
            JPanel curr_body_container = qbrowsercache.getCurrentBodyContainerPanel(m_msgid);
            JPanel curr_panel = (JPanel)qbrowsercache.getCurrentBodyPanel(m_msgid);
            final JFrame dFrame = qbrowsercache.getCurrentDetailFrame(m_msgid);

            if (e.getActionCommand().equals(resources.getString("qkey.msg.msg188"))) {
                //Normal

                PropertyPanel textBodyPanel = new TextMessageBodyPanel();

                curr_body_container.remove(curr_panel);
                curr_body_container.updateUI();

                //if (bodyPanel.textArea.getText().length() == 0) {
                if (textBodyPanel.textArea.getText().length() == 0) {
                    textBodyPanel.load(jmsMsgBodyAsString(msg));
                }
                curr_body_container.add(BorderLayout.CENTER, textBodyPanel);
                qbrowsercache.setCurrentBodyPanel(m_msgid, textBodyPanel);
                //details_body_current = bodyPanel;
                curr_panel.updateUI();
                //detailsFrame.getRootPane().updateUI();
                
                dFrame.getRootPane().updateUI();
            } else if (e.getActionCommand().equals(resources.getString("qkey.msg.msg189"))) {
                //Bytes Property Download

                //curr_body_container.remove(details_body_current);
                curr_body_container.remove(curr_panel);
                curr_body_container.updateUI();
                //createStreamMessageBodyForDownloadPanel();
                StreamMessagePropertyForDownloadPanel smBodyForDownloadPanel = new StreamMessagePropertyForDownloadPanel();
                StreamMessageBytesForDownloadPropertyTable sm_download_property_table = smBodyForDownloadPanel.getStreamMessageBytesForDownloadPropertyTable();

                //データ挿入、Bytesプロパティのみ
                MessageContainer currentDownloadTargetMsg = qbrowsercache.getCurrentDownloadTargetMsg(m_msgid);
                sm_download_property_table.load(currentDownloadTargetMsg);

                JTable sdTable = smBodyForDownloadPanel.getSdTable();
                TableColumn column2 = sdTable.getColumnModel().getColumn(2);
                DownloadCellEditor dce2 = new DownloadCellEditor();
                DownloadCellRenderer dcr2 = new DownloadCellRenderer();
                dce2.setClickCountToStart(0);
                column2.setCellEditor(dce2);
                column2.setCellRenderer(dcr2);

                curr_body_container.add(BorderLayout.CENTER, smBodyForDownloadPanel);
                qbrowsercache.setCurrentBodyPanel(m_msgid, smBodyForDownloadPanel);

                //details_body_current = smBodyForDownloadPanel;
                JPanel downloadmsgpanel = new JPanel();
                JLabel downloadmsg = new JLabel("");
                downloadmsgpanel.add(downloadmsg);
                curr_body_container.add(BorderLayout.SOUTH, downloadmsgpanel);
                curr_body_container.updateUI();

                dFrame.getRootPane().updateUI();
            } else if (e.getActionCommand().equals(resources.getString("qkey.msg.msg197"))) {
                //Display all properties as table format.

                curr_body_container.remove(curr_panel);
                curr_body_container.updateUI();
                //createStreamMessageAllPropertiesPanel();
                StreamMessageAllPropertiesPanel
                smBodyForAPPanel = (StreamMessageAllPropertiesPanel)qbrowsercache.getCurrentSmBodyForAPPanel(m_msgid);


                curr_body_container.add(BorderLayout.CENTER, smBodyForAPPanel);
                qbrowsercache.setCurrentBodyPanel(m_msgid, smBodyForAPPanel);

                //details_body_current = smBodyForAPPanel;
                JPanel downloadmsgpanel = new JPanel();
                JLabel downloadmsg = new JLabel("");
                downloadmsgpanel.add(downloadmsg);
                curr_body_container.add(BorderLayout.SOUTH, downloadmsgpanel);
                curr_body_container.updateUI();
                //detailsFrame.getRootPane().updateUI();
                dFrame.getRootPane().updateUI();

            }

        }
    }


    class NewMessageListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            showNewMessagePanel(false);
        }
    }

    class NewMessageFromFileListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showFileChooseWindow(resources.getString("qkey.msg.msg255"),
                    resources.getString("qkey.msg.msg256"), new NewMessageFromFileOKListener());
        }
    }

    class OpenMessageFromFileListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showFileChooseWindow(resources.getString("qkey.msg.msg261"),
                    resources.getString("qkey.msg.msg262"), new OpenMessageFromFileOKListener());
        }
    }
    
    void addLocalStoreCTableFromSpecifiedFilePath(String puredest, String filepath, LocalMsgTable mt, TextArea ta) throws Exception {

          if (filepath.trim().toLowerCase().endsWith(".zip")) {

              File wf = null;

              try {

              if (filepath.indexOf("_" + TEXTMESSAGE) != -1) {

                ta.append(TEXTMESSAGE + resources.getString("qkey.msg.msg321"));
                TextMessageReader tmr = new TextMessageReader();
                ta.append(resources.getString("qkey.msg.msg322") + " " + filepath+ "\n");
                wf = tmr.readPersistedMessageWithLazyLoad(new File(filepath));
                ta.append(TEXTMESSAGE + resources.getString("qkey.msg.msg323"));
                LocalMessageContainer tmsg = tmr.recreateMessagefromReadDataWithLazyLoad();
                mt.add_one_row_ifexists_update(tmsg);
                LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                ta.append(TEXTMESSAGE + resources.getString("qkey.msg.msg324"));
                PersistedMessageReader.clearDir(wf);
                wf = null;

              } else
              if (filepath.indexOf("_" + BYTESMESSAGE) != -1) {

                ta.append(BYTESMESSAGE + resources.getString("qkey.msg.msg321"));
                BytesMessageReader bmr = new BytesMessageReader();
                ta.append(resources.getString("qkey.msg.msg322")+ " " + filepath+ "\n");
                //File wf = bmr.readPersistedMessage(new File(filepath));
                wf = bmr.readPersistedMessageWithLazyLoad(new File(filepath));
                ta.append(BYTESMESSAGE + resources.getString("qkey.msg.msg323"));
                //LocalMessageContainer tmsg = bmr.recreateMessagefromReadData(session);
                LocalMessageContainer tmsg = bmr.recreateMessagefromReadDataWithLazyLoad();
                mt.add_one_row_ifexists_update(tmsg);
                LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                ta.append(BYTESMESSAGE + resources.getString("qkey.msg.msg324"));
                PersistedMessageReader.clearDir(wf);
                wf = null;

              } else
              if (filepath.indexOf("_" + MAPMESSAGE) != -1) {

                ta.append(MAPMESSAGE + resources.getString("qkey.msg.msg321"));
                MapMessageReader mmr = new MapMessageReader();
                ta.append(resources.getString("qkey.msg.msg322") + " " + filepath+ "\n");
                //File wf = mmr.readPersistedMessage(new File(filepath));
                wf = mmr.readPersistedMessageWithLazyLoad(new File(filepath));
                ta.append(MAPMESSAGE + resources.getString("qkey.msg.msg323"));
                //LocalMessageContainer tmsg = mmr.recreateMessagefromReadData(session);
                LocalMessageContainer tmsg = mmr.recreateMessagefromReadDataWithLazyLoad();
                mt.add_one_row_ifexists_update(tmsg);
                LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                ta.append(MAPMESSAGE + resources.getString("qkey.msg.msg324"));
                PersistedMessageReader.clearDir(wf);
                wf = null;


              } else
              if (filepath.indexOf("_" + STREAMMESSAGE) != -1) {

                ta.append(STREAMMESSAGE + resources.getString("qkey.msg.msg321"));
                StreamMessageReader smr = new StreamMessageReader();
                ta.append(resources.getString("qkey.msg.msg322") + " " + filepath+ "\n");
                //File wf = smr.readPersistedMessage(new File(filepath));
                wf = smr.readPersistedMessageWithLazyLoad(new File(filepath));
                ta.append(STREAMMESSAGE + resources.getString("qkey.msg.msg323"));
                //LocalMessageContainer tmsg = smr.recreateMessagefromReadData(session);
                LocalMessageContainer tmsg = smr.recreateMessagefromReadDataWithLazyLoad();
                mt.add_one_row_ifexists_update(tmsg);
                LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                ta.append(STREAMMESSAGE + resources.getString("qkey.msg.msg324"));
                PersistedMessageReader.clearDir(wf);
                wf = null;


              } else
              if (filepath.indexOf("_" + OBJECTMESSAGE) != -1) {

                ta.append(OBJECTMESSAGE + resources.getString("qkey.msg.msg321"));
                ObjectMessageReader omr = new ObjectMessageReader();
                ta.append(resources.getString("qkey.msg.msg322") + " " + filepath+ "\n");
                //File wf = omr.readPersistedMessage(new File(filepath));
                wf = omr.readPersistedMessageWithLazyLoad(new File(filepath));
                ta.append(OBJECTMESSAGE + resources.getString("qkey.msg.msg323"));
                //LocalMessageContainer tmsg = omr.recreateMessagefromReadData(session);
                LocalMessageContainer tmsg = omr.recreateMessagefromReadDataWithLazyLoad();
                mt.add_one_row_ifexists_update(tmsg);
                LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                ta.append(OBJECTMESSAGE + resources.getString("qkey.msg.msg324"));
                PersistedMessageReader.clearDir(wf);


              } else if (filepath.indexOf("_" + MESSAGE) != -1) {

                ta.append(MESSAGE + resources.getString("qkey.msg.msg321"));
                PersistedMessageReader pmr = new PersistedMessageReader();
                ta.append(resources.getString("qkey.msg.msg322") + " " + filepath+ "\n");
                //File wf = pmr.readPersistedMessage(new File(filepath));
                wf = pmr.readPersistedMessageWithLazyLoad(new File(filepath));
                ta.append(MESSAGE + resources.getString("qkey.msg.msg323"));
                //LocalMessageContainer tmsg = pmr.recreateMessagefromReadData(session);
                LocalMessageContainer tmsg = pmr.recreateMessagefromReadDataWithLazyLoad();
                mt.add_one_row_ifexists_update(tmsg);
                LocalStoreManager.addMsgIndex(puredest, tmsg.getVmsgid() , filepath);
                ta.append(MESSAGE + resources.getString("qkey.msg.msg324"));
                PersistedMessageReader.clearDir(wf);

              }

              } catch (Exception e) {
                  PersistedMessageReader.clearDir(wf);
                  throw e;
              }

          }

    }

        public void prepareTMPWORK_LOCALSTORETab() {

            int current_tab_index = 0;

            //まだタブがないとき
            if (!isNamedTabAlreadyCreated(TMPWORK_LOCALSTORE)) {

                //先にキャッシュにあるかを判定する
                JTable cTable = (JTable)jtableins.get(TMPWORK_LOCALSTORE);
                JTable taihiTable = new JTable(new LocalMsgTable());


                //キャッシュにある場合は、旧データを退避しておく
                if (cTable != null) {
                    localTableCopy(cTable, taihiTable);
                }

                //新しいテーブルとタブを作成する
                current_tab_index = createNewLocalMsgPane(TMPWORK_LOCALSTORE);


                //退避データがあるかどうかをチェック
                if (cTable == null) {
                    cTable = (JTable)jtableins.get(TMPWORK_LOCALSTORE);
                    LocalMsgTable mt = (LocalMsgTable) cTable.getModel();
                    //cTable = new JTable(mt);
                    mt.init();

                    addDestToMenu(TMPWORK_LOCALSTORE);


                } else {
                    //System.out.println("退避データあり：要復旧");
                    cTable = (JTable)jtableins.get(TMPWORK_LOCALSTORE);
                    localTableCopy(taihiTable, cTable);
                    jtableins.put(TMPWORK_LOCALSTORE, cTable);

                }

                tabbedPane.setSelectedIndex(current_tab_index);

            } else {
                current_tab_index = tabbedPane.indexOfTab(TMPWORK_LOCALSTORE);
                tabbedPane.setSelectedIndex(current_tab_index);
            }

        }

   class OpenMessageFromFolderOKListener implements ActionListener {


        LocalMsgTable vmt;
        TextArea ta;


        public void processDir(File dir) {

            File[] files = dir.listFiles();
            for (int i = 0 ; i < files.length ;i++) {
                if (files[i].isFile()) {
                    processFile(files[i]);
                } else if (files[i].isDirectory()) {
                    processDir(files[i]);
                }
            }

        }

        public void processFile(File targetf) {
           try {
            //外部から来るファイルについては、ローカルストアの場所に本体ごとコピーする
            LocalStoreProperty lsp = lsm.getLocalStoreProperty("TMPWORK");
            File toFile = new File(lsp.getReal_file_directory() + targetf.getName());
            QBrowserUtil.copy(targetf, toFile);
            addLocalStoreCTableFromSpecifiedFilePath("TMPWORK", toFile.getAbsolutePath(), vmt, ta);
           } catch (Exception ex) {
               ta.append(resources.getString("qkey.msg.msg273") + " " + ex.getMessage());
           }
        }


        public void actionPerformed(ActionEvent e) {
            folderchooseDialog.setVisible(false);

            prepareTMPWORK_LOCALSTORETab();


            try {
              String filepath = folderchoose_file_path.getText();

              JTable cTable = (JTable) jtableins.get(TMPWORK_LOCALSTORE);
              vmt = (LocalMsgTable) cTable.getModel();
              
              File target_folder = new File(filepath);
              if (!target_folder.exists()) {
                  throw new Exception(resources.getString("qkey.msg.msg271"));
              }
              
              //popup
              ta = new TextArea("", 20, 100, TextArea.SCROLLBARS_BOTH);
              int arow = target_folder.list().length + 5;
              if (arow > 30) arow = 30;

              ta.setRows(arow);

              ta.setEditable(false);
              ta.setBackground(Color.WHITE);
              String dispid = e.getSource().toString();
              DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg272"), ta,
                         QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.OpenMultiFile), oya_frame);
                    Thread dprth = new Thread(dpr);
                    //display_threads.add(dprth);
                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }

              try {

                processDir(target_folder);
                reNumberLocalCTable(cTable);

              } catch (Exception openmessageex) {
                   ta.append(resources.getString("qkey.msg.msg273") + " " + openmessageex.getMessage());
              }


            } catch (Exception ex) {
                popupErrorMessageDialog(ex);
            }
        }
    }

    class OpenMessageFromFolderListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            showFolderChooseWindow(resources.getString("qkey.msg.msg268"),
                    resources.getString("qkey.msg.msg269"), new OpenMessageFromFolderOKListener());
        }
    }

   class OpenMessageFromFileOKListener implements ActionListener {

        TextArea ta;

        public void actionPerformed(ActionEvent e) {
            filechooseDialog.setVisible(false);

            prepareTMPWORK_LOCALSTORETab();
            StringBuilder sb = new StringBuilder();


            
              String filepath = filechoose_file_path.getText();

              JTable cTable = (JTable) jtableins.get(TMPWORK_LOCALSTORE);
              LocalMsgTable mt = (LocalMsgTable) cTable.getModel();

              ta = new TextArea("", 5, 60, TextArea.SCROLLBARS_BOTH);

              ta.setEditable(false);
              ta.setBackground(Color.WHITE);
              String dispid = e.getSource().toString();
              DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg272"), ta,
                         QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.OpenFile), oya_frame);
                    Thread dprth = new Thread(dpr);
                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                    }

              try {
                LocalStoreProperty lsp = lsm.getLocalStoreProperty("TMPWORK");
                File fromFile = new File(filepath);
                File toFile = new File(lsp.getReal_file_directory() + fromFile.getName());
                QBrowserUtil.copy(fromFile, toFile);
                addLocalStoreCTableFromSpecifiedFilePath("TMPWORK",toFile.getAbsolutePath(), mt, ta);

              } catch (Exception filereadex) {
                  ta.append(resources.getString("qkey.msg.msg273") + " " + filereadex.getMessage());
              }

        }
    }


   class NewMessageFromFileOKListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            filechooseDialog.setVisible(false);
            File wf = null;

            try {
              String filepath = filechoose_file_path.getText();

              if (filepath.indexOf(TEXTMESSAGE) != -1) {

                TextMessageReader tmr = new TextMessageReader();
                wf = tmr.readPersistedMessage(new File(filepath));
                showNewMessagePanelAsMessageCopy(tmr.recreateMessagefromReadData(session));
                PersistedMessageReader.clearDir(wf);
                wf = null;

              } else
              if (filepath.indexOf(BYTESMESSAGE) != -1) {

                BytesMessageReader bmr = new BytesMessageReader();
                wf = bmr.readPersistedMessage(new File(filepath));
                showNewMessagePanelAsMessageCopy(bmr.recreateMessagefromReadData(session));
                PersistedMessageReader.clearDir(wf);
                wf = null;


              } else
              if (filepath.indexOf(MAPMESSAGE) != -1) {

                MapMessageReader mmr = new MapMessageReader();
                wf = mmr.readPersistedMessage(new File(filepath));
                showNewMessagePanelAsMessageCopy(mmr.recreateMessagefromReadData(session));
                PersistedMessageReader.clearDir(wf);
                wf = null;


              } else
              if (filepath.indexOf(STREAMMESSAGE) != -1) {

                StreamMessageReader smr = new StreamMessageReader();
                wf = smr.readPersistedMessage(new File(filepath));
                showNewMessagePanelAsMessageCopy(smr.recreateMessagefromReadData(session));
                PersistedMessageReader.clearDir(wf);
                wf = null;


              } else
              if (filepath.indexOf(OBJECTMESSAGE) != -1) {

                ObjectMessageReader omr = new ObjectMessageReader();
                wf = omr.readPersistedMessage(new File(filepath));
                showNewMessagePanelAsMessageCopy(omr.recreateMessagefromReadData(session));
                PersistedMessageReader.clearDir(wf);
                wf = null;


              } else {

                PersistedMessageReader pmr = new PersistedMessageReader();
                wf = pmr.readPersistedMessage(new File(filepath));
                showNewMessagePanelAsMessageCopy(pmr.recreateMessagefromReadData(session));
                PersistedMessageReader.clearDir(wf);
                wf = null;

              }
            } catch (Exception ex) {
                PersistedMessageReader.clearDir(wf);
                popupErrorMessageDialog(ex);
            }
        }
    }

    class NewMessageClearListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            cleanupNewMessagePanelObjects();

            showNewMessagePanel(true);
            mbodyPanel.textArea.updateUI();
        }
    }

    class FileChooserActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            if (!e.getActionCommand().equals("ApproveSelection")) {
                fcframe.setVisible(false);
                return;
            }

            File selectedfile = mfilechooser.getSelectedFile();

            if (selectedfile != null && selectedfile.exists()) {
                fcframe.setVisible(false);
                if (e.getActionCommand().equals("ApproveSelection")) {
                    mfilepath.setText(selectedfile.getAbsolutePath());
                    long fkirobyte = 0;
                    if (selectedfile.length() > 0) {
                        fkirobyte = selectedfile.length() / 1024;
                    }
                    cmessagefooter.setText(resources.getString("qkey.msg.msg078") + " " + fkirobyte + "KB");
                } else {
                    fcframe.setVisible(false);
                }
            }
        }
    }

    class NewMessageOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {


            //新メッセージ作成処理開始！
            
            //宛先名取得
            String dest_name = (String) matesakiBox1.getSelectedItem();
            //V2連絡エリア
            matesakiname = new JTextField();
            matesakiname.setText(dest_name);

            //宛先タイプ取得
            String dest_type = (String) mqBox.getSelectedItem();

            //body入力タイプ取得
            String bodyinputtype = (String)message_type.getSelectedItem();

            //入力タイプ別にボディ情報を入手
            if (bodyinputtype.equals(TEXTMESSAGE)) {
                //Text
                String data = mbodyPanel.textArea.getText();
            } else if (bodyinputtype.equals(BYTESMESSAGE)) {
                //File

                if (!mfilepath.getText().equals(resources.getString("qkey.msg.msg219"))) {

                    File ff = new File(mfilepath.getText());
                    if (!ff.exists()) {
                        cmessagefooter.setText(resources.getString("qkey.msg.msg079"));
                        return;
                    } else if (ff.isDirectory()) {
                        cmessagefooter.setText(resources.getString("qkey.msg.msg080"));
                        return;
                    }

                }
            } 

            try {

                Integer.parseInt(soufukosu.getText().trim());

            } catch (Exception nfe) {

                cmessagefooter.setText(resources.getString("qkey.msg.msg081"));
                return;

            }
            
            //フラグリセット
            newmessage1stpanelok = true;
            newmessage1stpanel_user_props_ok = true;
            newmessage1stpanel_mapm_props_ok = true;
            newmessage1stpanel_sm_props_ok = true;

            last_jmsheader_validate_error = "";
            last_user_prop_validate_error = "";
            last_mapmessage_prop_validate_error = "";
            last_streammessage_prop_validate_error = "";
           

            
            hdce2.stopCellEditing();
            pdce1.stopCellEditing();
            pdce3.stopCellEditing();

            validateAllUserProperties();

            //エディットの確定とvalidate
            if (bodyinputtype.equals(MAPMESSAGE)) {
              if (mapmdce0 != null)
                mapmdce0.stopCellEditing();
              if (mapmdce3 != null)
                mapmdce3.stopCellEditing();
            } else if (bodyinputtype.equals(STREAMMESSAGE)) {
              if (smdce3 != null)
                smdce3.stopCellEditing();
            }

            //JMSヘッダのチェックOK
            if (newmessage1stpanelok) {

                //道のりは遠い・・・ユーザプロパティチェックOK
                //MAPMESSAGEの時はMAPMESSAGEプロパティチェックに通っていること
                if (newmessage1stpanel_user_props_ok) {

                    if (newmessage1stpanel_mapm_props_ok) {
                        if (newmessage1stpanel_sm_props_ok) {
                            showMessageSendConfirmation(bodyinputtype);
                        } else {

                        ArrayList ar = QBrowserUtil.parseDelimitedString(last_streammessage_prop_validate_error, MAGIC_SEPARATOR);
                        String errorcode = null;
                        String errorprop = null;
                        String errortype = null;
                        String errorvalue = null;

                        int count = 0;
                        for (int i = 0; i < ar.size(); i++) {
                            count++;
                            switch (count) {
                                case 1:
                                    errorcode = (String) ar.get(i);
                                case 2:
                                    errorprop = (String) ar.get(i);
                                case 3:
                                    errortype = (String) ar.get(i);
                                case 4:
                                    errorvalue = (String) ar.get(i);

                            }
                        }


                        cmessagefooter.setText(resources.getString("qkey.msg.msg178"));
                        TextArea ta = new TextArea("", 7, 50, TextArea.SCROLLBARS_BOTH);
                        ta.append(resources.getString("qkey.msg.msg237"));
                        ta.append(resources.getString("qkey.msg.err." + errorcode));
                        ta.append(errorprop + " (" + errortype + " " + resources.getString("qkey.msg.msg182") + ") = " + errorvalue + "\n");

                        popupMessageDialog(resources.getString("qkey.msg.msg178"), ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));


                        }
                    } else {
                        ArrayList ar = QBrowserUtil.parseDelimitedString(last_mapmessage_prop_validate_error, MAGIC_SEPARATOR);
                        String errorcode = null;
                        String errorprop = null;
                        String errortype = null;
                        String errorvalue = null;

                        int count = 0;
                        for (int i = 0; i < ar.size(); i++) {
                            count++;
                            switch (count) {
                                case 1:
                                    errorcode = (String) ar.get(i);
                                case 2:
                                    errorprop = (String) ar.get(i);
                                case 3:
                                    errortype = (String) ar.get(i);
                                case 4:
                                    errorvalue = (String) ar.get(i);

                            }
                        }


                        cmessagefooter.setText(resources.getString("qkey.msg.msg178"));
                        TextArea ta = new TextArea("", 7, 50, TextArea.SCROLLBARS_BOTH);
                        ta.append(resources.getString("qkey.msg.msg226"));
                        ta.append(resources.getString("qkey.msg.err." + errorcode));
                        ta.append(errorprop + " (" + errortype + " " + resources.getString("qkey.msg.msg182") + ") = " + errorvalue + "\n");

                        popupMessageDialog(resources.getString("qkey.msg.msg178"), ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));

                    }

                } else {
                    ArrayList ar = QBrowserUtil.parseDelimitedString(last_user_prop_validate_error, MAGIC_SEPARATOR);
                    String errorcode = null;
                    String errorprop = null;
                    String errortype = null;
                    String errorvalue = null;

                    int count = 0;
                    for (int i = 0; i < ar.size(); i++) {
                        count++;
                        switch (count) {
                            case 1:
                                errorcode = (String) ar.get(i);
                            case 2:
                                errorprop = (String) ar.get(i);
                            case 3:
                                errortype = (String) ar.get(i);
                            case 4:
                                errorvalue = (String) ar.get(i);

                        }
                    }


                    cmessagefooter.setText(resources.getString("qkey.msg.msg178"));
                    TextArea ta = new TextArea("", 7, 50, TextArea.SCROLLBARS_BOTH);
                    ta.append(resources.getString("qkey.msg.msg180"));
                    ta.append(resources.getString("qkey.msg.err." + errorcode));
                    ta.append(errorprop + " (" + errortype + " " + resources.getString("qkey.msg.msg182") + ") = " + errorvalue + "\n");

                    popupMessageDialog(resources.getString("qkey.msg.msg178"), ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));

                }
            } else {
                //エラー構文
                ArrayList ar = QBrowserUtil.parseDelimitedString(last_jmsheader_validate_error, MAGIC_SEPARATOR);
                String errorcode = null;
                String errorprop = null;
                String errortype = null;
                String errorvalue = null;

                int count = 0;
                for (int i = 0; i < ar.size(); i++) {
                    count++;
                    switch (count) {
                        case 1:
                            errorcode = (String) ar.get(i);
                        case 2:
                            errorprop = (String) ar.get(i);
                        case 3:
                            errortype = (String) ar.get(i);
                        case 4:
                            errorvalue = (String) ar.get(i);

                    }
                }

                cmessagefooter.setText(resources.getString("qkey.msg.msg181"));
                TextArea ta = new TextArea("", 5, 50, TextArea.SCROLLBARS_NONE);
                ta.append(resources.getString("qkey.msg.msg179"));
                ta.append(resources.getString("qkey.msg.err." + errorcode));
                ta.append(errorprop + " = " + errorvalue + "\n");

                popupMessageDialog(resources.getString("qkey.msg.msg178"), ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));

            }

        }
    }

    class RefreshDestNames implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
             initDestinationList();
             cleanupNewMessagePanelObjects();
             newmessageFrame = null;
             subscribeDialog = null;
             initTreePane();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            /*
            if (metricSubscriber != null) {
                try {
                    reinitDestListConsumer();
                } catch (Throwable tx) {
                    //tx.printStackTrace();
                    System.err.println(tx.getMessage());
                }
            }
            */
        }
    }

    class SelectAllMessageListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {


            int tabindex = tabbedPane.getSelectedIndex();
            String tkey = tabbedPane.getTitleAt(tabindex);
            JTable cTable = (JTable) jtableins.get(tkey);


            if (cTable.getSelectedRows().length == cTable.getRowCount()) {
                cTable.clearSelection();
            } else {
                cTable.selectAll();
            }
        }
    }

    class VersionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            Version version = new Version();
            String ver = resources.getString("qkey.msg.msg082");
            TextArea ta = new TextArea();
            ta.setText("Written by Naoki Takemura\nPlease send feedback to naoki_takemura@hotmail.com\n" + version.toString());
            popupMessageDialog(ver, ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ClientVersion));

        }
    }

   class PauseAndResumeDestCmdListener implements ActionListener {

       String destType;
       String targetname;
       boolean isResume;

       public PauseAndResumeDestCmdListener(String vdestType, String vtargetname, boolean visResume) {

           destType = vdestType;
           targetname = vtargetname;
           isResume = visResume;
           if (destType.equals(QUEUE_LITERAL)) {
               destType = "q";
           } else if (destType.equals(TOPIC_LITERAL)) {
               destType = "t";
           }
        }

        public void actionPerformed(ActionEvent e) {

            confirmDialog.setVisible(false);
            //resume dst -t q -n ABCD
            String cmd_prefix = "pause dst -t ";
            if (isResume) {
                cmd_prefix = "resume dst -t ";
            }

            String cmd = cmd_prefix + destType + " -n " + targetname + " -f  -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                popupErrorMessageDialog(oe);
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            StringBuffer result = new StringBuffer();
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);


            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            int finalrowsize = restarray.size() + 3;

            if (finalrowsize > 19) {
                finalrowsize = 18;
            }

            /*
            TextArea ta = new TextArea("", finalrowsize, maxcolumnsize, TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ListAtesaki));
            */

        }
    }

    class ListDestCmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String cmd = "list dst -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                popupErrorMessageDialog(oe);
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            StringBuffer result = new StringBuffer();
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);


            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            int finalrowsize = restarray.size() + 3;

            if (finalrowsize > 19) {
                finalrowsize = 18;
            }

            TextArea ta = new TextArea("", finalrowsize, maxcolumnsize, TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ListAtesaki));

        }
    }

    class ListTxnCmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String cmd = "list txn -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);
            ar.add("-f");

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            StringBuffer result = new StringBuffer();
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);


            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            int finalrowsize = restarray.size() + 1;

            //トランザクション多数の場合は15行分のサイズに抑える
            if (finalrowsize > 16) {
                finalrowsize = 15;
            }

            TextArea ta = new TextArea("", finalrowsize, maxcolumnsize + 20, TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AllTxn));

        }
    }

    class ListCxnCmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String cmd = "list cxn -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            StringBuffer result = new StringBuffer();
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);


            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            int finalrowsize = restarray.size() + 1;

            //トランザクション多数の場合は15行分のサイズに抑える
            if (finalrowsize > 16) {
                finalrowsize = 15;
            }

            TextArea ta = new TextArea("", finalrowsize, maxcolumnsize + 20, TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConnList));

        }
    }

    class ListSvcCmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String cmd = "list svc -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

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
            StringBuffer result = new StringBuffer();
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);


            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            TextArea ta = new TextArea("", (restarray.size() + 1), maxcolumnsize + 20, TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.SvcList));

        }
    }

    class QuerySvcCmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String cmd = "query svc -n jms -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            StringBuffer result = new StringBuffer();
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);

            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }
            }

            TextArea ta = new TextArea("", (restarray.size() + 1), maxcolumnsize + 20, TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.SvcDetails));

        }
    }

    class ConfigPrinterListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            StringBuffer result =
                    BrokerUtil.printAllProperties(serverHost, String.valueOf(serverPort), "admin", "admin");

            JTextArea ta = new JTextArea();
            ta.setColumns(90);
            ta.setRows(30);

            ta.setText(result.toString());
            popupMessageDialog(resources.getString("qkey.msg.msg083"), createSearchableTextArea(ta),
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter));

        }
    }

    class QueryBkrCmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String cmd = "query bkr -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            StringBuffer result = new StringBuffer();
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);


            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            TextArea ta = new TextArea("", (restarray.size() - 3), maxcolumnsize + 25, TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.BkrDetails));

        }
    }



    class QueryDestCmdListener implements ActionListener {

        String internalruncommand(String destType, String targetname, StringBuffer result) {

            String cmd = "query dst -t " + destType + " -n " + targetname + " -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

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
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);
            return exitcode;
        }

        public void actionPerformed(ActionEvent e) {

            //現在コンボボックスで選択されている宛先名
            ComboBoxEditor editor = qBox.getEditor();
            String dispDest = (String) editor.getItem();
            String name = getPureDestName(dispDest);

            StringBuffer result = new StringBuffer();

            String dtype = "q";
            if (isDestNameTopic(dispDest))
            dtype = "t";

            String exitcode = internalruncommand(dtype , name, result);

            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            TextArea ta = new TextArea("", (restarray.size() - 3), (maxcolumnsize + 10), TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails));

        }
    }

    //for Tree
    class QueryDestCmdListener2 implements ActionListener {

        String internalruncommand(String destType, String targetname, StringBuffer result) {

            String cmd = "query dst -t " + destType + " -n " + targetname + " -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);

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
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);
            return exitcode;
        }

        public void actionPerformed(ActionEvent e) {

            TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();

            String dispDest = di.name_with_suffix;
            String name = di.destinationName;

            StringBuffer result = new StringBuffer();

            String dtype = "q";
            if (isDestNameTopic(dispDest))
            dtype = "t";

            String exitcode = internalruncommand(dtype , name, result);

            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            TextArea ta = new TextArea("", (restarray.size() - 3), (maxcolumnsize + 10), TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails));

        }
    }

    class PurgeDestOKListener implements ActionListener {

        String internalruncommand(String destType, String targetname, StringBuffer result) {

            String cmd = "purge dst -t " + destType + " -n " + targetname + " -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);
            ar.add("-f");

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);
            return exitcode;
        }

        public void actionPerformed(ActionEvent e) {

            purgedestconfirmDialog.setVisible(false);

            //現在コンボボックスで選択されている宛先名
            ComboBoxEditor editor = qBox.getEditor();
            String dispDest = (String) editor.getItem();
            String name = getPureDestName(dispDest);

            String dtype = "q";
            if (isDestNameTopic(dispDest))
            dtype = "t";

            StringBuffer result = new StringBuffer();
            String exitcode = internalruncommand(dtype , name, result);


            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }
            }

            TextArea ta = new TextArea("", (restarray.size() + 2), (maxcolumnsize + 10), TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            doBrowse();
            //popupMessageDialog("cmd", ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));

        }
    }

    class PurgeDestCmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            showPurgeDestConfirmation();

        }
    }

    class PurgeDestCmdListener2 implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            showPurgeDestConfirmation2();

        }
    }

    class FilteredListTxnCmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            showTxnFilter();

        }
    }

    class CmdListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            showCommandWindow();

        }
    }

    class NewLocalStoreListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

                LocalStoreConfigPanel lscp = new LocalStoreConfigPanel();
                lscp.showCreateLSConfigPanel(oya, lsm);

        }
    }

    class SearchListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            showSearchWindow();

        }
    }

    class ConnectionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {


            showConnectionWindow();

        }
    }

    public void moveToDisconnectStatus() {
            cleanupQBrowser();
            setNotConnected();
            connected = false;
            tree_location.remove(tree_location.getComponent(0));
            disconnect_item.setEnabled(false);
            connect_item.setEnabled(true);
            oya_frame.setTitle(QBrowserV2.title + " - " + resources.getString("qkey.msg.msg173"));
            setFooter(resources.getString("qkey.msg.msg165"));
            tree_location.remove(treePane);
            tree_location.updateUI();
    }

    class DisConnectionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            moveToDisconnectStatus();


        }
    }

    class SubscribeListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            showSubscribeWindow(null);

        }
    }

    class AddListenToLocalStoreListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            int selidx = tabbedPane.getSelectedIndex();
            final String dest_with_suffix = tabbedPane.getTitleAt(selidx);
            ArrayList local_dests = lsm.getCopyToListOfTheDestination(dest_with_suffix);
            final JComboBox rlsBox = new JComboBox();
            ArrayList local_store_names = lsm.getAllValidLocalStoreNames();
            for (int i = 0; i < local_store_names.size(); i++) {
                String lsname = (String) local_store_names.get(i);
                if (!local_dests.contains(lsname)) {
                    rlsBox.addItem(lsname);
                }
            }
            rlsBox.setEditable(false);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel(resources.getString("qkey.msg.msg312"));
            panel.add(BorderLayout.NORTH, lbl);
            panel.add(BorderLayout.CENTER, rlsBox);

            popupConfirmationDialog(resources.getString("qkey.msg.msg313"), panel, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Forward),
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            confirmDialog.dispose();
                            confirmDialog = null;

                            try {
                                lsm.addDestCopySubscriptionToLocalStore((String) rlsBox.getSelectedItem(),
                                        dest_with_suffix, "");
                                initTreePane();

                                copyToLocalStoreListItem.getActionListeners()[0].actionPerformed(event);
                                
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });



        }
    }

    class LocalStoreSubscriptionListListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            int selidx = tabbedPane.getSelectedIndex();
            final String dest_with_suffix = tabbedPane.getTitleAt(selidx);
            JPanel tp = new JPanel();
            final MessageRecordTable mrt = new MessageRecordTable();
            final JTable mrTable = new JTable(mrt);
            configMRTable(mrTable);
            JScrollPane tablePane = new JScrollPane(mrTable);
            mrTable.setBackground(Color.WHITE);
            tp.add(tablePane);
            final String local_dest_without_suffix = getPureDestName(dest_with_suffix);

            LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_dest_without_suffix);

            Iterator ilsp = lsp.getFromDests().keySet().iterator();
            while (ilsp.hasNext()) {
                MessageRecordProperty mrp = new MessageRecordProperty();
                String listen_dest_name_with_suffix = (String) ilsp.next();
                mrp.setDestName(listen_dest_name_with_suffix);

                String thread_status_string = null;

                Boolean isRunningT = (Boolean) subscribe_thread_status.get(listen_dest_name_with_suffix);
                if (isRunningT == null) {
                    thread_status_string = resources.getString("qkey.msg.msg320");
                } else if (isRunningT.booleanValue()) {
                    thread_status_string = resources.getString("qkey.msg.msg309");
                } else {
                    thread_status_string = resources.getString("qkey.msg.msg310");
                }
                mrp.setConsumerThreadStatus(thread_status_string);
                mrp.setCount(lsp.getEachCount(listen_dest_name_with_suffix));

                JButton jbt = new JButton(resources.getString("qkey.msg.msg316"));
                jbt.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent event) {
                        //ターゲットのローカルストアから、この宛先を削除する
                        try {
                            Integer iiv = (Integer) (((JButton) event.getSource()).getClientProperty(QBrowserV2.QBBUTTONROWPOSITION));
                            int real_row = mrTable.convertRowIndexToModel(iiv.intValue());
                            MessageRecordProperty mrp2 = mrt.getPropertyAtRow(real_row);

                            LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_dest_without_suffix);


                            String target_dest_with_suffix = (String) mrt.getValueAt(real_row, 0);
                            //System.out.println("target_dest_with_suffix " + target_dest_with_suffix);
                            lsp.removeFromDests(target_dest_with_suffix);
                            lsm.updateAndSaveLocalStoreProperty(lsp);
                            mrt.deletePropertyAtRow(real_row);
                            lsm.removeDestCopySubscriptionToLocalStore(local_dest_without_suffix, target_dest_with_suffix);
                            initTreePane();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mrp.setButton(jbt);
                mrt.add_one_row(mrp);

            }

            popupMessageDialog(resources.getString("qkey.msg.msg318") + local_dest_without_suffix + resources.getString("qkey.msg.msg319"), tp, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConnList));
        }
    }

    class AddLocalStoreSubscriptionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            int selidx = tabbedPane.getSelectedIndex();
            final String dest_with_suffix = tabbedPane.getTitleAt(selidx);
            //今このローカルストアがリスンしているsuffix付宛先一覧

            final String local_dest_without_suffix = getPureDestName(dest_with_suffix);
            LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_dest_without_suffix);

            ArrayList listen_dests = new ArrayList();
            Iterator ilsp = lsp.getFromDests().keySet().iterator();
            while (ilsp.hasNext()) {
                MessageRecordProperty mrp = new MessageRecordProperty();
                String listen_dest_name_with_suffix = (String) ilsp.next();
                listen_dests.add(listen_dest_name_with_suffix);
            }

            final JComboBox rlsBox = new JComboBox();

            for (int i = 0; i < qBox.getItemCount(); i++) {
                String dest_in_qBox = (String) qBox.getItemAt(i);
                if (!listen_dests.contains(dest_in_qBox) &&
                        (isTopic(dest_in_qBox))) {
                    rlsBox.addItem(new String(dest_in_qBox));
                }
            }

            rlsBox.setEditable(false);
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel(resources.getString("qkey.msg.msg315"));
            JLabel lbl2 = new JLabel(resources.getString("qkey.msg.msg358"));
            JLabel lbl3 = new JLabel(resources.getString("qkey.msg.msg359"));
            JPanel lbl_container = new JPanel();
            lbl_container.setLayout(new BorderLayout());
            lbl_container.add(BorderLayout.NORTH, lbl);
            lbl_container.add(BorderLayout.CENTER, lbl2);
            lbl_container.add(BorderLayout.SOUTH, lbl3);
            panel.add(BorderLayout.NORTH, lbl_container);
            panel.add(BorderLayout.CENTER, rlsBox);

            popupConfirmationDialog(resources.getString("qkey.msg.msg314"), panel, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Forward),
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            confirmDialog.dispose();
                            confirmDialog = null;

                            if (rlsBox.getItemCount() != 0) {
                                try {
                                    lsm.addDestCopySubscriptionToLocalStore(local_dest_without_suffix, (String) rlsBox.getSelectedItem(), "");
                                    initTreePane();
                                    localstoreSubscriptionListItem.getActionListeners()[0].actionPerformed(event);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });



        }
    }


    class CopyToLocalStoreListListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            int selidx = tabbedPane.getSelectedIndex();
            final String dest_with_suffix = tabbedPane.getTitleAt(selidx);

            JPanel tp = new JPanel();

            final MessageRecordTable mrt = new MessageRecordTable();
            final JTable mrTable = new JTable(mrt);
            configMRTable(mrTable);
            JScrollPane tablePane = new JScrollPane(mrTable);
            tp.add(tablePane);

            ArrayList local_dests = lsm.getCopyToListOfTheDestination(dest_with_suffix);
            if (local_dests != null) {
                for (int i = 0; i < local_dests.size(); i++) {
                    String local_dest = (String) local_dests.get(i);
                    final MessageRecordProperty mrp = new MessageRecordProperty();
                    mrp.setDestName(local_dest);

                    String thread_status_string = null;

                    Boolean isRunningT = (Boolean) subscribe_thread_status.get(dest_with_suffix);
                    if (isRunningT == null) {
                        thread_status_string = resources.getString("qkey.msg.msg320");
                    } else if (isRunningT.booleanValue()) {
                        thread_status_string = resources.getString("qkey.msg.msg309");
                    } else {
                        thread_status_string = resources.getString("qkey.msg.msg310");
                    }

                    mrp.setConsumerThreadStatus(thread_status_string);
                    LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_dest);
                    mrp.setCount(lsp.getEachCount(dest_with_suffix));
                    JButton jbt = new JButton(resources.getString("qkey.msg.msg316"));
                    jbt.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            //ターゲットのローカルストアから、この宛先を削除する

                            LocalStoreProperty lsp = lsm.getLocalStoreProperty(mrp.getDestName());
                            lsp.removeFromDests(dest_with_suffix);
                            try {
                                lsm.updateAndSaveLocalStoreProperty(lsp);
                                Integer iiv = (Integer) (((JButton) event.getSource()).getClientProperty(QBrowserV2.QBBUTTONROWPOSITION));
                                mrt.deletePropertyAtRow(mrTable.convertRowIndexToModel(iiv.intValue()));
                                lsm.removeDestCopySubscriptionToLocalStore(mrp.getDestName(), dest_with_suffix);
                                initTreePane();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mrp.setButton(jbt);
                    mrt.add_one_row(mrp);
                }

            }

            popupMessageDialog(dest_with_suffix + resources.getString("qkey.msg.msg317"), tp, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConnList));
        }
    }

    class SelectAllMenuListener implements MenuListener {

        public void menuSelected(MenuEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
            checkAndchangeSelectAllMenuText();
        }

        public void menuDeselected(MenuEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void menuCanceled(MenuEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class SelectTabMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            

            //左クリックで更新、右クリックでメニュー


            if (SwingUtilities.isLeftMouseButton(e)) {

                int selindex = tabbedPane.getSelectedIndex();

                //System.out.println("mouseClicked : " + selindex);


                if (selindex != -1) {
                    String tab_title = tabbedPane.getTitleAt(selindex);

                    //タブはあるけど、既に宛先が消されてしまっているケースに対応
                    if (isDestNameInDestNameComboBox(tab_title)) {

                        if (jtableins.get(tab_title) != null) {
                            refreshTableOnCurrentSelectedTab();
                        }

                    } else {
                        //タブを消しちゃう
                        jtableins.remove(tab_title);
                        tabbedPane.remove(selindex);
                    }

                    String dtype = QBrowserV2.QUEUE_LITERAL;
                    if (isLocalStore(tab_title)) {
                        dtype = QBrowserV2.LOCAL_STORE_LITERAL;
                    } else if (isTopic(tab_title)) {
                        dtype = QBrowserV2.TOPIC_LITERAL;
                    }
                    TreePath ftp = treePane.findTreePath(QBrowserUtil.getPureDestName(tab_title), dtype);
                    if (ftp != null) {
                        treePane.getTree().setSelectionPath(ftp);
                    }

                }

                //右クリックはメニュー
            } else if (SwingUtilities.isRightMouseButton(e)) {


                int selindex = tabbedPane.getSelectedIndex();

                if (selindex != -1) {
                    String tab_title = tabbedPane.getTitleAt(selindex);

                    last_right_click_X = e.getX();
                    last_right_click_Y = e.getY();
                    if (isTopic(tab_title)) {

                        popupMenuXForTab.show(e.getComponent(), last_right_click_X, last_right_click_Y);
                    } else if (isLocalStore(tab_title)) {
                        popupMenuXForLSTab.show(e.getComponent(), last_right_click_X, last_right_click_Y);
                    } else if (isQueue(tab_title)) {
                        popupMenuXForQTab.show(e.getComponent(), last_right_click_X, last_right_click_Y);
                    }

                }
            }

        }

        public void mousePressed(MouseEvent e) {

            /*

            //左クリックで更新、右クリックでメニュー

            if (SwingUtilities.isLeftMouseButton(e)) {

                int selindex = tabbedPane.getSelectedIndex();
                System.out.println("mousePressed : " + selindex);

                if (selindex != -1) {
                    String tab_title = tabbedPane.getTitleAt(selindex);

                    //タブはあるけど、既に宛先が消されてしまっているケースに対応
                    if (isDestNameInDestNameComboBox(tab_title)) {

                        if (jtableins.get(tab_title) != null) {
                            refreshTableOnCurrentSelectedTab();
                        }

                    } else {
                        //タブを消しちゃう
                        jtableins.remove(tab_title);
                        tabbedPane.remove(selindex);
                    }

                    String dtype = QBrowserV2.QUEUE_LITERAL;
                    if (isLocalStore(tab_title)) {
                        dtype = QBrowserV2.LOCAL_STORE_LITERAL;
                    } else if (isTopic(tab_title)) {
                        dtype = QBrowserV2.TOPIC_LITERAL;
                    }
                    TreePath ftp = treePane.findTreePath(QBrowserUtil.getPureDestName(tab_title), dtype);
                    if (ftp != null) {
                        treePane.getTree().setSelectionPath(ftp);
                    }

                }

                //右クリックはメニュー
            } else if (SwingUtilities.isRightMouseButton(e)) {


                int selindex = tabbedPane.getSelectedIndex();

                if (selindex != -1) {
                    String tab_title = tabbedPane.getTitleAt(selindex);

                    last_right_click_X = e.getX();
                    last_right_click_Y = e.getY();
                    if (isTopic(tab_title)) {

                        popupMenuXForTab.show(e.getComponent(), last_right_click_X, last_right_click_Y);
                    } else if (isLocalStore(tab_title)) {
                        popupMenuXForLSTab.show(e.getComponent(), last_right_click_X, last_right_click_Y);
                    } else if (isQueue(tab_title)) {
                        popupMenuXForQTab.show(e.getComponent(), last_right_click_X, last_right_click_Y);
                    }

                }
            }
*/
        }

        public void mouseReleased(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mouseEntered(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mouseExited(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class SelectAllButtonMouseListener implements MouseListener {

        public void mouseClicked(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mousePressed(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mouseReleased(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void mouseEntered(MouseEvent e) {
            checkAndchangeSelectAllMenuText();
        }

        public void mouseExited(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class FilterTxnOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            filterTxnDialog.setVisible(false);

            String name = (String) txnStateBox.getSelectedItem();
            int filtervalue = convertTxnStateStringtoInt(name);

            String cmd = "list abtxn -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);
            ar.add("-f");

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            cmdRunner.setFilter_transactionstate(filtervalue);

            StringBuffer result = new StringBuffer();
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);


            String resultstr = result.toString();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            int finalrowsize = restarray.size() + 1;

            //トランザクション多数の場合は15行分のサイズに抑える
            if (finalrowsize > 16) {
                finalrowsize = 15;
            }

            TextArea ta = new TextArea("", finalrowsize, maxcolumnsize + 20, TextArea.SCROLLBARS_BOTH);

            ta.setText(result.toString());
            popupMessageDialog("cmd", ta, this,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.FilteredTxn));

        }
    }

    class FilterTxnCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            filterTxnDialog.setVisible(false);

        }
    }

    class ConfirmDialogCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            confirmDialog.setVisible(false);

        }
    }

    class SendAtesakiComboBoxItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == e.SELECTED) {
            String sel = (String)mqBox.getSelectedItem();
            if (sel.equals(TOPIC_LITERAL)) {
                 importTopicNamesToMATESAKIBOX1();
                 matesakiBox1.setEditable(true);
            } else if (sel.equals(QUEUE_LITERAL)) {
                 importQueueNamesToMATESAKIBOX1();
                 matesakiBox1.setEditable(true);
            } else if (sel.equals(LOCAL_STORE_LITERAL)) {
                 importLocalStoreNamesToMATESAKIBOX1();
            }

            }

            
        }

    }

    class SendForwardAtesakiComboBoxItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if (e.getStateChange() == e.SELECTED) {
            String sel = (String)forwardBox.getSelectedItem();
            if (sel.equals(TOPIC_LITERAL)) {
                 importTopicNamesToMATESAKIBOX2();
                 matesakiBox2.setEditable(true);
            } else if (sel.equals(QUEUE_LITERAL)) {
                 importQueueNamesToMATESAKIBOX2();
                 matesakiBox2.setEditable(true);
            } else if (sel.equals(LOCAL_STORE_LITERAL)) {
                 importLocalStoreNamesToMATESAKIBOX2();
            }

            }
        }
    }

    class CmdRunnerThread extends Thread {

        QBrowserV2 oya = null;
        private JTextArea ta;
        public jp.sun.util.CmdRunner2 cmdRunner;

        public CmdRunnerThread(JTextArea value, QBrowserV2 value2) {
            ta = value;
            oya = value2;

        }

        private boolean checkDups(String hikaku) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) cmdTemplateBox.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                String key = (String) model.getElementAt(i);
                if (key.trim().equals(hikaku.trim())) {
                    return false;
                }
            }
            return true;
        }

        public void run() {
            //入力されたコマンド
            String inputcmd = cmdtextfield.getText();

            //String cmd = "list abtxn -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile .\\passfile.txt" + " -f";
            StringTokenizer st = new StringTokenizer(inputcmd);
            ArrayList ar = new ArrayList();

            int count = 0;

            int serverHostIndex = -1;
            int userIndex = -1;
            int passwordIndex = -1;

            while (st.hasMoreTokens()) {

                String tk = st.nextToken();
                if (tk.equalsIgnoreCase("-b")) {
                    serverHostIndex = count + 1;
                } else if (tk.equalsIgnoreCase("-u")) {
                    userIndex = count + 1;
                }

                ar.add(tk);
                count++;
            }

            //指定されてなかったら補完する
            if (serverHostIndex == -1) {
                ar.add("-b");
                ar.add(serverHost + ":" + serverPort);
            }

            if (userIndex == -1) {
                ar.add("-u");
                ar.add(oya.serverUser);
            }

            if (passwordIndex == -1) {
                ar.add("-passfile");
                ar.add(real_passfile_path);
            }

            String[] args = new String[ar.size()];
            ar.toArray(args);

            //
            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                TextArea ta = new TextArea("", 2, 30, TextArea.SCROLLBARS_NONE);
                ta.setText(resources.getString("qkey.msg.msg084"));
                popupMessageDialog("Error", ta,
                        QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.CmdInput));
                //既に開いてしまっている結果Windowを閉じる
                return;
            }

            //履歴にいれましょ。
            DefaultComboBoxModel model = (DefaultComboBoxModel) cmdTemplateBox.getModel();
            DefaultComboBoxModel model2 = (DefaultComboBoxModel) cmdTemplateBoxForSave.getModel();
            //重複チェック
            if (checkDups(cmdtextfield.getText())) {
                model.insertElementAt(cmdtextfield.getText(), 0);
                cmdTemplateBox.setSelectedIndex(0);
            }

            //ファイルに書き出すものについては、今回のコマンドならば重複チェックなし。
            model2.insertElementAt(cmdtextfield.getText(), 0);
            QBrowserUtil.saveHistoryToFile("command_history", QBrowserUtil.jcomboBoxToArrayList(cmdTemplateBoxForSave));


            cmdmsgDialog.setVisible(true);

            cmdRunner = new jp.sun.util.CmdRunner2(brokerCmdProps);

            //バッファ共有（しないと、順序バラバラで出てくる）
            jp.sun.util.BrokerCmdPrinter2.sb = cmdRunner.getInnerSyncStringBuffer();
            String exitcode = cmdRunner.runCommands(ta);


            String resultstr = ta.getText();
            StringTokenizer rest = new StringTokenizer(resultstr, "\n");
            ArrayList restarray = new ArrayList();
            int maxcolumnsize = 0;
            while (rest.hasMoreTokens()) {
                String key = rest.nextToken();
                restarray.add(key);
                if (key.length() > maxcolumnsize) {
                    maxcolumnsize = key.length();
                }

            }

            int finalrowsize = restarray.size() + 1;

            //トランザクション多数の場合は20行分のサイズに抑える
            if (finalrowsize > 20 || finalrowsize < 10) {
                finalrowsize = 20;
            }

            ta.setColumns(maxcolumnsize + 25);
            ta.setRows(finalrowsize);

            //おわり
            crthread = null;
            //Destroyとかで宛先がなくなるなどする可能性があるため
            if (inputcmd.indexOf("create") != -1 || inputcmd.indexOf("destroy") != -1) {
                try {
                    oya.initDestListConsumer();
                } catch (Exception oyae) {
                    System.err.println(oyae.getMessage());
                }
            } else if (inputcmd.indexOf("purge") != -1 || inputcmd.indexOf("commit") != -1 || inputcmd.indexOf("rollback") != -1) {

                oya.doBrowse();
            }

            cmdRunner.cancelFlusherTask();

        }
    }

    class CmdOKListener implements ActionListener {

        QBrowserV2 qbv2;

        public CmdOKListener(QBrowserV2 oya) {
            qbv2 = oya;
        }

        public void actionPerformed(ActionEvent e) {

            final JTextArea ta = new JTextArea();
            ta.setColumns(90);
            ta.setRows(30);
            final JDialog jd = popupCmdMessageDialog("cmd", createSearchableTextArea(ta), this);

            cleanupCommandThread();

            crthread = new CmdRunnerThread(ta, qbv2);
            crthread.start();

        }
    }

    class CmdCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            cmdDialog.setVisible(false);

        }
    }

    class SubscriberThread extends Thread {

        private SubscriberRunner subscribe_runner;

        public SubscriberThread(SubscriberRunner obj) {
            super(obj);
            this.subscribe_runner = obj;

        }

        @Override
        public void destroy() {

            if (subscribe_runner != null) {
                subscribe_runner.stopSubscribe();
            }
            //super.stop();
            try {
              subscribe_runner.sSubscriber.close();
              super.destroy();
            } catch (Throwable nsme) {
                //メソッドが消されている可能性あり。
                //System.err.println("nsme");
            }
        }
    }
    
    void copyConsumedMessageToLS(String local_store_name_without_suffix, MessageContainer orgmsgc) {
        try {
            LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_store_name_without_suffix);

            if (lsp == null) {
                return;
            }

            if (lsp.isValid()) {
                LocalStoreManager.LocalStore localstore = lsm.getLocalStoreInstance(local_store_name_without_suffix);
                Message copyto = copyMessage(orgmsgc.getMessage());
                String dest_name_with_suffix = local_store_name_without_suffix + LOCAL_STORE_SUFFIX;
                JTable cTable = (JTable) jtableins.get(dest_name_with_suffix);
                LocalMsgTable mt = null;

                LocalMessageContainer newlmc = new LocalMessageContainer();
                newlmc.setMessage(copyto);
                QBrowserUtil.populateHeadersOfLocalMessageContainer(orgmsgc, newlmc);
                
                newlmc.setVdest(convertVendorDestinationToLocalDestination(newlmc.getVdest()));
                newlmc.setVreplyto(convertVendorDestinationToLocalDestination(newlmc.getVreplyto()));
                newlmc.setDest_name_with_suffix(dest_name_with_suffix);

                if (cTable != null) {
                    mt = (LocalMsgTable) cTable.getModel();
                }

                File saved_message = localstore.localMessageToFile(session, newlmc, new StringBuilder(), new JDialog());
                LocalStoreManager.addMsgIndex(local_store_name_without_suffix, newlmc.getVmsgid(), saved_message.getAbsolutePath());

                if (mt != null) {
                    mt.add_one_row_ifexists_update(newlmc);
                }



            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SubscriberRunner implements Runnable {

        public String dest_full_name;
        //public int dest_tab_index;
        MessageConsumer sSubscriber;
        boolean running = true;
        int receive_count = 0;

        public void run() {
            JTable cTable = (JTable) jtableins.get(dest_full_name);
            MsgTable mt = (MsgTable) cTable.getModel();

            try {


                Topic cTopic = session.createTopic(getPureDestName(dest_full_name));
                sSubscriber = session.createConsumer(cTopic);

                while (running) {
                    try {
                        Message tmsg = sSubscriber.receive();
                        receive_count++;

                        if (running) {
                            MessageContainer mc = new MessageContainer();
                            mc.setMessage(tmsg);
                            mc.setVdest(convertVendorDestinationToLocalDestination(mc.getVdest()));
                            mc.setVreplyto(convertVendorDestinationToLocalDestination(mc.getVreplyto()));
                            mc.setDest_name_with_suffix(dest_full_name);
                            mt.add_one_row(mc);
                            reNumberCTable(cTable);
                            ArrayList local_copy_to = lsm.getCopyToListOfTheDestination(dest_full_name);
                            for (int i = 0 ; i < local_copy_to.size(); i++) {
                                String local_name_without_suffix = (String)local_copy_to.get(i);
                                copyConsumedMessageToLS(local_name_without_suffix, mc);
                                LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_name_without_suffix);
                                lsp.incrementEachCount(dest_full_name);
                            }
                            set_sub_button(dest_full_name);
                        }

                    } catch (Throwable te) {

                        System.err.println("SubscriberRunner while : " + te.getMessage());
                        break;

                    }
                }

                subscribe_thread_status.put(dest_full_name, new Boolean(false));
                set_sub_button(dest_full_name);

            } catch (Throwable gtx) {

                System.err.println(gtx.getMessage());
                subscribe_thread_status.put(dest_full_name, new Boolean(false));
                set_sub_button(dest_full_name);

            }

        }

        public void stopSubscribe() {
            running = false;
            if (sSubscriber != null) {
                try {
                    sSubscriber.close();
                } catch (Throwable tex) {
                    //NOP
                }
            }
        }
    }

    class SubscribeOKListener implements ActionListener {

        private boolean checkDups(String hikaku) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) subscribeTemplateBox.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                String key = (String) model.getElementAt(i);
                if (key.trim().equals(hikaku.trim())) {
                    return false;
                }
            }
            return true;
        }

        public void actionPerformed(ActionEvent e) {

            String compl_subscribename = complementTopicName((String)matesakiBox3.getSelectedItem());

            //コピー定義
            String copyto = (String)localstoreBox.getSelectedItem();
            if (!copyto.equals(resources.getString("qkey.msg.msg275"))) {
               try {
                lsm.addDestCopySubscriptionToLocalStore(copyto, compl_subscribename, "");
               } catch (Exception ie) {
                   ie.printStackTrace();
               }
            }

            int current_tab_index = 0;

            //まだタブがないとき
            //if (!isNamedTabAlreadyCreated(compl_subscribename)) {

            if(isNamedTabAlreadyCreated(compl_subscribename)) {
              int target_tab_index = tabbedPane.indexOfTab(compl_subscribename);
              jtableins.remove(compl_subscribename);
              tabbedPane.remove(target_tab_index);
            }

                //先にキャッシュにあるかを判定する
                JTable cTable = (JTable)jtableins.get(compl_subscribename);
                JTable taihiTable = new JTable(new MsgTable());

                //キャッシュにあるからといって、今は停止しているかも
                boolean subscriber_thread_current_running = isSubscriberThreadRunning(compl_subscribename);


                //キャッシュにある場合は、旧データを退避しておく
                if (cTable != null) {
                    tableCopy(cTable, taihiTable);
                }

                //新しいテーブルとタブを作成する
                current_tab_index = createNewMsgPane(compl_subscribename);


                //退避データがあるかどうかをチェック
                if (cTable == null) {
                    //退避データなし/初回なので、スレッドは起動状態で準備
                    cTable = (JTable)jtableins.get(compl_subscribename);
                    MsgTable mt = (MsgTable) cTable.getModel();
                    mt.init();
                    if (subscriber_thread_current_running) {
                        stopSubscriberThread(compl_subscribename);
                    }
                    createAndStartSubscriberThread(compl_subscribename);
                    addDestToMenu(compl_subscribename);
                    /*
                    DefaultComboBoxModel model = (DefaultComboBoxModel) subscribeTemplateBox.getModel();
                    //重複チェック
                    if (checkDups((String) matesakiBox3.getSelectedItem())) {
                      model.insertElementAt((String) matesakiBox3.getSelectedItem(), 0);
                      subscribeTemplateBox.setSelectedIndex(0);
                    }
                    */

                } else {
                    //System.out.println("退避データあり：要復旧");
                    cTable = (JTable)jtableins.get(compl_subscribename);
                    tableCopy(taihiTable, cTable);
                    jtableins.put(compl_subscribename, cTable);
                    restartSubscriberThreadAlongWithCurrentStatus(compl_subscribename);

                }

                tabbedPane.setSelectedIndex(current_tab_index);


//            } else {
//                current_tab_index = tabbedPane.indexOfTab(compl_subscribename);
//                tabbedPane.setSelectedIndex(current_tab_index);
//            }


            DefaultComboBoxModel model = (DefaultComboBoxModel) subscribeTemplateBox.getModel();
            //重複チェック
            if (checkDups((String) matesakiBox3.getSelectedItem())) {
               model.insertElementAt((String) matesakiBox3.getSelectedItem(), 0);
               subscribeTemplateBox.setSelectedIndex(0);
               subscribeTemplateBox.updateUI();
            }

            QBrowserUtil.saveHistoryToFile("subscription_history", QBrowserUtil.jcomboBoxToArrayList(subscribeTemplateBox));

            subscribeDialog.setVisible(false);
            ifnotyetDestNameInTopicDisplayBoxThenAdd((String)matesakiBox3.getSelectedItem());
            qBox.setSelectedItem(compl_subscribename);
            initTreePane();
            refreshMsgTableWithDestName();

        }
    }

    class SearchOKListener implements ActionListener {

        QBrowserV2 qbv2;
        MsgTable mt;

        public SearchOKListener(QBrowserV2 oya) {
            qbv2 = oya;
        }

        private boolean checkDups(String hikaku) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) searchTemplateBox.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                String key = (String) model.getElementAt(i);
                if (key.trim().equals(hikaku.trim())) {
                    return false;
                }
            }
            return true;
        }

        public void actionPerformed(ActionEvent e) {
            if (searchtextfield == null) {
                selector = null;
            } else if (searchtextfield.getText().length() == 0) {
                selector = null;
            } else {
                selector = searchtextfield.getText();
                //履歴にいれましょ。
                DefaultComboBoxModel model = (DefaultComboBoxModel) searchTemplateBox.getModel();
                //重複チェック
                if (checkDups(searchtextfield.getText())) {
                    model.insertElementAt(searchtextfield.getText(), 0);
                    searchTemplateBox.setSelectedIndex(0);
                }

                //ファイルに書き出すものについては、今回のコマンドならば重複チェックなし。
                QBrowserUtil.saveHistoryToFile("search_history", QBrowserUtil.jcomboBoxToArrayList(searchTemplateBox));
            }
            String selectedDest = (String) tqBox.getSelectedItem();
            //System.err.println("selectedDest : " + selectedDest);

            //宛先が空欄の場合は全部のキューから探してきて、件数のみ表示する
            //Selector文字列は空白でない場合のみ。
            if (((selectedDest == null) || (selectedDest.length() == 0)) && ((selector != null) && (selector.length() != 0))) {

               SwingUtilities.invokeLater(new Runnable() {

                    public void run() {
                        TextArea ta = new TextArea("", 10, 59, TextArea.SCROLLBARS_BOTH);
                        ta.setEditable(true);

                        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
                        ArrayList resultcol = new ArrayList();
                        int total_found = 0;

                        for (int i = 0; i < tqBox.getItemCount(); i++) {
                            String tqdest = getPureDestName((String) tqBox.getItemAt(i));

                            int kensu = searchBrowse(tqdest, selector);
 
                            if (kensu != 0) {
                                total_found += kensu;
                                String found = tqdest + " " + resources.getString("qkey.msg.msg085") + " " + kensu + " " + resources.getString("qkey.msg.msg086") + "\n";
                                ta.append(found);
                                resultcol.add(found);
                                for (int j = 0; j < kensu; j++) {
                                    MessageContainer msg = mt.getMessageAtRow(j);
                                   
                                        ta.append("  " + msg.getVmsgid() + "\n");


                                }

                            }
                        }

                        ta.append(resources.getString("qkey.msg.msg087") + "\n");
                        ta.append(resources.getString("qkey.msg.msg088"));
                        ta.append(resources.getString("qkey.msg.msg089") + selector + "\n\n");
                        for (int k = 0; k < resultcol.size(); k++) {
                            ta.append((String) resultcol.get(k));
                        }
                        ta.append(resources.getString("qkey.msg.msg090") + " " + total_found + " " + resources.getString("qkey.msg.msg091") + "\n");
                        ta.append(resources.getString("qkey.msg.msg092"));
                        popupMessageDialog(resources.getString("qkey.msg.msg093"), ta,
                                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.AtesakiDetails));
                        ta.setCaretPosition(ta.getText().length());

                    }
                });

            } else {
                ComboBoxEditor editor = qBox.getEditor();
                editor.setItem(selectedDest);
                doBrowse();
            }
            searchDialog.setVisible(false);
        }

        int searchBrowse(String destname, String sel) {
            int returnvalue = 0;
            try {
                Queue q = session.createQueue(destname);
                QueueBrowser qb;
                qb = session.createBrowser(q, sel);

                // Load messages into table
                mt = new MsgTable();
                ArrayList tc = new ArrayList();
                Enumeration emt = qb.getEnumeration();
            while (emt.hasMoreElements()) {
                Message imsg = (Message)emt.nextElement();
                MessageContainer mc = new MessageContainer();
                mc.setMessage(imsg);
                mc.setDest_name_with_suffix(destname + QUEUE_SUFFIX);

                try {
                 mc.setVdest(convertVendorDestinationToLocalDestination(imsg.getJMSDestination()));
                 mc.setVreplyto(convertVendorDestinationToLocalDestination(imsg.getJMSReplyTo()));
                } catch (Exception mce) { mce.printStackTrace();}

                tc.add(mc);
            }
                returnvalue = mt.load(tc);
                qb.close();
            } catch (JMSException jmse) {
                //jmse.printStackTrace();
                //NOP
            }

            return returnvalue;
        }
    }

    class SearchCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            searchDialog.setVisible(false);

        }
    }

    class SelectLS_SubMenuListener implements ActionListener {

        String target_local_store_name;

        public SelectLS_SubMenuListener(String lsname) {
            target_local_store_name = lsname;
        }

        public void actionPerformed(ActionEvent e) {

            qBox.setSelectedItem(target_local_store_name + LOCAL_STORE_SUFFIX);

        }
    }

    class ConnectionOKListener implements ActionListener {


        public ConnectionOKListener() {

        }

        private boolean checkDups(String hikaku) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) connectionTemplateBox.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                String key = (String) model.getElementAt(i);
                if (key.trim().equals(hikaku.trim())) {
                    model.removeElementAt(i);
                    return false;
                }
            }
            return true;
        }

        //パスワードは履歴にいれません。
        private String generateTemplateString(String host, int port, String user) {
            //"host = localhost port = 7676 user = admin password = admin "

            if (user != null) {
              return "host = " + host + " port = " + port + " user = " + user;
            } else {
              return "host = " + host + " port = " + port;
            }

        }

        public void actionPerformed(ActionEvent e) {
            if ((connectiontext_host != null) && (connectiontext_port != null)) {

                String new_host = connectiontext_host.getText();
                String new_port = connectiontext_port.getText();
                String new_user = connectiontext_user.getText();
                String new_password = new String(connectiontext_password.getPassword());
                //ここに再接続ロジックを書く。
                serverHost = new_host;

                try {
                    serverPort = Integer.parseInt(new_port);

                    if ((new_user == null) || (new_user.trim().length() == 0)) {
                        new_user = DEFAULT_BROKER_ADMIN_USER;
                    }

                    if ((new_password == null) || (new_password.trim().length() == 0)) {
                        new_password = DEFAULT_BROKER_PASSWORD;
                    }

                    serverUser = new_user;
                    serverPassword = new_password;
                    //コマンド用ファイル作成
                    QBrowserUtil.cleanupPassFile();
                    QBrowserUtil.createPassfile(serverPassword);
                    real_passfile_path = QBrowserUtil.getTargetPassfilePath();
                    //System.out.println(real_passfile_path);

                    try {
                        connect();
                    } catch (Exception conex) {
                        TextArea ta = new TextArea("", 8, 55, TextArea.SCROLLBARS_BOTH);

                        ta.setText(resources.getString("qkey.msg.msg171") + resources.getString("qkey.msg.msg172") + conex.getMessage());
                        popupMessageDialog(resources.getString("qkey.msg.msg170"), ta,
                                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));

                        return;
                    }

                    bkr_instance_name = retrieveBrokerInstanceName();
                    initTreePane();

                    //タブとあて先ボックスのクリア


                    //履歴にいれましょ。
                    DefaultComboBoxModel model = (DefaultComboBoxModel) connectionTemplateBox.getModel();
                    //重複チェック
                    String generatedTemplateString = generateTemplateString(serverHost, serverPort, new_user);

                    //成功した場合は、順番を入れ替える
                    checkDups(generatedTemplateString);
                    model.insertElementAt(generatedTemplateString, 0);
                    connectionTemplateBox.setSelectedIndex(0);
                    

                    QBrowserUtil.saveHistoryToFile("connect_history", QBrowserUtil.jcomboBoxToArrayList(connectionTemplateBox));

                    //doBrowse();
                    setConnected();
                    connected = true;
                    disconnect_item.setEnabled(true);
                    connect_item.setEnabled(false);
                    oya_frame.setTitle(QBrowserV2.title + " - " + bkr_instance_name + "(" + serverHost + ":" + serverPort + ") user=" + serverUser);



                } catch (Exception nfe) {
                    popupErrorMessageDialog(nfe);
                }
            }




            connectionDialog.setVisible(false);
        }
    }

    public void initTreePane() {
        if (treePane != null) {
          tree_location.remove(treePane);
        }

                    treePane = new TreeIconPanel(bkr_instance_name + "(" + serverHost + ":" + serverPort + ")", oya);
                    
                    JTree tree = treePane.getTree();

                    tree.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mousePressed(final MouseEvent e) {

                            if (treePane != null) {

                                final TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();

                                if (di != null) {
                                    if (SwingUtilities.isLeftMouseButton(e)) {

                                        //if (e.getClickCount() == 1) {

                                            if (!di.destinationType.equals("FD")) {
                                                qBox.setSelectedItem(di.name_with_suffix);
                                            }

                                       // }
                                    } else if (SwingUtilities.isRightMouseButton(e)) {

                                        if (di.destinationType.equals("FD")) {
                                            //ツリー上の宛先ポップアップメニュー用意
                                            if (di.destinationName.equals("LocalStore")) {
                                                popupMenuForLocalStoreFolder.show(e.getComponent(), e.getX(), e.getY());
                                            } else if (di.destinationName.equals("Queue")) {
                                                popupMenuForQueueFolder.show(e.getComponent(), e.getX(), e.getY());
                                            } else if (di.destinationName.equals("Topic")) {
                                                popupMenuForTopicFolder.show(e.getComponent(), e.getX(), e.getY());
                                            }

                                        } else if (di.destinationType.equals(TOPIC_LITERAL) ||
                                                     di.destinationType.equals(CHILD_TOPIC_LITERAL)) {

                                            popupMenuTForTopic.remove(subscribe_on_tree);
                                            popupMenuTForTopic.remove(addListenToLocalStoreItem2);
                                            popupMenuTForTopic.remove(copyToLocalStoreListItem2);
                                            popupMenuTForTopic.remove(pastemsgItem4);
                                            popupMenuTForTopic.remove(topic_separator1);

                                            Boolean isRunningT = (Boolean) subscribe_thread_status.get(di.name_with_suffix);
                                            if (isRunningT == null || !isRunningT.booleanValue()) {
                                                popupMenuTForTopic.add(subscribe_on_tree);
                                            } else {
                                                popupMenuTForTopic.add(addListenToLocalStoreItem2);
                                                popupMenuTForTopic.add(topic_separator1);
                                                popupMenuTForTopic.add(copyToLocalStoreListItem2);
                                            }

                                            if (remove_child_topic_itm != null) {
                                                popupMenuTForTopic.remove(remove_child_topic_itm);
                                            }

                                            if (pause_dest_itm != null) {
                                                popupMenuTForTopic.remove(pause_dest_itm);
                                            }
                                            if (resume_dest_itm != null) {
                                                popupMenuTForTopic.remove(resume_dest_itm);
                                            }


                                                pause_dest_itm = new JMenuItem(resources.getString("qkey.msg.msg371") +
                                                        di.destinationName + resources.getString("qkey.msg.msg372"),
                                                           QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Stopped));
                                                pause_dest_itm.addActionListener(new ActionListener() {

                                                    public void actionPerformed(ActionEvent e) {

                                                        JTextArea jta = new JTextArea("", 3, 25 + di.destinationName.length());
                                                        jta.setText(resources.getString("qkey.msg.msg374") + di.destinationName + resources.getString("qkey.msg.msg375"));
                                                        jta.setEditable(false);
                                                        jta.setBackground(Color.WHITE);

                                                        popupConfirmationDialog(resources.getString("qkey.msg.msg377"), jta,
                                                                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                                                                new PauseAndResumeDestCmdListener(di.destinationType, di.destinationName,false));

                                                    }
                                                });
                                                popupMenuTForTopic.add(pause_dest_itm);
                                            
                                                resume_dest_itm = new JMenuItem(resources.getString("qkey.msg.msg371") +
                                                        di.destinationName + resources.getString("qkey.msg.msg373"),
                                                           QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Playing));
                                                resume_dest_itm.addActionListener(new ActionListener() {

                                                    public void actionPerformed(ActionEvent e) {

                                                        JTextArea jta = new JTextArea("", 3, 25 + di.destinationName.length());
                                                        jta.setText(resources.getString("qkey.msg.msg374") + di.destinationName + resources.getString("qkey.msg.msg376"));
                                                        jta.setEditable(false);
                                                        jta.setBackground(Color.WHITE);

                                                        popupConfirmationDialog(resources.getString("qkey.msg.msg378"), jta,
                                                                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                                                                new PauseAndResumeDestCmdListener(di.destinationType, di.destinationName,true));

                                                    }
                                                });
                                                popupMenuTForTopic.add(resume_dest_itm);
                                           

                                            if (di.destinationType.equals(CHILD_TOPIC_LITERAL)) {
                                                remove_child_topic_itm = new JMenuItem(resources.getString("qkey.msg.msg380"),
                                                        QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));

                                                remove_child_topic_itm.addActionListener(new ActionListener() {

                                                    public void actionPerformed(ActionEvent event) {
                                                        //ターゲットのローカルストアから、この宛先を削除する
                                                        try {

                                                            String local_store_without_suffix = getPureDestName(di.parent_with_suffix);
                                                            LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_store_without_suffix);


                                                            String target_dest_with_suffix = di.name_with_suffix;
                                                            lsp.removeFromDests(target_dest_with_suffix);
                                                            lsm.updateAndSaveLocalStoreProperty(lsp);
                                                            lsm.removeDestCopySubscriptionToLocalStore(local_store_without_suffix, target_dest_with_suffix);
                                                            initTreePane();

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                                popupMenuTForTopic.add(remove_child_topic_itm);
                                            }

                                            if (cbm.hasClipBoardValidData()) {
                                                popupMenuTForTopic.add(pastemsgItem4);
                                            }

                                            popupMenuTForTopic.show(e.getComponent(), e.getX(), e.getY());
                                            
                                        } else if (di.destinationType.equals(QUEUE_LITERAL)) {

                                            popupMenuTForQueue.remove(pastemsgItem5);
                                            if (pause_dest_itm2 != null) {
                                                popupMenuTForQueue.remove(pause_dest_itm2);
                                            }
                                            if (resume_dest_itm2 != null) {
                                                popupMenuTForQueue.remove(resume_dest_itm2);
                                            }


                                            if (updateproperty_item != null)
                                            popupMenuTForQueue.remove(updateproperty_item);

                                            updateproperty_item = new JMenuItem(resources.getString("qkey.msg.msg381"),
                                                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.SvcDetails));




                                            updateproperty_item.addActionListener(new ActionListener() {

                                                public void actionPerformed(ActionEvent e) {

                                                    DestProperty dp = new DestProperty();
                                                    dp.setDestName(di.destinationName);
                                                    dp.setDestType(di.destinationType);

                                                    DestPropertyPanel dpPanel = new DestPropertyPanel();
                                                    dpPanel.showConfigPanel(dp, oya);

                                                }});

                                            
                                                pause_dest_itm2 = new JMenuItem(resources.getString("qkey.msg.msg371") +
                                                        di.destinationName + resources.getString("qkey.msg.msg372"),
                                                           QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Stopped));
                                                pause_dest_itm2.addActionListener(new ActionListener() {

                                                    public void actionPerformed(ActionEvent e) {

                                                        JTextArea jta = new JTextArea("", 3, 25 + di.destinationName.length());
                                                        jta.setText(resources.getString("qkey.msg.msg374") + di.destinationName + resources.getString("qkey.msg.msg375"));
                                                        jta.setEditable(false);
                                                        jta.setBackground(Color.WHITE);

                                                        popupConfirmationDialog(resources.getString("qkey.msg.msg377"), jta,
                                                                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                                                                new PauseAndResumeDestCmdListener(di.destinationType, di.destinationName,false));

                                                    }
                                                });
                                                popupMenuTForQueue.add(pause_dest_itm2);
                                            
                                                resume_dest_itm2 = new JMenuItem(resources.getString("qkey.msg.msg371") +
                                                        di.destinationName + resources.getString("qkey.msg.msg373"),
                                                           QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Playing));
                                                resume_dest_itm2.addActionListener(new ActionListener() {

                                                    public void actionPerformed(ActionEvent e) {

                                                        JTextArea jta = new JTextArea("", 3, 25 + di.destinationName.length());
                                                        jta.setText(resources.getString("qkey.msg.msg374") + di.destinationName + resources.getString("qkey.msg.msg376"));
                                                        jta.setEditable(false);
                                                        jta.setBackground(Color.WHITE);

                                                        popupConfirmationDialog(resources.getString("qkey.msg.msg378"), jta,
                                                                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                                                                new PauseAndResumeDestCmdListener(di.destinationType, di.destinationName,true));

                                                    }
                                                });
                                                popupMenuTForQueue.add(resume_dest_itm2);
                                            

                                            popupMenuTForQueue.add(updateproperty_item);



                                            if (cbm.hasClipBoardValidData()) {
                                                popupMenuTForQueue.add(pastemsgItem5);
                                            }

                                            popupMenuTForQueue.show(e.getComponent(), e.getX(), e.getY());

                                        } else if (di.destinationType.equals(LOCAL_STORE_LITERAL) ||
                                                     di.destinationType.equals(CHILD_LOCAL_STORE_LITERAL)) {

                                            popupMenuTForLocalStore.remove(pastemsgItem6);
                                            if (remove_child_local_store_itm != null) {
                                                popupMenuTForLocalStore.remove(remove_child_local_store_itm);
                                            }

                                            if (pause_localstore_itm != null) {
                                                popupMenuTForLocalStore.remove(pause_localstore_itm);
                                            }
                                            if (resume_localstore_itm != null) {
                                                popupMenuTForLocalStore.remove(resume_localstore_itm);
                                            }

                                            final LocalStoreProperty lsp = lsm.getLocalStoreProperty(di.destinationName);
                                            if (lsp.isValid()) {
                                                pause_localstore_itm = new JMenuItem(resources.getString("qkey.msg.msg371") +
                                                        di.destinationName + resources.getString("qkey.msg.msg372"),
                                                           QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Stopped));
                                                pause_localstore_itm.addActionListener(new ActionListener() {

                                                    public void actionPerformed(ActionEvent e) {

                                                        JTextArea jta = new JTextArea("", 3, 25 + di.destinationName.length());
                                                        jta.setText(resources.getString("qkey.msg.msg374") + di.destinationName + resources.getString("qkey.msg.msg375"));
                                                        jta.setEditable(false);
                                                        jta.setBackground(Color.WHITE);

                                                        popupConfirmationDialog(resources.getString("qkey.msg.msg377"), jta,
                                                                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                                                                new ActionListener() {

                                                            public void actionPerformed(ActionEvent e) {
                                                                confirmDialog.dispose();
                                                                confirmDialog = null;
                                                                lsp.setValid(false);
                                                                try {
                                                                  lsm.updateAndSaveLocalStoreProperty(lsp);
                                                                } catch (Exception savee) {
                                                                    popupErrorMessageDialog(savee);
                                                                }

                                                            }

                                                        });

                                                    }
                                                });
                                                popupMenuTForLocalStore.add(pause_localstore_itm);
                                            } else {
                                                resume_localstore_itm = new JMenuItem(resources.getString("qkey.msg.msg371") +
                                                        di.destinationName + resources.getString("qkey.msg.msg373"),
                                                           QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Playing));
                                                resume_localstore_itm.addActionListener(new ActionListener() {

                                                    public void actionPerformed(ActionEvent e) {

                                                        JTextArea jta = new JTextArea("", 3, 25 + di.destinationName.length());
                                                        jta.setText(resources.getString("qkey.msg.msg374") + di.destinationName + resources.getString("qkey.msg.msg376"));
                                                        jta.setEditable(false);
                                                        jta.setBackground(Color.WHITE);

                                                        popupConfirmationDialog(resources.getString("qkey.msg.msg378"), jta,
                                                                QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                                                                new ActionListener() {

                                                            public void actionPerformed(ActionEvent e) {
                                                                confirmDialog.dispose();
                                                                confirmDialog = null;
                                                                lsp.setValid(true);
                                                                try {
                                                                  lsm.updateAndSaveLocalStoreProperty(lsp);
                                                                } catch (Exception savee) {
                                                                    popupErrorMessageDialog(savee);
                                                                }

                                                            }

                                                        });

                                                    }
                                                });
                                                popupMenuTForLocalStore.add(resume_localstore_itm);
                                            }

                                            if (di.destinationType.equals(CHILD_LOCAL_STORE_LITERAL)) {

                                                remove_child_local_store_itm = new JMenuItem(resources.getString("qkey.msg.msg379"),
                                                        QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.EXIT));

                                                remove_child_local_store_itm.addActionListener(new ActionListener() {

                                                    public void actionPerformed(ActionEvent event) {
                                                        //ターゲットのローカルストアから、この宛先を削除する

                                                        LocalStoreProperty lsp = lsm.getLocalStoreProperty(di.destinationName);
                                                        lsp.removeFromDests(di.parent_with_suffix);
                                                        try {
                                                            lsm.updateAndSaveLocalStoreProperty(lsp);
                                                            lsm.removeDestCopySubscriptionToLocalStore(di.destinationName, di.parent_with_suffix);
                                                            initTreePane();

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });

                                                popupMenuTForLocalStore.add(remove_child_local_store_itm);
                                            }


                                            if (cbm.hasClipBoardValidData()) {
                                                popupMenuTForLocalStore.add(pastemsgItem6);
                                            }
                                            popupMenuTForLocalStore.show(e.getComponent(), e.getX(), e.getY());
                                        } else if (di.destinationType.equals("BKR")) {

                                            popupMenuForBrokerFolder.show(e.getComponent(), e.getX(), e.getY());

                                        }

                                    }
                                }
                            }

                        }
                    });
                    addDropTargetListenerToComponents(new QBrowserTreeDropTargetListener(), treePane);
                    treePane.setOpaque(true);
                    tree_location.add(treePane);
                    tree_location.updateUI();

    }


    class ConnectionCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            connectionDialog.setVisible(false);

        }
    }

    class SubscribeCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            subscribeDialog.setVisible(false);

        }
    }

    class ForwardOKListener implements ActionListener {

        boolean deleteSrcMessageAfterForward;

        public ForwardOKListener() {}

        public ForwardOKListener(boolean value) {
            deleteSrcMessageAfterForward = value;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

           try {

           String forward_target_name = (String)matesakiBox2.getSelectedItem();
           String forward_target_type = (String)forwardBox.getSelectedItem();
           int tabindex = tabbedPane.getSelectedIndex();
           String from_msg_table_with_suffix = tabbedPane.getTitleAt(tabindex);

           forwardMessage(ext_messages,from_msg_table_with_suffix,forward_target_name,forward_target_type,deleteSrcMessageAfterForward, true);

           qBox.removeItemListener(acbil);

                    DefaultComboBoxModel model = (DefaultComboBoxModel) qBox.getModel();
                    String compl_dest_name = convertFullDestName(forward_target_name, forward_target_type);
                    boolean found = false;
                    for (int i = 0; i < model.getSize(); i++) {
                        String key = (String) model.getElementAt(i);
                        if (key.trim().equals(compl_dest_name)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        if(!forward_target_type.equals(TOPIC_LITERAL)) {

                          model.insertElementAt(compl_dest_name, 0);
                          qBox.setSelectedItem(compl_dest_name);

                        }
                    } else {
                        //既にBOXに入っていた場合
                        qBox.setSelectedItem(compl_dest_name);
                    }


              refreshMsgTableWithDestName();
              qBox.addItemListener(acbil);
           } catch (Throwable tex) {
               popupErrorMessageDialog(tex);
           }

           forwardDialog.setVisible(false);

        }
    }

    class ForwardCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            forwardDialog.setVisible(false);

        }
    }

    class NewMessageCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            hideNewMessagePanel();
        }
    }
    
    
    class FileLoadingButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            showFileChooseWindow(resources.getString("qkey.msg.msg198"), 
                                 resources.getString("qkey.msg.msg199"),
                                 new FileChooseOKListener(e.getSource()));
        }
    }

    class FileLoadingButtonListener2 implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            showFileChooseWindow(resources.getString("qkey.msg.msg201"), 
                                 resources.getString("qkey.msg.msg202"),
                                 new FileChooseOKListener2(e.getSource()));
        }
    }

    class FileLoadingButtonListener3 implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            showFileChooseWindow(resources.getString("qkey.msg.msg198"),
                                 resources.getString("qkey.msg.msg199"),
                                 new FileChooseOKListener4(e.getSource()));
        }
    }

    class FileLoadingButtonListener4 implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            showFileChooseWindow(resources.getString("qkey.msg.msg201"),
                                 resources.getString("qkey.msg.msg202"),
                                 new FileChooseOKListener5(e.getSource()));
        }
    }



    class BrowseListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            refreshMsgTableWithDestName();
        }
    }

    class PasteActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            String cb_data = cbm.getClipBoardData();

            if (cb_data != null && cb_data.indexOf(QBrowserV2.MAGIC_SEPARATOR) != -1) {

                //リアルメッセージからのコピー
                if (cb_data.startsWith("RM@")) {

                    ext_messages = new ArrayList();

                    try {
                        ArrayList ar = QBrowserUtil.parseDelimitedString(cb_data.substring(3), QBrowserV2.MAGIC_SEPARATOR);
                        ArrayList targets = new ArrayList();

                        //最初は元ネタのsuffix付名称
                        String from_dest_with_suffix = (String) ar.get(1);
                        int selidx = tabbedPane.getSelectedIndex();
                        String dest_with_suffix = tabbedPane.getTitleAt(selidx);

                        //元ネタ
                        JTable cTable = (JTable) jtableins.get(from_dest_with_suffix);
                        TableModel mo = cTable.getModel();

                        if (mo instanceof LocalMsgTable) {
                            LocalMsgTable mt = (LocalMsgTable) mo;

                            int[] target = new int[ar.size() - 2];
                            targetX = new int[ar.size() - 2];

                            for (int i = 2; i < ar.size(); i++) {
                                String msgid = (String) ar.get(i);
                                int rst = mt.getRealRowNoFromMsgId(msgid);
                                target[i-2] = rst;
                                targetX[i-2] = rst;
                            }

                            for (int i = 0; i < target.length; i++) {
                                //コピーを入れる

                                ext_messages.add(mt.getMessageAtRow(target[i]));

                            }

                        } else if (mo instanceof MsgTable) {
                            MsgTable mt = (MsgTable) mo;

                            int[] target = new int[ar.size() - 2];
                            targetX = new int[ar.size() - 2];

                            for (int i = 2; i < ar.size(); i++) {
                                String msgid = (String) ar.get(i);
                                int rst = mt.getRealRowNoFromMsgId(msgid);
                                target[i-2] = rst;
                                targetX[i-2] = rst;
                            }

                            for (int i = 0; i < target.length; i++) {
                                //コピーを入れる

                                ext_messages.add(mt.getMessageAtRow(target[i]));

                            }
                        }

                        String target_name = getPureDestName(dest_with_suffix);

                        String target_type = QUEUE_LITERAL;
                        if (isTopic(dest_with_suffix)) {
                            target_type = TOPIC_LITERAL;
                        } else if (isLocalStore(dest_with_suffix)) {
                            target_type = LOCAL_STORE_LITERAL;
                        }

                        if ((target_name != null) && (target_type != null)) {
                            forwardMessage(ext_messages,from_dest_with_suffix,target_name, target_type, false, false);
                        }

                    } catch (Exception reade) {
                        popupErrorMessageDialog(reade);
                    }

                } else {

                    ArrayList ar = QBrowserUtil.parseDelimitedString(cb_data, QBrowserV2.MAGIC_SEPARATOR);

                    ArrayList targets = new ArrayList();
                    String target_name = null;
                    String target_type = null;

                    try {

                        for (int i = 1; i < ar.size(); i++) {
                            String path = (String) ar.get(i);
                            //ファイルに応じた読込み処理をおこなうべし。
                            int selidx = tabbedPane.getSelectedIndex();
                            String dest_with_suffix = tabbedPane.getTitleAt(selidx);
                            LocalMessageContainer lmc = new LocalMessageContainer(new File(path));
                            lmc.setDest_name_with_suffix(dest_with_suffix);
                            target_name = lmc.getPureDest_name();
                            target_type = lmc.getDest_type();
                            targets.add(lmc);

                        }

                    } catch (Exception reade) {
                        popupErrorMessageDialog(reade);
                    }

                    try {


                        int tabindex = tabbedPane.getSelectedIndex();
                        String from_msg_table_with_suffix = tabbedPane.getTitleAt(tabindex);
                        forwardMessage(targets,from_msg_table_with_suffix,target_name, target_type, false, false);



                    } catch (Exception ie) {
                        popupErrorMessageDialog(ie);
                    }

                }

            }

        //クリアしないようにした。
        //cbm.clearClipBoard();

        }
    }

    class PasteToTreeActionListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            String cb_data = cbm.getClipBoardData();

            if (cb_data != null && cb_data.indexOf(QBrowserV2.MAGIC_SEPARATOR) != -1) {

                //リアルメッセージからのコピー
                if (cb_data.startsWith("RM@")) {

                    ext_messages = new ArrayList();

                    try {
                        ArrayList ar = QBrowserUtil.parseDelimitedString(cb_data.substring(3), QBrowserV2.MAGIC_SEPARATOR);
                        ArrayList targets = new ArrayList();

                        //最初は元ネタのsuffix付名称
                        String from_dest_with_suffix = (String) ar.get(1);
                        TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();
                        String dest_with_suffix = di.name_with_suffix;

                        //元ネタ
                        JTable cTable = (JTable) jtableins.get(from_dest_with_suffix);
                        TableModel mo = cTable.getModel();

                        if (mo instanceof LocalMsgTable) {
                            LocalMsgTable mt = (LocalMsgTable) mo;

                            int[] target = new int[ar.size() - 2];
                            targetX = new int[ar.size() - 2];

                            for (int i = 2; i < ar.size(); i++) {
                                String msgid = (String) ar.get(i);
                                int rst = mt.getRealRowNoFromMsgId(msgid);
                                target[i-2] = rst;
                                targetX[i-2] = rst;
                            }

                            for (int i = 0; i < target.length; i++) {
                                //コピーを入れる

                                ext_messages.add(mt.getMessageAtRow(target[i]));

                            }

                        } else if (mo instanceof MsgTable) {
                            MsgTable mt = (MsgTable) mo;

                            int[] target = new int[ar.size() - 2];
                            targetX = new int[ar.size() - 2];

                            for (int i = 2; i < ar.size(); i++) {
                                String msgid = (String) ar.get(i);
                                int rst = mt.getRealRowNoFromMsgId(msgid);
                                target[i-2] = rst;
                                targetX[i-2] = rst;
                            }

                            for (int i = 0; i < target.length; i++) {
                                //コピーを入れる

                                ext_messages.add(mt.getMessageAtRow(target[i]));

                            }
                        }

                        String target_name = getPureDestName(dest_with_suffix);

                        String target_type = QUEUE_LITERAL;
                        if (isTopic(dest_with_suffix)) {
                            target_type = TOPIC_LITERAL;
                        } else if (isLocalStore(dest_with_suffix)) {
                            target_type = LOCAL_STORE_LITERAL;
                        }

                        if ((target_name != null) && (target_type != null)) {
                            forwardMessage(ext_messages,from_dest_with_suffix,target_name, target_type, false, false);
                        }

                    } catch (Exception reade) {
                        popupErrorMessageDialog(reade);
                    }

                } else {

                    ArrayList ar = QBrowserUtil.parseDelimitedString(cb_data, QBrowserV2.MAGIC_SEPARATOR);

                    ArrayList targets = new ArrayList();
                    String target_name = null;
                    String target_type = null;

                    try {

                        for (int i = 1; i < ar.size(); i++) {
                            String path = (String) ar.get(i);

                            //int selidx = tabbedPane.getSelectedIndex();
                            //String dest_with_suffix = tabbedPane.getTitleAt(selidx);
                            TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();
                            String dest_with_suffix = di.name_with_suffix;
                            LocalMessageContainer lmc = new LocalMessageContainer(new File(path));
                            lmc.setDest_name_with_suffix(dest_with_suffix);
                            target_name = lmc.getPureDest_name();
                            target_type = lmc.getDest_type();
                            targets.add(lmc);

                        }

                    } catch (Exception reade) {
                        popupErrorMessageDialog(reade);
                    }

                    try {


                        int tabindex = tabbedPane.getSelectedIndex();
                        String from_msg_table_with_suffix = tabbedPane.getTitleAt(tabindex);
                        forwardMessage(targets,from_msg_table_with_suffix,target_name, target_type, false, false);



                    } catch (Exception ie) {
                        popupErrorMessageDialog(ie);
                    }

                }

            }

        }
    }

    class DownloadListener implements ActionListener {

         String m_msgid;
         JLabel downloadmsg;
         JTextField downloadfilepath;

        public DownloadListener(String msgid) {
            m_msgid = msgid;
            downloadmsg = qbrowsercache.getCurrentDownloadMsgLabel(msgid);
            downloadfilepath = qbrowsercache.getCurrentDownloadFilePath(msgid);
        }


        public void actionPerformed(ActionEvent e) {

            downloadmsg.setText("");
            java.io.FileOutputStream fo = null;

            try {

                MessageContainer imc =
                qbrowsercache.getCurrentDownloadTargetMsg(m_msgid);

                //BytesMessage bm = (BytesMessage) currentDownloadTargetMsg.getMessage();
                BytesMessage bm = (BytesMessage)imc.getMessage();

                TextArea ta = new TextArea("", 5, 80, TextArea.SCROLLBARS_BOTH);

                ta.setEditable(false);
                ta.setBackground(Color.WHITE);

                    String dispid = e.getSource().toString();
                    DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg105"), ta,
                                  QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest), oya_frame);
                    Thread dprth = new Thread(dpr);
                    DisplayDialogThreadPoolForShowDetails.addDisplayThread(m_msgid, dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
                String datef = df.format(new Date(System.currentTimeMillis()));
                ta.append(resources.getString("qkey.msg.msg095") + bm.getJMSTimestamp() + resources.getString("qkey.msg.msg096"));
                ta.append("\n" + resources.getString("qkey.msg.msg097") + datef + "\n");

                bm.reset();

                File efile = new File(downloadfilepath.getText().trim());
                byte[] bibi = new byte[1024];

                fo = new FileOutputStream(efile);

                int len = 0;
                long readfilesize = 0;
                int count = 1000;

                while ((len = bm.readBytes(bibi)) != -1) {
                    fo.write(bibi, 0, len);
                    readfilesize += len;
                    if (++count > 1000) {
                        ta.append(readfilesize + " " + resources.getString("qkey.msg.msg098") + bm.getBodyLength() + " " + resources.getString("qkey.msg.msg099") + "\n");
                        ta.setCaretPosition(ta.getText().length());
                        count = 0;
                    }

                }

                ta.append(readfilesize + resources.getString("qkey.msg.msg100") + bm.getBodyLength() + resources.getString("qkey.msg.msg101") + "\n");
                ta.append(resources.getString("qkey.msg.msg102") + "\n");

            } catch (Exception ie) {
                popupErrorMessageDialog(ie);
                downloadmsg.setText(resources.getString("qkey.msg.msg103"));
            } finally {
                if (fo != null) {
                    try {
                    fo.close();
                    
                    } catch (IOException ioe) {
                        //
                    }
                    fo = null;
                }
            }



            downloadmsg.setText(resources.getString("qkey.msg.msg104"));
        }
    }

    class ErrorConfirmedListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            errDialog.setVisible(false);
        }
    }

    class MsgConfirmedListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            msgDialog.setVisible(false);
        }
    }
   

    class CmdMsgConfirmedListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            cmdmsgDialog.setVisible(false);
            cleanupCommandThread();
        }
    }

    class TableMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 2) {
                int tabindex = tabbedPane.getSelectedIndex();
                String tkey = tabbedPane.getTitleAt(tabindex);
                JTable cTable = (JTable) jtableins.get(tkey);
                int row = cTable.convertRowIndexToModel(cTable.getSelectedRow());
                if (row > -1) {
                    MsgTable mt = (MsgTable) cTable.getModel();
                    MessageContainer msg = mt.getMessageAtRow(row);
                    showDetails(msg, row);
                }
            }
        }
    }

    class LocalTableMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {

            if (e.getClickCount() == 2) {
                int tabindex = tabbedPane.getSelectedIndex();
                String tkey = tabbedPane.getTitleAt(tabindex);
                JTable cTable = (JTable) jtableins.get(tkey);
                int row = cTable.convertRowIndexToModel(cTable.getSelectedRow());
                if (row > -1) {
                    LocalMsgTable mt = (LocalMsgTable) cTable.getModel();
                    LocalMessageContainer msg = mt.getMessageAtRow(row);
                    showDetails(msg, row);
                }
            }
        }
    }

    class TFocusListener implements FocusListener {

        public void focusLost(FocusEvent e) {
        }

        public void focusGained(FocusEvent e) {
            cmessagefooter.setText("");
        }
    }

    class PurgeLSListener implements ActionListener {
                public void actionPerformed(ActionEvent e) {
                    int tabindex = tabbedPane.getSelectedIndex();

                    if (tabindex != -1) {
                    final String lsn_with_suffix = tabbedPane.getTitleAt(tabindex);
                    JTextArea jta = new JTextArea("", 3, 25 + lsn_with_suffix.length());
                    jta.setText(resources.getString("qkey.msg.msg300") + lsn_with_suffix + resources.getString("qkey.msg.msg305"));
                    jta.setEditable(false);
                    jta.setBackground(Color.WHITE);

                    popupConfirmationDialog(resources.getString("qkey.msg.msg301"), jta,
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                            new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    confirmDialog.setVisible(false);
                                    try {
                                        lsm.clearLocalStore(getPureDestName(lsn_with_suffix));
                                    } catch (IOException ioe) {
                                        popupErrorMessageDialog(ioe);
                                    }
                                    refreshLocalStoreMsgTableWithFileReloading(lsn_with_suffix);

                                }
                            });

                 }
                }
    }

    //for tree
    class PurgeLSListener2 implements ActionListener {
                public void actionPerformed(ActionEvent e) {
                    int tabindex = tabbedPane.getSelectedIndex();

                    if (tabindex != -1) {
                    //final String lsn_with_suffix = tabbedPane.getTitleAt(tabindex);
                    TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();
                    final String lsn_with_suffix = di.name_with_suffix;
                    JTextArea jta = new JTextArea("", 3, 25 + lsn_with_suffix.length());
                    jta.setText(resources.getString("qkey.msg.msg300") + lsn_with_suffix + resources.getString("qkey.msg.msg305"));
                    jta.setEditable(false);
                    jta.setBackground(Color.WHITE);

                    popupConfirmationDialog(resources.getString("qkey.msg.msg301"), jta,
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                            new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    confirmDialog.setVisible(false);
                                    try {
                                        lsm.clearLocalStore(getPureDestName(lsn_with_suffix));
                                    } catch (IOException ioe) {
                                        popupErrorMessageDialog(ioe);
                                    }
                                    refreshLocalStoreMsgTableWithFileReloading(lsn_with_suffix);

                                }
                            });

                 }
                }
    }

    //downloadfilepath
    class DetailsListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            int tabindex = tabbedPane.getSelectedIndex();
            String dispName = tabbedPane.getTitleAt(tabindex);
            JTable cTable = (JTable) jtableins.get(dispName);

            int row = cTable.getSelectedRow();
            if (row < 0) {
                setFooter(resources.getString("qkey.msg.msg227"));
                return;
            }

            row = cTable.convertRowIndexToModel(row);
            Object mobj = cTable.getModel();
            if (mobj instanceof MsgTable) {
              MsgTable mt = (MsgTable) cTable.getModel();
              MessageContainer msg = mt.getMessageAtRow(row);
              showDetails(msg, row);
            } else if (mobj instanceof LocalMsgTable) {
              LocalMsgTable mt = (LocalMsgTable) cTable.getModel();
              LocalMessageContainer msg = mt.getMessageAtRow(row);
              showDetails(msg, row);
            }

        }
    }

    class DeleteListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            int tabindex = tabbedPane.getSelectedIndex();
            String tkey = tabbedPane.getTitleAt(tabindex);
            JTable cTable = (JTable) jtableins.get(tkey);

            int[] rows = cTable.getSelectedRows();
            for (int i = 0; i < rows.length; i++) {
                rows[i] = cTable.convertRowIndexToModel(rows[i]);
            }


            if (rows.length == 0) {
                setFooter(resources.getString("qkey.msg.msg227"));
                return;
            }

            if (tkey.indexOf(LOCAL_STORE_SUFFIX) != -1) {
                //LocalStore
                showDeleteFromLocalStoreConfirmation(rows);
            } else
            if (tkey.indexOf(TOPIC_SUFFIX) != -1) {
                //Topic
                showDeleteFromTopicCacheConfirmation(rows);
            } else {
                //Queue
                showDeleteConfirmation(rows);
            }
        }
    }

    class CopyXListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            int tabindex = tabbedPane.getSelectedIndex();

            if (tabindex != -1) {

                String tkey = tabbedPane.getTitleAt(tabindex);
                collectCurrentSelectedRows();

                if (isLocalStore(tkey)) {

                    StringBuilder sb = new StringBuilder();
                    //選択されているメッセージをコレクト。
                    for (int i = 0; i < ext_messages.size(); i++) {
                        LocalMessageContainer lmc = (LocalMessageContainer) ext_messages.get(i);
                        sb.append(QBrowserV2.MAGIC_SEPARATOR).append(lmc.getReal_file_path());
                    }

                    cbm.copyToClipBoard(sb.toString());
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("RM@");
                    sb.append(QBrowserV2.MAGIC_SEPARATOR).append(tkey);

                    //選択されているメッセージをコレクト。
                    for (int i = 0; i < ext_messages.size(); i++) {
                        MessageContainer mc = (MessageContainer) ext_messages.get(i);
                        sb.append(QBrowserV2.MAGIC_SEPARATOR).append(mc.getVmsgid());
                    }

                    cbm.copyToClipBoard(sb.toString());
                }

            }

        }
    }
    

    class DeleteXListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            //System.err.println("DeleteXListener::actionperformed called.");
            collectCurrentSelectedRows();
            int tabindex = tabbedPane.getSelectedIndex();
            String tkey = tabbedPane.getTitleAt(tabindex);

            if (targetX != null) {

                if (tkey.indexOf(LOCAL_STORE_SUFFIX) != -1) {
                    //LocalStore
                    showDeleteFromLocalStoreConfirmation(targetX);
                } else
                if (tkey.indexOf(TOPIC_SUFFIX) != -1) {
                    //Topic
                    showDeleteFromTopicCacheConfirmation(targetX);
                } else {
                    //Queue
                    showDeleteConfirmation(targetX);
                }

            }
        }
    }

    class DeleteCleanup implements Runnable {

        MessageConsumer imc;

        public void run() {
            try {
                imc.close();
                imc = null;
            } catch (Throwable tex) {
                System.err.println(tex.getMessage());
            }
        }
    }

    class DisplayPropRunner implements Runnable {

        String mpkey;
        JTextArea ta;
        String source_id;
        JFrame oya;
        String m_msgid;

        public DisplayPropRunner(String msgid ,String mpkey_value, JTextArea ta_value, String source_id_value, JFrame voya) {
            m_msgid = msgid;
            mpkey = mpkey_value;
            ta = ta_value;
            source_id = source_id_value;
            oya = voya;
        }

        public void run() {

            try {

                JPanel curr_panel = qbrowsercache.getCurrentBodyPanel(m_msgid);
                //if ((curr_panel != null) && (curr_panel instanceof MapMessageAllPropertiesPanel)) {
                MapMessageAllPropertiesPanel mmap = (MapMessageAllPropertiesPanel)curr_panel;
                MapMessageAllPropertiesTable mapm_all_property_table = mmap.getMapm_all_property_table();

                String recovered_string = mapm_all_property_table.getInnerMessage().getString(mpkey);
                ta.setText(recovered_string);

                DisplayMsgDialogFactory.popupDisposalMessageDialog(m_msgid, resources.getString("qkey.msg.msg210"), createSearchableTextArea(ta),
                        QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter), oya);


                DisplayDialogThreadPoolForShowDetails.removeDisplayThread(m_msgid, this);

            } catch (Exception e) {
            }


        }

    }

    //ForStreamMessage
    class DisplayPropRunner2 implements Runnable {

        String m_msgid;
        String mpkey;
        JTextArea ta;
        String source_id;
        JComponent oya;


        public DisplayPropRunner2(String msgid,String mpkey_value, JTextArea ta_value, String source_id_value, JComponent voya) {
            m_msgid = msgid;
            mpkey = mpkey_value;
            ta = ta_value;
            source_id = source_id_value;
            oya = voya;
        }

        public void run() {

            try {

                JPanel curr_panel = qbrowsercache.getCurrentBodyPanel(m_msgid);
                //if ((curr_panel != null) && (curr_panel instanceof MapMessageAllPropertiesPanel)) {
                StreamMessageAllPropertiesPanel mmap = (StreamMessageAllPropertiesPanel)curr_panel;
                StreamMessageAllPropertiesTable sm_all_property_table = mmap.getStreamMessageAllPropertiesTable();

                StreamMessage msg = sm_all_property_table.getInnerMessage();
                String recovered_string = null;

                try {

                    msg.reset();

                    Object obj = null;
                    int rowcount = 0;

                    while ((obj = ((StreamMessage) msg).readObject()) != null) {

                        rowcount++;

                        String name = String.valueOf(rowcount);

                        if ((mpkey.equals(name)) && (obj instanceof String)) {
                            recovered_string = (String) obj;
                        }

                    }

                } catch (JMSException ex) {
                    //ex.printStackTrace();
                } catch (Throwable thex) {
                }

                ta.setText(recovered_string);

                DisplayMsgDialogFactory.popupDisposalMessageDialog(m_msgid, resources.getString("qkey.msg.msg210"), createSearchableTextArea(ta),
                        QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter), oya_frame);


                DisplayDialogThreadPoolForShowDetails.removeDisplayThread(m_msgid, this);

            } catch (Exception e) {
            }


        }
    }

    class DeleteOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    deleteconfirmDialog.setVisible(false);
                    TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
                    ta.setEditable(true);
                    int size = currentDeleteTarget.size();

                    int appropriaterowsize = size + 2;
                    if (appropriaterowsize > 15) {
                        appropriaterowsize = 15;
                    }

                    ta.setRows(appropriaterowsize);

                    //削除進捗情報
                    String dispid = "MessageDeletionProgress";
                    DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg105"), ta,
                                  QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest), oya_frame);
                    Thread dprth = new Thread(dpr);

                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
                    String datef = df.format(new Date(System.currentTimeMillis()));
                    ta.append(resources.getString("qkey.msg.msg106") + "\n");
                    ta.append(resources.getString("qkey.msg.msg107") + datef + "\n");
                    //2008-3-31 add transaction-seized message consumption timeout and error.
                    couldnotdelete = new ArrayList();

                    //int caretcount = 0;
                    StringBuffer mediumbuffer = new StringBuffer();

                    String msg108 = resources.getString("qkey.msg.msg108");
                    String msg109 = resources.getString("qkey.msg.msg109");
                    String msg110 = resources.getString("qkey.msg.msg110");
                    String msg111 = resources.getString("qkey.msg.msg111");


                    for (int i = 0; i < currentDeleteTarget.size(); i++) {
                        try {
                            //caretcount++;
                            MessageContainer msg = (MessageContainer) currentDeleteTarget.get(i);
                            //convertAllLocalDestinationInMessageToVendorDestination(msg);
                            String selector = "JMSMessageID ='" + msg.getVmsgid() + "'";
                            //String selector = "JMSTimestamp = " + msg.getJMSTimestamp();
                            MessageConsumer mc = session.createConsumer(convertLocalDestinationToVendorDestination(msg.getVdest()), selector, false);

                            Message delm = mc.receive(3000L);

                            DeleteCleanup dcp = new DeleteCleanup();
                            dcp.imc = mc;
                            Thread th1 = new Thread(dcp);
                            th1.start();

                            String msgid = msg.getVmsgid();

                            //2008-3-31 add transaction-seized message consumption timeout and error.
                            if (delm != null) {

                                mediumbuffer.append(msgid + " " + msg108 + (i + 1) + msg109 + size + msg110);

                                    //ta.append(mediumbuffer.toString());
                                    //ta.setCaretPosition(ta.getText().length());
                                    //mediumbuffer = new StringBuffer();

                            } else {
                                ta.append(msgid + msg111 + "\n");
                                couldnotdelete.add(msgid);
                            }

                        } catch (Exception ee) {
                         if ((ee.getMessage().indexOf("C4059") != -1) ||
                                (ee.getMessage().indexOf("C4056") != -1) ||
                                (session == null)) {
                                try {
                                    //接続を張りなおしてリトライ
                                    reconnect();
                                    this.run();
                                } catch (Exception recex) {
                                    //仏の顔は一度まで。
                                    popupErrorMessageDialog(recex);
                                }
                            } else {
                                popupErrorMessageDialog(ee);
                            }

                        }
                    } //roop end

                    ta.append(mediumbuffer.toString());
                    mediumbuffer = null;
                    ta.setCaretPosition(ta.getText().length());

                    if (couldnotdelete.isEmpty()) {

                        ta.append(resources.getString("qkey.msg.msg112"));

                    } else {
                        ta.append(resources.getString("qkey.msg.msg113") + "\n");
                        ta.append(resources.getString("qkey.msg.msg114") + "\n");
                        for (int j = 0; j < couldnotdelete.size(); j++) {
                            String cmsgid = (String) couldnotdelete.get(j);
                            ta.append(cmsgid + "\n");
                        }
                    }
                    
                    
                    currentDeleteTarget.clear();
                    refreshTableOnCurrentSelectedTab();
                }
            });


        }
    }

    class DeleteFromCacheOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    deleteconfirmDialog.setVisible(false);
                    TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
                    ta.setEditable(true);
                    int size = delete_from_cache_rows.length;

                    int appropriaterowsize = size + 2;
                    if (appropriaterowsize > 15) {
                        appropriaterowsize = 15;
                    }

                    ta.setRows(appropriaterowsize);

                    //削除進捗情報
                    //popupMessageDialog(resources.getString("qkey.msg.msg140"), ta,
                    //        QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest));
                    String dispid = "CachedMessageDeletionProgress";
                    DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg140"), ta,
                                  QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest), oya_frame);
                    Thread dprth = new Thread(dpr);
                    //display_threads.add(dprth);
                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
                    String datef = df.format(new Date(System.currentTimeMillis()));
                    ta.append(resources.getString("qkey.msg.msg106") + "\n");
                    ta.append(resources.getString("qkey.msg.msg107") + datef + "\n");

                    //int caretcount = 0;
                    StringBuffer mediumbuffer = new StringBuffer();

                    String msg108 = resources.getString("qkey.msg.msg108");
                    String msg109 = resources.getString("qkey.msg.msg109");
                    String msg110 = resources.getString("qkey.msg.msg110");

                    int tabindex = tabbedPane.getSelectedIndex();
                    String tkey = tabbedPane.getTitleAt(tabindex);
                    JTable cTable = (JTable) jtableins.get(tkey);
                    JTable taihiTable = new JTable(new MsgTable());

                    MsgTable mt = (MsgTable) cTable.getModel();
                    HashSet deletekey = new HashSet();

                    for (int i = 0; i < size; i++) {
                        try {
                            int tgt = delete_from_cache_rows[i];
                            deletekey.add(new Integer(tgt));
                            MessageContainer msg = mt.getMessageAtRow(tgt);

                            String msgid = msg.getVmsgid();
                            mediumbuffer.append(msgid + " " + msg108 + (i + 1) + msg109 + size + msg110);

                            //ta.append(mediumbuffer.toString());
                            //ta.setCaretPosition(ta.getText().length());
                            //mediumbuffer = new StringBuffer();


                        } catch (Exception ee) {

                            popupErrorMessageDialog(ee);

                        }
                    } //roop end

                    //like jvm gc...
                    tableCopyWithoutIndicatedRows(cTable, taihiTable, deletekey);
                    tableCopy(taihiTable, cTable);

                    ta.append(mediumbuffer.toString());
                    mediumbuffer = null;
                    ta.setCaretPosition(ta.getText().length());

                    ta.append(resources.getString("qkey.msg.msg141"));
                    doBrowseSubscriberCache(tabindex);
                    set_sub_button(tkey);

                }
            });


        }
    }

    class AtesakiInputListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if (e.getStateChange() == e.SELECTED) {
            String selitem = (String) matesakiBox1.getSelectedItem();
            if (selitem != null && selitem.length() > 0) {
              okbutton.setEnabled(true);
            } else {
              okbutton.setEnabled(false);
            }

            }

        }
    }

    class AtesakiComboBoxItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == e.SELECTED) {
              refreshMsgTableWithDestName();
            }
        }
    }

    class DeleteFromLocalStoreOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    deleteconfirmDialog.setVisible(false);
                    TextArea ta = new TextArea("", 10, 90, TextArea.SCROLLBARS_BOTH);
                    ta.setEditable(true);
                    int size = delete_from_cache_rows.length;

                    int appropriaterowsize = size + 5;
                    if (appropriaterowsize > 20) {
                        appropriaterowsize = 20;
                    }

                    ta.setRows(appropriaterowsize);

                    //削除進捗情報
                    String dispid = "LocalStoreDeletionProgress";
                    DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg263"), ta,
                                  QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.PurgeDest), oya_frame);
                    Thread dprth = new Thread(dpr);
                    DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
                    dprth.start();

                    while (!dpr.isStarted()) {
                     try {
                       Thread.sleep(100);
                     } catch (Throwable thex) {}
                     }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
                    String datef = df.format(new Date(System.currentTimeMillis()));
                    ta.append(resources.getString("qkey.msg.msg106") + "\n");
                    ta.append(resources.getString("qkey.msg.msg107") + datef + "\n");

                    StringBuffer mediumbuffer = new StringBuffer();

                    String msg108 = resources.getString("qkey.msg.msg108");
                    String msg109 = resources.getString("qkey.msg.msg109");
                    String msg110 = resources.getString("qkey.msg.msg110");

                    int tabindex = tabbedPane.getSelectedIndex();
                    String tkey = tabbedPane.getTitleAt(tabindex);
                    JTable cTable = (JTable) jtableins.get(tkey);
                    JTable taihiTable = new JTable(new LocalMsgTable());

                    LocalMsgTable mt = (LocalMsgTable) cTable.getModel();
                    HashSet deletekey = new HashSet();

                    for (int i = 0; i < size; i++) {
                        try {

                            int tgt = delete_from_cache_rows[i];
                            deletekey.add(new Integer(tgt));
                            LocalMessageContainer msg = mt.getMessageAtRow(tgt);

                            String msgid = msg.getVmsgid();
                             //ここで実ファイルも削除する
                            mt.msgids.remove(msgid);
                            msg.deleteRealMessageFile();
                            mediumbuffer.append(msgid + " " + msg108 + (i + 1) + msg109 + size + msg110);

                            //ta.append(mediumbuffer.toString());
                            //ta.setCaretPosition(ta.getText().length());
                            //mediumbuffer = new StringBuffer();


                        } catch (Exception ee) {

                            popupErrorMessageDialog(ee);

                        }
                    } //roop end

                    //like jvm gc...
                    localTableCopyWithoutIndicatedRows(cTable, taihiTable, deletekey);
                    localTableCopy(taihiTable, cTable);
                    

                    ta.append(mediumbuffer.toString());
                    mediumbuffer = null;
                    ta.setCaretPosition(ta.getText().length());
                    ta.append(resources.getString("qkey.msg.msg267"));

                    LocalMsgTable mta = (LocalMsgTable) cTable.getModel();
                    mta.fireTableDataChanged();
                    doBrowseLocalStore(tabindex);

                }
            });


        }
    }

    class CmdTemplateItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == e.SELECTED) {
            String selected = (String) cmdTemplateBox.getSelectedItem();
            cmdtextfield.setText(selected);
            }
        }
    }

    class UserPropertyTypeComboBoxItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if (e.getStateChange() == e.SELECTED) {
            String selected_type = (String) e.getItem();
            JComboBox sourcebox = (JComboBox) e.getSource();
            
            if (pdce1 != null)
                pdce1.stopCellEditing();

            int row_selected = property_table.findRowNumberFromJComboBox(sourcebox);

            if (row_selected != -1) {
                if (selected_type.equals(Property.BOOLEAN_TYPE)) {

                    JComboBox jcb = new JComboBox();
                    jcb.addItem("true");
                    jcb.addItem("false");
                    property_table.setValueAt(jcb,
                            row_selected, 2);
                    property_table.fireTableDataChanged();

                } else if (selected_type.equals(Property.STRING_TYPE)) {

                    Object gv = property_table.getValueAt(row_selected, 2);

                    if (gv instanceof JTextArea) {
                        //もうインスタンスが作られている
                    } else if (gv instanceof String) {
                        JTextArea newgv = new JTextArea(1,10);
                        newgv.setToolTipText(resources.getString("qkey.msg.msg230"));
                        newgv.setText((String)gv);
                        newgv.addMouseListener(new UserPropertyStringPropertyMouseListener());
                        gv = newgv;
                    } else {
                        JTextArea newgv = new JTextArea(1,10);
                        newgv.setToolTipText(resources.getString("qkey.msg.msg230"));
                        newgv.addMouseListener(new UserPropertyStringPropertyMouseListener());
                        gv = newgv;
                    }

                    property_table.setValueAt(gv,
                            row_selected, 2);
                } else {

                    Object gv = property_table.getValueAt(row_selected, 2);
                    if (gv instanceof JComboBox)
                      property_table.setValueAt("", row_selected, 2);
                      property_table.fireTableDataChanged();
                    
                }

            }
            }

        }
    }

    class MapMessageTypeComboBoxItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if (e.getStateChange() == e.SELECTED) {

            String selected_type = (String) e.getItem();
            JComboBox sourcebox = (JComboBox) e.getSource();
            
            int row_selected = mapm_property_table.findRowNumberFromJComboBox(sourcebox);

            if (row_selected != -1) {
                if (selected_type.equals("Bytes")) {

                    JButton jbt = new JButton(resources.getString("qkey.msg.msg207"));
                    jbt.addActionListener(new FileLoadingButtonListener());
                   

                    //FileLoadingButtonListener
                    mapm_property_table.setValueAt(jbt,
                            row_selected, 3);

                    Object gv = mapm_property_table.getValueAt(row_selected, 2);
                    if (gv instanceof JComboBox)
                    mapm_property_table.setValueAt("",
                            row_selected, 2);
                    mapm_property_table.fireTableDataChanged();
                    mTable.updateUI();


                } else if (selected_type.equals("String")) {


                    JButton jbt = new JButton(resources.getString("qkey.msg.msg208"));
                    jbt.addActionListener(new FileLoadingButtonListener2());
                    

                    mapm_property_table.setValueAt(jbt,
                            row_selected, 3);

                    //リスナつきのJTextAreaをいれこんじゃう。
                    Object gv = mapm_property_table.getValueAt(row_selected, 2);

                    if (gv instanceof JTextArea) {
                        //もうインスタンスが作られている
                    } else if (gv instanceof String) {
                        JTextArea newgv = new JTextArea(1,10);
                        newgv.setToolTipText(resources.getString("qkey.msg.msg230"));
                        newgv.setText((String)gv);
                        newgv.addMouseListener(new MapMessageStringPropertyMouseListener());
                        gv = newgv;
                    } else {
                        JTextArea newgv = new JTextArea(1,10);
                        newgv.setToolTipText(resources.getString("qkey.msg.msg230"));
                        newgv.addMouseListener(new MapMessageStringPropertyMouseListener());
                        gv = newgv;
                    }

                    mapm_property_table.setValueAt(gv,
                            row_selected, 2);
                    
                    mapm_property_table.fireTableDataChanged();
                    mTable.updateUI();
                } else if (selected_type.equals("Boolean")) {

                    JComboBox jcb = new JComboBox();
                    jcb.addItem("true");
                    jcb.addItem("false");
                    mapm_property_table.setValueAt(jcb,
                            row_selected, 2);
                    mapm_property_table.setValueAt("",
                            row_selected, 3);
                    mapm_property_table.fireTableDataChanged();

                } else {

                    mapm_property_table.setValueAt("",
                            row_selected, 3);

                    Object gv = mapm_property_table.getValueAt(row_selected, 2);
                    if (gv instanceof JComboBox)
                    mapm_property_table.setValueAt("",
                            row_selected, 2);
                    mapm_property_table.fireTableDataChanged();
                    
                    mTable.updateUI();
                }
            }

            }

        }
    }

    class StreamMessageTypeComboBoxItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {

            if (e.getStateChange() == e.SELECTED) {

            String selected_type = (String) e.getItem();
            JComboBox sourcebox = (JComboBox) e.getSource();

            int row_selected = sm_property_table.findRowNumberFromJComboBox(sourcebox);

            if (row_selected != -1) {
                if (selected_type.equals("Bytes")) {

                    JButton jbt = new JButton(resources.getString("qkey.msg.msg207"));
                    jbt.addActionListener(new FileLoadingButtonListener3());

                    //FileLoadingButtonListener
                    sm_property_table.setValueAt(jbt,
                            row_selected, 3);

                    Object gv = sm_property_table.getValueAt(row_selected, 2);
                    if (gv instanceof JComboBox)
                    sm_property_table.setValueAt("",
                            row_selected, 2);
                    sm_property_table.fireTableDataChanged();
                    sTable.updateUI();


                } else if (selected_type.equals("String")) {


                    JButton jbt = new JButton(resources.getString("qkey.msg.msg208"));
                    jbt.addActionListener(new FileLoadingButtonListener4());


                    sm_property_table.setValueAt(jbt,
                            row_selected, 3);

                    //リスナつきのJTextArea
                    Object gv = sm_property_table.getValueAt(row_selected, 2);

                    if (gv instanceof JTextArea) {
                        //もうインスタンスが作られている
                    } else if (gv instanceof String) {
                        JTextArea newgv = new JTextArea(1,10);
                        newgv.setToolTipText(resources.getString("qkey.msg.msg230"));
                        newgv.setText((String)gv);
                        newgv.addMouseListener(new StreamMessageStringPropertyMouseListener());
                        gv = newgv;
                    } else {
                        JTextArea newgv = new JTextArea(1,10);
                        newgv.setToolTipText(resources.getString("qkey.msg.msg230"));
                        newgv.addMouseListener(new StreamMessageStringPropertyMouseListener());
                        gv = newgv;
                    }

                    sm_property_table.setValueAt(gv,
                            row_selected, 2);

                    sm_property_table.fireTableDataChanged();
                    sTable.updateUI();
                } else if (selected_type.equals("Boolean")) {

                    JComboBox jcb = new JComboBox();
                    jcb.addItem("true");
                    jcb.addItem("false");
                    sm_property_table.setValueAt(jcb,
                            row_selected, 2);
                    sm_property_table.setValueAt("",
                            row_selected, 3);
                    sm_property_table.fireTableDataChanged();

                } else {

                    sm_property_table.setValueAt("",
                            row_selected, 3);

                    Object gv = sm_property_table.getValueAt(row_selected, 2);
                    if (gv instanceof JComboBox)
                    sm_property_table.setValueAt("",
                            row_selected, 2);
                    sm_property_table.fireTableDataChanged();

                    sTable.updateUI();
                }
            }

            }

        }
    }

    class FileChooserpathInputListener implements CaretListener {

        public void caretUpdate(CaretEvent e) {

            filechoose_okbutton.setEnabled(false);

            if (filechoose_file_path.getText().trim().endsWith("\\")) {
                errlabel.setText(resources.getString("qkey.msg.msg115"));
                errlabel.setForeground(Color.RED);
                errlabel.updateUI();
                temppanel.updateUI();
                filechoosemsgPanel.updateUI();
                filechooseDialog.pack();
                return;
            }

            try {
                File df = new File(filechoose_file_path.getText().trim());
                if ((df != null)) {



                    if (df.isDirectory()) {

                        errlabel.setText(resources.getString("qkey.msg.msg117"));
                        errlabel.setForeground(Color.RED);
                        errlabel.updateUI();
                        temppanel.updateUI();
                        filechoosemsgPanel.updateUI();
                        filechooseDialog.pack();
                        return;

                    }

                    if (!df.exists()) {
                        errlabel.setText(resources.getString("qkey.msg.msg200"));
                        errlabel.setForeground(Color.RED);
                        errlabel.updateUI();
                        temppanel.updateUI();
                        filechoosemsgPanel.updateUI();
                        filechooseDialog.pack();
                        return;
                    } else {
                        errlabel.setText("");
                        errlabel.updateUI();
                        temppanel.updateUI();
                        filechoosemsgPanel.updateUI();
                        filechooseDialog.pack();
                        filechoose_okbutton.setEnabled(true);

                        return;
                    }
                    
                }
            } catch (Exception fe) {
                //fe.printStackTrace();
                filechoose_okbutton.setEnabled(false);
            }
        }
    }

    public void showSaveToWindow(String title, String desc) {


        if (saveDialog == null) {
            saveDialog = new JDialog();
            saveDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Download).getImage());


            saveDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                }
            });

            saveDialog.getContentPane().setLayout(new BorderLayout());

            savemsgPanel = new JPanel();
            savemsgPanel.setLayout(new BorderLayout());

            JPanel savemsg = new JPanel();
            saveDialog.setSize(200, 300);
            saveDialog.setTitle(title);

            save_file_path = new JTextField(30);

        if (_folderChooser == null) {
            _folderChooser = new FolderChooser();
            _folderChooser.setAvailableButtons(_folderChooser.getAvailableButtons() & ~FolderChooser.BUTTON_DELETE);
            _folderChooser.setNavigationFieldVisible(true);
        }
            JButton file_choose_button = (JButton)createSaveChooseButton();

            JLabel downloadlabel = new JLabel(desc);
            JPanel expl = new JPanel();
            expl.setLayout(new BorderLayout());

            JPanel tqboxpanel = new JPanel();
            tqboxpanel.setLayout(new BorderLayout());
            expl.add(BorderLayout.NORTH, tqboxpanel);
            expl.add(BorderLayout.CENTER, downloadlabel);


            savemsg.add(save_file_path);
            savemsg.add(file_choose_button);
            savemsgPanel.add(BorderLayout.NORTH, expl);
            savemsgPanel.add(BorderLayout.CENTER, savemsg);
            JButton okbutton1 = new JButton("              OK              ");
            okbutton1.addActionListener(new SaveOKListener());
            JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg129") + "             ");
            cancelbutton.addActionListener(new SaveCancelListener());

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, okbutton1);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            temppanel = new JPanel();
            temppanel.setLayout(new BorderLayout());

            temppanel.add(BorderLayout.SOUTH, pbuttonpanel);

            savemsgPanel.add(BorderLayout.SOUTH, temppanel);


            saveDialog.getContentPane().add(BorderLayout.NORTH, savemsgPanel);
            saveDialog.pack();


        }
        saveDialog.setLocationRelativeTo(oya);
        saveDialog.setVisible(true);

    }
    public void showFileChooseWindow(String title, String description, ActionListener acl) {

        // Create popup
        if (filechooseDialog == null) {
            filechooseDialog = new JDialog();
            filechooseDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Upload).getImage());


            filechooseDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                }
            });

            filechooseDialog.getContentPane().setLayout(new BorderLayout());

            filechoosemsgPanel = new JPanel();
            filechoosemsgPanel.setLayout(new BorderLayout());

            JPanel filechoosemsg = new JPanel();
            filechooseDialog.setSize(200, 300);
            filechooseDialog.setTitle(title);

            filechoose_file_path = new JTextField(30);
            filechoose_file_path.addCaretListener(new FileChooserpathInputListener());

            file_chooser = new JFileChooser();

            JButton file_choose_button = (JButton)createBrowseButton("...");

            filechooselabel = new JLabel(description);
            JPanel expl = new JPanel();
            expl.setLayout(new BorderLayout());

            JPanel tqboxpanel = new JPanel();
            tqboxpanel.setLayout(new BorderLayout());
            expl.add(BorderLayout.NORTH, tqboxpanel);
            expl.add(BorderLayout.CENTER, filechooselabel);


            filechoosemsg.add(filechoose_file_path);
            filechoosemsg.add(file_choose_button);
            filechoosemsgPanel.add(BorderLayout.NORTH, expl);
            filechoosemsgPanel.add(BorderLayout.CENTER, filechoosemsg);
            filechoose_okbutton = new JButton("            OK              ");
            filechoose_okbutton.addActionListener(acl);
            JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg129") + "             ");
            cancelbutton.addActionListener(new FileChooseCancelListener());

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, filechoose_okbutton);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            temppanel = new JPanel();
            temppanel.setLayout(new BorderLayout());

            errlabel = new JLabel();
            temppanel.add(BorderLayout.CENTER, errlabel);
            temppanel.add(BorderLayout.SOUTH, pbuttonpanel);

            filechoosemsgPanel.add(BorderLayout.SOUTH, temppanel);


            filechooseDialog.getContentPane().add(BorderLayout.NORTH, filechoosemsgPanel);
            filechooseDialog.pack();
            

        } else {
            
            //一旦アクションリスナをクリア
            ActionListener[] listeners = filechoose_okbutton.getActionListeners();
            for (int i = 0 ; i < listeners.length ; i++) {
                filechoose_okbutton.removeActionListener(listeners[i]);
            }

            filechoose_okbutton.addActionListener(acl);

            filechooseDialog.setTitle(title);
            filechooselabel.setText(description);

        }

        filechooseDialog.setLocationRelativeTo(newmessageFrame);
        filechooseDialog.setVisible(true);



    }


    private AbstractButton createBrowseButton(String button_name) {
        final JButton button = new JButton(button_name);
        button.setMnemonic('B');
        button.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (filechoose_file_path.getText().length() > 0) {
                    _currentFolder = file_chooser.getFileSystemView().createFileObject(filechoose_file_path.getText());
                }
                file_chooser.setCurrentDirectory(_currentFolder);
                //file_chooser.setRecentList(_recentList);
                file_chooser.setFileHidingEnabled(true);
                int result = file_chooser.showOpenDialog(button.getTopLevelAncestor());
                if (result == FolderChooser.APPROVE_OPTION) {
                    _currentFolder = file_chooser.getSelectedFile();
                    if (_recentList.contains(_currentFolder.toString())) {
                        _recentList.remove(_currentFolder.toString());
                    }
                    _recentList.add(0, _currentFolder.toString());
                    File selectedFile = file_chooser.getSelectedFile();
                    if (selectedFile != null) {
                        filechoose_file_path.setText(selectedFile.toString());
                    }
                    else {
                        filechoose_file_path.setText("");
                    }
                }
            }
        });
        button.setRequestFocusEnabled(false);
        button.setFocusable(false);
        return button;
    }


    class FileChooseCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            filechooseDialog.setVisible(false);

        }
    }

    class FolderChooseCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            folderchooseDialog.setVisible(false);

        }
    }

    public void showFolderChooseWindow(String title, String description, ActionListener acl) {

        // Create popup
        if (folderchooseDialog == null) {
            folderchooseDialog = new JDialog();
            folderchooseDialog.setIconImage(QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Upload).getImage());


            folderchooseDialog.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                }
            });

            folderchooseDialog.getContentPane().setLayout(new BorderLayout());

            folderchoosemsgPanel = new JPanel();
            folderchoosemsgPanel.setLayout(new BorderLayout());

            JPanel folderchoosemsg = new JPanel();
            folderchooseDialog.setSize(200, 300);
            folderchooseDialog.setTitle(title);

            folderchoose_file_path = new JTextField(30);
            //folderchoose_file_path.addCaretListener(new FileChooserpathInputListener());

        if (_folderChooser == null) {
            _folderChooser = new FolderChooser();
            _folderChooser.setAvailableButtons(_folderChooser.getAvailableButtons() & ~FolderChooser.BUTTON_DELETE);
            _folderChooser.setNavigationFieldVisible(true);
        }
            JButton folder_choose_button = (JButton)createFolderChooseButton();

            folderchooselabel = new JLabel(description);
            JPanel expl = new JPanel();
            expl.setLayout(new BorderLayout());

            JPanel tqboxpanel = new JPanel();
            tqboxpanel.setLayout(new BorderLayout());
            expl.add(BorderLayout.NORTH, tqboxpanel);
            expl.add(BorderLayout.CENTER, folderchooselabel);

            folderchoosemsg.add(folderchoose_file_path);
            folderchoosemsg.add(folder_choose_button);
            folderchoosemsgPanel.add(BorderLayout.NORTH, expl);
            folderchoosemsgPanel.add(BorderLayout.CENTER, folderchoosemsg);
            folderchoose_okbutton = new JButton("             OK             ");
            folderchoose_okbutton.addActionListener(acl);
            JButton cancelbutton = new JButton("         " + resources.getString("qkey.msg.msg129") + "             ");
            cancelbutton.addActionListener(new FolderChooseCancelListener());

            JPanel pbuttonpanel = new JPanel();
            pbuttonpanel.setLayout(new BorderLayout());
            pbuttonpanel.add(BorderLayout.WEST, folderchoose_okbutton);
            pbuttonpanel.add(BorderLayout.CENTER, cancelbutton);

            temppanelf = new JPanel();
            temppanelf.setLayout(new BorderLayout());

            errlabel = new JLabel();
            temppanelf.add(BorderLayout.CENTER, errlabel);
            temppanelf.add(BorderLayout.SOUTH, pbuttonpanel);

            folderchoosemsgPanel.add(BorderLayout.SOUTH, temppanelf);


            folderchooseDialog.getContentPane().add(BorderLayout.NORTH, folderchoosemsgPanel);
            folderchooseDialog.pack();


        } else {

            //一旦アクションリスナをクリア
            ActionListener[] listeners = folderchoose_okbutton.getActionListeners();
            for (int i = 0 ; i < listeners.length ; i++) {
                folderchoose_okbutton.removeActionListener(listeners[i]);
            }

            folderchoose_okbutton.addActionListener(acl);

            folderchooseDialog.setTitle(title);
            folderchooselabel.setText(description);

        }

        folderchooseDialog.setLocationRelativeTo(oya_frame);
        folderchooseDialog.setVisible(true);



    }


//Bytesのケースで、ファイル名をカラムに入れれば良い場合
    class FileChooseOKListener implements ActionListener {

        JButton real_source;
        public FileChooseOKListener(Object obj) {
            real_source = (JButton)obj;
        }

        public void actionPerformed(ActionEvent e) {

            mapmdce3.stopCellEditing();
            //ファイル選択結果をここへ。
            mapm_property_table.setValueAt(filechoose_file_path.getText(),
                     ((Integer)real_source.getClientProperty(QBrowserV2.QBBUTTONROWPOSITION)).intValue(), 2);
            mapm_property_table.fireTableDataChanged();

            filechooseDialog.setVisible(false);

        }

    }

//Stringのケースで、ファイル内容をカラムにコピーする場合
    class FileChooseOKListener2 implements ActionListener {

        JButton real_source;

        public FileChooseOKListener2(Object obj) {
            real_source = (JButton)obj;
        }

        public void actionPerformed(ActionEvent e) {

            mapmdce3.stopCellEditing();

            //ファイル選択結果をここへ。
            java.io.FileInputStream fi = null;
            ByteArrayOutputStream baos = null;

            try {
        
            fi = new FileInputStream(filechoose_file_path.getText());
            baos = new ByteArrayOutputStream();

            byte buf[] = new byte[1024];
            int len = 0;

            int filesizecount = 0;

            while ((len = fi.read(buf)) != -1) {
                filesizecount += buf.length;
                baos.write(buf, 0, len);
            }


            int row_selected = ((Integer)real_source.getClientProperty(QBrowserV2.QBBUTTONROWPOSITION)).intValue();
            JTextArea jgv = (JTextArea)mapm_property_table.getValueAt(row_selected, 2);
            String encode = (String)encoding_type.getSelectedItem();
            if (!encode.equals("default")) {
                jgv.setText(baos.toString(encode));
            } else {
                jgv.setText(baos.toString());
            }

            
            mapm_property_table.setValueAt(jgv, row_selected , 2);
            mapm_property_table.fireTableDataChanged();

            } catch (Exception ie) {
                popupErrorMessageDialog(ie);
            } finally {

                if (fi != null) {
                    try {
                     fi.close();
                    } catch (IOException iie) {}
                    fi = null;
                }

                if (baos != null) {
                    try {
                     baos.close();
                    } catch (IOException iie) {}
                    baos = null;
                }
            }

            filechooseDialog.setVisible(false);

        }

    }

    //TextMessageのボディをファイルから読み込む場合
    class FileChooseOKListener3 implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            //ファイル選択結果をここへ。
            java.io.FileInputStream fi = null;
            ByteArrayOutputStream baos = null;

            try {

            fi = new FileInputStream(filechoose_file_path.getText());
            baos = new ByteArrayOutputStream();

            byte buf[] = new byte[1024];
            int len = 0;

            int filesizecount = 0;

            while ((len = fi.read(buf)) != -1) {
                filesizecount += buf.length;
                baos.write(buf, 0, len);
            }

            //mbodyPanel.textArea.setText(baos.toString());
            //デフォルト以外が選択されていたらエンコードをセット

            String encode = (String)encoding_type.getSelectedItem();
            if (!encode.equals("default")) {
                mbodyPanel.textArea.setText(baos.toString(encode));
            } else {
                mbodyPanel.textArea.setText(baos.toString());
            }

            mbodyPanel.textAreaBK = baos.toByteArray();

            } catch (Exception ie) {
                popupErrorMessageDialog(ie);
            } finally {

                if (fi != null) {
                    try {
                     fi.close();
                    } catch (IOException iie) {}
                    fi = null;
                }

                if (baos != null) {
                    try {
                     baos.close();
                    } catch (IOException iie) {}
                    baos = null;
                }
            }

            filechooseDialog.setVisible(false);

        }

    }


//Bytesのケースで、ファイル名をカラムに入れれば良い場合
    class FileChooseOKListener4 implements ActionListener {

        JButton real_source;

        public FileChooseOKListener4(Object obj) {
            real_source = (JButton)obj;
        }

        public void actionPerformed(ActionEvent e) {
            smdce3.stopCellEditing();

            //ファイル選択結果をここへ。
            Integer row_pos = (Integer)real_source.getClientProperty(QBrowserV2.QBBUTTONROWPOSITION);
            sm_property_table.setValueAt(filechoose_file_path.getText(),
                                    row_pos.intValue() , 2);
            
            sm_property_table.fireTableDataChanged();

            filechooseDialog.setVisible(false);

        }

    }

//Stringのケースで、ファイル内容をカラムにコピーする場合
    class FileChooseOKListener5 implements ActionListener {

        JButton real_source;

        public FileChooseOKListener5(Object obj) {
            real_source = (JButton)obj;
        }

        public void actionPerformed(ActionEvent e) {

            smdce3.stopCellEditing();

            //ファイル選択結果をここへ。
            java.io.FileInputStream fi = null;
            ByteArrayOutputStream baos = null;

            try {

            fi = new FileInputStream(filechoose_file_path.getText());
            baos = new ByteArrayOutputStream();

            byte buf[] = new byte[1024];
            int len = 0;

            int filesizecount = 0;

            while ((len = fi.read(buf)) != -1) {
                filesizecount += buf.length;
                baos.write(buf, 0, len);
            }

            Integer row_pos = (Integer)real_source.getClientProperty(QBrowserV2.QBBUTTONROWPOSITION);
            JTextArea jgv = (JTextArea)sm_property_table.getValueAt(row_pos.intValue(), 2);
            //jgv.setText(baos.toString());
            String encode = (String)encoding_type.getSelectedItem();
            if (!encode.equals("default")) {
                jgv.setText(baos.toString(encode));
            } else {
                jgv.setText(baos.toString());
            }
            sm_property_table.setValueAt(jgv, row_pos.intValue() , 2);
            sm_property_table.fireTableDataChanged();


            } catch (Exception ie) {
                popupErrorMessageDialog(ie);
            } finally {

                if (fi != null) {
                    try {
                     fi.close();
                    } catch (IOException iie) {}
                    fi = null;
                }

                if (baos != null) {
                    try {
                     baos.close();
                    } catch (IOException iie) {}
                    baos = null;
                }
            }

            filechooseDialog.setVisible(false);

        }

    }



    class SearchTemplateItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == e.SELECTED) {
            String selected = (String) searchTemplateBox.getSelectedItem();
            searchtextfield.setText(selected);
            }
        }
    }

    class ConnectionTemplateItemListener implements ItemListener {

        private void parseTemplateString() {
            String selected = (String) connectionTemplateBox.getSelectedItem();
            //"host = localhost port = 7676 user = admin password = admin "
            try {
                StringTokenizer st = new StringTokenizer(selected);
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (token.equals("host")) {
                        if (st.nextToken().equals("=")) {
                            connectiontext_host.setText(st.nextToken());
                        }
                    } else if (token.equals("port")) {
                        if (st.nextToken().equals("=")) {
                            connectiontext_port.setText(st.nextToken());
                        }
                    } else if (token.equals("user")) {
                        if (st.nextToken().equals("=")) {
                            connectiontext_user.setText(st.nextToken());
                        }
                    } else if (token.equals("password")) {
                        if (st.nextToken().equals("=")) {
                            connectiontext_password.setText(st.nextToken());
                        }
                    }

                }
            } catch (Throwable txex) {
                //Parse失敗、何もしない。
            }
        }

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == e.SELECTED) {
                  parseTemplateString();
            }
        }
    }

    class SubscribeTemplateItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == e.SELECTED) {
            if ((subscribetextfield != null) && (matesakiBox3 != null)) {
              String selected = (String) subscribeTemplateBox.getSelectedItem();
              subscribetextfield.setText(selected);
              matesakiBox3.setSelectedItem(selected);
            }
            }
        }
    }

    class DownloadpathInputListener implements CaretListener {
        
        JLabel downloadmsg;
        JButton downloadbutton;
        JTextField downloadfilepath;

        String m_msgid;

        public DownloadpathInputListener(String msgid) {
            downloadmsg = qbrowsercache.getCurrentDownloadMsgLabel(msgid);
            downloadbutton = qbrowsercache.getCurrentDownloadButton(msgid);
            downloadfilepath = qbrowsercache.getCurrentDownloadFilePath(msgid);
            m_msgid = msgid;

        }

        public void caretUpdate(CaretEvent e) {

            downloadmsg.setText("");
            downloadbutton.setEnabled(false);

            if (downloadfilepath.getText().trim().endsWith("\\")) {
                downloadmsg.setText(resources.getString("qkey.msg.msg115"));
                return;
            }


            //downloadfilepath
            try {
                File df = new File(downloadfilepath.getText().trim());
                if ((df != null)) {

                    if (df.exists()) {
                        downloadmsg.setText(resources.getString("qkey.msg.msg116"));
                        return;
                    }

                    if (df.isDirectory()) {

                        downloadmsg.setText(resources.getString("qkey.msg.msg117"));

                    } else {
                        //ファイルが指定されたっぽい。でも親ディレクトリがあるか？
                        String parentpath = df.getParent();

                        if (parentpath != null) {
                            File pdir = new File(parentpath);
                            if (pdir != null)
                            {
                                if ((pdir != null) && (pdir.exists())) {
                                    downloadbutton.setEnabled(true);
                                } else {
                                    downloadmsg.setText(resources.getString("qkey.msg.msg118"));
                                }
                            }
                        }
                    }
                }
            } catch (Exception fe) {
                //fe.printStackTrace();
                downloadbutton.setEnabled(false);
            }
        }
    }

    class DeleteLSListener2 implements ActionListener {
                public void actionPerformed(ActionEvent e) {

                    if (treePane != null) {

                    TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();
                    String lsn_with_suffix = di.name_with_suffix;
                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    JTextArea jta = new JTextArea("", 3, 15 + lsn_with_suffix.length());
                    jta.setText(resources.getString("qkey.msg.msg303") + lsn_with_suffix + resources.getString("qkey.msg.msg304"));
                    jta.setEditable(false);
                    jta.setBackground(Color.WHITE);
                    JScrollPane jsp = new JScrollPane(jta);
                    jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                    panel.add(BorderLayout.CENTER, jsp);
                    JPanel cbpanel = new JPanel();
                    final JCheckBox del_file = new JCheckBox();
                    del_file.setSelected(false);
                    cbpanel.setLayout(new BorderLayout());
                    JLabel dellabel = new JLabel(resources.getString("qkey.msg.msg339"));
                    cbpanel.add(BorderLayout.WEST, dellabel);
                    cbpanel.add(BorderLayout.CENTER, del_file);
                    panel.add(BorderLayout.SOUTH, cbpanel);

                    popupConfirmationDialog(resources.getString("qkey.msg.msg302"), panel,
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                            new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    confirmDialog.setVisible(false);
                                    String lsn_with_suffix = (String) lsdelete_button.getClientProperty(CURRENTLOCALSTORE);

                                    try {
                                        LocalStoreProperty lsp = lsm.getLocalStoreProperty(getPureDestName(lsn_with_suffix));
                                        if (lsp == null) {
                                            throw new Exception("Local store not found in LocalStoreManager");
                                        }
                                        if (del_file.getSelectedObjects() != null) {
                                           lsm.clearLocalStore(getPureDestName(lsn_with_suffix));
                                        }
                                        lsm.removeLocalStoreProperty(lsp);
                                        //タブも消す
                                        int target_tab_index = tabbedPane.indexOfTab(lsn_with_suffix);
                                        jtableins.remove(lsn_with_suffix);
                                        tabbedPane.remove(target_tab_index);
                                        destinationNamesForDisplayQueue = new ArrayList();
                                        destinationNamesForDisplayTopic = new ArrayList();
                                        collectDestination();
                                        refreshLocalStoresOnMenu();
                                        initTreePane();
                                    } catch (Exception ree) {
                                        ree.printStackTrace();
                                    }

                                }
                            });
                      }
                }
    }

    class SoufukosuInputListener implements CaretListener {

        public void caretUpdate(CaretEvent e) {

            if (e.getMark() > 0) {
                try {
                    Integer.parseInt(soufukosu.getText().trim());
                    if (okbutton != null) {
                        okbutton.setEnabled(true);
                    }
                } catch (Exception ne) {
                    if (okbutton != null) {
                        okbutton.setEnabled(false);
                    }
                    if (cmessagefooter != null) {
                        cmessagefooter.setText(resources.getString("qkey.msg.msg119"));
                    }
                }

            }
        }
    }

    class SaveCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            saveDialog.setVisible(false);

        }
    }



    class SaveOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

        try {

            saveDialog.setVisible(false);
            String fsave_file_path = save_file_path.getText();
            if (!fsave_file_path.endsWith(File.separator)) {
                fsave_file_path = fsave_file_path + File.separator;
            }

            TextArea ta = new TextArea("", 6, 100, TextArea.SCROLLBARS_BOTH);
            ta.setEditable(false);
            ta.setBackground(Color.WHITE);
            int approw = ext_messages.size() + 20;
            if (approw > 40) approw = 40;
            ta.setRows(approw);
            
            

            String dispid = e.getSource().toString();
            DisplayMsgDialogRunner dpr = new DisplayMsgDialogRunner(dispid ,resources.getString("qkey.msg.msg249"), ta,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Download), oya_frame);
            Thread dprth = new Thread(dpr);
            //display_threads.add(dprth);
            DisplayDialogThreadPool.addDisplayThread(dpr, dprth);
            dprth.start();

            while (!dpr.isStarted()) {
                try {
                    Thread.sleep(100);
                } catch (Throwable thex) {}
            }

            JDialog cmp = dpr.getMessageDialog();

            if (!ext_messages.isEmpty()) {
                StringBuilder sbuffer = new StringBuilder();
                for (int i = 0; i < ext_messages.size(); i++) {

                    Object mobj = ext_messages.get(i);

                  //ローカルストアからのセーブの場合、リアルファイルがあるならばそちらから、
                  //万が一何かの原因で消えていたらロードされたデータからダウンロードする
                   if (mobj instanceof LocalMessageContainer){

                   LocalMessageContainer lmc = (LocalMessageContainer) mobj;

                   String sourcepath = lmc.getReal_file_path();
                   File fromFile = new File(sourcepath);

                   if (fromFile.exists()) {

                       File toFile = new File(fsave_file_path + fromFile.getName());
                       try {
                            sbuffer.append(resources.getString("qkey.msg.msg251"));
                            sbuffer.append(lmc.getVmsgid());
                            sbuffer.append(resources.getString("qkey.msg.msg252"));
                            QBrowserUtil.copy(fromFile, toFile);
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            //ta.append(file_target_string);
                            sbuffer.append("\n\n");
                       } catch (Exception copye) {
                           popupErrorMessageDialog(copye);
                       }

                   } else {

                   Message sel_message = lmc.getMessage();
                   File workdir = null;
                   if (sel_message == null) {
                       sel_message = lmc.getRealMessage(session);
                   }
                            sbuffer.append(resources.getString("qkey.msg.msg251"));
                            sbuffer.append(lmc.getVmsgid());
                            sbuffer.append(resources.getString("qkey.msg.msg252"));
                   if (sel_message instanceof TextMessage) {
                        TextMessagePersister mp = new TextMessagePersister(lmc);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_TextMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof BytesMessage) {
                        BytesMessagePersister mp = new BytesMessagePersister(lmc);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_BytesMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            sbuffer.append("\n\n");
                            cmp.getRootPane().updateUI();
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof MapMessage) {
                        MapMessagePersister mp = new MapMessagePersister(lmc);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_MapMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof StreamMessage) {
                        StreamMessagePersister mp = new StreamMessagePersister(lmc);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_StreamMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof ObjectMessage) {
                        ObjectMessagePersister mp = new ObjectMessagePersister(lmc);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_ObjectMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof Message) {
                        MessagePersister mp = new MessagePersister(lmc);
                        mp.setMsgDialog(cmp);
                        mp.setTextBuffer(sbuffer);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(lmc.getVmsgid()) + "_Message.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                    }

                   }
                  } else
                    if (mobj instanceof MessageContainer) {

                     //int tabindex = tabbedPane.getSelectedIndex();
                     //String from_msg_table_with_suffix = tabbedPane.getTitleAt(tabindex);

                    MessageContainer mc = (MessageContainer)mobj;
                    Message sel_message = mc.getMessage();
                    if (sel_message == null) {
                        try {
                          Queue rq = getQueue(getPureDestName(mc.getPureDest_name()));
                          sel_message = mc.getRealMessageFromBroker(session, rq);
                        } catch (Exception reale) {
                            reale.printStackTrace();
                        }
                    }

                            sbuffer.append(resources.getString("qkey.msg.msg251"));
                            try {
                              sbuffer.append(sel_message.getJMSMessageID());
                            } catch (Exception tae) {}
                            sbuffer.append(resources.getString("qkey.msg.msg252"));

                   File workdir = null;
                   if (sel_message instanceof TextMessage) {
                        TextMessagePersister mp = new TextMessagePersister((TextMessage) sel_message);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_TextMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            //ta.append(file_target_string);
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof BytesMessage) {
                        BytesMessagePersister mp = new BytesMessagePersister((BytesMessage) sel_message);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_BytesMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            sbuffer.append("\n\n");
                            cmp.getRootPane().updateUI();
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof MapMessage) {
                        MapMessagePersister mp = new MapMessagePersister((MapMessage) sel_message);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_MapMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            //ta.append(file_target_string);
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof StreamMessage) {
                        StreamMessagePersister mp = new StreamMessagePersister((StreamMessage) sel_message);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_StreamMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            //ta.append(file_target_string);
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof ObjectMessage) {
                        ObjectMessagePersister mp = new ObjectMessagePersister((ObjectMessage) sel_message);
                        mp.setTextBuffer(sbuffer);
                        mp.setMsgDialog(cmp);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_ObjectMessage.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            //ta.append(file_target_string);
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                   } else if (sel_message instanceof Message) {
                        MessagePersister mp = new MessagePersister(sel_message);
                        mp.setMsgDialog(cmp);
                        mp.setTextBuffer(sbuffer);
                        try {
                            workdir = mp.persistToFile();
                            String file_target_string = fsave_file_path + QBrowserUtil.eliminateDameMoji(sel_message.getJMSMessageID()) + "_Message.zip";
                            mp.zipUp(workdir, new File(file_target_string));
                            sbuffer.append(resources.getString("qkey.msg.msg253"));
                            sbuffer.append("\n\n");
                        } catch (Exception ex) {
                            mp.cleanupWorkDir(workdir);
                            ex.printStackTrace();
                        }
                    }

                  } 

                } //end for

                sbuffer.append("\n" + resources.getString("qkey.msg.msg250"));
                ta.append(sbuffer.toString());
                ta.setCaretPosition(ta.getText().length());
                sbuffer = null;
                cmp.getRootPane().updateUI();

            }

        } catch (Exception reade) {
            popupErrorMessageDialog(reade);
        }

        }
    }

    class DeleteCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            currentDeleteTarget.clear();
            deleteconfirmDialog.setVisible(false);

        }
    }

    class PurgeDestCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            purgedestconfirmDialog.setVisible(false);

        }
    }

    class SendCancelListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            sendconfirmDialog.setVisible(false);

        }
    }

    class SendOKListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    newmessageFrame.setVisible(false);
                    sendconfirmDialog.setVisible(false);
                    String ft = resources.getString("qkey.msg.msg120") + " " + nmi.getDest_type() + ":" + nmi.getDest() + " " + resources.getString("qkey.msg.msg121");

                    try {
                        sendMessage();
                    } catch (Exception ee) {
                        if (ee.getMessage().indexOf("C4059") != -1) {
                            try {
                                //接続を張りなおしてリトライ
                                reconnect();
                                sendMessage();
                            } catch (Exception recex) {
                                //仏の顔は一度まで。
                                popupErrorMessageDialog(recex);
                            }
                        } else {
                            popupErrorMessageDialog(ee);
                        }
                    }

                    cmessagefooter.setText(ft);

                    try {
                        //今作成したDESTをあて先コンボボックスのデフォルトに設定する

                    String send_target_name = nmi.getDest().trim();
                    String send_target_type = nmi.getDest_type();
                    DefaultComboBoxModel model = (DefaultComboBoxModel) qBox.getModel();
                    String compl_dest_name = convertFullDestName(send_target_name, send_target_type);
                    boolean found = false;
                    for (int i = 0; i < model.getSize(); i++) {
                        String key = (String) model.getElementAt(i);
                        if (key.trim().equals(compl_dest_name)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        if(!send_target_type.equals(TOPIC_LITERAL)) {

                          model.insertElementAt(compl_dest_name, 0);
                          qBox.setSelectedItem(compl_dest_name);

                        }
                    } else {
                        //既にBOXに入っていた場合
                        qBox.setSelectedItem(compl_dest_name);
                    }

                    refreshMsgTableWithDestName();
                    } catch (Exception iie) {
                        popupErrorMessageDialog(iie);
                    }
                }
            });

        }
    }

    /**
     * A table of JMS Messages
     */
    class MsgTable extends AbstractTableModel {

        final String[] columnNames = 
                {"#", resources.getString("qkey.table.header.msgid"),
            resources.getString("qkey.table.header.timestamp"),
            resources.getString("qkey.table.header.type"),
            resources.getString("qkey.table.header.size"),
            resources.getString("qkey.table.header.mode"),
            resources.getString("qkey.table.header.priority")};
        SimpleDateFormat df =
                new SimpleDateFormat("yyyy/MM/dd kk:mm:ss z");
        LinkedList list = null;

        //MsgId重複チェック用
        HashMap msgids = new HashMap();

        public int getRealRowNoFromMsgId(String msgid) {
            Integer iiv = (Integer)msgids.get(msgid);
            int retval = -1;
            if (iiv != null) {
                retval = iiv.intValue();
            }

            return retval;
        }

        public int getRowCount() {
            if (list == null) {
                return 0;
            } else {
                return list.size();
            }
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        public void setDisplayNumberAt(int number , int row) {
            if (list == null) return;

            if (list.size() <= row) {
                return;
            }

            MessageContainer mc = (MessageContainer) list.get(row);
            mc.setDisplaynumber(number);


        }

        public Object getValueAt(int row, int column) {
            if (list == null) {
                return null;
            }

            if (list.size() <= row) {
                return null;
            }

            MessageContainer mc = (MessageContainer) list.get(row);
            //Message m = mc.getMessage();


            if (mc == null) {
                return "null";
            }

            try {
                switch (column) {
                    case 0:
                        // MessageID
                        return mc.getDisplaynumber();
                    case 1:
                        // MessageID
                        return mc.getVmsgid();
                    case 2:
                        // Need to format into date/time
                        return df.format(new Date(mc.getVtimestamp()));
                    case 3:
                        return mc.getMessage_type();
                    case 4:
                        return QBrowserUtil.messageBodyLengthAsString(mc.getBody_size());
                    case 5:
                        // Delivery mode
                        int mode = mc.getVdeliverymode();
                        if (mode == DeliveryMode.PERSISTENT) {
                            return PERSISTENT;
                        } else if (mode == DeliveryMode.NON_PERSISTENT) {
                            return NONPERSISTENT;
                        } else {
                            return String.valueOf(mode) + "?";
                        }
                    case 6:
                        // Priority
                        return new Integer(mc.getVpriority());
                    default:
                        return "Bad column value: " + column;
                }
            } catch (Exception e) {
                return ("Error: " + e);
            }
        }


        int load(ArrayList<MessageContainer> ar) {
            if (ar == null) {
                return 0;
            }

            list = new LinkedList();

            for (int i = 0; i < ar.size(); i++) {
                MessageContainer mc = (MessageContainer)ar.get(i);
                mc.setDisplaynumber(list.size());
                list.add(mc);
                msgids.put(mc.getVmsgid() , list.size() - 1);
            }

            fireTableDataChanged();

            return list.size();
        }


        void init() {

            list = new LinkedList();
        }
        
        
        void add_one_row_ifexists_update(MessageContainer mc) {
                String smsgid = mc.getVmsgid();
                if (msgids.containsKey(smsgid)) {

                    for (int i = 0; i < list.size(); i++) {
                        MessageContainer tmc = (MessageContainer) list.get(i);
                        if (tmc.getVmsgid().equals(smsgid)) {
                            list.remove(i);
                            break;
                        }

                    }

                }

                mc.setDisplaynumber(list.size());
                list.add(mc);
                msgids.put(mc.getVmsgid() , list.size() -1);

                fireTableDataChanged();
        }

        void add_one_row(MessageContainer mc) {
            mc.setDisplaynumber(list.size());
            list.add(mc);
            msgids.put(mc.getVmsgid() , list.size() - 1);
            fireTableDataChanged();
        }

        void deleteMessageAtRow(int row) {
            if (list == null) {
                return;
            }

            MessageContainer mc = (MessageContainer)list.get(row);
            msgids.remove(mc.getVmsgid());
            list.remove(row);
            mc = null;
            fireTableDataChanged();

        }

        MessageContainer getMessageAtRow(int row) {
            if (list == null) {
                return null;
            }

            return (MessageContainer)list.get(row);
            //return mc.getMessage();
        }
    }

    class TextMessageBodyPanel extends PropertyPanel {
        TextMessageBodyPanel() {
            super();
            JPanel button_panel = new JPanel();
            button_panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            JButton jbt = new JButton(resources.getString("qkey.msg.msg220"));
            jbt.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    JTextArea ta = new JTextArea();
                    ta.setColumns(90);
                    ta.setRows(40);
                    

                    ta.setText(textArea.getText());

                    popupMessageDialog(resources.getString("qkey.msg.msg212"), createSearchableTextArea(ta),
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter));
                }
            });
            
            button_panel.setLayout(new BorderLayout());
            button_panel.add(jbt, BorderLayout.EAST);
            add(button_panel, BorderLayout.CENTER);
        }
    }

   void addDropTargetListenerToComponents(DropTargetListener listener, Component component) {
        DropTarget target = new DropTarget(component, DnDConstants.ACTION_REFERENCE, listener);
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                addDropTargetListenerToComponents(listener, container.getComponent(i));
            }
        }
    }

    class QBrowserTreeDropTargetListener implements DropTargetListener {

        public void dragEnter(DropTargetDragEvent dtde) {
            //dtde.acceptDrag(DnDConstants.ACTION_REFERENCE);
            tree_location.setBorder(BorderFactory.createEtchedBorder());
        }

        public void dragOver(DropTargetDragEvent dtde) {
            //dtde.acceptDrag(DnDConstants.ACTION_REFERENCE);
            //ターゲットのパスを青くしたいー
            TreePath tp = treePane.getTree().getClosestPathForLocation((int)dtde.getLocation().getX(), (int)dtde.getLocation().getY());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tp.getLastPathComponent();
            Object nodeInfo = node.getUserObject();
            TreeIconPanel.DestInfo di = (TreeIconPanel.DestInfo)nodeInfo;
            boolean isDropTarget = false;
            if (di.destinationType.equals("BKR") || di.destinationType.equals("FD")) {
                dtde.rejectDrag();
            } else if (di.destinationType.equals(QUEUE_LITERAL) || di.destinationType.equals(TOPIC_LITERAL)) {

//                String state = getStateOfDestination(di.destinationType, di.destinationName);
//              if (state.equals("PAUSED")) {
//                dtde.rejectDrag();
//              } else {
                isDropTarget = true;
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
//              }
            } else if (di.destinationType.equals(LOCAL_STORE_LITERAL)) {
                if (lsm.getLocalStoreProperty(di.destinationName).isValid()) {
                  isDropTarget = true;
                  dtde.acceptDrag(DnDConstants.ACTION_COPY);
                } else {
                  dtde.rejectDrag();
                }
            } else if (di.destinationType.equals(CHILD_TOPIC_LITERAL) || di.destinationType.equals(CHILD_LOCAL_STORE_LITERAL)) {
                dtde.rejectDrag();
            }
            //System.out.println("X座標：" + dtde.getLocation().getX() + " Y座標：" + dtde.getLocation().getY());
            //System.out.println("おやX座標：" + oya_frame.getX() + " Y座標：" + oya_frame.getY());

            if (dtde.getLocation().getY() > 480.0) {
              int row_count = treePane.getTree().getRowCount();
              int current_row = treePane.getTree().getRowForPath(tp);
              if (row_count > current_row + 1) {
                  //スクロールの余地あり。
                  treePane.getTree().scrollRowToVisible(current_row + 1);
              }
            } else if (dtde.getLocation().getY() < 150.0) {
              int current_row = treePane.getTree().getRowForPath(tp);
              if (0 < current_row) {
                  //スクロールの余地あり。
                  treePane.getTree().scrollRowToVisible(current_row - 1);
              }
            }
            //DefaultMutableTreeNode node = (DefaultMutableTreeNode)tp.getLastPathComponent();
            if (isDropTarget) {
                treePane.getTree().setSelectionPath(tp);
            }
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        public void dragExit(DropTargetEvent dte) {
            Border emptyBorder = new EmptyBorder(2, 2, 2, 2);
            tree_location.setBorder(emptyBorder);

        }
     
        public void drop(DropTargetDropEvent dtde) {

            ext_messages = new ArrayList();

            dtde.acceptDrop(DnDConstants.ACTION_REFERENCE);
            Transferable transfer = dtde.getTransferable();
            String td = null;
            try {
                td = (String) transfer.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                //e.printStackTrace();
                try {
                    //drop.acceptDrop(DnDConstants.ACTION_REFERENCE);
                    //Transferable transfer = drop.getTransferable();
                    File[] files = checkAcceptable(transfer);
                    if (null != files) {

                        final ArrayList targets = new ArrayList();
                        TreePath tp = treePane.getTree().getClosestPathForLocation((int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();

                        Object nodeInfo = node.getUserObject();
                        TreeIconPanel.DestInfo di = (TreeIconPanel.DestInfo) nodeInfo;
                        final String target_name = di.destinationName;
                        final String target_type = di.destinationType;

                        try {

                            for (int i = 0; i < files.length; i++) {
                                //ファイルに応じた読込み処理をおこなうべし。


                                LocalMessageContainer lmc = new LocalMessageContainer(files[i]);
                                lmc.setDest_name_with_suffix(di.name_with_suffix);
                                //target_name = lmc.getPureDest_name();
                                //target_type = lmc.getDest_type();
                                targets.add(lmc);


                            }

                        } catch (Exception reade) {
                            popupErrorMessageDialog(reade);
                        }



                        try {

                            JTextArea jta = new JTextArea("", 3, 25 + target_name.length());
                            jta.setText(target_type + resources.getString("qkey.msg.msg361") + target_name + resources.getString("qkey.msg.msg362") + resources.getString("qkey.msg.msg364") + " " + targets.size());
                            jta.setEditable(false);
                            jta.setBackground(Color.WHITE);

                            popupConfirmationDialog(resources.getString("qkey.msg.msg363"), jta,
                                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                                    new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            confirmDialog.dispose();
                                            confirmDialog = null;
                                            String from_msg_table_with_suffix = target_name + " : " + target_type;
                                            //転送(コピー開始）
                                            try {

                                                if ((target_name != null) && (target_type != null)) {
                                                    
                                                    forwardMessage(targets, from_msg_table_with_suffix, target_name, target_type, false, false);
                                                }

                                            } catch (Exception ie) {
                                                popupErrorMessageDialog(ie);
                                            }

                                            qBox.setSelectedItem(convertFullDestName(target_name, target_type));


                                        }
                                    });
















                        } catch (Exception ie) {
                            popupErrorMessageDialog(ie);
                        }

                        dtde.getDropTargetContext().dropComplete(true);
                        return;
                    }
                } catch (InvalidDnDOperationException ex) {
                    ex.printStackTrace();
                }
            }
            TreePath tp = treePane.getTree().getClosestPathForLocation((int) dtde.getLocation().getX(), (int) dtde.getLocation().getY());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();

            Object nodeInfo = node.getUserObject();
            final TreeIconPanel.DestInfo di = (TreeIconPanel.DestInfo) nodeInfo;

            //元ネタ宛先
            int sel_tab_index = tabbedPane.getSelectedIndex();
            String dispName = tabbedPane.getTitleAt(sel_tab_index);

            ArrayList target_msgids = QBrowserUtil.getTargetMsgidArrayFromStringFlavor(td);


            JTable cTable = (JTable) jtableins.get(dispName);
            TableModel mo = cTable.getModel();

            if (mo instanceof LocalMsgTable) {
                LocalMsgTable mt = (LocalMsgTable) mo;

                int[] target = new int[target_msgids.size()];
                targetX = new int[target_msgids.size()];

                for (int i = 0; i < target_msgids.size(); i++) {
                    String msgid = (String) target_msgids.get(i);
                    int rst = mt.getRealRowNoFromMsgId(msgid);
                    target[i] = rst;
                    targetX[i] = rst;
                }

                for (int i = 0; i < target.length; i++) {
                    //コピーを入れる

                    ext_messages.add(mt.getMessageAtRow(target[i]));

                }

            } else if (mo instanceof MsgTable) {
                MsgTable mt = (MsgTable) mo;

                int[] target = new int[target_msgids.size()];
                targetX = new int[target_msgids.size()];

                for (int i = 0; i < target_msgids.size(); i++) {
                    String msgid = (String) target_msgids.get(i);
                    int rst = mt.getRealRowNoFromMsgId(msgid);
                    target[i] = rst;
                    targetX[i] = rst;
                }

                for (int i = 0; i < target.length; i++) {
                    //コピーを入れる

                    ext_messages.add(mt.getMessageAtRow(target[i]));

                }
            }

            JTextArea jta = new JTextArea("", 3, 25 + di.destinationName.length());
            jta.setText(di.destinationType + resources.getString("qkey.msg.msg361") + di.destinationName + resources.getString("qkey.msg.msg362") + resources.getString("qkey.msg.msg364") + " " + ext_messages.size());
            jta.setEditable(false);
            jta.setBackground(Color.WHITE);

            popupConfirmationDialog(resources.getString("qkey.msg.msg363"), jta,
                    QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Confirm),
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            confirmDialog.dispose();
                            confirmDialog = null;
                            //転送(コピー開始）
                            try {

                                String target_name = di.destinationName;
                                String target_type = di.destinationType;

                                if ((target_name != null) && (target_type != null)) {

                                    int tabindex = tabbedPane.getSelectedIndex();
                                    String from_msg_table_with_suffix = tabbedPane.getTitleAt(tabindex);
                                    forwardMessage(ext_messages, from_msg_table_with_suffix, target_name, target_type, false, false);
                                }

                            } catch (Exception ie) {
                                popupErrorMessageDialog(ie);
                            }

                            qBox.setSelectedItem(di.name_with_suffix);


                        }
                    });

        }
    }

    class QBrowserDropTargetListener implements DropTargetListener {
        

        public void dragEnter(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dragOver(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dragExit(DropTargetEvent dte) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void drop(DropTargetDropEvent drop) {
            try {
                //System.out.println("drop = " + drop);
                drop.acceptDrop(DnDConstants.ACTION_REFERENCE);
                Transferable transfer = drop.getTransferable();
                File[] files = checkAcceptable(transfer);
                if (null != files) {

                    for (int i = 0; i < files.length; i++) {
                        java.io.FileInputStream fi = null;
                        ByteArrayOutputStream baos = null;

                        try {

                            fi = new FileInputStream(files[i].getAbsolutePath());
                            baos = new ByteArrayOutputStream();

                            byte buf[] = new byte[1024];
                            int len = 0;

                            int filesizecount = 0;

                            while ((len = fi.read(buf)) != -1) {
                                filesizecount += buf.length;
                                baos.write(buf, 0, len);
                            }

                            //デフォルト以外が選択されていたらエンコードをセット
                            
                            String encode = (String)encoding_type.getSelectedItem();
                            if (!encode.equals("default")) {
                              mbodyPanel.textArea.setText(baos.toString(encode));
                            } else {
                              mbodyPanel.textArea.setText(baos.toString());
                            }

                            mbodyPanel.textAreaBK = baos.toByteArray();

                        } catch (Exception ie) {
                            popupErrorMessageDialog(ie);
                            
                        } finally {

                            if (fi != null) {
                                try {
                                    fi.close();
                                } catch (IOException iie) {
                                }
                                fi = null;
                            }

                            if (baos != null) {
                                try {
                                    baos.close();
                                } catch (IOException iie) {
                                }
                                baos = null;
                            }
                        }
                    }

                    drop.getDropTargetContext().dropComplete(true);
                }
            } catch (InvalidDnDOperationException ex) {
                ex.printStackTrace();
            }
        }

    }

    class QBrowserDropTargetListener2 implements DropTargetListener {

        JTextArea jtadnd;

        public QBrowserDropTargetListener2(JTextArea vjta) {
                this.jtadnd = vjta;
        }


        public void dragEnter(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dragOver(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dragExit(DropTargetEvent dte) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void drop(DropTargetDropEvent drop) {
            try {
                drop.acceptDrop(DnDConstants.ACTION_REFERENCE);
                Transferable transfer = drop.getTransferable();
                File[] files = checkAcceptable(transfer);
                if (null != files) {

                    for (int i = 0; i < files.length; i++) {
                        java.io.FileInputStream fi = null;
                        ByteArrayOutputStream baos = null;

                        try {

                            fi = new FileInputStream(files[i].getAbsolutePath());
                            baos = new ByteArrayOutputStream();

                            byte buf[] = new byte[1024];
                            int len = 0;

                            int filesizecount = 0;

                            while ((len = fi.read(buf)) != -1) {
                                filesizecount += buf.length;
                                baos.write(buf, 0, len);
                            }

                            //jtadnd.setText(baos.toString());
                            String encode = (String)encoding_type.getSelectedItem();
                            if (!encode.equals("default")) {
                              jtadnd.setText(baos.toString(encode));
                            } else {
                              jtadnd.setText(baos.toString());
                            }

                        } catch (Exception ie) {
                            popupErrorMessageDialog(ie);
                        } finally {

                            if (fi != null) {
                                try {
                                    fi.close();
                                } catch (IOException iie) {
                                }
                                fi = null;
                            }

                            if (baos != null) {
                                try {
                                    baos.close();
                                } catch (IOException iie) {
                                }
                                baos = null;
                            }
                        }
                    }

                    drop.getDropTargetContext().dropComplete(true);
                }
            } catch (InvalidDnDOperationException ex) {
                ex.printStackTrace();
            }
        }

    }

    class QBrowserDropTargetListener3 implements DropTargetListener {


        public void dragEnter(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
                Transferable transfer = dtde.getTransferable();
                File[] files = checkAcceptable(transfer);
                if (null != files) {

                    for (int i = 0; i < files.length; i++) {
                        if (!files[i].getName().toLowerCase().endsWith("message.zip")) {
                          dtde.rejectDrag();
                        }
                    }
                }
        }

        public void dragOver(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dropActionChanged(DropTargetDragEvent dtde) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void dragExit(DropTargetEvent dte) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void drop(DropTargetDropEvent drop) {
            try {
                System.out.println("drop!!!!!");
                drop.acceptDrop(DnDConstants.ACTION_REFERENCE);
                Transferable transfer = drop.getTransferable();
                File[] files = checkAcceptable(transfer);
                if (null != files) {

                    ArrayList targets = new ArrayList();
                    String target_name = null;
                    String target_type = null;

                 try {

                    for (int i = 0; i < files.length; i++) {
                        //ファイルに応じた読込み処理をおこなうべし。
                        int selidx = tabbedPane.getSelectedIndex();
                        String dest_with_suffix = tabbedPane.getTitleAt(selidx);
                        LocalMessageContainer lmc = new LocalMessageContainer(files[i]);
                        lmc.setDest_name_with_suffix(dest_with_suffix);
                        target_name = lmc.getPureDest_name();
                        target_type = lmc.getDest_type();
                        targets.add(lmc);


                    }

                 } catch (Exception reade) {
                     popupErrorMessageDialog(reade);
                 }

                        try {

                           if ((target_name != null) && (target_type != null)) {
                             int tabindex = tabbedPane.getSelectedIndex();
                             String from_msg_table_with_suffix = tabbedPane.getTitleAt(tabindex);
                             forwardMessage(targets,from_msg_table_with_suffix,target_name,target_type,false,false);
                           }
                           


                        } catch (Exception ie) {
                            popupErrorMessageDialog(ie);
                        }

                    drop.getDropTargetContext().dropComplete(true);
                }
            } catch (InvalidDnDOperationException ex) {
                ex.printStackTrace();
            }
        }

    }

    class TextMessageInputBodyPanel extends PropertyPanel {
        TextMessageInputBodyPanel() {
            super();
            JPanel button_panel = new JPanel();
            JButton jbt = new JButton(resources.getString("qkey.msg.msg215"));
            final FileChooseOKListener3 fcok3 = new FileChooseOKListener3();
            jbt.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    showFileChooseWindow(resources.getString("qkey.msg.msg213"),
                            resources.getString("qkey.msg.msg214"),
                            fcok3);
                    textArea.setText("");
                    textAreaBK = null;
                }
            });

            textArea.setDragEnabled(true);
            textArea.setEditable(true);
            JLabel exp1 = new JLabel(resources.getString("qkey.msg.msg325"));
            addDropTargetListenerToComponents(new QBrowserDropTargetListener(), this);

            button_panel.setBorder(BorderFactory.createEtchedBorder());
            button_panel.setLayout(new BorderLayout());
            button_panel.add(BorderLayout.WEST, exp1);
            button_panel.add(BorderLayout.EAST, jbt);
            add(BorderLayout.CENTER, button_panel);
        }


    }

    /**
     * A panel with a text area that knows how to format and display
     * a HashMap of values.
     */
    class PropertyPanel extends JPanel {

        JLabel label = null;
        JTextArea textArea = null;
        byte[] textAreaBK = null;
        JScrollPane areaScrollPane = null;

        PropertyPanel() {
            super(true);
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());

            label = new JLabel();

            textArea = new JTextArea();
            
            //textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setBackground(Color.WHITE);
            textArea.setDragEnabled(true);
            

            areaScrollPane = new JScrollPane(textArea);
            areaScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            areaScrollPane.setPreferredSize(new Dimension(500, 200));

            add(BorderLayout.NORTH, label);
            add(BorderLayout.SOUTH, areaScrollPane);
        }

        void setTitle(String title) {
            label.setText(title);
        }

        /**
         * Display a HashMap in the text window
         */
        void load(HashMap map) {

            StringBuffer buf = new StringBuffer();

            Set entries = map.entrySet();
            Map.Entry entry = null;
            Iterator iter = entries.iterator();
            while (iter.hasNext()) {
                entry = (Map.Entry) iter.next();
                String key = entry.getKey().toString();

                Object o = entry.getValue();
                String value = "";
                if (o != null) {
                    value = o.toString();
                }

                buf.append(key).append("=");
                buf.append(value).append("\n");
            }

            textArea.setText(buf.toString());

            areaScrollPane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));

        }

        void load2(HashMap map) {

            StringBuffer buf = new StringBuffer();

            Set entries = map.entrySet();
            Map.Entry entry = null;
            Iterator iter = entries.iterator();
            while (iter.hasNext()) {
                entry = (Map.Entry) iter.next();
                String key = entry.getKey().toString();

                Object o = entry.getValue();
                String value = "";
                if (o != null) {
                    value = o.toString();
                }

                buf.append(key).append(" / ");
                buf.append(value).append("\n");
            }

            textArea.setText(buf.toString());

            areaScrollPane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));

        }

        void setProperty(String key, String value) {
            HashMap innermap = getProperties();
            innermap.put(key, value);
            Iterator iinermap = innermap.entrySet().iterator();
            StringBuffer sb = new StringBuffer();
            while(iinermap.hasNext()) {
                Map.Entry me = (Map.Entry)iinermap.next();
                String ckey = (String)me.getKey();
                String cvalue = (String)me.getValue();
                sb.append(ckey).append("=").append(cvalue).append("\n");
            }

            textArea.setText(sb.toString());

        }

        /**
         * Display text in the text window
         */
        void load(String s) {
            textArea.setText(s);
        }

        //セットされたプロパティを解析してHashMapに入れる
        //HashMapの中身は基本的に、key(String), value(String)
        HashMap getProperties() {

            HashMap result = new HashMap();
            //TextAreaの中身をまずは解析

            StringTokenizer pst = new StringTokenizer(textArea.getText(), "\n");
            while (pst.hasMoreTokens()) {

                ArrayList junban = new ArrayList();
                String ppp = pst.nextToken();

                StringTokenizer st = new StringTokenizer(ppp);
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    StringTokenizer bst = new StringTokenizer(item, "=");

                    while (bst.hasMoreTokens()) {
                        String bitem = bst.nextToken();
                        junban.add(bitem);
                    }

                }

                int count = 0;
                String key = null;
                String value = null;
                for (int i = 0; i < junban.size(); i++) {
                    count++;

                    if (count == 1) {
                        key = new String((String) junban.get(i));
                    } else if (count == 2) {
                        count = 0;
                        value = new String((String) junban.get(i));
                        result.put(key, value);
                    }
                }

            }

            return result;

        }


    }

    public void adjustRowHeight(JTable targetTable, int size) {
        for (int i = 0; i < targetTable.getRowCount();i++) {
            targetTable.setRowHeight(i, size);
        }
    }







    class MapMessagePropertyPanel extends JPanel {

        JLabel label = null;

        MapMessagePropertyPanel(MapMessage srcmsg) {
            super(true);
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());

            label = new JLabel();

            JPanel header_header_container = new JPanel();
            header_header_container.setLayout(new BorderLayout());

            JLabel header_header_label = new JLabel(resources.getString("qkey.msg.msg024"));
            JPanel hbutton_container = new JPanel();
            JButton hplus_button = new JButton("+");
            hplus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    MapMessageInputProperty newmm = mapm_property_table.createMapMessageInputProperty();
                    //リスナの追加
                    JComboBox jcb = (JComboBox)newmm.getType_combo_box();
                    jcb.addItemListener(new MapMessageTypeComboBoxItemListener());
                    mapm_property_table.add_one_row(newmm);
            }

            });

            JButton hminus_button = new JButton("-");
            hminus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    TableCellEditor tce = mTable.getCellEditor();

                    if (tce != null)
                    tce.stopCellEditing();
                    int sel_row = mTable.getSelectedRow();
                    if (sel_row < 0) sel_row = 0;
                    if (mapm_property_table.getRowCount() > 0)
                    mapm_property_table.deletePropertyAtRow(sel_row);
            }

            });
            hbutton_container.add(hplus_button);
            hbutton_container.add(hminus_button);

            header_header_container.setBorder(BorderFactory.createEtchedBorder());

            header_header_container.add(label, BorderLayout.CENTER);
            header_header_container.add(hbutton_container, BorderLayout.EAST);

            if (srcmsg == null) {
               mapm_property_table = new MapMessageInputTable(1);
               mapm_property_table.setItemListenerInComboBoxAt(0, new MapMessageTypeComboBoxItemListener());
            } else {
               mapm_property_table = new MapMessageInputTable(0);
               mapm_property_table.load(srcmsg);
               mapm_property_table.fireTableDataChanged();
               for (int mi = 0; mi < mapm_property_table.getRowCount(); mi++) {
                   mapm_property_table.setItemListenerInComboBoxAt(mi, new MapMessageTypeComboBoxItemListener());
                   mapm_property_table.setMouseListenerInTextAreaAt(mi, new MapMessageStringPropertyMouseListener());
                   mapm_property_table.setActionListenerInButtonAt(mi, new FileLoadingButtonListener2());
               }
            }
            mapm_property_table.setOya(this);

            for (int mi = 0; mi < mapm_property_table.getRowCount(); mi++) {
                MapMessageInputProperty newmm = (MapMessageInputProperty)mapm_property_table.getPropertyAtRow(mi);
                JComboBox newmmcombobox = (JComboBox)newmm.getType_combo_box();
                newmmcombobox.addItemListener(new MapMessageTypeComboBoxItemListener());
            }

            //newmessageFrame.getContentPane().add(BorderLayout.CENTER, mpropertyPanel);
            mTable = new QBTable(mapm_property_table);
            mTable.setRowHeight(20);
            mTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            mTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());

            mTable.setPreferredScrollableViewportSize(new Dimension(500,170));

            TableColumn pcolumn1 = mTable.getColumnModel().getColumn(0);
            mapmdce0 = new DefaultCellEditor(new JTextField());
            mapmdce0.setClickCountToStart(1);

            pcolumn1.setCellEditor(mapmdce0);

            TableColumn pcolumn2 = mTable.getColumnModel().getColumn(1);

            pcolumn2.setPreferredWidth(10);
            ListCellEditor lce2 = new ListCellEditor();
            lce2.setClickCountToStart(0);
            pcolumn2.setCellEditor(lce2);
            pcolumn2.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

            //mapmdce3 = new PropTableCellEditor();
            mapmdce3 = new ListCellEditor();
            TableColumn pcolumn3 = mTable.getColumnModel().getColumn(2);
            mapmdce3.setClickCountToStart(0);
            mapmdce3.addCellEditorListener(new CellEditorListener() {

                public void editingStopped(ChangeEvent e) {
                    //System.out.println("Editing Stopped mapmdce3 : " + mapmdce3.getCellEditorValue());
                    //今表の中にある全部の行をvalidateする

                    try {

                    //重複チェック用
                    HashSet keycheck = new HashSet();

                        for (int hi = 0; hi < mapm_property_table.getRowCount(); hi++) {
                            MapMessageInputProperty hpr = mapm_property_table.getPropertyAtRow(hi);

                            JComboBox jcb = (JComboBox)hpr.getType_combo_box();
                            hpr.setProperty_type((String)jcb.getSelectedItem());

                            if (hpr.getKey() != null) {
                                if (keycheck.contains(hpr.getKey())) {
                                    throw new QBrowserPropertyException("Q0019" + MAGIC_SEPARATOR + hpr.getKey() + MAGIC_SEPARATOR + hpr.getProperty_type() + MAGIC_SEPARATOR + hpr.getProperty_value());
                                } else {
                                    //System.out.println("abc");
                                    keycheck.add(hpr.getKey());
                                }
                            }

                            hpr.selfValidate();

                        }

                        newmessage1stpanel_mapm_props_ok = true;
                    } catch (QBrowserPropertyException qpe) {
                        last_mapmessage_prop_validate_error = qpe.getMessage();
                        newmessage1stpanel_mapm_props_ok = false;
                    }

                }

                public void editingCanceled(ChangeEvent e) {}
            });
            pcolumn3.setCellEditor(mapmdce3);
            pcolumn3.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

            TableColumn column4 = mTable.getColumnModel().getColumn(3);
            column4.setPreferredWidth(30);

            DownloadCellEditor dce4 = new DownloadCellEditor();
            DownloadCellRenderer dcr4 = new DownloadCellRenderer();
            
            dce4.setClickCountToStart(0);
            column4.setCellEditor(dce4);
            column4.setCellRenderer(dcr4);

            JScrollPane tablePane = new JScrollPane(mTable);
            tablePane.setPreferredSize(new Dimension(500, 232));
            add(BorderLayout.NORTH, header_header_container);
            add(BorderLayout.CENTER, tablePane);
        }

        void setTitle(String title) {
            label.setText(title);
        }


        /**
         * Pad a string to the specified width, right justified.
         * If the string is longer than the width you get back the
         * original string.
         */
        String pad(String s, int width) {

            // Very inefficient, but we don't care
            StringBuffer sb = new StringBuffer();
            int padding = width - s.length();

            if (padding <= 0) {
                return s;
            }

            while (padding > 0) {
                sb.append(" ");
                padding--;
            }
            sb.append(s);
            return sb.toString();
        }
    }


    int calcPreferedWidth(int size) {
        String intv = String.valueOf(size);
        return (intv.length() * 10) + 10;

    }

    class MapMessageStringPropertyMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {

                string_edit_area = new JTextArea();
                string_edit_area.setColumns(90);
                string_edit_area.setRows(30);
                string_edit_area.setLineWrap(false);
                addDropTargetListenerToComponents(new QBrowserDropTargetListener2(string_edit_area), string_edit_area);

                //ダブルクリックでエディタ登場
                if (e.getClickCount() == 2) {

                    mapmdce3.stopCellEditing();

                    int rn = mapm_property_table.findRowNumberFromJTextArea((JTextArea)e.getSource());
                    Object gv2 = mapm_property_table.getValueAt(rn, 2);
                    string_edit_area.setText(((JTextArea) gv2).getText());
                    popupStringEditDialog(resources.getString("qkey.msg.msg229"), createSearchableTextArea(string_edit_area),
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter), rn);
                }

            }
        }
    }

    class StreamMessageStringPropertyMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {

                string_edit_area = new JTextArea();
                string_edit_area.setColumns(90);
                string_edit_area.setRows(30);
                string_edit_area.setLineWrap(false);
                addDropTargetListenerToComponents(new QBrowserDropTargetListener2(string_edit_area), string_edit_area);

                //ダブルクリックでエディタ登場
                if (e.getClickCount() == 2) {

                    smdce3.stopCellEditing();

                    int rn = sm_property_table.findRowNumberFromJTextArea((JTextArea)e.getSource());
                    Object gv2 = sm_property_table.getValueAt(rn, 2);
                    string_edit_area.setText(((JTextArea) gv2).getText());
                    popupStringEditDialogForStreamMessage(resources.getString("qkey.msg.msg229"), createSearchableTextArea(string_edit_area),
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter), rn);
                }

            }
        }
    }

class QBrowserKey
{

    private final Properties props = new Properties();
    public final String KEY_STROKE_FILE = "keystroke";

    public QBrowserKey()
    {
        String packageName = getCallerPackage();
        String location;
        if(OSDetector.isMac())
        {
            //暫定的にwinと同じにしておく。
            location = packageName + "/" + "keystroke" + "_win.properties";
        } else
        {
            location = packageName + "/" + "keystroke" + "_win.properties";
        }
        java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(location);
        if(null != is)
        {
            try
            {
                props.load(is);
            }
            catch(IOException ioe) { }
        }
    }

    public void setKeyStroke(JMenuItem menuItem, String key)
    {
        String property = getProperty(key);
        if(null != property && !"".equals(property))
        {
            if(property.startsWith("_"))
            {
                menuItem.setMnemonic(property.charAt(1));
                if(-1 == menuItem.getText().toUpperCase().indexOf(property.charAt(1)))
                {
                    menuItem.setText(menuItem.getText() + "(" + property.charAt(1) + ")");
                }
            } else
            {
                menuItem.setAccelerator(toKeyStroke(property));
            }
        }
    }
    

    public void setKeyStroke(JMenu menuItem, String key)
    {
        String property = getProperty(key);
        if(null != property && !"".equals(property))
        {

                menuItem.setMnemonic(property.charAt(1));
                if(-1 == menuItem.getText().toUpperCase().indexOf(property.charAt(1)))
                {
                    menuItem.setText(menuItem.getText() + "(" + property.charAt(1) + ")");
                }
        }
    }

    private String getCallerPackage()
    {
        String callerClass = (new Throwable()).getStackTrace()[2].getClassName();
        return callerClass.substring(0, callerClass.lastIndexOf(".")).replaceAll("\\.", "/");
    }

    public char getMnemonic(String key)
    {
        return getProperty(key).charAt(0);
    }

    public KeyStroke getKeyStroke(String key)
    {
        return toKeyStroke(getProperty(key));
    }

    public String getProperty(String key)
    {
        return props.getProperty(key);
    }

    public KeyStroke toKeyStroke(String keyStrokeText)
    {
        if(null != keyStrokeText)
        {
            keyStrokeText = keyStrokeText.replaceAll("command", "meta");
            keyStrokeText = keyStrokeText.replaceAll("cmd", "meta");
            keyStrokeText = keyStrokeText.replaceAll("option", "alt");
            keyStrokeText = keyStrokeText.replaceAll("ctl", "control");
            keyStrokeText = keyStrokeText.replaceAll("ctrl", "control");
            keyStrokeText = keyStrokeText.replaceAll("opt", "alt");
        }
        return KeyStroke.getKeyStroke(keyStrokeText);
    }

    public void apply(Object obj)
    {
        Field fields[] = obj.getClass().getDeclaredFields();
          //Field fields[] = obj.getClass().getFields();

        for(int i = 0; i < fields.length; i++)
        {
            String fieldName = fields[i].getName();
            Class type = fields[i].getType();
            try
            {
                Object theObject = fields[i].get(obj);

                if (theObject instanceof JMenu) {
                    JMenu menu = (JMenu)theObject;
                    String key = menu.getText();
                    menu.setText(resources.getString(key));
                    setKeyStroke(menu, key);
                } else
                if(theObject instanceof JMenuItem)
                {
                    JMenuItem menuItem = (JMenuItem)theObject;
                    String key = menuItem.getText();
                    menuItem.setText(resources.getString(key));
                    setKeyStroke(menuItem, key);
                } 
            }
            catch(IllegalAccessException iae) { iae.printStackTrace(); }
        }

    }

    public boolean isPressed(String key, KeyEvent event)
    {
        return getKeyStroke(key).equals(KeyStroke.getKeyStrokeForEvent(event));
    }

    public boolean isPressed(String key1, String key2, KeyEvent event)
    {
        return isPressed(key1, event) || isPressed(key2, event);
    }
}

    class UserPropertyStringPropertyMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {

                string_edit_area = new JTextArea();
                string_edit_area.setColumns(90);
                string_edit_area.setRows(30);
                string_edit_area.setLineWrap(false);
                addDropTargetListenerToComponents(new QBrowserDropTargetListener2(string_edit_area), string_edit_area);

                //ダブルクリックでエディタ登場
                if (e.getClickCount() == 2) {

                    pdce3.stopCellEditing();

                    int rn = property_table.findRowNumberFromJTextArea((JTextArea)e.getSource());
                    Object gv2 = property_table.getValueAt(rn, 2);
                    string_edit_area.setText(((JTextArea) gv2).getText());
                    popupStringEditDialogForUserProperty(resources.getString("qkey.msg.msg229"), createSearchableTextArea(string_edit_area),
                            QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.ConfigPrinter), rn);
                }

            }
        }
    }

    class MapMessagePropertyForDownloadPanel extends JPanel {

        BytesForDownloadPropertyTable mapm_download_property_table;
        JTable mdTable;

        public BytesForDownloadPropertyTable getMapm_download_property_table() {
            return mapm_download_property_table;
        }

        public JTable getMdTable() {
            return mdTable;
        }

        MapMessagePropertyForDownloadPanel() {
            super(true);
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());

            JPanel header_header_container = new JPanel();
            header_header_container.setLayout(new BorderLayout());

            JLabel header_header_label = new JLabel(resources.getString("qkey.msg.msg190"));


            header_header_container.setBorder(BorderFactory.createEtchedBorder());

            header_header_container.add(header_header_label, BorderLayout.CENTER);


            mapm_download_property_table = new BytesForDownloadPropertyTable(5);
            mapm_download_property_table.setOya(this);

            //newmessageFrame.getContentPane().add(BorderLayout.CENTER, mpropertyPanel);
            mdTable = new JTable(mapm_download_property_table);
            mdTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            HeaderRenderer01 hr = new HeaderRenderer01();
            mdTable.getColumnModel().getColumn(0).setHeaderRenderer(hr);
            mdTable.getColumnModel().getColumn(1).setHeaderRenderer(hr);
            mdTable.getColumnModel().getColumn(2).setHeaderRenderer(hr);

            //TODO プロパティの多さによって最大200くらいまで動的拡張するようにする
            mdTable.setPreferredScrollableViewportSize(new Dimension(500,120));

            JScrollPane tablePane = new JScrollPane(mdTable);
            tablePane.setPreferredSize(new Dimension(500, 150));
            add(BorderLayout.NORTH, header_header_container);
            add(BorderLayout.CENTER, tablePane);
        }
        

    }

    class StreamMessagePropertyForDownloadPanel extends JPanel {

        StreamMessageBytesForDownloadPropertyTable sm_download_property_table = null;
        JTable sdTable = null;

        public JTable getSdTable() {
            return sdTable;
        }

        public StreamMessageBytesForDownloadPropertyTable getStreamMessageBytesForDownloadPropertyTable() {
            return sm_download_property_table;
        }

        StreamMessagePropertyForDownloadPanel() {
            super(true);
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());

            JPanel header_header_container = new JPanel();
            header_header_container.setLayout(new BorderLayout());

            JLabel header_header_label = new JLabel(resources.getString("qkey.msg.msg190"));


            header_header_container.setBorder(BorderFactory.createEtchedBorder());

            header_header_container.add(header_header_label, BorderLayout.CENTER);

            sm_download_property_table = new StreamMessageBytesForDownloadPropertyTable(5);
            sm_download_property_table.setOya(this);

            //newmessageFrame.getContentPane().add(BorderLayout.CENTER, mpropertyPanel);
            sdTable = new JTable(sm_download_property_table);
            sdTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            HeaderRenderer01 hr = new HeaderRenderer01();
            sdTable.getColumnModel().getColumn(0).setHeaderRenderer(hr);
            sdTable.getColumnModel().getColumn(1).setHeaderRenderer(hr);
            sdTable.getColumnModel().getColumn(2).setHeaderRenderer(hr);

            //TODO プロパティの多さによって最大200くらいまで動的拡張するようにする
            sdTable.setPreferredScrollableViewportSize(new Dimension(500,120));

            JScrollPane tablePane = new JScrollPane(sdTable);
            tablePane.setPreferredSize(new Dimension(500, 150));
            add(BorderLayout.NORTH, header_header_container);
            add(BorderLayout.CENTER, tablePane);
        }


    }

    class MapMessageAllPropertiesPanel extends JPanel {

        MapMessageAllPropertiesTable mapm_all_property_table;
        JTable maTable;

        MapMessageAllPropertiesPanel() {
            super(true);
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());

            JPanel header_header_container = new JPanel();
            header_header_container.setLayout(new BorderLayout());

            JLabel header_header_label = new JLabel(resources.getString("qkey.msg.msg190"));
            JLabel header_header_label2 = new JLabel(resources.getString("qkey.msg.msg209"));


            //header_header_container.setBorder(BorderFactory.createEtchedBorder());

            header_header_label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            header_header_label2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            header_header_container.add(header_header_label, BorderLayout.SOUTH);
            header_header_container.add(header_header_label2, BorderLayout.CENTER);


            mapm_all_property_table = new MapMessageAllPropertiesTable(0);
            mapm_all_property_table.setOya(this);

            //newmessageFrame.getContentPane().add(BorderLayout.CENTER, mpropertyPanel);
            maTable = new JTable(mapm_all_property_table);
            maTable.setRowHeight(20);
            maTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            maTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());
            HeaderRenderer01 hr = new HeaderRenderer01();
            maTable.getColumnModel().getColumn(0).setHeaderRenderer(hr);
            maTable.getColumnModel().getColumn(1).setHeaderRenderer(hr);
            maTable.getColumnModel().getColumn(2).setHeaderRenderer(hr);
            maTable.getColumnModel().getColumn(3).setHeaderRenderer(hr);
            maTable.setPreferredScrollableViewportSize(new Dimension(500,120));
            maTable.setColumnSelectionAllowed(false);

            JScrollPane tablePane = new JScrollPane(maTable);
            tablePane.setPreferredSize(new Dimension(500, 150));
            add(BorderLayout.NORTH, header_header_container);
            add(BorderLayout.CENTER, tablePane);
        }

        public  MapMessageAllPropertiesTable getMapm_all_property_table() {
            return mapm_all_property_table;
        }

        public JTable getMaTable() {
            return maTable;
        }


    }

    class StreamMessageAllPropertiesPanel extends JPanel {

        StreamMessageAllPropertiesTable sm_all_property_table;
        JTable saTable;

        StreamMessageAllPropertiesPanel() {
            super(true);
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());

            JPanel header_header_container = new JPanel();
            header_header_container.setLayout(new BorderLayout());

            JLabel header_header_label = new JLabel(resources.getString("qkey.msg.msg190"));
            JLabel header_header_label2 = new JLabel(resources.getString("qkey.msg.msg209"));


            //header_header_container.setBorder(BorderFactory.createEtchedBorder());

            header_header_label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            header_header_label2.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            header_header_container.add(header_header_label, BorderLayout.SOUTH);
            header_header_container.add(header_header_label2, BorderLayout.CENTER);


            sm_all_property_table = new StreamMessageAllPropertiesTable(0);
            sm_all_property_table.setOya(this);

            //newmessageFrame.getContentPane().add(BorderLayout.CENTER, mpropertyPanel);
            saTable = new JTable(sm_all_property_table);
            saTable.setRowHeight(20);
            saTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            saTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());
            HeaderRenderer01 hr = new HeaderRenderer01();
            saTable.getColumnModel().getColumn(0).setHeaderRenderer(hr);
            saTable.getColumnModel().getColumn(1).setHeaderRenderer(hr);
            saTable.getColumnModel().getColumn(2).setHeaderRenderer(hr);
            saTable.getColumnModel().getColumn(3).setHeaderRenderer(hr);
            saTable.setPreferredScrollableViewportSize(new Dimension(500,120));
            saTable.setColumnSelectionAllowed(false);

            JScrollPane tablePane = new JScrollPane(saTable);
            tablePane.setPreferredSize(new Dimension(500, 150));
            add(BorderLayout.NORTH, header_header_container);
            add(BorderLayout.CENTER, tablePane);
        }

        public JTable getSaTable() {
            return saTable;
        }

        public StreamMessageAllPropertiesTable getStreamMessageAllPropertiesTable() {
            return sm_all_property_table;
        }


    }

    class StreamMessagePropertyPanel extends JPanel {

        JLabel label = null;

        StreamMessagePropertyPanel(StreamMessage srcmsg) {
            super(true);
            setBorder(BorderFactory.createEtchedBorder());
            setLayout(new BorderLayout());

            label = new JLabel();

            JPanel header_header_container = new JPanel();
            header_header_container.setLayout(new BorderLayout());

            JLabel header_header_label = new JLabel(resources.getString("qkey.msg.msg024"));
            JPanel hbutton_container = new JPanel();
            JButton hplus_button = new JButton("+");
            hplus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    StreamMessageInputProperty newsm = sm_property_table.createStreamMessageInputProperty();
                    //リスナの追加
                    JComboBox jcb = (JComboBox)newsm.getType_combo_box();
                    jcb.addItemListener(new StreamMessageTypeComboBoxItemListener());
                    sm_property_table.add_one_row(newsm);
                    sm_property_table.renumberAll();
                    
            }

            });

            JButton hminus_button = new JButton("-");
            hminus_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    TableCellEditor tce = sTable.getCellEditor();

                    if (tce != null)
                    tce.stopCellEditing();
                    int sel_row = sTable.getSelectedRow();
                    if (sel_row < 0) sel_row = 0;
                    if (sm_property_table.getRowCount() > 0)
                    sm_property_table.deletePropertyAtRow(sel_row);

                    //番号振りなおし
                    sm_property_table.renumberAll();
            }

            });
            hbutton_container.add(hplus_button);
            hbutton_container.add(hminus_button);

            header_header_container.setBorder(BorderFactory.createEtchedBorder());

            header_header_container.add(label, BorderLayout.CENTER);
            header_header_container.add(hbutton_container, BorderLayout.EAST);

            if (srcmsg == null) {
               sm_property_table = new StreamMessageInputTable(1);
               sm_property_table.setItemListenerInComboBoxAt(0, new StreamMessageTypeComboBoxItemListener());
            } else {
               sm_property_table = new StreamMessageInputTable(0);
               sm_property_table.load(srcmsg);
               sm_property_table.fireTableDataChanged();
               for (int mi = 0; mi < sm_property_table.getRowCount(); mi++) {
                   sm_property_table.setItemListenerInComboBoxAt(mi, new StreamMessageTypeComboBoxItemListener());
                   sm_property_table.setMouseListenerInTextAreaAt(mi, new StreamMessageStringPropertyMouseListener());
                   sm_property_table.setActionListenerInButtonAt(mi, new FileLoadingButtonListener2());
               }
            }
            sm_property_table.setOya(this);

            for (int mi = 0; mi < sm_property_table.getRowCount(); mi++) {
                StreamMessageInputProperty newsm = (StreamMessageInputProperty)sm_property_table.getPropertyAtRow(mi);
                JComboBox newsmcombobox = (JComboBox)newsm.getType_combo_box();
                newsmcombobox.addItemListener(new StreamMessageTypeComboBoxItemListener());
            }

            //newmessageFrame.getContentPane().add(BorderLayout.CENTER, mpropertyPanel);
            sTable = new QBTable(sm_property_table);
            sTable.setRowHeight(20);
            sTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            sTable.setDefaultRenderer(Object.class, new StripeTableRendererForProperty());

            sTable.setPreferredScrollableViewportSize(new Dimension(500,170));

            TableColumn pcolumn1 = sTable.getColumnModel().getColumn(0);
            DefaultCellEditor smdce0 = new DefaultCellEditor(new JTextField());
            smdce0.setClickCountToStart(1);

            pcolumn1.setCellEditor(smdce0);
            pcolumn1.setCellRenderer(new com.qbrowser.render.ListCellRenderer());
            pcolumn1.setWidth(100);

            TableColumn pcolumn2 = sTable.getColumnModel().getColumn(1);

            pcolumn2.setPreferredWidth(10);
            ListCellEditor lce2 = new ListCellEditor();
            lce2.setClickCountToStart(0);
            pcolumn2.setCellEditor(lce2);
            pcolumn2.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

            //mapmdce3 = new PropTableCellEditor();
            smdce3 = new ListCellEditor();
            TableColumn pcolumn3 = sTable.getColumnModel().getColumn(2);
            smdce3.setClickCountToStart(0);
            smdce3.addCellEditorListener(new CellEditorListener() {

                public void editingStopped(ChangeEvent e) {
                    //System.out.println("Editing Stopped mapmdce3 : " + mapmdce3.getCellEditorValue());
                    //今表の中にある全部の行をvalidateする
                    validateAllStreamMessageData();

                }

                public void editingCanceled(ChangeEvent e) {}
            });
            pcolumn3.setCellEditor(smdce3);
            pcolumn3.setCellRenderer(new com.qbrowser.render.ListCellRenderer());

            TableColumn column4 = sTable.getColumnModel().getColumn(3);
            column4.setPreferredWidth(30);

            DownloadCellEditor dce4 = new DownloadCellEditor();
            DownloadCellRenderer dcr4 = new DownloadCellRenderer();

            dce4.setClickCountToStart(0);
            column4.setCellEditor(dce4);
            column4.setCellRenderer(dcr4);

            JScrollPane tablePane = new JScrollPane(sTable);
            tablePane.setPreferredSize(new Dimension(500, 232));
            add(BorderLayout.NORTH, header_header_container);
            add(BorderLayout.CENTER, tablePane);
        }

        void setTitle(String title) {
            label.setText(title);
        }


        /**
         * Pad a string to the specified width, right justified.
         * If the string is longer than the width you get back the
         * original string.
         */
        String pad(String s, int width) {

            // Very inefficient, but we don't care
            StringBuffer sb = new StringBuffer();
            int padding = width - s.length();

            if (padding <= 0) {
                return s;
            }

            while (padding > 0) {
                sb.append(" ");
                padding--;
            }
            sb.append(s);
            return sb.toString();
        }
    }

    class CreateQueueListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            final JTextField queue_name_input = new JTextField();
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel(resources.getString("qkey.msg.msg344"));
            panel.add(BorderLayout.NORTH, lbl);
            panel.add(BorderLayout.CENTER, queue_name_input);

            popupConfirmationDialog(resources.getString("qkey.msg.msg345"), panel, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "1.png"),
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            confirmDialog.dispose();
                            confirmDialog = null;
                            String target_queue_name = queue_name_input.getText();
                            if (target_queue_name != null && target_queue_name.length() != 0) {
                                try {


                                    String command_prefix = "create dst -t ";
                                    String dtype = "q";

                                    StringBuffer result = new StringBuffer();
                                    String exitcode = internalruncommand(command_prefix, dtype, target_queue_name, result);


                                    String resultstr = result.toString();
                                    StringTokenizer rest = new StringTokenizer(resultstr, "\n");
                                    ArrayList restarray = new ArrayList();
                                    int maxcolumnsize = 0;
                                    while (rest.hasMoreTokens()) {
                                        String key = rest.nextToken();
                                        restarray.add(key);
                                        if (key.length() > maxcolumnsize) {
                                            maxcolumnsize = key.length();
                                        }
                                    }

                                    TextArea ta = new TextArea("", (restarray.size() + 2), (maxcolumnsize + 10), TextArea.SCROLLBARS_BOTH);

                                    ta.setText(result.toString());
                                    //doBrowse();
                                    //popupMessageDialog("cmd", ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "1.png"));

                                    destinationNamesForDisplayQueue = new ArrayList();
                                    destinationNamesForDisplayTopic = new ArrayList();
                                    collectDestination();
                                    qBox.setSelectedItem(target_queue_name + QUEUE_SUFFIX);
                                    refreshMsgTableWithDestName();
                                    initTreePane();
                                } catch (Exception e) {
                                    popupErrorMessageDialog(e);
                                }

                            }

                        }
                    });

        }
    }

        String internalruncommand(String command_prefix ,String destType, String targetname, StringBuffer result) {

            String cmd = command_prefix + destType + " -n " + targetname + " -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);
            ar.add("-f");

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);
            return exitcode;
        }

        String internalbrokercommand(String brokercommand , StringBuffer result) {

            String cmd = brokercommand + " -b " + serverHost + ":" + serverPort + " -u " + serverUser + " -passfile ";
            StringTokenizer st = new StringTokenizer(cmd);
            ArrayList ar = new ArrayList();
            while (st.hasMoreTokens()) {
                ar.add(st.nextToken());
            }
            ar.add(real_passfile_path);
            ar.add("-f");

            String[] args = new String[ar.size()];
            ar.toArray(args);

            BrokerCmdProperties brokerCmdProps = null;

            try {
                brokerCmdProps = BrokerCmdOptionParser.parseArgs(args);
            } catch (Exception oe) {
                System.err.println(oe.getMessage());
            }

            jp.sun.util.CmdRunner cmdRunner = new jp.sun.util.CmdRunner(brokerCmdProps);
            jp.sun.util.BrokerCmdPrinter.sb = result;
            String exitcode = cmdRunner.runCommands(result);
            return exitcode;
        }

    class BrokerCommandListener implements ActionListener {

        boolean withrestart;

        public BrokerCommandListener(boolean vwithrestart) {
            withrestart = vwithrestart;
        }

        public void actionPerformed(ActionEvent event) {

           JTextArea jta = new JTextArea("", 3, 35);
           String vtitle = null;
           String msg = resources.getString("qkey.msg.msg365");
           msg += bkr_instance_name;
           if (withrestart) {
               msg += resources.getString("qkey.msg.msg367");
               vtitle = resources.getString("qkey.msg.msg369");
           } else {
               msg += resources.getString("qkey.msg.msg366");
               vtitle = resources.getString("qkey.msg.msg368");
           }
           jta.setText(msg);
           jta.setEditable(false);
           jta.setBackground(Color.WHITE);




            popupConfirmationDialog(vtitle, jta , QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Disconnect),
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            confirmDialog.dispose();
                            confirmDialog = null;

                                try {


                                    String bkr_command = null;
                                    if (withrestart) {
                                        bkr_command = "restart bkr ";
                                        
                                    } else {

                                        bkr_command = "shutdown bkr ";
                                        
                                    }

                                    StringBuffer result = new StringBuffer();
                                    String exitcode = internalbrokercommand(bkr_command, result);


                                    String resultstr = result.toString();
                                    StringTokenizer rest = new StringTokenizer(resultstr, "\n");
                                    ArrayList restarray = new ArrayList();
                                    int maxcolumnsize = 0;
                                    while (rest.hasMoreTokens()) {
                                        String key = rest.nextToken();
                                        restarray.add(key);
                                        if (key.length() > maxcolumnsize) {
                                            maxcolumnsize = key.length();
                                        }
                                    }

                                    TextArea ta = new TextArea("", (restarray.size() + 2), (maxcolumnsize + 10), TextArea.SCROLLBARS_BOTH);

                                    ta.setText(result.toString());
                                    popupMessageDialog("cmd", ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "1.png"));

                                    if (!withrestart) {
                                        moveToDisconnectStatus();
                                    }


                                } catch (Exception e) {
                                    popupErrorMessageDialog(e);
                                }

                            

                        }
                    });

        }
    }


    class CreateTopicListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            final JTextField topic_name_input = new JTextField();
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel(resources.getString("qkey.msg.msg349"));
            panel.add(BorderLayout.NORTH, lbl);
            panel.add(BorderLayout.CENTER, topic_name_input);

            popupConfirmationDialog(resources.getString("qkey.msg.msg348"), panel, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "6.png"),
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            confirmDialog.dispose();
                            confirmDialog = null;
                            String target_topic_name = topic_name_input.getText();
                            if (target_topic_name != null && target_topic_name.length() != 0) {
                                try {

                                    String command_prefix = "create dst -t ";
                                    String dtype = "t";

                                    StringBuffer result = new StringBuffer();
                                    String exitcode = internalruncommand(command_prefix, dtype, target_topic_name, result);


                                    String resultstr = result.toString();
                                    StringTokenizer rest = new StringTokenizer(resultstr, "\n");
                                    ArrayList restarray = new ArrayList();
                                    int maxcolumnsize = 0;
                                    while (rest.hasMoreTokens()) {
                                        String key = rest.nextToken();
                                        restarray.add(key);
                                        if (key.length() > maxcolumnsize) {
                                            maxcolumnsize = key.length();
                                        }
                                    }

                                    //ta.setText(result.toString());
                                    //doBrowse();
                                    //popupMessageDialog("cmd", ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "4.png"));

                                    collectDestination();

                                  //DUMMY
                                  matesakiBox3 = new JComboBox();
                                  importTopicNamesToMATESAKIBOX3();
                                  matesakiBox3.setSelectedItem(target_topic_name);
                                  localstoreBox = new JComboBox();

                                    if (subscribeTemplateBox == null) {
                                        subscribeTemplateBox = new JComboBox();
                                        subscribeTemplateBox.addItemListener(new SubscribeTemplateItemListener());
                                        subscribeTemplateBox.setPreferredSize(new Dimension(250, 20));

                                        ArrayList extracted_history = QBrowserUtil.getHistoryFromFile("subscription_history");
                                        QBrowserUtil.ArrayListToJComboBox(extracted_history, subscribeTemplateBox);
                                    }
                                  subscribeDialog = new JDialog();
                                  importLocalStoreNamesToLOCALSTOREBOX();
                                  localstoreBox.setSelectedItem(resources.getString("qkey.msg.msg275"));
                                  SubscribeOKListener sok = new SubscribeOKListener();
                                  sok.actionPerformed(event);
                                  subscribeDialog = null;


                                  destinationNamesForDisplayQueue = new ArrayList();
                                  destinationNamesForDisplayTopic = new ArrayList();
                                  collectDestination();
                                  initTreePane();
                                  qBox.setSelectedItem(target_topic_name + TOPIC_SUFFIX);
                                  refreshMsgTableWithDestName();
                                } catch (Exception e) {
                                    popupErrorMessageDialog(e);
                                }

                            }

                        }
                    });



        }
    }

    class DeleteQueueListener implements ActionListener {


        public void actionPerformed(ActionEvent event) {

            final TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel(resources.getString("qkey.msg.msg351") + di.destinationName + resources.getString("qkey.msg.msg352"));
            panel.add(BorderLayout.NORTH, lbl);

            popupConfirmationDialog(resources.getString("qkey.msg.msg350"), panel, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "1.png"),
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            confirmDialog.dispose();
                            confirmDialog = null;
                            String selectedQueue = (String)di.destinationName;
                            if (selectedQueue != null && selectedQueue.length() != 0) {
                                try {
                                    String command_prefix = "destroy dst -t ";
                                    String dtype = "q";

                                    StringBuffer result = new StringBuffer();
                                    String exitcode = internalruncommand(command_prefix, dtype, selectedQueue, result);


                                    String resultstr = result.toString();
                                    StringTokenizer rest = new StringTokenizer(resultstr, "\n");
                                    ArrayList restarray = new ArrayList();
                                    int maxcolumnsize = 0;
                                    while (rest.hasMoreTokens()) {
                                        String key = rest.nextToken();
                                        restarray.add(key);
                                        if (key.length() > maxcolumnsize) {
                                            maxcolumnsize = key.length();
                                        }
                                    }

                                    TextArea ta = new TextArea("", (restarray.size() + 2), (maxcolumnsize + 10), TextArea.SCROLLBARS_BOTH);

                                    ta.setText(result.toString());
                                    doBrowse();
                                    //popupMessageDialog("cmd", ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "1.png"));
                                  //パネルが存在している場合クローズ

                                  String dest_with_suffix = selectedQueue + QUEUE_SUFFIX;
                                  removeNamedTabbedPane(dest_with_suffix);
                                  int selidx = tabbedPane.getSelectedIndex();
                                  if (selidx != -1) {
                                    refreshTableOnCurrentSelectedTab();
                                     String tabname = tabbedPane.getTitleAt(selidx);
                                     qBox.setSelectedItem(tabname);
                                  }
                                  removeDestRelatedCache(dest_with_suffix);
                                  destinationNamesForDisplayQueue = new ArrayList();
                                  destinationNamesForDisplayTopic = new ArrayList();
                                  collectDestination();
                                  refreshMsgTableWithDestName();
                                  initTreePane();

                                } catch (Exception e) {
                                    popupErrorMessageDialog(e);
                                }

                            }

                        }
                    });



        }
    }

    class DeleteTopicListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            final TreeIconPanel.DestInfo di = treePane.getSelectedDestInfo();

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel(resources.getString("qkey.msg.msg354") + di.destinationName + resources.getString("qkey.msg.msg355"));
            panel.add(BorderLayout.NORTH, lbl);

            popupConfirmationDialog(resources.getString("qkey.msg.msg353"), panel, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "6.png"),
                    new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            confirmDialog.dispose();
                            confirmDialog = null;
                            String selectedTopic = (String)di.destinationName;
                            if (selectedTopic != null && selectedTopic.length() != 0) {
                                try {
                                    String command_prefix = "destroy dst -t ";
                                    String dtype = "t";

                                    StringBuffer result = new StringBuffer();
                                    String exitcode = internalruncommand(command_prefix, dtype, selectedTopic, result);


                                    String resultstr = result.toString();
                                    StringTokenizer rest = new StringTokenizer(resultstr, "\n");
                                    ArrayList restarray = new ArrayList();
                                    int maxcolumnsize = 0;
                                    while (rest.hasMoreTokens()) {
                                        String key = rest.nextToken();
                                        restarray.add(key);
                                        if (key.length() > maxcolumnsize) {
                                            maxcolumnsize = key.length();
                                        }
                                    }

                                    TextArea ta = new TextArea("", (restarray.size() + 2), (maxcolumnsize + 10), TextArea.SCROLLBARS_BOTH);

                                    ta.setText(result.toString());
                                    doBrowse();
                                    //popupMessageDialog("cmd", ta, QBrowserIconsFactory.getImageIcon(QBrowserIconsFactory.Flagbase + "4.png"));
                                  //パネルが存在している場合クローズ


                                  String dest_with_suffix = selectedTopic + TOPIC_SUFFIX;
                                  removeNamedTabbedPane(dest_with_suffix);

                                  int selidx = tabbedPane.getSelectedIndex();
                                  if (selidx != -1) {
                                    refreshTableOnCurrentSelectedTab();
                                     String tabname = tabbedPane.getTitleAt(selidx);
                                     qBox.setSelectedItem(tabname);
                                  }
                                  removeDestRelatedCache(dest_with_suffix);
                                  stopSubscriberThread(dest_with_suffix);
                                  subscribe_thread_status.remove(dest_with_suffix);

                                  //関連するローカルストアからこのトピック名を削除
                                  ArrayList local_copy_to = lsm.getCopyToListOfTheDestination(dest_with_suffix);
                                  for (int i = 0 ; i < local_copy_to.size(); i++) {
                                      String local_name_without_suffix = (String)local_copy_to.get(i);
                                      LocalStoreProperty lsp = lsm.getLocalStoreProperty(local_name_without_suffix);
                                      lsp.removeFromDests(dest_with_suffix);
                                      lsm.updateAndSaveLocalStoreProperty(lsp);
                                  }
                                  lsm.removeRelatedEntryOfSubscribeDest(dest_with_suffix);


                                  refreshMsgTableWithDestName();
                                  destinationNamesForDisplayQueue = new ArrayList();
                                  destinationNamesForDisplayTopic = new ArrayList();
                                  collectDestination();
                                  initTreePane();

                                } catch (Exception e) {
                                    popupErrorMessageDialog(e);
                                }

                            }

                        }
                    });



        }
    }


}
