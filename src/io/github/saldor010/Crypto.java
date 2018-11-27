package io.github.saldor010;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

public class Crypto {
	private static Path workingPath;
	private static byte[] workingFile;
	private static byte imageDataStart;
	private static byte[] newImage;
	public static float progress;
	private static byte[] spliceMaskArray;
	private static boolean allAPartOfThePlan;
	private static int currentThreadBeingDefined;
	public enum DecryptStatus {
		WORKING, OUTOFBOUNDS, SUCCESS
	}
	private static DecryptStatus localDecryptStatus;
	public static DecryptStatus getStatus() {
		return localDecryptStatus;
	}
	public static byte[] decryptMessage(int offset,int expectedLength,int threadCount) {
		localDecryptStatus = DecryptStatus.WORKING;
		byte[] retrievedMessage = new byte[expectedLength];
		Decrypt[] run_ = new Decrypt[threadCount];
		byte[][] run_results = new byte[threadCount][];
		boolean[] run_complete = new boolean[threadCount];
		int[] run_nibbles = new int[threadCount];
		allAPartOfThePlan = false;
		
		int new_expectedLength = expectedLength;
		int new_threadCount = threadCount;
		for(int i=0;i<threadCount;i++) {
			int divisor = (int) Math.ceil(new_expectedLength/new_threadCount);
			run_nibbles[i] = divisor;
			new_expectedLength = new_expectedLength - divisor;
			new_threadCount = new_threadCount - 1;
		}
		
		for(int i=0;i<threadCount;i++) {
			currentThreadBeingDefined = i;
			Runnable threadExecute = new Runnable() {
				public void run() {
					int localCurrentThreadBeingDefined = currentThreadBeingDefined;
					run_[localCurrentThreadBeingDefined] = new Decrypt();
					run_[localCurrentThreadBeingDefined].setImageDataStart(imageDataStart);
					int startingPoint = 0;
					for(int j=0;j<threadCount;j++) {
						if(j<localCurrentThreadBeingDefined) {
							startingPoint = startingPoint + run_nibbles[j];
						}
					}
					byte[] run_result = run_[localCurrentThreadBeingDefined].decryptMessage(offset,startingPoint,run_nibbles[localCurrentThreadBeingDefined],newImage);
					if(run_[localCurrentThreadBeingDefined].getStatus().equals(DecryptStatus.OUTOFBOUNDS)) {
						//System.out.println("YEE");
						localDecryptStatus = DecryptStatus.OUTOFBOUNDS;
					} else {
						run_results[localCurrentThreadBeingDefined] = run_result;
					}
					run_complete[localCurrentThreadBeingDefined] = true;
				}
			};
			new Thread(threadExecute).start();
			try {
				TimeUnit.MILLISECONDS.sleep((long)50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Timer refresh = new Timer();
		TimerTask refresh_task = new TimerTask() {
			@Override
			public void run() {
				boolean goCode = true;
				float new_progress = 0;
				for(int i=0;i<threadCount;i++) {
					if(run_complete[i] == false) {
						goCode = false;
					}
					new_progress = (new_progress + (run_[i].getProgress()/threadCount));
				}
				progress = new_progress;
				if(goCode) {
					if(localDecryptStatus == DecryptStatus.OUTOFBOUNDS) {
						//System.out.println("REE");
						allAPartOfThePlan = true;
					} else {
						for(int i=0;i<threadCount;i++) {
							int startingPoint = 0;
							for(int j=0;j<threadCount;j++) {
								if(j<i) {
									startingPoint = startingPoint + run_nibbles[j];
								}
							}
							for(int j=0;j<run_nibbles[i];j++) {
								byte screwByThisMuch = (byte) 0;
								/*if(j>12000 && j<50000) {
									screwByThisMuch = (byte) Math.floor(Math.random()*2);
								}*/
								retrievedMessage[startingPoint+j] = (byte) (run_results[i][j] + screwByThisMuch);
							}
						}
						localDecryptStatus = DecryptStatus.SUCCESS;
						allAPartOfThePlan = true;
					}
				}
			}
		};
		
		refresh.schedule(refresh_task,(long)100,(long)100);
		while(allAPartOfThePlan != true) {
			try {
				TimeUnit.MILLISECONDS.sleep((long)50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		refresh.cancel();
		allAPartOfThePlan = false;
		return retrievedMessage;
		/*
		byte[] retrievedMessage = new byte[expectedLength];
		for (int i=0;i<expectedLength;i++) {
			//byte workingByteR = (byte) (newImage[(i*3*offset)+imageDataStart]&0xF);
			//byte workingByteG = (byte) (newImage[(i*3*offset)+1+imageDataStart]&0xF);
			//System.out.println(workingByteR);
			//System.out.println(workingByteG);
			byte workingByte = 0b0;
			for(int j=0;j<8;j++) {
				byte spliceMask = 0b1;
				//System.out.println(newImage[(i*8)-8+j+imageDataStart] & spliceMask);
				workingByte = (byte) (workingByte + ((newImage[(i*8*offset)-8+(j*offset)+imageDataStart] & spliceMask)<<j));
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
			retrievedMessage[i] = workingByte;
			progress = ((float)i)/((float)expectedLength);
		}
		return retrievedMessage;
		*/
	}
	public static void encryptMessage(int offset,byte[] messageInBytes) {
		for(int i=0;i<messageInBytes.length;i++) {
			//byte workingByteR = (byte) (messageInBytes[i]&0xF);
			//System.out.println(messageInBytes[i]);
			//System.out.println(messageInBytes[i]&0xF);
			//newImage[(i*3*offset)+imageDataStart] = (byte) (newImage[(i*3*offset)+imageDataStart]-(newImage[(i*3*offset)+imageDataStart]&0xF)+workingByteR);
			//byte workingByteG = (byte) (messageInBytes[i]>>4);
			//System.out.println(messageInBytes[i]>>4);
			//newImage[(i*3*offset)+1+imageDataStart] = (byte) (newImage[(i*3*offset)+1+imageDataStart]-(newImage[(i*3*offset)+1+imageDataStart]>>4)+workingByteG);
			for(int j=0;j<8;j++) {
				byte spliceMask = 0b1;
				byte byteToBeSpliced = (byte) ((messageInBytes[i]>>j) & spliceMask);
				/*boolean printit = false;
				if(newImage[(i*8)-8+j+imageDataStart] >= 0x80 && newImage[(i*8)-8+j+imageDataStart] < 0x90) {
					System.out.println(j + " #1: " + newImage[(i*8)-8+j+imageDataStart]);
					printit = true;
				}*/
				newImage[(i*8*offset)-8+(j*offset)+imageDataStart] = (byte)((byte)(newImage[(i*8*offset)-8+(j*offset)+imageDataStart] & (byte)(254)) + byteToBeSpliced);
				/*if(printit == true) {
					System.out.println(j + " #2: " + newImage[(i*8)-8+j+imageDataStart]);
				}*/
			}
			progress = ((float)i)/((float)messageInBytes.length);
		}
		try {
			Files.write(workingPath,newImage);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"Could not continue writing to output file!","Stenographer",JOptionPane.ERROR_MESSAGE);
		}
	}
	public static boolean loadFile(String filePath) {
		workingPath = Paths.get(filePath);
		try {
			workingFile =  Files.readAllBytes(workingPath);
			newImage = workingFile;
			imageDataStart = workingFile[0x0A];
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			//System.out.println("Not a valid file path! The file path is relative to where the jar file is located, moving them both to the same folder may help.");
			//System.exit(0);
			//JOptionPane.showMessageDialog(null,"Invalid input file path!\n"+filePath+"\nDid you forget the file extension?","Stenographer",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	public static void main(String[] args) {
		spliceMaskArray = new byte[] {
			0b1,
			0b10,
			0b100,
			0b1000,
			0b10000,
			0b100000,
			0b1000000,
			0b1000000,
		};
		//System.out.println((0xAB)&0xF);
		//Runnable GUIHandlingThread = new Runnable() {
		//	public void run() {
				GUIHandler newGUI = new GUIHandler();
		//	}
		//};
		//new Thread(GUIHandlingThread).start();
		//if(args.length < 4) {
		//	System.out.println("Missing arguments! Cryptographer requires a file path (1), enc/dec for encryption or decryption (2), the offset key (3), and either the message to encrypt, or the expected length of the message you're decrypting (4).");
		//	//System.exit(0);
		//} else {
		/*
			workingPath = Paths.get(args[0]);
			try {
				workingFile =  Files.readAllBytes(workingPath);
				newImage = workingFile;
				imageDataStart = workingFile[0x0A];
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Not a valid file path! The file path is relative to where the jar file is located, moving them both to the same folder may help.");
				System.exit(0);
			}
			int newOffset = 0;
			try {
				newOffset = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.out.println("Malformed argument! The third (3rd) argument must be a positive integer. (If in doubt, set it to 1)");
				System.exit(0);
			}
			if(newOffset<=0) {
				System.out.println("Malformed argument! The third (3rd) argument must be a positive integer. (If in doubt, set it to 1)");
				System.exit(0);
			}
			if(args[1].toLowerCase().equals("enc")){
				String newMessage = "";
				for (int i=2;i<args.length;i++) {
					newMessage = newMessage + " " + args[i];
				}
				//encryptMessage(newOffset,newMessage);
			} else if(args[1].toLowerCase().equals("dec")){
				int expected = 0;
				try {
					expected = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					System.out.println("Malformed argument! The fourth (4th) argument must be a positive integer. (If in doubt, set it to 20 and increase from there)");
					System.exit(0);
				}
				if(expected<=0) {
					System.out.println("Malformed argument! The fourth (4th) argument must be a positive integer. (If in doubt, set it to 20 and increase from there)");
					System.exit(0);
				}
				String weGotItBois = decryptMessage(newOffset,expected);
				System.out.println(weGotItBois);
			} else {
				System.out.println("Malformed argument! The second (2nd) argument must be enc or dec (for encryption or decryption).");
				System.exit(0);
			}
		//}
		 */
	}
}
