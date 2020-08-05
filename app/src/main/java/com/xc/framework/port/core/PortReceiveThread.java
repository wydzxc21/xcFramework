package com.xc.framework.port.core;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xc.framework.thread.XCThread;
import com.xc.framework.util.XCByteUtil;

import java.util.Arrays;

/**
 * Date：2020/3/10
 * Author：ZhangXuanChen
 * Description：串口接收基类
 */
public abstract class PortReceiveThread extends XCThread {
    private final String TAG = "PortReceiveThread";
    private PortParam portParam;//串口参数
    private IPort iPort;//串口工具
    //
    private byte[] bufferDatas;//缓存数据
    private int bufferPosition;//缓存索引
    private int frameHeadsType;//帧头类型，1：响应，2：请求
    private byte[] responseDatas;//接收响应数据
    private byte[] interruptDatas;//接收中断数据

    /**
     * @param portParam 串口参数
     * @param iPort     串口工具
     * @author ZhangXuanChen
     * @date 2020/3/15
     */
    public PortReceiveThread(PortParam portParam, IPort iPort) {
        this.portParam = portParam;
        this.iPort = iPort;
        this.bufferDatas = new byte[16 * 1024];
        this.bufferPosition = 0;
    }


    @Override
    protected Object onRun(Handler handler) {
        try {
            while (isRun()) {
                readDatas();
                Thread.sleep(1);
            }
        } catch (Exception e) {
            setRun(false);
        }
        return null;
    }

    @Override
    protected void onHandler(Message msg) {
        switch (msg.what) {
            case 0x123://请求
                onRequest((byte[]) msg.obj);
                break;
        }
    }

    /**
     * @author ZhangXuanChen
     * @date 2020/3/8
     * @description readDatas
     */
    private void readDatas() {
        byte[] readDatas = iPort.readPort();
        if (readDatas != null && readDatas.length > 0) {
            System.arraycopy(readDatas, 0, bufferDatas, bufferPosition, readDatas.length);
            bufferPosition += readDatas.length;
            byte[] cutDatas = Arrays.copyOf(bufferDatas, bufferPosition);
            if (portParam.getReceiveResponseFrameHeads() != null && portParam.getReceiveResponseFrameHeads().length > 0 || portParam.getReceiveRequestFrameHeads() != null && portParam.getReceiveRequestFrameHeads().length > 0) {//设置了帧头
                if (cutDatas.length >= portParam.getReceiveResponseFrameHeads().length || cutDatas.length >= portParam.getReceiveRequestFrameHeads().length) {
                    //获取最后一组接收帧头索引
                    frameHeadsType = 1;//响应
                    int lastFrameHeadPosition = FrameHeadUtil.getLastFrameHeadPosition(portParam.getReceiveResponseFrameHeads(), cutDatas);
                    //获取最后一组请求帧头索引
                    if (lastFrameHeadPosition < 0) {
                        frameHeadsType = 2;//请求
                        lastFrameHeadPosition = FrameHeadUtil.getLastFrameHeadPosition(portParam.getReceiveRequestFrameHeads(), cutDatas);
                    }
                    //最终
                    if (lastFrameHeadPosition < 0) {//没有帧头
                        reset();
                    } else {
                        cutDatas = FrameHeadUtil.splitDataByLastFrameHead(lastFrameHeadPosition, cutDatas);//根据最后一组帧头索引分割数据
                        result(cutDatas);
                    }
                }
            } else {//未设置帧头
                result(cutDatas);
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/5/18 17:38
     * Description：结果
     */
    private void result(byte[] cutDatas) {
        if (cutDatas == null || cutDatas.length <= 0) {
            return;
        }
        int length = portParam.portParamCallback != null ? portParam.portParamCallback.onLength(cutDatas) : 0;//判断指令长度
        if (length > 0 && length <= cutDatas.length) {
            reset();
            byte[] datas = Arrays.copyOf(cutDatas, length);//重发粘包根据长度截取
            if (frameHeadsType == 1) {//响应
                Log.i(TAG, "指令-接收响应:[" + XCByteUtil.toHexStr(datas, true) + "]");
                responseDatas = datas;
            } else if (frameHeadsType == 2) {//请求
                sendMessage(0x123, datas);
                boolean isInterrupt = portParam.portParamCallback != null ? portParam.portParamCallback.onInterrupt(datas) : false;
                if (isInterrupt) {//接收中断
                    Log.i(TAG, "指令-接收中断:[" + XCByteUtil.toHexStr(datas, true) + "]");
                    interruptDatas = datas;
                } else {//接收请求
                    Log.i(TAG, "指令-接收请求:[" + XCByteUtil.toHexStr(datas, true) + "]");
                }
            }
        }
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/3/10 14:51
     * Description：reset
     */
    public void reset() {
        bufferPosition = 0;
        responseDatas = null;
        interruptDatas = null;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/3 9:23
     * Description：getResponseDatas
     */
    public byte[] getResponseDatas() {
        return responseDatas;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2020/7/3 9:23
     * Description：getInterruptDatas
     */
    public byte[] getInterruptDatas() {
        return interruptDatas;
    }

    /**
     * Author：ZhangXuanChen
     * Time：2019/11/27 15:14
     * Description：onRequest
     */
    public abstract void onRequest(byte[] requestDatas);
}
