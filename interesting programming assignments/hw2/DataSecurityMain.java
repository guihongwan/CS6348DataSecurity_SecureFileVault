import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.lang.Math;
import java.math.BigInteger;

/**
 *
 * @author guihongwan March 19,2018
 **/

public class DataSecurityMain {

    public static void main(String[] args) {
       String message = "I Love Data Security Class So Much";
       int[] ks = new int[4];
       ks[0] = 2;
       ks[1] = 4;
       ks[2] = 6;
       ks[3] = 8;
//        ks[0] = 4;
//        ks[1] = 8;
//        ks[2] = 12;
//        ks[3] = 16;
       
       int total = 10;//repeat 10 time for each k.
       double[] nums = new double[total];//store the total times for each k.
       
       for(int i = 0; i < ks.length; i++){
           System.out.println("target: "+ks[i]);
           for(int j = 0; j < total; j++){
               String ret =P(message,ks[i]);
               String num_string = ret.split(",")[2];
               double num = Double.parseDouble(num_string);
               nums[j] = num;
               System.out.println(ret);
           }
           Statistics mStatistics = new Statistics(nums);
           double std = mStatistics.getStdDev();
           double mean = mStatistics.getMean();
           System.out.println("mean: "+mean);
           System.out.println("std: "+std);
           System.out.println();
       }
    }
    
   /**
    * P(M,k)
    * M could be an arbitrary string
    * k is an integer, k bits equal to zero
    *
    * output: ‘H (M ||ni ), ni , i’
    **/
    private static String P(String message, int k){
        String hash_message = "";
        String nonce = "";
        int num = 0;
        
        String substring = "";
        String target_bit = "";
        for(int i=0; i<k; i++){
            target_bit += "0";
        }
        System.out.println("target_bit:"+target_bit);
        
        while (!substring.equals(target_bit)){
            nonce = getNonce();
            String mo = message+nonce;
            hash_message = StringToSha(mo);
            int end = (int)Math.ceil(k/4.0);
            substring = hash_message.substring(0,end);
            
            String binary = new BigInteger(substring,16).toString(2);
            
            //make up
            String makeup_bit = "";
            for(int i=0; i<(end*4 - binary.length()); i++){
                makeup_bit += "0";
            }

            binary = makeup_bit+binary;
            substring = binary.substring(0,k);
            //System.out.println(substring);
            num++;
        }
        return hash_message+","+nonce+","+num;
    }
   
   private static String getNonce(){
       long long64bit = new Random().nextLong();
       byte[] longbyte = longToBytes(long64bit);
       
       return bytesToHex(longbyte);
   }
   private static String StringToSha(String message){
       String ret = "";
       try {
           MessageDigest digest = MessageDigest.getInstance("SHA-256");
           byte[] encodedhash = digest.digest(
               message.getBytes(StandardCharsets.UTF_8));
           ret = bytesToHex(encodedhash);
       }catch(NoSuchAlgorithmException e){
           e.printStackTrace();
       }
       
       return ret;
   }
   private static String bytesToHex(byte[] hash) {
       StringBuffer hexString = new StringBuffer();
       for (int i = 0; i < hash.length; i++) {
           String hex = Integer.toHexString(0xff & hash[i]);
           if(hex.length() == 1) hexString.append('0');
               hexString.append(hex);
           }
       return hexString.toString();
   }
   private static byte[] longToBytes(long x) {
       ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
       buffer.putLong(x);
       return buffer.array();
   }
}