#define SDT_ACCEL_NUM 80
#define SDT_PRIOR_NUM 80
#define SDT_ACCEL_POS_TH 1000
#define SDT_ACCEL_NEG_TH -500
#define SDT_ACCEL_INTERVAL 10

int ipCirculate(int sdtAccelP, int i){
  return (sdtAccelP+i) % SDT_ACCEL_NUM;
}

bool posthCheck(double accelValue){
  if(accelValue>SDT_ACCEL_POS_TH)
    return true;
  else
    return false;
}

bool negthCheck(double accelValue){
  if(accelValue<SDT_ACCEL_NEG_TH)
    return true;
  else
    return false;
}

bool intervalCheck(int interval){
  if(interval<SDT_ACCEL_INTERVAL)
    return true;
  else
    return false;
}
bool sdtCheck(double* sdtAccel,int sdtAccelP){
	bool inposTh=false;
	bool innegTh=false;
	int interval=SDT_PRIOR_NUM+1;
	int accelCount=0;
  for (int j=sdtAccelP+1; j<80; j++){
    Serial.println(sdtAccel[ipCirculate(sdtAccelP, j)]);
  }
  for (int j=0; j<sdtAccelP+1; j++){
    Serial.println(sdtAccel[ipCirculate(sdtAccelP, j)]);
  }
	for (int i=1; i<=40; i++){
		if(inposTh==false){
			inposTh=posthCheck(sdtAccel[ipCirculate(sdtAccelP, i)]);
			if(inposTh==true){
				accelCount=i;
				continue;
			}else{
				continue;
			}	
		}else{
			innegTh=posthCheck(sdtAccel[ipCirculate(sdtAccelP, i)]);
			if(innegTh==true){
				interval=accelCount-i;
				if(intervalCheck(interval)==true){
					break;
				}else{
					innegTh=false;
					continue;
				}
			}else{
				continue;
			}	
		}		
	}
	if(interval>SDT_PRIOR_NUM)
		return false;
	else
		return true;
}



