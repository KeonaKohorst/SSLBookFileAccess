// ReadSequentialFile.java
// This program reads a file of objects sequentially
// and displays each record.
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class ReadSequentialFile extends JFrame {
   private ObjectInputStream input;
   private BookUI userInterface;
   private JButton nextButton, openButton;

   // Constructor -- initialize the Frame
   public ReadSequentialFile()
   {
      super( "Reading a Sequential File of Objects" );

      // create instance of reusable user interface
      userInterface = new BookUI( 7 );  // four textfields
      getContentPane().add( userInterface, BorderLayout.CENTER );

      // get reference to generic task button doTask1 from BankUI
      openButton = userInterface.getDoTask1Button();
      openButton.setText( "Open File" );

      // register listener to call openFile when button pressed
      openButton.addActionListener(

         // anonymous inner class to handle openButton event
         new ActionListener() {

            // close file and terminate application
            public void actionPerformed( ActionEvent event )
            {
               openFile();
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
               if ( input != null )
                  closeFile();

               System.exit( 0 );
            }

         } // end anonymous inner class

      ); // end call to addWindowListener

      // get reference to generic task button doTask2 from BankUI
      nextButton = userInterface.getDoTask2Button();
      nextButton.setText( "Next Record" );
      nextButton.setEnabled( false );

      // register listener to call readRecord when button pressed
      nextButton.addActionListener(

         // anonymous inner class to handle nextRecord event
         new ActionListener() {

            // call readRecord when user clicks nextRecord
            public void actionPerformed( ActionEvent event )
            {
               readRecord();
            }

         } // end anonymous inner class

      ); // end call to addActionListener

      pack();
      setBounds(150,350, 300, 200 );
      setVisible( true );

   } // end ReadSequentialFile constructor

   // enable user to select file to open
   private void openFile()
   {
      // display file dialog so user can select file to open
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );

      int result = fileChooser.showOpenDialog( this );

      // if user clicked Cancel button on dialog, return
      if ( result == JFileChooser.CANCEL_OPTION )
         return;

      // obtain selected file
      File fileName = fileChooser.getSelectedFile();

      // display error if file name invalid
      if ( fileName == null || fileName.getName().equals( "" ) )
         JOptionPane.showMessageDialog( this, "Invalid File Name",
            "Invalid File Name", JOptionPane.ERROR_MESSAGE );

      else {

         // open file
         try {
            input = new ObjectInputStream(
               new FileInputStream( fileName ) );

            openButton.setEnabled( false );
            nextButton.setEnabled( true );
         }

         // process exceptions opening file
         catch ( IOException ioException ) {
            JOptionPane.showMessageDialog( this, "Error Opening File",
               "Error", JOptionPane.ERROR_MESSAGE );
         }

      } // end else

   } // end method openFile

   // read record from file
   public void readRecord()
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

         // display record contents
         userInterface.setFieldValues( values );
      }

      // display message when end-of-file reached
      catch ( EOFException endOfFileException ) {
         nextButton.setEnabled( false );

         JOptionPane.showMessageDialog( this, "No more records in file",
            "End of File", JOptionPane.ERROR_MESSAGE );
      }

      // display error message if class is not found
      catch ( ClassNotFoundException classNotFoundException ) {
         JOptionPane.showMessageDialog( this, "Unable to create object",
            "Class Not Found", JOptionPane.ERROR_MESSAGE );
      }

      // display error message if cannot read due to problem with file
      catch ( IOException ioException ) {
         JOptionPane.showMessageDialog( this,
            "Error during read from file",
            "Read Error", JOptionPane.ERROR_MESSAGE );
      }

   } // end method readRecord

   // close file and terminate application
   private void closeFile()
   {
      // close file and exit
      try {
         input.close();
         System.exit( 0 );
      }

      // process exception while closing file
      catch ( IOException ioException ) {
         JOptionPane.showMessageDialog( this, "Error closing file",
            "Error", JOptionPane.ERROR_MESSAGE );

         System.exit( 1 );
      }

   } // end method closeFile

   public static void main( String args[] )
   {
      new ReadSequentialFile();
   }

} // end class ReadSequentialFile

