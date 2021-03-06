package com.xc.framework.port.core;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Date：2021/3/26
 * Author：ZhangXuanChen
 * Description：串口接收缓存
 */
public class PortReceiveCache {
    private final String TAG = "PortReceiveCache";
    private final CopyOnWriteArrayList<byte[]> responseList;
    private final CopyOnWriteArrayList<byte[]> resultList;
    public static PortReceiveCache mPortReceiveCache;

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：getInstance
     */
    public static PortReceiveCache getInstance() {
        if (mPortReceiveCache == null) {
            mPortReceiveCache = new PortReceiveCache();
        }
        return mPortReceiveCache;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:10
     * Description：PortReceiveCache
     */
    public PortReceiveCache() {
        responseList = new CopyOnWriteArrayList<byte[]>();
        resultList = new CopyOnWriteArrayList<byte[]>();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResponse
     */
    public void addResponse(byte[] bytes) {
        responseList.add(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:35
     * Description：removeResponse
     */
    public void removeResponse(byte[] bytes) {
        responseList.remove(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:18
     * Description：getResponseList
     */
    public CopyOnWriteArrayList<byte[]> getResponseList() {
        return responseList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:35
     * Description：clearResponseList
     */
    public void clearResponseList() {
        responseList.clear();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResult
     */
    public void addResult(byte[] bytes) {
        resultList.add(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 11:21
     * Description：addResult
     */
    public void removeResult(byte[] bytes) {
        resultList.remove(bytes);
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:19
     * Description：getResultList
     */
    public CopyOnWriteArrayList<byte[]> getResultList() {
        return resultList;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clearResultList
     */
    public void clearResultList() {
        resultList.clear();
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:34
     * Description：clear
     */
    public void clear() {
        clearResponseList();
        clearResultList();
    }

    public byte[] getReceiveDatas(PortReceiveType receiveType, byte[] sendDatas, PortFilterCallback portFilterCallback) {
        CopyOnWriteArrayList<byte[]> receiveList = null;
        if (receiveType == PortReceiveType.Response) {//响应
            receiveList = getResponseList();
        } else if (receiveType == PortReceiveType.Result) {//结果
            receiveList = getResultList();
        }
        if (receiveList != null && !receiveList.isEmpty()) {
            for (byte[] receiveDatas : receiveList) {
                if (portFilterCallback != null ? portFilterCallback.onFilter(sendDatas, receiveDatas, receiveType) : true) {//判断指令正确性
                    return receiveDatas;
                }
            }
        }
        return null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2021/3/26 13:47
     * Description：removeReceiveDatas
     */
    public void removeReceiveDatas(PortReceiveType receiveType, byte[] receiveDatas) {
        if (receiveDatas != null && receiveDatas.length > 0) {
            if (receiveType == PortReceiveType.Response) {//响应
                removeResponse(receiveDatas);
            } else if (receiveType == PortReceiveType.Result) {//结果
                removeResult(receiveDatas);
            }
        }
    }

}
