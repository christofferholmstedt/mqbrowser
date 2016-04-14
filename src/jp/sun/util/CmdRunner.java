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

/*
 * @(#)CmdRunner.java	1.165 07/12/07
 */

package jp.sun.util;

import com.sun.messaging.jmq.admin.apps.broker.BrokerCmdException;
import com.sun.messaging.jmq.admin.apps.broker.BrokerCmdOptions;
import com.sun.messaging.jmq.admin.apps.broker.BrokerCmdProperties;
import java.io.*;
import java.util.Properties;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Date;
import java.text.DateFormat;
import javax.jms.DeliveryMode;

import com.sun.messaging.jmq.admin.util.Globals;
import com.sun.messaging.jmq.admin.resources.AdminResources;
import com.sun.messaging.jmq.admin.bkrutil.BrokerAdmin;
import com.sun.messaging.jmq.admin.bkrutil.BrokerAdminException;
import com.sun.messaging.jmq.admin.bkrutil.BrokerAdminUtil;
import com.sun.messaging.jmq.admin.bkrutil.BrokerConstants;
import com.sun.messaging.jmq.io.MetricCounters;
import com.sun.messaging.jmq.io.DestMetricsCounters;
import com.sun.messaging.jmq.util.DestType;
import com.sun.messaging.jmq.util.DestState;
import com.sun.messaging.jmq.util.Password;
import com.sun.messaging.jmq.util.ServiceState;
import com.sun.messaging.jmq.util.ServiceType;
import com.sun.messaging.jmq.util.SizeString;
import com.sun.messaging.jmq.util.DestLimitBehavior;
import com.sun.messaging.jmq.util.ClusterDeliveryPolicy;
import com.sun.messaging.jmq.util.DebugPrinter;
import com.sun.messaging.jmq.util.admin.MessageType;
import com.sun.messaging.jmq.util.admin.DestinationInfo;
import com.sun.messaging.jmq.util.admin.DurableInfo;
import com.sun.messaging.jmq.util.admin.ServiceInfo;
import com.sun.messaging.jmq.jmsclient.GenericPortMapperClient;
import com.sun.messaging.jmq.admin.apps.console.event.AdminEventListener;
import com.sun.messaging.jmq.admin.apps.console.event.AdminEvent;
import com.sun.messaging.jmq.admin.apps.console.event.BrokerCmdStatusEvent;
import com.sun.messaging.jmq.admin.apps.objmgr.ObjMgr;
import com.sun.messaging.jms.management.server.BrokerClusterInfo;
import com.sun.messaging.jms.management.server.BrokerState;
import com.sun.messaging.jmq.util.FileUtil;
import java.util.ArrayList;


/**
 * This class contains the logic to execute the user commands
 * specified in the BrokerCmdProperties object. It has one
 * public entry point which is the runCommands() method. It
 * is expected to display to the user if the command execution
 * was successful or not.
 * @see  ObjMgr
 *
 */
public class CmdRunner implements BrokerCmdOptions, BrokerConstants, AdminEventListener {
    /*
     * Int constants for metric types
     * Convenience - to avoid doing String.equals().
     */
    private static final int METRICS_TOTALS				= 0;
    private static final int METRICS_RATES				= 1;
    private static final int METRICS_CONNECTIONS			= 2;
    private static final int METRICS_CONSUMER				= 3;
    private static final int METRICS_DISK				= 4;
    private static final int METRICS_REMOVE				= 5;

    /*
     * List types
     */
    private static final int LIST_ALL					= 0;
    private static final int LIST_TOPIC					= 1;
    private static final int LIST_QUEUE					= 2;

    private int zeroNegOneInt[] = {0, -1};
    private long zeroNegOneLong[] = {0, -1};
    private String zeroNegOneString[] = {"0", "-1"};
    private String negOneString[] = {"-1"};

    private AdminResources ar = Globals.getAdminResources();
    private BrokerCmdProperties brokerCmdProps;
    private BrokerAdmin admin;

     private int filter_transactionstate = -1;

    /**
     * Constructor
     */
    public CmdRunner(BrokerCmdProperties props) {
        this.brokerCmdProps = props;
    }

    public void setFilter_transactionstate(int value) {
        this.filter_transactionstate = value;
    }


    /*
     * Run/execute the user commands specified in the BrokerCmdProperties object.
     */
    public String runCommands(StringBuffer result) {
        int exitcode = 0;

        /*
         * If -debug was used, run the debug mode handler
         * and exit.
         */
        if (brokerCmdProps.debugModeSet())  {
            exitcode = runDebug(brokerCmdProps, result);
            return String.valueOf(exitcode);
        }

        /*
         * Determine type of command and invoke the relevant run method
         * to execute the command.
         *
         */
        String cmd = brokerCmdProps.getCommand();
        if (cmd.equals(PROP_VALUE_CMD_LIST))  {
            exitcode = runList(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_PAUSE))  {
            exitcode = runPause(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_RESUME))  {
            exitcode = runResume(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_SHUTDOWN))  {
            exitcode = runShutdown(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_RESTART))  {
            exitcode = runRestart(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_CREATE))  {
            exitcode = runCreate(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_DESTROY))  {
            exitcode = runDestroy(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_PURGE))  {
            exitcode = runPurge(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_UPDATE))  {
            exitcode = runUpdate(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_QUERY))  {
            exitcode = runQuery(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_METRICS))  {
            exitcode = runMetrics(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_RELOAD))  {
            exitcode = runReload(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_COMMIT))  {
            exitcode = runCommit(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_ROLLBACK))  {
            exitcode = runRollback(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_COMPACT))  {
            exitcode = runCompact(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_QUIESCE))  {
            exitcode = runQuiesce(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_TAKEOVER))  {
            exitcode = runTakeover(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_UNQUIESCE))  {
            exitcode = runUnquiesce(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_RESET))  {
            exitcode = runReset(brokerCmdProps, result);

        /*
         * Private subcommands - to support testing only
         */
        } else if (cmd.equals(PROP_VALUE_CMD_EXISTS))  {
            exitcode = runExists(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_GETATTR))  {
            exitcode = runGetAttr(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_UNGRACEFUL_KILL))  {
            exitcode = runUngracefulKill(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_PURGEALL))  {
            exitcode = runPurgeAll(brokerCmdProps, result);
        } else if (cmd.equals(PROP_VALUE_CMD_DESTROYALL))  {
            exitcode = runDestroyAll(brokerCmdProps, result);
        }
        return String.valueOf(exitcode);
    }

    /*
     * BEGIN INTERFACE AdminEventListener
     */
    public void adminEventDispatched(AdminEvent e)  {
        if (e instanceof BrokerCmdStatusEvent)  {
            BrokerCmdStatusEvent be = (BrokerCmdStatusEvent)e;
            int type = be.getType();

            if (type == BrokerCmdStatusEvent.BROKER_BUSY)  {
                int numRetriesAttempted = be.getNumRetriesAttempted(),
                        maxNumRetries = be.getMaxNumRetries();
                long retryTimeount = be.getRetryTimeount();
                Object args[] = new Object [ 3 ];

                args[0] = Integer.toString(numRetriesAttempted);
                args[1] = Integer.toString(maxNumRetries);
                args[2] = Long.toString(retryTimeount);

                /*
                 * This string is of the form:
                 *  Broker not responding, retrying [1 of 5 attempts, timeout=20 seconds]
                 */
                String s = ar.getString(ar.I_JMQCMD_BROKER_BUSY, args);

                Globals.stdOutPrintln(s);
//sb.append(s).append("\n");
            }
        }
    }

