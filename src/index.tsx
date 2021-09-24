import { NativeModules } from 'react-native';

type SunmiPrinterType = {
  multiply(a: number, b: number): Promise<number>;
};

const { SunmiPrinter } = NativeModules;

export default SunmiPrinter as SunmiPrinterType;
