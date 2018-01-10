package com.example.lycoris.smartbelt.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclair D'Amour on 2016/9/5.
 */
public class BaseBLEHandler {
    public final static  int BLE_CMD_NUM =19;


    private final int[] encodeTable= { 1, 2, 4, 8, 16, 32, 64, 128 };
    private final int[] numOfParam=  { 7, 1, 0, 1, 8, 0, 1, 2, 1, 0, 1, 7, 0, 1, 3, 1, 5, 1, 3,  1 };
    private final int[] numOfBytes=  { 7, 1, 1, 3, 9, 1, 1, 2, 1, 1, 3, 7, 1, 1, 2, 1, 5, 2, 2,  1 };



    private int[][][] cutTable= new int[][][] {
            { { 5, 1, 3 },  { 2, 1, 0 }, { 7, 2, 0 }, { 7, 3, 0 }, { 7, 4, 0 }, { 7, 5, 0 }, { 7, 6, 0 }, { 7, 7, 0 } },
            { { 1, 0, 1 } },
            { { 0, 0, 0 } },
            { { 14, 1, 0 } },
            { { 6, 1, 4 }, { 3, 1, 0 }, { 7, 2, 0 }, { 7, 3, 0 }, { 7, 4, 0 }, { 7, 5, 0 }, { 7, 6, 0 }, { 14, 7, 0 } },
            { { 0, 0, 0 } },
            { { 1, 0, 1 } },
            { { 1, 0, 1 }, { 6, 1, 0 } },
            { { 1, 0, 1 } },
            { { 0, 0, 0 } },
            { { 14, 1, 0 } },
            { { 7, 1, 4 }, { 3, 1, 0 }, { 7, 2, 0 }, { 7, 3, 0 }, { 7, 4, 0 }, { 7, 5, 0 }, { 7, 6, 0 } },
            { { 0, 0, 0 } },
            { { 1, 0, 1 } },
            { { 4, 1, 4 }, { 3, 1, 3 }, { 2, 1, 0 } },
            { { 1, 0, 1 } },
            { { 3, 1, 1 }, { 0, 1, 0 }, { 7, 2, 0 }, { 7, 3, 0 }, { 6, 4, 0 } },
            { { 2, 1, 0 } },
            { { 5, 1, 3 }, { 2, 1, 1 }, { 0, 1, 0 } },
            { { 1, 0 , 0 } }
    };



    public boolean decode(char[] receivedInfo, int[] decodedInfo){
        int type=(int)(receivedInfo[0] & 0xfc) >> 2;
        if(type>20){
            error(0);
            return false;
        }
        decodedInfo[0]=type;
        if (numOfParam[type] == 0) {
            return true;
        }else{
            for (int pos = 0; pos < numOfParam[type]; pos++){
                if(cutTable[type][pos][0]!=14){
                    decodedInfo[pos + 1] = get_part(cutTable[type][pos][0], receivedInfo[cutTable[type][pos][1]], cutTable[type][pos][2]);
                }else{
                    decodedInfo[pos + 1] = get_part(5, receivedInfo[cutTable[type][pos][1]], 0) * 64 + get_part(5, receivedInfo[cutTable[type][pos][1] + 1], 0);
                }
            }
            return true;
        }

    }

    public char[] encode(int[] encodeInfo){
        if (encodeInfo[0] < 0 || encodeInfo[0] > BLE_CMD_NUM)
            return "".toCharArray();
        char[] temp={0,0,0,0,0,0,0,0,0,0};
        temp[0] = set_part(encodeInfo[0], 2);
        for (int pos = 0; pos < numOfParam[encodeInfo[0]]; pos++) {
            if (cutTable[encodeInfo[0]][pos][0] != 14) {
                temp[cutTable[encodeInfo[0]][pos][1]] += set_part(encodeInfo[pos + 1], cutTable[encodeInfo[0]][pos][2]);
            } else {
                temp[cutTable[encodeInfo[0]][pos][1]] = set_part(encodeInfo[pos + 1] / 64, 0);
                temp[cutTable[encodeInfo[0]][pos][1] + 1] = set_part(encodeInfo[pos + 1] % 64, 0);
            }
        }
        return temp;
    }

    public void error(int code){
        int[] encodeInfo = { 20, code };
        char[] errorInfo=encode(encodeInfo);

    }

    private byte get_part(int high, char c, int low){
        if (high == 7 && low == 0)
            return (byte)(c);
        byte parter = 0; //to fetch high~low bits
        for (int i = high; i >= low; i--)
            parter += encodeTable[i];
        return (byte)((c & parter) >> low);
    }

    private char set_part(int info, int low){
        return (char)((char)info << low);
    }

    public String cutEncode(int[] encodeCode){
        char[] tempCut=encode(encodeCode);
        List<Integer> x=new ArrayList<Integer>();
        for(int i=0;i<tempCut.length;i++){
            x.add((int)tempCut[i]);
        }

        String temp=String.copyValueOf(tempCut).substring(0,numOfBytes[encodeCode[0]])+Character.toString((char)127);
        return temp;

    }

    public int[] cutDecode(String encodeInfo){
        String realEncodeInfo=encodeInfo.substring(0,encodeInfo.indexOf(Character.toString((char)127)));
        int[] decodeinfo=new int[10];
        decode(realEncodeInfo.toCharArray(),decodeinfo);
        int[] cutdecode=new int[numOfParam[decodeinfo[0]]+1];
        for(int i=0;i<cutdecode.length;i++){
            cutdecode[i]=decodeinfo[i];
        }
        return cutdecode;
    }

}