    public ArrayList retrieveDestinations(boolean isQueue) {

        BrokerAdmin broker;

        broker = init();

        if (broker == null) {
            return null;
        }

        ArrayList result = new ArrayList();


        try {
            connectToBroker(broker);
            

            broker.sendGetDestinationsMessage(null, -1);
            Vector dests = broker.receiveGetDestinationsReplyMessage();

            Enumeration thisEnum = dests.elements();

            while (thisEnum.hasMoreElements()) {
                DestinationInfo dInfo = (DestinationInfo) thisEnum.nextElement();

                if (MessageType.JMQ_ADMIN_DEST.equals(dInfo.name)) {
                    continue;
                }

                if (DestType.isInternal(dInfo.fulltype)) {
                    continue;
                }

                if (DestType.isTemporary(dInfo.type)) {
                    continue;
                }

                if (DestType.isTopic(dInfo.type)) {
                    //この宛先がTOPICの場合
                    if (!isQueue) {
                        result.add(dInfo.name);

                    }
                }

                if (DestType.isQueue(dInfo.type)) {
                    //この宛先がQueueの場合
                    if (isQueue) {
                        result.add(dInfo.name);
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (broker != null)
            broker.close();

        }

        return result;

    }

    public DestinationInfo getSpecifiedDestinationInfo() {


        BrokerAdmin broker;

        broker = init();

        if (broker == null) {
            return null;
        }


        DestinationInfo retval = null;
        String destName = brokerCmdProps.getTargetName();
        int destTypeMask = getDestTypeMask(brokerCmdProps);


            try  {
                connectToBroker(broker);

                broker.sendGetDestinationsMessage(destName, destTypeMask);
                Vector dest = broker.receiveGetDestinationsReplyMessage();

                if ((dest != null) && (dest.size() == 1)) {
                    Enumeration thisEnum = dest.elements();
                    DestinationInfo dInfo = (DestinationInfo)thisEnum.nextElement();
                    retval = dInfo;

                }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (broker != null)
            broker.close();

        }



        return retval;

    }

    public String getStateOfSpecifiedDestination() {

        BrokerAdmin broker;

        broker = init();

        if (broker == null) {
            return null;
        }

        String retval = null;
        String destName = brokerCmdProps.getTargetName();
        int destTypeMask = getDestTypeMask(brokerCmdProps);


        try {

            connectToBroker(broker);

            broker.sendGetDestinationsMessage(destName, destTypeMask);
            Vector dest = broker.receiveGetDestinationsReplyMessage();


                if ((dest != null) && (dest.size() == 1)) {
                    Enumeration thisEnum = dest.elements();
                    DestinationInfo dInfo = (DestinationInfo)thisEnum.nextElement();

                    retval = DestState.toString(dInfo.destState);

                }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (broker != null)
            broker.close();

        }

        return retval;

    }

    public String retrieveInstanceName() {

        BrokerAdmin broker;

        broker = init();

        if (broker == null) {
            return null;
        }

        String retval = null;


        try {
            connectToBroker(broker);

            broker.sendGetBrokerPropsMessage();
            Properties bkrProps = broker.receiveGetBrokerPropsReplyMessage();
            retval = bkrProps.getProperty(PROP_NAME_BKR_INSTANCE_NAME, "");



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
          if (broker != null)
          broker.close();
        }

        return retval;

    }


    /*
     * END INTERFACE AdminEventListener
     */

    private int runList(BrokerCmdProperties brokerCmdProps,StringBuffer sb) {

        BrokerAdmin broker;

        broker = init();

        // Check for the target argument
        String commandArg = brokerCmdProps.getCommandArg(),
                destTypeStr = brokerCmdProps.getDestType();
        int destTypeMask = getDestTypeMask(brokerCmdProps);
        boolean listAll = (destTypeStr == null);

        if (CMDARG_DESTINATION.equals(commandArg)) {

            if (broker == null)  {
                sb.append(ar.getString(ar.I_JMQCMD_LIST_DST_FAIL)).append("\n");
                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_DST_FAIL));
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            if (listAll)  {
                sb.append(ar.getString(ar.I_JMQCMD_LIST_DST)).append("\n");
                //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_DST));
            } else if (DestType.isQueue(destTypeMask))  {
                sb.append(ar.getString(ar.I_JMQCMD_LIST_QUEUE_DST)).append("\n");
                //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_QUEUE_DST));
            } else if (DestType.isTopic(destTypeMask))  {
                sb.append(ar.getString(ar.I_JMQCMD_LIST_TOPIC_DST)).append("\n");
                //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_TOPIC_DST));
            }
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetDestinationsMessage(null, -1);
                Vector dests = broker.receiveGetDestinationsReplyMessage();

                if (dests != null) {
                    if (listAll) {
                        listDests(brokerCmdProps, dests, LIST_ALL);
                    } else if (DestType.isTopic(destTypeMask))  {
                        listDests(brokerCmdProps, dests, LIST_TOPIC);
                    } else if (DestType.isQueue(destTypeMask))  {
                        listDests(brokerCmdProps, dests, LIST_QUEUE);
                    }
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_DST_SUC));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_DST_SUC)).append("\n");
                } else  {
                    //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_DST_FAIL));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_DST_FAIL)).append("\n");
                    return (1);
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_DST_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_DST_FAIL)).append("\n");
                return (1);
            }

        } else if (CMDARG_SERVICE.equals(commandArg)) {

            if (broker == null)  {
                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_SVC_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_SVC_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_SVC));
            sb.append(ar.getString(ar.I_JMQCMD_LIST_SVC)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetServicesMessage(null);
                Vector svcs = broker.receiveGetServicesReplyMessage();

                if (svcs != null) {
                    BrokerCmdPrinter bcp = new BrokerCmdPrinter(3, 4, "-");
                    String[] row = new String[3];
                    row[0] = ar.getString(ar.I_JMQCMD_SVC_NAME);
                    row[1] = ar.getString(ar.I_JMQCMD_SVC_PORT);
                    row[2] = ar.getString(ar.I_JMQCMD_SVC_STATE);
                    bcp.addTitle(row);

                    Enumeration thisEnum = svcs.elements();
                    while (thisEnum.hasMoreElements()) {
                        ServiceInfo sInfo = (ServiceInfo)thisEnum.nextElement();
                        row[0] = sInfo.name;

                        // The port number is not applicable to this service
                        if (sInfo.port == -1) {
                            row[1] = "-";

                            // Add more information about the port number:
                            // dynamically generated or statically declared
                        } else if (sInfo.dynamicPort) {

                            switch (sInfo.state) {
                                case ServiceState.UNKNOWN:
                                    row[1] = ar.getString(ar.I_DYNAMIC);
                                    break;
                                default:
                                    row[1] = new Integer(sInfo.port).toString() +
                                            " (" + ar.getString(ar.I_DYNAMIC) + ")";
                            }
                        } else {
                            row[1] = new Integer(sInfo.port).toString() +
                                    " (" + ar.getString(ar.I_STATIC) + ")";;
                        }
                        row[2] = ServiceState.getString(sInfo.state);
                        bcp.add(row);
                    }

                    bcp.println();
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_SVC_SUC));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_SVC_SUC)).append("\n");

                } else  {
                    //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_SVC_FAIL));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_SVC_FAIL)).append("\n");
                    return (1);
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_SVC_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_SVC_FAIL)).append("\n");
                return (1);
            }

        } else if (CMDARG_DURABLE.equals(commandArg)) {

            if (broker == null)  {
                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String destName = brokerCmdProps.getDestName();

            //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_DUR, destName));
            sb.append(ar.getString(ar.I_JMQCMD_LIST_DUR, destName)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL)).append("\n");
                return (1);
            }

            try  {
                isDestTypeTopic(broker, destName);

            } catch (BrokerAdminException bae)  {
                if (BrokerAdminException.INVALID_OPERATION == bae.getType())
                    bae.setBrokerErrorStr
                            (ar.getString(ar.I_ERROR_MESG) +
                            ar.getKString(ar.E_DEST_NOT_TOPIC, destName));

                handleBrokerAdminException(bae);

                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL)).append("\n");
                return (1);
            }

            try {
                broker.sendGetDurablesMessage(destName, null);
                Vector durs = broker.receiveGetDurablesReplyMessage();

                if (durs != null) {
                    BrokerCmdPrinter bcp = new BrokerCmdPrinter(4, 3, "-");
                    String[] row = new String[4];
                    row[0] = ar.getString(ar.I_JMQCMD_DUR_NAME);
                    row[1] = ar.getString(ar.I_JMQCMD_CLIENT_ID);
                    row[2] = ar.getString(ar.I_JMQCMD_DUR_NUM_MSG);
                    row[3] = ar.getString(ar.I_JMQCMD_DUR_STATE);
                    bcp.addTitle(row);

                    Enumeration thisEnum = durs.elements();
                    while (thisEnum.hasMoreElements()) {
                        DurableInfo dInfo = (DurableInfo)thisEnum.nextElement();
                        row[0] = (dInfo.name == null) ? "" : dInfo.name;
                        row[1] = (dInfo.clientID == null) ? "" : dInfo.clientID;
                        row[2] = new Integer(dInfo.nMessages).toString();
                        if (dInfo.isActive)
                            row[3] = ar.getString(ar.I_ACTIVE);
                        else
                            row[3] = ar.getString(ar.I_INACTIVE);
                        bcp.add(row);
                    }

                    // Use durname+clientID as the key when listing.
                    bcp.setKeyCriteria(new int[] {0, 1});
                    bcp.println();
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_DUR_SUC));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_DUR_SUC)).append("\n");

                } else  {
                    //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL)).append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_DUR_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_TRANSACTION.equals(commandArg)) {

            if (broker == null)  {
                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_TXN_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_TXN_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_TXN));
            sb.append(ar.getString(ar.I_JMQCMD_LIST_TXN)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetTxnsMessage();
                Vector txns = broker.receiveGetTxnsReplyMessage();

                if ((txns != null) && (txns.size() > 0)) {
                    BrokerCmdPrinter bcp = new BrokerCmdPrinter(5, 3, "-");
                    BrokerCmdPrinter bcp_local = new BrokerCmdPrinter(4, 3, "-");
                    BrokerCmdPrinter bcp_remote = new BrokerCmdPrinter(4, 3, "-");
                    String[]	row = new String[5], value;
                    Long	tmpLong;
                    Integer	tmpInt;
                    String	tmpStr, tmpStr2;

                    row[0] = ar.getString(ar.I_JMQCMD_TXN_ID);
                    row[1] = ar.getString(ar.I_JMQCMD_TXN_STATE);
                    row[2] = ar.getString(ar.I_JMQCMD_TXN_USERNAME);
                    row[3] = ar.getString(ar.I_JMQCMD_TXN_NUM_MSGS_ACKS);
                    row[4] = ar.getString(ar.I_JMQCMD_TXN_TIMESTAMP);
                    bcp.addTitle(row);

                    row[0] = ar.getString(ar.I_JMQCMD_TXN_ID);
                    row[1] = ar.getString(ar.I_JMQCMD_TXN_STATE);
                    row[2] = ar.getString(ar.I_JMQCMD_TXN_USERNAME);
                    row[3] = ar.getString(ar.I_JMQCMD_TXN_TIMESTAMP);
                    bcp_local.addTitle(row);

                    row[0] = ar.getString(ar.I_JMQCMD_TXN_ID);
                    row[1] = ar.getString(ar.I_JMQCMD_TXN_STATE);
                    row[2] = "# Acks";
                    row[3] = "Remote broker";
                    bcp_remote.addTitle(row);

                    Enumeration thisEnum = txns.elements();
                    while (thisEnum.hasMoreElements()) {
                        Hashtable txnInfo = (Hashtable)thisEnum.nextElement();

                        Integer type = (Integer)txnInfo.get("type");

                        if (type.intValue() == TXN_LOCAL)  {
                            tmpLong = (Long)txnInfo.get(PROP_NAME_TXN_ID);
                            row[0] = checkNullAndPrint(tmpLong);

                            tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_STATE);
                            row[1] = getTxnStateString(tmpInt);

                            tmpStr = (String)txnInfo.get(PROP_NAME_TXN_USER);
                            row[2] = checkNullAndPrint(tmpStr);

                            tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_NUM_MSGS);
                            tmpStr = checkNullAndPrint(tmpInt);
                            tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_NUM_ACKS);
                            tmpStr2 = checkNullAndPrint(tmpInt);
                            row[3] = tmpStr + "/" + tmpStr2;

                            tmpLong = (Long)txnInfo.get(PROP_NAME_TXN_TIMESTAMP);
                            row[4] = checkNullAndPrintTimestamp(tmpLong);

                            bcp.add(row);
                        } else if (type.intValue() == TXN_CLUSTER)  {
                            tmpLong = (Long)txnInfo.get(PROP_NAME_TXN_ID);
                            row[0] = checkNullAndPrint(tmpLong);

                            tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_STATE);
                            row[1] = getTxnStateString(tmpInt);

                            tmpStr = (String)txnInfo.get(PROP_NAME_TXN_USER);
                            row[2] = checkNullAndPrint(tmpStr);

                            tmpLong = (Long)txnInfo.get(PROP_NAME_TXN_TIMESTAMP);
                            row[3] = checkNullAndPrintTimestamp(tmpLong);

                            bcp_local.add(row);
                        } else if (type.intValue() == TXN_REMOTE)  {
                            tmpLong = (Long)txnInfo.get(PROP_NAME_TXN_ID);
                            row[0] = checkNullAndPrint(tmpLong);

                            tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_STATE);
                            row[1] = getTxnStateString(tmpInt);

                            tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_NUM_ACKS);
                            tmpStr2 = checkNullAndPrint(tmpInt);
                            row[2] = tmpStr2;

                            tmpStr = (String)txnInfo.get("homebroker");
                            row[3] = checkNullAndPrint(tmpStr);

                            bcp_remote.add(row);
                        }
                    }

                    //Globals.stdOutPrintln(
                    //        "Transactions that are owned by this broker");
                    sb.append( "Transactions that are owned by this broker\n");
                    bcp.println();

                    //Globals.stdOutPrintln(
                    //        "   Transactions that involve remote brokers");
                    sb.append("   Transactions that involve remote brokers\n");
                    bcp_local.setIndent(3);
                    bcp_local.println();

                    //Globals.stdOutPrintln("Transactions that are owned by a remote broker");
                    sb.append("Transactions that are owned by a remote broker\n");
                    bcp_remote.println();

                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_TXN_SUC));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_TXN_SUC)).append("\n");

                } else  {
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_TXN_NONE));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_TXN_NONE)).append("\n");
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_TXN_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_TXN_FAIL)).append("\n");
                return (1);
            }

        } else if (CMDARG_CONNECTION.equals(commandArg)) {
            String svcName = brokerCmdProps.getService();

            if (broker == null)  {
                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_CXN_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_CXN_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            if (svcName == null)  {
                //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_CXN));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_CXN)).append("\n");
                printBrokerInfo(broker);
            } else  {
                //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_CXN_FOR_SVC));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_CXN_FOR_SVC)).append("\n");
                printServiceInfo(svcName);

                //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
                sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
                printBrokerInfo(broker);
            }

            try  {
                connectToBroker(broker);

                broker.sendGetConnectionsMessage(svcName, null);
                Vector cxnList = broker.receiveGetConnectionsReplyMessage();

                if ((cxnList != null) && (cxnList.size() > 0)) {
                    BrokerCmdPrinter bcp = new BrokerCmdPrinter(6, 2, "-");
                    String[]	row = new String[6], value;
                    Long	tmpLong;
                    Integer	tmpInt;
                    String	tmpStr;
                    int i;

                    i = 0;
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_CXN_ID);
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_USER);
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_SERVICE);
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_NUM_PRODUCER);
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_NUM_CONSUMER);
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_HOST);

                    /*
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_CLIENT_ID);
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_PORT);
                    row[i++] = ar.getString(ar.I_JMQCMD_CXN_CLIENT_PLATFORM);
                     */
                    bcp.addTitle(row);

                    Enumeration thisEnum = cxnList.elements();
                    while (thisEnum.hasMoreElements()) {
                        Hashtable cxnInfo = (Hashtable)thisEnum.nextElement();

                        i = 0;

                        tmpLong = (Long)cxnInfo.get(PROP_NAME_CXN_CXN_ID);
                        row[i++] = checkNullAndPrint(tmpLong);

                        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_USER);
                        row[i++] = checkNullAndPrint(tmpStr);

                        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_SERVICE);
                        row[i++] = checkNullAndPrint(tmpStr);

                        tmpInt = (Integer)cxnInfo.get(PROP_NAME_CXN_NUM_PRODUCER);
                        row[i++] = checkNullAndPrint(tmpInt);

                        tmpInt = (Integer)cxnInfo.get(PROP_NAME_CXN_NUM_CONSUMER);
                        row[i++] = checkNullAndPrint(tmpInt);

                        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_HOST);
                        row[i++] = checkNullAndPrint(tmpStr);

                        /*
                        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_CLIENT_ID);
                        row[i++] = checkNullAndPrint(tmpStr);

                        tmpInt = (Integer)cxnInfo.get(PROP_NAME_CXN_PORT);
                        row[i++] = checkNullAndPrint(tmpInt);

                        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_CLIENT_PLATFORM);
                        row[i++] = checkNullAndPrint(tmpStr);
                         */

                        bcp.add(row);
                    }

                    bcp.println();
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_CXN_SUC));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_CXN_SUC)).append("\n");

                } else  {
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_CXN_NONE));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_CXN_NONE)).append("\n");
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_CXN_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_CXN_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_BROKER.equals(commandArg)) {
            if (broker == null)  {
                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_BKR_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_BKR_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_BKR));
            sb.append(ar.getString(ar.I_JMQCMD_LIST_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                /*
                 * Get broker props to find out if broker is in HA cluster and
                 * broker cluster ID
                 */
                broker.sendGetBrokerPropsMessage();
                Properties bkrProps = broker.receiveGetBrokerPropsReplyMessage();

                BrokerCmdPrinter haBcp = new BrokerCmdPrinter(2, 3, null);
                String[]	haInfoRow = new String[2];

                /*
                 * Check if cluster is HA or not
                 */
                String value = bkrProps.getProperty(PROP_NAME_BKR_CLS_HA);
                boolean isHA = Boolean.valueOf(value).booleanValue();

                /*
                 * Display cluster ID only if HA cluster
                 */
                if (isHA)  {
                    haInfoRow[0] = ar.getString(ar.I_CLS_CLUSTER_ID);
                    value = bkrProps.getProperty(PROP_NAME_BKR_CLS_CLUSTER_ID, "");
                    haInfoRow[1] = value;
                    haBcp.add(haInfoRow);
                }

                haInfoRow[0] = ar.getString(ar.I_CLS_IS_HA);
                if (!isHA)  {
                    haInfoRow[1] = Boolean.FALSE.toString();
                } else  {
                    haInfoRow[1] = Boolean.TRUE.toString();
                }
                haBcp.add(haInfoRow);

                haBcp.println();

                /*
                 * Get state of each broker in cluster
                 */
                broker.sendGetClusterMessage(true);
                Vector bkrList = broker.receiveGetClusterReplyMessage();

                if ((bkrList != null) && (bkrList.size() > 0)) {
                    BrokerCmdPrinter bcp;
                    String[]	row;
                    Long tmpLong;
                    Integer tmpInt;
                    long idle;
                    int	i;

                    if (isHA)  {
                        bcp = new BrokerCmdPrinter(6, 3, "-");
                        row = new String[6];

                        bcp.setSortNeeded(false);
                        bcp.setTitleAlign(BrokerCmdPrinter.CENTER);

                        i = 0;
                        row[i++] = "";
                        row[i++] = "";
                        row[i++] = "";
                        row[i++] = "";
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_TAKEOVER_ID1);
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_TIME_SINCE_TIMESTAMP1);

                        bcp.addTitle(row);

                        i = 0;
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_BROKER_ID);
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_ADDRESS);
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_BROKER_STATE);
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_NUM_MSGS);
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_TAKEOVER_ID2);
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_TIME_SINCE_TIMESTAMP2);

                        bcp.addTitle(row);

                        Enumeration thisEnum = bkrList.elements();
                        while (thisEnum.hasMoreElements()) {
                            Hashtable bkrClsInfo
                                    = (Hashtable)thisEnum.nextElement();

                            i = 0;

                            row[i++] =
                                    checkNullAndPrint(
                                    bkrClsInfo.get(BrokerClusterInfo.ID));

                            row[i++] =
                                    checkNullAndPrint(
                                    bkrClsInfo.get(BrokerClusterInfo.ADDRESS));

                            tmpInt = (Integer)bkrClsInfo.get(BrokerClusterInfo.STATE);
                            if (tmpInt != null)  {
                                row[i++] = BrokerState.toString(tmpInt.intValue());
                            } else  {
                                row[i++] = "";
                            }

                            tmpLong = (Long)bkrClsInfo.get(BrokerClusterInfo.NUM_MSGS);
                            row[i++] = checkNullAndPrint(tmpLong);

                            row[i++] = checkNullAndPrint(
                                    bkrClsInfo.get(
                                    BrokerClusterInfo.TAKEOVER_BROKER_ID));

                            tmpLong = (Long)bkrClsInfo.get(
                                    BrokerClusterInfo.STATUS_TIMESTAMP);
                            if (tmpLong != null)  {
                                idle = System.currentTimeMillis() - tmpLong.longValue();
                                row[i++] = getTimeString(idle);
                            } else  {
                                row[i++] = "";
                            }

                            bcp.add(row);
                        }
                    } else  {
                        bcp = new BrokerCmdPrinter(2, 3, "-");
                        row = new String[2];

                        bcp.setSortNeeded(false);
                        bcp.setTitleAlign(BrokerCmdPrinter.CENTER);

                        i = 0;
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_ADDRESS);
                        row[i++] = ar.getString(ar.I_JMQCMD_CLS_BROKER_STATE);

                        bcp.addTitle(row);

                        Enumeration thisEnum = bkrList.elements();
                        while (thisEnum.hasMoreElements()) {
                            Hashtable bkrClsInfo
                                    = (Hashtable)thisEnum.nextElement();

                            i = 0;

                            row[i++] =
                                    checkNullAndPrint(
                                    bkrClsInfo.get(BrokerClusterInfo.ADDRESS));

                            tmpInt = (Integer)bkrClsInfo.get(BrokerClusterInfo.STATE);
                            if (tmpInt != null)  {
                                row[i++] = BrokerState.toString(tmpInt.intValue());
                            } else  {
                                row[i++] = "";
                            }

                            bcp.add(row);
                        }
                    }

                    bcp.println();
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_BKR_SUC));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_BKR_SUC)).append("\n");
                } else  {
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_BKR_NONE));
                    sb.append("ar.getString(ar.I_JMQCMD_LIST_BKR_NONE)").append("\n");
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_BKR_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_BKR_FAIL)).append("\n");
                return (1);
            }
        } else if ("abtxn".equals(commandArg))         {
            if(broker == null)
            {
                //ar;
                //Globals.stdErrPrintln(ar.getString("A1253"));
                sb.append(ar.getString("A1253")).append("\n");
                return 1;
            }
            boolean flag4 = brokerCmdProps.forceModeSet();
            flag4 = true;
            if(!flag4)
            {
                broker = promptForAuthentication(broker);
            }

            //Globals.stdOutPrintln(ar.getString("A1250"));
            sb.append(ar.getString("A1250")).append("\n");
            printBrokerInfo(broker);
            try
            {
                connectToBroker(broker);

                //broker.sendGetTxnsMessage(flag6);
                //Vector vector3 = broker.receiveGetTxnsReplyMessage();
                broker.sendGetTxnsMessage();
                Vector vector3 = broker.receiveGetTxnsReplyMessage();

                if(vector3 != null && vector3.size() > 0)
                {
                    BrokerCmdPrinter brokercmdprinter2 = new BrokerCmdPrinter(5, 3, "-");
                    BrokerCmdPrinter brokercmdprinter4 = new BrokerCmdPrinter(4, 3, "-");
                    BrokerCmdPrinter brokercmdprinter5 = new BrokerCmdPrinter(4, 3, "-");
                    String as3[] = new String[5];
                    //ar;
                    as3[0] = ar.getString("A1263");
                    //ar;
                    as3[1] = ar.getString("A1269");
                    //ar;
                    as3[2] = ar.getString("A1270");
                    //ar;
                    as3[3] = ar.getString("A1272");
                    //ar;
                    as3[4] = ar.getString("A1266");
                    brokercmdprinter2.addTitle(as3);
                    //ar;
                    as3[0] = ar.getString("A1263");
                    //ar;
                    as3[1] = ar.getString("A1269");
                    //ar;
                    as3[2] = ar.getString("A1270");
                    //ar;
                    as3[3] = ar.getString("A1266");
                    brokercmdprinter4.addTitle(as3);
                    //ar;
                    as3[0] = ar.getString("A1263");
                    //ar;
                    as3[1] = ar.getString("A1269");
                    as3[2] = "# Acks";
                    as3[3] = "Remote broker";
                    brokercmdprinter5.addTitle(as3);
                    for(Enumeration enumeration4 = vector3.elements(); enumeration4.hasMoreElements();)
                    {
                        Hashtable hashtable1 = (Hashtable)enumeration4.nextElement();
                        //ローカルトランザクションかどうかを判定
                        Integer integer4 = (Integer)hashtable1.get("type");
                        if(integer4.intValue() == 0)
                        {
                            Long long4 = (Long)hashtable1.get("txnid");
                            as3[0] = checkNullAndPrint(long4);
/*
	case 0:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_CREATED));
	case 1:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_STARTED));
	case 2:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_FAILED));
	case 3:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_INCOMPLETE));
	case 4:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_COMPLETE));
	case 5:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_PREPARED));
	case 6:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_COMMITTED));
	case 7:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_ROLLEDBACK));
	default:
	    return(ar.getString(ar.I_JMQCMD_TXN_STATE_UNKNOWN));

 */


                            Integer integer1 = (Integer)hashtable1.get("state");
                            //値保存
                            int current_txn_state = integer1.intValue();

                            as3[1] = getTxnStateString(integer1);
                            String s6 = (String)hashtable1.get("user");
                            as3[2] = checkNullAndPrint(s6);
                            integer1 = (Integer)hashtable1.get("nmsgs");
                            s6 = checkNullAndPrint(integer1);
                            integer1 = (Integer)hashtable1.get("nacks");
                            String s9 = checkNullAndPrint(integer1);
                            as3[3] = s6 + "/" + s9;
                            long4 = (Long)hashtable1.get("timestamp");
                            as3[4] = checkNullAndPrintTimestamp(long4);

                            //System.err.println("指定したとらんざくしょん：" + this.filter_transactionstate);
                            //System.err.println("とったとらんざくしょん：" + current_txn_state);

                            //フィルターにステート指定がない場合または、指定がある場合は一致したステートしか出さなくする
                            if (this.filter_transactionstate == -1
                                    || this.filter_transactionstate == current_txn_state
                                    || (this.filter_transactionstate == 8 && current_txn_state != 1)
                            ) {
                                brokercmdprinter2.add(as3);
                                //System.err.println("added");
                            }
                        } else
                        if(integer4.intValue() == 1)
                        {
                            Long long5 = (Long)hashtable1.get("txnid");
                            as3[0] = checkNullAndPrint(long5);
                            Integer integer2 = (Integer)hashtable1.get("state");
                            as3[1] = getTxnStateString(integer2);
                            String s7 = (String)hashtable1.get("user");
                            as3[2] = checkNullAndPrint(s7);
                            long5 = (Long)hashtable1.get("timestamp");
                            as3[3] = checkNullAndPrintTimestamp(long5);
                            brokercmdprinter4.add(as3);
                        } else
                        {
                            Long long6 = (Long)hashtable1.get("txnid");
                            as3[0] = checkNullAndPrint(long6);
                            Integer integer3 = (Integer)hashtable1.get("state");
                            as3[1] = getTxnStateString(integer3);
                            integer3 = (Integer)hashtable1.get("nacks");
                            String s10 = checkNullAndPrint(integer3);
                            as3[2] = s10;
                            String s8 = (String)hashtable1.get("homebroker");
                            as3[3] = checkNullAndPrint(s8);
                            brokercmdprinter5.add(as3);
                        }
                    }

                    brokercmdprinter2.println();

                    //Globals.stdOutPrintln(ar.getString("A1251"));
                    sb.append(ar.getString("A1251")).append("\n");
                } else
                {

                    //Globals.stdOutPrintln(ar.getString("A1252"));
                    sb.append(ar.getString("A1252")).append("\n");
                }
            }
            catch(BrokerAdminException brokeradminexception4)
            {
                handleBrokerAdminException(brokeradminexception4);
                //ar;
                //Globals.stdErrPrintln(ar.getString("A1253"));
                sb.append(ar.getString("A1253")).append("\n");
                return 1;
            }
        }



        else if (CMDARG_JMX_CONNECTOR.equals(commandArg)) {

            if (broker == null)  {
                //Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_JMX_FAIL));
                sb.append(ar.getString(ar.I_JMQCMD_LIST_JMX_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_JMX));
            sb.append(ar.getString(ar.I_JMQCMD_LIST_JMX)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetJMXConnectorsMessage(null);
                Vector jmxList = broker.receiveGetJMXConnectorsReplyMessage();

                if (jmxList != null) {
                    BrokerCmdPrinter bcp = new BrokerCmdPrinter(3, 4, null);
                    String[] row = new String[3];
                    row[0] = ar.getString(ar.I_JMQCMD_JMX_NAME);
                    row[1] = ar.getString(ar.I_JMQCMD_JMX_ACTIVE);
                    row[2] = ar.getString(ar.I_JMQCMD_JMX_URL);
                    bcp.addTitle(row);

                    Enumeration thisEnum = jmxList.elements();
                    while (thisEnum.hasMoreElements()) {
                        Hashtable jmxInfo = (Hashtable)thisEnum.nextElement();
                        int i = 0;

                        row[i++] = checkNullAndPrint(jmxInfo.get(PROP_NAME_JMX_NAME));
                        row[i++] = checkNullAndPrint(jmxInfo.get(PROP_NAME_JMX_ACTIVE));
                        row[i++] = checkNullAndPrint(jmxInfo.get(PROP_NAME_JMX_URL));

                        bcp.add(row);
                    }

                    bcp.println();
                    //Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_JMX_SUC));
                    sb.append(ar.getString(ar.I_JMQCMD_LIST_JMX_SUC)).append("\n");

                } else  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_JMX_NONE));
sb.append(ar.getString(ar.I_JMQCMD_LIST_JMX_NONE)).append("\n");

//                    Globals.stdOutPrintln("");
sb.append("").append("\n");
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_JMX_SUC));
sb.append(ar.getString(ar.I_JMQCMD_LIST_JMX_SUC)).append("\n");
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_JMX_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_LIST_JMX_FAIL)).append("\n");
                return (1);
            }

        } else if (CMDARG_MSG.equals(commandArg)) {

            if (broker == null)  {
                /*
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_MSG_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_LIST_MSG_FAIL)).append("\n");
                 */
//                Globals.stdErrPrintln("Listing messages failed.");
sb.append("Listing messages failed.").append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String destName = brokerCmdProps.getTargetName();
            destTypeMask = getDestTypeMask(brokerCmdProps);
            Long maxNumMsgsRetrieved = brokerCmdProps.getMaxNumMsgsRetrieved(),
                    startMsgIndex = brokerCmdProps.getStartMsgIndex();

            /*
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_MSG));
sb.append(ar.getString(ar.I_JMQCMD_LIST_MSG)).append("\n");
             */
//            Globals.stdOutPrintln("Listing messages for the destination");
sb.append("Listing messages for the destination").append("\n");
            printDestinationInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetMessagesMessage(destName, destTypeMask, false, null,
                        startMsgIndex, maxNumMsgsRetrieved);
                Vector msgList = broker.receiveGetMessagesReplyMessage();

                if ((msgList != null) && (msgList.size() != 0)) {
                    BrokerCmdPrinter bcp = new BrokerCmdPrinter(4, 3, "-");
                    String[] row = new String[4];

                    bcp.setSortNeeded(false);

                    int i = 0;
                    row[i++] = "Message #";
                    row[i++] = "Message IDs";
                    row[i++] = "Priority";
                    row[i++] = "Body Type";
                    bcp.addTitle(row);

                    long start = 0;
                    if (startMsgIndex != null)  {
                        start = startMsgIndex.longValue();
                    }
                    Enumeration thisEnum = msgList.elements();
                    while (thisEnum.hasMoreElements()) {
                        HashMap oneMsg = (HashMap)thisEnum.nextElement();
                        i = 0;
                        /*
                        String oneID = (String)thisEnum.nextElement();
                         */

                        row[i++] = Long.toString(start++);
                        row[i++] = checkNullAndPrint(oneMsg.get("MessageID"));
                        row[i++] = checkNullAndPrint(oneMsg.get("Priority"));
                        row[i++] = checkNullAndPrintMsgBodyType(
                                (Integer)oneMsg.get("MessageBodyType"), false);

                        bcp.add(row);
                    }

                    bcp.println();
                    /*
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_MSG_SUC));
sb.append(ar.getString(ar.I_JMQCMD_LIST_MSG_SUC)).append("\n");
                     */
//                    Globals.stdOutPrintln("Successfully listed messages.");
sb.append("Successfully listed messages.").append("\n");

                } else  {
                    /*
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_MSG_NONE));
sb.append(ar.getString(ar.I_JMQCMD_LIST_MSG_NONE)).append("\n");
                     */
//                    Globals.stdErrPrintln("There are no messages.");
sb.append("There are no messages.").append("\n");

//                    Globals.stdOutPrintln("");
sb.append("").append("\n");
                    /*
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_LIST_MSG_SUC));
sb.append(ar.getString(ar.I_JMQCMD_LIST_MSG_SUC)).append("\n");
                     */
//                    Globals.stdOutPrintln("Successfully listed messages.");
sb.append("Successfully listed messages.").append("\n");
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                /*
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_MSG_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_LIST_MSG_FAIL)).append("\n");
                 */
//                Globals.stdErrPrintln("Listing messages failed.");
sb.append("Listing messages failed.").append("\n");
                return (1);
            }
        }

        broker.close();

        return (0);
    }

    private String getTimeString(long millis)  {
        String ret = null;

        if (millis < 1000)  {
            ret = millis + " milliseconds";
        } else if (millis < (60 * 1000))  {
            long seconds = millis / 1000;
            ret = seconds + " seconds";
        } else if (millis < (60 * 60 * 1000))  {
            long mins = millis / (60 * 1000);
            ret = mins + " minutes";
        } else  {
            ret = "> 1 hour";
        }

        return (ret);
    }

    private void listDests(BrokerCmdProperties brokerCmdProps, Vector dests,
            int listType)  {
        BrokerCmdPrinter bcp = setupListDestTitle(listType);
        String[] row = new String[11];
        int i = 0;

        Enumeration thisEnum = dests.elements();

        while (thisEnum.hasMoreElements()) {
            DestinationInfo dInfo = (DestinationInfo)thisEnum.nextElement();
            int j = 0, numMsgs;
            long totalMsgSize;
            float avgMsgSize = 0;
            String destType;

            if (MessageType.JMQ_ADMIN_DEST.equals(dInfo.name))
                continue;

            if (DestType.isInternal(dInfo.fulltype))
                continue;

            // List temporary destinations only if the "-tmp" flag is
            // specified.  This will also display the admin temporary
            // destination(s), since there is currently no way to
            // differentiate it.
            if (DestType.isTemporary(dInfo.type)) {
                if (brokerCmdProps.showTempDestModeSet()) {
                    destType = BrokerAdminUtil.getDestinationType(dInfo.type)
                    + " ("
                            + ar.getString(ar.I_TEMPORARY)
                            + ")";
                } else  {
                    continue;
                }
            } else  {
                destType = BrokerAdminUtil.getDestinationType(dInfo.type);
            }

            if ((listType == LIST_TOPIC) && !DestType.isTopic(dInfo.type))  {
                continue;
            }

            if ((listType == LIST_QUEUE) && !DestType.isQueue(dInfo.type))  {
                continue;
            }

            /*
             * get total msgs, calculate average size
             */
            numMsgs = dInfo.nMessages - dInfo.nTxnMessages;
            totalMsgSize = dInfo.nMessageBytes;
            if (numMsgs > 0)
                avgMsgSize = (float)totalMsgSize/(float)numMsgs;

            row[j++] = dInfo.name;
            row[j++] = destType;
            row[j++] = DestState.toString(dInfo.destState);
            row[j++] = new Integer(dInfo.nProducers).toString();

            if (DestType.isTopic(dInfo.type))  {
                /*
                 * For topics, show number of producer wildcards, if any.
                 */
                Hashtable h = dInfo.producerWildcards;
                row[j++] = Integer.toString(getWildcardCount(h));
            } else  {
                /*
                 * Wildcards not applicable for queues.
                 */
                row[j++] = "-";
            }

            /*
             * Use cases:
             *  list dst -t t
             *	  -> show total consumers
             *	  -> show total wildcard consumers
             *  list dst -t q
             *	  -> show active/backup consumers
             *  list dst
             *	  -> show total consumers
             *	  -> show total wildcard consumers for topics
             *	  -> show "-"  for queues
             */
            if (DestType.isTopic(dInfo.type))  {
                row[j++] = new Integer(dInfo.nConsumers).toString();

                /*
                 * For topics, show number of producer wildcards, if any.
                 */
                Hashtable h = dInfo.consumerWildcards;
                row[j++] = Integer.toString(getWildcardCount(h));
            } else  {
                if (listType == LIST_QUEUE)  {
                    row[j++] = new Integer(dInfo.naConsumers).toString();
                    row[j++] = new Integer(dInfo.nfConsumers).toString();
                } else  {
                    row[j++] =
                            new Integer(dInfo.naConsumers + dInfo.nfConsumers).toString();

                    /*
                     * Wildcards not applicable for queues.
                     */
                    row[j++] = "-";
                }
            }

            row[j++] = new Integer(numMsgs).toString();
            row[j++] = new Integer(dInfo.nRemoteMessages).toString();
            row[j++] = new Integer(dInfo.nUnackMessages).toString();
            row[j++] = new Float(avgMsgSize).toString();

            bcp.add(row);
        }

        // Fix for bug 4495379: jmqcmd: when create queue and topic
        // with same name only one is listed
        // Use name+type as the key when listing.
        bcp.setKeyCriteria(new int[] {0, 1});
        bcp.println();
    }

    /*
     * The Hashble contains wildcard consumers (or producers).
     * Each entry is of the form:
     *		<wildcard, count>
     *
     * eg.
     *
     *		<*.sun, 2>
     *		<news.*, 4>
     *
     * This method returns the total # of wildcard consumers or
     * producers. In the example above, the total would be
     *		2 + 4 = 6.
     */
    private int getWildcardCount(Hashtable h)  {
        int count = 0;

        if (h == null)  {
            return (0);
        }

        Enumeration keys = h.keys();

        while (keys.hasMoreElements())  {
            String wildcard = (String)keys.nextElement();
            Integer val = (Integer)h.get(wildcard);
            count += val.intValue();
        }

        return (count);
    }

    private String getTxnStateString(Integer txnState)  {

        if (txnState == null)  {
            return ("");
        }

        int	tmpInt = txnState.intValue();

        /*
         * Instead of hardcoding the values 0 - 7 here we should get it
         * from a interface or class shared by the broker and admin.
         * The current values are currently in a broker private class:
         *	com.sun.messaging.jmq.jmsserver.data.TransactionState
         */
        switch (tmpInt) {
            case 0:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_CREATED));
            case 1:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_STARTED));
            case 2:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_FAILED));
            case 3:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_INCOMPLETE));
            case 4:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_COMPLETE));
            case 5:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_PREPARED));
            case 6:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_COMMITTED));
            case 7:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_ROLLEDBACK));
            default:
                return(ar.getString(ar.I_JMQCMD_TXN_STATE_UNKNOWN));
        }
    }

    private int runPause(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin 	broker;
        String		input = null;
        String		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);


        broker = init();

        boolean force = brokerCmdProps.forceModeSet();

        // Check for the target argument
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_BROKER.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_BKR));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_PAUSE_BKR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendPauseMessage(null);
                    broker.receivePauseReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_BKR_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_BKR_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_BKR_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_BKR_NOOP)).append("\n");
                return (1);
            }

        } else if (CMDARG_SERVICE.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_SVC_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            String svcName = brokerCmdProps.getTargetName();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_SVC));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_SVC)).append("\n");
            printServiceInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_SVC_FAIL)).append("\n");
                return (1);
            }

            try {
                isAdminService(broker, svcName);

            } catch (BrokerAdminException bae)  {
                if (BrokerAdminException.INVALID_OPERATION == bae.getType())
                    bae.setBrokerErrorStr
                            (ar.getString(ar.I_ERROR_MESG) +
                            ar.getKString(ar.E_CANNOT_PAUSE_SVC, svcName));

                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_SVC_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_PAUSE_SVC_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendPauseMessage(svcName);
                    broker.receivePauseReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_SVC_SUC));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_SVC_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_SVC_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_SVC_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_SVC_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_SVC_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_SVC_NOOP)).append("\n");
                return (1);
            }

        } else if (CMDARG_DESTINATION.equals(commandArg)) {
            String destName, pauseTypeStr;
            BrokerCmdPrinter bcp = new BrokerCmdPrinter(2,4);
            String[] row = new String[2];
            boolean pauseAll = true;
            int destTypeMask;

            destName = brokerCmdProps.getTargetName();
            destTypeMask = getDestTypeMask(brokerCmdProps);
            pauseTypeStr = brokerCmdProps.getPauseType();

            if (destName != null)  {
                pauseAll = false;
            }

            if (broker == null)  {
                if (pauseAll)  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_FAIL)).append("\n");
                } else  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DST_FAIL)).append("\n");
                }
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            if (pauseAll)  {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DSTS));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DSTS)).append("\n");
            } else  {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DST));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DST)).append("\n");
                printDestinationInfo();
            }

            // Only print out the pause type if it was specified
            if (pauseTypeStr != null) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_USING_ATTR));
sb.append(ar.getString(ar.I_JMQCMD_USING_ATTR)).append("\n");

                row[0] = ar.getString(ar.I_JMQCMD_PAUSE_DST_TYPE);
                row[1] = pauseTypeStr;
                bcp.add(row);
                bcp.println();
            }


//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                if (pauseAll)  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_FAIL)).append("\n");
                } else  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DST_FAIL)).append("\n");
                }
                return (1);
            }

            if (!force) {
                if (pauseAll)  {
                    input = getUserInput(ar.getString(ar.Q_PAUSE_DSTS_OK), noShort);
                } else  {
                    input = getUserInput(ar.getString(ar.Q_PAUSE_DST_OK), noShort);
                }
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    int pauseType = getPauseTypeVal(pauseTypeStr);
                    broker.sendPauseMessage(destName, destTypeMask, pauseType);
                    broker.receivePauseReplyMessage();
                    if (pauseAll)  {
//                        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_SUC));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_SUC)).append("\n");
                    } else  {
//                        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DST_SUC)).append("\n");
                    }

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

                    if (pauseAll)  {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_FAIL)).append("\n");
                    } else  {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DST_FAIL)).append("\n");
                    }
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
                if (pauseAll)  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_NOOP)).append("\n");
                } else  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DST_NOOP)).append("\n");
                }
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
                if (pauseAll)  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DSTS_NOOP)).append("\n");
                } else  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PAUSE_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PAUSE_DST_NOOP)).append("\n");
                }
                return (1);
            }

        }

        broker.close();

        return (0);
    }

    private int runReset(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin 	broker;
        String		input = null;
        String		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        broker = init();

        boolean force = brokerCmdProps.forceModeSet();

        // Check for the target argument
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_BROKER.equals(commandArg)) {
            String resetType;
            BrokerCmdPrinter bcp = new BrokerCmdPrinter(2,4);
            String[] row = new String[2];

            resetType = brokerCmdProps.getResetType();

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESET_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESET_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESET_BKR));
sb.append(ar.getString(ar.I_JMQCMD_RESET_BKR)).append("\n");

            // Only print out the pause type if it was specified
            if (resetType != null) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_USING_ATTR));
