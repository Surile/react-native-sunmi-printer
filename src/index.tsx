import { NativeModules } from 'react-native';

const { SunmiPrinter, SunmiScanModule } = NativeModules;

export enum PrinterStyleKey {
  // 文本倍宽
  ENABLE_DOUBLE_WIDTH = 1000,
  // 文本倍高
  ENABLE_DOUBLE_HEIGHT = 1001,
  // 文本加粗
  ENABLE_BOLD = 1002,
  // 文本下划线
  ENABLE_UNDERLINE = 1003,
  // 文本反白
  ENABLE_ANTI_WHITE = 1004,
  // 文本删除线
  ENABLE_STRIKETHROUGH = 1005,
  // 文本斜体
  ENABLE_ILALIC = 1006,
  // 文本倒影
  ENABLE_INVERT = 1007,
  // 设置文本左右间距
  SET_TEXT_RIGHT_SPACING = 2000,
  // 设置相对位置
  SET_RELATIVE_POSITION = 2001,
  // 设置绝对位置
  SET_ABSOLUATE_POSITION = 2002,
  // 设置行间距
  SET_LINE_SPACING = 2003,
  // 设置左边距
  SET_LEFT_SPACING = 2004,
  // 设置删除线的样式
  SET_STRIKETHROUGH_STYLE = 2005,
}

export enum PrinterStyleValue {
  ENABLE = 1,
  DISABLE = 2,
}

export enum AlignValue {
  LEFT = 0,
  CENTER = 1,
  RIGHT = 2,
}

