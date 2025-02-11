
/**
 * This is the separate thread that services each
 * incoming echo client request.
 */

import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


public class Connection implements Runnable
{

	private Socket	client;
	private FileSystemView fsview;
	private ObjectInputStream input;

	public Connection (Socket client) {
		this.client = client;
	}

	public void run() {
		try{
			process(client);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void process(Socket client) throws IOException {
	String [] recordValues;
    	File [] allfiles = null;
    	File [] files;
   	File current, parent;
   	String cmd;
  	boolean validfile;
	ObjectInputStream fromClient = null;
	ObjectOutputStream toClient = null;
	boolean filefound = true;
        
        


	try {
		/**
		 * get the input and output streams associated with the socket.
		 */

		fromClient = new ObjectInputStream(client.getInputStream());
		toClient = new ObjectOutputStream (client.getOutputStream());


           	 // open file
         		try {
         			fsview = FileSystemView.getFileSystemView();
         		//	current = new File("C:\\Documents and Settings\\DLing\\My Documents\\cosc\\cs318\\Fall2009\\Examples and Notes\\SequentialFileAccess-Sept28\\NetworkFileAccess\\Server");
         		          current = new File(new File(".").getAbsolutePath());
         	          // or do this:    current = fsview.getDefaultDirectory();
         			files = fsview.getFiles(current, true);
         			parent = fsview.getParentDirectory(current);
         			allfiles = addparent (files, parent);
         			toClient.writeObject(allfiles);

         			filefound = false;
         			while (!filefound) {
         				current = (File) fromClient.readObject();
         				if (current.isDirectory())
         	   				toClient.writeObject("directory");
         				else
         	   				toClient.writeObject("file");

         				cmd = (String) fromClient.readObject();

         				while (cmd.equals("sendfiles")) {
         					files = fsview.getFiles(current, true);
         					parent = fsview.getParentDirectory(current);
         					allfiles = addparent (files, parent);
         					toClient.writeObject(allfiles);
                				current = (File) fromClient.readObject();

                				if (current.isDirectory())
         	   					toClient.writeObject("directory");
         					else
         	   					toClient.writeObject("file");

         					cmd = (String) fromClient.readObject();
         				}

         				validfile = isValidFile(current);
            			if (validfile) {
              				 input = new ObjectInputStream(new FileInputStream(current) );
              				 toClient.writeObject("ready");
               				 filefound = true;
               				 boolean done = false;
                                         
                                         
                                         BookRecord record;
                                         
                                         
                				 while (!done) {
                                                    cmd = (String) fromClient.readObject();
                                                    
                                                    if (cmd.equals("nextrecord")) {
                                                          System.out.println("command is next record");
                                                          record = null;
                                                          try{
                                                            record = (BookRecord)input.readObject();
                                                          }catch(Exception e){
                                                              System.err.println(e.getMessage());
                                                          }
                                                          System.out.println("record is null? " + (record == null));
                                                          if(record != null){
                                                              System.out.println("writing record to client");
                                                              toClient.writeObject(record);
                                                              record = null;
                                                          }else{
                                                              
                                                              System.out.println("writing null to client");
                                                              toClient.writeObject(null);
                                                              done = true;
                                                          }
//                                                        recordValues = readRecord();
//                                                        
//                                                        if (recordValues != null)
//                                                            toClient.writeObject(recordValues);
//                                                        
//                                                        else {
//                                                            done = true;
//                                                            toClient.writeObject(null);
//                                                        }
                                                    }
                				}
              			      	input.close();
              			}
            			else {
                  				toClient.writeObject("invaliddatafile");
                  				filefound = false;
                  			}
          		} //file not found? keep looking for it!

            		fromClient.close();
            		toClient.close();

         		   }
         		     // process exceptions opening file
         		   catch ( IOException ioException ) {
         			if (!filefound)
            		JOptionPane.showMessageDialog( null, "Error Opening File",
               			"Error", JOptionPane.ERROR_MESSAGE );

         		   }
        		   catch (ClassNotFoundException e) {};

	      	toClient.close();
       	  }
	 catch (IOException ioe) {
		System.err.println(ioe);
	  }
           finally {
                    // close streams and socket
                    if (fromClient != null)
                        fromClient.close();
                    if (toClient != null)
                        toClient.close();
                    if (client != null)
                        client.close();
          	}
	}


   public File [] addparent(File[] files, File parent){
   	 File [] allfiles = new File [files.length + 1];
   	 allfiles[0] = parent;
     	 for (int i = 0 ; i < files.length; i++)
  			allfiles [i+1] = files[i];
  	 return allfiles;
   }


   public boolean isValidFile(File current) {
   	 BookRecord record;
     	 try{
   	         ObjectInputStream input = new ObjectInputStream(new FileInputStream(current) );
   	         record = (BookRecord) input.readObject();
   	         input.close();
         	         return true;
     	    } catch (IOException e) {
     			return false;
              }
               catch (ClassNotFoundException e) {
    			return false;
   		}
   	     catch (RuntimeException e) {
    		    	return false;
   		}
   }


   // read record from file
   public String [] readRecord()
   {
      BookRecord record;

      // input the values from the file
      try {
         record = ( BookRecord ) input.readObject();

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

      // display message when end-of-file reached
      catch ( EOFException endOfFileException ) {

      }

      // display error message if class is not found
      catch ( ClassNotFoundException classNotFoundException ) {
         JOptionPane.showMessageDialog( null, "Unable to create object",
            "Class Not Found", JOptionPane.ERROR_MESSAGE );
      }

      // display error message if cannot read due to problem with file
      catch ( IOException ioException ) {
         JOptionPane.showMessageDialog( null,
            "Error during read from file",
            "Read Error", JOptionPane.ERROR_MESSAGE );
    }
    return null;
  } //readRecord
}