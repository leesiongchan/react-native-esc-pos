//
//  Copyright Seiko Epson Corporation 2014-2018 All rights reserved.
//
//  Ver.2.2.1
//

enum EposEasySelectDeviceType : int {
    EPOS_EASY_SELECT_DEVTYPE_TCP = 0,
    EPOS_EASY_SELECT_DEVTYPE_BLUETOOTH,
};

enum EposEasySelectParseTimeout : int {
    PARSE_NFC_TIMEOUT_DEFAULT = 500,
};

#ifdef __OBJC__
#import <CoreLocation/CoreLocation.h>
#import <Foundation/Foundation.h>
#import <CoreNFC/CoreNFC.h>

@interface EposEasySelectInfo : NSObject

@property (assign, nonatomic) int deviceType;
@property (strong, nonatomic) NSString *printerName;
@property (strong, nonatomic) NSString *macAddress;
@property (strong, nonatomic) NSString *target;

@end

@interface EposEasySelect : NSObject

- (EposEasySelectInfo *)parseQR:(NSString *)data;
- (EposEasySelectInfo *)parseBeacon:(CLBeacon *)beacon;
- (NSString *)createQR:(NSString *)printerName DeviceType:(int)deviceType MacAddress:(NSString*)macAddress;

- (NSArray<EposEasySelectInfo *> *)parseNFC:(NSArray<NFCNDEFMessage *> *)message  timeout:(long)timeout NS_AVAILABLE_IOS(11_0);

@end
#endif  /*__OBJC__*/

