#pragma once
#include <SPI.h>
#include "BLEhandler.h"
#include "step.h"
#include "swtich.h"
#include "time.h"
#include "sdt.h"
#include "trans.h"
#include "alarm.h"
#include <stdio.h>
//change the serial!
#define mySerial Serial

void notify_handler()
{
  if (mySerial.available() > 0)  //如果串口有数据输入
  {
    bool alarmState;
    char c[] = "ALARM_0.TXT";
    //decode info
    String msgTemp = "";
    char c127 = 127;
    msgTemp = mySerial.readStringUntil(c127); //读取到换行符
    int ConPos = msgTemp.indexOf("Connected\r\n");
    if (ConPos != -1)
    {
      msgTemp.remove(ConPos, ConPos + 10);
    }
    //    Serial.println(msgTemp);
    int d[10]; //decoded int array
    int arrTemp[10] = { 0 }; //int array to encode
    int pointer;// step and sdt use.
    byte temp;
    String t;
    bleHandler.decode(msgTemp, d);
    switch (d[0])
    {
      case 0://TIME SYNC
        time_rtc_init(d[2], d[3], d[4], d[1], d[5], d[6], d[7]);//(int _year, int _month, int _day, int _week, int _hour, int _min, int _sec)
        {
          //TIME SYNC SUCCESS
          arrTemp[0] = 1;
          arrTemp[1] = 1;
          mySerial.println(bleHandler.encode(arrTemp));
          break;
        case 2://STEPS SYNC
          //STEPS SYNC HEADER
          arrTemp[0] = 3;
          EEPROM.get(17, arrTemp[1]);
          mySerial.println(bleHandler.encode(arrTemp));
          //STEPS SYNC INFO
          EEPROM.get(31, pointer);
          for (int i = 0; i < arrTemp[1]; i++)
          {
            stepInfoRead(pointer, t);
            mySerial.println(t);
            pointer += 9;
          }
          //STEP SYNC STOPPER
          arrTemp[0] = 5;
          arrTemp[1] = 1;
          mySerial.println(bleHandler.encode(arrTemp));
          break;
        case 6://STEPS SYNC SUCCESS
          if (d[1])
          {
            //修改step数据的指针
            EEPROM.put(31, pointer);
            //清空信息条数
            EEPROM.update(17, 0);
            EEPROM.update(18, 0);
          }
          break;
        case 7://STEPS SETTINGS
          stepTarget = d[2];
          stepAlarmOrNot = (bool)d[1];

          temp = EEPROM.read(12);
          temp = temp & (d[1] << 5);
          temp = temp & 240 + d[2];
          EEPROM.update(12, temp);

          //STEPS SETTINGS SUCCESS
          arrTemp[0] = 8;
          arrTemp[1] = 1;
          mySerial.println(bleHandler.encode(arrTemp));
          break;
        case 9://SENDENTARY SYNC
          //SENDENTARYP SYNC HEADER
          arrTemp[0] = 10;
          EEPROM.get(34, arrTemp[1]);
          mySerial.println(bleHandler.encode(arrTemp));

          //SENDENTARY SYNC INFO
          EEPROM.get(44, pointer);
          for (int i = 0; i < arrTemp[1]; i++)
          {
            sdtInfoRead(pointer, t);
            mySerial.println(t);
            pointer += 7;
          }
          //TIME SYNC STOPPER
          arrTemp[0] = 12;
          arrTemp[1] = 1;
          mySerial.println(bleHandler.encode(arrTemp));
          break;
        case 13://SENDENTARY SYNC SUCCESS
          if (d[1])
          {
            //修改sdt数据的指针
            EEPROM.put(44, pointer);
            //清空信息条数
            EEPROM.update(34, 0);
            EEPROM.update(34, 0);
          }
          break;
        case 14:
          sdtAlarmOrNot = d[1];
          sdtContinualOrNot = d[2];
          sdtDuration = d[3];

          temp = d[1]*16+d[2]*d[3];
          EEPROM.update(33,temp);

          //SENDENTARY SETTINGS SUCCESS
          arrTemp[0] = 15;
          arrTemp[1] = 1;
          mySerial.println(bleHandler.encode(arrTemp));
          break;
        case 16:
          //ALARM UPDATE
          EEPROM.update(d[1] * 4,2 + d[2]); switch(default is on) and repeatOrNot
          EEPROM.update(d[1] * 4 + 1,d[5]); //mode
          EEPROM.update(d[1] * 4 + 2,d[3]); //hour
          EEPROM.update(d[1] * 4 + 3,d[4]); //minute
          //ALARM SETTINGS SECCESS
          arrTemp[0] = 18;
          arrTemp[1] = d[1];
          arrTemp[2] = 0;
          arrTemp[3] = 1;
          mySerial.println(bleHandler.encode(arrTemp));
          break;
        case 17:
          temp = EEPROM.read(d[1] * 4);
          if (temp <= 1)
            temp += 2;
           else temp -=2;
          EEPROM.update(d[i] * 4, temp);
          //ALARM SETTINGS SECCESS
          arrTemp[0] = 18;
          arrTemp[1] = d[1];
          arrTemp[2] = 1;
          arrTemp[3] = 1;
          mySerial.println(bleHandler.encode(arrTemp));
          break;
        default:
          //ERROR:INVALID COMMAND CODE
          arrTemp[0] = 19;
          arrTemp[1] = 0;
          mySerial.println(bleHandler.encode(arrTemp));
          break;
        }
    }
  }
