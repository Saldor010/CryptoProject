package io.github.saldor010;

import io.github.saldor010.Crypto.DecryptStatus;

public class Decrypt {
	private static float progress;
	private static byte imageDataStart;
	private static DecryptStatus status;
	public void setImageDataStart(byte im) {
		imageDataStart = im;
	}
	public float getProgress() {
		return progress;
	}
	public DecryptStatus getStatus() {
		return status;
	}
	public byte[] decryptMessage(int offset,int startingPoint,int expectedLength,byte[] workingImage) {
		status = DecryptStatus.WORKING;
		byte[] retrievedMessage = new byte[expectedLength];
		for (int i=startingPoint;i<startingPoint+expectedLength;i++) {
			if(i>startingPoint+expectedLength) {
				break;
			}
			//byte workingByteR = (byte) (newImage[(i*3*offset)+imageDataStart]&0xF);
			//byte workingByteG = (byte) (newImage[(i*3*offset)+1+imageDataStart]&0xF);
			//System.out.println(workingByteR);
			//System.out.println(workingByteG);
			byte workingByte = 0b0;
			for(int j=0;j<8;j++) {
				byte spliceMask = 0b1;
				//System.out.println(newImage[(i*8)-8+j+imageDataStart] & spliceMask);
				try {
					workingByte = (byte) (workingByte + ((workingImage[(i*8*offset)-8+(j*offset)+imageDataStart] & spliceMask)<<j));
				} catch(ArrayIndexOutOfBoundsException e) {
					status = DecryptStatus.OUTOFBOUNDS;
					return new byte[0];
				}
				//System.out.println(j + "| " + (newImage[(i*8*offset)-8+(j*offset)+imageDataStart] & spliceMask) + " | " + workingByte);
				//byte byteToBeSpliced = (byte) ((messageInBytes[i]>>(j+1)) & spliceMask);
				//newImage[(i*8)-8+j+imageDataStart] = (byte)((byte)(newImage[(i*8)-8+j+imageDataStart] & (byte)(254)) + byteToBeSpliced);
			}
			if(workingByte < 0) {
				//workingByte = (byte)(workingByte + 0xFF);
			}
			//System.out.println(workingByte);
			//System.out.println(new String(new byte[] {workingByte}));
			//System.out.println(new String(new byte[] {(byte) 0x01}));
			retrievedMessage[i-startingPoint] = workingByte;
			progress = ((float)i)/((float)expectedLength);
		}
		status = DecryptStatus.SUCCESS;
		return retrievedMessage;
	}
}
