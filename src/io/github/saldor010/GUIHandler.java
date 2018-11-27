package io.github.saldor010;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import io.github.saldor010.Crypto.DecryptStatus;


public class GUIHandler extends JFrame implements ActionListener,ChangeListener {
	private JPanel mainPanel;
	private JButton encrypt;
	private JButton decrypt;
	private JLabel statusLabel;
	private JTextField inputFile;
	private JTextField outputFile;
	private JLabel inputFile_label;
	private JLabel outputFile_label;
	private JSlider offsetSlider;
	private JSlider expectedSlider;
	private JTextField exactField;
	private JLabel offsetSlider_label;
	private JLabel expectedSlider_label;
	private JLabel exactField_label;
	private ButtonGroup radioGroup;
	private JRadioButton expectedSlider_radio;
	private JRadioButton exactField_radio;
	private JButton start;
	private JLabel inputFile_status;
	private JProgressBar progressbar;
	private JLabel fileSize_label;
	private JLabel fileSize;
	private int STATUS;
	public boolean debounce;
	public int timeElapsed;
	public GUIHandler() {
		super("Crypto");
		debounce = false;
		setSize(600,370);
		setVisible(true);
		setResizable(false);
		Image icon;
		try {
			icon = ImageIO.read(new File("NewbieSteg.png"));
			setIconImage(icon);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		statusLabel = new JLabel();
		statusLabel.setBounds(200,5,200,30);
		statusLabel.setFont( new Font("Arial",2,20) );
		statusLabel.setText("Currently Encrypting");
		
		encrypt = new JButton();
		encrypt.setBounds(0,10,100,20);
		encrypt.setFont( new Font("Arial",1,16) );
		encrypt.setText("Encrypt");
		encrypt.setBackground(new Color(200,120,120));
		
		decrypt = new JButton();
		decrypt.setBounds(600-0-100,10,100,20);
		decrypt.setFont( new Font("Arial",1,16) );
		decrypt.setText("Decrypt");
		decrypt.setBackground(new Color(120,200,120));
		
		inputFile_label = new JLabel();
		inputFile_label.setBounds(50,50,500,20);
		inputFile_label.setText("Input file path relative to .jar folder (must be .txt)");
		inputFile = new JTextField();
		inputFile.setBounds(50,70,500,20);
		inputFile.setText("exampleFolder/example.txt");
		
		outputFile_label = new JLabel();
		outputFile_label.setBounds(50,100,500,20);
		outputFile_label.setText("Output file path relative to .jar folder (must be .bmp)");
		outputFile = new JTextField();
		outputFile.setBounds(50,120,500,20);
		outputFile.setText("exampleFolder/example.bmp");
		
		offsetSlider_label = new JLabel();
		offsetSlider_label.setBounds(50,150,500,20);
		offsetSlider_label.setText("Offset setting (higher is less detectable, but reduces available space)");
		offsetSlider = new JSlider();
		offsetSlider.setBounds(50,170,500,45);
		offsetSlider.setValue(8);
		offsetSlider.setMinimum(1);
		offsetSlider.setMaximum(8);
		offsetSlider.setMajorTickSpacing(1);
		offsetSlider.setMinorTickSpacing(1);
		offsetSlider.setPaintTicks(true);
		offsetSlider.setPaintLabels(true);
		
		expectedSlider_label = new JLabel();
		expectedSlider_label.setBounds(50,220,600,20);
		expectedSlider_label.setText("Expected length of message (exponential scale, keep at 5 if unsure and increase as needed)");
		expectedSlider_label.setVisible(false);
		expectedSlider_radio = new JRadioButton();
		expectedSlider_radio.setBounds(15,240,20,20);
		expectedSlider_radio.setSelected(true);
		expectedSlider_radio.setVisible(false);
		expectedSlider = new JSlider();
		expectedSlider.setBounds(50,240,500,45);
		expectedSlider.setValue(5);
		expectedSlider.setMinimum(0);
		expectedSlider.setMaximum(100);
		expectedSlider.setMajorTickSpacing(5);
		expectedSlider.setMinorTickSpacing(1);
		expectedSlider.setPaintTicks(true);
		expectedSlider.setPaintLabels(true);
		expectedSlider.setVisible(false);
		
		exactField_label = new JLabel();
		exactField_label.setBounds(50,290,600,20);
		exactField_label.setText("Exact length of message (in bytes)");
		exactField_label.setVisible(false);
		exactField_radio = new JRadioButton();
		exactField_radio.setBounds(15,310,20,20);
		exactField_radio.setVisible(false);
		exactField = new JTextField();
		exactField.setBounds(50,310,500,20);
		exactField.setVisible(false);
		
		radioGroup = new ButtonGroup();
		radioGroup.add(expectedSlider_radio);
		radioGroup.add(exactField_radio);
		
		start = new JButton();
		start.setBounds(175, 370, 250, 20);
		start.setText("Start!");
		
		progressbar = new JProgressBar();
		progressbar.setBounds(50,400,500,20);
		progressbar.setMinimum(0);
		progressbar.setMaximum(100);
		progressbar.setStringPainted(true);
		progressbar.setString("");
		
		fileSize_label = new JLabel();
		fileSize_label.setBounds(50,220,600,20);
		fileSize_label.setText("Available storage space given the output file and current offset:");
		fileSize = new JLabel();
		fileSize.setBounds(50,240,500,20);
		fileSize.setForeground(Color.WHITE);
		fileSize.setOpaque(true);
		fileSize.setText("YOUR MOM IS TRIPLE DECKER DOUBLE FLIPPING SLICK");
		
		encrypt.addActionListener(this);
		decrypt.addActionListener(this);
		offsetSlider.addChangeListener(this);
		expectedSlider.addChangeListener(this);
		start.addActionListener(this);
		
		mainPanel.add(statusLabel);
		mainPanel.add(encrypt);
		mainPanel.add(decrypt);
		mainPanel.add(inputFile_label);
		mainPanel.add(inputFile);
		mainPanel.add(outputFile_label);
		mainPanel.add(outputFile);
		mainPanel.add(offsetSlider_label);
		mainPanel.add(offsetSlider);
		mainPanel.add(expectedSlider_label);
		mainPanel.add(expectedSlider_radio);
		mainPanel.add(expectedSlider);
		mainPanel.add(exactField_label);
		mainPanel.add(exactField_radio);
		mainPanel.add(exactField);
		mainPanel.add(start);
		mainPanel.add(progressbar);
		mainPanel.add(fileSize_label);
		mainPanel.add(fileSize);
		
		add(mainPanel);
		this.getContentPane().setPreferredSize(new Dimension(600,420));
		pack();
		//start.setBounds(175, 230, 250, 20); // To trick pack() into resizing the window large enough to fit everything
		
		STATUS = 0; // 0 is encrypting, 1 is decrypting
		mainPanel.setBackground(new Color(200,120,120));
		
		addWindowListener(new WindowAdapter()
			{public void windowClosing(WindowEvent e)
			{dispose(); System.exit(0);}
		});
		
		Timer tick = new Timer();
		TimerTask updateProgress = new TimerTask() {
			public void run() {
				if(debounce == true) {
					progressbar.setValue((int) (Crypto.progress*100));
				}
			}
		};
		Timer refresh = new Timer();
		TimerTask refreshFunction = new TimerTask() {
			public void run() {
				if(fileSize.isVisible()) {
					File check = new File(outputFile.getText());
					if(check.exists() && !check.isDirectory()) {
						File checkAgain = new File(inputFile.getText());
						long availableSpace = ((check.length()-36)/8)/offsetSlider.getValue();
						if (checkAgain.exists() && !checkAgain.isDirectory()) {
							if(checkAgain.length() <= availableSpace){
								fileSize.setBackground(new Color(0,180,0));
							} else {
								fileSize.setBackground(new Color(180,0,0));
							}
						} else {
							fileSize.setBackground(new Color(180,180,0));
						}
						fileSize.setText(availableSpace + " bytes");
					} else {
						fileSize.setBackground(new Color(180,0,0));
						fileSize.setText("Output file does not exist.");
					}
				}
			}
		};
		tick.schedule(updateProgress,(long)5,(long)5);
		refresh.schedule(refreshFunction,(long)500,(long)500);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == encrypt) {
			mainPanel.setBackground(new Color(200,120,120));
			statusLabel.setText("Currently Encrypting");
			inputFile_label.setText("Input file path relative to .jar folder");
			outputFile_label.setText("Output file path relative to .jar folder (must be.bmp)");
			if(inputFile.getText().equals("exampleFolder/example.bmp")) {
				inputFile.setText("exampleFolder/example.txt");
			}
			if(outputFile.getText().equals("exampleFolder/example.txt")) {
				outputFile.setText("exampleFolder/example.bmp");
			}
			
			expectedSlider_label.setVisible(false);
			expectedSlider_radio.setVisible(false);
			expectedSlider.setVisible(false);
			exactField_label.setVisible(false);
			exactField_radio.setVisible(false);
			exactField.setVisible(false);
			fileSize_label.setVisible(true);
			fileSize.setVisible(true);
			
			STATUS = 0;
		} else if(e.getSource() == decrypt) {
			mainPanel.setBackground(new Color(120,200,120));
			statusLabel.setText("Currently Decrypting");
			inputFile_label.setText("Input file path relative to .jar folder (must be .bmp)");
			outputFile_label.setText("Output file path relative to .jar folder");
			if(inputFile.getText().equals("exampleFolder/example.txt")) {
				inputFile.setText("exampleFolder/example.bmp");
			}
			if(outputFile.getText().equals("exampleFolder/example.bmp")) {
				outputFile.setText("exampleFolder/example.txt");
			}
			
			expectedSlider_label.setVisible(true);
			expectedSlider_radio.setVisible(true);
			expectedSlider.setVisible(true);
			exactField_label.setVisible(true);
			exactField_radio.setVisible(true);
			exactField.setVisible(true);
			fileSize_label.setVisible(false);
			fileSize.setVisible(false);
			
			STATUS = 1;
		} else if(e.getSource() == start) {
			if(debounce == false) {
				debounce = true;
				if(STATUS == 0) {
					boolean continueEvaulating = true;
					File check = new File(outputFile.getText());
					if(check.exists() && !check.isDirectory()) {
						File checkAgain = new File(inputFile.getText());
						long availableSpace = ((check.length()-36)/8)/offsetSlider.getValue();
						if (checkAgain.exists() && !checkAgain.isDirectory()) {
							if(checkAgain.length() > availableSpace){
								JOptionPane.showMessageDialog(null,"Input file is too large to fit in output file!\nConsider turning down your offset settings or using a larger output file.","Crypto",JOptionPane.ERROR_MESSAGE);
								continueEvaulating = false;
							}
						}
					}
					
					if(continueEvaulating) {
						Path workingPath = Paths.get(inputFile.getText());
						byte[] workingFile;
						try {
							workingFile = Files.readAllBytes(workingPath);
							boolean status1 = Crypto.loadFile(outputFile.getText());
							if(status1 == true) {
								Runnable r = new Runnable() {
									public void run() {
										Crypto.encryptMessage(offsetSlider.getValue(),workingFile);
										debounce = false;
										JOptionPane.showMessageDialog(null,"Encryption completed successfully!","Crypto",JOptionPane.INFORMATION_MESSAGE);
										progressbar.setValue(100);
									}
								};
								new Thread(r).start();
							} else {
								JOptionPane.showMessageDialog(null,"Invalid output file path!\n"+outputFile.getText()+"\nDid you forget the file extension? Remember, the output file must be an existing .bmp file!","Crypto",JOptionPane.ERROR_MESSAGE);
								debounce = false;
							}
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						debounce = false;
					}
					
					//boolean status1 = Crypto.loadFile(inputFile.getText());
				} else if(STATUS == 1) {
					boolean status1 = Crypto.loadFile(inputFile.getText());
					if(status1 == true) {
						Runnable r = new Runnable() {
							public void run() {
								int numOfBytes;
								if(expectedSlider_radio.isSelected() == true) {
									numOfBytes = (int) Math.pow(expectedSlider.getValue(),3);
								} else {
									numOfBytes = Integer.parseInt(exactField.getText());
								}
								byte[] retrieved = Crypto.decryptMessage(offsetSlider.getValue(), numOfBytes, 4);
								if(Crypto.getStatus().equals(DecryptStatus.OUTOFBOUNDS)) {
									JOptionPane.showMessageDialog(null,"Oops! The decryptor attempted to read past the size of the file! You may be reading from the wrong input file,\n or set the length of message too high. Your offset value may also be too high.","Crypto",JOptionPane.ERROR_MESSAGE);
									debounce = false;
									progressbar.setValue(100);
								} else {
									debounce = false;
									progressbar.setValue(100);
									Path workingPath = Paths.get(outputFile.getText());
									try {
										Files.write(workingPath, retrieved);
										JOptionPane.showMessageDialog(null,"Decryption completed successfully! Check the output file.","Crypto",JOptionPane.INFORMATION_MESSAGE);
									} catch (IOException e1) {
										JOptionPane.showMessageDialog(null,"Oops! Something went wrong exporting the data! Check the output file path.","Crypto",JOptionPane.ERROR_MESSAGE);
									}
								}
							}
						};
						new Thread(r).start();
					} else {
						JOptionPane.showMessageDialog(null,"Invalid input file path!\n"+inputFile.getText()+"\nDid you forget the file extension? Remember, the input file must be an existing .bmp file!","Crypto",JOptionPane.ERROR_MESSAGE);
						debounce = false;
					}
				} else {
					debounce = false;
				}
			} else {
				JOptionPane.showMessageDialog(null,"Already running a process! Please wait until current process finishes to start another one.","Crypto",JOptionPane.ERROR_MESSAGE);
			}
			/*boolean status1 = Crypto.loadFile(inputFile.getText());
			if(status1 == true) {
				//inputFile_status.setText("Success!");
				if(STATUS == 0) {
					//encryptMessage(offsetSlider.getValue(),)
				} else if(STATUS == 1) {
					
				}
			} else {
				//inputFile_status.setText("Failed!");
			}*/
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		if(offsetSlider.getValue() == 0) {
			offsetSlider.setValue(1);
		}
	}
}
