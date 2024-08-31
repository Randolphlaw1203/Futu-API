package futu_api.futu_api.trade;

import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTAPI_Conn_Trd;
import com.futu.openapi.FTSPI_Conn;
import com.futu.openapi.FTSPI_Trd;
import com.futu.openapi.pb.TrdCommon;
import com.futu.openapi.pb.TrdGetOrderList;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

public class GetOrderList implements FTSPI_Trd, FTSPI_Conn {
    FTAPI_Conn_Trd trd = new FTAPI_Conn_Trd();

    public GetOrderList() {
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

        TrdCommon.TrdHeader header = TrdCommon.TrdHeader//
                .newBuilder()//
                .setAccID(1)//
                .setTrdEnv(TrdCommon.TrdEnv.TrdEnv_Simulate_VALUE)//
                .setTrdMarket(TrdCommon.TrdMarket.TrdMarket_HK_VALUE)//
                .build();
        TrdGetOrderList.C2S c2s = TrdGetOrderList.C2S//
                .newBuilder()//
                .setHeader(header)//
                .build();
        TrdGetOrderList.Request req = TrdGetOrderList.Request//
                .newBuilder()//
                .setC2S(c2s)//
                .build();
        int seqNo = trd.getOrderList(req);
        System.out.printf("Send TrdGetOrderList: %d\n", seqNo);
    }

    @Override
    public void onDisconnect(FTAPI_Conn client, long errCode) {
        System.out.printf("Trd onDisConnect: %d\n", errCode);
    }

    @Override
    public void onReply_GetOrderList(FTAPI_Conn client, int nSerialNo,
            TrdGetOrderList.Response rsp) {
        if (rsp.getRetType() != 0) {
            System.out.printf("TrdGetOrderList failed: %s\n", rsp.getRetMsg());
        } else {
            try {
                String json = JsonFormat.printer().print(rsp);
                System.out.printf("Receive TrdGetOrderList: %s\n", json);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FTAPI.init();
        GetOrderList trd = new GetOrderList();
        trd.start();

        while (true) {
            try {
                Thread.sleep(1000 * 600);
            } catch (InterruptedException exc) {

            }
        }
    }
}

