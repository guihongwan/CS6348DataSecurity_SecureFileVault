package com.datasecurity.filemanager;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.datasecurity.filemanager.Utils.Log;
import com.datasecurity.filemanager.encryption.FileEncrypter;
import com.datasecurity.filemanager.vault.Vault;
import com.datasecurity.filemanager.vault.VaultAuthentication;
import com.datasecurity.filemanager.vault.VaultFileWatcher;

/**
 * 
 * Data Security Group Project: Secure File Manager
 * 
 * This is the main UI
 * 
 * @author Data Security Group: Jihye Choi, Wenqing Jiang, Guihong Wan, Husheng Zhou
 * 
 * Today's Quote: The World Is Your Playground.
 * 
 */
public class PlayGround implements VaultBrowserListener {
	private static final String TAG = "PlayGround";
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_TIMEOUT = true;
	
	private static boolean is_GPU = false;
	private static boolean is_TIMEOUT = true;
	
	private JFrame frame;
	private JTabbedPane  mJTabbedPane;

	private VaultManager mVaultManager;
	private int last_tab = 0;
	private static int no_operation_time = 0;
	
	private static VaultBrowser[] vaultBrowsers;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
		    public void run() {
			    try {
					PlayGround window = new PlayGround();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PlayGround() {
		mVaultManager = new VaultManager(Env.csvFileName);
		
		//build environment
		Env.build();
		
		//initialize views
		initialize();
		
		// timeout
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
		    @Override
			public void run() {
		    	    if(is_TIMEOUT) {
		    	    	    no_operation_time += 1000*30;
		    	    	    if(DEBUG_TIMEOUT) Log.Debug(TAG, " NO OPERATION IN " + (no_operation_time/1000.0/30.0)*0.5 + " MIN.");
		    	    	    
		    	    	    if(no_operation_time > Env.timeout) {
		    	    	    	    mVaultManager.clearPassword();
		 			    	Env.destroy();
		 			    	Log.Debug(TAG, "Log out..." );    	     
		    	    	    }
		    	    } else {
		    	    	    no_operation_time = 0;
		    	    }
		    	   
			}
		}, 1000*30, 1000*30);//0.5 min
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(300, 150, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				Log.Debug(TAG, "windowClosing");
				Env.destroy();
			}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {
				if(DEBUG_TIMEOUT)System.out.println("windowActivated");
				no_operation_time = 0;
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				//System.out.println("windowDeactivated");
				
			}
			
		});
		
		//add menu
		addMenu();
		
		//add content pane
		mJTabbedPane = new JTabbedPane(JTabbedPane.TOP) ;
		mJTabbedPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		mVaultManager.initVaultFolders();
		createVaultBrowsers();
		updateVaultView(false);
	}
	
	private void addMenu() {
		//add menu bar
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		
		//Vault
		JMenu jm_vault=new JMenu("Vault") ;
		JMenuItem createItem=new JMenuItem("Create new Vault"); 
		//JMenuItem deleteItem=new JMenuItem("Delete Vault");
		createItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(DEBUG) Log.Debug(TAG, "Create new Vault");
				createVaultView();
			}
		});
		
//		deleteItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				if(DEBUG) Log.Debug(TAG, "Delete Vault");
//			}
//		});
		jm_vault.add(createItem);
