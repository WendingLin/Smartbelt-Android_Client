#pragma once
#include <SPI.h>
#include "EEPROMhandler.h"
#include "time.h"
#include "alarmHandler.h"
#include "BLEhandler.h"

#define STEP_INTERVAL_MIN 300
#define STEP_INTERVAL_MAX 2000
#define STEP_CACHE_INTERVAL 10
#define STEP_CACHE_RECOVER_INTERVAL 5
#define TARGET_STEP_TONE_MODE 0
#define STEP_PEAK_MAX 8000

boolean fffff;
unsigned long int fffft;

int origStep[5], smoothStep[3];
int origStepPos = 0, smoothStepPos = 0;
//todayPos = 0;
int pkf[2], pkCount, thr[2], lastHour;
bool pkfFlag = 0;
unsigned long pkfTime, pkdTime;
time_t stepStart = 0;

unsigned long int stepCacheTime;
bool stepCacheOn = 0;

bool stepAlarmOrNot;
int stepTarget;
int stepToday;
bool stepAlarmFlag;

void step_noi_update()
{
  short step_noi;
  EEPROM.get(17, step_noi);
  EEPROM.put(17, step_noi + 1);
}

void step_save(unsigned long s, unsigned long e, int pkCount)
{
  if (pkCount < 10) return;
  //  Serial.print("  starttime:");
  //  Serial.print(s);
  //  Serial.print("  endtime:");
  //  Serial.print(e);
  //  Serial.print("  step:");
  //  Serial.println(pkCount);
  unsigned int params[9] = {4, year(e), month(e), day(e), hour(s), minute(s), hour(e), minute(e), pkCount};
  String s_temp;
  bleHandler.encode(s_temp, params);
  stepInfoWrite(s_temp);
  step_noi_update();
  stepClearCache();
}

void step_init(int flag)
{
  for (int i = 0; i < 5; i++)
    origStep[i] = 0;
  for (int i = 0; i < 3; i++)
    smoothStep[i] = 0;
  origStepPos = 0;
  smoothStepPos = 0;
  //  todayPos = 0;
  pkf[0] = pkf[1] = 0;
  pkCount = 0;
  thr[0] = thr[1] = 0;
  //  lastHour;
  pkfFlag = 0;
  pkfTime = 0;
  pkdTime = 0;
  stepStart = 0;

  stepCacheTime = 0;
  stepCacheOn = 0;

  if (flag == 1)
    return;

  if (flag == 2)
  {
    stepAlarmFlag = 1;
    EEPROM.update(12, EEPROM.read(12) | 16);
    //    Serial.print("ALARMFLAG1:");
    //    Serial.println(stepAlarmFlag);
    return;
  }

  stepAlarmFlag = (EEPROM.read(12) & 16) / 16;
  //  Serial.print("ALARMFLAG:");
  //  Serial.println(stepAlarmFlag);

  unsigned long s, e, stepCac;
  EEPROM.get(23, s);
  EEPROM.get(27, e);
  EEPROM.get(31, stepCac);
  step_save(s, e, stepCac);

  byte stepSetTemp = EEPROM.read(12);
  stepAlarmOrNot = (stepSetTemp & 32) / 32;
  stepTarget = 50;//(stepSetTemp & 15) * 1000; //此处暂时设置为50
  //  Serial.print("alarmornot:");
  //  Serial.println(stepAlarmOrNot);
  //  Serial.print("Target:");
  //  Serial.println(stepTarget);
};

void step_target_detect()
{
  Serial.println("target detect!");
  if (!stepAlarmOrNot)
    return;
  long stepToday;
  EEPROM.get(13,stepToday);
  if (pkCount + stepToday >= stepTarget && stepAlarmFlag)
  {
    //    Serial.println("alarm now!");
    alarmHandler.start_alarm(TARGET_STEP_TONE_MODE);
    stepAlarmFlag = 0;

    EEPROM.update(12,EEPROM.read(12) & 111); //对应位置1
  }
}

