package futu_api.futu_api.trade;

import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTAPI_Conn_Trd;
import com.futu.openapi.FTSPI_Conn;
import com.futu.openapi.FTSPI_Trd;
import com.futu.openapi.pb.TrdCommon;
import com.futu.openapi.pb.TrdPlaceOrder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

public class PlaceOrder implements FTSPI_Trd, FTSPI_Conn {
    FTAPI_Conn_Trd trd = new FTAPI_Conn_Trd();

    public PlaceOrder() {
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
        TrdPlaceOrder.C2S c2s = TrdPlaceOrder.C2S//
                .newBuilder()//
                .setPacketID(trd.nextPacketID())//
                .setHeader(header)//
                .setTrdSide(TrdCommon.TrdSide.TrdSide_Buy_VALUE)//
                .setOrderType(TrdCommon.OrderType.OrderType_Normal_VALUE)//
                .setSecMarket(TrdCommon.TrdSecMarket.TrdSecMarket_HK_VALUE)//
                .setCode("00939")//
                .setQty(1000)//
                .setPrice(5.80)//
                .build();
        TrdPlaceOrder.Request req = TrdPlaceOrder.Request//
                .newBuilder()//
                .setC2S(c2s)//
                .build();
        int seqNo = trd.placeOrder(req);
        System.out.printf("Send TrdPlaceOrder: %d\n", seqNo);
    }

    @Override
    public void onDisconnect(FTAPI_Conn client, long errCode) {
        System.out.printf("Trd onDisConnect: %d\n", errCode);
    }

    @Override
    public void onReply_PlaceOrder(FTAPI_Conn client, int nSerialNo,
            TrdPlaceOrder.Response rsp) {
        if (rsp.getRetType() != 0) {
            System.out.printf("TrdPlaceOrder failed: %s\n", rsp.getRetMsg());
        } else {
            try {
                String json = JsonFormat.printer().print(rsp);
                System.out.printf("Receive TrdPlaceOrder: %s\n", json);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FTAPI.init();
        PlaceOrder trd = new PlaceOrder();
        trd.start();

        while (true) {
            try {
                Thread.sleep(1000 * 600);
            } catch (InterruptedException exc) {

            }
        }
    }
}

