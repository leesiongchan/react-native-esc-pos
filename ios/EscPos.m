#import "EscPos.h"
#import <React/RCTLog.h>
#import "ePOS2.h"
#import "ePOSEasySelect.h"



@interface EscPos ()<Epos2DiscoveryDelegate, Epos2PtrStatusChangeDelegate, Epos2PtrReceiveDelegate, Epos2PrinterSettingDelegate>{

  Epos2Printer *eposPrinter;
  BOOL isBluetoothPrinter;
    
}


@end

@implementation EscPos

static NSString * _PRINTING_SIZE_58_MM = @"_PRINTING_SIZE_58_MM";
static NSString * _PRINTING_SIZE_80_MM = @"_PRINTING_SIZE_80_MM";


+ (BOOL)requiresMainQueueSetup
{
  return YES;  // only do this if your module initialization relies on calling UIKit!
}

- (NSDictionary *)constantsToExport
{
  return @{ @"PRINTING_SIZE_58_MM": _PRINTING_SIZE_58_MM, @"PRINTING_SIZE_80_MM": _PRINTING_SIZE_80_MM};
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(setConfig:(NSDictionary *)config)
{
    NSString *strPrinterType = [config valueForKey:@"type"];
    if (strPrinterType!=nil) {
        if ([strPrinterType isEqualToString:@"bluetooth"]) {
            isBluetoothPrinter = YES;
        } else {
             isBluetoothPrinter = NO;
        }
    }else{
             isBluetoothPrinter = NO;
    }
}

RCT_EXPORT_METHOD(connectNetworkPrinter:(NSString *)address port:(int)port resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    @try{
        BOOL isConnected = [self eposPrinterConnect:address port:port];
        resolve(@[[NSNumber numberWithBool:isConnected]]);
    }
    @catch(NSError *e){
        reject(nil, nil, e);
    }
}

RCT_EXPORT_METHOD(connectBluetoothPrinter:(NSString *)address resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
     @try{
          BOOL isConnected = [self eposPrinterConnect:address port:0];
          resolve(@[[NSNumber numberWithBool:isConnected]]);
      }
      @catch(NSError *e){
          reject(nil, nil, e);
      }
}

RCT_EXPORT_METHOD(disconnect:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    @try {
        [self disconnectCurrentPrinter];
        resolve(@"Success");
    } @catch (NSError *e) {
        reject(nil, nil, e);
    }
}

RCT_EXPORT_METHOD(printSample:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject) {
    @try {
        [self printSampleText];
        resolve(@"Success");
    } @catch (NSError *e) {
        reject(nil, nil, e);
    }
}

RCT_EXPORT_METHOD(setPrintingSize:(NSString *)printingSize resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"printingSize--%@",printingSize);
    @try{
        int result = EPOS2_SUCCESS;

        NSMutableDictionary *dictSettings = [[NSMutableDictionary alloc]init];
        if([printingSize isEqualToString:PRINTING_SIZE_58_MM]) {
            
            [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PAPERWIDTH_58_0] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PAPERWIDTH]];
        }
        else if([printingSize isEqualToString:_PRINTING_SIZE_80_MM]) {
        
            [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PAPERWIDTH_80_0] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PAPERWIDTH]];

        }
        
        result = [eposPrinter setPrinterSetting:EPOS2_PARAM_DEFAULT setttingList:dictSettings delegate:self];
        if(result != EPOS2_SUCCESS){
            NSLog(@"setPrinterSetting error --%d",result);
            return;
        }
        
        resolve(@"Done");
    }
    @catch(NSError *e){
        reject(nil, nil, e);
    }
}

RCT_EXPORT_METHOD(setTextDensity:(int)textDensity resolver:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
{
    NSLog(@"setTextDensity--%d",textDensity);
    @try{
        NSMutableDictionary *dictSettings = [[NSMutableDictionary alloc]init];

        // // 0 to 8 (0-3 = smaller, 4 = default, 5-8 = larger)
        
        switch(textDensity) {
        case 0:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_70] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

            break;
        case 1:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_80] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

            break;
        case 2:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_85] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

            break;
        case 3:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_90] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

            break;
        case 4:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_100] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

            break;
        case 5:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_110] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

            break;
        case 6:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_120] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

            break;
        case 7:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_125] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

            break;
        case 8:
                [dictSettings setObject:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY_130] forKey:[NSNumber numberWithInt:EPOS2_PRINTER_SETTING_PRINTDENSITY]];

        }
        NSLog(@"dictSettings--%@",dictSettings);
        [eposPrinter setPrinterSetting:EPOS2_PARAM_DEFAULT setttingList:dictSettings delegate:self];
        
        resolve(@"Done");
    }
    @catch(NSError *e){
        reject(nil, nil, e);
    }
}

