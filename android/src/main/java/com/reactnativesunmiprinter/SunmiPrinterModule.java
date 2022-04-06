package com.reactnativesunmiprinter;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.InnerResultCallback;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.peripheral.printer.TransBean;
import com.sunmi.peripheral.printer.WoyouConsts;

import java.util.Map;

@ReactModule(name = SunmiPrinterModule.NAME)
public class SunmiPrinterModule extends ReactContextBaseJavaModule {

  private Promise p;

  private SunmiPrinterService printerService;

  public static final String NAME = "SunmiPrinter";

  private static final String TAG = "SunmiPrinter_Error";

  private InnerResultCallback innerResultCallback = new InnerResultCallback() {
    @Override
    public void onRunResult(boolean isSuccess) throws RemoteException {
      if (isSuccess) {
        p.resolve(200);
      } else {
        p.reject("" + 0);
      }
    }

    @Override
    public void onReturnString(String result) throws RemoteException {

    }

    @Override
    public void onRaiseException(int code, String msg) throws RemoteException {

    }

    @Override
    public void onPrintResult(int code, String msg) throws RemoteException {

    }
  };

  InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
    @Override
    protected void onConnected(SunmiPrinterService service) {
      //这⾥即获取到绑定服务成功连接后的远程服务接⼝句柄
      //可以通过service调⽤⽀持的打印⽅法
      printerService = service;
    }

