package paillierp.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

import paillierp.Paillier;
import paillierp.key.KeyGen;
import paillierp.key.PaillierKey;
import paillierp.key.PaillierPrivateKey;


/**
 * Data Security Homework 3 Question 1
 * A Paillier Program
 * 
 * Support following commands:
   -keygen -outputPK public-key-file -outputPr private-key-file
   -encrypt -pk public-keyfile -input input-file -output encrypted-file
   -process -pk public-key-file -input encrypted-file -output processed-file
   -decryt -pr private-key-file -input processed-file -output output-file
 *
 * @author Guihong Wan
 * @version v1.0 04/19/2018
 *
 */
public class Playground {
	private static final boolean DEBUG = true;
	private static final String DIVIDER = " ";

	public static void main(String[] args) {
        if(args.length == 0) {
        	    printUsage();
            	
            	@SuppressWarnings("resource")
				Scanner standardinput=new Scanner(System.in);
	        System.out.println("Please enter commands:");
	        String cmd_input = standardinput.nextLine();
	        args = cmd_input.split(DIVIDER);
        }
        //-keygen -outputPK public-key-file -outputPr private-key-file
		if(args[0].equalsIgnoreCase("-keygen")) {
			if(args.length != 5) {
				System.err.println("the format of command is wrong.");
				printUsage();
			}else {
			    String pubkey_file = args[2];
			    String prikey_file = args[4];
			    generateKeys(pubkey_file, prikey_file);
			}
			
		//-encrypt -pk public-key-file -input input-file -output output-file
		} else if(args[0].equalsIgnoreCase("-encrypt")) {
			if(args.length != 7) {
				System.err.println("the format of command is wrong.");
				printUsage();
			}else {
			    String pubkey_file = args[2];
			    String input_file = args[4];
			    String output_file = args[6];
			    encryptFile(pubkey_file, input_file, output_file);
			    System.out.println("Finish encrypting data.");
			}
		
	    //-process -pk public-key-file -input encrypted-file -output processed-file
		} else if(args[0].equalsIgnoreCase("-process")) {
			if(args.length != 7) {
				System.err.println("the format of command is wrong.");
				printUsage();
			}else {
			    String pubkey_file = args[2];
			    String encrypted_file = args[4];
			    String processed_file = args[6];
			    processFile(pubkey_file, encrypted_file, processed_file);
			    System.out.println("Finish processing data.");
			}
		
		//-decryt -pr private-key-file -input processed-file -output output-file
		} else if(args[0].equalsIgnoreCase("-decryt")) {
			if(args.length != 7) {
				System.err.println("the format of command is wrong.");
				printUsage();
			}else {
			    String prikey_file = args[2];
			    String processed_file = args[4];
			    String output_file = args[6];
			    decryptFile(prikey_file, processed_file, output_file);
			    System.out.println("Finish decrypting data.");
			}
		} else {
			printUsage();
		}
	}
	
	/**
	 * 
	 * Generate public key and private key.
	 * @param pubkey_file, where to store the public key
	 * @param prikey_file, where to store the private key
	 * 
	 */
	private static void generateKeys(String pubkey_file, String prikey_file) {
        SecureRandom rnd = new SecureRandom();
        PaillierPrivateKey private_key = KeyGen.PaillierKey(2048, 1024, rnd.nextLong());
        
        writeKeyToFile(private_key, pubkey_file, prikey_file);
        
        //test whether the generated keys work well.
        if(DEBUG) {
          	PaillierPrivateKey prik = readPrivateKeyFromFile(prikey_file);
            
            PaillierKey pupk = readPublicKeyFromFile(pubkey_file);
            
            BigInteger msg1 = BigInteger.valueOf(2);
            BigInteger msg2 = BigInteger.valueOf(4);
            Paillier bob = new Paillier(pupk);
            BigInteger emsg1 = bob.encrypt(msg1);
            BigInteger emsg2 = bob.encrypt(msg2);
            BigInteger emsgadd = bob.add(emsg1, emsg2);
            
            Paillier alice = new Paillier(prik);
            BigInteger dmsg = alice.decrypt(emsgadd);
            System.out.println(dmsg);
        }
	}
	
