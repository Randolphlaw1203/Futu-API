package futu_api.futu_api.quote;

import com.futu.openapi.FTAPI;
import com.futu.openapi.FTAPI_Conn;
import com.futu.openapi.FTAPI_Conn_Qot;
import com.futu.openapi.FTSPI_Conn;
import com.futu.openapi.FTSPI_Qot;
import com.futu.openapi.pb.QotGetSubInfo;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

public class GetSubInfo implements FTSPI_Qot, FTSPI_Conn {
  FTAPI_Conn_Qot qot = new FTAPI_Conn_Qot();

  public GetSubInfo() {
    qot.setClientInfo("javaclient", 1);
    qot.setConnSpi(this);
    qot.setQotSpi(this);
  }

  public void start() {
    qot.initConnect("127.0.0.1", (short) 11111, false);
  }

  @Override
  public void onInitConnect(FTAPI_Conn client, long errCode, String desc) {
    System.out.printf("Qot onInitConnect: ret=%b desc=%s connID=%d\n", errCode,
        desc, client.getConnectID());
    if (errCode != 0)
      return;

    QotGetSubInfo.C2S c2s = QotGetSubInfo.C2S//
        .newBuilder()//
        .build();
    QotGetSubInfo.Request req = QotGetSubInfo.Request//
        .newBuilder()//
        .setC2S(c2s)//
        .build();
    int seqNo = qot.getSubInfo(req);
    System.out.printf("Send QotGetSubInfo: %d\n", seqNo);
  }

  @Override
  public void onDisconnect(FTAPI_Conn client, long errCode) {
    System.out.printf("Qot onDisConnect: %d\n", errCode);
  }

  @Override
  public void onReply_GetSubInfo(FTAPI_Conn client, int nSerialNo,
      QotGetSubInfo.Response rsp) {
    if (rsp.getRetType() != 0) {
      System.out.printf("QotGetSubInfo failed: %s\n", rsp.getRetMsg());
    } else {
      try {
        String json = JsonFormat.printer().print(rsp);
        System.out.printf("Receive QotGetSubInfo: %s\n", json);
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    FTAPI.init();
    GetSubInfo qot = new GetSubInfo();
    qot.start();

    while (true) {
      try {
        Thread.sleep(1000 * 600);
      } catch (InterruptedException exc) {

      }
    }
  }
}