    @Override
    protected void onDisconnected() {
      //当服务异常断开后，会回调此⽅法，建议在此做重连策略
    }
  };

  public SunmiPrinterModule(ReactApplicationContext reactContext) {
    super(reactContext);
    try {
      InnerPrinterManager.getInstance().bindService(reactContext, innerPrinterCallback);
    } catch (RemoteException e) {
      Log.i(TAG, "ERROR: " + e.getMessage());
    }
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  /**
   * 初始化打印机，重置打印逻辑程序，但不清空缓存区数据，因此
   * 未完成的打印工作将在重置后继续
   *
   * @return
   */
  @ReactMethod
  public void printerInit() throws RemoteException {
    printerService.printerInit(innerResultCallback);
  }

  /**
   * 打印自检
   *
   * @param callback 回调
   */
  @ReactMethod
  public void printerSelfChecking() throws RemoteException {
    printerService.printerSelfChecking(innerResultCallback);
  }

  /*
   * 获取打印机序号
   * */
  @ReactMethod
  public void getPrinterSerialNo(Promise promise) {
    try {
      promise.resolve(printerService.getPrinterSerialNo());
    } catch (RemoteException e) {
      e.printStackTrace();
      Log.i(TAG, "ERROR: " + e.getMessage());
      promise.reject("" + 0, e.getMessage());
    }
  }

  /*
   * 获取打印机固件版本号
   * */
  @ReactMethod
  public void getPrinterVersion(Promise promise) {
    try {
      promise.resolve(printerService.getPrinterVersion());
    } catch (RemoteException e) {
      e.printStackTrace();
      Log.i(TAG, "ERROR: " + e.getMessage());
      promise.reject("" + 0, e.getMessage());
    }
  }

  /*
   * 获取打印机型号
   */
  @ReactMethod
  public void getPrinterModal(Promise promise) {
    try {
      promise.resolve(printerService.getPrinterModal());
    } catch (RemoteException e) {
      e.printStackTrace();
      Log.i(TAG, "ERROR: " + e.getMessage());
      promise.reject("" + 0, e.getMessage());
    }
  }

  /**
   * 获取打印机当前的纸张规格
   * ⼿持打印机默认为58mm的纸张规格，台式打印机默认为80mm的纸张规格，但可以通过增加挡
   * 板并进⾏打印机配置设置为使⽤58mm的纸张规格，此接⼝会返回当前打印机设置的纸张规格；
   */
  @ReactMethod
  public void getPrinterPaper(Promise promise) {
    try {
      promise.resolve(printerService.getPrinterPaper());
    } catch (RemoteException e) {
      e.printStackTrace();
      Log.i(TAG, "ERROR: " + e.getMessage());
      promise.reject("" + 0, e.getMessage());
    }
  }

  /**
   * 获取打印机的最新状态
   */
  @ReactMethod
  public void updatePrinterState(Promise promise) {
    try {
      promise.resolve(printerService.updatePrinterState());
    } catch (RemoteException e) {
      e.printStackTrace();
      Log.i(TAG, "ERROR: " + e.getMessage());
      promise.reject("" + 0, e.getMessage());
    }
  }

  /**
   * 获取打印机服务版本号
   * 此接⼝返回值可适⽤于所有商⽶机器判断但部分状态由于硬件配置不会拿到（例如⼿持机不⽀持
   * 开盖检测）
   */
  @ReactMethod
  public void getServiceVersion(Promise promise) {
    try {
      promise.resolve(printerService.getServiceVersion());
    } catch (RemoteException e) {
      e.printStackTrace();
      Log.i(TAG, "ERROR: " + e.getMessage());
      promise.reject("" + 0, e.getMessage());
    }
  }

  /**
   * 获取打印头打印长度
   * ⽬前可获取到上电以来的打印⻓度，由于台式机和⼿持机的硬件区别，获取打印结果的返回略有
   * 不同，即⼿持机通过ICallback callback接⼝获取打印⻓度，台式机通过返回值直接获取⻓度。
   */
  @ReactMethod
  public void getPrintedLength() throws RemoteException {
    printerService.getPrintedLength(innerResultCallback);
  }

  /**
   * 是否存在打印机服务
   */
  @ReactMethod
  public void hasPrinter(Promise promise) {
    final boolean hasPrinterService = printerService != null;
    promise.resolve(hasPrinterService);
  }

  /**
   * 打印ESC/POS格式指令
   * data byte[]
   */
  @ReactMethod
  public void sendRAWData(String base64Data) throws RemoteException {
    final byte[] d = Base64.decode(base64Data, Base64.DEFAULT);
    printerService.sendRAWData(d, innerResultCallback);
  }

  /**
   * @param key
   * @param value
   */
  @ReactMethod
  public void setPrinterStyle(int key, int value) throws RemoteException {
    printerService.setPrinterStyle(key, value);
  }

  /**
   * 设置对齐模式
   * 全局⽅法，对之后执⾏的打印有影响，打印机初始化时取消相关设置。
   */
  @ReactMethod
  public void setAlignment(int alignment) throws RemoteException {
    printerService.setAlignment(alignment, innerResultCallback);
  }

  /**
   * 设置字体⼤⼩
   * 全局⽅法，对之后打印有影响，初始化能取消设置，字体⼤⼩是超出标准国际指令的打印⽅式，
   * 调整字体⼤⼩会影响字符宽度，每⾏字符数量也会随之改变，因此按等宽字体形成的排版可能会错乱。
   *
   * @param fontSize
   */
  @ReactMethod
  public void setFontSize(float fontSize) throws RemoteException {
    printerService.setFontSize(fontSize, innerResultCallback);

  }

  /**
   * 设置粗体
   *
   * @param isWeight
   */
  @ReactMethod
  public void setFontWeight(boolean isWeight) throws RemoteException {
    if (isWeight) {
      printerService.sendRAWData(ESCUtil.boldOn(), null);
    } else {
      printerService.sendRAWData(ESCUtil.boldOff(), null);
    }
  }


  /**
   * 打印文字
   * 若要修改打印⽂本的样式（如：对⻬⽅式、字体⼤⼩、加粗等），请在调⽤printText⽅法前设
   * 置。
   *
   * @param text
   */
  @ReactMethod
  public void printerText(String text) throws RemoteException {
    printerService.printText(text, null);
  }


  /**
   * 打印指定字体，⼤⼩的⽂本
   * 字体设置只对本次有效
   *
   * @param text
   * @param typeface
   * @param fontsize
   */
  @ReactMethod
  public void printTextWithFont(String text, String typeface, float fontsize) throws RemoteException {
    printerService.printTextWithFont(text, typeface, fontsize, null);

  }

  /**
   * 打印矢量文字
   * ⽂字按⽮量⽂字宽度原样输出，即每个字符不等宽。
   *
   * @param text
   */
  @ReactMethod
  public void printOriginalText(String text) throws RemoteException {
    printerService.printOriginalText(text, null);
  }

  /**
   * 打印表格的一行（不支持阿拉伯字符）
   *
   * @param colrsTextAir
   * @param colsWidthArr
   * @param colsAlign
   */
  @ReactMethod
  public void printColumnsText(ReadableArray colrsTextAir, ReadableArray colsWidthArr, ReadableArray colsAlign) throws RemoteException {
    String[] texts = new String[colrsTextAir.size()];

    for (int j = 0; j < colrsTextAir.size(); ++j) {
      texts[j] = colrsTextAir.getString(j);
    }

    int[] widths = new int[colsWidthArr.size()];
    for (int j = 0; j < colsWidthArr.size(); ++j) {
      widths[j] = colsWidthArr.getInt(j);
    }

    int[] aligns = new int[colsAlign.size()];
    for (int j = 0; j < colsAlign.size(); ++j) {
      aligns[j] = colsAlign.getInt(j);
    }

    printerService.printColumnsText(texts, widths, aligns, null);
  }


  /**
   * 打印表格的一行，可以指定列宽、对齐方式
   *
   * @param colsTextArr
   * @param colsWidthArr
   * @param colsAlign
   */
  @ReactMethod
  public void printColumnsString(ReadableArray colsTextArr, ReadableArray colsWidthArr, ReadableArray colsAlign) throws
    RemoteException {
    String[] texts = new String[colsTextArr.size()];

    for (int j = 0; j < colsTextArr.size(); ++j) {
      texts[j] = colsTextArr.getString(j);
    }

    int[] widths = new int[colsWidthArr.size()];
    for (int j = 0; j < colsWidthArr.size(); ++j) {
      widths[j] = colsWidthArr.getInt(j);
    }

    int[] aligns = new int[colsAlign.size()];
    for (int j = 0; j < colsAlign.size(); ++j) {
      aligns[j] = colsAlign.getInt(j);
    }
    printerService.printColumnsString(texts, widths, aligns, null);
  }

  /**
   * 打印图片
   * 图⽚最⼤像素需要宽x⾼⼩于250万，且宽度根据纸张规格设置（58为384像素，80为576像素），
   * 如果超过纸张宽度将不显示
   * https://github.com/Surile/react-native-sunmi-printer/issues/1#issuecomment-1088685896
   * @param encodedString
   * @param pixelWidth
   */
  @ReactMethod
  public void printBitmap(String encodedString, Int pixelWidth) throws RemoteException {
    final String pureBase64Encoded = encodedString.substring(encodedString.indexOf(",")  + 1);
    final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    int w = decodedBitmap.getWidth();
    Integer h = decodedBitmap.getHeight();
    Bitmap scaledImage = Bitmap.createScaledBitmap(decodedBitmap, pixelWidth, (pixelWidth / w) * h, false);
    printerService.printBitmap(scaledImage, innerResultCallback);
  }

  /**
   * 打印图⽚(2)
   * 图⽚像素分辨率⼩于200万，且宽度根据纸张规格设置（58为384像素，80为576像素），如果超
   * 过纸张宽度将不显示
   *
   * @param bitmap
   * @param type
   */
  @ReactMethod
  public void printBitmapCustom(Bitmap bitmap, int type) throws RemoteException {
    printerService.printBitmapCustom(bitmap, type, innerResultCallback);
  }

  /**
   * 打印⼀维条码
   *
   * @param data
   * @param symbology
   * @param height
   * @param width
   * @param textPosition
   */
  @ReactMethod
  public void printBarCode(String data, int symbology, int height, int width, int textPosition) throws
    RemoteException {
    printerService.printBarCode(data, symbology, height, width, textPosition, innerResultCallback);
  }

  /**
   * 打印QR条码
   * 普通打印状态下在调⽤该⽅法后会直接输出打印，每个⼆维码块为 4 个像素点（⼩于 4 扫码解析
   * 有可能失败）。最⼤⽀持 version19（93*93）的模式。
   *
   * @param data
   * @param modulesize
   * @param errorlevel
   */
  @ReactMethod
  public void printQRCode(String data, int modulesize, int errorlevel) throws RemoteException {
    printerService.printQRCode(data, modulesize, errorlevel, innerResultCallback);
  }

  /**
   * 打印二维条码
   *
   * @param data
   * @param symbology
   * @param modulesize
   * @param errorlevel
   */
  @ReactMethod
  public void print2DCode(String data, int symbology, int modulesize, int errorlevel) throws
    RemoteException {
    printerService.print2DCode(data, symbology, modulesize, errorlevel, innerResultCallback);
  }

  /**
   * 包事务打印专用接口
   *
   * @param tranBean
   */
  @ReactMethod
  public void commitPrint(TransBean[] tranBean) throws RemoteException {
    printerService.commitPrint(tranBean, innerResultCallback);
  }

  /**
   * 进入事务模式
   *
   * @param clear
   */
  @ReactMethod
  public void enterPrinterBuffer(Boolean clear) throws RemoteException {
    printerService.enterPrinterBuffer(clear);
  }

  /**
   * 退出事务模式
   *
   * @param commit
   */
  @ReactMethod
  public void exitPrinterBuffer(Boolean commit) throws RemoteException {
    printerService.exitPrinterBuffer(commit);
  }


  /**
   * 提交事务打印
   * 将事务队列中的所有内容提交并打印，之后仍然处于事务打印模式
   */
  @ReactMethod
  public void commitPrinterBuffer() throws RemoteException {
    printerService.commitPrinterBuffer();
  }


  /**
   * 提交事务打印并回调结果
   */
  @ReactMethod
  public void commitPrinterBufferWithCallbacka() throws RemoteException {
    printerService.commitPrinterBufferWithCallback(innerResultCallback);
  }

  /**
   * 打印走纸n行
   *
   * @param n
   */
  @ReactMethod
  public void lineWrap(int n) throws RemoteException {
    printerService.lineWrap(n, innerResultCallback);
  }

  /**
   * 切纸
   *
   * @throws RemoteException
   */
  @ReactMethod
  public void cutPaper() throws RemoteException {
    printerService.cutPaper(innerResultCallback);
  }

  /**
   * 打开钱箱
   */
  @ReactMethod
  public void openDrawer() throws RemoteException {
    printerService.openDrawer(innerResultCallback);
  }

  /**
   * 钱箱状态
   *
   * @param promise
   */
  @ReactMethod
  public void getDrawerStatus(Promise promise) {
    try {
      promise.resolve(printerService.getDrawerStatus());
    } catch (RemoteException e) {
      e.printStackTrace();
      Log.i(TAG, "ERROR: " + e.getMessage());
      promise.reject("" + 0, e.getMessage());
    }
  }
}