sb.append(ar.getString(ar.I_JMQCMD_USING_ATTR)).append("\n");

                row[0] = ar.getString(ar.I_JMQCMD_RESET_BKR_TYPE);
                row[1] = resetType;
                bcp.add(row);
                bcp.println();
            }

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESET_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESET_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_RESET_BKR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    String resetTypeVal = getResetTypeVal(resetType);
                    broker.sendResetBrokerMessage(resetTypeVal);
                    broker.receiveResetBrokerReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESET_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_RESET_BKR_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESET_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESET_BKR_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESET_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESET_BKR_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESET_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESET_BKR_NOOP)).append("\n");
                return (1);
            }

        }

        broker.close();

        return (0);
    }


    private int runResume(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin 	broker;
        String		input = null;
        String 		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);


        broker = init();

        boolean force = brokerCmdProps.forceModeSet();

        // Check for the target argument
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_BROKER.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_BKR));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_RESUME_BKR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendResumeMessage(null);
                    broker.receiveResumeReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_BKR_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_BKR_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_BKR_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_BKR_NOOP)).append("\n");
                return (1);
            }

        } else if (CMDARG_SERVICE.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_SVC_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            String svcName = brokerCmdProps.getTargetName();
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_SVC));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_SVC)).append("\n");
            printServiceInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_SVC_FAIL)).append("\n");
                return (1);
            }

            try {
                isAdminService(broker, svcName);

            } catch (BrokerAdminException bae)  {
                if (BrokerAdminException.INVALID_OPERATION == bae.getType())
                    bae.setBrokerErrorStr
                            (ar.getString(ar.I_ERROR_MESG) +
                            ar.getKString(ar.E_CANNOT_RESUME_SVC, svcName));

                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_SVC_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_RESUME_SVC_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendResumeMessage(svcName);
                    broker.receiveResumeReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_SVC_SUC));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_SVC_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_SVC_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_SVC_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_SVC_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_SVC_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_SVC_NOOP)).append("\n");
                return (1);
            }
        } else if (CMDARG_DESTINATION.equals(commandArg)) {
            String destName;
            int destTypeMask;
            boolean resumeAll = true;

            destName = brokerCmdProps.getTargetName();
            destTypeMask = getDestTypeMask(brokerCmdProps);

            if (destName != null)  {
                resumeAll = false;
            }

            if (broker == null)  {
                if (resumeAll)  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DSTS_FAIL)).append("\n");
                } else  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DST_FAIL)).append("\n");
                }
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            if (resumeAll)  {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_DSTS));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DSTS)).append("\n");
            } else  {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_DST));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DST)).append("\n");

                printDestinationInfo();

//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            }

            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                if (resumeAll)  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DSTS_FAIL)).append("\n");
                } else  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DST_FAIL)).append("\n");
                }
                return (1);
            }

            if (!force) {
                if (resumeAll)  {
                    input = getUserInput(ar.getString(ar.Q_RESUME_DSTS_OK), noShort);
                } else  {
                    input = getUserInput(ar.getString(ar.Q_RESUME_DST_OK), noShort);
                }
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendResumeMessage(destName, destTypeMask);
                    broker.receiveResumeReplyMessage();
                    if (resumeAll)  {
//                        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_DSTS_SUC));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DSTS_SUC)).append("\n");
                    } else  {
//                        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_DST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DST_SUC)).append("\n");
                    }

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

                    if (resumeAll)  {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DSTS_FAIL)).append("\n");
                    } else  {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESUME_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DST_FAIL)).append("\n");
                    }
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
                if (resumeAll)  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_DSTS_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DSTS_NOOP)).append("\n");
                } else  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DST_NOOP)).append("\n");
                }
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
                if (resumeAll)  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_DSTS_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DSTS_NOOP)).append("\n");
                } else  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESUME_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESUME_DST_NOOP)).append("\n");
                }
                return (1);
            }

        }

        broker.close();

        return (0);
    }

    private int runShutdown(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin 	broker;
        String 		input = null;
        String 		yes, yesShort, no, noShort, sessionID;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);


        broker = init();

        if (broker == null)  {
//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_FAIL)).append("\n");
            return (1);
        }

        boolean force = brokerCmdProps.forceModeSet();
        if (!force)
            broker = promptForAuthentication(broker);

        boolean noFailover = brokerCmdProps.noFailoverSet();
        int time = brokerCmdProps.getTime();

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR)).append("\n");
        printBrokerInfo(broker);

        try {
            connectToBroker(broker);
            sessionID =  getPortMapperSessionID(brokerCmdProps, broker);

        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);

//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_FAIL)).append("\n");
            return (1);
        }

        if (!force) {
            input = getUserInput(ar.getString(ar.Q_SHUTDOWN_BKR_OK), noShort);
//            Globals.stdOutPrintln("");
sb.append("").append("\n");
        }

        if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
            try  {
                broker.sendShutdownMessage(false, false, noFailover, time);
                broker.receiveShutdownReplyMessage();

                if (waitForShutdown(broker, sessionID, brokerCmdProps))  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_SUC)).append("\n");
                } else  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SENT_SHUTDOWN_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_SENT_SHUTDOWN_BKR_SUC)).append("\n");
                }

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_FAIL)).append("\n");
                return (1);
            }

        } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_NOOP)).append("\n");
            return (0);

        } else {
//            Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//            Globals.stdOutPrintln("");
sb.append("").append("\n");
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_SHUTDOWN_BKR_NOOP)).append("\n");
            return (1);
        }
        /*
         * We don't need to call broker.close() since the broker
         * connection should be gone.
         * broker.close();
         */
        return (0);
    }

    /*
     * Wait for broker to shutdown.
     * Returns true if a wait was performed.
     * Returns false if no waiting was done - return immediately.
     */
    private boolean waitForShutdown(BrokerAdmin broker, String waitSessionID,
            BrokerCmdProperties brokerCmdProps)  {
        GenericPortMapperClient pmc = null;
        String hostName = broker.getBrokerHost(),
                portString = broker.getBrokerPort(),
                sessionID = null;
        int port, shutdownDelaySecs;
        long sleepTime;

        /*
         * Don't wait if don't have broker props
         */
        if (brokerCmdProps == null)  {
            return (false);
        }

        sleepTime = brokerCmdProps.getShutdownWaitInterval();
        shutdownDelaySecs = brokerCmdProps.getTime();

        /*
         * Don't wait if delayed shutdown (ie 'imqcmd shutdown bkr' used with -time <non-zero time>)
         */
        if (shutdownDelaySecs > 0)  {
            return (false);
        }

        /*
         * Don't wait if cannot identify the specific broker to wait for.
         */
        if (waitSessionID == null)  {
            return (false);
        }

        try  {
            port = Integer.parseInt(portString);
        } catch(Exception e)  {
            port = 7676;
        }

        boolean brokerDown = false;
        int count = 0;

        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_WAITING_FOR_SHUTDOWN,
                hostName + ":" + portString));
        while (!brokerDown)  {
            try  {
                Thread.sleep(sleepTime);
            } catch (Exception e)  {
            }

            try  {
                pmc = new GenericPortMapperClient(hostName, port);
            } catch(Exception e)  {
                brokerDown = true;
            }

            if (pmc != null)  {
                sessionID = pmc.getProperty("sessionid", null, "PORTMAPPER", "portmapper");

                if (sessionID == null)  {
                    brokerDown = true;
                } else if (!sessionID.equals(waitSessionID))  {
                    brokerDown = true;
                }
            }
        }

        return (true);
    }

    private String getPortMapperSessionID(BrokerCmdProperties brokerCmdProps, BrokerAdmin broker)  {
        GenericPortMapperClient pmc;
        String hostName = broker.getBrokerHost(),
                portString = broker.getBrokerPort(),
                sessionID = null;
        int port;


        try  {
            port = Integer.parseInt(portString);
        } catch(Exception e)  {
            port = 7676;
        }

        try  {
            pmc = new GenericPortMapperClient(hostName, port);
            sessionID = pmc.getProperty("sessionid", null, "PORTMAPPER", "portmapper");
        } catch(Exception e)  {
        }

        return (sessionID);
    }

    private int runRestart(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin 	broker;
        String		input = null;
        String 		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);


        broker = init();

        if (broker == null)  {
//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL)).append("\n");
            return (1);
        }

        boolean force = brokerCmdProps.forceModeSet();
        if (!force)
            broker = promptForAuthentication(broker);

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR)).append("\n");
        printBrokerInfo(broker);

        try {
            connectToBroker(broker);

        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);

//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL)).append("\n");
            return (1);
        }

        if (!force) {
            input = getUserInput(ar.getString(ar.Q_RESTART_BKR_OK), noShort);
//            Globals.stdOutPrintln("");
sb.append("").append("\n");
        }

        if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
            try  {
                broker.sendShutdownMessage(true);
                broker.receiveShutdownReplyMessage();
                /*
                 * Shutdown was successful.  Now wait to see if jmqcmd can get
                 * reconnected back to the broker.
                 */
                if (reconnectToBroker(broker))
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR_SUC)).append("\n");
                else {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL)).append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL)).append("\n");
                return (1);
            }

        } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR_NOOP)).append("\n");
            return (0);

        } else {
//            Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//            Globals.stdOutPrintln("");
sb.append("").append("\n");
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR_NOOP)).append("\n");
            return (1);
        }

        broker.close();

        return (0);
    }

    private int runCreate(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin	broker;
        DestinationInfo	destInfo;
        String		destName;
        int		destTypeMask;
        Properties	destAttrs;

        broker = init();

        if (broker == null)  {
//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_CREATE_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_CREATE_DST_FAIL)).append("\n");
            return (1);
        }

        boolean force = brokerCmdProps.forceModeSet();
        if (!force)
            broker = promptForAuthentication(broker);

        destName = brokerCmdProps.getTargetName();
        destTypeMask = getDestTypeMask(brokerCmdProps);
        destAttrs = brokerCmdProps.getTargetAttrs();

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_CREATE_DST));
sb.append(ar.getString(ar.I_JMQCMD_CREATE_DST)).append("\n");

        BrokerCmdPrinter bcp = new BrokerCmdPrinter(2,4);
        String[] row = new String[2];

        bcp.setSortNeeded(false);

        row[0] = ar.getString(ar.I_JMQCMD_DST_NAME);
        row[1] = destName;
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_DST_TYPE);
        row[1] = BrokerAdminUtil.getDestinationType(destTypeMask);
        bcp.add(row);

        /*
        // Only print out the flavor type if the destination is a queue.
        if (DestType.isQueue(destTypeMask)) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_FLAVOR);
            row[1] = BrokerAdminUtil.getDestinationFlavor(destTypeMask);
            bcp.add(row);
        }
         */

        // Check for optional destination attributes.
        // Note that the same checking is done twice; once for printing
        // and once for creating the DestinationInfo object.  It can
        // be combined, but this is cleaner.
        String prop = null;
        if ((prop = destAttrs.getProperty
                (PROP_NAME_OPTION_MAX_MESG)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_MSG_ALLOW);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_OPTION_MAX_MESG_BYTE)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_MSG_BYTES_ALLOW);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_OPTION_MAX_PER_MESG_SIZE)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_BYTES_PER_MSG_ALLOW);
            row[1] = prop;
            bcp.add(row);
        }

        if ((prop = destAttrs.getProperty
                (PROP_NAME_MAX_PRODUCERS)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_PRODUCERS);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_MAX_ACTIVE_CONSUMER_COUNT)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_ACTIVE_CONSUMER_COUNT);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_MAX_FAILOVER_CONSUMER_COUNT)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_FAILOVER_CONSUMER_COUNT);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_LIMIT_BEHAVIOUR)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_LIMIT_BEHAVIOUR);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_CONSUMER_FLOW_LIMIT)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_CONS_FLOW_LIMIT);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_IS_LOCAL_DEST)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_IS_LOCAL_DEST);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_LOCAL_DELIVERY_PREF)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_LOCAL_DELIVERY_PREF);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_USE_DMQ)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_USE_DMQ);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_VALIDATE_XML_SCHEMA_ENABLED)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_VALIDATE_XML_SCHEMA_ENABLED);
            row[1] = prop;
            bcp.add(row);
        }
        if ((prop = destAttrs.getProperty
                (PROP_NAME_XML_SCHEMA_URI_LIST)) != null) {
            row[0] = ar.getString(ar.I_JMQCMD_DST_XML_SCHEMA_URI_LIST);
            row[1] = prop;
            bcp.add(row);
        }
        bcp.println();

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
        printBrokerInfo(broker);

        try {
            SizeString	ss;
            long	byteValue;

            destInfo = new DestinationInfo();

            destInfo.setType(destTypeMask);
            destInfo.setName(destName);

            // Check for optional destination attributes
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_OPTION_MAX_MESG_BYTE)) != null) {
                try  {
                    ss = new SizeString(prop);
                    byteValue = ss.getBytes();
                    destInfo.setMaxMessageBytes(byteValue);
                } catch (NumberFormatException nfe)  {
                    /*
                     * Do nothing. We shouldn't ever get here since
                     * we do input validation prior to all this.
                     */
                }

            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_OPTION_MAX_MESG)) != null) {
                destInfo.setMaxMessages(Integer.parseInt(prop));
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_OPTION_MAX_PER_MESG_SIZE)) != null) {
                try  {
                    ss = new SizeString(prop);
                    byteValue = ss.getBytes();
                    destInfo.setMaxMessageSize(byteValue);
                } catch (NumberFormatException nfe)  {
                    /*
                     * Do nothing. We shouldn't ever get here since
                     * we do input validation prior to all this.
                     */
                }
            }

            if ((prop = destAttrs.getProperty
                    (PROP_NAME_MAX_FAILOVER_CONSUMER_COUNT)) != null) {
                destInfo.setMaxFailoverConsumers(Integer.parseInt(prop));
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_MAX_ACTIVE_CONSUMER_COUNT)) != null) {
                destInfo.setMaxActiveConsumers(Integer.parseInt(prop));
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_IS_LOCAL_DEST)) != null) {
                destInfo.setScope(Boolean.valueOf(prop).booleanValue());
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_LIMIT_BEHAVIOUR)) != null) {
                destInfo.setLimitBehavior(getLimitBehavValue(prop));
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_LOCAL_DELIVERY_PREF)) != null) {
                destInfo.setClusterDeliveryPolicy(getClusterDeliveryPolicy(prop));
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_CONSUMER_FLOW_LIMIT)) != null) {
                destInfo.setPrefetch(Integer.parseInt(prop));
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_MAX_PRODUCERS)) != null) {
                destInfo.setMaxProducers(Integer.parseInt(prop));
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_USE_DMQ)) != null) {
                destInfo.setUseDMQ(Boolean.valueOf(prop).booleanValue());
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_VALIDATE_XML_SCHEMA_ENABLED)) != null) {
                destInfo.setValidateXMLSchemaEnabled(Boolean.valueOf(prop).booleanValue());
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_XML_SCHEMA_URI_LIST)) != null) {
                destInfo.setXMLSchemaUriList(prop);
            }
            if ((prop = destAttrs.getProperty
                    (PROP_NAME_RELOAD_XML_SCHEMA_ON_FAILURE)) != null) {
                destInfo.setReloadXMLSchemaOnFailure(Boolean.valueOf(prop).booleanValue());
            }

            connectToBroker(broker);

            broker.sendCreateDestinationMessage(destInfo);
            broker.receiveCreateDestinationReplyMessage();
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_CREATE_DST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_CREATE_DST_SUC)).append("\n");

        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);

//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_CREATE_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_CREATE_DST_FAIL)).append("\n");
            return (1);
        }

        broker.close();

        return (0);
    }

    private int runDestroy(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin     broker;
        String 		input = null;
        String 		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        String commandArg = brokerCmdProps.getCommandArg();
        boolean force = brokerCmdProps.forceModeSet();

        broker = init();

        if (CMDARG_DESTINATION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DST_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            String destName = brokerCmdProps.getTargetName();
            int destTypeMask = getDestTypeMask(brokerCmdProps);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DST));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DST)).append("\n");
            printDestinationInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DST_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_DESTROY_DST_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendDestroyDestinationMessage(destName, destTypeMask);
                    broker.receiveDestroyDestinationReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DST_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DST_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DST_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DST_NOOP)).append("\n");
                return (1);
            }
        } else if (CMDARG_DURABLE.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DUR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DUR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            String subName = brokerCmdProps.getTargetName();
            String clientID = brokerCmdProps.getClientID();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DUR));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DUR)).append("\n");
            printDurableSubscriptionInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DUR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DUR_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_DESTROY_DUR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendDestroyDurableMessage(subName, clientID);
                    broker.receiveDestroyDurableReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DUR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DUR_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DUR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DUR_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DUR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DUR_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_DUR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_DUR_NOOP)).append("\n");
                return (1);
            }
        } else if (CMDARG_CONNECTION.equals(commandArg)) {
            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_CXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_CXN_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            String cxnIdStr = brokerCmdProps.getTargetName();
            Long cxnId = null;

            try  {
                cxnId = Long.valueOf(cxnIdStr);
            } catch (NumberFormatException nfe)  {
//                Globals.stdErrPrintln(ar.getString(ar.E_INVALID_CXN_ID, cxnIdStr));
sb.append(ar.getString(ar.E_INVALID_CXN_ID, cxnIdStr)).append("\n");
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_CXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_CXN_FAIL)).append("\n");
                return (1);
            }