void step_cache()
{
  if (stepCacheOn && now() - stepCacheTime >= STEP_CACHE_INTERVAL && pkCount >= 10)
  {
    //    Serial.print("now:");
    //    Serial.print(now());
    //    Serial.print("  stepCacheTime");
    //    Serial.println(stepCacheTime);

    stepCacheTime = now();
    //Serial.println("cache!");
    EEPROM.put(19,stepStart);
    EEPROM.put(23,now());
    EEPROM.put(27,pkCount);
    step_target_detect();
  }
}

void step_detect()
{
  origStep[origStepPos] = aaWorld.z;
  smoothStep[smoothStepPos] = (origStep[origStepPos] + origStep[(origStepPos - 1) % 5] + origStep[(origStepPos - 2) % 5]) / 3;
  thr[0] = 0.25 * pkf[0] + 1000;
  thr[1] = 0.25 * pkf[1] + 1000;

  if (millis() - pkfTime > STEP_INTERVAL_MAX && pkfTime)
  {
    //此时退出一段步行状态
    step_save(stepStart, now(), pkCount);
    
    int stepToday;
    EEPROM.get(13,stepToday);
    EEPROM.put(13,stepToday + pkCount);

    step_init(1);
    step_target_detect();
  }

  //如果当前检测到的数据使峰值且小于最大阈值，则运行以下代码
  if (smoothStep[(smoothStepPos - 1) % 3] < STEP_PEAK_MAX && smoothStep[(smoothStepPos - 1) % 3] > smoothStep[(smoothStepPos - 2) % 3]  && smoothStep[(smoothStepPos - 1) % 3] > smoothStep[smoothStepPos])
  {
    pkdTime = millis() - pkfTime;
    //如果当前检测到的峰值之前存储过峰值信息，即此时已不再是某一段走步状态的起始，则运行以下代码
    if (pkf[0] != 0)
    {
      //如果此峰与前一峰值的时间间隔小于时间阈值，则取其中较大的一个，并更新时间；
      if (pkdTime < STEP_INTERVAL_MIN)
      {
        if (smoothStep[(smoothStepPos - 1) % 3] > thr[!pkfFlag] && smoothStep[(smoothStepPos - 1) % 3] > pkf[!pkfFlag])
        {
          pkf[!pkfFlag] = smoothStep[(smoothStepPos - 1) % 3];
          pkfTime = millis();
        }
      }
      //以下检测峰值条件：如果峰值大于阈值，则判断与前一峰值之间的时间间隔是否小于时间阈值，如果小于，则产生一合理峰值
      else
      {
        if (smoothStep[(smoothStepPos - 1) % 3] > thr[pkfFlag])
        {
          //此时产生了步数
          pkf[pkfFlag] = smoothStep[(smoothStepPos - 1) % 3];
          pkCount++;
          fffff = 1;
          fffft = millis();
          //          Serial.print("step:");
          //          Serial.println(pkCount);
          //step_target_detect();
          pkfTime = millis();
          pkfFlag = !pkfFlag;
        }
      }
    }
    //此时为某一段的第一步
    else
    {
      //the first peak appeared
      if (smoothStep[(smoothStepPos - 1) % 3] > thr[0])
      {
        pkf[0] = smoothStep[(smoothStepPos - 1) % 3];
        pkCount = 1;
        //        Serial.print("stepfirst:");
        //        Serial.println(pkCount);
        //step_target_detect();
        pkfTime = millis();
        stepStart = now();
        pkfFlag = 1;
        //cache power on
        stepCacheTime = now();
        stepCacheOn = 1;
        fffff = 1;
        fffft = millis();
      }
    }
  }
  //Serial.println("step_detect ended.");
  origStepPos = (origStepPos + 1) % 5;
  smoothStepPos = (smoothStepPos + 1) % 3;
}
