// FileAccessClient.java
// Accessing objects sequentially from a file across a network connection.
import java.io.*;
import java.net.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.*;

public class FileAccessClient extends JFrame {
   private ObjectOutputStream toServer;
   private ObjectInputStream fromServer;
   private String [] values;
   private File [] files;
   private InetAddress host;
   private String response;
   private JTextField ip;
   private JLabel iplabel, label;
   private JPanel ippane;
   private JList filelist;
   private SSLSocket sock;
   private TrustManager[] trustManagers;
   private BookUI userInterface;
   private JButton nextButton, connectButton, openButton;

   // set up GUI
   public FileAccessClient()
   {
      super( "Reading a Sequential File across a network connection" );

      // create instance of reusable user interface
      userInterface = new BookUI( 7 );  // four textfields
      ip = new JTextField("localhost",32);
      iplabel = new JLabel("Host IP ");
      filelist = new JList();
      ippane = new JPanel();
      ippane.setLayout(new GridLayout(1,1));
      ippane.add(iplabel);
      ippane.add(ip);
      getContentPane().add(ippane, BorderLayout.NORTH);
      getContentPane().add( userInterface, BorderLayout.CENTER );
      getContentPane().add(new JScrollPane(filelist), BorderLayout.SOUTH);


      label = userInterface.getLabel();
      label.setText("");

      // configure button doTask1 for use in this program
      connectButton = userInterface.getDoTask1Button();
      connectButton.setText( "Connect" );




      // register listener to call openFile when button pressed
      connectButton.addActionListener(

         // anonymous inner class to handle openButton event
         new ActionListener() {

            // when button pressed
            public void actionPerformed( ActionEvent event )
            {
             try {
                    trustManagers = new TrustManager[]{
                        new X509TrustManager(){
                            @Override
                            public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                              throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                              throws CertificateException {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                    };
                    
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, trustManagers, new SecureRandom());
                    
                    SSLSocketFactory factory = sslContext.getSocketFactory();
                    
                    sock = (SSLSocket)factory.createSocket(InetAddress.getByName(ip.getText()), 6868);
                    
	    	    //sock = new Socket(InetAddress.getByName(ip.getText()), 6868);
		        toServer = new ObjectOutputStream (sock.getOutputStream());
	    	    fromServer = new ObjectInputStream (sock.getInputStream());

				files = (File []) fromServer.readObject();
				filelist.setListData(files);
				connectButton.setEnabled(false);
				nextButton.setEnabled(false);
				label.setText("<html><Font Color=blue>" +
				"Please double-click one of the data files below:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp" +
                                    "&nbsp;&nbsp;&nbsp</Font></html>");


              }
              catch (IOException ioe) {
                  System.err.println(ioe.getMessage());
              }
              catch (ClassNotFoundException cnfe) {
                   System.err.println(cnfe.getMessage());
              }
              catch (Exception e) {
                   System.err.println(e.getMessage());
              };
            }

         } // end anonymous inner class

      ); // end call to addActionListener



      MouseListener mouseListener = new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
         if (e.getClickCount() == 2) {
             int i = filelist.locationToIndex(e.getPoint());
             try {
		 if (i < 0) return;

		 toServer.writeObject(files[i]);
		 response = (String) fromServer.readObject();

		 if (response.equals("directory")) {
			toServer.writeObject("sendfiles");
			files = (File []) fromServer.readObject();
			filelist.setListData(files);
		  }
		  else {
                       //THIS IS WHERE YOU READ THE RECORD OBJECT AND EXTRACT THE INFO
                       toServer.writeObject("openfile");
                       
                       response = (String) fromServer.readObject();
                     
                       if (response.equals("ready")) {
                        // configure button doTask2 for use in this program
                        nextButton = userInterface.getDoTask2Button();
                        nextButton.setText( "Next Record" );
                        nextButton.setEnabled( true);
                        connectButton.setEnabled (false);
                        label.setText("");
                       }
                       else if (response.equals("invaliddatafile")){
                            JOptionPane.showMessageDialog( null, "Invalid data file, try again!",
                                                "Invalid data file", JOptionPane.ERROR_MESSAGE );
                       }
              	 }
              }
              catch (IOException ioe) {}
              catch (ClassNotFoundException cnfe) {};
            }

          }

      };

      filelist.addMouseListener(mouseListener);

      nextButton = userInterface.getDoTask2Button();
      nextButton.setText( "Next Record" );
      nextButton.setEnabled(false);

      // register listener to call addRecord when button pressed
      nextButton.addActionListener(

         // anonymous inner class to handle enterButton event
         new ActionListener() {

            // call addRecord when button pressed
            public void actionPerformed( ActionEvent event )
            {
              try {
                       System.out.println("writing nextrecord to server");
	               toServer.writeObject("nextrecord");
                       //values = (String [])fromServer.readObject();
                       BookRecord record = (BookRecord)fromServer.readObject();
                       if(record != null){
                        values = readRecord(record);
                       }
                       
                       System.out.println("values is null?: " + (values == null));
        	       if (values != null){
        	  	     // display record contents
   		      	     userInterface.setFieldValues( values );
                             values = null;
                       }else {
                               connectButton.setEnabled(true);
                               nextButton.setEnabled( false );
                               userInterface.clearFields();

                               Vector  emptyfiles = new Vector();

                               filelist.setListData(emptyfiles);



                               JOptionPane.showMessageDialog( null, "No more records in file",
                                            "End of File", JOptionPane.ERROR_MESSAGE );
                               toServer.close();
                               fromServer.close();
                               sock.close();
                             }
                }
                catch (IOException ioe) {
                    System.err.println(ioe.getMessage());
                }
                catch (ClassNotFoundException cnfe) {
                    System.err.println(cnfe.getMessage());
                };
            }

         } // end anonymous inner class

      ); // end call to addActionListener

            // register window listener for window closing event
      addWindowListener(

         // anonymous inner class to handle windowClosing event
         new WindowAdapter() {

            // close file and terminate application
            public void windowClosing( WindowEvent event )
            {
                  dispose();
                  System.exit(0);
            }

         } // end anonymous inner class

      ); // end call to addWindowListener



      setBounds(350, 350, 500, 300 );
      setVisible( true );


   } // end FileAccessClient constructor
   
   // read record from file
   public String [] readRecord(BookRecord record)
   {
         // create array of Strings to display in GUI
         String values[] = { 
            record.getBookTitle(),
            record.getAuthor(), 
            record.getIsbn(),
            String.valueOf(record.getEdition()), 
            String.valueOf(record.getCopyrightYear()),
            String.valueOf( record.getPrice() ),
            String.valueOf(record.getQuantity())
         };

   	return values;
    }



   public static void main( String args[] )
   {
      FileAccessClient app = new FileAccessClient();
      app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

} // end class FileAccessClient