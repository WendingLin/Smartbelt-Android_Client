#define SDT_ACCEL_NUM 80
#define SDT_PRIOR_NUM 40
#define SDT_ACCEL_POS_TH 1000
#define SDT_ACCEL_NEG_TH -500
#define SDT_ACCEL_INTERVAL 10

int ipCirculate(int sdtAccelP, int i){
  return (sdtAccelP+i) % SDT_ACCEL_NUM;
}

bool sdtCheck(double* sdtAccel,int sdtAccelP){
	int max=0;
	int min=0;
	int temp_max=0;
	int temp_min=0;
	int interval=SDT_PRIOR_NUM+1;
	int max_location=0;
	int min_location=0;
	for (int j=0; j<80; j++){
		Serial.println(sdtAccel[ipCirculate(sdtAccelP, j)]);
	}
	for (int i=1; i<=40; i+=2){
		if(sdtAccel[ipCirculate(sdtAccelP, i)]<sdtAccel[ipCirculate(sdtAccelP, i+1)]){
			temp_max=sdtAccel[ipCirculate(sdtAccelP, i+1)];
			temp_min=sdtAccel[ipCirculate(sdtAccelP, i)];
			if(temp_max>max){
				max=temp_max;
				max_location=i+1;
			}
			if(temp_min<min){
				min=temp_min;
				min_location=i;				
			}
		}else{
			temp_min=sdtAccel[ipCirculate(sdtAccelP, i+1)];
			temp_max=sdtAccel[ipCirculate(sdtAccelP, i)];
			if(temp_max>max){
				max=temp_max;
				max_location=i;
			}
			if(temp_min<min){
				min=temp_min;
				min_location=i+1;				
			}			
		}
	}
	interval=(max_location-min_location+SDT_PRIOR_NUM) % SDT_PRIOR_NUM;
	if(interval<=SDT_PRIOR_NUM & max>=SDT_ACCEL_POS_TH & min<=SDT_ACCEL_NEG_TH) 
		return true;
	else
		return false;
}