type SunmiPrinterType = {
  /*
   * 初始化打印机，重置打印逻辑程序，但不清空缓存区数据，因此
   * 未完成的打印工作将在重置后继续
   */
  printerInit: () => void;
  /**
   * 打印自检
   */
  printerSelfChecking: () => void;
  /*
   * 获取打印机序号
   * */
  getPrinterSerialNo: () => Promise<string>;
  /*
   * 获取打印机固件版本号
   * */
  getPrinterVersion: () => Promise<string>;
  /**
   * 获取打印机服务版本号
   * 此接⼝返回值可适⽤于所有商⽶机器判断但部分状态由于硬件配置不会拿到（例如⼿持机不⽀持
   * 开盖检测）
   */
  getServiceVersion: () => Promise<string>;
  /*
   * 获取打印机型号
   */
  getPrinterModal: () => Promise<string>;
  /**
   * 获取打印机当前的纸张规格
   * ⼿持打印机默认为58mm的纸张规格，台式打印机默认为80mm的纸张规格，但可以通过增加挡
   * 板并进⾏打印机配置设置为使⽤58mm的纸张规格，此接⼝会返回当前打印机设置的纸张规格；
   */
  getPrinterPaper: () => Promise<string>;
  /**
   * 获取打印头打印长度
   * ⽬前可获取到上电以来的打印⻓度，由于台式机和⼿持机的硬件区别，获取打印结果的返回略有
   * 不同，即⼿持机通过ICallback callback接⼝获取打印⻓度，台式机通过返回值直接获取⻓度。
   */
  getPrintedLength: () => void;
  /**
   * 获取打印机的最新状态
   */
  updatePrinterState: () => Promise<number>;
  /**
   * 打印ESC/POS格式指令
   * @param data
   */
  sendRAWData: (data: string) => void;
  /**
   * 设置打印机的样式
   * @param key
   * @description 定义设置不同的属性
   * @param value
   * @description 对应属性设置状态或大小
   */
  setPrinterStyle: (
    key: PrinterStyleKey,
    val: PrinterStyleValue | number
  ) => void;
  /**
   * 设置对齐模式
   * 全局⽅法，对之后执⾏的打印有影响，打印机初始化时取消相关设置。
   *
   * @param align
   * @description 对⻬⽅式：AlignValue.LEFT => 居左；AlignValue.CENTER => 居中;AlignValue.RIGHT => 居右
   */
  setAlignment: (align: AlignValue) => void;
  /**
   * 设置自定义字体
   * @param typeface
   * @description 指定要使⽤的⾃定义字体名称，⽬前仅⽀持⽮量字体，字体需预置在应⽤assets⽬录
   */
  setFontName: (typeface: string) => void;
  /**
   * 设置字体大小
   *
   * @param size
   * @description 全局⽅法，对之后打印有影响，初始化能取消设置，字体⼤⼩是超出标准国际指令的打印⽅式，
   * 调整字体⼤⼩会影响字符宽度，每⾏字符数量也会随之改变，因此按等宽字体形成的排版可能会错乱。
   */
  setFontSize: (size: number) => void;
  /**
   * 设置与取消加粗
   *
   * @param isWeight
   * @default false
   */
  setFontWeight: (isWeight: boolean) => void;
  /**
   * 打印文字
   * 若要修改打印⽂本的样式（如：对⻬⽅式、字体⼤⼩、加粗等），请在调⽤printText⽅法前设
   * 置。
   *
   * @param text
   */
  printerText: (text: string) => void;
  /**
   * 打印指定字体，⼤⼩的⽂本
   * 字体设置只对本次有效
   *
   * @param text
   * @description 打印内容，⽂字宽度超出⼀⾏⾃动换⾏排版，不满⼀⾏或超出⼀⾏不满⼀⾏部分需要在结尾
   * 加强制换⾏符"\n"才会即时打印出来，否则会缓存在缓存区。
   * @param typeface
   * @description 字体名称（现有版本暂时不⽀持设置字体，默认）。
   * @param fontsize
   * @description 字体⼤⼩，只对该⽅法有效。
   */
  printTextWithFont: (text: string, typeface: string, fontsize: number) => void;
  /**
   * 打印矢量文字
   * ⽂字按⽮量⽂字宽度原样输出，即每个字符不等宽。
   *
   * @param text
   * @description ⽂字按⽮量⽂字宽度原样输出，即每个字符不等宽。
   */
  printOriginalText: (text: string) => void;
  /**
   * 打印表格的一行（不支持阿拉伯字符）
   *
   * @param texts
   * @description 字符串数组
   * @param widths
   * @description 各列宽度数组
   * @param aligns
   * @description 各列对⻬⽅式： AlignValue.LEFT => 居左, AlignValue.CENTER => 居中, AlignValue.RIGHT => 居右。
   */
  printColumnsText: (
    texts: string[],
    widths: number[],
    aligns: number[]
  ) => void;
  /**
   * 打印表格的⼀⾏，可以指定列宽、对齐⽅式
   *
   * @param texts
   * @description 字符串数组
   * @param widths
   * @description 各列宽度数组
   * @param aligns
   * @description 各列对⻬⽅式： AlignValue.LEFT => 居左, AlignValue.CENTER => 居中, AlignValue.RIGHT => 居右。
   */
  printColumnsString: (
    texts: string[],
    widths: number[],
    aligns: number[]
  ) => void;
  /**
   * 打印⼀维条码
   *
   * @param data
   * @description 维码内容
   * @param symbology
   * @description 条码类型(0-8)：0 -> UPC-A, 1 -> UPC-E, 2 -> JAN13(ENA13), 3 -> JAN8(EAN8), 4 -> CODE39, 5 -> ITF, 6 -> CODABAR, 7 -> CODE93, 8 -> CODE128
   * @param height
   * @default 162
   * @description 条码⾼度, 取值 1 - 255
   * @param width
   * @default 2
   * @description 条码宽度, 取值 2 - 6,
   * @param textPosition
   * @description ⽂字位置（0 - 3）：0 -> 不打印文字, 1 -> 文字在条码上方, 2 -> ⽂字在条码下⽅, 3 -> 条码上下⽅均打印
   */
  printBarCode: (
    data: string,
    symbology: number,
    height: number,
    width: number,
    textPosition: number
  ) => void;
  /**
   * 打印QR条码
   * @description 普通打印状态下在调⽤该⽅法后会直接输出打印，每个⼆维码块为 4 个像素点（⼩于 4 扫码解析
   * 有可能失败）。最⼤⽀持 version19（93*93）的模式。
   *
   * @param data
   * @description QR码内容
   * @param modulesize
   * @description QR码块⼤⼩，单位:点, 取值 4 ⾄ 16。
   * @param errorlevel
   * @description ⼆维码纠错等级(0 - 3)：0 -> 纠错级别 L ( 7%), 1 -> 纠错级别 M (15%), 2 -> 纠错级别 Q (25%), 3 -> 纠错级别 H (30%)
   */
  printQRCode: (data: string, modulesize: number, errorlevel: number) => void;
  /**
   * 打印⼆维条码
   * @description 普通打印状态下在调⽤该⽅法后会直接输出打印；此接⼝在4.1.2版本后⽀持;
   *
   * @param data
   * @description 二维码内容
   * @param symbology
   * @description 二维码类型：1 -> Qr（同printQRCode接⼝）, 2 -> PDF417, 3 -> DataMatrix
   * @param modulesize
   * @description ⼆维码有效块⼤⼩，根据码类型不同，⽀持的最佳块⼤⼩不同：Qr -> 4～16（同printQRCode接⼝）, PDF417 -> 1～4, DataMatrix -> 4～16
   * @param errorlevel
   * @description ⼆维码纠错等级，根据码类型不同，⽀持等级范围不同：Qr -> 0～3（同printQRCode接⼝）, PDF417 -> 0～8, DataMatrix -> 默认使⽤ECC200⾃动纠错 不⽀持设置
   */
  print2DCode: (
    data: string,
    sysmbology: number,
    modulesize: number,
    errorlevel: number
  ) => void;
  /**
   * 包事务打印专⽤接⼝
   *
   * @param tranBean
   * @description 任务列表
   */
  commitPrint: (list: any) => void;
  /**
   * 进⼊事务模式
   *
   * @param clear
   * @description 是否清除缓冲区内容：true -> 清除上⼀次事务打印未提交的内容；false -> 不清除上⼀次事务打印未提交的内容，下次提交将包含上次的内容。
   */
  enterPrinterBuffer: (clear: boolean) => void;
  /**
   * exitPrinterBuffer
   * @support 除V1设备
   * @param commit
   * @description 是否打印出缓冲区内容：true -> 会打印出事务队列中的所有内容；false -> 不会打印事务队列中的内容，此内容将保存直到下次提交。
   */
  exitPrinterBuffer: (commit: boolean) => void;
  /**
   * 提交事务打印
   * @support 除V1设备
   * @description 将事务队列中的所有内容提交并打印，之后仍然处于事务打印模式。
   */
  commitPrinterBuffer: () => void;
  /**
   * 提交事务打印并回调结果
   *
   * @support 除V1版本
   */
  commitPrinterBufferWithCallbacka: () => void;
  /**
   * 打印机⾛纸n⾏
   * @description 强制换⾏，结束之前的打印内容后⾛纸 n ⾏。
   *
   * @param num
   * @description 走纸行数
   */
  lineWrap: (num: number) => void;
  /**
   * 切纸
   * @supported 仅⽀持台式机带切⼑功能机器
   * @description 由于打印头和切⼑有⼀定距离，调⽤接⼝将⾃动补全这段距离；
   */
  cutPaper: () => void;
  /**
   * 获取切⼑次数
   */
  getCutPaperTimes: () => Promise<number>;
  /**
   * 打开钱箱
   * @supported 仅⽀持台式机带钱箱功能机器。
   */
  openDrawer: () => void;
  /**
   * 获取当前的钱箱状态
   * @supported ⽬前仅对S2、T2、T2mini机器 v4.0.0版本以上⽀持此接⼝
   * @description 可以通过此接⼝在部分具有连接钱箱功能的机型上获取钱箱开关状态,
   */
  getDrawerStatus: () => void;
  /**
   * 打印图片
   * 图⽚最⼤像素需要宽x⾼⼩于250万，且宽度根据纸张规格设置（58为384像素，80为576像素），
   * 如果超过纸张宽度将不显示
   * https://github.com/Surile/react-native-sunmi-printer/issues/1#issuecomment-1088685896
   * @param encodedString
   * @param pixelWidth
   */
  printBitmap: (encodedString: string, pixelWidth: number) => void;
  /**
   * 打印图⽚(2)
   * 图⽚像素分辨率⼩于200万，且宽度根据纸张规格设置（58为384像素，80为576像素），如果超
   * 过纸张宽度将不显示
   *
   * @param bitmap
   * @param type
   */
  printBitmapCustom: (bitmap: any, type: number) => void;
  /**
   * 打印图⽚(3)
   * 图⽚像素分辨率⼩于200万，且宽度根据纸张规格设置（58为384像素，80为576像素），如果超
   * 过纸张宽度将不显示
   *
   * @param encodedString
   * @param pixelWidth
   * @param type
   */
  printBitmapBase64Custom: (
    encodedString: string,
    pixelWidth: number,
    type: number
  ) => void;
  /**
   * 是否存在打印机服务
   */
  hasPrinter: () => Promise<boolean>;
};

type SunmiScanType = {
  /**
   * 摄像头扫码
   */
  scan: () => Promise<any>;
};

export const SunmiScan = SunmiScanModule as SunmiScanType;

export default SunmiPrinter as SunmiPrinterType;