//		jm_vault.add(deleteItem);
		menubar.add(jm_vault);
		
		//Timeout
		JMenu jm_timeout=new JMenu("TIMEOUT") ;
		JMenuItem timeoutItem=new JMenuItem();
		if(is_TIMEOUT) {
			timeoutItem.setText("Disable TIMEOUT");
		}else {
			timeoutItem.setText("Enable TIMEOUT");
		}
		
		timeoutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(is_TIMEOUT) {
					is_TIMEOUT = false;
					timeoutItem.setText("Enable TIMEOUT");
				}else {
					is_TIMEOUT = true;
					timeoutItem.setText("Disable TIMEOUT");
				}
				//if(DEBUG_TIMEOUT) Log.Debug(TAG, "is_TIMEOUT:"+is_TIMEOUT);
			}
		});
		
		jm_timeout.add(timeoutItem);
		menubar.add(jm_timeout);	
		
		//GPU
		JMenu jm_gpu=new JMenu("GPU") ;
		JMenuItem gpuItem=new JMenuItem();
		if(is_GPU) {
			gpuItem.setText("Disable GPU");
		}else {
			gpuItem.setText("Enable GPU");
		}
		
		gpuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(is_GPU) {
					is_GPU = false;
					gpuItem.setText("Enable GPU");
				}else {
					is_GPU = true;
					gpuItem.setText("Disable GPU");
				}
				//if(DEBUG) Log.Debug(TAG, "is_GPU:"+is_GPU);
			}
		});
		
		jm_gpu.add(gpuItem);
		
		menubar.add(jm_gpu);			
	}
	
	private void createVaultBrowsers() {
		//create a VaultBrowser for every Vault
		int vault_total = VaultManager.entries.size();
		vaultBrowsers = new VaultBrowser[vault_total];
		for(int i = 0; i < vaultBrowsers.length; i++) {
			vaultBrowsers[i] =new VaultBrowser(i);
			vaultBrowsers[i].setListener(this);
			
			//add watcher for vault
			FileEncrypter encrypter = new FileEncrypter();
			encrypter.setListener(vaultBrowsers[i]);
			
			VaultFileWatcher mVaultFileWatcher = new VaultFileWatcher(mVaultManager.getVaultfromId(i), encrypter, is_GPU);
			mVaultFileWatcher.start(); 
		}
	}
	/*
	 * add:a flag that we add a new Vault
	 */
	private void updateVaultView(boolean add) {
		mJTabbedPane.removeAll();
		
		//add panels
		int i = 0;
		for(Vault metadata: VaultManager.entries) {
			//get TAB name
			String dir = metadata.getStorageDir();
			String[] names = dir.split("/");
			String vaultname = names[names.length-1];
			if(DEBUG) Log.Debug(TAG, vaultname);
			
			mJTabbedPane.add(vaultname, vaultBrowsers[i]);
			i++;
		}
		
		mJTabbedPane.addChangeListener( new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				int idx = mJTabbedPane.getSelectedIndex();
				if(idx >=0 && idx < VaultManager.entries.size()) {
				    checkPassword(idx);
					last_tab = idx;
				}
			}});
		
		frame.add(mJTabbedPane, BorderLayout.CENTER);
		
		if(add) {
			mJTabbedPane.setSelectedIndex(VaultManager.entries.size() - 1 );;
		}else {
		    //mJTabbedPane.setSelectedIndex(last_tab);
		}
	}
	
	
	
	private boolean checkPassword(int idx) {
		boolean ret = false;
		if( idx<0 || idx >= VaultManager.entries.size() ) return false;
		
		Vault selected_vault = mVaultManager.getVaultfromId(idx);
		if(selected_vault.getPassword() == null) {
			
			JFrame parentFrame = new JFrame();
			parentFrame.setTitle("Enter Password");
			parentFrame.setBounds(400, 250, 400, 160);
			parentFrame.getContentPane().setLayout(null);
			parentFrame.setResizable(false);

			JLabel warningLabel = new JLabel("The password doesn't match.");
			warningLabel.setBounds(30, 20, 200, 20);
			warningLabel.setForeground(Color.RED);
			warningLabel.setVisible(false);
			parentFrame.getContentPane().add(warningLabel);
			
			JLabel lblPassword = new JLabel("Password");
			lblPassword.setBounds(20, 50, 100, 14);
			JPasswordField txtPassword = new JPasswordField();
			txtPassword.setBounds(120, 50, 160, 20);
			parentFrame.getContentPane().add(lblPassword);
			parentFrame.getContentPane().add(txtPassword);
			
			Button btnOK = new Button("OK");
			btnOK.setBounds(300, 100, 80, 20);
			parentFrame.getContentPane().add(btnOK);
			
			parentFrame.setVisible(true);
		
			btnOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String password = String.valueOf(txtPassword.getPassword());
					boolean ret = VaultAuthentication.authenticateVault(selected_vault, password);
					if(ret) {
						warningLabel.setVisible(false);
						VaultManager.entries.get(idx).setPassword(password);
						parentFrame.dispose();
						ret = true;
					} else {
						ret = false;
						warningLabel.setVisible(true);
					}
				}
			});
		}
		return ret;
	}
	
	//select the vault target root directory
	private void createVaultView(){
		JFrame parentFrame = new JFrame();
		parentFrame.setTitle("Create A Vault");
		parentFrame.setBounds(400, 250, 400, 191);
		//parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		parentFrame.getContentPane().setLayout(null);
		parentFrame.setResizable(false);
		
		JLabel warningLabel = new JLabel("The password doesn't match.");
		warningLabel.setBounds(30, 10, 300, 20);
		warningLabel.setForeground(Color.RED);
		warningLabel.setVisible(false);
		parentFrame.getContentPane().add(warningLabel);
		
		//target Directory
		JLabel lblTargetDir = new JLabel("Directory");
		lblTargetDir.setBounds(20, 30, 100, 14);
		JTextField txtTargetDir = new JTextField();
		txtTargetDir.setBounds(120, 30, 160, 20);
		parentFrame.getContentPane().add(txtTargetDir);
		parentFrame.getContentPane().add(lblTargetDir);
		Button btnChoose = new Button("choose");
		btnChoose.setBounds(300, 30, 80, 20);
		parentFrame.getContentPane().add(btnChoose);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(20, 60, 100, 14);
		JPasswordField txtPassword = new JPasswordField();
		txtPassword.setBounds(120, 60, 160, 20);
		parentFrame.getContentPane().add(lblPassword);
		parentFrame.getContentPane().add(txtPassword);
		
		JPasswordField txtPassword_Retype = new JPasswordField();
		JLabel lblPassword_Retype = new JLabel("Retype Password");
		lblPassword_Retype.setBounds(20, 90, 100, 14);
		parentFrame.getContentPane().add(lblPassword_Retype);
		txtPassword_Retype.setBounds(120, 90, 160, 20);
		parentFrame.getContentPane().add(txtPassword_Retype);
		
		Button btnOK = new Button("OK");
		btnOK.setBounds(300, 120, 80, 20);
		parentFrame.getContentPane().add(btnOK);
		
		parentFrame.setVisible(true);
		
		
		btnChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser .setDialogTitle("Setup a target directory");
				fileChooser.setFileSelectionMode(1);//select directory
				int state = fileChooser.showOpenDialog(parentFrame);
				 
				if (state == JFileChooser.APPROVE_OPTION) {
					String string = fileChooser.getSelectedFile().getAbsolutePath();
					txtTargetDir.setText(string);
				}
			}
		});
	
		
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String directory = txtTargetDir.getText();
				String password = String.valueOf(txtPassword.getPassword());
				String password_retype = String.valueOf(txtPassword_Retype.getPassword());
				
				if( directory.equalsIgnoreCase("") || !new File(directory).exists()) {
					warningLabel.setText(" The directory is empty or does not exist.");
					warningLabel.setVisible(true);
				} else if(!mVaultManager.checkPasswordValidity(password)) {
					warningLabel.setText(" The password is not valid.");
					warningLabel.setVisible(true);
				} else {
				    if(password.equals(password_retype)) {
					    mVaultManager.createVault(password, directory);
					    parentFrame.dispose();
					    createVaultBrowsers();
					    updateVaultView(true);
					    if(DEBUG) Log.Debug(TAG, "Succeed to create Vault for " + directory);
				    } else {
					    warningLabel.setText(" The password doesn't match.");
					    warningLabel.setVisible(true);
				    }
				}
			}
		});
	}

	@Override
	public void nullPassword() {
	    checkPassword(last_tab );
	}
}
