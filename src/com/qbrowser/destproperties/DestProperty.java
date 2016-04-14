/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.qbrowser.destproperties;

/**
 *
 * @author takemura
 */
public class DestProperty {

private String XMLSchemaURIList;
private int consumerFlowLimit;
private String limitBehavior;
private boolean localDeliveryPreferred;
private long maxBytesPerMsg;
private int  maxNumActiveConsumers;
private int  maxNumBackupConsumers;
private int  maxNumMsgs;
private int  maxNumProducers;
private long maxTotalMsgBytes;
private boolean reloadXMLSchemaOnFailure;
private boolean useDMQ;
private boolean validateXMLSchemaEnabled;
private String destName;
private String destType;

    /**
     * @return the XMLSchemaURIList
     */
    public String getXMLSchemaURIList() {
        return XMLSchemaURIList;
    }

    /**
     * @param XMLSchemaURIList the XMLSchemaURIList to set
     */
    public void setXMLSchemaURIList(String XMLSchemaURIList) {
        this.XMLSchemaURIList = XMLSchemaURIList;
    }

    /**
     * @return the consumerFlowLimit
     */
    public int getConsumerFlowLimit() {
        return consumerFlowLimit;
    }

    /**
     * @param consumerFlowLimit the consumerFlowLimit to set
     */
    public void setConsumerFlowLimit(int consumerFlowLimit) {
        this.consumerFlowLimit = consumerFlowLimit;
    }

    /**
     * @return the limitBehavior
     */
    public String getLimitBehavior() {
        return limitBehavior;
    }

    /**
     * @param limitBehavior the limitBehavior to set
     */
    public void setLimitBehavior(String limitBehavior) {
        this.limitBehavior = limitBehavior;
    }

    /**
     * @return the localDeliveryPreferred
     */
    public boolean isLocalDeliveryPreferred() {
        return localDeliveryPreferred;
    }

    /**
     * @param localDeliveryPreferred the localDeliveryPreferred to set
     */
    public void setLocalDeliveryPreferred(boolean localDeliveryPreferred) {
        this.localDeliveryPreferred = localDeliveryPreferred;
    }

    /**
     * @return the maxBytesPerMsg
     */
    public long getMaxBytesPerMsg() {
        return maxBytesPerMsg;
    }

    /**
     * @param maxBytesPerMsg the maxBytesPerMsg to set
     */
    public void setMaxBytesPerMsg(long maxBytesPerMsg) {
        this.maxBytesPerMsg = maxBytesPerMsg;
    }

    /**
     * @return the maxNumActiveConsumers
     */
    public int getMaxNumActiveConsumers() {
        return maxNumActiveConsumers;
    }

    /**
     * @param maxNumActiveConsumers the maxNumActiveConsumers to set
     */
    public void setMaxNumActiveConsumers(int maxNumActiveConsumers) {
        this.maxNumActiveConsumers = maxNumActiveConsumers;
    }

    /**
     * @return the maxNumBackupConsumers
     */
    public int getMaxNumBackupConsumers() {
        return maxNumBackupConsumers;
    }

    /**
     * @param maxNumBackupConsumers the maxNumBackupConsumers to set
     */
    public void setMaxNumBackupConsumers(int maxNumBackupConsumers) {
        this.maxNumBackupConsumers = maxNumBackupConsumers;
    }

    /**
     * @return the maxNumMsgs
     */
    public int getMaxNumMsgs() {
        return maxNumMsgs;
    }

    /**
     * @param maxNumMsgs the maxNumMsgs to set
     */
    public void setMaxNumMsgs(int maxNumMsgs) {
        this.maxNumMsgs = maxNumMsgs;
    }

    /**
     * @return the maxNumProducers
     */
    public int getMaxNumProducers() {
        return maxNumProducers;
    }

    /**
     * @param maxNumProducers the maxNumProducers to set
     */
    public void setMaxNumProducers(int maxNumProducers) {
        this.maxNumProducers = maxNumProducers;
    }

    /**
     * @return the maxTotalMsgBytes
     */
    public long getMaxTotalMsgBytes() {
        return maxTotalMsgBytes;
    }

    /**
     * @param maxTotalMsgBytes the maxTotalMsgBytes to set
     */
    public void setMaxTotalMsgBytes(long maxTotalMsgBytes) {
        this.maxTotalMsgBytes = maxTotalMsgBytes;
    }

    /**
     * @return the reloadXMLSchemaOnFailure
     */
    public boolean isReloadXMLSchemaOnFailure() {
        return reloadXMLSchemaOnFailure;
    }

    /**
     * @param reloadXMLSchemaOnFailure the reloadXMLSchemaOnFailure to set
     */
    public void setReloadXMLSchemaOnFailure(boolean reloadXMLSchemaOnFailure) {
        this.reloadXMLSchemaOnFailure = reloadXMLSchemaOnFailure;
    }

    /**
     * @return the useDMQ
     */
    public boolean isUseDMQ() {
        return useDMQ;
    }

    /**
     * @param useDMQ the useDMQ to set
     */
    public void setUseDMQ(boolean useDMQ) {
        this.useDMQ = useDMQ;
    }

    /**
     * @return the validateXMLSchemaEnabled
     */
    public boolean isValidateXMLSchemaEnabled() {
        return validateXMLSchemaEnabled;
    }

    /**
     * @param validateXMLSchemaEnabled the validateXMLSchemaEnabled to set
     */
    public void setValidateXMLSchemaEnabled(boolean validateXMLSchemaEnabled) {
        this.validateXMLSchemaEnabled = validateXMLSchemaEnabled;
    }

    /**
     * @return the destName
     */
    public String getDestName() {
        return destName;
    }

    /**
     * @param destName the destName to set
     */
    public void setDestName(String destName) {
        this.destName = destName;
    }

    /**
     * @return the destType
     */
    public String getDestType() {
        return destType;
    }

    /**
     * @param destType the destType to set
     */
    public void setDestType(String destType) {
        this.destType = destType;
    }

}