//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_CXN));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_CXN)).append("\n");
            printConnectionInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_CXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_CXN_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_DESTROY_CXN_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendDestroyConnectionMessage(cxnId);
                    broker.receiveDestroyConnectionReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_CXN_SUC));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_CXN_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_CXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_CXN_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_CXN_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_CXN_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_CXN_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_CXN_NOOP)).append("\n");
                return (1);
            }
        } else if (CMDARG_MSG.equals(commandArg)) {
            if (broker == null)  {
                /*
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_MSG_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_MSG_FAIL)).append("\n");
                 */
//                Globals.stdErrPrintln("Destroying message failed.");
sb.append("Destroying message failed.").append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            String destName = brokerCmdProps.getTargetName();
            int destTypeMask = getDestTypeMask(brokerCmdProps);
            String msgID = brokerCmdProps.getMsgID();

            /*
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_MSG));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_MSG)).append("\n");
             */
//            Globals.stdOutPrintln("Destroying message:");
sb.append("Destroying message:").append("\n");
            printMessageInfo();

//            Globals.stdOutPrintln("In the destination");
sb.append("In the destination").append("\n");
            printDestinationInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                /*
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_MSG_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_MSG_FAIL)).append("\n");
                 */
//                Globals.stdErrPrintln("Destroying message failed.");
sb.append("Destroying message failed.").append("\n");
                return (1);
            }

            if (!force) {
                /*
                input = getUserInput(ar.getString(ar.Q_DESTROY_MSG_OK), noShort);
                 */
                input = getUserInput("Are you sure you want to destroy this message? (y/n)[n] ", noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendDestroyMessagesMessage(destName, destTypeMask, msgID);
                    broker.receiveDestroyMessagesReplyMessage();
                    /*
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_MSG_SUC));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_MSG_SUC)).append("\n");
                     */
//                    Globals.stdOutPrintln("Successfully destroyed message.");
sb.append("Successfully destroyed message.").append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

                    /*
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_DESTROY_MSG_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_MSG_FAIL)).append("\n");
                     */
//                    Globals.stdErrPrintln("Destroying message failed.");
sb.append("Destroying message failed.").append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
                /*
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_MSG_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_MSG_NOOP)).append("\n");
                 */
//                Globals.stdOutPrintln("The message was not destroyed.\n");
sb.append("The message was not destroyed.\n").append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
                /*
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_DESTROY_MSG_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_DESTROY_MSG_NOOP)).append("\n");
                 */
//                Globals.stdOutPrintln("The message was not destroyed.\n");
sb.append("The message was not destroyed.\n").append("\n");
                return (1);
            }
        }

        broker.close();

        return (0);
    }

    private int runPurge(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin     broker;
        String          destName;
        int             destTypeMask;
        String		input = null;
        String 		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        String commandArg = brokerCmdProps.getCommandArg();
        boolean force = brokerCmdProps.forceModeSet();

        broker = init();

        if (CMDARG_DESTINATION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PURGE_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DST_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            destName = brokerCmdProps.getTargetName();
            destTypeMask = getDestTypeMask(brokerCmdProps);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PURGE_DST));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DST)).append("\n");
            printDestinationInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PURGE_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DST_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_PURGE_DST_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendPurgeDestinationMessage(destName, destTypeMask);
                    broker.receivePurgeDestinationReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PURGE_DST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DST_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PURGE_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DST_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PURGE_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DST_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PURGE_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DST_NOOP)).append("\n");
                return (1);
            }

        } else if (CMDARG_DURABLE.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PURGE_DUR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DUR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            String subName = brokerCmdProps.getTargetName();
            String clientID = brokerCmdProps.getClientID();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PURGE_DUR));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DUR)).append("\n");
            printDurableSubscriptionInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PURGE_DUR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DUR_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_PURGE_DUR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendPurgeDurableMessage(subName, clientID);
                    broker.receivePurgeDurableReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PURGE_DUR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DUR_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_PURGE_DUR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DUR_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PURGE_DUR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DUR_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_PURGE_DUR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_PURGE_DUR_NOOP)).append("\n");
                return (1);
            }
        }

        broker.close();

        return (0);
    }

    private int runPurgeAll(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin     broker;
        String		input = null;
        String 		yes, yesShort, no, noShort;
        int		ret_code = 0;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        String commandArg = brokerCmdProps.getCommandArg();
        boolean force = brokerCmdProps.forceModeSet();

        broker = init();

        if (CMDARG_DESTINATION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln("Purging all the destinations failed");
sb.append("Purging all the destinations failed").append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln("Purging all the destinations");
sb.append("Purging all the destinations").append("\n");

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln("Purging all the destinations failed");
sb.append("Purging all the destinations failed").append("\n");
                return (1);
            }

            if (!force) {
                input =
                        getUserInput("Are you sure you want to purge all the destinations? (y/n)[n] ", noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    boolean dstsPurged = false;

                /*
                 * List all destinations
                 */
                    broker.sendGetDestinationsMessage(null, -1);
                    Vector dests = broker.receiveGetDestinationsReplyMessage();

                    if (dests != null) {
                        Enumeration thisEnum = dests.elements();

                        while (thisEnum.hasMoreElements()) {
                            DestinationInfo dInfo = (DestinationInfo)thisEnum.nextElement();
                            String          destName;
                            int             destTypeMask;

                            destName = dInfo.name;
                            destTypeMask = dInfo.type;

                            if (MessageType.JMQ_ADMIN_DEST.equals(destName)
                            || DestType.isInternal(dInfo.fulltype)
                            || DestType.isTemporary(dInfo.type)) {

//                                Globals.stdOutPrintln("Skipping destination: " + destName);
sb.append("Skipping destination: " + destName).append("\n");
                                continue;
                            }

                            try  {
                                broker.sendPurgeDestinationMessage(destName, destTypeMask);
                                broker.receivePurgeDestinationReplyMessage();

                                if (DestType.isQueue(destTypeMask)) {
//                                    Globals.stdOutPrintln("Successfully purged queue " + destName);
sb.append("Successfully purged queue " + destName).append("\n");
                                } else  {
//                                    Globals.stdOutPrintln("Successfully purged topic " + destName);
sb.append("Successfully purged topic " + destName).append("\n");
                                }
                                dstsPurged = true;
                            } catch (BrokerAdminException purgeEx)  {
                                handleBrokerAdminException(purgeEx);

                                if (DestType.isQueue(destTypeMask)) {
//                                    Globals.stdOutPrintln("Purging failed for queue " + destName);
sb.append("Purging failed for queue " + destName).append("\n");
                                } else  {
//                                    Globals.stdOutPrintln("Purging failed for topic " + destName);
sb.append("Purging failed for topic " + destName).append("\n");
                                }
                                ret_code = 1;
                            }

                        }

                    }

                    if (!dstsPurged)  {
//                        Globals.stdOutPrintln("No destinations purged.");
sb.append("No destinations purged.").append("\n");
                    }

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln("Purging all the destinations failed");
sb.append("Purging all the destinations failed").append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln("The destinations were not purged.");
sb.append("The destinations were not purged.").append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln("The destinations were not purged.");
sb.append("The destinations were not purged.").append("\n");
                return (1);
            }

        }

        broker.close();

        return (ret_code);
    }

    private int runDestroyAll(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin     broker;
        String		input = null;
        String 		yes, yesShort, no, noShort;
        int		ret_code = 0;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        String commandArg = brokerCmdProps.getCommandArg();
        boolean force = brokerCmdProps.forceModeSet();

        broker = init();

        if (CMDARG_DESTINATION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln("Destroying all the destinations failed");
sb.append("Destroying all the destinations failed").append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln("Destroying all the destinations");
sb.append("Destroying all the destinations").append("\n");

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln("Destroying all the destinations failed");
sb.append("Destroying all the destinations failed").append("\n");
                return (1);
            }

            if (!force) {
                input =
                        getUserInput("Are you sure you want to destroy all the destinations? (y/n)[n] ", noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    boolean dstsDestroyed = false;

                /*
                 * List all destinations
                 */
                    broker.sendGetDestinationsMessage(null, -1);
                    Vector dests = broker.receiveGetDestinationsReplyMessage();

                    if (dests != null) {
                        Enumeration thisEnum = dests.elements();

                        while (thisEnum.hasMoreElements()) {
                            DestinationInfo dInfo = (DestinationInfo)thisEnum.nextElement();
                            String          destName;
                            int             destTypeMask;

                            destName = dInfo.name;
                            destTypeMask = dInfo.type;

                            if (MessageType.JMQ_ADMIN_DEST.equals(destName)
                            || DestType.isInternal(dInfo.fulltype)
                            || DestType.isTemporary(dInfo.type) ||
                                    DestType.isDMQ(dInfo.type)) {

//                                Globals.stdOutPrintln("Skipping destination: " + destName);
sb.append("Skipping destination: " + destName).append("\n");
                                continue;
                            }

                            try  {
                                broker.sendDestroyDestinationMessage(destName, destTypeMask);
                                broker.receiveDestroyDestinationReplyMessage();

                                if (DestType.isQueue(destTypeMask)) {
                                    Globals.stdOutPrintln("Successfully destroyed queue "
                                            + destName);
                                } else  {
                                    Globals.stdOutPrintln("Successfully destroyed topic "
                                            + destName);
                                }
                                dstsDestroyed = true;
                            } catch (BrokerAdminException destroyEx)  {
                                handleBrokerAdminException(destroyEx);

                                if (DestType.isQueue(destTypeMask)) {
//                                    Globals.stdOutPrintln("Destroy failed for queue " + destName);
sb.append("Destroy failed for queue " + destName).append("\n");
                                } else  {
//                                    Globals.stdOutPrintln("Destroy failed for topic " + destName);
sb.append("Destroy failed for topic " + destName).append("\n");
                                }
                                ret_code = 1;
                            }
                        }

                    }

                    if (!dstsDestroyed)  {
//                        Globals.stdOutPrintln("No destinations destroyed.");
sb.append("No destinations destroyed.").append("\n");
                    }

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln("Destroying all the destinations failed");
sb.append("Destroying all the destinations failed").append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln("The destinations were not destroyed.");
sb.append("The destinations were not destroyed.").append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln("The destinations were not destroyed.");
sb.append("The destinations were not destroyed.").append("\n");
                return (1);
            }

        }

        broker.close();

        return (ret_code);
    }

    private int runUpdate(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin	broker;
        Properties	targetAttrs;
        String		input = null;
        String 		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        broker = init();

        String commandArg = brokerCmdProps.getCommandArg();
        boolean force = brokerCmdProps.forceModeSet();

        if (CMDARG_BROKER.equals(commandArg)) {
            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            targetAttrs = brokerCmdProps.getTargetAttrs();
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_BKR));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_BKR)).append("\n");
//            Globals.stdOutPrintln("");
sb.append("").append("\n");
            printAttrs(targetAttrs);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_UPDATE_BKR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");

                if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_BKR_NOOP)).append("\n");
                    return (0);

                } else if (!(yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input))) {
//                    Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                    Globals.stdOutPrintln("");
sb.append("").append("\n");
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_BKR_NOOP)).append("\n");
                    return (1);
                }
            }

            try {
                connectToBroker(broker);

                broker.sendUpdateBrokerPropsMessage(targetAttrs);
                broker.receiveUpdateBrokerPropsReplyMessage();
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_BKR_SUC)).append("\n");

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_BKR_FAIL)).append("\n");
                return (1);
            }

        } else if (CMDARG_SERVICE.equals(commandArg)) {
            ServiceInfo	si;
            String svcName;

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            targetAttrs = brokerCmdProps.getTargetAttrs();
            svcName = brokerCmdProps.getTargetName();
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC, svcName));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC, svcName)).append("\n");
//            Globals.stdOutPrintln("");
sb.append("").append("\n");
            printAttrs(targetAttrs);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            si = getServiceInfoFromAttrs(targetAttrs);
            si.setName(svcName);

            /*
             * Get the svcPort value.
             */
            int svcType = -1;
            int svcPort = -1;

            Vector svc = null;
            try {
                connectToBroker(broker);

                broker.sendGetServicesMessage(svcName);
                svc = broker.receiveGetServicesReplyMessage();

                if ((svc != null) && (svc.size() == 1)) {
                    Enumeration thisEnum = svc.elements();
                    ServiceInfo sInfo = (ServiceInfo)thisEnum.nextElement();
                    svcType = sInfo.type;
                    svcPort = sInfo.port;
                }
            } catch (BrokerAdminException bae) {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                /*
                 * Rollback the fix for bug 4432483: jmqcmd, jmqadmin: setting
                 * admin max threads = 0 is allowed & hangs.
                 * Now this check is done by the broker.
                if ((si.isModified(ServiceInfo.MAX_THREADS)) && (si.maxThreads == 0)) {
//                    Globals.stdErrPrintln(ar.getString(ar.W_SET_MAX_THREAD_ZERO, svcName));
sb.append(ar.getString(ar.W_SET_MAX_THREAD_ZERO, svcName)).append("\n");
                }
                 */
                input = getUserInput(ar.getString(ar.Q_UPDATE_SVC_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");

                if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC_NOOP)).append("\n");
                    return (0);

                } else if (!(yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input))) {
//                    Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                    Globals.stdOutPrintln("");
sb.append("").append("\n");
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC_NOOP)).append("\n");
                    return (1);
                }
            }

            /*
             * Rollback the fix for bug 4432483: jmqcmd, jmqadmin: setting
             * admin max threads = 0 is allowed & hangs.
            if ((si.isModified(ServiceInfo.MAX_THREADS)) && (si.maxThreads == 0) &&
                (ServiceType.ADMIN == svcType)) {
//                Globals.stdErrPrintln(ar.getString(ar.E_ADMIN_MAX_THREAD));
sb.append(ar.getString(ar.E_ADMIN_MAX_THREAD)).append("\n");
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL)).append("\n");
                return (1);
            }
             */

            // If the port is -1, it is not used, so disallow the update.
            if ((si.isModified(ServiceInfo.PORT)) && (svcPort == -1)) {
                Globals.stdErrPrintln(ar.getString
                        (ar.E_PORT_NOT_ALLOWED_TO_CHANGE, svcName));
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL)).append("\n");
                return (1);
            }

            try {
                broker.sendUpdateServiceMessage(si);
                broker.receiveUpdateServiceReplyMessage();
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC_SUC));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC_SUC)).append("\n");

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_SVC_FAIL)).append("\n");
                return (1);
            }

        } else if (CMDARG_DESTINATION.equals(commandArg)) {
            DestinationInfo	di;
            String destName;
            int destTypeMask;

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_DEST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_DEST_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

            targetAttrs = brokerCmdProps.getTargetAttrs();
            destTypeMask = getDestTypeMask(brokerCmdProps);
            destName = brokerCmdProps.getTargetName();
            if (DestType.isQueue(destTypeMask)) {
                Globals.stdOutPrintln(ar.getString(
                        ar.I_JMQCMD_UPDATE_DEST_Q, destName));
            } else  {
                Globals.stdOutPrintln(ar.getString(
                        ar.I_JMQCMD_UPDATE_DEST_T, destName));
            }
//            Globals.stdOutPrintln("");
sb.append("").append("\n");
            printAttrs(targetAttrs);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            if (!force) {
                if (updatingDestXMLSchema(targetAttrs))  {
                    Object args[] = new Object [ 3 ];

                    args[0] = PROP_NAME_VALIDATE_XML_SCHEMA_ENABLED;
                    args[1] = PROP_NAME_XML_SCHEMA_URI_LIST;
                    args[2] = PROP_NAME_RELOAD_XML_SCHEMA_ON_FAILURE;
                    input = getUserInput(ar.getString(ar.Q_UPDATE_DEST_XML_SCHEMA_OK,
                            args), noShort);
                } else  {
                    input = getUserInput(ar.getString(ar.Q_UPDATE_DEST_OK), noShort);
                }
//                Globals.stdOutPrintln("");
sb.append("").append("\n");

                if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_DEST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_DEST_NOOP)).append("\n");
                    return (0);

                } else if (!(yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input))) {
//                    Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                    Globals.stdOutPrintln("");
sb.append("").append("\n");
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_DEST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_DEST_NOOP)).append("\n");
                    return (1);
                }
            }

            try {
                di = getDestinationInfoFromAttrs(targetAttrs);
                di.setType(destTypeMask);
                di.setName(destName);

                connectToBroker(broker);

                broker.sendUpdateDestinationMessage(di);
                broker.receiveUpdateDestinationReplyMessage();
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UPDATE_DEST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_DEST_SUC)).append("\n");

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UPDATE_DEST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UPDATE_DEST_FAIL)).append("\n");
                return (1);
            }

        }

        broker.close();

        return (0);
    }

    private void printAttrs(Properties targetAttrs) {
        printAttrs(targetAttrs, false);
    }

    private void printAttrs(Properties targetAttrs, boolean printTitle) {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4);
        String[] row = new String[2];

        if (printTitle)  {
            bcp = new BrokerCmdPrinter(2, 4, "-");
            row[0] = "Property Name";
            row[1] = "Property Value";
            bcp.addTitle(row);
        } else  {
            bcp = new BrokerCmdPrinter(2, 4);
        }

        for (Enumeration e = targetAttrs.propertyNames();  e.hasMoreElements() ;) {
            String propName = (String)e.nextElement(),
                    value = targetAttrs.getProperty(propName);
            row[0] = propName;
            row[1] = value;
            bcp.add(row);
        }
        bcp.println();
    }

    private ServiceInfo getServiceInfoFromAttrs(Properties svcAttrs) {
        ServiceInfo si = new ServiceInfo();

        for (Enumeration e = svcAttrs.propertyNames();  e.hasMoreElements() ;) {
            String propName = (String)e.nextElement(),
                    value = svcAttrs.getProperty(propName);
            int		intValue = 0;
            boolean	valueOK = true;

            if (propName.equals(PROP_NAME_SVC_PORT))  {
                try  {
                    intValue = Integer.parseInt(value);
                } catch (NumberFormatException nfe)  {
                    valueOK = false;
                }

                if (valueOK)  {
                    si.setPort(intValue);
                }
                continue;
            }

            if (propName.equals(PROP_NAME_SVC_MIN_THREADS))  {
                try  {
                    intValue = Integer.parseInt(value);
                } catch (NumberFormatException nfe)  {
                    valueOK = false;
                }

                if (valueOK)  {
                    si.setMinThreads(intValue);
                }
                continue;
            }

            if (propName.equals(PROP_NAME_SVC_MAX_THREADS))  {
                try  {
                    intValue = Integer.parseInt(value);
                } catch (NumberFormatException nfe)  {
                    valueOK = false;
                }

                if (valueOK)  {
                    si.setMaxThreads(intValue);
                }
                continue;
            }
        }

        return (si);
    }

    private boolean updatingDestXMLSchema(Properties dstAttrs) {
        String value;

        if (dstAttrs == null)  {
            return (false);
        }

        value = dstAttrs.getProperty(PROP_NAME_VALIDATE_XML_SCHEMA_ENABLED);
        if ((value != null) && !value.equals(""))  {
            return (true);
        }

        value = dstAttrs.getProperty(PROP_NAME_XML_SCHEMA_URI_LIST);
        if ((value != null) && !value.equals(""))  {
            return (true);
        }

        value = dstAttrs.getProperty(PROP_NAME_RELOAD_XML_SCHEMA_ON_FAILURE);
        if ((value != null) && !value.equals(""))  {
            return (true);
        }

        return (false);
    }

    public DestinationInfo getDestinationInfoFromAttrs(Properties destAttrs) {
        DestinationInfo di = new DestinationInfo();

        for (Enumeration e = destAttrs.propertyNames();  e.hasMoreElements() ;) {
            String propName = (String)e.nextElement(),
                    value = destAttrs.getProperty(propName);
            SizeString	ss;
            int		intValue = 0;
            long	longValue = 0;
            boolean	valueOK = true;

            /*
             * maxTotalMsgBytes
             */
            if (propName.equals(PROP_NAME_OPTION_MAX_MESG_BYTE))  {
                try  {
                    ss = new SizeString(value);
                    longValue = ss.getBytes();
                } catch (NumberFormatException nfe)  {
                    valueOK = false;
                }

                if (valueOK)  {
                    di.setMaxMessageBytes(longValue);
                }
                continue;
            }

            /*
             * maxNumMsgs
             */
            if (propName.equals(PROP_NAME_OPTION_MAX_MESG))  {
                try  {
                    intValue = Integer.parseInt(value);
                } catch (NumberFormatException nfe)  {
                    valueOK = false;
                }

                if (valueOK)  {
                    di.setMaxMessages(intValue);
                }
                continue;
            }

            /*
             * maxBytesPerMsg
             */
            if (propName.equals(PROP_NAME_OPTION_MAX_PER_MESG_SIZE))  {
                try  {
                    ss = new SizeString(value);
                    longValue = ss.getBytes();
                } catch (NumberFormatException nfe)  {
                    valueOK = false;
                }

                if (valueOK)  {
                    di.setMaxMessageSize(longValue);
                }
                continue;
            }


            /*
             * maxFailoverConsumerCount
             */
            if (propName.equals(PROP_NAME_MAX_FAILOVER_CONSUMER_COUNT))  {
                try  {
                    di.setMaxFailoverConsumers(Integer.parseInt(value));
                } catch (NumberFormatException nfe)  {
                }
            }

            /*
             * maxNumBackupConsumers
             */
            if (propName.equals(PROP_NAME_MAX_ACTIVE_CONSUMER_COUNT))  {
                try  {
                    di.setMaxActiveConsumers(Integer.parseInt(value));
                } catch (NumberFormatException nfe)  {
                }
            }

            /*
             * isLocalDestination
             */
            if (propName.equals(PROP_NAME_IS_LOCAL_DEST))  {
                di.setScope(Boolean.valueOf(value).booleanValue());
            }

            /*
             * limitBehaviour
             */
            if (propName.equals(PROP_NAME_LIMIT_BEHAVIOUR))  {
                di.setLimitBehavior(getLimitBehavValue(value));
            }

            /*
             * localDeliveryPreferred
             */
            if (propName.equals(PROP_NAME_LOCAL_DELIVERY_PREF))  {
                di.setClusterDeliveryPolicy(getClusterDeliveryPolicy(value));
            }

            /*
             * maxPrefetchCount
             */
            if (propName.equals(PROP_NAME_CONSUMER_FLOW_LIMIT))  {
                try  {
                    di.setPrefetch(Integer.parseInt(value));
                } catch (NumberFormatException nfe)  {
                }
            }

            /*
             * maxProducerCount
             */
            if (propName.equals(PROP_NAME_MAX_PRODUCERS))  {
                try  {
                    di.setMaxProducers(Integer.parseInt(value));
                } catch (NumberFormatException nfe)  {
                }
            }

            /*
             * useDMQ
             */
            if (propName.equals(PROP_NAME_USE_DMQ))  {
                di.setUseDMQ(Boolean.valueOf(value).booleanValue());
            }

            if (propName.equals(PROP_NAME_VALIDATE_XML_SCHEMA_ENABLED))  {
                di.setValidateXMLSchemaEnabled(Boolean.valueOf(value).booleanValue());
            }

            if (propName.equals(PROP_NAME_XML_SCHEMA_URI_LIST))  {
                di.setXMLSchemaUriList(value);
            }

            if (propName.equals(PROP_NAME_RELOAD_XML_SCHEMA_ON_FAILURE))  {
                di.setReloadXMLSchemaOnFailure(Boolean.valueOf(value).booleanValue());
            }
        }

        return (di);
    }


    private int runQuery(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin     broker;

        broker = init();

        // Check for the target argument.
        // Valid values are dst and svc.
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_DESTINATION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_DST_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String destName = brokerCmdProps.getTargetName();
            int destTypeMask = getDestTypeMask(brokerCmdProps);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_DST));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_DST)).append("\n");
            printDestinationInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetDestinationsMessage(destName, destTypeMask);
                Vector dest = broker.receiveGetDestinationsReplyMessage();

                if ((dest != null) && (dest.size() == 1)) {
                    Enumeration thisEnum = dest.elements();
                    DestinationInfo dInfo = (DestinationInfo)thisEnum.nextElement();
                    BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4);
                    String[] row = new String[2];

                    bcp.setSortNeeded(false);

                    /*
                     * Basic info - name/type/state etc.
                     */
                    row[0] = ar.getString(ar.I_JMQCMD_DST_NAME);
                    row[1] = dInfo.name;
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_TYPE);
                    row[1] = BrokerAdminUtil.getDestinationType(dInfo.type);
                    // If the destination is temporary, indicate so.
                    if (DestType.isTemporary(dInfo.type))
                        row[1] = row[1] + " ("
                                + ar.getString(ar.I_TEMPORARY)
                                + ")";

                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_STATE);
                    row[1] = DestState.toString(dInfo.destState);
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_CREATED_ADMIN);
                    if (dInfo.autocreated)  {
                        row[1] = Boolean.FALSE.toString();
                    } else  {
                        row[1] = Boolean.TRUE.toString();
                    }
                    bcp.add(row);

                    row[0] = "";
                    row[1] = "";
                    bcp.add(row);

                    /*
                     * 'Current' numbers
                     */
                    row[0] = ar.getString(ar.I_JMQCMD_DST_CUR_MSG);
                    row[1] = "";
                    bcp.add(row);

                    String indent = "    ";

                    row[0] = indent + ar.getString(ar.I_JMQCMD_DST_ACTUAL);
                    row[1] = new Integer(dInfo.nMessages - dInfo.nTxnMessages).toString();
                    bcp.add(row);

                    row[0] = indent + ar.getString(ar.I_JMQCMD_DST_REMOTE);
                    row[1] = new Integer(dInfo.nRemoteMessages).toString();
                    bcp.add(row);

                    row[0] = indent + ar.getString(ar.I_JMQCMD_DST_HELD_IN_TXN);
                    row[1] = new Integer(dInfo.nTxnMessages).toString();
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_CUR_MSG_BYTES);
                    row[1] = "";
                    bcp.add(row);

                    row[0] = indent + ar.getString(ar.I_JMQCMD_DST_ACTUAL);
                    row[1] = new Long(dInfo.nMessageBytes - dInfo.nTxnMessageBytes).toString();
                    bcp.add(row);

                    row[0] = indent + ar.getString(ar.I_JMQCMD_DST_REMOTE);
                    row[1] = new Long(dInfo.nRemoteMessageBytes).toString();
                    bcp.add(row);

                    row[0] = indent + ar.getString(ar.I_JMQCMD_DST_HELD_IN_TXN);
                    row[1] = new Long(dInfo.nTxnMessageBytes).toString();
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_CUR_PRODUCERS);
                    row[1] = new Integer(dInfo.nProducers).toString();
                    bcp.add(row);

                    if (DestType.isQueue(destTypeMask)) {
                        row[0] = ar.getString(ar.I_JMQCMD_DST_CUR_ACTIVE_CONS);
                        row[1] = new Integer(dInfo.naConsumers).toString();
                        bcp.add(row);

                        row[0] = ar.getString(ar.I_JMQCMD_DST_CUR_FAILOVER_CONS);
                        row[1] = new Integer(dInfo.nfConsumers).toString();
                        bcp.add(row);
                    } else  {
                        Hashtable h = dInfo.producerWildcards;

                        row[0] = ar.getString(ar.I_JMQCMD_DST_CUR_NUM_PRODUCERS_WILDCARD);
                        row[1] = Integer.toString(getWildcardCount(h));
                        bcp.add(row);

                        /*
                         * The code below will print something like:
                         *
                         *	foo.bar.* (2)
                         *	bar.* (1)
                         */
                        Enumeration keys;
                        if (h != null)  {
                            keys = h.keys();

                            while (keys.hasMoreElements())  {
                                String wildcard = (String)keys.nextElement();
                                Integer val = (Integer)h.get(wildcard);
                                row[0] = indent + wildcard + "  (" + val + ")";
                                row[1] = "";
                                bcp.add(row);
                            }
                        }


                        row[0] = ar.getString(ar.I_JMQCMD_DST_CUR_CONS);
                        row[1] = new Integer(dInfo.nConsumers).toString();
                        bcp.add(row);

                        h = dInfo.consumerWildcards;

                        row[0] = ar.getString(ar.I_JMQCMD_DST_CUR_NUM_CONSUMERS_WILDCARD);
                        row[1] = Integer.toString(getWildcardCount(h));
                        bcp.add(row);

                        if (h != null)  {
                            keys = h.keys();

                            while (keys.hasMoreElements())  {
                                String wildcard = (String)keys.nextElement();
                                Integer val = (Integer)h.get(wildcard);
                                row[0] = indent + wildcard + "  (" + val + ")";
                                row[1] = "";
                                bcp.add(row);
                            }
                        }

                    }

                    row[0] = "";
                    row[1] = "";
                    bcp.add(row);

                    /*
                     * 'Current' numbers
                     */
                    row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_MSG_ALLOW);
                    row[1] = checkAndPrintUnlimitedInt(dInfo.maxMessages, zeroNegOneInt);
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_MSG_BYTES_ALLOW);
                    row[1] = checkAndPrintUnlimitedLong(dInfo.maxMessageBytes,
                            zeroNegOneLong);
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_BYTES_PER_MSG_ALLOW);
                    row[1] = new Long(dInfo.maxMessageSize).toString();
                    row[1] = checkAndPrintUnlimitedLong(dInfo.maxMessageSize,
                            zeroNegOneLong);
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_PRODUCERS);
                    row[1] = checkAndPrintUnlimitedInt(dInfo.maxProducers, -1);
                    bcp.add(row);

                    if (DestType.isQueue(destTypeMask)) {
                        row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_ACTIVE_CONSUMER_COUNT);
                        row[1] = checkAndPrintUnlimitedInt(dInfo.maxActiveConsumers, -1);
                        bcp.add(row);

                        row[0] = ar.getString(ar.I_JMQCMD_DST_MAX_FAILOVER_CONSUMER_COUNT);
                        row[1] = checkAndPrintUnlimitedInt(dInfo.maxFailoverConsumers, -1);
                        bcp.add(row);
                    }

                    row[0] = "";
                    row[1] = "";
                    bcp.add(row);

                    /*
                     * Other misc props
                     */
                    row[0] = ar.getString(ar.I_JMQCMD_DST_LIMIT_BEHAVIOUR);
                    row[1] = DestLimitBehavior.getString(dInfo.destLimitBehavior);
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_CONS_FLOW_LIMIT);
                    row[1] = checkAndPrintUnlimitedInt(dInfo.maxPrefetch, -1);
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_IS_LOCAL_DEST);
                    if (dInfo.isDestinationLocal())  {
                        row[1] = Boolean.TRUE.toString();
                    } else  {
                        row[1] = Boolean.FALSE.toString();
                    }
                    bcp.add(row);

                    if (DestType.isQueue(destTypeMask)) {
                        row[0] = ar.getString(ar.I_JMQCMD_DST_LOCAL_DELIVERY_PREF);
                        if (dInfo.destCDP == ClusterDeliveryPolicy.LOCAL_PREFERRED)  {
                            row[1] = Boolean.TRUE.toString();
                        } else  {
                            row[1] = Boolean.FALSE.toString();
                        }
                        bcp.add(row);
                    }

                    row[0] = ar.getString(ar.I_JMQCMD_DST_USE_DMQ);
                    if (dInfo.useDMQ())  {
                        row[1] = Boolean.TRUE.toString();
                    } else  {
                        row[1] = Boolean.FALSE.toString();
                    }
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_VALIDATE_XML_SCHEMA_ENABLED);
                    if (dInfo.validateXMLSchemaEnabled())  {
                        row[1] = Boolean.TRUE.toString();
                    } else  {
                        row[1] = Boolean.FALSE.toString();
                    }
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_XML_SCHEMA_URI_LIST);
                    row[1] = dInfo.XMLSchemaUriList;
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_DST_RELOAD_XML_SCHEMA_ON_FAILURE);
                    if (dInfo.reloadXMLSchemaOnFailure())  {
                        row[1] = Boolean.TRUE.toString();
                    } else  {
                        row[1] = Boolean.FALSE.toString();
                    }
                    bcp.add(row);

                    bcp.println();

//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_DST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_DST_SUC)).append("\n");
                } else  {
                    // Should not get here, since if something went wrong we should get
                    // a BrokerAdminException
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET));
sb.append(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET)).append("\n");
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_DST_FAIL)).append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_DST_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_SERVICE.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_SVC_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String svcName = brokerCmdProps.getTargetName();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_SVC));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_SVC)).append("\n");
            printServiceInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetServicesMessage(svcName);
                Vector svc = broker.receiveGetServicesReplyMessage();

                if ((svc != null) && (svc.size() == 1)) {
                    Enumeration thisEnum = svc.elements();
                    ServiceInfo sInfo = (ServiceInfo)thisEnum.nextElement();
                    BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4);
                    String[] row = new String[2];

                    bcp.setSortNeeded(false);

                    /*
                     * Basic info - name/port/state
                     */
                    row[0] = ar.getString(ar.I_JMQCMD_SVC_NAME);
                    row[1] = sInfo.name;
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_SVC_STATE);
                    row[1] = ServiceState.getString(sInfo.state);
                    bcp.add(row);

                    // ONLY display port number if it is applicable
                    // It is NOT applicable if it is set to -1
                    if (sInfo.port != -1) {
                        row[0] = ar.getString(ar.I_JMQCMD_SVC_PORT);

                        // Add more information about the port number:
                        // dynamically generated or statically declared
                        if (sInfo.dynamicPort) {
                            switch (sInfo.state) {
                                case ServiceState.UNKNOWN:
                                    row[1] = ar.getString(ar.I_DYNAMIC);
                                    break;
                                default:
                                    row[1] = new Integer(sInfo.port).toString()
                                    +
                                            " ("
                                            +
                                            ar.getString(ar.I_DYNAMIC)
                                            +
                                            ")";
                            }
                        } else {
                            row[1] = new Integer(sInfo.port).toString() +
                                    " (" + ar.getString(ar.I_STATIC) + ")";
                        }
                        bcp.add(row);
                    }

                    row[0] = "";
                    row[1] = "";
                    bcp.add(row);

                    /*
                     * 'Curent' numbers
                     */
                    row[0] = ar.getString(ar.I_JMQCMD_SVC_CUR_THREADS);
                    row[1] = new Integer(sInfo.currentThreads).toString();
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_SVC_NUM_CXN);
                    row[1] = new Integer(sInfo.nConnections).toString();
                    bcp.add(row);

                    row[0] = "";
                    row[1] = "";
                    bcp.add(row);

                    /*
                     * Min/Max numbers
                     */
                    row[0] = ar.getString(ar.I_JMQCMD_SVC_MIN_THREADS);
                    row[1] = new Integer(sInfo.minThreads).toString();
                    bcp.add(row);

                    row[0] = ar.getString(ar.I_JMQCMD_SVC_MAX_THREADS);
                    row[1] = new Integer(sInfo.maxThreads).toString();
                    bcp.add(row);


                    bcp.println();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_SVC_SUC));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_SVC_SUC)).append("\n");

                } else  {
                    // Should not get here, since if something went wrong we should get
                    // a BrokerAdminException
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET));
sb.append(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET)).append("\n");
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_SVC_FAIL)).append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_SVC_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_BROKER.equals(commandArg)) {
            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_BKR_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetBrokerPropsMessage();
                Properties bkrProps = broker.receiveGetBrokerPropsReplyMessage();

                if (bkrProps == null) {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_BKR_FAIL)).append("\n");
                    return (1);
                }

                if (brokerCmdProps.adminDebugModeSet())  {
                    printAllBrokerAttrs(bkrProps);
                } else  {
                    printDisplayableBrokerAttrs(bkrProps);
                }

