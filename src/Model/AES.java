package Model;

import Model.SBOX.*;
import static Model.SBOX.getMul;


public class AES {

	public void ftn_main() {
		String plaintextString = "00000000000000000000000000000000";
		System.out.print("Key:");
		String keyString = "00000000000000000000000000000000";
		Cipher cipher = new Cipher();
		@SuppressWarnings("deprecation")
		byte[][] arr = cipher.performEncrytion(plaintextString, keyString);
		Decipher decipher = new Decipher(cipher.getexpandingKeysres());
		decipher.decryptarr(arr);
	}
}

class Cipher {
	
	/**
	 * This replaceVals also surpports AES-192 and AES-256 and is generated by finite field arrMul, 
	 * which I list down in full below for your convenience.
	 */
	
	private SBOX sboxDisplay ;	
	private byte[][] expanKeyArr;
	private int[] sub_sBox ;
	private static byte[][] replaceVals = {{0x00, 0x00, 0x00, 0x00}, {0x01, 0x00, 0x00, 0x00}, {0x02, 0x00, 0x00, 0x00}, {0x04, 0x00, 0x00, 0x00}, {0x08, 0x00, 0x00, 0x00}, {0x10, 0x00, 0x00, 0x00}, {0x20, 0x00, 0x00, 0x00}, {0x40, 0x00, 0x00, 0x00}, {(byte) 0x80, 0x00, 0x00, 0x00}, {(byte) 0x1b, 0x00, 0x00, 0x00}, {(byte) 0x36, 0x00, 0x00, 0x00}};
	public int Nb ;
	public int Nk ;	
	public int Nr ;
	
	public Cipher() {
		Nk = 4;
		sboxDisplay = new SBOX();
		sub_sBox = sboxDisplay.s_box_generate();
		Nb = 4;
		Nr = 10;
		expanKeyArr = new byte[Nb*(Nr+1)][Nb];
	}
	public byte[][] getexpandingKeysres() {
		return expanKeyArr;
	}
	
