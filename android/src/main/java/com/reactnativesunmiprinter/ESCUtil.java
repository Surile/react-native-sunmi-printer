package com.reactnativesunmiprinter;

public class ESCUtil {

  public static final byte ESC = 0x1B;  // 换码
  public static final byte FS = 0x1C;   // 文本分隔符
  public static final byte GS = 0x1D;   // 组分隔符
  public static final byte DLE = 0x10;  // 数据连接换码
  public static final byte EOT = 0x04;  // 传输结束
  public static final byte ENQ = 0x05;  // 询问字符
  public static final byte SP = 0x20;   // 空格
  public static final byte HT = 0x09;   // 横向列表
  public static final byte LF = 0x0A;   //打印并换行（水平定位）
  public static final byte CR = 0x0D;   // 归位键
  public static final byte FF = 0x0C;   // 走纸控制（打印并回到标准模式（在页模式下） ）
  public static final byte CAN = 0x18;  // 作废（页模式下取消打印数据 ）


  /**
   * 字体加粗
   */
  public static byte[] boldOn() {
    byte[] result = new byte[3];
    result[0] = ESC;
    result[1] = 69;
    result[2] = 0xF;
    return result;
  }

  /**
   * 取消字体加粗
   */
  public static byte[] boldOff() {
    byte[] result = new byte[3];
    result[0] = ESC;
    result[1] = 69;
    result[2] = 0;
    return result;
  }
}