- (BOOL)eposPrinterConnect:(NSString *)address port:(int)port {
    
    if (eposPrinter!=nil) {
        eposPrinter = nil;
    }
      eposPrinter = [[Epos2Printer alloc] initWithPrinterSeries:EPOS2_MODEL_ALL lang:EPOS2_MODEL_ANK];
      [eposPrinter setReceiveEventDelegate:self];
  
  if (eposPrinter != nil) {
    if (isBluetoothPrinter) {
       address = [NSString stringWithFormat:@"BT:%@", address];
    }
    else if (address.length > 0) {
      address = [NSString stringWithFormat:@"TCP:%@", address];
    }
    
    if (address.length > 0) {
      int result = [eposPrinter connect:address timeout:EPOS2_PARAM_DEFAULT];
      
      if (result != EPOS2_SUCCESS) {
        eposPrinter = nil;
        NSLog(@"epos error: unable to connect error code - %d", result);
        if (result == EPOS2_ERR_ILLEGAL) {
          // Trying to reconnect one more time
          NSLog(@"epos status: Trying to re-connect");
          return [self eposPrinterConnect:address port:port];
        }
      }
      
      result = [eposPrinter beginTransaction];
      
      if (result != EPOS2_SUCCESS) {
        eposPrinter = nil;
        
        NSLog(@"epos error: unable to connect error code - %d", result);
        if (result == EPOS2_ERR_ILLEGAL) {
          // Trying to reconnect one more time
          NSLog(@"epos status: Trying to re-connect");
          return [self eposPrinterConnect:address port:port];
        }
        
        return NO;
      }
    }
    else {
      eposPrinter = nil;
      NSLog(@"epos error: Address empty");
      return NO;
    }
  }
  else {
    NSLog(@"epos error: eposprinter object is null");
    return NO;
  }
    
  return YES;
}

-(BOOL)printSampleText{
    {
      int result = EPOS2_SUCCESS;
      Epos2PrinterStatusInfo *status = nil;
      
      if (eposPrinter == nil) {
        return NO;
      }
      status = [eposPrinter getStatus];
      [self dispPrinterWarnings:status];
      
      if (![self isPrintable:status]) {
        [eposPrinter disconnect];
        return NO;
      }
      
      [eposPrinter addTextAlign:EPOS2_ALIGN_CENTER];
      [eposPrinter addText:[NSString stringWithFormat:@"\n This is sample print text to verify \n printer is connected successfully \n \n \n"]];
      if (result != EPOS2_SUCCESS) {
        return NO;
      }
      
      result = [eposPrinter addCut:EPOS2_CUT_FEED];
      if (result != EPOS2_SUCCESS) {
        return NO;
      }
      result = [eposPrinter sendData:EPOS2_PARAM_DEFAULT];

      if (result != EPOS2_SUCCESS) {
        [eposPrinter disconnect];
        return NO;
      }
      return YES;
    }
}

- (void)disconnectCurrentPrinter
{
      if (eposPrinter == nil) {
        return;
      }
      [eposPrinter disconnect];
}

- (void)dispPrinterWarnings:(Epos2PrinterStatusInfo *)status
{
  if (status == nil) {
    return;
  }

  if (status.paper == EPOS2_PAPER_NEAR_END) {
  }
  
  if (status.batteryLevel == EPOS2_BATTERY_LEVEL_1) {
      
  }
}

- (BOOL)isPrintable:(Epos2PrinterStatusInfo *)status
{
    
  if (status == nil) {
    return NO;
  }
  
  if (status.connection == EPOS2_FALSE) {
    return NO;
  }
  else if (status.online == EPOS2_FALSE) {
    return NO;
  }
  else {
    ;//print available
  }
  
  return YES;
}

- (void)onDiscovery:(Epos2DeviceInfo *)deviceInfo {

}

- (void)onPtrStatusChange:(Epos2Printer *)printerObj eventType:(int)eventType {

}

- (void)onPtrReceive:(Epos2Printer *)printerObj code:(int)code status:(Epos2PrinterStatusInfo *)status printJobId:(NSString *)printJobId {

}

- (void) onGetPrinterSetting:(int)code type:(int)Type value:(int)value{
    NSLog(@"value--%d",value);
}

- (void)onSetPrinterSetting:(int)code {
    if(code != EPOS2_CODE_SUCCESS){
        NSLog(@"print setting set error");
        return;
    }else{
        NSLog(@"print setting set succesfully");
    }
}

@end