	private String arrToString(byte[][] arr) {
		StringBuilder strBuild = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				strBuild.append(String.format("%02X", arr[j][i]));
			}
		}
		String string = strBuild.toString();
		return string;
	}
	
	public byte[][] printEncryption(byte[][] arr) {
		AddRoundKey(0,arr);
		for (int i = 1; i < Nr ; i++) {
			substBytes(arr);	
			arr = ShiftRows(arr);
			arr = MixColumns(arr);
			AddRoundKey(i,arr);
		}
		substBytes(arr);
		arr = ShiftRows(arr);
		AddRoundKey(Nr,arr);	
		return arr;
	}
	
	private void substBytes(byte[][] arr) {
		for (int i = 0; i < Nb; i++) {
			for (int j = 0; j < Nb; j++) {
				arr[i][j] = (byte)(sub_sBox[arr[i][j] & 0x00ff] & 0x00ff);
			}
		}
	}
	
	public byte[][] encryptarr(byte[][] arr) {
		System.out.println("Begin to encrypt arr!!!");
		System.out.println("input:");
		System.out.println(arrToString(arr));
		
		AddRoundKey(0,arr);
		for (int i = 1; i < Nr ; i++) {
			int ignored = i - 1;
			System.out.println("start of round " + ignored +":");
			System.out.println(arrToString(arr));
			
			substBytes(arr);
			System.out.println("after subbytes:");
			System.out.println(arrToString(arr));
			
			arr = ShiftRows(arr);
			System.out.println("after shiftrows:");
			System.out.println(arrToString(arr));
			
			arr = MixColumns(arr);
			System.out.println("after mixcolumns:");
			System.out.println(arrToString(arr));
			
			AddRoundKey(i,arr);
			System.out.println("after round key:");
			System.out.println(arrToString(arr));
		}
		System.out.println("start of round " + Nr +":");
		System.out.println(arrToString(arr));
		
		substBytes(arr);
		System.out.println("after subbytes:");
		System.out.println(arrToString(arr));
		
		arr = ShiftRows(arr);
		System.out.println("after shiftrows:");
		System.out.println(arrToString(arr));
		
		System.out.println("after round key:");
		AddRoundKey(Nr,arr);
		System.out.println(arrToString(arr));
		
		
		return arr;
	}
	
	private byte[][]  MixColumns(byte[][] arr) {
		byte[] a = {2,3,1,1};
		
		byte[][] tempArr = new byte[Nb][Nb];
		for(int j = 0 ; j < Nb ; j++) {
			tempArr[0][j] = (byte)(getMul(a[0], arr[0][j])^getMul(a[1], arr[1][j])^
								getMul(a[2], arr[2][j])^getMul(a[3], arr[3][j]));
			tempArr[1][j] = (byte)(getMul(a[3], arr[0][j])^getMul(a[0], arr[1][j])^
								getMul(a[1], arr[2][j])^getMul(a[2], arr[3][j]));
			tempArr[2][j] = (byte)(getMul(a[2], arr[0][j])^getMul(a[3], arr[1][j])^
								getMul(a[0], arr[2][j])^getMul(a[1], arr[3][j]));
			tempArr[3][j] = (byte)(getMul(a[1], arr[0][j])^getMul(a[2], arr[1][j])^
								getMul(a[3], arr[2][j])^getMul(a[0], arr[3][j]));
		}
		return tempArr;
	}

	private void AddRoundKey(int crr,byte[][] arr) {
		for (int i = 0; i < 4; i++) {
			arr[0][i] = (byte) (arr[0][i] ^ expanKeyArr[Nb*crr + i][0]);
			arr[1][i] = (byte) (arr[1][i] ^ expanKeyArr[Nb*crr + i][1]);
			arr[2][i] = (byte) (arr[2][i] ^ expanKeyArr[Nb*crr + i][2]);
			arr[3][i] = (byte) (arr[3][i] ^ expanKeyArr[Nb*crr + i][3]);
		}
	}
	
	private byte[][]  ShiftRows(byte[][] arr) {
		byte[][] tempArr = new byte[Nb][Nb];
		for (int i = 0; i < Nb; i++) {
			for (int j = 0; j < Nb; j++) 
				tempArr[i][j] = arr[i][(Nb+i+j)%Nb];
		}
		return tempArr;
	}
	
	public void expandingKeys(byte[] key) {
		byte[] ignored = new byte[4];
		int i = 0;
		while(i < Nk) {
			expanKeyArr[i][0] = key[4*i];
			expanKeyArr[i][1] = key[4*i + 1];
			expanKeyArr[i][2] = key[4*i + 2];
			expanKeyArr[i][3] = key[4*i + 3];
			i++;
		}
		i = Nk;
		while(i < Nb *(Nr+1)) {
			ignored = expanKeyArr[i - 1];
			if(i%Nk == 0) {
				ignored = xorOperation(substituitingWords(rWording(ignored)), replaceVals[i/Nk]);
			}
			else if (Nk > 6 && i%Nk == 4) {
				ignored = substituitingWords(rWording(ignored));
			}
			expanKeyArr[i] = xorOperation(expanKeyArr[i-Nk], ignored);
			i++;
		}
		return;
	}
	
	private byte[] rWording(byte[] arrA) {
		byte[] res = new byte[arrA.length];
		for (int i = 0; i < arrA.length; i++) {
			res[i] = arrA[(i+1)%arrA.length];
		}
		return res;
	}

	private byte[] substituitingWords(byte[] arrA) {
		byte[] res = new byte[arrA.length];
		for (int i = 0; i < arrA.length; i++) {
			res[i] = (byte)sub_sBox[arrA[i]&0x00ff];
		}
		return res;
	}
	
	private byte[] xorOperation(byte[] arrA, byte[] arrB) {
		byte[] res = new byte[arrA.length];
		for (int i = 0; i < arrA.length; i++) {
			res[i] = (byte)(arrA[i]^arrB[i]);
		}
		return res;
		
	}

	public String ftnEncryption(String pText, String inKey) {
		StringBuilder strBuild = new StringBuilder();
		String sb;
		int start = 0;
		int end = 2;
		if(inKey.length() != 32) {
			System.out.println("please input correct key!");
			return "";
		}
		byte[] key = new byte[Nk*4];
		for (int i = 0; i < key.length; i++) {
				sb = inKey.substring(start, end);
				key[i] = (byte)(Integer.parseInt(sb.toString(), 16));
				start += 2;
				end += 2;
		}
		
		expandingKeys(key);
		
		byte[] plaintextbytes = pText.getBytes();
		byte[][] arr = new byte[Nb][Nb];
		
		for (int i = 0; i < plaintextbytes.length; i++) {
			if(i%16 == 0 && i != 0) {
				strBuild.append(encryptarr(arr));
				arr = new byte[Nb][Nb];
			}
			arr[i%4][(i/4)%4] = plaintextbytes[i];	
		}
		strBuild.append(encryptarr(arr));
		
		return strBuild.toString(); 
	}
	
	public byte[][] performEncrytion(String input, String inKey) {
		String sb;
		int start = 0;
		int end = 2;
		byte[] key = new byte[Nk*4];
		for (int i = 0; i < key.length; i++) {
			sb = inKey.substring(start, end);
			key[i] = (byte)(Integer.parseInt(sb.toString(), 16));
			start += 2;
			end += 2;
		}
	
		expandingKeys(key);
		start = 0;
		end = 2;
		byte[][] arr = new byte[Nb][Nb];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				sb = input.substring(start, end);
				arr[j][i] = (byte)(Integer.parseInt(sb.toString(), 16));
				start += 2;
				end += 2;
			}
		}
		arr = encryptarr(arr);
		printingArray(arr);
		return arr ;
	}
	
	private void printingArray(byte[][] arr) {
		for(int i = 0; i < 4 ; i++) {
			for(int j = 0; j < 4 ; j++) {
				String string = String.format("%02X", arr[i][j]);
				System.out.print(string+" ");
			}
			System.out.println();
		}
	}

}

