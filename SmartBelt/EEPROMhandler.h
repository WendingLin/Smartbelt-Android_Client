#pragma once
#include <EEPROM.h>

void stepInfoWrite(String s)
{
  short p;
  EEPROM.get(31,p);
  p += 9;
  for (int i = 0; i < 9; i++)
    EEPROM.update(p + i, s[i]);
}

void stepInfoRead(int p, String& s)
{
  s = "";
  for (int i = 0; i < 9; i++)
    s += (char)EEPROM.read(p + i);
}

void stepClearCache()
{
  for (int i = 0; i < 12; i++)
    EEPROM.update(19 + i,0);
}

void sdtInfoWrite(String s)
{
  short p;
  EEPROM.get(44, p);
  p += 7;
  for (int i = 0; i < 7; i++)
    EEPROM.update(p + i, s[i]);
}

void sdtClearCache()
{
  for (int i = 0; i < 8; i++)
    EEPROM.update(36 + i,0);
}

void sdtInfoRead(int p, String& s)
{
  s = "";
  for (int i = 0; i < 7; i++)
    s += (char)EEPROM.read(p + i);
}