//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_BKR_SUC)).append("\n");

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_BKR_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_TRANSACTION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_TXN_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String tidStr = brokerCmdProps.getTargetName();
            Long tid = null;

            try  {
                tid = Long.valueOf(tidStr);
            } catch (NumberFormatException nfe)  {
//                Globals.stdErrPrintln(ar.getString(ar.E_INVALID_TXN_ID, tidStr));
sb.append(ar.getString(ar.E_INVALID_TXN_ID, tidStr)).append("\n");
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_TXN_FAIL)).append("\n");
                return (1);
            }

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_TXN));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_TXN)).append("\n");
            printTransactionInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetTxnsMessage(tid);
                Vector txns = broker.receiveGetTxnsReplyMessage();

                if ((txns != null) && (txns.size() == 1)) {
                    Enumeration thisEnum = txns.elements();
                    Hashtable txnInfo = (Hashtable)thisEnum.nextElement();

                    if (brokerCmdProps.debugModeSet())  {
                        printAllTxnAttrs(txnInfo);
                    } else  {
                        printDisplayableTxnAttrs(txnInfo);
                    }

//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_TXN_SUC));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_TXN_SUC)).append("\n");

                } else  {
                    // Should not get here, since if something went wrong we should get
                    // a BrokerAdminException
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET));
sb.append(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET)).append("\n");
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_TXN_FAIL)).append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_TXN_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_CONNECTION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_CXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_CXN_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String cxnIdStr = brokerCmdProps.getTargetName();
            Long cxnId = null;

            try  {
                cxnId = Long.valueOf(cxnIdStr);
            } catch (NumberFormatException nfe)  {
//                Globals.stdErrPrintln(ar.getString(ar.E_INVALID_CXN_ID, cxnIdStr));
sb.append(ar.getString(ar.E_INVALID_CXN_ID, cxnIdStr)).append("\n");
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_CXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_CXN_FAIL)).append("\n");
                return (1);
            }

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_CXN));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_CXN)).append("\n");
            printConnectionInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetConnectionsMessage(null, cxnId);
                Vector cxnList = broker.receiveGetConnectionsReplyMessage();

                if ((cxnList != null) && (cxnList.size() == 1)) {
                    Enumeration thisEnum = cxnList.elements();
                    Hashtable cxnInfo = (Hashtable)thisEnum.nextElement();

                    if (brokerCmdProps.debugModeSet())  {
                        printAllCxnAttrs(cxnInfo);
                    } else  {
                        printDisplayableCxnAttrs(cxnInfo);
                    }

//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_CXN_SUC));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_CXN_SUC)).append("\n");

                } else  {
                    // Should not get here, since if something went wrong we should get
                    // a BrokerAdminException
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET));
sb.append(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET)).append("\n");
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_CXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_CXN_FAIL)).append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_CXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_CXN_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_MSG.equals(commandArg)) {
            if (broker == null)  {
                /*
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_MSG_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_MSG_FAIL)).append("\n");
                 */
//                Globals.stdErrPrintln("Querying message failed.");
sb.append("Querying message failed.").append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String destName = brokerCmdProps.getTargetName();
            int destTypeMask = getDestTypeMask(brokerCmdProps);
            String msgID = brokerCmdProps.getMsgID();

            /*
//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_MSG));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_MSG)).append("\n");
             */
//            Globals.stdOutPrintln("Querying message:");
sb.append("Querying message:").append("\n");
            printMessageInfo();

//            Globals.stdOutPrintln("In the destination");
sb.append("In the destination").append("\n");
            printDestinationInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                connectToBroker(broker);

                broker.sendGetMessagesMessage(destName, destTypeMask, true, msgID,
                        null, null);
                Vector msgList = broker.receiveGetMessagesReplyMessage();

                if ((msgList != null) && (msgList.size() == 1)) {
                    HashMap oneMsg = (HashMap)msgList.get(0);

                    printDisplayableMsgAttrs(oneMsg);

                    /*
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_MSG_SUC));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_MSG_SUC)).append("\n");
                     */
//                    Globals.stdOutPrintln("Successfully queried message.");
sb.append("Successfully queried message.").append("\n");

                } else  {
                    /*
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_LIST_MSG_NONE));
sb.append(ar.getString(ar.I_JMQCMD_LIST_MSG_NONE)).append("\n");
                     */
//                    Globals.stdErrPrintln("There are no messages.");
sb.append("There are no messages.").append("\n");

//                    Globals.stdOutPrintln("");
sb.append("").append("\n");
                    /*
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUERY_MSG_SUC));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_MSG_SUC)).append("\n");
                     */
//                    Globals.stdOutPrintln("Successfully queried message.");
sb.append("Successfully queried message.").append("\n");
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                /*
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_MSG_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_MSG_FAIL)).append("\n");
                 */
//                Globals.stdErrPrintln("Querying message failed.");
sb.append("Querying message failed.").append("\n");
                return (1);
            }
        }

        if (broker.isConnected())  {
            broker.close();
        }

        return (0);
    }

    private int runMetrics(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin		broker;
        BrokerCmdPrinter	bcp;
        String			commandArg;
        String			titleRow[];
        long			sleepTime;
        int			metricType,
                metricSamples;

        broker = init();

        commandArg = brokerCmdProps.getCommandArg();

        sleepTime = brokerCmdProps.getMetricInterval();

        metricType = getMetricType(brokerCmdProps);
        metricSamples = brokerCmdProps.getMetricSamples();


        if (CMDARG_SERVICE.equals(commandArg)) {
            bcp = setupMetricTitle(commandArg, metricType);

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_SVC_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String svcName = brokerCmdProps.getTargetName();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_METRICS_SVC));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_SVC)).append("\n");
            printServiceInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                MetricCounters	previousMetrics = null;
                int	rowsPrinted = 0;

                connectToBroker(broker);

                while (true)  {
                    broker.sendGetMetricsMessage(svcName);
                    MetricCounters mc = (MetricCounters)broker.receiveGetMetricsReplyMessage();

                    if (mc == null) {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_SVC_FAIL)).append("\n");
                        return (1);
                    }

                    addOneMetricRow(metricType, bcp, mc, previousMetrics);

                    if ((rowsPrinted % 20) == 0)  {
                        bcp.print();
                    } else  {
                        bcp.print(false);
                    }

                    bcp.clear();
                    previousMetrics = mc;
                    rowsPrinted++;

                    if (metricSamples > 0)  {
                        if (metricSamples == rowsPrinted)  {
                            break;
                        }
                    }

                    try  {
                        Thread.sleep(sleepTime * 1000);
                    } catch (InterruptedException ie)  {
//                        Globals.stdErrPrintln(ie.toString());
sb.append(ie.toString()).append("\n");
                    }
                }

//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_METRICS_SVC_SUC));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_SVC_SUC)).append("\n");

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_SVC_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_SVC_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_BROKER.equals(commandArg)) {
            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_BKR_FAIL)).append("\n");
                return (1);
            }

            bcp = setupMetricTitle(commandArg, metricType);

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_METRICS_BKR));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                MetricCounters	previousMetrics = null;
                int	rowsPrinted = 0;

                connectToBroker(broker);

                while (true)  {
                    broker.sendGetMetricsMessage(null);
                    MetricCounters mc = (MetricCounters)broker.receiveGetMetricsReplyMessage();


                    if (mc == null) {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_BKR_FAIL)).append("\n");
                        return (1);
                    }

                    addOneMetricRow(metricType, bcp, mc, previousMetrics);

                    if ((rowsPrinted % 20) == 0)  {
                        bcp.print();
                    } else  {
                        bcp.print(false);
                    }

                    bcp.clear();
                    previousMetrics = mc;
                    rowsPrinted++;

                    if (metricSamples > 0)  {
                        if (metricSamples == rowsPrinted)  {
                            break;
                        }
                    }

                    try  {
                        Thread.sleep(sleepTime * 1000);
                    } catch (InterruptedException ie)  {
//                        Globals.stdErrPrintln(ie.toString());
sb.append(ie.toString()).append("\n");
                    }

                }

//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_METRICS_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_BKR_SUC)).append("\n");

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_BKR_FAIL)).append("\n");
                return (1);
            }
        } else if (CMDARG_DESTINATION.equals(commandArg)) {
            String destName;
            int destTypeMask;

            destName = brokerCmdProps.getTargetName();
            destTypeMask = getDestTypeMask(brokerCmdProps);

            bcp = setupDestMetricTitle(commandArg, metricType, destTypeMask);
            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_DST_FAIL)).append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_METRICS_DST));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_DST)).append("\n");
            printDestinationInfo();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
            printBrokerInfo(broker);

            try  {
                DestMetricsCounters	previousMetrics = null;
                int	rowsPrinted = 0;

                connectToBroker(broker);

                while (true)  {
                    broker.sendGetMetricsMessage(destName, destTypeMask);
                    DestMetricsCounters mc
                            = (DestMetricsCounters)broker.receiveGetMetricsReplyMessage();

                    if (mc == null) {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_DST_FAIL)).append("\n");
                        return (1);
                    }

                    addOneDestMetricRow(metricType, destTypeMask, bcp, mc,
                            previousMetrics);

                    if ((rowsPrinted % 20) == 0)  {
                        bcp.print();
                    } else  {
                        bcp.print(false);
                    }

                    bcp.clear();
                    previousMetrics = mc;
                    rowsPrinted++;

                    if (metricSamples > 0)  {
                        if (metricSamples == rowsPrinted)  {
                            break;
                        }
                    }

                    try  {
                        Thread.sleep(sleepTime * 1000);
                    } catch (InterruptedException ie)  {
//                        Globals.stdErrPrintln(ie.toString());
sb.append(ie.toString()).append("\n");
                    }

                }

//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_METRICS_DST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_DST_SUC)).append("\n");

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_METRICS_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_METRICS_DST_FAIL)).append("\n");
                return (1);
            }

        }

        if (broker.isConnected())  {
            broker.close();
        }


        return (0);
    }

    private BrokerCmdPrinter setupListDestTitle(int listType)  {
        BrokerCmdPrinter bcp = null;

        if (listType != LIST_QUEUE)  {
            bcp = new BrokerCmdPrinter(11, 2, "-");
            String[] row = new String[11];
            int span[], i = 0;

            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);

            span = new int [ 11 ];

            span[i++] = 1;
            span[i++] = 1;
            span[i++] = 1;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 4;
            span[i++] = 0;
            span[i++] = 0;
            span[i++] = 0;

            i = 0;
            row[i++] = ar.getString(ar.I_JMQCMD_DST_NAME_SHORT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_TYPE_SHORT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_STATE_SHORT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_NUM_PRODUCER);
            row[i++] = "";
            row[i++] = ar.getString(ar.I_JMQCMD_DST_NUM_CONSUMER);
            row[i++] = "";
            row[i++] = ar.getString(ar.I_JMQCMD_DST_MSGS);
            row[i++] = "";
            row[i++] = "";
            row[i++] = "";
            bcp.addTitle(row, span);

            i = 0;
            row[i++] = "";
            row[i++] = "";
            row[i++] = "";
            row[i++] = ar.getString(ar.I_JMQCMD_DST_PRODUCERS_TOTAL);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_WILDCARD);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_CONSUMERS_TOTAL);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_WILDCARD);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_MSGS_TOTAL_COUNT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_REMOTE);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_MSGS_UNACK_COUNT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_MSGS_AVG_SIZE);
            bcp.addTitle(row);
        } else  {
            bcp = new BrokerCmdPrinter(10, 2, "-");
            String[] row = new String[10];
            int span[], i = 0;

            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);

            span = new int [ 10 ];

            span[i++] = 1;
            span[i++] = 1;
            span[i++] = 1;
            span[i++] = 1;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 4;
            span[i++] = 0;
            span[i++] = 0;
            span[i++] = 0;

            i = 0;
            row[i++] = ar.getString(ar.I_JMQCMD_DST_NAME_SHORT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_TYPE_SHORT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_STATE_SHORT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_NUM_PRODUCER);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_NUM_CONSUMER);
            row[i++] = "";
            row[i++] = ar.getString(ar.I_JMQCMD_DST_MSGS);
            row[i++] = "";
            row[i++] = "";
            bcp.addTitle(row, span);

            i = 0;
            row[i++] = "";
            row[i++] = "";
            row[i++] = "";
            row[i++] = "";
            row[i++] = ar.getString(ar.I_JMQCMD_DST_CONSUMERS_ACTIVE);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_CONSUMERS_BACKUP);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_MSGS_TOTAL_COUNT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_REMOTE);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_MSGS_UNACK_COUNT);
            row[i++] = ar.getString(ar.I_JMQCMD_DST_MSGS_AVG_SIZE);
            bcp.addTitle(row);
        }

        return(bcp);
    }

    private BrokerCmdPrinter setupMetricTitle(String commandArg, int metricType)  {
        String			titleRow[];
        BrokerCmdPrinter	bcp = null;

        if (metricType == METRICS_TOTALS)  {
            int i = 0, span[];

            span = new int [ 8 ];

            bcp = new BrokerCmdPrinter(8, 2, "-", BrokerCmdPrinter.CENTER);
            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);
            titleRow = new String[8];

            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_MSGS);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_MSG_BYTES);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_PKTS);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_PKT_BYTES);
            titleRow[i++] = "";
            bcp.addTitle(titleRow, span);

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            bcp.addTitle(titleRow);
        } else if (metricType == METRICS_RATES)  {
            int i = 0, span[];

            span = new int [ 8 ];

            bcp = new BrokerCmdPrinter(8, 2, "-", BrokerCmdPrinter.CENTER);
            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);
            titleRow = new String[8];

            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_MSGS_PER_SEC);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_MSG_BYTES_PER_SEC);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_PKTS_PER_SEC);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_PKT_BYTES_PER_SEC);
            titleRow[i++] = "";
            bcp.addTitle(titleRow, span);

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            bcp.addTitle(titleRow);
        } else if (metricType == METRICS_CONNECTIONS) {
            int i = 0, span[];

            titleRow = new String[6];
            span = new int [ 6 ];

            bcp = new BrokerCmdPrinter(6, 2, "-", BrokerCmdPrinter.CENTER);
            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);

            span[i++] = 1;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 3;
            span[i++] = 0;
            span[i++] = 0;

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_CON_NUM_CON1);
            titleRow[i++] = ar.getString(ar.I_METRICS_JVM_HEAP_BYTES);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_THREADS);
            titleRow[i++] = "";
            titleRow[i++] = "";
            bcp.addTitle(titleRow, span);

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_CON_NUM_CON2);
            titleRow[i++] = ar.getString(ar.I_METRICS_TOTAL);
            titleRow[i++] = ar.getString(ar.I_METRICS_FREE);
            titleRow[i++] = ar.getString(ar.I_METRICS_ACTIVE);
            titleRow[i++] = ar.getString(ar.I_METRICS_LOW);
            titleRow[i++] = ar.getString(ar.I_METRICS_HIGH);
            bcp.addTitle(titleRow);
        }

        return (bcp);
    }


    private BrokerCmdPrinter setupDestMetricTitle(String commandArg, int metricType,
            int destTypeMask)  {
        String			titleRow[];
        BrokerCmdPrinter	bcp = null;

        if (metricType == METRICS_TOTALS)  {
            bcp = new BrokerCmdPrinter(11, 2, "-", BrokerCmdPrinter.CENTER);
            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);
            titleRow = new String[11];
            int i, span[] = new int[ 11 ];

            i = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 3;
            span[i++] = 0;
            span[i++] = 0;
            span[i++] = 3;
            span[i++] = 0;
            span[i++] = 0;
            span[i++] = 1;

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_MSGS);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_MSG_BYTES);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_MSGS_COUNT);
            titleRow[i++] = "";
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_DST_MSGS_BYTES);
            titleRow[i++] = "";
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_DST_MSGS_LARGEST1);
            bcp.addTitle(titleRow, span);

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
            titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
            titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
            titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
            titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
            titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
            titleRow[i++] = ar.getString(ar.I_METRICS_DST_MSGS_LARGEST2);
            bcp.addTitle(titleRow);
        } else if (metricType == METRICS_RATES)  {
            bcp = new BrokerCmdPrinter(11, 2, "-", BrokerCmdPrinter.CENTER);
            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);
            titleRow = new String[11];
            int i, span[] = new int[ 11 ];

            i = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 2;
            span[i++] = 0;
            span[i++] = 3;
            span[i++] = 0;
            span[i++] = 0;
            span[i++] = 3;
            span[i++] = 0;
            span[i++] = 0;
            span[i++] = 1;

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_MSGS_PER_SEC);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_MSG_BYTES_PER_SEC);
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_MSGS_COUNT);
            titleRow[i++] = "";
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_DST_MSGS_BYTES);
            titleRow[i++] = "";
            titleRow[i++] = "";
            titleRow[i++] = ar.getString(ar.I_METRICS_DST_MSGS_LARGEST1);
            bcp.addTitle(titleRow, span);

            i = 0;
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_IN);
            titleRow[i++] = ar.getString(ar.I_METRICS_OUT);
            titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
            titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
            titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
            titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
            titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
            titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
            titleRow[i++] = ar.getString(ar.I_METRICS_DST_MSGS_LARGEST2);
            bcp.addTitle(titleRow);
        } else if (metricType == METRICS_CONSUMER) {
            if (DestType.isQueue(destTypeMask)) {
                bcp = new BrokerCmdPrinter(9, 2, "-", BrokerCmdPrinter.CENTER);
                bcp.setTitleAlign(BrokerCmdPrinter.CENTER);
                titleRow = new String[9];
                int i, span[] = new int[ 9 ];

                i = 0;
                span[i++] = 3;
                span[i++] = 0;
                span[i++] = 0;
                span[i++] = 3;
                span[i++] = 0;
                span[i++] = 0;
                span[i++] = 3;
                span[i++] = 0;
                span[i++] = 0;

                i = 0;
                titleRow[i++] = ar.getString(ar.I_METRICS_DST_CON_ACTIVE_CONSUMERS);
                titleRow[i++] = "";
                titleRow[i++] = "";
                titleRow[i++] = ar.getString(ar.I_METRICS_DST_CON_BACKUP_CONSUMERS);
                titleRow[i++] = "";
                titleRow[i++] = "";
                titleRow[i++] = ar.getString(ar.I_METRICS_MSGS_COUNT);
                titleRow[i++] = "";
                titleRow[i++] = "";
                bcp.addTitle(titleRow, span);

                i = 0;
                titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
                titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
                titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
                titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
                titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
                titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
                titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
                titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
                titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
                bcp.addTitle(titleRow);
            } else  {
                bcp = new BrokerCmdPrinter(6, 2, "-", BrokerCmdPrinter.CENTER);
                bcp.setTitleAlign(BrokerCmdPrinter.CENTER);
                titleRow = new String[6];
                int i, span[] = new int[ 6 ];

                i = 0;
                span[i++] = 3;
                span[i++] = 0;
                span[i++] = 0;
                span[i++] = 3;
                span[i++] = 0;
                span[i++] = 0;

                i = 0;
                titleRow[i++] = ar.getString(ar.I_METRICS_DST_CON_CONSUMERS);
                titleRow[i++] = "";
                titleRow[i++] = "";
                titleRow[i++] = ar.getString(ar.I_METRICS_MSGS_COUNT);
                titleRow[i++] = "";
                titleRow[i++] = "";
                bcp.addTitle(titleRow, span);

                i = 0;
                titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
                titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
                titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
                titleRow[i++] = ar.getString(ar.I_METRICS_CURRENT);
                titleRow[i++] = ar.getString(ar.I_METRICS_PEAK);
                titleRow[i++] = ar.getString(ar.I_METRICS_AVERAGE);
                bcp.addTitle(titleRow);
            }
        } else if (metricType == METRICS_DISK) {
            bcp = new BrokerCmdPrinter(3, 2, "-", BrokerCmdPrinter.CENTER);
            titleRow = new String[3];

            titleRow[0] = ar.getString(ar.I_METRICS_DSK_RESERVED);
            titleRow[1] = ar.getString(ar.I_METRICS_DSK_USED);
            titleRow[2] = ar.getString(ar.I_METRICS_DSK_UTIL_RATIO);
            bcp.addTitle(titleRow);
        } else if (metricType == METRICS_REMOVE) {
            bcp = new BrokerCmdPrinter(3, 2, "-", BrokerCmdPrinter.CENTER);
            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);
            titleRow = new String[3];
            int i, span[] = new int[ 3 ];

            i = 0;
            span[i++] = 3;
            span[i++] = 0;
            span[i++] = 0;

            i = 0;
            /*
            titleRow[i++] = ar.getString(ar.I_METRICS_MSGS_REMOVED);
             */
            titleRow[i++] = "Msgs Removed";
            titleRow[i++] = "";
            titleRow[i++] = "";
            bcp.addTitle(titleRow, span);

            i = 0;
            /*
            titleRow[i++] = ar.getString(ar.I_METRICS_EXPIRED);
            titleRow[i++] = ar.getString(ar.I_METRICS_DISCARDED);
            titleRow[i++] = ar.getString(ar.I_METRICS_PURGED);
             */
            titleRow[i++] = "Expired";
            titleRow[i++] = "Discarded";
            titleRow[i++] = "Purged";
            bcp.addTitle(titleRow);
        }

        return (bcp);
    }

    private void addOneMetricRow(int metricType, BrokerCmdPrinter bcp,
            MetricCounters latest,
            MetricCounters previous)  {
        String	metricRow[];

        if (metricType == METRICS_TOTALS)  {
            metricRow = new String[8];

            metricRow[0] = Long.toString(latest.messagesIn);
            metricRow[1] = Long.toString(latest.messagesOut);
            metricRow[2] = Long.toString(latest.messageBytesIn);
            metricRow[3] = Long.toString(latest.messageBytesOut);
            metricRow[4] = Long.toString(latest.packetsIn);
            metricRow[5] = Long.toString(latest.packetsOut);
            metricRow[6] = Long.toString(latest.packetBytesIn);
            metricRow[7] = Long.toString(latest.packetBytesOut);

            bcp.add(metricRow);
        } else if (metricType == METRICS_RATES)  {
            metricRow = new String[8];

            if (previous == null)  {
                metricRow[0] = "0";
                metricRow[1] = "0";
                metricRow[2] = "0";
                metricRow[3] = "0";
                metricRow[4] = "0";
                metricRow[5] = "0";
                metricRow[6] = "0";
                metricRow[7] = "0";
            } else  {
                float	secs;

                secs = (float)(latest.timeStamp - previous.timeStamp)/(float)1000;

                metricRow[0] = getRateString(latest.messagesIn,
                        previous.messagesIn, secs);

                metricRow[1] = getRateString(latest.messagesOut,
                        previous.messagesOut, secs);

                metricRow[2] = getRateString(latest.messageBytesIn,
                        previous.messageBytesIn, secs);

                metricRow[3] = getRateString(latest.messageBytesOut,
                        previous.messageBytesOut, secs);

                metricRow[4] = getRateString(latest.packetsIn,
                        previous.packetsIn, secs);

                metricRow[5] = getRateString(latest.packetsOut,
                        previous.packetsOut, secs);

                metricRow[6] = getRateString(latest.packetBytesIn,
                        previous.packetBytesIn, secs);

                metricRow[7] = getRateString(latest.packetBytesOut,
                        previous.packetBytesOut, secs);
            }

            bcp.add(metricRow);

        } else if (metricType == METRICS_CONNECTIONS) {
            metricRow = new String[6];
            metricRow[0] = Integer.toString(latest.nConnections);
            metricRow[1] = Long.toString(latest.totalMemory);
            metricRow[2] = Long.toString(latest.freeMemory);
            metricRow[3] = Integer.toString(latest.threadsActive);
            metricRow[4] = Integer.toString(latest.threadsLowWater);
            metricRow[5] = Integer.toString(latest.threadsHighWater);
            bcp.add(metricRow);
        }
    }

    private String getRateString(long latest, long previous, float secs)  {
        long	diff, rate;
        String	rateString = "";

        diff = latest - previous;

        rate = (long)(diff/secs);

        if (rate == 0)  {
            if (diff != 0)  {
                rateString = "< 1";
            } else  {
                rateString = "0";
            }
        } else  {
            rateString = Long.toString(rate);
        }

        return (rateString);
    }

    private String displayInKBytes(long l)  {
        if (l == 0)  {
            return ("0");
        } else if (l < 1024)  {
            return ("< 1");
        } else  {
            return(Long.toString(l/1024));
        }
    }

    private void addOneDestMetricRow(int metricType, int destTypeMask,
            BrokerCmdPrinter bcp,
            DestMetricsCounters latestDest,
            DestMetricsCounters previousDest)  {
        String	metricRow[];

        if (metricType == METRICS_TOTALS)  {
            metricRow = new String[11];

            metricRow[0] = Integer.toString(latestDest.getMessagesIn());
            metricRow[1] = Integer.toString(latestDest.getMessagesOut());
            metricRow[2] = Long.toString(latestDest.getMessageBytesIn());
            metricRow[3] = Long.toString(latestDest.getMessageBytesOut());

            metricRow[4] = Integer.toString(latestDest.getCurrentMessages());
            metricRow[5] = Integer.toString(latestDest.getHighWaterMessages());
            metricRow[6] = Integer.toString(latestDest.getAverageMessages());

            metricRow[7] = displayInKBytes(latestDest.getCurrentMessageBytes());
            metricRow[8] = displayInKBytes(latestDest.getHighWaterMessageBytes());
            metricRow[9] = displayInKBytes(latestDest.getAverageMessageBytes());
            metricRow[10] = displayInKBytes(latestDest.getHighWaterLargestMsgBytes());

            bcp.add(metricRow);
        } else if (metricType == METRICS_RATES)  {
            metricRow = new String[11];

            if (previousDest == null)  {
                metricRow[0] = "0";
                metricRow[1] = "0";
                metricRow[2] = "0";
                metricRow[3] = "0";
            } else  {
                float	secs;

                secs = (float)(latestDest.timeStamp - previousDest.timeStamp)/(float)1000;

                metricRow[0] = getRateString(latestDest.getMessagesIn(),
                        previousDest.getMessagesIn(), secs);

                metricRow[1] = getRateString(latestDest.getMessagesOut(),
                        previousDest.getMessagesOut(), secs);

                metricRow[2] = getRateString(latestDest.getMessageBytesIn(),
                        previousDest.getMessageBytesIn(), secs);

                metricRow[3] = getRateString(latestDest.getMessageBytesOut(),
                        previousDest.getMessageBytesOut(), secs);
            }

            metricRow[4] = Integer.toString(latestDest.getCurrentMessages());
            metricRow[5] = Integer.toString(latestDest.getHighWaterMessages());
            metricRow[6] = Integer.toString(latestDest.getAverageMessages());

            metricRow[7] = displayInKBytes(latestDest.getCurrentMessageBytes());
            metricRow[8] = displayInKBytes(latestDest.getHighWaterMessageBytes());
            metricRow[9] = displayInKBytes(latestDest.getAverageMessageBytes());
            metricRow[10] = displayInKBytes(latestDest.getHighWaterLargestMsgBytes());

            bcp.add(metricRow);
        } else if (metricType == METRICS_CONSUMER) {
            if (DestType.isQueue(destTypeMask)) {
                metricRow = new String[9];

                metricRow[0] = Integer.toString(latestDest.getActiveConsumers());
                metricRow[1] = Integer.toString(latestDest.getHWActiveConsumers());
                metricRow[2] = Integer.toString(latestDest.getAvgActiveConsumers());
                metricRow[3] = Integer.toString(latestDest.getFailoverConsumers());
                metricRow[4] = Integer.toString(latestDest.getHWFailoverConsumers());
                metricRow[5] = Integer.toString(latestDest.getAvgFailoverConsumers());
                metricRow[6] = Integer.toString(latestDest.getCurrentMessages());
                metricRow[7] = Integer.toString(latestDest.getHighWaterMessages());
                metricRow[8] = Integer.toString(latestDest.getAverageMessages());

                bcp.add(metricRow);
            } else  {
                metricRow = new String[6];

                metricRow[0] = Integer.toString(latestDest.getActiveConsumers());
                metricRow[1] = Integer.toString(latestDest.getHWActiveConsumers());
                metricRow[2] = Integer.toString(latestDest.getAvgActiveConsumers());
                metricRow[3] = Integer.toString(latestDest.getCurrentMessages());
                metricRow[4] = Integer.toString(latestDest.getHighWaterMessages());
                metricRow[5] = Integer.toString(latestDest.getAverageMessages());

                bcp.add(metricRow);
            }
        } else if (metricType == METRICS_DISK) {
            metricRow = new String[3];

            metricRow[0] = Long.toString(latestDest.getDiskReserved());
            metricRow[1] = Long.toString(latestDest.getDiskUsed());
            metricRow[2] = Integer.toString(latestDest.getDiskUtilizationRatio());

            bcp.add(metricRow);
        } else if (metricType == METRICS_REMOVE) {
            metricRow = new String[3];

            /*
            metricRow[0] = Long.toString(latestDest.getMsgsExpired());
            metricRow[1] = Long.toString(latestDest.getMsgsDiscarded());
            metricRow[2] = Long.toString(latestDest.getMsgsPurged());
             */
            metricRow[0] = "0";
            metricRow[1] = "0";
            metricRow[2] = "0";

            bcp.add(metricRow);
        }

    }

    private int runReload(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin		broker;
        BrokerCmdPrinter	bcp;
        String			commandArg;
        String			titleRow[];
        long			sleepTime;

        broker = init();

        if (broker == null)  {
//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RESTART_BKR_FAIL)).append("\n");
            return (1);
        }

        boolean force = brokerCmdProps.forceModeSet();
        if (!force)
            broker = promptForAuthentication(broker);

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RELOAD_CLS));
sb.append(ar.getString(ar.I_JMQCMD_RELOAD_CLS)).append("\n");
        printBrokerInfo(broker);

        try {
            connectToBroker(broker);

            broker.sendReloadClusterMessage();
            broker.receiveReloadClusterReplyMessage();

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_RELOAD_CLS_SUC));
sb.append(ar.getString(ar.I_JMQCMD_RELOAD_CLS_SUC)).append("\n");

        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);