class Decipher {
	
	/**
	 * This replaceVals also surpports AES-192 and AES-256 and is generated by finite field arrMul, 
	 * which I list down in full below for your convenience.
	 */
	
	int Nb ;		
	int Nk ;		
	int Nr ;
	byte[][] expanKeyArr;
	int[] inv_sub_sBox;
	private static byte[][] replaceVals = {{0x00, 0x00, 0x00, 0x00},{0x01, 0x00, 0x00, 0x00},{0x02, 0x00, 0x00, 0x00},{0x04, 0x00, 0x00, 0x00},{0x08, 0x00, 0x00, 0x00},{0x10, 0x00, 0x00, 0x00},{0x20, 0x00, 0x00, 0x00},{0x40, 0x00, 0x00, 0x00},{(byte) 0x80, 0x00, 0x00, 0x00},{(byte) 0x1b, 0x00, 0x00, 0x00},{(byte) 0x36, 0x00, 0x00, 0x00}};

	
	SBOX sboxDisplay ;
	
	//constructor with no parameter.
	public Decipher(byte[][] expanKeyArr) {
		this.expanKeyArr = expanKeyArr;
		sboxDisplay = new SBOX();
		inv_sub_sBox = sboxDisplay.inv_s_box_genrate();
		Nb = 4;
		Nk = 4;
		Nr = 10;
	}
	
