#pragma once
#include "EEPROMhandler.h"
#include "alarmHandler.h"
#include "trans.h"
#include "time.h"

#define ALARM_NUM 3

void alarm_load()
{
  for (int i = 0; i < ALARM_NUM; i++)
  {
    alarms[i].openOrNot = EEPROM.read(i * 4) / 10;
    alarms[i].repeatOrNot = EEPROM.read(i * 4) % 10;
    byte dayMode = EEPROM.read(i * 4 + 1);
    for (int j = 7; j > 0; j++)
    {
      alarms[i].mode[6] = dayMode % 2;
      dayMode %= 2;
    }
    alarms[i].h = EEPROM.read(i * 4 + 2);
    alarms[i].m = EEPROM.read(i * 4 + 3);
    alarms[i].repeatAllow = 1;
    if (alarms[i].repeatOrNot)
      alarms[i].repeatOnce = alarms[i].repeatTwice = 1;
    else
      alarms[i].repeatOnce = alarms[i].repeatTwice = 0;
  }
}

void alarm_check()
{
  for (int i = 0; i < ALARM_NUM; i++)
  {
    bool onAlarm = false;
    if (!alarms[i].openOrNot ||  !alarms[i].repeatAllow)
      continue;
    if (time_compare(alarms[i].h, alarms[i].m, weekday(now()) * alarms[i].mode[weekday(now()) - 1]))
    {
      onAlarm = true;
    }
    else if (alarms[i].repeatOrNot)
    {
      if (alarms[i].repeatOnce && time_compare(alarms[i].h, alarms[i].m, weekday(now()) * alarms[i].mode[weekday(now()) - 1], 1) )
      {
        //        Serial.print(alarms[i].repeatOrNot);
        //        Serial.println("  repeat detect 1");
        alarms[i].repeatOnce = 0;
        onAlarm = true;
        alarmHandler.reset_tone_now();
      }
      else if (alarms[i].repeatTwice && time_compare(alarms[i].h, alarms[i].m, weekday(now()) * alarms[i].mode[weekday(now()) - 1], 2) )
      {
        //        Serial.println("  repeat detect 2");
        alarms[i].repeatTwice = 0;
        onAlarm = true;
        alarmHandler.reset_tone_now();
      }
    }
    if (onAlarm && alarmHandler.get_tone_now() != i)
    {
      //      Serial.print(alarmHandler.get_tone_now());
      //      Serial.print(" alarm tone No: ");
      //      Serial.println(i);
      alarmHandler.start_alarm(i);
    }
  }
}