//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_RELOAD_CLS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_RELOAD_CLS_FAIL)).append("\n");
            return (1);
        }

        return (0);
    }

    private int runCommit(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin		broker;
        BrokerCmdPrinter	bcp;
        String			commandArg;
        String			titleRow[];
        String			tidStr;
        Long			tid = null;
        String 			yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        broker = init();

        if (broker == null)  {
//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMMIT_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMMIT_TXN_FAIL)).append("\n");
            return (1);
        }

        boolean force = brokerCmdProps.forceModeSet();
        if (!force)
            broker = promptForAuthentication(broker);

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMMIT_TXN));
sb.append(ar.getString(ar.I_JMQCMD_COMMIT_TXN)).append("\n");
        printTransactionInfo();

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
        printBrokerInfo(broker);


        tidStr = brokerCmdProps.getTargetName();

        try  {
            tid = Long.valueOf(tidStr);
        } catch (NumberFormatException nfe)  {
//            Globals.stdErrPrintln(ar.getString(ar.E_INVALID_TXN_ID, tidStr));
sb.append(ar.getString(ar.E_INVALID_TXN_ID, tidStr)).append("\n");
//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMMIT_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMMIT_TXN_FAIL)).append("\n");
            return (1);
        }

        try {
            connectToBroker(broker);

            /*
             *  Prompt user for confirmation.
             */
            String input = null;
            if (!force) {
                input = getUserInput(ar.getString(ar.Q_COMMIT_TXN_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendCommitTxnMessage(tid);
                    broker.receiveCommitTxnReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMMIT_TXN_SUC));
sb.append(ar.getString(ar.I_JMQCMD_COMMIT_TXN_SUC)).append("\n");
                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMMIT_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMMIT_TXN_FAIL)).append("\n");
                    return (1);
                }
            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMMIT_TXN_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_COMMIT_TXN_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMMIT_TXN_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_COMMIT_TXN_NOOP)).append("\n");
                return (1);
            }

        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);

//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMMIT_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMMIT_TXN_FAIL)).append("\n");
            return (1);
        }

        return (0);
    }

    private int runRollback(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin		broker;
        BrokerCmdPrinter	bcp;
        String			commandArg;
        String			titleRow[];
        String			tidStr;
        Long			tid = null;
        String 			yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        broker = init();

        if (broker == null)  {
//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL)).append("\n");
            return (1);
        }

        boolean force = brokerCmdProps.forceModeSet();
        if (!force)
            broker = promptForAuthentication(broker);

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN)).append("\n");
        printTransactionInfo();

//        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
        printBrokerInfo(broker);

        tidStr = brokerCmdProps.getTargetName();

        try  {
            tid = Long.valueOf(tidStr);
        } catch (NumberFormatException nfe)  {
//            Globals.stdErrPrintln(ar.getString(ar.E_INVALID_TXN_ID, tidStr));
sb.append(ar.getString(ar.E_INVALID_TXN_ID, tidStr)).append("\n");
//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL)).append("\n");
            return (1);
        }

        try {
            Hashtable txnInfo = null;

            connectToBroker(broker);

            /*
             * Obtain/query transaction info to check it's state.
             */
            broker.sendGetTxnsMessage(tid);
            Vector txns = broker.receiveGetTxnsReplyMessage();

            if ((txns != null) && (txns.size() == 1)) {
                Enumeration thisEnum = txns.elements();
                txnInfo = (Hashtable)thisEnum.nextElement();

                if (brokerCmdProps.debugModeSet())  {
                    printAllTxnAttrs(txnInfo);
                }
            } else  {
                // Should not get here, since if something went wrong we should get
                // a BrokerAdminException
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET));
sb.append(ar.getString(ar.I_JMQCMD_INCORRECT_DATA_RET)).append("\n");
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL)).append("\n");
                return (1);
            }

            /*
             * Get the transaction's state.
             */
            Integer tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_STATE);
            String txnState = getTxnStateString(tmpInt);

            /*
             *  Prompt user for confirmation. Show transaction's state.
             */
            String input = null;
            if (!force) {
                input = getUserInput(ar.getString(ar.Q_ROLLBACK_TXN_OK, txnState), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendRollbackTxnMessage(tid);
                    broker.receiveRollbackTxnReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_SUC));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_SUC)).append("\n");
                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL)).append("\n");
                    return (1);
                }
            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_NOOP)).append("\n");
                return (1);
            }
        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);

//            Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_ROLLBACK_TXN_FAIL)).append("\n");
            return (1);
        }

        return (0);
    }

    private int runCompact(BrokerCmdProperties brokerCmdProps, StringBuffer sb)  {
        BrokerAdmin     broker;
        String          destName;
        int             destTypeMask;
        String		input = null;
        String 		yes, yesShort, no, noShort;
        boolean		compactAll = true;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        String commandArg = brokerCmdProps.getCommandArg();
        boolean force = brokerCmdProps.forceModeSet();

        broker = init();

        if (CMDARG_DESTINATION.equals(commandArg)) {
            destName = brokerCmdProps.getTargetName();
            destTypeMask = getDestTypeMask(brokerCmdProps);

            if (destName != null)  {
                compactAll = false;
            }

            if (broker == null)  {
                if (compactAll)  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_FAIL)).append("\n");
                } else  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DST_FAIL)).append("\n");
                }
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);


            if (compactAll)  {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DSTS));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DSTS)).append("\n");
                printBrokerInfo(broker);
            } else  {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DST));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DST)).append("\n");
                printDestinationInfo();

//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_SPECIFY_BKR));
sb.append(ar.getString(ar.I_JMQCMD_SPECIFY_BKR)).append("\n");
                printBrokerInfo(broker);
            }

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

                if (compactAll)  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_FAIL)).append("\n");
                } else  {
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DST_FAIL)).append("\n");
                }
                return (1);
            }

            if (!force) {
                if (compactAll)  {
                    input = getUserInput(ar.getString(ar.Q_COMPACT_DSTS_OK), noShort);
                } else  {
                    input = getUserInput(ar.getString(ar.Q_COMPACT_DST_OK), noShort);
                }
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input)
            || yes.equalsIgnoreCase(input)
            || force) {
                try  {
                    broker.sendCompactDestinationMessage(destName, destTypeMask);
                    broker.receiveCompactDestinationReplyMessage();

                    if (compactAll)  {
//                        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_SUC));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_SUC)).append("\n");
                    } else  {
//                        Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DST_SUC));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DST_SUC)).append("\n");
                    }

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

                    if (compactAll)  {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_FAIL)).append("\n");
                    } else  {
//                        Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DST_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DST_FAIL)).append("\n");
                    }
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
                if (compactAll)  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_NOOP)).append("\n");
                } else  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DST_NOOP)).append("\n");
                }
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
                if (compactAll)  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DSTS_NOOP)).append("\n");
                } else  {
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_COMPACT_DST_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_COMPACT_DST_NOOP)).append("\n");
                }
                return (1);
            }
        }

        broker.close();

        return (0);
    }

    private int runQuiesce(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin 	broker;
        String		input = null;
        String		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);


        broker = init();

        boolean force = brokerCmdProps.forceModeSet();

        // Check for the target argument
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_BROKER.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUIESCE_BKR));
sb.append(ar.getString(ar.I_JMQCMD_QUIESCE_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_QUIESCE_BKR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendQuiesceMessage();
                    broker.receiveQuiesceReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_QUIESCE_BKR_NOOP)).append("\n");
                return (1);
            }

        }

        broker.close();

        return (0);
    }

    private int runUnquiesce(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin 	broker;
        String		input = null;
        String		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);


        broker = init();

        boolean force = brokerCmdProps.forceModeSet();

        // Check for the target argument
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_BROKER.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR));
sb.append(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR)).append("\n");
            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force) {
                input = getUserInput(ar.getString(ar.Q_UNQUIESCE_BKR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendUnquiesceMessage();
                    broker.receiveUnquiesceReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_UNQUIESCE_BKR_NOOP)).append("\n");
                return (1);
            }

        }

        broker.close();

        return (0);
    }

    private int runTakeover(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin 	broker;
        String		input = null;
        String		yes, yesShort, no, noShort;

        yes = ar.getString(ar.Q_RESPONSE_YES);
        yesShort = ar.getString(ar.Q_RESPONSE_YES_SHORT);
        no = ar.getString(ar.Q_RESPONSE_NO);
        noShort = ar.getString(ar.Q_RESPONSE_NO_SHORT);

        broker = init();

        boolean force = brokerCmdProps.forceModeSet();

        // Check for the target argument
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_BROKER.equals(commandArg)) {
            String brokerID = brokerCmdProps.getTargetName();

            if (broker == null)  {
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL)).append("\n");
                return (1);
            }

            if (!force)
                broker = promptForAuthentication(broker);

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR)).append("\n");

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_BKR_PERFORMING_TAKEOVER));
sb.append(ar.getString(ar.I_JMQCMD_BKR_PERFORMING_TAKEOVER)).append("\n");

            printBrokerInfo(broker);

            try {
                connectToBroker(broker);

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL)).append("\n");
                return (1);
            }

            try {
                /*
                 * Get broker props to find out if broker is in HA cluster and
                 * broker cluster ID
                 */
                broker.sendGetBrokerPropsMessage();
                Properties bkrProps = broker.receiveGetBrokerPropsReplyMessage();

                /*
                 * Check if cluster is HA or not
                 */
                String value = bkrProps.getProperty(PROP_NAME_BKR_CLS_HA);
                if (!Boolean.valueOf(value).booleanValue())  {
//                    Globals.stdErrPrintln(ar.getString(ar.E_BROKER_NOT_HA));
sb.append(ar.getString(ar.E_BROKER_NOT_HA)).append("\n");
//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL)).append("\n");
                    return (1);
                }
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_QUERY_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_QUERY_BKR_FAIL)).append("\n");
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL)).append("\n");
                return (1);
            }

//            Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_BKR_STORE_TAKEOVER));
sb.append(ar.getString(ar.I_JMQCMD_BKR_STORE_TAKEOVER)).append("\n");

            BrokerCmdPrinter bcp = new BrokerCmdPrinter(5, 3, "-");
            String[] row = new String[5];

            bcp.setSortNeeded(false);
            bcp.setTitleAlign(BrokerCmdPrinter.CENTER);

            int i = 0;
            row[i++] = "";
            row[i++] = "";
            row[i++] = "";
            row[i++] = "";
            row[i++] = ar.getString(ar.I_JMQCMD_CLS_TIME_SINCE_TIMESTAMP1);

            bcp.addTitle(row);

            i = 0;
            row[i++] = ar.getString(ar.I_JMQCMD_CLS_BROKER_ID);
            row[i++] = ar.getString(ar.I_JMQCMD_CLS_ADDRESS);
            row[i++] = ar.getString(ar.I_JMQCMD_CLS_BROKER_STATE);
            row[i++] = ar.getString(ar.I_JMQCMD_CLS_NUM_MSGS);
            row[i++] = ar.getString(ar.I_JMQCMD_CLS_TIME_SINCE_TIMESTAMP2);

            bcp.addTitle(row);

            /*
             * Get state of each broker in cluster
             */
            Vector bkrList = null;
            try  {
                broker.sendGetClusterMessage(true);
                bkrList = broker.receiveGetClusterReplyMessage();
            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);

