#define SDT_ACCEL_NUM 80
#define SDT_PRIOR_NUM 40
#define SDT_ACCEL_POS_TH 20
#define SDT_ACCEL_NEG_TH -20
#define SDT_ACCEL_INTERVAL 10
bool sdtCheck(double* sdtAccel,int sdtAccelP){
	bool inposTh=false;
	bool innegTh=false;
	int interval=SDT_PRIOR_NUM+1;
	int accelCount=0;
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

int ipCirculate(int sdtAccelP, int i){
	return (sdtAccelP+i) mod SDT_ACCEL_NUM;
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