    /**
     * 
     * Encrypt the data in the input_file
     * each line should be a integer, like x
     * after encryption: E(x),E(x2) each line
     * @param pubkey_file
     * @param input_file
     * @param encrypted_file, the output file
     */
	private static void encryptFile(String pubkey_file, String input_file, String encrypted_file) {
		PaillierKey pupk = readPublicKeyFromFile(pubkey_file);
		Paillier bob = new Paillier(pupk);
		
		BufferedReader fileReader = null;
		FileWriter fileWriter = null;
		try {
			fileReader = new BufferedReader(new FileReader(input_file));
			fileWriter  = new FileWriter(encrypted_file);
		    String line = fileReader.readLine();
		    while(line != null) {
		        System.out.println("try to encryt:'"+line+"'");
		        BigInteger data = BigInteger.valueOf(Long.valueOf(line));
		    
		        BigInteger en_data = bob.encrypt(data);
		        byte[] b_en_data = en_data.toByteArray();
			    fileWriter.append(byteToString(b_en_data));
			    
			    fileWriter.append(",");
			
			    BigInteger squ_data = data.multiply(data);
			    BigInteger en_squ_data = bob.encrypt(squ_data);
			    byte[] b_en_sq_data = en_squ_data.toByteArray();
				fileWriter.append(byteToString(b_en_sq_data));
				
				fileWriter.append("\n");
				line = fileReader.readLine();
		    }
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			    try {
			    	    if(fileWriter != null) {
					    fileWriter.flush();
					    fileWriter.close();
			    	    }
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

    /**
     * process the encrypted data in file
     * each line should be in this way: E(x), E(x2)
     * output: the sum of all E(x), the sum of all E(x2), and the sum of total number of lines
     * 
     * @param pubkey_file
     * @param encrypted_file
     * @param processed_file
     */
	@SuppressWarnings("resource")
	private static void processFile(String pubkey_file, String encrypted_file, String processed_file) {
		PaillierKey pupk = readPublicKeyFromFile(pubkey_file);
		Paillier bob = new Paillier(pupk);
		
		BufferedReader fileReader = null;
		FileWriter fileWriter = null;
		try {
			fileReader = new BufferedReader(new FileReader(encrypted_file));
			fileWriter  = new FileWriter(processed_file);
			
		    String line = fileReader.readLine();
		    String[] raws = line.split(",");
            if(raws.length != 2) {
            	    System.err.println("Please make sure the data is right.");
            	    return;
            }
            byte[] en_data = stringToByte(raws[0]);
            BigInteger sum = new BigInteger(en_data);
            
            byte[] en_data_squ = stringToByte(raws[1]);
            BigInteger sum_squ = new BigInteger(en_data_squ);
            long count = 1;
            
            line = fileReader.readLine();
		    while(line != null) {
                raws = line.split(",");
                if(raws.length != 2) {
                	    System.err.println("Please make sure the data is right.");
                	    break;
                }
                en_data = stringToByte(raws[0]);
                BigInteger en_data_add = new BigInteger(en_data);
                en_data_squ = stringToByte(raws[1]);
                BigInteger en_data_squ_add = new BigInteger(en_data_squ);
                
                if(DEBUG) {
                  	PaillierPrivateKey prik = readPrivateKeyFromFile("private-key-file");
                  	Paillier alice = new Paillier(prik);
                    BigInteger dmsg = alice.decrypt(en_data_add);
                    BigInteger dmsg_squ = alice.decrypt(en_data_squ_add);
                    System.out.println("processing data:"+dmsg + " "+dmsg_squ);
                }
                
                sum = bob.add(sum, en_data_add);
                sum_squ = bob.add(sum_squ, en_data_squ_add);
                count++;
                
                line = fileReader.readLine();
		    }
		    
		    byte[] data_sum = sum.toByteArray();
		    fileWriter.append(byteToString(data_sum));
		    fileWriter.append("\n");
		    byte[] data_sum_squ = sum_squ.toByteArray();
		    fileWriter.append(byteToString(data_sum_squ));
		    fileWriter.append("\n");
		    if(DEBUG) System.out.println("The total number:"+ count);
		    BigInteger countvalue = BigInteger.valueOf(count);
		    
		    byte[] count_byte = bob.encrypt(countvalue).toByteArray();
		    fileWriter.append(byteToString(count_byte));
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			    try {
			    	    if(fileWriter != null) {
					    fileWriter.flush();
					    fileWriter.close();
			    	    }
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}
	
	/**
	 * 
	 * Decrypt the processed data in the file.
	 * 
	 * @param prikey_file
	 * @param processed_file
	 * @param output_file
	 */
	private static void decryptFile(String prikey_file, String processed_file, String output_file) {
		PaillierPrivateKey prik = readPrivateKeyFromFile(prikey_file);
      	Paillier alice = new Paillier(prik);
		
		BufferedReader fileReader = null;
		FileWriter fileWriter = null;
		try {
			fileReader = new BufferedReader(new FileReader(processed_file));
			fileWriter  = new FileWriter(output_file);
			
		    String line = fileReader.readLine();
		   
		    while(line != null) {
                
                byte[] pr_data = stringToByte(line);
                BigInteger data = new BigInteger(pr_data);
                BigInteger de_data = alice.decrypt(data);
                byte[] bytes = de_data.toByteArray();
    		        fileWriter.append(byteToString(bytes));
    		        fileWriter.append("\n");
    		        
                line = fileReader.readLine();
                
                if(DEBUG) {
                	    if(line == null ) {
                	    	   System.out.println("the total number of lines:"+de_data);
                	    } else {
                	    	    System.out.println(de_data);
                	    }
                	    	
                }
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			    try {
			    	    if(fileWriter != null) {
					    fileWriter.flush();
					    fileWriter.close();
			    	    }
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}

	
	private static void writeKeyToFile(PaillierPrivateKey private_key, String pubkey_file, String prikey_file) {
		File file = new File(pubkey_file);
	    if(file.exists()) file.delete();
	    file = new File(prikey_file);
	    if(file.exists()) file.delete();
	    
	    FileWriter fileWriter = null;
	    //write public key
		try {
			fileWriter  = new FileWriter(pubkey_file);
			BigInteger n = private_key.getN();
			
			String nstr = byteToString(n.toByteArray());
			
			fileWriter.append(nstr);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			    try {
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		//write private key
		try {
			fileWriter  = new FileWriter(prikey_file);
			BigInteger n = private_key.getN();
			
			String nstr = byteToString(n.toByteArray());
			
			fileWriter.append(nstr);
			fileWriter.append('\n');
			BigInteger d = private_key.getD();
			String dstr = byteToString(d.toByteArray());
			fileWriter.append(dstr);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			    try {
					fileWriter.flush();
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}		
		
	}
	private static PaillierPrivateKey readPrivateKeyFromFile(String prikey_file) {
		PaillierPrivateKey pk = null;
		BufferedReader fileReader = null;
		try {
		    fileReader = new BufferedReader(new FileReader(prikey_file));
		    String nstr = fileReader.readLine();
		    byte[] nn = stringToByte(nstr);
		    BigInteger n = new BigInteger(nn);
		    
		    String dstr = fileReader.readLine();
		    byte[] dd = stringToByte(dstr);
		    BigInteger d = new BigInteger(dd);
		    
			pk = new PaillierPrivateKey(n, d, new SecureRandom().nextLong());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return pk;
	}
	
	private static PaillierKey readPublicKeyFromFile(String pubkey_file) {
		PaillierKey pk = null;
		BufferedReader fileReader = null;
		try {
		    fileReader = new BufferedReader(new FileReader(pubkey_file));
		    String nstr = fileReader.readLine();
		    byte[] n = stringToByte(nstr);
			pk = new PaillierKey(n, new SecureRandom().nextLong());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return pk;
	}
	
	/**
	 * convert BigInteger byte[] to String
	 * @param data
	 * @return
	 */
	private static String byteToString(byte[] data) {
		StringBuilder b = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            b.append(data[i]);
            b.append(" ");
        }
        return b.toString();
	}
	
	/**
	 * convert String to corresponding BigInteger byte[]
	 * @param data
	 * @return
	 */
	private static byte[] stringToByte(String bytes) {
		String[] strs = bytes.toString().split(" ");
		byte[] data = new byte[strs.length];
        for(int i = 0; i< strs.length; i++) {
        	    data[i] = Byte.valueOf(strs[i]);
        }
        return data;
	}
	
	private static void printUsage() {
		int cmds_num = 4;
	    String[] cmds = new String[cmds_num];
	    cmds[0] = "-keygen -outputPK public-key-file -outputPr private-key-file";
	    cmds[1] = "-encrypt -pk public-key-file -input input-file -output encrypted-file";
	    cmds[2] = "-process -pk public-key-file -input encrypted-file -output processed-file";
	    cmds[3] = "-decryt -pr private-key-file -input processed-file -output output-file";
	    
	    System.out.println("Usage:");
        
      	for(int i=0; i< cmds.length; i++) {
    		    System.out.println(cmds[i]);
    	    }
      	
      	System.out.println("");
	}

}
