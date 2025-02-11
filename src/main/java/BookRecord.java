// AccountRecord.java
// A class that represents one record of information.

import java.io.Serializable;

public class BookRecord implements Serializable {
   private String bookTitle;
   private String author;
   private String isbn;
   private int edition;
   private int copyrightYear;
   private double price;
   private int quantity;

    

   // no-argument constructor calls other constructor with default values
   public BookRecord()
   {
      this( "", "", "", 0, 0, 0.0, 0);
   }

    public BookRecord(String bookTitle, String author, String isbn, int edition, int copyrightYear, double price, int quantity) {
        this.bookTitle = bookTitle;
        this.author = author;
        this.isbn = isbn;
        this.edition = edition;
        this.copyrightYear = copyrightYear;
        this.price = price;
        this.quantity = quantity;
    }

   

   public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public void setCopyrightYear(int copyrightYear) {
        this.copyrightYear = copyrightYear;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getEdition() {
        return edition;
    }

    public int getCopyrightYear() {
        return copyrightYear;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

} // end class AccountRecord