#include <avr/wdt.h>
#include "switch.h"
#include "mpu.h"
#include "sdt.h"
#include "alarm.h"
#include "step.h"
#define TIMEOUT WDTO_8S

void setup() {
  Serial.begin(9600);
  //while(!Serial);
  wdt_enable(TIMEOUT);
  /*init the time*/
  //time_rtc_init(16, 9, 17, 7, 16, 7, 14);  // just for test
  time_t_init();
  timeBefore = now();
  newDayTime = now();
  /*reset the switch part*/
  switch_init();
  /*reset the mpu part*/
  while (!mpu_init())
  {
    //Serial.println("mpu failed!");
  }
  /*load the alarm*/
  alarm_load();
  /*init the sdt*/
  sdt_init();
  /*init the step*/
  step_init(0);
  Serial.println("reset");
}

void loop() {
  /*reset the watchDog*/
  wdt_reset();
  /*get the switch state*/
  if ((millis() - buttonTime) > 25) switch_judge();
  /*control the master switch*/
//  Serial.print("before:");
//  Serial.print(switchStateMode1b);
//  Serial.print("  now:");
//  Serial.println(switchStateMode1);
  if (switchStateMode1 && !switchStateMode1b)
  {
    //关机处理
    //不发光
    switchLED.setPixelColor(0, switchLED.Color(0, 0, 0));
    switchLED.show();
    switchLED.setPixelColor(2, switchLED.Color(0, 0, 0));
    switchLED.show();
    if (!sdtState)
    {
      sdt_save(0, sdtStartTime);
    }
    step_init(0);
  }
  else if (!switchStateMode1 && switchStateMode1b)
  {
    //进入开机状态
    //发光
    switchLED.setPixelColor(0, switchLED.Color(255, 0, 0));
    switchLED.show();
    timeBefore = now();
    newDayTime = now();
    sdt_init();
    step_init(0);
  }
  else if (!switchStateMode1 && !switchStateMode1b)
  {
    switchLED.setPixelColor(0, switchLED.Color(255, 0, 0));
    switchLED.show();
    /*check for the now day*/
    if ((now() - newDayTime) > 60)  is_new_day();
    /*check the alarm*/
    alarm_check();
    /*tone check*/
    alarmHandler.to_alarm();
    /*init the mpu*/
    if (!dmpReady) return;
    fifoCount = mpu.getFIFOCount();
    if (fifoCount == 1024)
    {
      mpu.resetFIFO();
      //Serial.println(F("FIFO overflow!"));
    }
    else
    {
      /*get the mpu data*/
      mpu_get();
      /*detect the step*/
      step_detect();
      /*make the step cache*/
      step_cache();
      /*judge the sdt state*/
      if ((millis() - sdtTime) > 200) sdt_judge();
      /*make the sdt cache*/
      sdt_cache();
    }
  }
  if (switchStateMode2)
  {
    //发光
    alarmHandler.stop_tone_now();
    switchLED.setPixelColor(1, switchLED.Color(255, 255, 0));
    switchLED.show();
  }
  else
  {
    //不发光
    switchLED.setPixelColor(1, switchLED.Color(0, 0, 0));
    switchLED.show();
  }
  if (fffff&&millis()-fffft>100)
  {
    switchLED.setPixelColor(3, switchLED.Color(0, 0, 0));
    switchLED.show();
    fffff=0;
  }
  else if(fffff&&millis()-fffft<100)
  {
    switchLED.setPixelColor(3, switchLED.Color(0, 255, 0));
    switchLED.show();
  }
}

void is_new_day()
{
  timeBefore=newDayTime;
  newDayTime = now();
  if (day(now()) != day(timeBefore))
  {
    //step
    EEPROM.update(13,0);
    EEPROM.update(14,0);
    EEPROM.update(15,0);
    EEPROM.update(16,0);
    step_init(2);
    //alarm
    for (int i = 0; i < ALARM_NUM; i++)
      alarms[i].repeatAllow = 1;
    if (!sdtState)
    {
      sdt_save(0, sdtStartTime);
    }
    sdt_init();
  }
}