//                Globals.stdErrPrintln(ar.getString(ar.E_FAILED_TO_OBTAIN_CLUSTER_INFO));
sb.append(ar.getString(ar.E_FAILED_TO_OBTAIN_CLUSTER_INFO)).append("\n");
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL)).append("\n");
                return (1);
            }

            String brokerIDFromList = null;
            boolean found = false;

            Enumeration thisEnum = bkrList.elements();
            while (thisEnum.hasMoreElements()) {
                Hashtable bkrClsInfo = (Hashtable)thisEnum.nextElement();
                Long tmpLong;
                Integer tmpInt;
                long idle;

                brokerIDFromList = (String)bkrClsInfo.get(BrokerClusterInfo.ID);

                if ((brokerIDFromList == null) ||
                        (!brokerIDFromList.equals(brokerID)))  {
                    continue;
                }

                found = true;

                i = 0;

                row[i++] = checkNullAndPrint(brokerIDFromList);

                row[i++] = checkNullAndPrint(
                        bkrClsInfo.get(BrokerClusterInfo.ADDRESS));

                tmpInt = (Integer)bkrClsInfo.get(BrokerClusterInfo.STATE);
                if (tmpInt != null)  {
                    row[i++] = BrokerState.toString(tmpInt.intValue());
                } else  {
                    row[i++] = "";
                }

                tmpLong = (Long)bkrClsInfo.get(BrokerClusterInfo.NUM_MSGS);
                row[i++] = checkNullAndPrint(tmpLong);

                tmpLong = (Long)bkrClsInfo.get(
                        BrokerClusterInfo.STATUS_TIMESTAMP);
                if (tmpLong != null)  {
                    idle = System.currentTimeMillis() - tmpLong.longValue();
                    row[i++] = getTimeString(idle);
                } else  {
                    row[i++] = "";
                }

                bcp.add(row);

                /*
                 * Only need to display info on the one desired broker
                 */
                break;
            }

            if (!found)  {
//                Globals.stdErrPrintln(ar.getString(ar.E_CANNOT_FIND_BROKERID, brokerID));
sb.append(ar.getString(ar.E_CANNOT_FIND_BROKERID, brokerID)).append("\n");
//                Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL)).append("\n");
                return (1);
            }

            bcp.println();


            if (!force) {
                input = getUserInput(ar.getString(ar.Q_TAKEOVER_BKR_OK), noShort);
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
            }

            if (yesShort.equalsIgnoreCase(input) || yes.equalsIgnoreCase(input) || force) {
                try  {
                    broker.sendTakeoverMessage(brokerID);
                    broker.receiveTakeoverReplyMessage();
//                    Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_SUC));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_SUC)).append("\n");

                } catch (BrokerAdminException bae)  {
                    handleBrokerAdminException(bae);

//                    Globals.stdErrPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_FAIL)).append("\n");
                    return (1);
                }

            } else if (noShort.equalsIgnoreCase(input) || no.equalsIgnoreCase(input)) {
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_NOOP)).append("\n");
                return (0);

            } else {
//                Globals.stdOutPrintln(ar.getString(ar.I_UNRECOGNIZED_RES, input));
sb.append(ar.getString(ar.I_UNRECOGNIZED_RES, input)).append("\n");
//                Globals.stdOutPrintln("");
sb.append("").append("\n");
//                Globals.stdOutPrintln(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_NOOP));
sb.append(ar.getString(ar.I_JMQCMD_TAKEOVER_BKR_NOOP)).append("\n");
                return (1);
            }

        }

        broker.close();

        return (0);
    }



    private int runExists(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin broker;
        int retValue = 1;

        broker = init();

        // Check for the target argument.
        // Valid value is dst only.
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_DESTINATION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdOutPrintln("Problems connecting to the broker.");
sb.append("Problems connecting to the broker.").append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String destName = brokerCmdProps.getTargetName();
            int destTypeMask = getDestTypeMask(brokerCmdProps);

            try {
                connectToBroker(broker);

                broker.sendGetDestinationsMessage(destName, destTypeMask);
                Vector dest = broker.receiveGetDestinationsReplyMessage();

                if ((dest != null) && (dest.size() == 1)) {
//                    Globals.stdOutPrintln(Boolean.TRUE.toString());
sb.append(Boolean.TRUE.toString()).append("\n");
                    retValue = 0;

                } else {
                    // Should not get here, since if something went wrong we should get
                    // a BrokerAdminException
//                    Globals.stdErrPrintln("Problems retrieving the destination info.");
sb.append("Problems retrieving the destination info.").append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae) {
                // com.sun.messaging.jmq.io.Status.java: 404 ==  not found
                if (bae.getReplyStatus() == 404) {
//                    Globals.stdOutPrintln(Boolean.FALSE.toString());
sb.append(Boolean.FALSE.toString()).append("\n");
                    retValue = 0;
                } else {
                    handleBrokerAdminException(bae);
                    return (1);
                }
            }
        }
        return (retValue);
    }

    private int runGetAttr(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin broker;
        int retValue = 1;

        broker = init();

        // Check for the target argument.
        // Valid value are dst, svc, and bkr.
        String commandArg = brokerCmdProps.getCommandArg();

        if (CMDARG_DESTINATION.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdOutPrintln("Problems connecting to the broker.");
sb.append("Problems connecting to the broker.").append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String destName = brokerCmdProps.getTargetName();
            int destTypeMask = getDestTypeMask(brokerCmdProps);
            String attrName = brokerCmdProps.getSingleTargetAttr();

            try {
                connectToBroker(broker);

                broker.sendGetDestinationsMessage(destName, destTypeMask);
                Vector dest = broker.receiveGetDestinationsReplyMessage();

                if ((dest != null) && (dest.size() == 1)) {
                    Enumeration thisEnum = dest.elements();
                    DestinationInfo dInfo = (DestinationInfo)thisEnum.nextElement();

                    if (PROP_NAME_OPTION_MAX_MESG_BYTE.equals(attrName)) {
//                        Globals.stdOutPrintln(Long.toString(dInfo.maxMessageBytes));
sb.append(Long.toString(dInfo.maxMessageBytes)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_OPTION_MAX_MESG.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(dInfo.maxMessages));
sb.append(Integer.toString(dInfo.maxMessages)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_OPTION_MAX_PER_MESG_SIZE.equals(attrName)) {
//                        Globals.stdOutPrintln(Long.toString(dInfo.maxMessageSize));
sb.append(Long.toString(dInfo.maxMessageSize)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_OPTION_CUR_MESG_BYTE.equals(attrName)) {
//                        Globals.stdOutPrintln(Long.toString(dInfo.nMessageBytes));
sb.append(Long.toString(dInfo.nMessageBytes)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_OPTION_CUR_MESG.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(dInfo.nMessages));
sb.append(Integer.toString(dInfo.nMessages)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_OPTION_CUR_PRODUCERS.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(dInfo.nProducers));
sb.append(Integer.toString(dInfo.nProducers)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_MAX_FAILOVER_CONSUMER_COUNT.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(dInfo.maxFailoverConsumers));
sb.append(Integer.toString(dInfo.maxFailoverConsumers)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_MAX_ACTIVE_CONSUMER_COUNT.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(dInfo.maxActiveConsumers));
sb.append(Integer.toString(dInfo.maxActiveConsumers)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_IS_LOCAL_DEST.equals(attrName)) {
                        if (dInfo.isDestinationLocal())  {
//                            Globals.stdOutPrintln(Boolean.TRUE.toString());
sb.append(Boolean.TRUE.toString()).append("\n");
                        } else  {
//                            Globals.stdOutPrintln(Boolean.FALSE.toString());
sb.append(Boolean.FALSE.toString()).append("\n");
                        }
                        retValue = 0;

                    } else if (PROP_NAME_LIMIT_BEHAVIOUR.equals(attrName)) {
//                        Globals.stdOutPrintln(DestLimitBehavior.getString(dInfo.destLimitBehavior));
sb.append(DestLimitBehavior.getString(dInfo.destLimitBehavior)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_LOCAL_DELIVERY_PREF.equals(attrName)) {
                        int cdp = dInfo.destCDP;

                        if (cdp == ClusterDeliveryPolicy.LOCAL_PREFERRED)  {
//                            Globals.stdOutPrintln(Boolean.TRUE.toString());
sb.append(Boolean.TRUE.toString()).append("\n");
                        } else  {
//                            Globals.stdOutPrintln(Boolean.FALSE.toString());
sb.append(Boolean.FALSE.toString()).append("\n");
                        }
                        retValue = 0;

                    } else if (PROP_NAME_CONSUMER_FLOW_LIMIT.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(dInfo.maxPrefetch));
sb.append(Integer.toString(dInfo.maxPrefetch)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_MAX_PRODUCERS.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(dInfo.maxProducers));
sb.append(Integer.toString(dInfo.maxProducers)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_OPTION_CUR_A_CONSUMERS.equals(attrName)) {
                        if (DestType.isQueue(destTypeMask)) {
//                            Globals.stdOutPrintln(Integer.toString(dInfo.naConsumers));
sb.append(Integer.toString(dInfo.naConsumers)).append("\n");
                        } else  {
//                            Globals.stdOutPrintln(Integer.toString(dInfo.nConsumers));
sb.append(Integer.toString(dInfo.nConsumers)).append("\n");
                        }
                        retValue = 0;

                    } else if (PROP_NAME_OPTION_CUR_B_CONSUMERS.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(dInfo.nfConsumers));
sb.append(Integer.toString(dInfo.nfConsumers)).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_USE_DMQ.equals(attrName)) {
//                        Globals.stdOutPrintln(Boolean.toString(dInfo.useDMQ()));
sb.append(Boolean.toString(dInfo.useDMQ())).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_VALIDATE_XML_SCHEMA_ENABLED.equals(attrName)) {
//                        Globals.stdOutPrintln(Boolean.toString(dInfo.validateXMLSchemaEnabled()));
sb.append(Boolean.toString(dInfo.validateXMLSchemaEnabled())).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_XML_SCHEMA_URI_LIST.equals(attrName)) {
//                        Globals.stdOutPrintln(dInfo.XMLSchemaUriList);
sb.append(dInfo.XMLSchemaUriList).append("\n");
                        retValue = 0;

                    } else if (PROP_NAME_RELOAD_XML_SCHEMA_ON_FAILURE.equals(attrName)) {
//                        Globals.stdOutPrintln(Boolean.toString(dInfo.reloadXMLSchemaOnFailure()));
sb.append(Boolean.toString(dInfo.reloadXMLSchemaOnFailure())).append("\n");
                        retValue = 0;

                    } else {
                        // Should not get here since we check for valid attribute
                        // names in BrokerCmd.checkGetAttr().
//                        Globals.stdErrPrintln(attrName + " is not recognized.");
sb.append(attrName + " is not recognized.").append("\n");
                        return (1);
                    }
                } else {
//                    Globals.stdErrPrintln("Problems retrieving the destination info.");
sb.append("Problems retrieving the destination info.").append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae) {
                handleBrokerAdminException(bae);
                return (1);
            }
            return (retValue);

        } else if (CMDARG_SERVICE.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdOutPrintln("Problems connecting to the broker.");
sb.append("Problems connecting to the broker.").append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String svcName = brokerCmdProps.getTargetName();
            String attrName = brokerCmdProps.getSingleTargetAttr();

            try  {
                connectToBroker(broker);

                broker.sendGetServicesMessage(svcName);
                Vector svc = broker.receiveGetServicesReplyMessage();

                if ((svc != null) && (svc.size() == 1)) {
                    Enumeration thisEnum = svc.elements();
                    ServiceInfo sInfo = (ServiceInfo)thisEnum.nextElement();

                    if (BrokerCmdOptions.PROP_NAME_SVC_PORT.equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(sInfo.port));
sb.append(Integer.toString(sInfo.port)).append("\n");
                        retValue = 0;

                    } else if (BrokerCmdOptions.PROP_NAME_SVC_MIN_THREADS.
                            equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(sInfo.minThreads));
sb.append(Integer.toString(sInfo.minThreads)).append("\n");
                        retValue = 0;

                    } else if (BrokerCmdOptions.PROP_NAME_SVC_MAX_THREADS.
                            equals(attrName)) {
//                        Globals.stdOutPrintln(Integer.toString(sInfo.maxThreads));
sb.append(Integer.toString(sInfo.maxThreads)).append("\n");
                        retValue = 0;

                    } else {
                        // Should not get here since we check for valid attribute
                        // names in BrokerCmd.checkGetAttr().
//                        Globals.stdOutPrintln(attrName + " is not recognized.");
sb.append(attrName + " is not recognized.").append("\n");
                        return (1);
                    }
                } else {
//                    Globals.stdOutPrintln("Problems retrieving the service info.");
sb.append("Problems retrieving the service info.").append("\n");
                    return (1);
                }

            } catch (BrokerAdminException bae) {
                handleBrokerAdminException(bae);
                return (1);
            }
            return (retValue);

        } else if (CMDARG_BROKER.equals(commandArg)) {

            if (broker == null)  {
//                Globals.stdOutPrintln("Problems connecting to the broker.");
sb.append("Problems connecting to the broker.").append("\n");
                return (1);
            }

            boolean force = brokerCmdProps.forceModeSet();
            if (!force)
                broker = promptForAuthentication(broker);

            String attrName = brokerCmdProps.getSingleTargetAttr();

            try  {
                connectToBroker(broker);

                broker.sendGetBrokerPropsMessage();
                Properties bkrProps = broker.receiveGetBrokerPropsReplyMessage();

                if (bkrProps == null) {
//                    Globals.stdOutPrintln("Problems retrieving the broker info.");
sb.append("Problems retrieving the broker info.").append("\n");
                    return (1);
                }

                String value;

                value = bkrProps.getProperty(attrName, "");
//                Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                retValue = 0;

                /*
                if (PROP_NAME_BKR_PRIMARY_PORT.equals(attrName)) {
                    value = bkrProps.getProperty(PROP_NAME_BKR_PRIMARY_PORT, "");
//                    Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                    retValue = 0;

                } else if (PROP_NAME_BKR_AUTOCREATE_TOPIC.equals(attrName)) {
                    value = bkrProps.getProperty(PROP_NAME_BKR_AUTOCREATE_TOPIC, "");
//                    Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                    retValue = 0;

                } else if (PROP_NAME_BKR_AUTOCREATE_QUEUE.equals(attrName)) {
                    value = bkrProps.getProperty(PROP_NAME_BKR_AUTOCREATE_QUEUE, "");
//                    Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                    retValue = 0;

                } else if (PROP_NAME_BKR_MAX_MSG.equals(attrName)) {
                    value = bkrProps.getProperty(PROP_NAME_BKR_MAX_MSG, "");
//                    Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                    retValue = 0;

                } else if (PROP_NAME_BKR_MAX_TTL_MSG_BYTES.equals(attrName)) {
                    value = bkrProps.getProperty(PROP_NAME_BKR_MAX_TTL_MSG_BYTES, "");
//                    Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                    retValue = 0;

                } else if (PROP_NAME_BKR_MAX_MSG_BYTES.equals(attrName)) {
                    value = bkrProps.getProperty(PROP_NAME_BKR_MAX_MSG_BYTES, "");
//                    Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                    retValue = 0;

                } else if (PROP_NAME_BKR_CUR_MSG.equals(attrName)) {
                    value = bkrProps.getProperty(PROP_NAME_BKR_CUR_MSG, "");
//                    Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                    retValue = 0;

                } else if (PROP_NAME_BKR_CUR_TTL_MSG_BYTES.equals(attrName)) {
                    value = bkrProps.getProperty(PROP_NAME_BKR_CUR_TTL_MSG_BYTES, "");
//                    Globals.stdOutPrintln(value);
sb.append(value).append("\n");
                    retValue = 0;

                } else {
                    // Should not get here since we check for valid attribute
                    // names in BrokerCmd.checkGetAttr().
//                    Globals.stdOutPrintln(attrName + " is not recognized.");
sb.append(attrName + " is not recognized.").append("\n");
                    return (1);
                }
                 */

            } catch (BrokerAdminException bae)  {
                handleBrokerAdminException(bae);
                return (1);
            }
        }
        return (retValue);
    }

    private int runUngracefulKill(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin broker;

        broker = init();

        if (broker == null)  {
//            Globals.stdOutPrintln("Problems connecting to the broker.");
sb.append("Problems connecting to the broker.").append("\n");
            return (1);
        }

        boolean force = brokerCmdProps.forceModeSet();
        if (!force)
            broker = promptForAuthentication(broker);

        try {
            connectToBroker(broker);
            broker.sendShutdownMessage(false, true);
//            Globals.stdOutPrintln("Ungracefully shutdown the broker.");
sb.append("Ungracefully shutdown the broker.").append("\n");
            return (0);

        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);
            return (1);
        }
    }

    private int runDebug(BrokerCmdProperties brokerCmdProps, StringBuffer sb) {
        BrokerAdmin broker;
        BrokerCmdPrinter bcp;
        Hashtable	debugHash = null;
        String		cmd, cmdarg, target;
        String		targetType;
        Properties	optionalProps = null;

        broker = init();

        if (broker == null)  {
//            Globals.stdOutPrintln("Problems connecting to the broker.");
sb.append("Problems connecting to the broker.").append("\n");
            return (1);
        }

        boolean force = brokerCmdProps.forceModeSet();
        if (!force)
            broker = promptForAuthentication(broker);

        cmd = brokerCmdProps.getCommand();
        cmdarg = brokerCmdProps.getCommandArg();
        target = brokerCmdProps.getTargetName();
        /*
         * The -t option is used to specify target type
         */
        targetType = brokerCmdProps.getDestType();
        optionalProps = brokerCmdProps.getTargetAttrs();

//        Globals.stdOutPrintln("Sending the following DEBUG message:");
sb.append("Sending the following DEBUG message:").append("\n");

        bcp = new BrokerCmdPrinter(2, 4, "-", BrokerCmdPrinter.LEFT, false);
        String[] row = new String[2];
        row[0] = "Header Property Name";
        row[1] = "Value";
        bcp.addTitle(row);
        row[0] = MessageType.JMQ_CMD;
        row[1] = cmd;
        bcp.add(row);
        row[0] = MessageType.JMQ_CMDARG;
        row[1] = cmdarg;
        bcp.add(row);
        if (target != null)  {
            row[0] = MessageType.JMQ_TARGET;
            row[1] = target;
            bcp.add(row);
        }
        if (targetType != null)  {
            row[0] = MessageType.JMQ_TARGET_TYPE;
            row[1] = targetType;
            bcp.add(row);
        }
        bcp.println();

        if ((optionalProps != null) && (optionalProps.size() > 0))  {
//            Globals.stdOutPrintln("Optional properties:");
sb.append("Optional properties:").append("\n");
            printAttrs(optionalProps, true);
        }

//        Globals.stdOutPrintln("To the broker specified by:");
sb.append("To the broker specified by:").append("\n");
        printBrokerInfo(broker);

        try {
            connectToBroker(broker);
            broker.sendDebugMessage(cmd, cmdarg, target, targetType, optionalProps);
            debugHash = broker.receiveDebugReplyMessage();

            if ((debugHash != null) && (debugHash.size() > 0))  {
//                Globals.stdOutPrintln("Data received back from broker:");
sb.append("Data received back from broker:").append("\n");
                printDebugHash(debugHash);
            } else  {
//                Globals.stdOutPrintln("No additional data received back from broker.\n");
sb.append("No additional data received back from broker.\n").append("\n");
            }

//            Globals.stdOutPrintln("DEBUG message sent successfully.");
sb.append("DEBUG message sent successfully.").append("\n");

            return (0);

        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);
            return (1);
        }
    }

    private void printDebugHash(Hashtable hash)  {
        DebugPrinter dbp;

        dbp = new DebugPrinter(hash, 4);
        dbp.println();
    }

    private void printAllBrokerAttrs(Properties bkrProps)  {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4);
        String[] row = new String[2];

        for (Enumeration e = bkrProps.propertyNames() ; e.hasMoreElements() ;) {
            String curPropName = (String)e.nextElement();

            row[0] = curPropName;
            row[1] = bkrProps.getProperty(curPropName, "");
            bcp.add(row);
        }
        bcp.println();
    }

    private void printDisplayableBrokerAttrs(Properties bkrProps)  {
        BrokerCmdPrinter	bcp = new BrokerCmdPrinter(2, 4);
        String[]		row = new String[2];
        String			value;

        bcp.setSortNeeded(false);

        /*
         * Basic info - version/instance/port
         */
        row[0] = ar.getString(ar.I_BKR_VERSION_STR);
        value = bkrProps.getProperty(PROP_NAME_BKR_PRODUCT_VERSION, "");
        if (value.equals(""))  {
            value = ar.getString(ar.I_BKR_VERSION_NOT_AVAILABLE);
        }
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_BKR_INSTANCE_NAME);
        value = bkrProps.getProperty(PROP_NAME_BKR_INSTANCE_NAME, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_CLS_BROKER_ID);
        value = bkrProps.getProperty(PROP_NAME_BKR_CLS_BROKER_ID, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_PRIMARY_PORT);
        value = bkrProps.getProperty(PROP_NAME_BKR_PRIMARY_PORT, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_BKR_IS_EMBEDDED);
        value = bkrProps.getProperty(PROP_NAME_BKR_IS_EMBEDDED, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CONFIG_DATA_ROOT_DIR);
        value = bkrProps.getProperty(PROP_NAME_BKR_VARHOME, "");
        row[1] = value;
        bcp.add(row);

        /*
        row[0] = ar.getString(ar.I_JMQCMD_LICENSE);
        value = bkrProps.getProperty(PROP_NAME_BKR_LICENSE_DESC, "");
        row[1] = value;
        bcp.add(row);
         */

        row[0] = "";
        row[1] = "";
        bcp.add(row);

        /*
         * 'Current' numbers
         */
        row[0] = ar.getString(ar.I_CUR_MSGS_IN_BROKER);
        value = bkrProps.getProperty(PROP_NAME_BKR_CUR_MSG, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_CUR_BYTES_IN_BROKER);
        value = bkrProps.getProperty(PROP_NAME_BKR_CUR_TTL_MSG_BYTES, "");
        row[1] = value;
        bcp.add(row);

        row[0] = "";
        row[1] = "";
        bcp.add(row);

        /*
         * 'Current' numbers for DMQ
         * Log Dead Msgs
         */
        row[0] = ar.getString(ar.I_CUR_MSGS_IN_DMQ);
        value = bkrProps.getProperty(PROP_NAME_DMQ_CUR_MSG, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_CUR_BYTES_IN_DMQ);
        value = bkrProps.getProperty(PROP_NAME_DMQ_CUR_TTL_MSG_BYTES, "");
        row[1] = value;
        bcp.add(row);

        row[0] = "";
        row[1] = "";
        bcp.add(row);

        row[0] = ar.getString(ar.I_BKR_LOG_DEAD_MSGS);
        value = bkrProps.getProperty(PROP_NAME_BKR_LOG_DEAD_MSGS, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_BKR_DMQ_TRUNCATE_MSG_BODY);
        value = bkrProps.getProperty(PROP_NAME_BKR_DMQ_TRUNCATE_MSG_BODY, "");
        row[1] = value;
        bcp.add(row);

        row[0] = "";
        row[1] = "";
        bcp.add(row);

        /*
         * Max numbers
         */
        row[0] = ar.getString(ar.I_MAX_MSGS_IN_BROKER);
        value = bkrProps.getProperty(PROP_NAME_BKR_MAX_MSG, "");
        row[1] = checkAndPrintUnlimited(value, zeroNegOneString);
        bcp.add(row);

        row[0] = ar.getString(ar.I_MAX_BYTES_IN_BROKER);
        value = bkrProps.getProperty(PROP_NAME_BKR_MAX_TTL_MSG_BYTES, "");
        row[1] = checkAndPrintUnlimitedBytes(value, zeroNegOneLong);
        bcp.add(row);

        row[0] = ar.getString(ar.I_MAX_MSG_SIZE);
        value = bkrProps.getProperty(PROP_NAME_BKR_MAX_MSG_BYTES, "");
        row[1] = checkAndPrintUnlimitedBytes(value, zeroNegOneLong);
        bcp.add(row);

        row[0] = "";
        row[1] = "";
        bcp.add(row);

        /*
         * Autocreate props
         */
        row[0] = ar.getString(ar.I_AUTO_CREATE_QUEUES);
        value = bkrProps.getProperty(PROP_NAME_BKR_AUTOCREATE_QUEUE, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_AUTO_CREATE_TOPICS);
        value = bkrProps.getProperty(PROP_NAME_BKR_AUTOCREATE_TOPIC, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_AUTOCREATED_QUEUE_MAX_ACTIVE_CONS);
        value = bkrProps.getProperty(PROP_NAME_BKR_AUTOCREATE_QUEUE_MAX_ACTIVE_CONS,
                "");
        row[1] = checkAndPrintUnlimited(value, negOneString);
        bcp.add(row);

        row[0] = ar.getString(ar.I_AUTOCREATED_QUEUE_MAX_FAILOVER_CONS);
        value = bkrProps.getProperty(PROP_NAME_BKR_AUTOCREATE_QUEUE_MAX_BACKUP_CONS,
                "");
        row[1] = checkAndPrintUnlimited(value, negOneString);
        bcp.add(row);

        row[0] = "";
        row[1] = "";
        bcp.add(row);

        /*
         * Cluster related props
         */
        row[0] = ar.getString(ar.I_CLS_CLUSTER_ID);
        value = bkrProps.getProperty(PROP_NAME_BKR_CLS_CLUSTER_ID, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_CLS_IS_HA);
        value = bkrProps.getProperty(PROP_NAME_BKR_CLS_HA);
        row[1] = Boolean.valueOf(value).toString();
        bcp.add(row);

        row[0] = ar.getString(ar.I_CLS_ACTIVE_BROKERLIST);
        value = bkrProps.getProperty(PROP_NAME_BKR_CLS_BKRLIST_ACTIVE, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_CLS_CONFIGD_BROKERLIST);
        value = bkrProps.getProperty(PROP_NAME_BKR_CLS_BKRLIST, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_CLS_CONFIG_SERVER);
        value = bkrProps.getProperty(PROP_NAME_BKR_CLS_CFG_SVR, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_CLS_URL);
        value = bkrProps.getProperty(PROP_NAME_BKR_CLS_URL, "");
        row[1] = value;
        bcp.add(row);

        row[0] = "";
        row[1] = "";
        bcp.add(row);

        /*
         * Log related props
         */
        row[0] = ar.getString(ar.I_LOG_LEVEL);
        value = bkrProps.getProperty(PROP_NAME_BKR_LOG_LEVEL, "");
        row[1] = value;
        bcp.add(row);

        row[0] = ar.getString(ar.I_LOG_ROLLOVER_INTERVAL);
        value = bkrProps.getProperty(PROP_NAME_BKR_LOG_ROLL_INTERVAL, "");
        row[1] = checkAndPrintUnlimited(value, zeroNegOneString);
        bcp.add(row);

        row[0] = ar.getString(ar.I_LOG_ROLLOVER_SIZE);
        value = bkrProps.getProperty(PROP_NAME_BKR_LOG_ROLL_SIZE, "");
        row[1] = checkAndPrintUnlimitedBytes(value, zeroNegOneLong);
        bcp.add(row);

        bcp.println();
    }

    private void printAllTxnAttrs(Hashtable txnInfo)  {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4);
        String[] row = new String[2];
        Object	tmpObj;

        for (Enumeration e = txnInfo.keys() ; e.hasMoreElements() ;) {
            String curPropName = (String)e.nextElement();

            row[0] = curPropName;
            tmpObj = txnInfo.get(curPropName);
            row[1] = tmpObj.toString();
            bcp.add(row);
        }
        bcp.println();
    }

    private void printDisplayableTxnAttrs(Hashtable txnInfo)  {
        BrokerCmdPrinter	bcp = new BrokerCmdPrinter(2, 4);
        String[]		row = new String[2];
        Long			tmpLong;
        Integer			tmpInt;
        String			tmpStr;

        row[0] = ar.getString(ar.I_JMQCMD_TXN_ID);
        tmpLong = (Long)txnInfo.get(PROP_NAME_TXN_ID);
        row[1] = checkNullAndPrint(tmpLong);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_STATE);
        tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_STATE);
        row[1] = getTxnStateString(tmpInt);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_NUM_MSGS);
        tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_NUM_MSGS);
        row[1] = checkNullAndPrint(tmpInt);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_NUM_ACKS);
        tmpInt = (Integer)txnInfo.get(PROP_NAME_TXN_NUM_ACKS);
        row[1] = checkNullAndPrint(tmpInt);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_CLIENT_ID);
        tmpStr = (String)txnInfo.get(PROP_NAME_TXN_CLIENTID);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_TIMESTAMP);
        tmpLong = (Long)txnInfo.get(PROP_NAME_TXN_TIMESTAMP);
        row[1] = checkNullAndPrintTimestamp(tmpLong);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_CONNECTION);
        tmpStr = (String)txnInfo.get(PROP_NAME_TXN_CONNECTION);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_CONNECTION_ID);
        tmpLong = (Long)txnInfo.get(PROP_NAME_TXN_CONNECTION_ID);
        row[1] = checkNullAndPrint(tmpLong);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_USERNAME);
        tmpStr = (String)txnInfo.get(PROP_NAME_TXN_USER);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_TXN_XID);
        tmpStr = (String)txnInfo.get(PROP_NAME_TXN_XID);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        bcp.println();
    }

    private void printAllCxnAttrs(Hashtable cxnInfo)  {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4);
        String[] row = new String[2];
        Object	tmpObj;

        for (Enumeration e = cxnInfo.keys() ; e.hasMoreElements() ;) {
            String curPropName = (String)e.nextElement();

            row[0] = curPropName;
            tmpObj = cxnInfo.get(curPropName);
            row[1] = tmpObj.toString();
            bcp.add(row);
        }
        bcp.println();
    }

    private void printDisplayableCxnAttrs(Hashtable cxnInfo)  {
        BrokerCmdPrinter	bcp = new BrokerCmdPrinter(2, 4);
        String[]		row = new String[2];
        Long			tmpLong;
        Integer			tmpInt;
        String			tmpStr;

        bcp.setSortNeeded(false);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_CXN_ID);
        tmpLong = (Long)cxnInfo.get(PROP_NAME_CXN_CXN_ID);
        row[1] = checkNullAndPrint(tmpLong);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_USER);
        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_USER);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_SERVICE);
        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_SERVICE);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_NUM_PRODUCER);
        tmpInt = (Integer)cxnInfo.get(PROP_NAME_CXN_NUM_PRODUCER);
        row[1] = checkNullAndPrint(tmpInt);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_NUM_CONSUMER);
        tmpInt = (Integer)cxnInfo.get(PROP_NAME_CXN_NUM_CONSUMER);
        row[1] = checkNullAndPrint(tmpInt);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_HOST);
        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_HOST);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_PORT);
        tmpInt = (Integer)cxnInfo.get(PROP_NAME_CXN_PORT);
        row[1] = checkNullAndPrint(tmpInt);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_CLIENT_ID);
        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_CLIENT_ID);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        row[0] = ar.getString(ar.I_JMQCMD_CXN_CLIENT_PLATFORM);
        tmpStr = (String)cxnInfo.get(PROP_NAME_CXN_CLIENT_PLATFORM);
        row[1] = checkNullAndPrint(tmpStr);
        bcp.add(row);

        bcp.println();
    }

    private void printDisplayableMsgAttrs(HashMap oneMsg)  {
        BrokerCmdPrinter	bcp = new BrokerCmdPrinter(2, 4),
                titleBcp = new BrokerCmdPrinter(1, 0, "-");
        String[]		row = new String[2],
                titleRow = new String[1];
        Integer			tmpInt;

        bcp.setSortNeeded(false);

        /*
         * Message Header
         */
        titleRow[0] = "Message Header Information";
        titleBcp.addTitle(titleRow);
        titleBcp.print();

        row[0] = "Message ID";
        row[1] = checkNullAndPrint(oneMsg.get("MessageID"));
        bcp.add(row);

        row[0] = "Correlation ID";
        row[1] = checkNullAndPrint(oneMsg.get("CorrelationID"));
        bcp.add(row);

        row[0] = "Destination Name";
        row[1] = checkNullAndPrint(oneMsg.get("DestinationName"));
        bcp.add(row);

        row[0] = "Destination Type";
        tmpInt = (Integer)oneMsg.get("DestinationType");
        row[1] = BrokerAdminUtil.getDestinationType(tmpInt.intValue());
        bcp.add(row);

        row[0] = "Delivery Mode";
        tmpInt = (Integer)oneMsg.get("DeliveryMode");
        row[1] = checkNullAndPrintDeliveryMode(tmpInt);
        bcp.add(row);

        row[0] = "Priority";
        row[1] = checkNullAndPrint(oneMsg.get("Priority"));
        bcp.add(row);

        row[0] = "Redelivered";
        row[1] = checkNullAndPrint(oneMsg.get("Redelivered"));
        bcp.add(row);

        row[0] = "Timestamp";
        row[1] = checkNullAndPrintTimestamp((Long)oneMsg.get("Timestamp"));
        bcp.add(row);

        row[0] = "Type";
        row[1] = checkNullAndPrint((String)oneMsg.get("Type"));
        bcp.add(row);

        row[0] = "Expiration";
        row[1] = checkNullAndPrintTimestamp((Long)oneMsg.get("Expiration"));
        bcp.add(row);

        row[0] = "ReplyTo Destination Name";
        row[1] = checkNullAndPrint(oneMsg.get("ReplyToDestinationName"));
        bcp.add(row);

        row[0] = "ReplyTo Destination Type";
        tmpInt = (Integer)oneMsg.get("ReplyToDestinationType");
        if (tmpInt != null)  {
            row[1] = BrokerAdminUtil.getDestinationType(tmpInt.intValue());
        } else  {
            row[1] = "";
        }
        bcp.add(row);

        bcp.println();

        /*
         * Message Properties
         */
        titleBcp.clear();
        titleBcp.clearTitle();
        titleRow[0] = "Message Properties Information";
        titleBcp.addTitle(titleRow);
        titleBcp.print();

        Hashtable props = (Hashtable)oneMsg.get("MessageProperties");
        if (props != null)  {
            Enumeration keys = props.keys();
            bcp.clear();

            while (keys.hasMoreElements())  {
                String key = (String)keys.nextElement();
                Object val = (Object)props.get(key);
                row[0] = key;
                row[1] = val.toString();
                bcp.add(row);
            }

            bcp.println();
        } else  {
            Globals.stdOutPrintln("");
//sb.append("").append("\n");
        }

        /*
         * Message body
         */
        titleBcp.clear();
        titleBcp.clearTitle();
        titleRow[0] = "Message Body Information";
        titleBcp.addTitle(titleRow);
        titleBcp.print();

        bcp.clear();
        row[0] = "Body Type";
        row[1] = checkNullAndPrintMsgBodyType((Integer)oneMsg.get("MessageBodyType"), true);
        bcp.add(row);

        /*
        row[0] = "Body Content";
        row[1] = "";
        bcp.add(row);
         */

        bcp.println();
    }


    private int getDestTypeMask(BrokerCmdProperties brokerCmdProps)  {
        Properties	props = brokerCmdProps.getTargetAttrs();
        String		destType = brokerCmdProps.getDestType(),
                flavour;
        int		mask = 0;

        if ((destType == null) || destType.equals(""))  {
            return (-1);
        }

        if (destType.equals(PROP_VALUE_DEST_TYPE_TOPIC))  {
            mask = DestType.DEST_TYPE_TOPIC;
        } else if (destType.equals(PROP_VALUE_DEST_TYPE_QUEUE))  {
            mask = DestType.DEST_TYPE_QUEUE;
        }

        if ((props == null) || props.isEmpty())  {
            return (mask);
        }

        flavour = props.getProperty(PROP_NAME_QUEUE_FLAVOUR);

        if (flavour == null)  {
            return (mask);
        }

        if (flavour.equals(PROP_VALUE_QUEUE_FLAVOUR_SINGLE))  {
            mask |= DestType.DEST_FLAVOR_SINGLE;
        } else if (flavour.equals(PROP_VALUE_QUEUE_FLAVOUR_FAILOVER))  {
            mask |= DestType.DEST_FLAVOR_FAILOVER;
        } else if (flavour.equals(PROP_VALUE_QUEUE_FLAVOUR_ROUNDROBIN))  {
            mask |= DestType.DEST_FLAVOR_RROBIN;
        }

        return (mask);
    }

    private BrokerAdmin init()  {
        BrokerAdmin	broker;

        String 		brokerHostPort = brokerCmdProps.getBrokerHostPort(),
                brokerHostName = getBrokerHost(brokerHostPort),
                adminUser = brokerCmdProps.getAdminUserId(),
                adminPasswd;
        int		brokerPort = -1,
                numRetries = brokerCmdProps.getNumRetries(),
                receiveTimeout = brokerCmdProps.getReceiveTimeout();
        boolean		adminKeyUsed = brokerCmdProps.isAdminKeyUsed();
        boolean		useSSL = brokerCmdProps.useSSLTransportSet();

        if (brokerCmdProps.adminDebugModeSet())  {
            BrokerAdmin.setDebug(true);
        }

        try  {
            adminPasswd = getPasswordFromFileOrCmdLine(brokerCmdProps);

            broker = new BrokerAdmin(brokerHostPort,
                    adminUser, adminPasswd,
                    (receiveTimeout * 1000), useSSL);

            if (adminKeyUsed)  {
                broker.setAdminKeyUsed(true);
            }
            if (useSSL)  {
                broker.setSSLTransportUsed(true);
            }
            if (numRetries > 0)  {
                /*
                 * If the number of retries was specified, set it on the
                 * BrokerAdmin object.
                 */
                broker.setNumRetries(numRetries);
            }
        } catch (BrokerCmdException bce)  {
            handleBrokerCmdException(bce);

            return (null);
        } catch (BrokerAdminException bae)  {
            handleBrokerAdminException(bae);

            return (null);
        }

        broker.addAdminEventListener(this);
        return (broker);
    }

    /*
     * Returns the broker host name.
     * Returns null if not specified.
     *
     * @param brokerHostPort String in the form of host:port
     *
     * @return host value or null if not specified
     */
    private String getBrokerHost(String brokerHostPort) {
        String host = brokerHostPort;

        if (brokerHostPort == null) return (null);

        int i = brokerHostPort.indexOf(':');
        if (i >= 0)
            host = brokerHostPort.substring(0, i);

        if (host == null || host.equals("")) {
            return null;
        }
        return host;
    }

    /*
     * Returns the broker port number.
     * Return -1 if not specified.
     *
     * @param brokerHostPort String in the form of host:port
     *
     * @return port value or -1 if not specified
     *
     * @throw BrokerAdminException if port value is not valid
     */
    private int getBrokerPort(String brokerHostPort) throws BrokerAdminException {
        int port = -1;

        if (brokerHostPort == null) return (port);

        int i = brokerHostPort.indexOf(':');

        if (i >= 0) {
            try {
                port = Integer.parseInt(brokerHostPort.substring(i + 1));

            } catch (Exception e) {
                throw new BrokerAdminException(BrokerAdminException.INVALID_PORT_VALUE);
            }
        }
        return port;
    }

    private void connectToBroker(BrokerAdmin broker) throws BrokerAdminException {
        if(!broker.isConnected()) {
          broker.connect();
          broker.sendHelloMessage();
          broker.receiveHelloReplyMessage();
        }
    }

    /*
     * Prints out the appropriate error message using
//     * Globals.stdErrPrintln()
sb.append().append("\n");
     */
    private void handleBrokerAdminException(BrokerAdminException bae)  {
        Exception	e = bae.getLinkedException();
        int		type = bae.getType();

        switch (type)  {
            case BrokerAdminException.CONNECT_ERROR:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_CONNECT_ERROR,
                        bae.getBrokerHost(), bae.getBrokerPort()));
                printBrokerAdminExceptionDetails(bae);
                Globals.stdErrPrintln(ar.getString(ar.E_VERIFY_BROKER, OPTION_BROKER_HOSTPORT));