	/**
	 * transfer a byte[][] arr to string type.
	 * @param arr the 4x4 byte[][] arr you want to transfer.
	 * @return a string represent the arr bytes
	 */
	public String arrToString(byte[][] arr) {
		StringBuilder strBuild = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				strBuild.append(String.format("%02X", arr[j][i]));
			}
		}
		String string = strBuild.toString();
		return string;
	}
	
	private void printingArray(byte[][] arr) {
		for(int i = 0; i < 4 ; i++) {
			for(int j = 0; j < 4 ; j++) {
				String string = String.format("%02X", arr[i][j]);
				System.out.print(string+" ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Decrypt a block arr.
	 * @param arr the 4x4 byte[][] arr you want to decrypt.
	 * @return encrypted arr.
	 */
	public String decryptarr(byte[][] arr) {
		
		
		AddRoundKey(Nr,arr);
		for (int i = Nr - 1; i > 0 ; i--) {
			System.out.println("-----------------------" + (Nr - i) + " Iteration:-----------------------");
			
			arr = InvShiftRows(arr);
			System.out.println("After rows shifting: ");
			System.out.println(arrToString(arr));
			//printingArray(arr);
			
			System.out.println("After substituting bytes: ");
			InvSubBytes(arr);
			System.out.println(arrToString(arr));
			//printingArray(arr);
			
			System.out.println("After adding round key: ");
			AddRoundKey(i,arr);
			System.out.println(arrToString(arr));
			//printingArray(arr);
			
			System.out.println("After mixing columns: ");
			arr = InvMixColumns(arr);
			System.out.println(arrToString(arr));
			//printingArray(arr);
		}
		System.out.println("-----------------------10 Iteration:-----------------------");
		
		arr = InvShiftRows(arr);
		System.out.println("After rows shifting: ");
		System.out.println(arrToString(arr));
		
		System.out.println("After substituting bytes: ");
		InvSubBytes(arr);
		System.out.println(arrToString(arr));
		
		System.out.println("After adding round key: ");
		AddRoundKey(0,arr);
		System.out.println(arrToString(arr));
		
		System.out.println("The decryption res:");
		printingArray(arr);
		
		return arrToString(arr);
	}
	
	/**
	 * do an inverse substitution using s_box.
	 * @param arr the 4x4 byte[][] arr you want to inverse substitute.
	 * @return the inverse substituted arr
	 */
	public void InvSubBytes(byte[][] arr) {
		for (int i = 0; i < Nb; i++) {
			for (int j = 0; j < Nb; j++) {
				arr[i][j] = (byte)(inv_sub_sBox[arr[i][j] & 0x00ff] & 0x00ff);
			}
		}
	}
	
	/**
	 * Inverse shift every row of arr with specific rule,just implement this on original arr.
	 * @param arr the 4x4 byte[][] arr you want to make inverse shifting rows.
	 */
	public byte[][]  InvShiftRows(byte[][] arr) {
		int i = 0;
		byte[][] tempArr = new byte[Nb][Nb];
		for (i = 0; i < Nb; i++) {
			for (int j = 0; j < Nb; j++) {
				tempArr[i][j] = arr[i][(Nb-i+j)%Nb];
			}
		}
		return tempArr;
	}
	
	/**
	 * @param arr the 4x4 byte[][] arr you wish to mix. 
	 * @returns the resulting [][] arr. Input the resulting orchestral arrangement through @return.
	 */
	
	public void AddRoundKey(int crr,byte[][] arr) {
		for (int i = 0; i < 4; i++) {
			arr[0][i] = (byte) (arr[0][i] ^ expanKeyArr[Nb*crr + i][0]);
			arr[1][i] = (byte) (arr[1][i] ^ expanKeyArr[Nb*crr + i][1]);
			arr[2][i] = (byte) (arr[2][i] ^ expanKeyArr[Nb*crr + i][2]);
			arr[3][i] = (byte) (arr[3][i] ^ expanKeyArr[Nb*crr + i][3]);
		}
	}
	
	public byte[][]  InvMixColumns(byte[][] arr) {
		byte[] a = {0x0e,0x0b,0x0d,0x09};
		
		byte[][] tempArr = new byte[Nb][Nb];
		for(int j = 0 ; j < Nb ; j++) {
			tempArr[0][j] = (byte)(getMul(a[0], arr[0][j])^getMul(a[1], arr[1][j])^
								getMul(a[2], arr[2][j])^getMul(a[3], arr[3][j]));
			tempArr[1][j] = (byte)(getMul(a[3], arr[0][j])^getMul(a[0], arr[1][j])^
								getMul(a[1], arr[2][j])^getMul(a[2], arr[3][j]));
			tempArr[2][j] = (byte)(getMul(a[2], arr[0][j])^getMul(a[3], arr[1][j])^
								getMul(a[0], arr[2][j])^getMul(a[1], arr[3][j]));
			tempArr[3][j] = (byte)(getMul(a[1], arr[0][j])^getMul(a[2], arr[1][j])^
								getMul(a[3], arr[2][j])^getMul(a[0], arr[3][j]));
		}
		return tempArr;
	}
	
	
}
