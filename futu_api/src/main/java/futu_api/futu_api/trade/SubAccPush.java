package futu_api.futu_api.trade;

import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTAPI_Conn_Trd;
import com.futu.openapi.FTSPI_Conn;
import com.futu.openapi.FTSPI_Trd;
import com.futu.openapi.pb.TrdSubAccPush;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

public class SubAccPush implements FTSPI_Trd, FTSPI_Conn {
    FTAPI_Conn_Trd trd = new FTAPI_Conn_Trd();

    public SubAccPush() {
        trd.setClientInfo("javaclient", 1);
        trd.setConnSpi(this);
        trd.setTrdSpi(this);
    }

    public void start() {
        trd.initConnect("127.0.0.1", (short) 11111, false);
    }

    @Override
    public void onInitConnect(FTAPI_Conn client, long errCode, String desc) {
        System.out.printf("Trd onInitConnect: ret=%b desc=%s connID=%d\n",
                errCode, desc, client.getConnectID());
        if (errCode != 0)
            return;

        TrdSubAccPush.C2S c2s = TrdSubAccPush.C2S//
                .newBuilder()//
                .addAccIDList(1)//
                .build();
        TrdSubAccPush.Request req = TrdSubAccPush.Request//
                .newBuilder()//
                .setC2S(c2s)//
                .build();
        int seqNo = trd.subAccPush(req);
        System.out.printf("Send TrdSubAccPush: %d\n", seqNo);
    }

    @Override
    public void onDisconnect(FTAPI_Conn client, long errCode) {
        System.out.printf("Trd onDisConnect: %d\n", errCode);
    }

    @Override
    public void onReply_SubAccPush(FTAPI_Conn client, int nSerialNo,
            TrdSubAccPush.Response rsp) {
        if (rsp.getRetType() != 0) {
            System.out.printf("TrdSubAccPush failed: %s\n", rsp.getRetMsg());
        } else {
            try {
                String json = JsonFormat.printer().print(rsp);
                System.out.printf("Receive TrdSubAccPush: %s\n", json);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FTAPI.init();
        SubAccPush trd = new SubAccPush();
        trd.start();

        while (true) {
            try {
                Thread.sleep(1000 * 600);
            } catch (InterruptedException exc) {

            }
        }
    }
}

