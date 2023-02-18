package Model;

public class SBOX {

	private int[] sbQuotient;
	private int[] sbFinQuotient;
	private int[] sbReminder;
	private int[][] s_box;
	private int[] subSbox;
	private int[] invSubSbox;
	public int[] c = {1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0} ;
	public int[] irreducible = {1,1,0,1,1,0,0,0,1,0,0,0,0,0,0,0};
	
	public SBOX() {
		invSubSbox = new int[256];
		subSbox = new int[256];
		s_box = new int[16][16];
		sbReminder = new int[16];
		sbFinQuotient = new int[16];
		sbQuotient = new int[16];
	}
	public int[][] genMethod() {
		System.out.println("begin to genMethod s_box");
		for(int i = 0; i < 16 ; i++) {
			for(int j = 0; j < 16 ; j++) {
				int[] bits = new int[16];
				if(i == 0 && j == 0) {
					
				}
				else {
					byte data = (byte) ((i<<4) + j);
					bits = get_inverse(byteIntArr(data));
				}
				s_box[i][j] = convert(bits);
				String string = String.format("%02X", s_box[i][j]);
				System.out.print(string+" ");
			}
			System.out.println();
		}
		System.out.println("s box has been generated");
		return s_box;
	}
	
	public int[] s_box_generate() {
		genMethod();
		for (int i = 0; i < 256; i++) {
			subSbox[i] = s_box[i/16][i%16];
			if(i%16 == 0 && i != 0)
				System.out.println();
			String string = String.format("%02X", subSbox[i]);
			System.out.print(string+" ");
		}
		System.out.println();
		System.out.println("s_box has been generated");
		return subSbox;
	}
	
	public int[] inv_s_box_genrate() {
		s_box_generate();
		System.out.println("Invser s_box:");
		for (int i = 0; i < invSubSbox.length; i++) {
			invSubSbox[subSbox[i]] = i;
		}
		for (int i = 0; i < invSubSbox.length; i++) {
			
			if(i%16 == 0 && i != 0)
				System.out.println();
			String string = String.format("%02X", invSubSbox[i]);
			System.out.print(string+" ");
		}
		System.out.println();
		return invSubSbox;
	}
	
	private boolean chkEquals(int[] arrA, int[] arrB) {
		for (int i = 0; i < arrB.length; i++) {
			if(arrA[i] != arrB[i])
				return false;
		}
		return true;
	}
	
	private int convert(int[] bitsArr) {
		int[] converted = new int[16];
		int res = 0;
		int exp = 1;
		for(int i = 0 ; i < 8 ; i++) {
			converted[i] = bitsArr[i]^bitsArr[(i+4)%8]^bitsArr[(i+5)%8]^bitsArr[(i+6)%8]^bitsArr[(i+7)%8]^c[i];
			res = res + converted[i]*exp;
			exp = exp * 2;
		}
		return res;
	}
	
	private int getBitArr(byte a,int ind) {
	    int bit = (int)((a>>ind) & 0x1);
	    return bit;
	}
	
	public int[] byteIntArr(byte d) {
		int[] bits = new int[16];
		for (int i = 0; i < 8; i++) {
			bits[i] = getBitArr(d, i);
		}
		return bits;
	}
	
	public byte intArrByte(int[] arr) {
		int res = 0;
		int exp = 1;
		for (int i = 0; i < 8; i++) {
			res = res + arr[i]*exp;
			exp = exp * 2;
		}
		return (byte)res;
	}
	
	private int sbGetExp(int[] num) {
		int exponent = 0;
		for (int i = 1; i <= num.length; i++) {
			if(num[i-1] == 1)
				exponent = i;
		}
		return exponent;
	}

	private void calQuotient(int diff_exponent) {
		sbQuotient[diff_exponent] = 1;
	}
	
	private void getDivArr(int[] arrA, int[] arrB){
		int arra_exponent = sbGetExp(arrA);
		int arrb_exponent = sbGetExp(arrB);
		int[] one_quotient = new int[16];
		int diff_exponent = 0;
		
		if(arra_exponent >= arrb_exponent) {
			diff_exponent = arra_exponent - arrb_exponent;
			for (int i = 0; i < arrB.length; i++) {
				if(i+diff_exponent < arrA.length)
					one_quotient[i+diff_exponent] = arrB[i];
				if(i < diff_exponent)
					one_quotient[i] = 0;
			}
			
			for (int i = 0; i < arrA.length; i++) {
				arrA[i] = arrA[i]^one_quotient[i];
			}
			
			calQuotient(diff_exponent);
			getDivArr(arrA, arrB);
		}
		else {
			sbFinQuotient = sbQuotient;
			sbReminder = arrA;
			sbQuotient = new int[16];
		}
	}
	
	private int[] get_inverse(int[] bits) {
		int[] inverse = new int[16];
		int[][] arrR = new int[3][16];
		int[] arrQ = new int[16];
		int[][] arrV = new int[3][16];
		int[][] expanKeyArr = new int[3][16];
		
		int[] arrT = new int[16];
		
		arrR[0] = irreducible.clone();
		arrR[1] = bits;
		
		arrV[0][0] = 1;
		expanKeyArr[1][0] = 1;
		
		getDivArr(arrR[0], arrR[1]);
		arrQ = sbFinQuotient;
		arrR[2] = sbReminder;
		while(!chkEquals(sbReminder, arrT)) {
			arrV[2] = subArrs(arrV[0], arrMul(arrQ, arrV[1]));
			expanKeyArr[2] = subArrs(expanKeyArr[0], arrMul(arrQ, expanKeyArr[1]));
			arrV[0] = arrV[1];
			arrV[1] = arrV[2];
			expanKeyArr[0] = expanKeyArr[1];
			expanKeyArr[1] = expanKeyArr[2];
			arrR[0] = arrR[1];
			arrR[1] = arrR[2];
			getDivArr(arrR[0], arrR[1]);
			arrQ = sbFinQuotient;
			arrR[2] = sbReminder;
		}
		inverse = expanKeyArr[1];
		
		return inverse;
	}
	
	private int[] subArrs(int[] arrA, int[] arrB) {
		int[] res = new int[arrA.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = arrA[i]^arrB[i];
		}
		return res;
	}
	
	public int[] arrMul(int[] arrA, int[] arrB) {
		int[] asProd = new int[16];
		int[] modProd = new int[16];
		for(int i = 0 ; i < 8 ; i++) {
			if(arrA[i] == 1) {
				for (int j = 0; j < 8; j++) {
					asProd[i+j] = asProd[i+j]^arrB[j];
				}
			}
		}
		modProd = asProd;
		if(sbGetExp(asProd) >= sbGetExp(irreducible) -1 ) {
			getDivArr(asProd, irreducible.clone());
			modProd = sbReminder;
		}
		return modProd;
	}
	
	public static int getMul(byte arrA, byte arrB) {
		SBOX s_box = new SBOX();
		int[] AA = s_box.byteIntArr(arrA);
		int[] BB = s_box.byteIntArr(arrB);
		int[] res = s_box.arrMul(AA, BB);
		return s_box.intArrByte(res);
	}


}