//sb.append(ar.getString(ar.E_VERIFY_BROKER, OPTION_BROKER_HOSTPORT)).append("\n");
                break;

            case BrokerAdminException.MSG_SEND_ERROR:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_MSG_SEND_ERROR));
//sb.append(ar.getString(ar.E_JMQCMD_MSG_SEND_ERROR)).append("\n");
                printBrokerAdminExceptionDetails(bae);
                break;

            case BrokerAdminException.MSG_REPLY_ERROR:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_MSG_REPLY_ERROR));
//sb.append(ar.getString(ar.E_JMQCMD_MSG_REPLY_ERROR)).append("\n");
                printBrokerAdminExceptionDetails(bae);
                break;

            case BrokerAdminException.CLOSE_ERROR:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_CLOSE_ERROR));
//sb.append(ar.getString(ar.E_JMQCMD_CLOSE_ERROR)).append("\n");
                printBrokerAdminExceptionDetails(bae);
                break;

            case BrokerAdminException.PROB_GETTING_MSG_TYPE:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_PROB_GETTING_MSG_TYPE));
//sb.append(ar.getString(ar.E_JMQCMD_PROB_GETTING_MSG_TYPE)).append("\n");
                printBrokerAdminExceptionDetails(bae);
                break;

            case BrokerAdminException.PROB_GETTING_STATUS:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_PROB_GETTING_STATUS));
//sb.append(ar.getString(ar.E_JMQCMD_PROB_GETTING_STATUS)).append("\n");
                printBrokerAdminExceptionDetails(bae);
                break;

            case BrokerAdminException.REPLY_NOT_RECEIVED:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_REPLY_NOT_RECEIVED));
//sb.append(ar.getString(ar.E_JMQCMD_REPLY_NOT_RECEIVED)).append("\n");
                printBrokerAdminExceptionDetails(bae);
                break;

            case BrokerAdminException.INVALID_OPERATION:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_INVALID_OPERATION));
//sb.append(ar.getString(ar.E_JMQCMD_INVALID_OPERATION)).append("\n");
                printBrokerAdminExceptionDetails(bae);
                break;

            case BrokerAdminException.INVALID_PORT_VALUE:
                Globals.stdErrPrintln(
                        ar.getString(ar.I_ERROR_MESG),
                        ar.getKString(ar.E_JMQCMD_INVALID_PORT_VALUE));
                break;

            case BrokerAdminException.INVALID_LOGIN:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_CONNECT_ERROR,
                        bae.getBrokerHost(), bae.getBrokerPort()));
                printBrokerAdminExceptionDetails(bae);
                Globals.stdErrPrintln(ar.getString(ar.E_INVALID_LOGIN));
//sb.append(ar.getString(ar.E_INVALID_LOGIN)).append("\n");
                break;

            case BrokerAdminException.SECURITY_PROB:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_CONNECT_ERROR,
                        bae.getBrokerHost(), bae.getBrokerPort()));
                printBrokerAdminExceptionDetails(bae);
                Globals.stdErrPrintln(ar.getString(ar.E_LOGIN_FORBIDDEN));
//sb.append(ar.getString(ar.E_LOGIN_FORBIDDEN)).append("\n");
                break;

            case BrokerAdminException.PROB_SETTING_SSL:
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_CONNECT_ERROR,
                        bae.getBrokerHost(), bae.getBrokerPort()));
                printBrokerAdminExceptionDetails(bae);
                Globals.stdErrPrintln(ar.getString(ar.E_PROB_SETTING_SSL));
//sb.append(ar.getString(ar.E_PROB_SETTING_SSL)).append("\n");
                break;

            case BrokerAdminException.BAD_ADDR_SPECIFIED:
                Globals.stdErrPrintln(
                        ar.getString(ar.I_ERROR_MESG),
                        ar.getKString(ar.E_JMQCMD_BAD_ADDRESS, bae.getBrokerAddress()));
                break;

        }
    }

    private void printBrokerAdminExceptionDetails(BrokerAdminException bae)  {
        Exception	e = bae.getLinkedException();
        String		s = bae.getBrokerErrorStr();

        if (s != null)  {
           Globals.stdErrPrintln(s);
//sb.append(s).append("\n");
        }

        if (e != null)  {
            String msg = e.getMessage(), s2 = e.toString();

            if (s2 != null)  {
                Globals.stdErrPrintln(s2);
//sb.append(s2).append("\n");
            } else if (msg != null)  {
                Globals.stdErrPrintln(msg);
//sb.append(msg).append("\n");
            }

            if (brokerCmdProps.debugModeSet())  {
                e.printStackTrace(System.err);
            }

        }
    }


    private void handleBrokerCmdException(BrokerCmdException bce)  {
        Exception	ex = bce.getLinkedException();
        //BrokerCmdProperties brokerCmdProps = bce.getProperties();
        java.util.Properties brokerCmdProps = bce.getProperties();
        int		type = bce.getType();

        switch (type)  {
            case BrokerCmdException.READ_PASSFILE_FAIL:
                Globals.stdErrPrintln(
                        ar.getString(ar.I_ERROR_MESG),
                        ar.getKString(ar.E_READ_PASSFILE_FAIL, ex));
                break;

            default:
                Globals.stdErrPrintln("Unknown exception caught: " + type);
//sb.append("Unknown exception caught: " + type).append("\n");
        }
    }


    /**
     * Return user input. Return null if an error occurred.
     */
    private String getUserInput(String question)  {
        return (getUserInput(question, null));
    }

    /**
     * Return user input. Return <defaultResponse> if no response ("") was
     * given. Return null if an error occurred.
     */
    private String getUserInput(String question, String defaultResponse)  {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            Globals.stdOutPrint(question);
            String s = in.readLine();

            if (s.equals("") && (defaultResponse != null))  {
                s = defaultResponse;
            }
            return(s);

        } catch (IOException ex) {
            Globals.stdErrPrintln(
                    ar.getString(ar.I_ERROR_MESG),
                    ar.getKString(ar.E_PROB_GETTING_USR_INPUT));
            return null;
        }
    }

    /**
     * Return the password without echoing.
     */
    private String getPassword() {

        Password pw = new Password();
        Globals.stdOutPrint(ar.getString(ar.I_JMQCMD_PASSWORD));
        return pw.getPassword();
    }

    private void printBrokerInfo(BrokerAdmin broker) {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4, "-");
        String[] row = new String[2];

        row[0] = ar.getString(ar.I_JMQCMD_BKR_HOST);
        row[1] = ar.getString(ar.I_JMQCMD_PRIMARY_PORT);
        bcp.addTitle(row);

        row[0] = broker.getBrokerHost();
        row[1] = broker.getBrokerPort();
        bcp.add(row);

        bcp.println();
    }

    private void printServiceInfo() {
        printServiceInfo(null);
    }

    private void printServiceInfo(String svcName) {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(1, 4, "-");
        String[] row = new String[1];

        row[0] = ar.getString(ar.I_JMQCMD_SVC_NAME);
        bcp.addTitle(row);

        /*
         * If servicename not provided, get value of '-n'.
         */
        if (svcName == null)  {
            row[0] = brokerCmdProps.getTargetName();
        } else  {
            row[0] = svcName;
        }
        bcp.add(row);

        bcp.println();
    }

    private void printMessageInfo() {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(1, 4, "-");
        String[] row = new String[1];
        /*
        row[0] = ar.getString(ar.I_JMQCMD_MSG_ID);
         */
        row[0] = "Message ID";
        bcp.addTitle(row);

        row[0] = brokerCmdProps.getMsgID();
        bcp.add(row);

        bcp.println();
    }

    private void printDestinationInfo() {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4, "-");
        String[] row = new String[2];
        row[0] = ar.getString(ar.I_JMQCMD_DST_NAME);
        row[1] = ar.getString(ar.I_JMQCMD_DST_TYPE);
        bcp.addTitle(row);

        row[0] = brokerCmdProps.getTargetName();
        row[1] = BrokerAdminUtil.getDestinationType(getDestTypeMask(brokerCmdProps));
        bcp.add(row);

        bcp.println();
    }

    private void printDurableSubscriptionInfo() {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(2, 4, "-");
        String[] row = new String[2];
        row[0] = ar.getString(ar.I_JMQCMD_DUR_NAME);
        row[1] = ar.getString(ar.I_JMQCMD_CLIENT_ID);
        bcp.addTitle(row);

        row[0] = brokerCmdProps.getTargetName();
        row[1] = brokerCmdProps.getClientID();
        bcp.add(row);

        bcp.println();
    }

    private void printTransactionInfo() {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(1, 4, "-");
        String[] row = new String[1];
        row[0] = ar.getString(ar.I_JMQCMD_TXN_ID);
        bcp.addTitle(row);

        row[0] = brokerCmdProps.getTargetName();
        bcp.add(row);

        bcp.println();
    }

    private void printConnectionInfo() {
        BrokerCmdPrinter bcp = new BrokerCmdPrinter(1, 4, "-");
        String[] row = new String[1];
        row[0] = ar.getString(ar.I_JMQCMD_CXN_CXN_ID);
        bcp.addTitle(row);

        row[0] = brokerCmdProps.getTargetName();
        bcp.add(row);

        bcp.println();
    }


    // Check to see if the service is an admin service.
    private void isAdminService(BrokerAdmin broker, String svcName)
    throws BrokerAdminException {

        broker.sendGetServicesMessage(svcName);
        Vector svc = broker.receiveGetServicesReplyMessage();

        if ((svc != null) && (svc.size() == 1)) {
            Enumeration thisEnum = svc.elements();
            ServiceInfo sInfo = (ServiceInfo)thisEnum.nextElement();

            if (sInfo.type == ServiceType.ADMIN)
                throw new BrokerAdminException(BrokerAdminException.INVALID_OPERATION);
        }
    }

    // Check to see if the dest type is topic.
    private void isDestTypeTopic(BrokerAdmin broker, String destName)
    throws BrokerAdminException {

        // Query the destination first to make sure it is topic.
        // First get all the destinations and check each destination's type
        // until we find the one.
        // We have to do this because 'query dst' requires both the name
        // and type of the destination.
        broker.sendGetDestinationsMessage(null, -1);
        Vector dests = broker.receiveGetDestinationsReplyMessage();

        boolean found = false;
        int i = 0;
        while ((!found) && (i < dests.size())) {
            DestinationInfo dInfo = (DestinationInfo)dests.elementAt(i);
            if ((destName.equals(dInfo.name)) &&
                    (DestType.isTopic(dInfo.type)))
                found = true;
            i++;
        }

        if (!found) {
            throw new BrokerAdminException(BrokerAdminException.INVALID_OPERATION);
        }
    }

    /*
     * Not used
    private String checkAndPrintUnlimitedInt(int value)  {
        return (checkAndPrintUnlimitedInt(value, 0));
    }
     */

    private String checkAndPrintUnlimitedInt(int value, int unlimitedValues[])  {
        String ret = null;

        for (int i = 0; i < unlimitedValues.length; ++i)  {
            if (value == unlimitedValues[i])  {
                ret = ar.getString(ar.I_UNLIMITED) + " (-1)";
                break;
            }
        }

        if (ret == null)  {
            ret = new Integer(value).toString();
        }

        return (ret);
    }

    private String checkAndPrintUnlimitedInt(int value, int unlimitedValue)  {
        String ret;

        if (value == unlimitedValue)  {
            ret = ar.getString(ar.I_UNLIMITED) + " (-1)";
        } else  {
            ret = new Integer(value).toString();
        }

        return (ret);
    }

    /*
     * Not used
    private String checkAndPrintUnlimitedLong(long value)  {
         String ret;

         if (value == 0)  {
             ret = ar.getString(ar.I_UNLIMITED) + " (-1)";
         } else  {
             ret = new Long(value).toString();
         }

         return (ret);
    }
     */

    private String checkAndPrintUnlimitedLong(long value, long unlimitedValues[])  {
        String ret = null;

        for (int i = 0; i < unlimitedValues.length; ++i)  {
            if (value == unlimitedValues[i])  {
                ret = ar.getString(ar.I_UNLIMITED) + " (-1)";
                break;
            }
        }

        if (ret == null)  {
            ret = new Long(value).toString();
        }

        return (ret);
    }


    private String checkAndPrintUnlimitedBytes(String s, long unlimitedValues[])  {
        SizeString	ss;
        String ret = null, value = s.trim();

        try {
            ss = new SizeString(value);
        } catch (Exception e)  {
            /*
             * Should not get here
             */
            return (value);
        }

        for (int i = 0; i < unlimitedValues.length; ++i)  {
            if (ss.getBytes() == unlimitedValues[i])  {
                ret = ar.getString(ar.I_UNLIMITED) + " (-1)";
                break;
            }
        }

        if (ret == null)  {
            ret = value;
        }

        return (ret);
    }

    /*
     * Not used
    private String checkAndPrintUnlimitedBytes(String s)  {
         SizeString	ss;
         String ret, value = s.trim();

         try {
            ss = new SizeString(value);
         } catch (Exception e)  {
            return (value);
         }

         if (ss.getBytes() == 0)  {
             ret = ar.getString(ar.I_UNLIMITED) + " (-1)";
         } else  {
             ret = value;
         }

         return (ret);
    }
     */

    private String checkAndPrintUnlimited(String s, String unlimitedValues[])  {
        String ret = null, value = s.trim();

        for (int i = 0; i < unlimitedValues.length; ++i)  {
            if (value.equals(unlimitedValues[i]))  {
                ret = ar.getString(ar.I_UNLIMITED) + " (-1)";
                break;
            }
        }

        if (ret == null)  {
            ret = value;
        }

        return (ret);
    }

    private String checkNullAndPrint(Object obj)  {
        if (obj != null)  {
            return (obj.toString());
        } else  {
            return ("");
        }
    }

    private String checkNullAndPrintTimestamp(Long timestamp)  {
        if (timestamp != null)  {
            String	ts;
            Date	d = new Date(timestamp.longValue());
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                    DateFormat.MEDIUM);

            ts = df.format(d);

            return (ts);
        } else  {
            return ("");
        }
    }

    /*
     * Returns an integer representing the metric type.
     * If the metric type is not specified, the totals
     * metrics is assumed.
     */
    private int getMetricType(BrokerCmdProperties brokerCmdProps)  {
        String	s = brokerCmdProps.getMetricType(),
                commandArg = brokerCmdProps.getCommandArg();

        if (s == null)  {
            return (METRICS_TOTALS);
        }

        if (s.equals(PROP_VALUE_METRICS_TOTALS))  {
            return (METRICS_TOTALS);
        } else if (s.equals(PROP_VALUE_METRICS_RATES))  {
            return (METRICS_RATES);
        } else if (s.equals(PROP_VALUE_METRICS_CONNECTIONS))  {
            return (METRICS_CONNECTIONS);
        } else if (s.equals(PROP_VALUE_METRICS_CONSUMER))  {
            return (METRICS_CONSUMER);
        } else if (s.equals(PROP_VALUE_METRICS_DISK))  {
            return (METRICS_DISK);
        } else if (s.equals(PROP_VALUE_METRICS_REMOVE))  {
            return (METRICS_REMOVE);
        }

        return (METRICS_TOTALS);
    }

    /*
     * Prompts for authentication and stores the missing username/password.
     */
    private BrokerAdmin promptForAuthentication(BrokerAdmin broker) {
        String usernameValue = broker.getUserName();
        String passwordValue = broker.getPassword();

        boolean carriageReturnNeeded = false;

        if (usernameValue == null) {
            broker.setUserName(getUserInput(ar.getString(ar.I_JMQCMD_USERNAME)));
            carriageReturnNeeded = true;
        }

        if (passwordValue == null) {
            String passwd = getPassword();
            broker.setPassword(passwd);
            carriageReturnNeeded = false;
        }

        if (carriageReturnNeeded)
            Globals.stdOutPrintln("");
//sb.append("").append("\n");

        return broker;
    }

    private boolean reconnectToBroker(BrokerAdmin broker) {

        boolean connected = false;
        int count = 0;

        while (!connected && (count < BrokerAdmin.RECONNECT_RETRIES)) {
            try {
                broker.connect();
                broker.sendHelloMessage();
                broker.receiveHelloReplyMessage();
                connected = true;

            } catch (BrokerAdminException baex) {
                // try to reconnect based on RECONNECT attributes
                if (baex.getType() == BrokerAdminException.CONNECT_ERROR) {
                    try {
                        Thread.sleep(BrokerAdmin.RECONNECT_DELAY);
                        count++;
                    } catch (InterruptedException ie) {
                        connected = false;
                    }
                } else {
                    connected = false;
                }

            } catch (Exception ex) {
                connected = false;
            }

            if (count >= BrokerAdmin.RECONNECT_RETRIES) {
                connected = false;
                Globals.stdErrPrintln(ar.getString(ar.E_JMQCMD_CONNECT_ERROR,
                        broker.getBrokerHost(), broker.getBrokerPort()));
                Globals.stdErrPrintln(ar.getString(ar.E_MAX_RECONNECT_REACHED,
                        new Long(BrokerAdmin.RECONNECT_DELAY*BrokerAdmin.RECONNECT_RETRIES / 1000)));
            }
        }
        return connected;
    }

    private static Properties convertQueueDeliveryPolicy
            (Properties targetAttrs) {

        String deliveryValue =
                targetAttrs.getProperty(PROP_NAME_BKR_QUEUE_DELIVERY_POLICY);

        if (PROP_VALUE_QUEUE_FLAVOUR_SINGLE.equals(deliveryValue)) {
            targetAttrs.setProperty(PROP_NAME_BKR_QUEUE_DELIVERY_POLICY,
                    PROP_NAME_QUEUE_FLAVOUR_SINGLE);

        } else if (PROP_VALUE_QUEUE_FLAVOUR_FAILOVER.equals(deliveryValue)) {
            targetAttrs.setProperty(PROP_NAME_BKR_QUEUE_DELIVERY_POLICY,
                    PROP_NAME_QUEUE_FLAVOUR_FAILOVER);

        } else if (PROP_VALUE_QUEUE_FLAVOUR_ROUNDROBIN.equals(deliveryValue)) {
            targetAttrs.setProperty(PROP_NAME_BKR_QUEUE_DELIVERY_POLICY,
                    PROP_NAME_QUEUE_FLAVOUR_ROUNDROBIN);

        } else {
            // Should not get here, as the value has already been validated
        }

        return targetAttrs;
    }

    private String getDisplayableQueueDeliveryPolicy(String deliveryValue) {

        if (PROP_NAME_QUEUE_FLAVOUR_SINGLE.equals(deliveryValue)) {
            return (ar.getString(ar.I_SINGLE));

        } else if (PROP_NAME_QUEUE_FLAVOUR_FAILOVER.equals(deliveryValue)) {
            return (ar.getString(ar.I_FAILOVER));

        } else if (PROP_NAME_QUEUE_FLAVOUR_ROUNDROBIN.equals(deliveryValue)) {
            return (ar.getString(ar.I_RROBIN));

        } else {
            // Should not get here, as the value has already been validated
            return (ar.getString(ar.I_UNKNOWN));
        }
    }

    private int getPauseTypeVal(String destStateStr)  {
        int ret = DestState.UNKNOWN;

        if (destStateStr == null)
            return (ret);

        if (destStateStr.equals(PROP_VALUE_PAUSETYPE_ALL))  {
            ret = DestState.PAUSED;
        } else if (destStateStr.equals(PROP_VALUE_PAUSETYPE_PRODUCERS))  {
            ret = DestState.PRODUCERS_PAUSED;
        } else if (destStateStr.equals(PROP_VALUE_PAUSETYPE_CONSUMERS))  {
            ret = DestState.CONSUMERS_PAUSED;
        }

        return (ret);
    }

    private String getResetTypeVal(String resetType)  {

        if ((resetType == null) || (resetType.equals("")))
            return (null);

        if (resetType.equals(PROP_VALUE_RESETTYPE_METRICS))  {
            return (MessageType.JMQ_METRICS);
        }

        /*
         * If "ALL" was specified, not setting reset type is OK
         * the admin protocol treats this as "ALL".
         */

        return (null);
    }

    private int getLimitBehavValue(String limitBehavStr)  {
        int ret = DestLimitBehavior.UNKNOWN;

        if (limitBehavStr == null)
            return (ret);

        if (limitBehavStr.equals(LIMIT_BEHAV_FLOW_CONTROL))  {
            ret = DestLimitBehavior.FLOW_CONTROL;
        } else if (limitBehavStr.equals(LIMIT_BEHAV_RM_OLDEST))  {
            ret = DestLimitBehavior.REMOVE_OLDEST;
        } else if (limitBehavStr.equals(LIMIT_BEHAV_REJECT_NEWEST))  {
            ret = DestLimitBehavior.REJECT_NEWEST;
        } else if (limitBehavStr.equals(LIMIT_BEHAV_RM_LOW_PRIORITY))  {
            ret = DestLimitBehavior.REMOVE_LOW_PRIORITY;
        }

        return (ret);
    }

    private int getClusterDeliveryPolicy(String cdp)  {
        int ret = ClusterDeliveryPolicy.UNKNOWN;

        if (cdp == null)
            return (ret);

        boolean b = Boolean.valueOf(cdp).booleanValue();

        if (b)  {
            ret = ClusterDeliveryPolicy.LOCAL_PREFERRED;
        } else  {
            ret = ClusterDeliveryPolicy.DISTRIBUTED;
        }

        return (ret);
    }

    /*
     * Get password from either the passfile or -p option.
     * In some future release, the -p option will go away
     * leaving the passfile the only way to specify the
     * password (besides prompting the user for it).
     * -p has higher precendence compared to -passfile.
     */
    private String getPasswordFromFileOrCmdLine(BrokerCmdProperties brokerCmdProps)
    throws BrokerCmdException  {
        String passwd = brokerCmdProps.getAdminPasswd(),
                passfile = brokerCmdProps.getAdminPassfile();

        if (passwd != null)  {
            return (passwd);
        }

        if (passfile != null)  {
            String ret = null;
            InputStream fis = null;
            try  {
                Properties props = new Properties();
                /*
                 * Read password from passfile
                 */
                fis = FileUtil.retrieveObfuscatedFile(
                        passfile);

                props.load(fis);
                ret = props.getProperty(PROP_NAME_PASSFILE_PASSWD);

                if (ret == null)  {
                    throw new RuntimeException(
                            ar.getString(ar.E_PASSFILE_PASSWD_PROPERTY_NOT_FOUND,
                            PROP_NAME_PASSFILE_PASSWD,
                            passfile));
                }
            } catch(Exception e)  {
                BrokerCmdException bce =
                        new BrokerCmdException(BrokerCmdException.READ_PASSFILE_FAIL);
                bce.setProperties(brokerCmdProps);
                bce.setLinkedException(e);

                throw (bce);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                       
                    } catch (IOException ioe) {
                        //
                    }

                    fis = null;
                }
            }
            return (ret);
        }

        return (null);
    }

    private String checkNullAndPrintDeliveryMode(Integer deliveryMode)  {
        if (deliveryMode != null)  {
            String	val;

            switch (deliveryMode.intValue())  {
                case DeliveryMode.NON_PERSISTENT:
                    val = "NON_PERSISTENT";
                    break;

                case DeliveryMode.PERSISTENT:
                    val = "PERSISTENT";
                    break;

                default:
                    val = "Unknown";
            }

            return (val + " (" + deliveryMode.intValue() + ")");
        } else  {
            return ("");
        }
    }

    private String checkNullAndPrintMsgBodyType(Integer bodyType, boolean includeValue)  {
        if (bodyType != null)  {
            String label = null;

            switch (bodyType.intValue())  {
                case 1:
                    label = "TextMessage";
                    break;

                case 2:
                    label = "BytesMessage";
                    break;

                case 3:
                    label = "MapMessage";
                    break;

                case 4:
                    label = "StreamMessage";
                    break;

                case 5:
                    label = "ObjectMessage";
                    break;

                default:
                    label = "Unknown";
            }

            if (includeValue)
                return (label + " (" + bodyType.intValue() + ")");
            return (label);
        } else  {
            return ("");
        }
    }
}

